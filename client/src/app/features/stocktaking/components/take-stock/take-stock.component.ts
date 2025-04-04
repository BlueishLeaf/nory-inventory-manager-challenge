import {Component, inject, OnInit} from '@angular/core';
import {InventoryApiService} from '../../../../core/services/inventory-api/inventory-api.service';
import {LocationIngredient} from '../../../../core/models/ingredients/location-ingredient';
import {StocktakeApiService} from '../../services/stocktake-api/stocktake-api.service';
import {NgClass, NgIf} from '@angular/common';
import {NgbAlert, NgbTypeahead} from '@ng-bootstrap/ng-bootstrap';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {debounceTime, distinctUntilChanged, filter, map, Observable, OperatorFunction} from 'rxjs';
import {CreateStocktake} from '../../models/create-stocktake';
import {StockCorrection} from '../../models/stock-correction';

interface FormStockCorrection extends StockCorrection {
  locationIngredient: LocationIngredient;
}

@Component({
  selector: 'app-take-stock',
  imports: [
    NgIf,
    NgbAlert,
    NgbTypeahead,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './take-stock.component.html',
  styleUrl: './take-stock.component.css'
})
export class TakeStockComponent implements OnInit{
  private readonly inventoryApi = inject(InventoryApiService);
  private readonly stocktakeApi = inject(StocktakeApiService);

  locationIngredients: LocationIngredient[] = [];

  // Form for adding stock corrections to the main form
  addStockCorrectionForm = new FormGroup({
    selectedIngredient: new FormControl<LocationIngredient | null>(null, Validators.required),
    quantityCounted: new FormControl<number | null>(null, Validators.required)
  });

  // Form for submitting the complete accepted delivery
  createStocktakeForm = new FormGroup({
    stockCorrections: new FormControl<FormStockCorrection[]>([], [Validators.required, Validators.min(1)])
  });

  formSubmitting = false;
  errorMessage = '';
  successMessage = '';

  formatter = (locationIngredient: LocationIngredient) => locationIngredient.ingredient.name;

  search: OperatorFunction<string, readonly LocationIngredient[]> = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      filter((term) => term.length >= 2),
      map((term) => this.locationIngredients.filter((locationIngredient) =>
        new RegExp(term, 'mi').test(locationIngredient.ingredient.name)).slice(0, 10))
    );

  ngOnInit(): void {
    this.addStockCorrectionForm.controls.selectedIngredient.valueChanges.subscribe(value => {
      if (value) {
        this.retrieveLatestIngredientData(value);
      }
    });
    this.populateIngredients();
  }

  private populateIngredients() {
    this.inventoryApi.fetchIngredients().subscribe(locationIngredients => {
      this.locationIngredients = locationIngredients;
    });
  }

  // Refresh the value of the currently selected ingredient
  retrieveLatestIngredientData(ingredient: LocationIngredient) {
    this.inventoryApi.fetchIngredientById(ingredient.ingredientId).subscribe(freshIngredientData => {
      this.addStockCorrectionForm.controls.selectedIngredient.setValue(freshIngredientData, {emitEvent: false});
    });
  }

  addCorrectionToStocktake() {
    if (this.addStockCorrectionForm.invalid) {
      this.addStockCorrectionForm.markAllAsTouched();
      return;
    }

    const selectedIngredient = this.addStockCorrectionForm.controls.selectedIngredient.value;
    const stockCorrection: FormStockCorrection = {
      locationIngredient: selectedIngredient!,
      ingredientId: selectedIngredient!.ingredientId,
      quantityCounted: this.addStockCorrectionForm.controls.quantityCounted.value!
    }

    // Update the contents of the stockCorrections control
    const stockCorrections = [...this.createStocktakeForm.controls.stockCorrections.value || []];
    const alreadyExists = !!stockCorrections.find(item => item.ingredientId === stockCorrection.ingredientId);

    if (alreadyExists) {
      this.errorMessage = 'That ingredient is already present in this stocktake';
      return;
    }

    stockCorrections.push(stockCorrection);
    this.createStocktakeForm.controls.stockCorrections.setValue(stockCorrections);

    // Reset the form to allow the user to enter a new ingredient
    this.addStockCorrectionForm.reset();
  }

  createStocktake() {
    if (this.createStocktakeForm.invalid) {
      this.createStocktakeForm.markAllAsTouched();
      return;
    }

    this.formSubmitting = true;

    const createStocktake: CreateStocktake = {
      stockCorrections: this.createStocktakeForm.controls.stockCorrections.value!
    }

    this.stocktakeApi.createStocktake(createStocktake).subscribe({
      next: (_delivery) => {
        this.formSubmitting = false;
        this.successMessage = 'Stock corrections successfully submitted.';
        this.createStocktakeForm.reset();
      },
      error: (error) => {
        this.formSubmitting = false;
        this.errorMessage = error.error?.message || 'Failed to submit stock corrections. Please try again.';
      }
    });
  }

  removeStockCorrection(stockCorrection: FormStockCorrection) {
    let stockCorrections = [...this.createStocktakeForm.controls.stockCorrections.value || []]
      .filter(item => item.ingredientId !== stockCorrection.ingredientId);

    this.createStocktakeForm.controls.stockCorrections.setValue(stockCorrections);
  }
}

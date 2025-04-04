import {Component, inject, OnInit} from '@angular/core';
import {LocationIngredient} from '../../../../core/models/ingredients/location-ingredient';
import {InventoryApiService} from '../../../../core/services/inventory-api/inventory-api.service';
import {DeliveryApiService} from '../../services/delivery-api/delivery-api.service';
import {CreateDelivery} from '../../models/create-delivery';
import {DeliveredItem} from '../../models/delivered-item';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgbAlert, NgbTypeahead} from '@ng-bootstrap/ng-bootstrap';
import {debounceTime, distinctUntilChanged, filter, map, Observable, OperatorFunction} from 'rxjs';
import {NgClass, NgIf} from '@angular/common';

interface FormDeliveredItem extends DeliveredItem {
  locationIngredient: LocationIngredient;
}

@Component({
  selector: 'app-accept-delivery',
  imports: [
    ReactiveFormsModule,
    NgbTypeahead,
    NgClass,
    NgIf,
    NgbAlert
  ],
  templateUrl: './accept-delivery.component.html',
  styleUrl: './accept-delivery.component.css'
})
export class AcceptDeliveryComponent implements OnInit{
  private readonly inventoryApi = inject(InventoryApiService);
  private readonly deliveryApi = inject(DeliveryApiService);

  locationIngredients: LocationIngredient[] = [];

  // Form for adding individual ingredients to the main form
  addIngredientForm = new FormGroup({
    selectedIngredient: new FormControl<LocationIngredient | null>(null, Validators.required),
    quantityDelivered: new FormControl<number | null>(null, Validators.required)
  });

  // Form for submitting the complete accepted delivery
  acceptDeliveryForm = new FormGroup({
    deliveredItems: new FormControl<FormDeliveredItem[]>([], [Validators.required, Validators.min(1)])
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
    this.populateIngredients();
  }

  private populateIngredients() {
    this.inventoryApi.fetchIngredients().subscribe(locationIngredients => {
      this.locationIngredients = locationIngredients;
    });
  }

  addIngredientToDelivery() {
    if (this.addIngredientForm.invalid) {
      this.addIngredientForm.markAllAsTouched();
      return;
    }

    const selectedIngredient = this.addIngredientForm.controls.selectedIngredient.value;
    const deliveredItem: FormDeliveredItem = {
      locationIngredient: selectedIngredient!,
      ingredientId: selectedIngredient!.ingredientId,
      quantityDelivered: this.addIngredientForm.controls.quantityDelivered.value!
    }

    // Update the contents of the deliveredItems control
    const deliveredItems = [...this.acceptDeliveryForm.controls.deliveredItems.value || []];
    const alreadyExists = !!deliveredItems.find(item => item.ingredientId === deliveredItem.ingredientId);

    if (alreadyExists) {
      this.errorMessage = 'That ingredient is already present in this delivery';
      return;
    }

    deliveredItems.push(deliveredItem);
    this.acceptDeliveryForm.controls.deliveredItems.setValue(deliveredItems);

    // Reset the form to allow the user to enter a new ingredient
    this.addIngredientForm.reset();
  }

  acceptDelivery() {
    if (this.acceptDeliveryForm.invalid) {
      this.acceptDeliveryForm.markAllAsTouched();
      return;
    }

    this.formSubmitting = true;

    const createDelivery: CreateDelivery = {
      deliveredItems: this.acceptDeliveryForm.controls.deliveredItems.value!
    }

    // Submit the delivery to the API
    this.deliveryApi.createDelivery(createDelivery).subscribe({
      next: (_delivery) => {
        this.formSubmitting = false;
        this.successMessage = 'Delivery successfully accepted.';
        this.acceptDeliveryForm.reset();
      },
      error: (error) => {
        this.formSubmitting = false;
        this.errorMessage = error.error?.message || 'Failed to submit delivery. Please try again.';
      }
    });
  }

  removeDeliveredItem(deliveredItem: FormDeliveredItem) {
    let deliveredItems = [...this.acceptDeliveryForm.controls.deliveredItems.value || []]
      .filter(item => item.ingredientId !== deliveredItem.ingredientId);

    this.acceptDeliveryForm.controls.deliveredItems.setValue(deliveredItems);
  }
}

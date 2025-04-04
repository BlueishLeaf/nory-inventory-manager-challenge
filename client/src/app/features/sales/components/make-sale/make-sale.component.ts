import {Component, inject, OnInit} from '@angular/core';
import {MenuApiService} from '../../services/menu-api/menu-api.service';
import {SaleApiService} from '../../services/sale-api/sale-api.service';
import {MenuItem} from '../../models/menu-item';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {SoldItem} from '../../models/sold-item';
import {debounceTime, distinctUntilChanged, filter, forkJoin, map, Observable, OperatorFunction} from 'rxjs';
import {Modifier} from '../../models/modifier';
import {CreateSale} from '../../models/create-sale';
import {NgClass, NgIf} from '@angular/common';
import {NgbAlert, NgbTypeahead} from '@ng-bootstrap/ng-bootstrap';

interface FormSoldItem extends SoldItem {
  menuItem: MenuItem;
  // modifiers: Modifier[];
}

@Component({
  selector: 'app-make-sale',
  imports: [
    NgIf,
    NgbAlert,
    NgbTypeahead,
    ReactiveFormsModule,
    NgClass
  ],
  templateUrl: './make-sale.component.html',
  styleUrl: './make-sale.component.css'
})
export class MakeSaleComponent implements OnInit{
  private readonly menuApi = inject(MenuApiService);
  private readonly saleApi = inject(SaleApiService);

  menuItems: MenuItem[] = [];
  modifiers: Modifier[] = [];

  // Form for adding individual ingredients to the main form
  addMenuItemForm = new FormGroup({
    selectedMenuItem: new FormControl<MenuItem | null>(null, Validators.required),
    quantityOrdered: new FormControl<number | null>(null, Validators.required)
  });

  // Form for submitting the complete accepted delivery
  makeSaleForm = new FormGroup({
    soldItems: new FormControl<FormSoldItem[]>([], [Validators.required, Validators.min(1)])
  });

  formSubmitting = false;
  errorMessage = '';
  successMessage = '';

  formatter = (menuItem: MenuItem) => menuItem.recipeName;

  search: OperatorFunction<string, readonly MenuItem[]> = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      filter((term) => term.length >= 2),
      map((term) => this.menuItems.filter((menuItem) =>
        new RegExp(term, 'mi').test(menuItem.recipeName)).slice(0, 10))
    );

  ngOnInit(): void {
    this.populateFormData();
  }

  // buildModifierCheckboxes(modifierType: ModifierType): FormArray {
  //   const modifierArray = this.modifiers
  //     .filter(modifier => modifier.modifierType === modifierType)
  //     .map(_modifier => new FormControl(false));
  //   return new FormArray(modifierArray);
  // }

  private populateFormData() {
    forkJoin([this.menuApi.fetchMenuItems(), this.menuApi.fetchModifiers()]).subscribe(([menuItems, modifiers]) => {
      this.menuItems = menuItems;
      this.modifiers = modifiers;
    });
  }

  addSoldItemToSale() {
    if (this.addMenuItemForm.invalid) {
      this.addMenuItemForm.markAllAsTouched();
      return;
    }

    const selectedMenuItem = this.addMenuItemForm.controls.selectedMenuItem.value;
    // const modifiers = this.addMenuItemForm.controls.modifiers.value || [];
    const soldItem: FormSoldItem = {
      menuItem: selectedMenuItem!,
      recipeId: selectedMenuItem!.recipeId,
      // modifiers: modifiers,
      modifierIds: [],
      quantity: this.addMenuItemForm.controls.quantityOrdered.value!
    }

    // Update the contents of the soldItems control
    const soldItems = [...this.makeSaleForm.controls.soldItems.value || []];

    soldItems.push(soldItem);
    this.makeSaleForm.controls.soldItems.setValue(soldItems);

    // Reset the form to allow the user to enter a new menu item
    this.addMenuItemForm.reset();
  }

  makeSale() {
    if (this.makeSaleForm.invalid) {
      this.makeSaleForm.markAllAsTouched();
      return;
    }

    this.formSubmitting = true;

    const createSale: CreateSale = {
      soldItems: this.makeSaleForm.controls.soldItems.value!
    }

    this.saleApi.createSale(createSale).subscribe({
      next: (_sale) => {
        this.formSubmitting = false;
        this.successMessage = 'Sale successfully processed.';
        this.makeSaleForm.reset();
      },
      error: (error) => {
        this.formSubmitting = false;
        this.errorMessage = error.error?.message || 'Failed to process sale. Please try again.';
      }
    });
  }

  removeSoldItem(soldItem: FormSoldItem) {
    let soldItems = [...this.makeSaleForm.controls.soldItems.value || []]
      .filter(item => item.recipeId !== soldItem.recipeId);

    this.makeSaleForm.controls.soldItems.setValue(soldItems);
  }
}

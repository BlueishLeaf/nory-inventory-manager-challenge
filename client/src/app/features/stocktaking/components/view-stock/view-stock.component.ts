import {Component, inject, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InventoryApiService} from '../../../../core/services/inventory-api/inventory-api.service';
import {LocationIngredient} from '../../../../core/models/ingredients/location-ingredient';
import {NgbPagination} from '@ng-bootstrap/ng-bootstrap';
import {SlicePipe} from '@angular/common';

@Component({
  selector: 'app-view-stock',
  imports: [
    ReactiveFormsModule,
    NgbPagination,
    SlicePipe,
    FormsModule
  ],
  templateUrl: './view-stock.component.html',
  styleUrl: './view-stock.component.css'
})
export class ViewStockComponent implements OnInit {
  private readonly inventoryApi = inject(InventoryApiService);

  locationIngredients: LocationIngredient[] = [];
  searchTerm: string | null = '';

  page = 1;
  pageSize = 20;
  maxDisplayPages = 10;

  get filteredLocationIngredients(): LocationIngredient[] {
    if (!this.searchTerm) {
      return this.locationIngredients;
    }

    const searchTermLower = this.searchTerm.toLowerCase();
    return this.locationIngredients
      .filter(locationIngredient => locationIngredient.ingredient.name.toLowerCase().includes(searchTermLower));
  }

  ngOnInit(): void {
    this.populateIngredients();
  }

  private populateIngredients() {
    this.inventoryApi.fetchIngredients().subscribe(locationIngredients => {
      this.locationIngredients = locationIngredients.sort((a, b) => a.ingredient.name.localeCompare(b.ingredient.name)); // Sort alphabetically by default
    });
  }
}

import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {Observable} from 'rxjs';
import {LocationIngredient} from '../../models/ingredients/location-ingredient';

@Injectable({
  providedIn: 'root'
})
export class InventoryApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl + '/inventory';

  fetchIngredients(): Observable<LocationIngredient[]> {
    return this.http.get<LocationIngredient[]>(`${this.baseUrl}/ingredients`);
  }

  fetchIngredientById(ingredientId: number): Observable<LocationIngredient> {
    return this.http.get<LocationIngredient>(`${this.baseUrl}/ingredients/${ingredientId}`);
  }
}

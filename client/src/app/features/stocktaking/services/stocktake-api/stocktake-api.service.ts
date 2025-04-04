import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../../environments/environment';
import {Observable} from 'rxjs';
import {CreateStocktake} from '../../models/create-stocktake';
import {Stocktake} from '../../models/stocktake';

@Injectable({
  providedIn: 'root'
})
export class StocktakeApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl + '/stocktakes';

  createStocktake(createStocktake: CreateStocktake): Observable<Stocktake> {
    return this.http.post<Stocktake>(`${this.baseUrl}`, createStocktake);
  }
}

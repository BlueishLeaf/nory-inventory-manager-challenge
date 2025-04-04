import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../../environments/environment';
import {Observable} from 'rxjs';
import {CreateSale} from '../../models/create-sale';
import {Sale} from '../../models/sale';

@Injectable({
  providedIn: 'root'
})
export class SaleApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl + '/sales';

  createSale(createSale: CreateSale): Observable<Sale> {
    return this.http.post<Sale>(`${this.baseUrl}`, createSale);
  }
}

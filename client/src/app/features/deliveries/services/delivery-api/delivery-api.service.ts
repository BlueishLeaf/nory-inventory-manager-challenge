import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../../environments/environment';
import {Observable} from 'rxjs';
import {Delivery} from '../../models/delivery';
import {CreateDelivery} from '../../models/create-delivery';

@Injectable({
  providedIn: 'root'
})
export class DeliveryApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl + '/deliveries';

  createDelivery(createDelivery: CreateDelivery): Observable<Delivery> {
    return this.http.post<Delivery>(`${this.baseUrl}`, createDelivery);
  }
}

import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../../environments/environment';
import {Observable} from 'rxjs';
import {MenuItem} from '../../models/menu-item';
import {Modifier} from '../../models/modifier';

@Injectable({
  providedIn: 'root'
})
export class MenuApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl + '/menu';

  fetchMenuItems(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(`${this.baseUrl}/items`);
  }

  fetchModifiers(): Observable<Modifier[]> {
    return this.http.get<Modifier[]>(`${this.baseUrl}/modifiers`);
  }
}

import {Injectable, inject} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import {Observable, catchError, tap, throwError, of} from 'rxjs';
import { environment } from '../../../../environments/environment';
import {StaffIdentity} from '../../../core/models/identity/staff-identity';
import {IdentityStoreService} from '../../../core/services/identity-store/identity-store.service';
import {StorageConstants} from '../constants/storage.constants';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private identityStore = inject(IdentityStoreService);

  login(staffMemberId: string): Observable<StaffIdentity> {
    return this.http.get<StaffIdentity>(`${environment.apiUrl}/identities/${staffMemberId}`)
      .pipe(
        tap(identity => {
          localStorage.setItem(StorageConstants.STAFF_MEMBER_ID_KEY, String(identity.staffMember.id));
          localStorage.setItem(StorageConstants.LOCATION_ID_KEY, String(identity.location.id));

          // Update signals
          this.identityStore.login(identity);
        }),
        catchError(error => {
          console.error('Login failed', error);
          return throwError(() => new Error('Invalid staff ID. Please try again.'));
        })
      );
  }

  logout(): void {
    // Clear token and state
    localStorage.removeItem(StorageConstants.STAFF_MEMBER_ID_KEY);
    localStorage.removeItem(StorageConstants.LOCATION_ID_KEY);

    this.identityStore.logout();

    // Navigate to login page
    this.router.navigate(['/login']);
  }

  checkAuth(): Observable<StaffIdentity | null> {
    // Check if user identity is already present, if it is then refresh the store
    const existingStaffId = localStorage.getItem(StorageConstants.STAFF_MEMBER_ID_KEY);

    if (!existingStaffId) {
      return of(null);
    }

    if (this.identityStore.isLoggedIn()) {
      return of(this.identityStore.identity());
    }

    return this.login(existingStaffId);
  }
}

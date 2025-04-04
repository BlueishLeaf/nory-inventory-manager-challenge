import { Injectable, computed, signal } from '@angular/core';
import {StaffIdentity} from '../../models/identity/staff-identity';

@Injectable({
  providedIn: 'root'
})
export class IdentityStoreService {
  // Create a writable signal with the initial state
  private identityState = signal<StaffIdentity | null>(null);

  // Create computed signals for derived state
  readonly isLoggedIn = computed(() => !!this.identityState());

  // Public accessor for the identity state
  readonly identity = this.identityState.asReadonly();

  // Methods to update state
  login(staffIdentity: StaffIdentity) {
    this.identityState.set(staffIdentity);
  }

  logout() {
    this.identityState.set(null);
  }
}

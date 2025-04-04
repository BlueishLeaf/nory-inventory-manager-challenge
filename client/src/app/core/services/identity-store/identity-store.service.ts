import { Injectable, computed, signal } from '@angular/core';
import {StaffIdentity} from '../../models/identity/staff-identity';

@Injectable({
  providedIn: 'root'
})
export class IdentityStoreService {
  // Privately accessible signal to manage the state of the user's identity
  private identityState = signal<StaffIdentity | null>(null);

  // Helper to see if the user has an identity in the store
  readonly isLoggedIn = computed(() => !!this.identityState());

  // Public accessor for the identity state
  readonly identity = this.identityState.asReadonly();

  // Methods to update state when the user logs in
  login(staffIdentity: StaffIdentity) {
    this.identityState.set(staffIdentity);
  }

  logout() {
    this.identityState.set(null);
  }
}

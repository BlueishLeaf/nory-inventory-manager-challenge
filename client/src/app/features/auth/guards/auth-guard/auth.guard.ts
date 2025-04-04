import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import {IdentityStoreService} from '../../../../core/services/identity-store/identity-store.service';
import {AuthService} from '../../services/auth.service';
import {map} from 'rxjs';

export const authGuard: CanActivateFn = (_route, _state) => {
  const identityStore = inject(IdentityStoreService);
  const router = inject(Router);
  const authService = inject(AuthService);

  return authService.checkAuth().pipe(map(result => {
    // Continue to desired route if user was logged in
    if (identityStore.isLoggedIn() && result) {
      return true;
    }

    // Redirect to login page
    return router.createUrlTree(['/login']);
  }));
};

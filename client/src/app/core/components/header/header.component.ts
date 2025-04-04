import {Component, inject} from '@angular/core';
import {AuthService} from '../../../features/auth/services/auth.service';
import {IdentityStoreService} from '../../services/identity-store/identity-store.service';
import {NgIf} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [
    NgIf,
    RouterLink
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  private authService = inject(AuthService);
  identityStore = inject(IdentityStoreService);

  onLogout() {
    this.authService.logout();
  }
}

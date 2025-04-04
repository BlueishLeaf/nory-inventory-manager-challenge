import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

import { AuthService } from '../../services/auth.service';
import {NgbAlert} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgbAlert],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm = new FormGroup({
    staffMemberId: new FormControl<string | null>(null, Validators.required)
  });

  formSubmitting = false;
  errorMessage = '';

  login(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.formSubmitting = true;
    this.errorMessage = '';

    const staffMemberId = this.loginForm.controls.staffMemberId.value!;

    this.authService.login(staffMemberId).subscribe({
      next: () => {
        this.formSubmitting = false;
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.formSubmitting = false;
        this.errorMessage = error.error?.message || 'Failed to login. Please try again.';
      }
    });
  }
}

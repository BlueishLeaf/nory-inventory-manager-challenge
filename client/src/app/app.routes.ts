import { Routes } from '@angular/router';
import {LoginComponent} from './features/auth/components/login/login.component';
import {authGuard} from './features/auth/guards/auth-guard/auth.guard';
import {TakeStockComponent} from './features/stocktaking/components/take-stock/take-stock.component';
import {ViewStockComponent} from './features/stocktaking/components/view-stock/view-stock.component';
import {DashboardComponent} from './features/dashboard/components/dashboard/dashboard.component';
import {ViewReportComponent} from './features/reporting/components/view-report/view-report.component';

export const routes: Routes = [
  // Default route to take the staff member to the dashboard
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },

  // Unprotected auth-related routes
  {
    path: 'login',
    component: LoginComponent
  },

  // Protected routes requiring the staff member to login first
  {
    path: '',
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent
      },
      {
        path: 'stocktaking/take',
        component: TakeStockComponent
      },
      {
        path: 'stocktaking/view',
        component: ViewStockComponent
      },
      {
        path: 'reporting',
        component: ViewReportComponent
      },
    ]
  },

  // Wildcard to match any other route
  { path: '**', redirectTo: '/dashboard' }
];

import {Component, inject, OnInit} from '@angular/core';
import {IdentityStoreService} from '../../../../core/services/identity-store/identity-store.service';
import {StaffMember} from '../../../../core/models/identity/staff-member';
import {DashboardItemComponent} from '../dashboard-item/dashboard-item.component';
import {DashboardOption} from '../../models/dashboard-option';
import {RoleConstants} from '../../../auth/constants/role.constants';

@Component({
  selector: 'app-dashboard',
  imports: [
    DashboardItemComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private identityStore = inject(IdentityStoreService);
  staffMember: StaffMember | undefined;
  availableMenuOptions: DashboardOption[] = [];

  allMenuOptions: DashboardOption[] = [
    {
      route: '/stocktaking/take',
      displayText: 'Take Stock',
      allowedRoles: [RoleConstants.ROLE_ALL]
    },
    {
      route: '/stocktaking/view',
      displayText: 'View Inventory',
      allowedRoles: [RoleConstants.ROLE_ALL]
    },
    {
      route: '/reporting',
      displayText: 'View Reporting',
      allowedRoles: [RoleConstants.ROLE_MANAGER]
    }
  ];

  ngOnInit() {
    this.staffMember = this.identityStore.identity()?.staffMember;
    this.availableMenuOptions = this.allMenuOptions.filter(option => {
      return option.allowedRoles.includes(this.staffMember!.role) || option.allowedRoles.includes(RoleConstants.ROLE_ALL);
    });
  }
}

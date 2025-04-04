import {Component, Input} from '@angular/core';
import {RouterLink} from '@angular/router';
import {DashboardOption} from '../../models/dashboard-option';

@Component({
  selector: 'app-dashboard-item',
  imports: [
    RouterLink
  ],
  templateUrl: './dashboard-item.component.html',
  styleUrl: './dashboard-item.component.css'
})
export class DashboardItemComponent {
  @Input({ required: true }) optionData: DashboardOption | undefined;
}

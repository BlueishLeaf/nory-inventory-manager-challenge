import {Component, inject, OnInit} from '@angular/core';
import {ReportApiService} from '../../services/report-api/report-api.service';
import {InventoryAuditLog} from '../../../../core/models/audit-logs/inventory-audit-log';
import {forkJoin} from 'rxjs';
import {MonthlySummaryReport} from '../../models/monthly-summary-report';
import {ReportCriteria} from '../../models/report-criteria';
import {NgbPagination} from '@ng-bootstrap/ng-bootstrap';
import {CurrencyPipe, DatePipe, SlicePipe} from '@angular/common';

@Component({
  selector: 'app-view-report',
  imports: [
    NgbPagination,
    SlicePipe,
    CurrencyPipe,
    DatePipe
  ],
  templateUrl: './view-report.component.html',
  styleUrl: './view-report.component.css'
})
export class ViewReportComponent implements OnInit{
  private readonly reportApi = inject(ReportApiService);

  inventoryAuditLogs: InventoryAuditLog[] = [];
  monthlySummaryReport: MonthlySummaryReport | null = null;

  page = 1;
  pageSize = 20;
  maxDisplayPages = 10;

  ngOnInit(): void {
    this.populateReports();
  }

  private populateReports() {
    const now = new Date();
    const firstDayOfThisMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDayOfThisMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    const reportCriteria: ReportCriteria = {
      fromDate: firstDayOfThisMonth,
      toDate: lastDayOfThisMonth
    };

    forkJoin([this.reportApi.fetchAuditLogsForPeriod(reportCriteria), this.reportApi.fetchSummaryForPeriod(reportCriteria)]).subscribe(([auditLogs, summary]) => {
      this.inventoryAuditLogs = auditLogs.sort((a, b) => new Date(a.createdAt).getDate() - new Date(b.createdAt).getDate()).reverse();
      this.monthlySummaryReport = summary;
    });
  }
}

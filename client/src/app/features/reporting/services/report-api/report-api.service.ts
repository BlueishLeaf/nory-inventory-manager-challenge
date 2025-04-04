import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../../environments/environment';
import {Observable} from 'rxjs';
import {ReportCriteria} from '../../models/report-criteria';
import {InventoryAuditLog} from '../../../../core/models/audit-logs/inventory-audit-log';
import {MonthlySummaryReport} from '../../models/monthly-summary-report';

@Injectable({
  providedIn: 'root'
})
export class ReportApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl + '/reporting';

  fetchAuditLogsForPeriod(reportCriteria: ReportCriteria): Observable<InventoryAuditLog[]> {
    return this.http.post<InventoryAuditLog[]>(`${this.baseUrl}/audit-logs`, reportCriteria);
  }

  fetchSummaryForPeriod(reportCriteria: ReportCriteria): Observable<MonthlySummaryReport> {
    return this.http.post<MonthlySummaryReport>(`${this.baseUrl}/summary`, reportCriteria);
  }
}

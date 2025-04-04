import { TestBed } from '@angular/core/testing';

import { SaleApiService } from './sale-api.service';

describe('SaleApiService', () => {
  let service: SaleApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SaleApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

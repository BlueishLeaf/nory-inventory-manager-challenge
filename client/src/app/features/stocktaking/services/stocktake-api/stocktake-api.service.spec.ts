import { TestBed } from '@angular/core/testing';

import { StocktakeApiService } from './stocktake-api.service';

describe('StocktakeApiService', () => {
  let service: StocktakeApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StocktakeApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

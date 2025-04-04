import { TestBed } from '@angular/core/testing';

import { IdentityStoreService } from './identity-store.service';

describe('IdentityStoreService', () => {
  let service: IdentityStoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IdentityStoreService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

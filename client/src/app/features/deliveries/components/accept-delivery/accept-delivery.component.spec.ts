import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AcceptDeliveryComponent } from './accept-delivery.component';

describe('AcceptDeliveryComponent', () => {
  let component: AcceptDeliveryComponent;
  let fixture: ComponentFixture<AcceptDeliveryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AcceptDeliveryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AcceptDeliveryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

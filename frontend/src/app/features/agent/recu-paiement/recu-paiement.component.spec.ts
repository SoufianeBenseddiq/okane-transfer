import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecuPaiementComponent } from './recu-paiement.component';

describe('RecuPaiementComponent', () => {
  let component: RecuPaiementComponent;
  let fixture: ComponentFixture<RecuPaiementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecuPaiementComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RecuPaiementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

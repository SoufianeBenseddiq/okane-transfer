import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RapportsAgenceComponent } from './rapports-agence.component';

describe('RapportsAgenceComponent', () => {
  let component: RapportsAgenceComponent;
  let fixture: ComponentFixture<RapportsAgenceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RapportsAgenceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RapportsAgenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { Component, ElementRef, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-two-factor',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './two-factor.component.html',
})
export class TwoFactorComponent implements OnInit, OnDestroy {
  @ViewChildren('digitInput') digitInputs!: QueryList<ElementRef<HTMLInputElement>>;

  digits: string[] = ['', '', '', '', '', ''];
  remainingSeconds = 120;
  loading  = false;
  canResend = false;

  private timerSub?: Subscription;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.timerSub = interval(1000).subscribe(() => {
      if (this.remainingSeconds > 0) {
        this.remainingSeconds--;
      } else {
        this.canResend = true;
        this.timerSub?.unsubscribe();
      }
    });
  }

  ngOnDestroy(): void {
    this.timerSub?.unsubscribe();
  }

  get timerDisplay(): string {
    const m = Math.floor(this.remainingSeconds / 60).toString().padStart(2, '0');
    const s = (this.remainingSeconds % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  }

  get otpCode(): string {
    return this.digits.join('');
  }

  get isComplete(): boolean {
    return this.digits.every(d => d !== '');
  }

  isActiveBox(i: number): boolean {
    return !this.digits[i] && this.digits.slice(0, i).every(d => d !== '');
  }

  onDigitInput(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const val   = input.value.replace(/\D/g, '').slice(-1);
    this.digits  = [...this.digits];
    this.digits[index] = val;
    input.value  = val;
    if (val && index < 5) {
      setTimeout(() => this.digitInputs.get(index + 1)?.nativeElement.focus());
    }
  }

  onDigitKeydown(event: KeyboardEvent, index: number): void {
    if (event.key === 'Backspace' && !this.digits[index] && index > 0) {
      this.digits  = [...this.digits];
      this.digits[index - 1] = '';
      setTimeout(() => {
        const prev = this.digitInputs.get(index - 1)?.nativeElement;
        if (prev) { prev.value = ''; prev.focus(); }
      });
    }
  }

  onPaste(event: ClipboardEvent): void {
    event.preventDefault();
    const text = (event.clipboardData?.getData('text') ?? '').replace(/\D/g, '').slice(0, 6);
    const newDigits = [...this.digits];
    text.split('').forEach((ch, i) => {
      newDigits[i] = ch;
      const el = this.digitInputs.get(i)?.nativeElement;
      if (el) el.value = ch;
    });
    this.digits = newDigits;
    setTimeout(() => this.digitInputs.get(Math.min(text.length, 5))?.nativeElement.focus());
  }

  onSubmit(): void {
    if (!this.isComplete) return;
    this.loading = true;
    // TODO: call authService.verifyOtp({ code: this.otpCode }) when endpoint is available
    // On success → redirectByRole(); On error → show error, reset digits
    console.log('OTP submitted:', this.otpCode);
    this.loading = false;
  }

  resend(): void {
    if (!this.canResend) return;
    this.remainingSeconds = 120;
    this.canResend        = false;
    this.digits           = ['', '', '', '', '', ''];
    this.timerSub?.unsubscribe();
    this.timerSub = interval(1000).subscribe(() => {
      if (this.remainingSeconds > 0) {
        this.remainingSeconds--;
      } else {
        this.canResend = true;
        this.timerSub?.unsubscribe();
      }
    });
    // TODO: call authService.resendOtp() when endpoint is available
  }
}

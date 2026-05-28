import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';
import { RoleUtilisateur } from '../../../core/models/enums';

const ROLE_ROUTES: Record<RoleUtilisateur, string> = {
  [RoleUtilisateur.ROLE_ADMIN]:   '/admin',
  [RoleUtilisateur.ROLE_MANAGER]: '/manager',
  [RoleUtilisateur.ROLE_AGENT]:   '/agent',
  [RoleUtilisateur.ROLE_CLIENT]:  '/client',
};

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  form = this.fb.group({
    email:      ['', [Validators.required, Validators.email]],
    motDePasse: ['', Validators.required],
    rememberMe: [true],
  });

  showPwd         = false;
  loading         = false;
  loginError:     string | null = null;
  accountInactive = false;

  constructor(
    private fb:          FormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private router:      Router,
  ) {}

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading         = true;
    this.loginError      = null;
    this.accountInactive = false;

    const { email, motDePasse } = this.form.value;
    this.authService.login({ email: email!, motDePasse: motDePasse! }).subscribe({
      next: res => {
        this.loading = false;
        if (res.requiresOtp) {
          this.router.navigate(['/auth/2fa']);
        } else {
          this.redirectByRole();
        }
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        if (err.status === 403) {
          this.accountInactive = true;
        } else {
          this.loginError = 'Identifiants incorrects. Vérifiez votre email et mot de passe.';
        }
      },
    });
  }

  private redirectByRole(): void {
    this.userService.getMe().subscribe({
      next: user => {
        this.authService.setCurrentUser(user);
        this.router.navigate([ROLE_ROUTES[user.role] ?? '/auth/login']);
      },
      error: () => this.router.navigate(['/auth/login']),
    });
  }
}

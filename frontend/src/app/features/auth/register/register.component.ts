import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { RoleUtilisateur } from '../../../core/models/enums';

function passwordsMatch(group: AbstractControl) {
  const pwd    = group.get('motDePasse')?.value;
  const confirm = group.get('confirmPassword')?.value;
  return pwd === confirm ? null : { passwordsMismatch: true };
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  step     = 1;
  loading  = false;
  error: string | null = null;
  showPwd  = false;
  showConfirmPwd = false;

  form = this.fb.group({
    nom:             ['', Validators.required],
    prenom:          ['', Validators.required],
    email:           ['', [Validators.required, Validators.email]],
    telephone:       ['', Validators.required],
    pays:            ['MA', Validators.required],
    motDePasse:      ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required],
  }, { validators: passwordsMatch });

  constructor(
    private fb:          FormBuilder,
    private userService: UserService,
    private router:      Router,
  ) {}

  get step1Fields(): string[] {
    return ['nom', 'prenom', 'email', 'telephone', 'pays'];
  }

  get step1Valid(): boolean {
    return this.step1Fields.every(f => this.form.get(f)?.valid);
  }

  nextStep(): void {
    this.step1Fields.forEach(f => this.form.get(f)?.markAsTouched());
    if (this.step1Valid) this.step = 2;
  }

  prevStep(): void {
    this.step = 1;
  }

  onSubmit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.loading = true;
    this.error   = null;

    const { nom, prenom, email, telephone, pays, motDePasse } = this.form.value;

    // Note: POST /api/utilisateurs — backend must allow unauthenticated access for self-registration
    this.userService.create({
      nom:        nom!,
      prenom:     prenom!,
      email:      email!,
      motDePasse: motDePasse!,
      telephone:  telephone!,
      pays:       pays!,
      role:       RoleUtilisateur.ROLE_CLIENT,
    }).subscribe({
      next: () => this.router.navigate(['/auth/login']),
      error: () => {
        this.error   = 'Une erreur est survenue. Cet email est peut-être déjà utilisé.';
        this.loading = false;
      },
    });
  }
}

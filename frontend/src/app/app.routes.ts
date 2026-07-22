import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./pages/auth/login/login.component').then(c => c.LoginComponent) },
  { path: 'registro', loadComponent: () => import('./pages/auth/register/register.component').then(c => c.RegisterComponent) },
  { path: 'restablecer-password', loadComponent: () => import('./pages/auth/reset-password/reset-password.component').then(c => c.ResetPasswordComponent) },
  { path: 'terminos', loadComponent: () => import('./pages/terminos/terminos.component').then(c => c.TerminosComponent) },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'dashboard/ajustes', loadComponent: () => import('./pages/ajustes/ajustes.component').then(c => c.AjustesComponent), canActivate: [authGuard] },
  { path: 'tarjetas', loadComponent: () => import('./pages/tarjetas/tarjetas.component').then(c => c.TarjetasComponent), canActivate: [authGuard] },
  { path: '**', redirectTo: 'dashboard' }
];

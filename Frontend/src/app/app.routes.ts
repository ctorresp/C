import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/Hub/home.component').then((m) => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./components/Auth/login.component').then((m) => m.LoginComponent)
  },
  {
    path: 'recuperar',
    loadComponent: () => import('./components/Auth/recuperarContrasena.component').then((m) => m.RecuperarComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./components/Auth/register.component').then((m) => m.RegisterComponent)
  },
  {
    path: 'report',
    loadComponent: () => import('./components/Reporte/report.component').then((m) => m.ReportComponent)
  },
  {
    path: 'mascotas',
    loadComponent: () => import('./components/Mascotas/registroMascotas.component').then((m) => m.RegistroMascotasComponent)
  },
  {
    path: 'principal',
    loadComponent: () => import('./components/Hub/principal.component').then((m) => m.PrincipalComponent)
  },
  {
    path: 'reportHistory',
    loadComponent: () => import('./components/Reporte/reportHistory.component').then((m) => m.ReportHistoryComponent)
  },
  {
    path : 'mascotasHistory',
    loadComponent: () => import('./components/Mascotas/mascotas.component').then((m) => m.MascotasComponent)
  },
  {
    path : 'coincidencias',
    loadComponent: () => import('./components/coincidencias/coincidencias.component').then((m) => m.CoincidenciasComponent)
  },
  {
    path : 'perfil',
    loadComponent: () => import('./components/Auth/perfil.component').then((m) => m.PerfilComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];

import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { MapaGlobalComponent } from './components/Map/mapa-global.component'; // <-- 1. Importamos el componente del mapa

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, MapaGlobalComponent], // <-- 2. Lo agregamos a los imports de Angular
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('Frontend');
  
  isMenuOpen = false;
  mostrarMenu = false; 
  isLoggedIn = false;  
  isMapaGlobalOpen = false; // <-- 3. Nueva variable para controlar el estado del modal

  constructor(private router: Router) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      
      this.verificarEstadoLogin();
      
      const rutasPublicas = ['/login', '/recuperar', '/register', '/'];
      const esRutaPublica = rutasPublicas.includes(event.urlAfterRedirects);

      this.mostrarMenu = this.isLoggedIn && !esRutaPublica;
      
      if (!this.isLoggedIn && !esRutaPublica) {
        this.router.navigate(['/login']);
      }
    });
  }

  ngOnInit() {
    this.verificarEstadoLogin();
  }

  verificarEstadoLogin() {
    const token = localStorage.getItem('token');
    this.isLoggedIn = !!token; 
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  closeMenu() {
    this.isMenuOpen = false;
  }

  // <-- 4. Métodos para controlar el Radar de Mascotas
  abrirMapaGlobal() {
    this.isMapaGlobalOpen = true;
    this.closeMenu(); // Cierra el menú de hamburguesa automáticamente al abrir el mapa
  }

  cerrarMapaGlobal() {
    this.isMapaGlobalOpen = false;
  }

  logout() {
    this.closeMenu();
    this.cerrarMapaGlobal(); // Por seguridad, cerramos el mapa si estaba abierto
    localStorage.removeItem('token'); 
    localStorage.removeItem('uuid'); // Limpieza completa
    this.isLoggedIn = false;
    this.mostrarMenu = false;
    
    this.router.navigate(['/login']);
  }
}
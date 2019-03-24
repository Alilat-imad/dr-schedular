import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Login } from '../login/model/login';
import { Register } from '../register/model/register';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private BASE_URL = 'http://localhost:8080/api';
  private SIGNUP_URL = `${this.BASE_URL}\\auth\\signup`;
  private SIGNIN_URL = `${this.BASE_URL}\\auth\\signin`;

  constructor(private http: HttpClient, private router: Router) { }

  // Sign Up
  register(signupData: Register): Observable<any> {
      return this.http.post(this.SIGNUP_URL, signupData);
  }
  // Sign In
  login(signinData: Login): Observable<any> {
    return this.http.post(this.SIGNIN_URL, signinData);
  }
  // Check if there is a token stored
  isLoggedIn() {
    return !!localStorage.getItem('token');
  }
  // Get the token from Local Storage
  getToken(){
    return localStorage.getItem('token');
  }
  // Logout / token destrcuction
  logoutUser() {
    localStorage.removeItem('token');
    this.router.navigate((['login']));
  }
}

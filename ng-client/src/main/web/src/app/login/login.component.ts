import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../shared/authentication.service';
import { Login } from './model/login';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  signinData: Login = {usernameOrEmail: '', password: ''};
  popupResult: boolean;
  popupMessage = '';

  constructor(private router: Router, private authservice: AuthenticationService) { }

  ngOnInit() {
  }



  signinUser() {
    this.authservice.login(this.signinData)
    .subscribe(
      res => {
        console.log('Access Token = ' + res.accessToken);
        // Save the Token
        localStorage.setItem('token', res.accessToken);
        this.router.navigate(['/calendar']);
      },
      err => {
        console.log('error = >' + err.message);
        this.popupResult = err.success;
        this.popupMessage = err.message;
        if(err instanceof HttpErrorResponse) {
          if(err.status === 401){
            this.popupResult = false;
            this.popupMessage = '(Username/email) et/ou mot de passe incorrecte.';
          }
        }
        
      }
    )
}
}

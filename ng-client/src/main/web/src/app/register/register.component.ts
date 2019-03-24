import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../shared/authentication.service';
import { Register } from './model/register';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  signupData: Register = {fullName: '', phone: '', address: '', username: '', email: '', password: ''};
  popupResult: boolean;
  popupMessage = '';

  constructor(private authservice: AuthenticationService) { }

  ngOnInit() {
  }

  signupUser() {
    this.authservice.register(this.signupData)
      .subscribe(
        res => {
          console.log('Status ' + res.message);
          console.log('success = ' + res.success);
          this.popupResult = res.success;
          this.popupMessage = res.message;
        },
        err => {
          console.log('error = >' + err.message);
          this.popupResult = err.success;
          this.popupMessage = err.message;
          if(err instanceof HttpErrorResponse) {
            if(err.status === 400){
              this.popupResult = false;
              this.popupMessage = 'Erreur lors de l\'enregistrement, veuillez réessayer.';
            }
          }
          
        }
      )
  }
}

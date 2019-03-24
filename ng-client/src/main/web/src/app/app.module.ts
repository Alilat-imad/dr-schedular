import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LayoutComponent } from './layout/layout.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { LoginComponent } from './login/login.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { CalendarComponent } from './calendar/calendar.component';
import { AppointmentComponent } from './appointment/appointment.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthenticationService } from './shared/authentication.service';
import { RegisterComponent } from './register/register.component';
import { AuthGuard } from './shared/auth.guard';
import { FormsModule } from '@angular/forms';
import { PublicService } from './shared/public.service';
import { TokenInterceptorService } from './shared/token-interceptor.service';
import { PrivateService } from './shared/private.service';

@NgModule({
  declarations: [
    AppComponent,
    LayoutComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    WelcomeComponent,
    FeedbackComponent,
    CalendarComponent,
    AppointmentComponent,
    NotFoundComponent,
    RegisterComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
     // import HttpClientModule after BrowserModule.
     HttpClientModule,
     FormsModule
  ],
  providers: [
      AuthenticationService
    , PublicService
    , PrivateService
    , AuthGuard
    , {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptorService,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

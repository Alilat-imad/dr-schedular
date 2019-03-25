import { Component, OnInit } from '@angular/core';
import { PrivateService } from '../shared/private.service';
import { Calendar } from './model/calendar';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { getLocaleDateFormat } from '@angular/common';
import { AuthenticationService } from '../shared/authentication.service';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {

  popupResult: boolean;
  popupMessage = '';

  calendarRequest = {date: ''};

  calendar: Calendar = {date: '', size: 0, events: []};
  events: Event[] = [];

  constructor(private privateService: PrivateService, private router: Router, private authService: AuthenticationService) { }

  ngOnInit() {
    // On init get today Date.
    this.calendarRequest.date = new Date().toISOString().slice(0, 10);
    this.getAppointment();
  }


  previousDate(value: string) {
    let dt = new Date(value);
    dt.setDate( dt.getDate() - 1 );
    this.calendarRequest.date = dt.toISOString().slice(0, 10);
    this.calendar.date = this.calendarRequest.date;
    this.getAppointment();
  }
  nextDay(value: string) {
    let dt = new Date(value);
    dt.setDate( dt.getDate() + 1 );
    this.calendarRequest.date = dt.toISOString().slice(0, 10);
    this.calendar.date = this.calendarRequest.date;
    this.getAppointment();
  }


  getAppointment() {
    this.privateService.calendarEvent(this.calendarRequest)
    .subscribe(
      res => {
        this.calendar = res;
        this.events = res.events;
        console.log('Success =>' + res.body);
      },
      err => {
        if (err instanceof HttpErrorResponse) {
          if (err.status === 401 || err.status === 500) {

            this.authService.logoutUser();
          } else if (err.status === 404) {
            location.reload();
          }
        }
    }
    );
  }


  deleteEvent(event: Event) {
    if(confirm('Êtes-vous sûr de vouloir supprimer le rendez-vous ? (Un email d\'information sera envoyer au client)')) {
      this.privateService.deleteEvent(event.appointmentId).subscribe(
        res => {
          let indexOfEvent = this.events.indexOf(event);
          this.events.splice(indexOfEvent, 1 );
          this.calendar.size--;
        },
        err => {
          alert('Could not delete Event');
          if (err instanceof HttpErrorResponse) {
            if (err.status === 401 || err.status === 500) {
                this.router.navigate(['/login']);
            }
          }
        }
      );
    }
  }






  updateEventStatus(event: Event) {
    const status = (event.status) ? 'Activer' : 'Annuler';
    if (confirm('Vous allez ' + status + ' le rendez-vous | Mr.' + event.patientName  + ' sera notifer par email. ')) {
      this.privateService.changeEventStatus(event.appointmentId).subscribe(
        res => {

        },
        err => {
              alert('Une erreur s\'est produite lors de la mise à jour de l\'événement.');
              if (err instanceof HttpErrorResponse) {
                if (err.status === 401 || err.status === 500) {
                    this.router.navigate(['/login']);
                }
              }
      }
      );
    }
  }


}

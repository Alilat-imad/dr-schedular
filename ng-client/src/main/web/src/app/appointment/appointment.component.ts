import { Component, OnInit } from '@angular/core';
import { PublicService } from '../shared/public.service';
import { Appointment } from './model/appointment';
import { HttpErrorResponse } from '@angular/common/http';
import { Medecin } from './model/medecin';
import { Availability } from './model/availability';

@Component({
  selector: 'app-appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.css']
})
export class AppointmentComponent implements OnInit {

  availabilityRequest = {medecinId: 0 , date: ''};
  medecinSelectHasError = false;
  popupResult: boolean;
  popupMessage = '';



  // -----
  medecins: Medecin[] = [];
  availabilities: Availability[] = [];
  availabilityData: Appointment = {name: '', email: '', date: '', shiftTimeId: 0, medecinId: 0};
  constructor(private publicService: PublicService) { }

  ngOnInit() {
    // On init get today Date.
    this.availabilityRequest.date = new Date().toISOString().slice(0, 10);
    this.availabilityData.date = this.availabilityRequest.date;
    this.getMedecin();
  }

  getMedecin() {
    this.publicService.getAllMedecin()
    .subscribe(
      res => {
        this.medecins = res;
        console.log('Success =>' + res);
      },
      err => {
        // location.reload();
    }
    );
  }

  getAvailabilities() {
    this.publicService.getAllAvailabilities(this.availabilityRequest)
    .subscribe(
      res => {
        this.availabilities = res;
      },
      err => {
        alert('Erreur durant la prise de rendez-vous, veuillez réessayer plus tard.');
    }
    );
  }



  previousDate(value: string) {
    let dt = new Date(value);
    dt.setDate( dt.getDate() - 1 );
    this.availabilityRequest.date = dt.toISOString().slice(0, 10);
    this.availabilityData.date = this.availabilityRequest.date;
    this.getAvailabilities();
  }
  toDay(medecinId: number) {
    this.availabilityRequest.medecinId = medecinId;
    this.getAvailabilities();
  }
  nextDay(value: string) {
    let dt = new Date(value);
    dt.setDate( dt.getDate() + 1 );
    this.availabilityRequest.date = dt.toISOString().slice(0, 10);
    this.availabilityData.date = this.availabilityRequest.date;
    this.getAvailabilities();
  }



  sendAppointment() {
    if (this.availabilityData.shiftTimeId === 0) {
      alert('Merci de choisir un rendez-vous');
    } else {

    this.publicService.appointment(this.availabilityData)
    .subscribe(
      res => {
        this.popupResult = true;
        this.popupMessage = 'Evenement créer avec succès.';
          },
      err => {
        this.popupMessage = err.error.message;
        this.popupResult = err.error.success;
            }
    );
  }
  }

}

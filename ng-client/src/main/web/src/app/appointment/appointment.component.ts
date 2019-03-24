import { Component, OnInit } from '@angular/core';
import { PublicService } from '../shared/public.service';
import { Appointment } from './model/appointment';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-appointment',
  templateUrl: './appointment.component.html',
  styleUrls: ['./appointment.component.css']
})
export class AppointmentComponent implements OnInit {

  appointmentData: Appointment = {name: '', email: '', date: '', shiftTimeId: 0, medecinId: 0};

  medecinSelectHasError = false;
  popupResult: boolean;
  popupMessage = '';

  constructor(private publicService: PublicService) { }

  ngOnInit() {
  }

  sendAppointment() {
    this.publicService.appointment(this.appointmentData)
    .subscribe(
      res => {
        this.popupResult = true;
        this.popupMessage = 'Evenement créer avec succès.';
      },
      err => {
        this.popupMessage = err.error.message;
        this.popupResult = err.error.success;
    }
    )
  }

  validateMedecinSelect(value) {
		console.log("TCL: AppointmentComponent -> validateMedecinSelect -> value", value)
    
    if(value === '' || value === 0) {
        this.medecinSelectHasError = true;
    } else {
        this.medecinSelectHasError = false;
    }
  }
}

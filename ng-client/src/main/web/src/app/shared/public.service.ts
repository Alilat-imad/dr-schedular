import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Feedback } from '../feedback/model/feedback';
import { Appointment } from '../appointment/model/appointment';
import { Medecin } from '../appointment/model/medecin';

@Injectable({
  providedIn: 'root'
})
export class PublicService {

  private BASE_URL = 'http://localhost:8080/api';
  private FEEDBACK_URL = `${this.BASE_URL}\\mailing\\feedback`;
  private APPOINTMENT_URL = `${this.BASE_URL}\\appointment\\save`;
  private MEDECIN_LIST_URL = `${this.BASE_URL}\\appointment\\medecin`;
  private AVAILABILITY_LIST_URL = `${this.BASE_URL}\\appointment\\availability`;

  constructor(private http: HttpClient, private router: Router) { }

  // Feedback
  feedback(feedbackData: Feedback): Observable<any> {
      return this.http.post(this.FEEDBACK_URL, feedbackData);
  }

  // Take Appointment
  appointment(appointmentData: Appointment): Observable<any> {
      return this.http.post(this.APPOINTMENT_URL, appointmentData);
  }
  // Get Medecin List
  getAllMedecin(): Observable<any> {
    return this.http.get<Medecin[]>(this.MEDECIN_LIST_URL);
  }
  // Get Availability.
  getAllAvailabilities(availabilityRequest: any): Observable<any> {
      return this.http.post(this.AVAILABILITY_LIST_URL, availabilityRequest);
  }
}

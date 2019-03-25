import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Calendar } from '../calendar/model/calendar';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PrivateService {

  private BASE_URL = 'http://localhost:8080/api/private';
  private CALENDAR_EVENTS_URL = `${this.BASE_URL}\\events\\daily`;
  private DELETE_EVENT_URL = `${this.BASE_URL}/events/`;
  private CHANGE_EVENT_STATUS_URL = `${this.BASE_URL}/events/status/`;

  constructor(private http: HttpClient, private router: Router) { }


  // Show Calendar Events
  calendarEvent(calendarRequest: any): Observable<any> {
    return this.http.post(this.CALENDAR_EVENTS_URL, calendarRequest);
  }

  // Delete Event
  deleteEvent(appointmentId: number): Observable<any> {
    return this.http.delete(this.DELETE_EVENT_URL + appointmentId);
  }

  // Change Event Status
  changeEventStatus(appointmentId: number): Observable<any> {
    return this.http.post(this.CHANGE_EVENT_STATUS_URL + appointmentId, {});
  }
}

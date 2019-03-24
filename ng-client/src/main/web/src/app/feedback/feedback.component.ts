import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../shared/authentication.service';
import { PublicService } from '../shared/public.service';
import { Feedback } from './model/feedback';

@Component({
  selector: 'app-feedback',
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent implements OnInit {

  feedbackData: Feedback = {name: '', email: '', feedback: ''};
  popupResult: boolean;
  popupMessage = '';

  constructor(private publicService: PublicService) { }

  ngOnInit() {
  }


  sendFeedback() {
    this.publicService.feedback(this.feedbackData)
    .subscribe(
      res => {
        console.log('Status ' + res.message);
        console.log('success = ' + res.success);
        this.popupResult = res.success;
        this.popupMessage = res.message;
        // Save the Token
      },
      err => {
        console.log('Please Try again later.');
        this.popupResult = err.success;
        this.popupMessage = err.message;
      }
    )
  }
}

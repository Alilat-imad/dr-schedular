package com.ositel.apiserver.Api;

import com.ositel.apiserver.Api.DtoViewModel.FeedbackRequest;
import com.ositel.apiserver.mail.IMailSender;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/mailing")
@CrossOrigin
public class MailController {

    private IMailSender mailSender;

    public MailController(IMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/feedback")
    public void sendFeedback(@RequestBody FeedbackRequest feedback, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad request."
            );
        }
        this.mailSender.sendFeedback(feedback.getEmail(), feedback.getName(), feedback.getFeedback());
    }

    @PostMapping("/notification")
    public void sendNotification(@RequestBody FeedbackRequest feedback, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad request."
            );
        }
        this.mailSender.sendFeedback(feedback.getEmail(), feedback.getName(), feedback.getFeedback());
    }
}

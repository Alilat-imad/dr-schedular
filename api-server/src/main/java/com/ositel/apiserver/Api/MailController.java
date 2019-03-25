package com.ositel.apiserver.Api;

import com.ositel.apiserver.Api.DtoViewModel.Response.ApiResponse;
import com.ositel.apiserver.Api.DtoViewModel.Request.FeedbackRequest;
import com.ositel.apiserver.db.UserRepository;
import com.ositel.apiserver.mail.IMailSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/mailing")
@CrossOrigin
public class MailController {

    private UserRepository userRepository;
    private IMailSender mailSender;

    public MailController(
              UserRepository userRepository
            , IMailSender mailSender
    ) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> sendFeedback(@RequestBody FeedbackRequest feedback, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Bad request."
            );
        }
        this.mailSender.sendFeedback(feedback.getEmail(), feedback.getName(), feedback.getFeedback());
        return ResponseEntity.ok(
                new ApiResponse(true, "Feedback mail successfully sent.")
                                );
    }

}

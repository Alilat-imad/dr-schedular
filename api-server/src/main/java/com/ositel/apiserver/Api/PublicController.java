package com.ositel.apiserver.Api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PublicController {

    @GetMapping("/public")
    public ResponseEntity<?> getPrivateDate(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Public value 1");
        strings.add("Public value 2");
        strings.add("Public value 3");
        return ResponseEntity.ok(strings);
    }
}

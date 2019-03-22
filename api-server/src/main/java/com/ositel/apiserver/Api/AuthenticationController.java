package com.ositel.apiserver.Api;

import com.ositel.apiserver.Api.DtoViewModel.ApiResponse;
import com.ositel.apiserver.Api.DtoViewModel.JwtAuthenticationResponse;
import com.ositel.apiserver.Api.DtoViewModel.SignInRequest;
import com.ositel.apiserver.Api.DtoViewModel.SignUpRequest;
import com.ositel.apiserver.db.RoleRepository;
import com.ositel.apiserver.db.UserRepository;
import com.ositel.apiserver.exception.AppException;
import com.ositel.apiserver.exception.BadRequestException;
import com.ositel.apiserver.model.Role;
import com.ositel.apiserver.model.RoleName;
import com.ositel.apiserver.model.User;
import com.ositel.apiserver.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            throw new BadRequestException("Incorrect SignIn Request");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                          signInRequest.getUsernameOrEmail()
                        , signInRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult bindingResult){

        if(bindingResult.hasErrors())
            throw new BadRequestException("Incorecte SignUp Request");
        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!"), HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            return new ResponseEntity<>(new ApiResponse(false,"Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }
        // Create user's account
        User user = new User(signUpRequest.getName()
                        , signUpRequest.getUsername()
                        , signUpRequest.getEmail()
                        , signUpRequest.getPassword()
                        );
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        // Setting up roles.
//        Set<String> setRoles = signUpRequest.getRoles();
//        Set<Role> userRoles = new HashSet<>();
//        setRoles.forEach(role -> {
//            switch (role){
//                case "admin":
//                    Role role1 = this.roleRepository.findByName(RoleName.ROLE_ADMIN)
//                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: Admin Role not find."));
//                    userRoles.add(role1);
//                    break;
//
//                case "pm":
//                    Role role2 = this.roleRepository.findByName(RoleName.ROLE_PM)
//                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: PM Role not find."));
//                    userRoles.add(role2);
//                    break;
//                default:
////                    Role role3 = this.roleRepository.findByName(RoleName.ROLE_USER)
////                            .orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
////                    userRoles.add(role3);
//                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
//                            .orElseThrow(() -> new AppException("User Role not set."));
//
//                    user.setRoles(Collections.singleton(userRole));
//                    break;
//            }
//        });
//        user.setRoles(userRoles);

        Role userRole = roleRepository.findByName(RoleName.ROLE_MEDECIN)
                .orElseThrow(() -> new AppException("Medecin Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

}

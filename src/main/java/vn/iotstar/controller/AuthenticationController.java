package vn.iotstar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import vn.iotstar.entity.User;
import vn.iotstar.model.LoginResponse;
import vn.iotstar.model.LoginUserModel;
import vn.iotstar.model.RegisterUserModel;
import vn.iotstar.service.AuthenticationService;
import vn.iotstar.service.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(
            JwtService jwtService,
            AuthenticationService authenticationService) {

        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(
            @Valid @RequestBody RegisterUserModel input) {

        User user = authenticationService.signup(input);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginUserModel input) {

        User user = authenticationService.authenticate(input);

        String token = jwtService.generateToken(user);

        LoginResponse response =
            new LoginResponse(
                token,
                jwtService.getExpirationTime()
            );

        return ResponseEntity.ok(response);
    }
}
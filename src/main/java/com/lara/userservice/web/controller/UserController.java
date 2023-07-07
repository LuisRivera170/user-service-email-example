package com.lara.userservice.web.controller;

import com.lara.userservice.domain.User;
import com.lara.userservice.service.UserService;
import com.lara.userservice.web.model.request.UserRequest;
import com.lara.userservice.web.model.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<HttpResponse> createUser(@RequestBody UserRequest userRequest) {
        User newUser = userService.saveUser(userRequest);
        return ResponseEntity
                .created(URI.create(""))
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of("user", newUser))
                                .message("User created")
                                .status(CREATED)
                                .statusCode(CREATED.value())
                                .build()
                );
    }

    @GetMapping
    public ResponseEntity<HttpResponse> confirmUserAccount(@RequestParam String token) {
        Boolean isUserVerified = userService.verifyToken(token);
        return ResponseEntity
                .created(URI.create(""))
                .body(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of("isUserVerified", isUserVerified))
                                .message("Account verified")
                                .status(OK)
                                .statusCode(OK.value())
                                .build()
                );
    }


}

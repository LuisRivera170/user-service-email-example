package com.lara.userservice.service.impl;

import com.lara.userservice.domain.Confirmation;
import com.lara.userservice.domain.User;
import com.lara.userservice.repository.ConfirmationRepository;
import com.lara.userservice.repository.UserRepository;
import com.lara.userservice.service.EmailService;
import com.lara.userservice.service.UserService;
import com.lara.userservice.web.model.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;


    @Override
    public User saveUser(UserRequest userRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(userRequest.getEmail()))) {
            throw new RuntimeException("Email already exists");
        }

        User newUser = User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .isEnabled(false)
                .build();

        userRepository.save(newUser);

        Confirmation newConfirmation = new Confirmation(newUser);
        confirmationRepository.save(newConfirmation);

        // emailService.sendSimpleMailMessage(newUser.getName(), newUser.getEmail(), newConfirmation.getToken());
        // emailService.sendMimeMailMessageWithAttachments(newUser.getName(), newUser.getEmail(), newConfirmation.getToken());
        // emailService.sendMimeMessageWithEmbeddedImages(newUser.getName(), newUser.getEmail(), newConfirmation.getToken());
        // emailService.sendHTMLEmail(newUser.getName(), newUser.getEmail(), newConfirmation.getToken());
        emailService.sendHTMLEmailWithEmbeddedFiles(newUser.getName(), newUser.getEmail(), newConfirmation.getToken());

        return newUser;
    }

    @Override
    public Boolean verifyToken(String token) {
        Confirmation confirmation = confirmationRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Confirmation not found"));

        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail())
                .orElseThrow(() -> new RuntimeException("User not set to confirmation"));

        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

}

package dev.uepb.gereciador.ambientes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.uepb.gereciador.ambientes.config.TokenConfig;
import dev.uepb.gereciador.ambientes.dto.response.LoginResponse;
import dev.uepb.gereciador.ambientes.dto.response.RegisterUserResponse;
import dev.uepb.gereciador.ambientes.dto.resquest.LoginRequest;
import dev.uepb.gereciador.ambientes.dto.resquest.RegisterUserRequest;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenConfig tokenConfig;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(),
                request.password());

        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();

        String token = tokenConfig.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setName(request.name());
        newUser.setEmail(request.email());

        userRepository.save(newUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterUserResponse(
                        newUser.getName(),
                        newUser.getEmail()));
    }

}

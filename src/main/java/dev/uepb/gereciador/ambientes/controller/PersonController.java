package dev.uepb.gereciador.ambientes.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.uepb.gereciador.ambientes.config.AuthConfig;
import dev.uepb.gereciador.ambientes.dto.response.UserResponse;
import dev.uepb.gereciador.ambientes.dto.resquest.RegisterUserRequest;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.service.PersonService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("person")
@Tag(name = "person")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class PersonController {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private PersonService personService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        User user = (User) authConfig.loadUserByUsername(authConfig.getLoggedUsername());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new UserResponse(user.getName(), user.getEmail()));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<List<UserResponse>> allUsers() {
        List<UserResponse> users = personService.findAll().stream()
                .map(item -> new UserResponse(item.getName(), item.getEmail())).toList();

        return ResponseEntity.ok(users);
    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<User> createAdministrator(
            @RequestBody RegisterUserRequest registerUserRequest) {
        User createdAdmin = personService.createAdministrator(registerUserRequest);

        return ResponseEntity.ok(createdAdmin);
    }

}

package dev.uepb.gereciador.ambientes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.uepb.gereciador.ambientes.dto.resquest.CreateEnvironmentRequest;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.service.EnvironmentService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/environments")
public class EnvironmentController {

    @Autowired
    private EnvironmentService environmentService;

    @PostMapping
    public ResponseEntity<Environment> create(
        @Valid @RequestBody CreateEnvironmentRequest createEnvironmentRequest
    ) {
        Environment environment = environmentService.create(createEnvironmentRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(environment);
    }
}

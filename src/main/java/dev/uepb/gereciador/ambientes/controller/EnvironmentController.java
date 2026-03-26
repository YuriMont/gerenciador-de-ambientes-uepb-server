package dev.uepb.gereciador.ambientes.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import dev.uepb.gereciador.ambientes.dto.resquest.SaveEnvironmentRequest;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.service.EnvironmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/environments")
@Tag(name = "Environments")
public class EnvironmentController {

    @Autowired
    private EnvironmentService environmentService;

    @Operation(summary = "Create new environment")
    @PostMapping
    public ResponseEntity<Environment> create(
            @Valid @RequestBody SaveEnvironmentRequest createEnvironmentRequest) {
        Environment environment = environmentService.create(createEnvironmentRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(environment);
    }

    @Operation(summary = "Update a environment")
    @PutMapping("/{environmentId}")
    public ResponseEntity<Environment> update(@PathVariable String environmentId,
            @Valid @RequestBody SaveEnvironmentRequest createEnvironmentRequest) {
        Environment environment =
                environmentService.update(environmentId, createEnvironmentRequest);

        return ResponseEntity.ok(environment);
    }

    @Operation(summary = "Delete a environment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{environmentId}")
    public void delete(@PathVariable String environmentId) {
        environmentService.deleteById(environmentId);
    }

    @Operation(summary = "Get a environment by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/{environmentId}")
    public ResponseEntity<Environment> findById(@PathVariable String environmentId) {
        Environment environment = environmentService.findById(environmentId);

        return ResponseEntity.ok(environment);
    }

    @Operation(summary = "Get all environments")
    @GetMapping
    public ResponseEntity<List<Environment>> findAll() {
        List<Environment> environments = environmentService.findAll();

        return ResponseEntity.ok(environments);
    }
}

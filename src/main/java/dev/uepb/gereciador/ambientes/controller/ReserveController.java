package dev.uepb.gereciador.ambientes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.uepb.gereciador.ambientes.config.AuthConfig;
import dev.uepb.gereciador.ambientes.dto.resquest.CreateReserveRequest;
import dev.uepb.gereciador.ambientes.entity.Reserve;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.service.ReserveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/reserves")
@Tag(name = "Reserves")
public class ReserveController {

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private ReserveService reserveService;

    @Operation(summary = "Create new Reserve")
    @PostMapping
    public ResponseEntity<Reserve> create(
            @Valid @RequestBody CreateReserveRequest createReserveRequest) {
        User user = (User) authConfig.loadUserByUsername(authConfig.getLoggedUsername());

        Reserve reserve = reserveService.createReserve(user.getId(), createReserveRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(reserve);
    }
}

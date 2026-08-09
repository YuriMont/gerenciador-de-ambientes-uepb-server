package dev.uepb.gereciador.ambientes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import dev.uepb.gereciador.ambientes.dto.resquest.SaveEnvironmentRequest;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.repository.EnvironmentRepository;
import dev.uepb.gereciador.ambientes.service.EnvironmentService;

@ExtendWith(MockitoExtension.class)
public class EnvironmentServiceTest {
    @Mock
    private EnvironmentRepository environmentRepository;

    @InjectMocks
    private EnvironmentService environmentService;

    private Environment environment;
    private SaveEnvironmentRequest saveEnvironmentRequest;

    @BeforeEach
    void setUp() {
        environment = new Environment();
        environment.setId("1");
        environment.setName("Laboratório de Redes");
        environment.setDescription("Laboratório para aulas de redes de computadores");

        saveEnvironmentRequest = new SaveEnvironmentRequest("Laboratório de Redes",
                "Laboratório para aulas de redes de computadores", 30, "Bloco B");
    }

    @Test
    void createEnvironmentSuccessfully() {
        when(environmentRepository.save(any(Environment.class))).thenReturn(environment);

        Environment createdEnvironment = environmentService.create(saveEnvironmentRequest);

        assertNotNull(createdEnvironment);
        assertEquals(environment.getName(), createdEnvironment.getName());
        assertEquals(environment.getDescription(), createdEnvironment.getDescription());
        verify(environmentRepository, times(1)).save(any(Environment.class));
    }

    @Test
    void updateEnvironmentSuccessfully() {
        when(environmentRepository.findById("1")).thenReturn(Optional.of(environment));
        when(environmentRepository.save(any(Environment.class))).thenReturn(environment);

        SaveEnvironmentRequest updatedRequest = new SaveEnvironmentRequest(
                "Laboratório de Software", "Laboratório para aulas de desenvolvimento de software",
                40, "Bloco A");
        Environment updatedEnvironment = environmentService.update("1", updatedRequest);

        assertNotNull(updatedEnvironment);
        assertEquals(updatedRequest.name(), updatedEnvironment.getName());
        assertEquals(updatedRequest.description(), updatedEnvironment.getDescription());
        verify(environmentRepository, times(1)).findById("1");
        verify(environmentRepository, times(1)).save(any(Environment.class));
    }

    @Test
    void updateEnvironmentNotFound() {
        when(environmentRepository.findById("2")).thenReturn(Optional.empty());

        SaveEnvironmentRequest updatedRequest = new SaveEnvironmentRequest(
                "Laboratório de Software", "Laboratório para aulas de desenvolvimento de software",
                40, "Bloco A");

        assertThrows(ResponseStatusException.class,
                () -> environmentService.update("2", updatedRequest));
        verify(environmentRepository, times(1)).findById("2");
        verify(environmentRepository, never()).save(any(Environment.class));
    }

    @Test
    void findAllEnvironments() {
        List<Environment> environments = Arrays.asList(environment, new Environment());
        when(environmentRepository.findAll()).thenReturn(environments);

        List<Environment> foundEnvironments = environmentService.findAll();

        assertNotNull(foundEnvironments);
        assertEquals(2, foundEnvironments.size());
        verify(environmentRepository, times(1)).findAll();
    }
}

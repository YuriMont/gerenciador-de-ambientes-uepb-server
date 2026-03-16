package dev.uepb.gereciador.ambientes.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dev.uepb.gereciador.ambientes.dto.resquest.SaveEnvironmentRequest;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.repository.EnvironmentRepository;

@ExtendWith(MockitoExtension.class)
class EnvironmentServiceTest {

    @Mock
    private EnvironmentRepository environmentRepository;

    @InjectMocks
    private EnvironmentService environmentService;

    @Test
    @DisplayName("Deve criar um ambiente com sucesso a partir de um Record DTO")
    void create_ShouldReturnEnvironment_WhenSuccessful() {

        SaveEnvironmentRequest request =
                new SaveEnvironmentRequest("Laboratório 1", "Descrição do Lab");


        Environment result = environmentService.create(request);

        assertNotNull(result);
        assertEquals("Laboratório 1", result.getName());
        assertEquals("Descrição do Lab", result.getDescription());
        verify(environmentRepository, times(1)).save(any(Environment.class));
    }

    @Test
    @DisplayName("Deve atualizar um ambiente existente no MongoDB")
    void update_ShouldReturnUpdatedEnvironment_WhenIdExists() {

        String environmentId = "mongo-id-123";
        SaveEnvironmentRequest request =
                new SaveEnvironmentRequest("Nome Atualizado", "Nova Descrição");

        Environment existingEnvironment = new Environment();
        existingEnvironment.setId(environmentId);
        existingEnvironment.setName("Nome Antigo");

        // Simula o retorno do MongoDB
        when(environmentRepository.findById(environmentId))
                .thenReturn(Optional.of(existingEnvironment));


        Environment result = environmentService.update(environmentId, request);

        assertEquals("Nome Atualizado", result.getName());
        assertEquals("Nova Descrição", result.getDescription());
        verify(environmentRepository).save(existingEnvironment);
    }

    @Test
    @DisplayName("Deve lançar 404 quando o ID do ambiente não existir no MongoDB")
    void update_ShouldThrowNotFound_WhenIdDoesNotExist() {

        String environmentId = "id-inexistente";
        SaveEnvironmentRequest request = new SaveEnvironmentRequest("Teste", "Teste");

        when(environmentRepository.findById(environmentId)).thenReturn(Optional.empty());


        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            environmentService.update(environmentId, request);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(environmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve listar todos os ambientes cadastrados")
    void findAll_ShouldReturnList() {

        Environment env1 = new Environment();
        env1.setName("Lab 1");
        Environment env2 = new Environment();
        env2.setName("Lab 2");

        when(environmentRepository.findAll()).thenReturn(List.of(env1, env2));


        List<Environment> result = environmentService.findAll();

        assertEquals(2, result.size());
        assertEquals("Lab 1", result.get(0).getName());
        verify(environmentRepository, times(1)).findAll();
    }
}

package dev.uepb.gereciador.ambientes.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import dev.uepb.gereciador.ambientes.dto.resquest.SaveEnvironmentRequest;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.repository.EnvironmentRepository;

/**
 * Serviço responsável pela lógica de negócio relacionada a ambientes.
 *
 * <p>Provê operações de criação, atualização e listagem de ambientes físicos
 * da UEPB que estão disponíveis para reserva.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Service
public class EnvironmentService {

    @Autowired
    private EnvironmentRepository environmentRepository;

    /**
     * Cria e persiste um novo ambiente com os dados fornecidos.
     *
     * @param createEnvironmentRequest DTO com os dados do novo ambiente
     * @return o ambiente criado, com ID e timestamps preenchidos
     */
    public Environment create(SaveEnvironmentRequest createEnvironmentRequest) {
        Environment environment = new Environment();
        environment.setName(createEnvironmentRequest.name());
        environment.setDescription(createEnvironmentRequest.description());
        environment.setCapacity(createEnvironmentRequest.capacity());
        environment.setBlock(createEnvironmentRequest.block());
        environment.setImageUrl(createEnvironmentRequest.imageUrl());
        environmentRepository.save(environment);
        return environment;
    }

    /**
     * Atualiza os dados de um ambiente existente.
     *
     * @param environmentId            o ID do ambiente a ser atualizado
     * @param createEnvironmentRequest DTO com os novos dados do ambiente
     * @return o ambiente atualizado
     * @throws ResponseStatusException com status {@code 404 Not Found} se o ambiente não for encontrado
     */
    public Environment update(String environmentId, SaveEnvironmentRequest createEnvironmentRequest) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ambiente não encontrado com ID: " + environmentId));

        environment.setName(createEnvironmentRequest.name());
        environment.setDescription(createEnvironmentRequest.description());
        environment.setCapacity(createEnvironmentRequest.capacity());
        environment.setBlock(createEnvironmentRequest.block());
        environment.setImageUrl(createEnvironmentRequest.imageUrl());
        environmentRepository.save(environment);
        return environment;
    }

    /**
     * Retorna a lista de todos os ambientes cadastrados no sistema.
     *
     * @return lista de {@link Environment}; pode estar vazia se não houver nenhum cadastrado
     */
    public List<Environment> findAll() {
        return environmentRepository.findAll();
    }

    /**
     * Busca um ambiente pelo seu ID.
     *
     * @param environmentId o ID do ambiente a ser buscado
     * @return o {@link Environment} encontrado, ou {@code null} se não existir
     */
    public Environment findById(String environmentId) {
        return environmentRepository.findById(environmentId).orElse(null);
    }

    /**
     * Remove um ambiente pelo seu ID.
     *
     * @param environmentId o ID do ambiente a ser deletado
     */
    public void deleteById(String environmentId) {
        environmentRepository.deleteById(environmentId);
    }
}

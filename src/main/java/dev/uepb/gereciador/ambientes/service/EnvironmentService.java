package dev.uepb.gereciador.ambientes.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import dev.uepb.gereciador.ambientes.dto.resquest.SaveEnvironmentRequest;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.repository.EnvironmentRepository;

@Service
public class EnvironmentService {

    @Autowired
    private EnvironmentRepository environmentRepository;

    public Environment create(SaveEnvironmentRequest createEnvironmentRequest) {
        Environment environment = new Environment();

        environment.setName(createEnvironmentRequest.name());
        environment.setDescription(createEnvironmentRequest.description());

        environmentRepository.save(environment);

        return environment;
    }

    public Environment update(Long environmentId, SaveEnvironmentRequest createEnvironmentRequest) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        environment.setName(createEnvironmentRequest.name());
        environment.setDescription(createEnvironmentRequest.description());

        environmentRepository.save(environment);

        return environment;
    }

    public List<Environment> findAll() {
        return environmentRepository.findAll();
    }
}

package dev.uepb.gereciador.ambientes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.uepb.gereciador.ambientes.dto.resquest.CreateEnvironmentRequest;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.repository.EnvironmentRepository;

@Service
public class EnvironmentService {

    @Autowired
    private EnvironmentRepository environmentRepository;

    public Environment create(CreateEnvironmentRequest createEnvironmentRequest) {
        Environment environment = new Environment();

        environment.setName(createEnvironmentRequest.name());
        environment.setDescription(createEnvironmentRequest.description());

        environmentRepository.save(environment);

        return environment;
    }
}

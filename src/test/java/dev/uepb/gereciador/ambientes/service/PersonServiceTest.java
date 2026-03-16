package dev.uepb.gereciador.ambientes.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.uepb.gereciador.ambientes.dto.resquest.RegisterUserRequest;
import dev.uepb.gereciador.ambientes.entity.Role;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import dev.uepb.gereciador.ambientes.repository.RoleRepository;
import dev.uepb.gereciador.ambientes.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PersonService personService;

    @Test
    @DisplayName("Deve criar um administrador mapeando nome, email e senha criptografada")
    void createAdministrator_ShouldMapAllFieldsCorrectly() {
        // Agora com os 3 argumentos: name, email, password
        RegisterUserRequest request =
                new RegisterUserRequest("Admin Teste", "admin@uepb.edu", "senha123");

        Role adminRole = new Role();
        adminRole.setName(UserRole.ADMIN);

        when(roleRepository.findByName(UserRole.ADMIN)).thenReturn(Optional.of(adminRole));
        when(passwordEncoder.encode("senha123")).thenReturn("hash_seguro_789");

        // Simula o retorno do save
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = personService.createAdministrator(request);

        assertAll("Validação completa do mapeamento do Usuário",
                () -> assertEquals("admin@uepb.edu", result.getEmail()),
                () -> assertEquals("hash_seguro_789", result.getPassword()),
                () -> assertEquals(UserRole.ADMIN, result.getRole().getName()),
                () -> assertEquals("Admin Teste", result.getName()));

        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve falhar se a role de administrador não existir no banco")
    void createAdministrator_ShouldFailWhenRoleNotFound() {
        // Arrange - Passando os 3 argumentos também aqui para não dar erro
        RegisterUserRequest request = new RegisterUserRequest("Nome", "email@test.com", "123");
        when(roleRepository.findByName(UserRole.ADMIN)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> personService.createAdministrator(request));
    }
}

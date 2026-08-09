package dev.uepb.gereciador.ambientes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import dev.uepb.gereciador.ambientes.dto.resquest.RegisterUserRequest;
import dev.uepb.gereciador.ambientes.entity.Role;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import dev.uepb.gereciador.ambientes.repository.RoleRepository;
import dev.uepb.gereciador.ambientes.repository.UserRepository;
import dev.uepb.gereciador.ambientes.service.PersonService;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PersonService personService;

    private User user;
    private Role role;
    private RegisterUserRequest registerUserRequest;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName(UserRole.ADMIN);

        user = new User();
        user.setEmail("admin@example.com");
        user.setPassword("encodedPassword");
        user.setRole(role);

        registerUserRequest =
                new RegisterUserRequest("Admin User", "admin@example.com", "password123");
    }

    @Test
    void createAdministratorSuccessfully() {
        when(roleRepository.findByName(UserRole.ADMIN)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(registerUserRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = personService.createAdministrator(registerUserRequest);

        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getPassword(), createdUser.getPassword());
        assertEquals(user.getRole().getName(), createdUser.getRole().getName());
        verify(roleRepository, times(1)).findByName(UserRole.ADMIN);
        verify(passwordEncoder, times(1)).encode(registerUserRequest.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findAllUsers() {
        List<User> users = Arrays.asList(user, new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = personService.findAll();

        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        verify(userRepository, times(1)).findAll();
    }
}

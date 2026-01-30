package dev.uepb.gereciador.ambientes.seeder;

import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import dev.uepb.gereciador.ambientes.entity.Role;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import dev.uepb.gereciador.ambientes.repository.RoleRepository;

@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    private void loadRoles() {
        UserRole[] roleNames = new UserRole[] {UserRole.USER, UserRole.ADMIN, UserRole.OWNER};

        Arrays.stream(roleNames).forEach((roleName) -> {
            Optional<Role> optionalRole = roleRepository.findByName(roleName);

            optionalRole.ifPresentOrElse(System.out::println, () -> {
                Role roleToCreate = new Role();

                roleToCreate.setName(roleName);

                roleRepository.save(roleToCreate);
            });
        });
    }
}

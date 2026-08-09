package dev.uepb.gereciador.ambientes.seeder;

import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import dev.uepb.gereciador.ambientes.entity.Role;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import dev.uepb.gereciador.ambientes.repository.RoleRepository;

/**
 * Componente responsável por popular o banco de dados com os papéis padrão do sistema.
 *
 * <p>Executado automaticamente na inicialização da aplicação (ao receber o evento
 * {@link ContextRefreshedEvent}). Verifica se cada papel definido em {@link UserRole}
 * já existe na coleção {@code roles}; caso contrário, cria-o.</p>
 *
 * <p>Os papéis criados por este seeder são: {@code USER}, {@code ADMIN} e {@code OWNER}.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 * @see UserRole
 * @see Role
 */
@Component
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(RoleSeeder.class);

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Invocado pelo Spring quando o contexto da aplicação é inicializado ou atualizado.
     *
     * <p>Uma falha ao popular os papéis é registrada mas não propagada: sem isso, um banco
     * indisponível na subida derruba o contexto do Spring inteiro e a aplicação entra em
     * loop de restart, em vez de subir e responder assim que o banco voltar.</p>
     *
     * @param contextRefreshedEvent o evento de atualização do contexto
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            this.loadRoles();
        } catch (RuntimeException exception) {
            log.error("Não foi possível popular os papéis padrão. "
                    + "A aplicação vai subir sem eles — verifique a conexão com o MongoDB.",
                    exception);
        }
    }

    /**
     * Itera sobre todos os valores de {@link UserRole} e garante que cada um
     * possua um documento correspondente na coleção {@code roles}.
     *
     * <p>Se o papel já existir, apenas imprime o registro no console.
     * Caso contrário, cria e persiste um novo {@link Role}.</p>
     */
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

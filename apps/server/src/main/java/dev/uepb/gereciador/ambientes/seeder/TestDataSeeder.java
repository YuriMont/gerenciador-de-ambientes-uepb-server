package dev.uepb.gereciador.ambientes.seeder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import dev.uepb.gereciador.ambientes.entity.Environment;
import dev.uepb.gereciador.ambientes.entity.Reserve;
import dev.uepb.gereciador.ambientes.entity.Reserve.Slot;
import dev.uepb.gereciador.ambientes.entity.Role;
import dev.uepb.gereciador.ambientes.entity.User;
import dev.uepb.gereciador.ambientes.enums.ReserveStatus;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import dev.uepb.gereciador.ambientes.repository.EnvironmentRepository;
import dev.uepb.gereciador.ambientes.repository.ReserveRespository;
import dev.uepb.gereciador.ambientes.repository.RoleRepository;
import dev.uepb.gereciador.ambientes.repository.UserRepository;

/**
 * Seeder de dados de teste para popular o sistema com usuários, ambientes e reservas.
 *
 * <p>Executado automaticamente apenas quando o perfil ativo é {@code test} (definido pela variável
 * {@code APP_PROFILE}). Cria perfis de exemplo em todas as hierarquias ({@code OWNER},
 * {@code ADMIN} e {@code USER}), ambientes físicos da UEPB e reservas em todos os status,
 * incluindo para a data atual — o que alimenta o painel da tela de início e a fila de aprovação.</p>
 *
 * <p>O seeder é <strong>idempotente</strong>: usuários são verificados por e-mail e ambientes por
 * nome, então executar a aplicação novamente apenas completa o que ainda não existe.</p>
 *
 * <p><strong>Credenciais geradas:</strong> todos os usuários usam a senha {@code senha@123},
 * armazenada com hash BCrypt.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 * @see RoleSeeder
 */
@Component
@Profile("test")
public class TestDataSeeder implements ApplicationListener<ContextRefreshedEvent> {

    /** Senha padrão compartilhada por todos os usuários de teste. */
    private static final String DEFAULT_PASSWORD = "senha@123";

    /** E-mail do proprietário do sistema. */
    private static final String OWNER_EMAIL = "owner@uepb.edu.br";

    /** E-mail do administrador. */
    private static final String ADMIN_EMAIL = "admin@uepb.edu.br";

    /** E-mail de um usuário comum. */
    private static final String USER_EMAIL = "maria.silva@uepb.edu.br";

    /** E-mail de um segundo usuário comum. */
    private static final String SECOND_USER_EMAIL = "carlos.santos@uepb.edu.br";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private ReserveRespository reserveRespository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Invocado pelo Spring quando o contexto da aplicação é inicializado.
     *
     * @param contextRefreshedEvent o evento de atualização do contexto
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        User owner = this.createUser("Maria Oliveira", OWNER_EMAIL, UserRole.OWNER);
        User admin = this.createUser("João Pereira", ADMIN_EMAIL, UserRole.ADMIN);
        User user = this.createUser("Marina Silva", USER_EMAIL, UserRole.USER);
        User secondUser = this.createUser("Carlos Santos", SECOND_USER_EMAIL, UserRole.USER);

        Environment lab = this.createEnvironment("Laboratório de Informática 02",
                "Laboratório com 30 computadores, projetor e acesso à internet.", 30, "Bloco B");
        Environment classroom = this.createEnvironment("Sala de Aula 101",
                "Sala climatizada com projetor, quadro branco e 50 lugares.", 50, "Bloco A");
        Environment auditorium = this.createEnvironment("Auditório Central",
                "Auditório completo para eventos, com palco e sistema de som.", 220, "Bloco A");
        Environment chemistryLab = this.createEnvironment("Laboratório de Química 03",
                "Laboratório com bancadas, capela de exaustão e vidrarias de química.", 30, "Bloco C");

        this.createReserves(owner, admin, user, secondUser,
                List.of(lab, classroom, auditorium, chemistryLab));
    }

    /**
     * Cria um usuário de teste, caso ainda não exista pelo e-mail informado.
     *
     * @param name  nome completo do usuário
     * @param email e-mail utilizado como login
     * @param role  perfil de acesso desejado
     * @return o usuário recém-criado ou o já existente no banco
     */
    private User createUser(String name, String email, UserRole role) {
        return userRepository.findUserByEmail(email)
                .map(userDetails -> (User) userDetails)
                .map(this::ensureTestPassword)
                .orElseGet(() -> {
                    User user = new User();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
                    user.setRole(this.findOrCreateRole(role));
                    return userRepository.save(user);
                });
    }

    /**
     * Garante que um usuário já existente mantenha a senha padrão de teste.
     *
     * <p>Se o usuário foi criado antes com outra senha, o hash BCrypt é substituído por
     * {@link #DEFAULT_PASSWORD}, preservando o restante dos dados de teste.</p>
     *
     * @param user o usuário já cadastrado no banco
     * @return o usuário com a senha de teste garantida
     */
    private User ensureTestPassword(User user) {
        if (user.getPassword() == null || !passwordEncoder.matches(DEFAULT_PASSWORD,
                user.getPassword())) {
            user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            return userRepository.save(user);
        }
        return user;
    }

    /**
     * Busca um papel no banco, criando-o se ainda não existir.
     *
     * <p>Garante que o {@link RoleSeeder} tenha executado antes, mas não depende da ordem dos
     * listeners do contexto.</p>
     *
     * @param roleName o nome do papel desejado
     * @return o papel localizado (ou criado)
     */
    private Role findOrCreateRole(UserRole roleName) {
        Optional<Role> optionalRole = roleRepository.findByName(roleName);
        return optionalRole.orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        });
    }

    /**
     * Cria um ambiente de teste, caso ainda não exista pelo nome informado.
     *
     * @param name        nome do ambiente
     * @param description descrição do ambiente
     * @param capacity    quantidade de lugares
     * @param block       bloco onde o ambiente fica
     * @return o ambiente recém-criado ou o já existente no banco
     */
    private Environment createEnvironment(String name, String description, Integer capacity,
            String block) {
        return environmentRepository.findAll().stream()
                .filter(environment -> environment.getName().equals(name)).findFirst()
                .orElseGet(() -> {
                    Environment environment = new Environment();
                    environment.setName(name);
                    environment.setDescription(description);
                    environment.setCapacity(capacity);
                    environment.setBlock(block);
                    environment.setImageUrl("https://storage.example.com/" + name.toLowerCase()
                            .replaceAll("[^a-z0-9]+", "-") + ".jpg");
                    return environmentRepository.save(environment);
                });
    }

    /**
     * Cria um lote de reservas de exemplo distribuídas entre hoje e os próximos dias.
     *
     * <p>As reservas cobrem os três status do sistema — pendentes (fila de aprovação), aprovadas
     * (ocupam horário e compõem a agenda do dia) e recusadas — e somam horas ao painel da tela de
     * início. Sempre respeitam as regras de negócio: data presente ou futura, slots de 1 hora em
     * hora cheia, dentro de 08:00–22:00.</p>
     *
     * @param owner       usuário com perfil OWNER
     * @param admin       usuário com perfil ADMIN
     * @param user        usuário comum
     * @param secondUser  segundo usuário comum
     * @param environments ambientes criados pelo seeder
     */
    private void createReserves(User owner, User admin, User user, User secondUser,
            List<Environment> environments) {

        if (reserveRespository.count() > 0) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextWeek = today.plusDays(7);

        Environment lab = environments.get(0);
        Environment classroom = environments.get(1);
        Environment auditorium = environments.get(2);
        Environment chemistryLab = environments.get(3);

        this.createReserve(user, lab, "Aula prática de Redes de Computadores", 28, tomorrow,
                LocalTime.of(8, 0), LocalTime.of(10, 0), ReserveStatus.PENDING);
        this.createReserve(secondUser, chemistryLab, "Prática de laboratório da disciplina de Química Geral",
                24, tomorrow, LocalTime.of(14, 0), LocalTime.of(16, 0), ReserveStatus.PENDING);
        this.createReserve(user, classroom, "Monitoria de Cálculo I", 20, nextWeek,
                LocalTime.of(18, 0), LocalTime.of(19, 0), ReserveStatus.REJECTED);

        this.createReserve(admin, auditorium, "Palestra de abertura do congresso de extensão", 180,
                today, LocalTime.of(8, 0), LocalTime.of(10, 0), ReserveStatus.APPROVED);
        this.createReserve(user, lab, "Oficina de programação em Java", 25, today,
                LocalTime.of(10, 0), LocalTime.of(12, 0), ReserveStatus.APPROVED);
        this.createReserve(secondUser, classroom, "Aula de reforço de Física", 35, tomorrow,
                LocalTime.of(9, 0), LocalTime.of(11, 0), ReserveStatus.APPROVED);
        this.createReserve(user, chemistryLab, "Ensaio do grupo de pesquisa em química ambiental",
                12, nextWeek, LocalTime.of(15, 0), LocalTime.of(17, 0), ReserveStatus.APPROVED);

        this.createReserve(owner, auditorium, "Reunião do conselho universitário", 60, nextWeek,
                LocalTime.of(10, 0), LocalTime.of(12, 0), ReserveStatus.PENDING);
    }

    /**
     * Cria e persiste uma reserva de exemplo, respeitando os limites de horário do sistema.
     *
     * @param user                   usuário solicitante
     * @param environment            ambiente reservado
     * @param justification          justificativa da reserva
     * @param numberOfParticipants   quantidade de participantes (dentro da capacidade)
     * @param date                   data da reserva (presente ou futura)
     * @param startTime              início do primeiro slot
     * @param endTime                fim do último slot
     * @param status                 status inicial da reserva
     */
    private void createReserve(User user, Environment environment, String justification,
            Integer numberOfParticipants, LocalDate date, LocalTime startTime, LocalTime endTime,
            ReserveStatus status) {

        Reserve reserve = new Reserve();
        reserve.setUserId(user.getId());
        reserve.setEnvironmentId(environment.getId());
        reserve.setJustification(justification);
        reserve.setNumberOfParticipants(numberOfParticipants);
        reserve.setDate(date);
        reserve.setStatus(status);
        reserve.setSlots(this.buildSlots(startTime, endTime));

        reserveRespository.save(reserve);
    }

    /**
     * Gera a lista de slots de 1 hora compreendidos entre os horários informados.
     *
     * @param startTime início da faixa (hora cheia)
     * @param endTime   fim da faixa
     * @return os slots de horário da reserva
     */
    private List<Slot> buildSlots(LocalTime startTime, LocalTime endTime) {
        List<Slot> slots = new java.util.ArrayList<>();
        LocalTime current = startTime;

        while (current.isBefore(endTime)) {
            Slot slot = new Slot();
            slot.setStartTime(current);
            slot.setEndTime(current.plusHours(1));
            slots.add(slot);
            current = current.plusHours(1);
        }

        return slots;
    }
}
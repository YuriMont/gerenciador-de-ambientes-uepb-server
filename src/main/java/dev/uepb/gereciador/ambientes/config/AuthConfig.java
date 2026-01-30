package dev.uepb.gereciador.ambientes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dev.uepb.gereciador.ambientes.repository.UserRepository;

@Service
public class AuthConfig implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository
                .findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public String getLoggedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object authObject = authentication.getPrincipal();

        if (authObject instanceof JWTUserData) {
            return ((JWTUserData) authObject).email();
        } else {
            return "";
        }
    }

}

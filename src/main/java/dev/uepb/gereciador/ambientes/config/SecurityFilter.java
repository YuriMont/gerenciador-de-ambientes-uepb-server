package dev.uepb.gereciador.ambientes.config;

import java.io.IOException;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenConfig tokenConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizedHeader = request.getHeader("Authorization");

        if (!Strings.isEmpty(authorizedHeader) && authorizedHeader.startsWith("Bearer ")) {
            String token = authorizedHeader.substring("Bearer ".length());

            Optional<JWTUserData> optUser = tokenConfig.validateToken(token);

            if (optUser.isPresent()) {
                JWTUserData userData = optUser.get();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userData,
                        null, null);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);

    }

}

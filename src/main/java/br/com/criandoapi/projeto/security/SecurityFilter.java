package br.com.criandoapi.projeto.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class SecurityFilter extends OncePerRequestFilter {

    // Rotas públicas que não precisam de autenticação
    private static final List<String> PUBLIC_ROUTES = List.of(
            "/usuarios/login"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String authorizationHeader = request.getHeader("Authorization");

        // Se for uma rota pública (login ou cadastro), pula o filtro
        if (PUBLIC_ROUTES.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Para outros endpoints, verifica se há token
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            Authentication auth = TokenUtil.validate(request);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}

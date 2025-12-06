package com.cydrag.fridgeapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            Optional<String> optionalJwt = getJwt(request);
            if (optionalJwt.isPresent() && jwtUtils.validateJwtToken(optionalJwt.get())) {
                String userId = jwtUtils.getUserIdFromJwtToken(optionalJwt.get());

                UserDetails userDetails = userDetailsService.loadUserById(userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            log.warn("Stale JWT Usage. Token valid but User ID {} not found in DB.", e.getMessage());

            handlerExceptionResolver.resolveException(request, response, null, e);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private static Optional<String> getJwt(HttpServletRequest request) {
        String authorizationValue = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationValue == null || authorizationValue.isBlank() || !authorizationValue.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }

        return Optional.of(authorizationValue.substring(BEARER_PREFIX.length()));
    }
}

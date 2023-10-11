package com.izakdvlpr.todolist.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.izakdvlpr.todolist.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuthentication extends OncePerRequestFilter {
    @Autowired
    private IUserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authorization = request.getHeader("Authorization");
        var token = authorization.substring("Basic".length()).trim();
        var credentials = new String(Base64.getDecoder().decode(token)).split(":");

        var username = credentials[0];
        var password = credentials[1];

        var user = this.userRepository.findByUsername(username);

        if (user == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());

            return;
        }

        var passwordValid = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified;

        if (!passwordValid) {
            response.sendError(HttpStatus.UNAUTHORIZED.value());

            return;
        }

        filterChain.doFilter(request, response);
    }
}

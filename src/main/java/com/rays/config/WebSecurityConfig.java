package com.rays.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the application
 * Rajat Dhakad
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enables @PreAuthorize / @PostAuthorize annotations
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is recommended for hashing user passwords
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // Expose AuthenticationManager as a Spring bean
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            // Disable CSRF because we are using JWT (not cookies for auth)
            .csrf().disable()

            // Define public endpoints that do not require authentication
            .authorizeRequests()
                .antMatchers("/Auth/**", "/User/profilePic/**", "/Jasper/**").permitAll()
                .antMatchers("/actuator/health", "/actuator/info").permitAll() // public actuator endpoints
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated() // All other requests must be authenticated
            .and()

            // Configure exception handling (return 401 for unauthorized access)
            .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()

            // Make the session stateless (every request must have a JWT)
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // Enable CORS to allow frontend (Angular) to call backend
        httpSecurity.cors();
    }
}

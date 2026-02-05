package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Required to make @PreAuthorize work in your Controllers
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF for easier testing (Enable in production!)
            .csrf(csrf -> csrf.disable())

            // 2. Define URL Permissions
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**").permitAll() // Public access
                .requestMatchers("/api/courses/**").hasRole("TEACHER")      // Restrict Course Creation
                .requestMatchers("/api/delete/**").hasRole("TEACHER")       // Restrict Deletion
                .anyRequest().authenticated()                               // Everything else needs login
            )

            // 3. Configure Custom Form Login
            .formLogin(form -> form
                .loginPage("/login")                // Use our custom login.html
                .defaultSuccessUrl("/dashboard", true) // Go here after successful login
                .failureUrl("/login?error=true")    // Go here if login fails
                .permitAll()
            )

            // 4. Configure Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Defining users in memory with roles
        // {noop} tells Spring the password is plain text (no encoding)
        
        UserDetails student = User.builder()
                .username("student_user")
                .password("{noop}123")
                .roles("STUDENT")
                .build();

        UserDetails teacher = User.builder()
                .username("teacher_user")
                .password("{noop}123")
                .roles("TEACHER")
                .build();

        return new InMemoryUserDetailsManager(student, teacher);
    }
}
package com.example.demo.config;

import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    public UserDetailsService userDetailsService(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository
    ) {
        return username -> {
            Teacher teacher = teacherRepository.findByUsername(username).orElse(null);
            if (teacher != null) {
                return User.builder()
                        .username(teacher.getUsername())
                        .password(normalizePassword(teacher.getPassword()))
                        .roles("TEACHER")
                        .build();
            }

            Student student = studentRepository.findByUsername(username).orElse(null);
            if (student != null) {
                return User.builder()
                        .username(student.getUsername())
                        .password(normalizePassword(student.getPassword()))
                        .roles("STUDENT")
                        .build();
            }

            throw new UsernameNotFoundException("User not found: " + username);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private String normalizePassword(String storedPassword) {
        if (storedPassword == null) {
            return null;
        }
        if (storedPassword.startsWith("{")) {
            return storedPassword;
        }
        return "{noop}" + storedPassword;
    }
}
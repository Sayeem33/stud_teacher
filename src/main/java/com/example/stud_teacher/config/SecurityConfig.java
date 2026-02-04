package com.example.stud_teacher.config;

import com.example.stud_teacher.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Static resources - UI
                        .requestMatchers("/", "/index.html", "/app.js", "/favicon.ico").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                        
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/error").permitAll()
                        
                        // Department - Teacher has full CRUD, Student can only read
                        .requestMatchers(HttpMethod.GET, "/api/departments/**").authenticated()
                        .requestMatchers("/api/departments/**").hasRole("TEACHER")
                        
                        // Course - Teacher has full CRUD, Student can only read
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").authenticated()
                        .requestMatchers("/api/courses/**").hasRole("TEACHER")
                        
                        // Student - Student can view/edit own profile and manage their course enrollments
                        .requestMatchers(HttpMethod.GET, "/api/students/me").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/students/me").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/students/me/courses").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/students/me/courses/**").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/me/courses/**").hasRole("STUDENT")
                        // Student can view students from their own department
                        .requestMatchers(HttpMethod.GET, "/api/students/my-department").hasRole("STUDENT")
                        // Teacher has full CRUD on all students
                        .requestMatchers("/api/students/**").hasRole("TEACHER")
                        
                        // Teacher - View all teachers is allowed for all authenticated users
                        .requestMatchers(HttpMethod.GET, "/api/teachers").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/teachers/{id}").authenticated()
                        // Teacher can view/edit own and has full CRUD on all teachers
                        .requestMatchers(HttpMethod.GET, "/api/teachers/me").hasRole("TEACHER")
                        .requestMatchers(HttpMethod.PUT, "/api/teachers/me").hasRole("TEACHER")
                        .requestMatchers("/api/teachers/**").hasRole("TEACHER")
                        
                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

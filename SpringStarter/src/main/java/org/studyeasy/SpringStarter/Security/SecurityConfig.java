package org.studyeasy.SpringStarter.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String[] WHITELIST = {
        "/",
        "/login",
        "/resources/**",
        "/db-console/**"
};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection for the H2 console
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/logout") // Disable CSRF protection for H2 console
                .ignoringRequestMatchers("/db-console/**")
                .ignoringRequestMatchers("/**")
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login").permitAll() // Allow access to login page
                .requestMatchers(WHITELIST).permitAll()
                .requestMatchers("/**").permitAll() // All other pages require authentication
            )
            .formLogin(form -> form
                .loginPage("/login") // Specify the login page
                .defaultSuccessUrl("/", true) // Redirect to home page after successful login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL to trigger logout
                .logoutSuccessUrl("/login") // Redirect to login page after successful logout with logout parameter
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        // Admin user
        manager.createUser(User.withUsername("admin")
            .password(passwordEncoder().encode("adminPass"))
            .roles("ADMIN")
            .build());
        
        // Normal user
        manager.createUser(User.withUsername("user")
            .password(passwordEncoder().encode("userPass"))
            .roles("USER")
            .build());

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

package com.ositel.apiserver.config;

import com.ositel.apiserver.security.AuthenticationEntryPointImpl;
import com.ositel.apiserver.security.JwtAuthenticationFilter;
import com.ositel.apiserver.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled  = true,
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private AuthenticationEntryPointImpl unauthorized;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter JwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                    .and()
                .csrf()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(unauthorized)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .authorizeRequests()
                    .antMatchers("/"
                            , "/favicon.ico"
                            , "/**/*.png"
                            , "/**/*.gif"
                            , "/**/*.svg"
                            , "/**/*.jpg"
                            , "/**/*.ttf"
                            , "/**/*.woff"
                            , "/**/*.woff2"
                            , "/**/*.html"
                            , "/**/*.css"
                            , "/**/*.js")
                            .permitAll()
                    .antMatchers("/api/auth/**")
                            .permitAll()
                    .antMatchers("/api/public/**")
                            .permitAll()
                .antMatchers("/api/public/appointment/save")
                .permitAll()
                .antMatchers("/api/public/medecin")
                .permitAll()
                .antMatchers("/api/public/availability")
                .permitAll()
                    .antMatchers("/api/mailing/feedback")
                            .permitAll()
                    .anyRequest()
                            .authenticated();


        // Add our custom JWT security filter
        http.addFilterBefore(JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}

package wostal.call.of.code.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import wostal.call.of.code.service.MyUserDetailsService;

@Configuration
@EnableWebSecurity
@ComponentScan("wostal.call.of.code")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	MyUserDetailsService myUserDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(myUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().and().authorizeRequests()
		.antMatchers("/authorize", "/websocket/**").permitAll()
		.anyRequest().hasAnyRole("ADMIN", "USER")
		.and().csrf().disable()
		.formLogin().disable();
	}

}
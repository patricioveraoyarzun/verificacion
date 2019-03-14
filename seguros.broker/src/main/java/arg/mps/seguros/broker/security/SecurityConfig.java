package arg.mps.seguros.broker.security;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import arg.mps.seguros.broker.security.filters.JwtTokenAuthenticationFilter;
import arg.mps.seguros.broker.security.filters.JwtUsernameAndPasswordAuthenticationFilter;

@Order(1)
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtConfig jwtConfig;
 
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Override
  	protected void configure(HttpSecurity http) throws Exception {
		
		logger.info("########## SecurityConfig -> configure HttpSecurity ##########");
		
		http .csrf().disable()
				// make sure we use stateless session; session won't be used to store user's
				// state.
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				// handle an authorized attempts
				.exceptionHandling()
				.authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED)).and()
				// Add a filter to validate the tokens with every request
				.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig))
				.addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
				// authorization requests config
				.authorizeRequests()
				// allow all who are accessing "auth" service
				.antMatchers(HttpMethod.POST, jwtConfig.getUri()).permitAll()
				// Any other request must be authenticated
				.antMatchers("/api/**").authenticated();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		logger.info("########## SecurityConfig -> configure AuthenticationManagerBuilder ##########");
		auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
	}	
}

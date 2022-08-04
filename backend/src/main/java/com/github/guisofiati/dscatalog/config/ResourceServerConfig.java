package com.github.guisofiati.dscatalog.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer // a classe sera o resource server do oauth2
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private Environment env; // pegar as envs do app
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	// endpoints
	private static final String[] PUBLIC = {"/oauth/token", "/h2-console/**"};
	
	private static final String[] OPERATOR_OR_ADMIN = {"/products/**", "/categories/**"};
	
	private static final String[] ADMIN = {"/users/**"};
	
	// sera capaz de decodificar o token e ver se o token esta expirado ou nao, validar o token em si
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}

	// configurar as rotas
	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		// liberar o h2
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll() // quem estiver acessando as rotas PUBLIC nao precisa de login, liberado para todos
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll() // libera somente GET nas rotas OPERATOR_OR_ADMIN apenas com os ROLES informados
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN")
		.antMatchers(ADMIN).hasRole("ADMIN")
		.anyRequest().authenticated(); // qualquer outra rota, tem que estar logado
	}
}

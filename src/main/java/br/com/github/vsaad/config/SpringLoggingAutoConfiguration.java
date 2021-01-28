package br.com.github.vsaad.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import br.com.github.vsaad.client.RestTemplateSetHeaderInterceptor;
import br.com.github.vsaad.filter.SpringLoggingFilter;
import br.com.github.vsaad.util.UniqueIDGenerator;

@Configuration
public class SpringLoggingAutoConfiguration {

	private String ignorePatterns;
	private boolean logHeaders;
	@Autowired(required = false)
	Optional<RestTemplate> template;

	@Bean
	public UniqueIDGenerator generator() {
		return new UniqueIDGenerator();
	}

	@Bean
	public SpringLoggingFilter loggingFilter() {
		return new SpringLoggingFilter(generator(), ignorePatterns, logHeaders);
	}

	@Bean
	@ConditionalOnMissingBean(RestTemplate.class)
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		List<ClientHttpRequestInterceptor> interceptorList = new ArrayList<ClientHttpRequestInterceptor>();
		interceptorList.add(new RestTemplateSetHeaderInterceptor());
		restTemplate.setInterceptors(interceptorList);
		return restTemplate;
	}

	@PostConstruct
	public void init() {
		template.ifPresent(restTemplate -> {
			List<ClientHttpRequestInterceptor> interceptorList = new ArrayList<ClientHttpRequestInterceptor>();
			interceptorList.add(new RestTemplateSetHeaderInterceptor());
			restTemplate.setInterceptors(interceptorList);
		});
	}


	public String getIgnorePatterns() {
		return ignorePatterns;
	}

	public void setIgnorePatterns(String ignorePatterns) {
		this.ignorePatterns = ignorePatterns;
	}

	public boolean isLogHeaders() {
		return logHeaders;
	}

	public void setLogHeaders(boolean logHeaders) {
		this.logHeaders = logHeaders;
	}
}

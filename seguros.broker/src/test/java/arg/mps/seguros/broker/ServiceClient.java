package arg.mps.seguros.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import arg.mps.seguros.broker.security.UserCredentials;

@Service
public class ServiceClient {

	private final Logger logger = LoggerFactory.getLogger(ServiceClient.class);
	private final RestTemplate restTemplate;
	
	public ServiceClient(@Autowired RestTemplateBuilder restTemplateBuilder) {
	        restTemplate = restTemplateBuilder.build();
	}
	
	public String testCredentials(UserCredentials credentials) {
		
		String respuesta = null;
		try {
			respuesta = restTemplate.postForObject("http://localhost:8080/auth", credentials, String.class);
		}catch(RestClientException ex) {
			logger.error("Hubo un error.", ex);
			respuesta = null;
		}
		
		return respuesta;
	}

}

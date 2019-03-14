package arg.mps.seguros.broker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import arg.mps.seguros.broker.dao.SecurityDao;
import arg.mps.seguros.broker.security.dto.Role;
import arg.mps.seguros.broker.security.dto.User;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
	
	private final Logger logger = LoggerFactory.getLogger(ApplicationTests.class);
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	/*@Autowired
	private ServiceClient serviceClient;*/
	
	@Autowired
	private SecurityDao securityDao;
 
	@Transactional
	@Test
	public void contextLoads() {
		
		String password = "jnempu123";
		String encodedPass = passwordEncoder.encode(password);
		logger.info("encodedPass:"+encodedPass);
		
		Role role = new Role();
		role.setCode("R002");
		role.setName("ADMIN");
		
		securityDao.saveRole(role);
		
		User user = new User();
		user.setName("jnempu2");
		user.setPassword(encodedPass);
		user.setRole(role);
		
		securityDao.saveUser(user);
		
		User jnempuUser = securityDao.findUserByName("jnempu2");
		
		if(jnempuUser!=null) {
			logger.info(" encontrado:"+jnempuUser);
		}
		
		/*UserCredentials credentials = new UserCredentials("jnempu", password);
		
		String respuesta = serviceClient.testCredentials(credentials);
		logger.info("respuesta:"+respuesta);*/
	}

}

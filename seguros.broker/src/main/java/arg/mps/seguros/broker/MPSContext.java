package arg.mps.seguros.broker;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import arg.mps.seguros.broker.api.integration.BrokerService;

@Component
public class MPSContext implements ApplicationContextAware{

	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
		
	}

	public static Collection<BrokerService> getBrokerServices(){
		return applicationContext.getBeansOfType(BrokerService.class).values();
	}
}

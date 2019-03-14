package arg.mps.seguros.broker.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
public class DatabaseConfiguration {
	
	@Autowired
	private ResourceLoader rl;
	
	@Autowired
	private Environment env;
	
	@Value("${datasource.jdbcUrl}")
	private String jdbcUrl;
	
	@Value("${datasource.username}")
	private String username;
	
	@Value("${datasource.password}")
	private String password;
	
	@Value("${datasource.driver-class-name}")
	private String driverClassName;
	
	@Value("${datasource.hikari.maximum-pool-size}")
	private Integer maximumPoolSize;
	
	@Value("${datasource.hikari.connectionTimeout}")
	private Long connectionTimeout;
	
	@Value("${datasource.hikari.idleTimeout}")
	private Long idleTimeout;
	
	@Value("${datasource.hikari.maxLifetime}")
	private Long maxLifetime;
	
	@Value("${datasource.hikari.pool-name}")
	private String poolName;
	
	@Bean
	@ConfigurationProperties("spring.datasource")
	public DataSource dataSource() {
		
		HikariConfig config = new HikariConfig();
		config.setAutoCommit(true);
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(username);
		config.setPassword(password);
		config.setDriverClassName(driverClassName);
		config.setConnectionTimeout(connectionTimeout);
		config.setMaximumPoolSize(maximumPoolSize);
		config.setPoolName(poolName);
		config.setIdleTimeout(idleTimeout);
		config.setMaxLifetime(maxLifetime);
		
	    DataSource dataSource = new HikariDataSource(config);
	    
	    return dataSource;
	}
	
    @Bean
    public LocalSessionFactoryBean getSessionFactory(@Autowired DataSource dataSource) {
    	
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setHibernateProperties(this.getHibernateProperties());
        factoryBean.setMappingLocations(this.loadResources());
        return factoryBean;
    }
 
    @Bean
    public HibernateTransactionManager getTransactionManager(@Autowired SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }
    
    @Bean
    public HibernateTemplate getHibernateTemplate(@Autowired SessionFactory sessionFactory) {
    	return new HibernateTemplate(sessionFactory);
    }
    
    private Properties getHibernateProperties() {
    	Properties hibernateProperties = new Properties();
    	
    	hibernateProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
    	hibernateProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
    	hibernateProperties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
    	hibernateProperties.put("hibernate.ddl-auto", env.getProperty("hibernate.ddl-auto"));
    	hibernateProperties.put("hibernate.connection.autocommit"
    			, env.getProperty("hibernate.connection.autocommit"));
        return hibernateProperties;

    }
    
    private Resource[] loadResources() {
        Resource[] resources = null;
        try {
            resources = ResourcePatternUtils.getResourcePatternResolver(rl)
                    .getResources("classpath:/config/hbm/*.hbm.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resources;
    }
}

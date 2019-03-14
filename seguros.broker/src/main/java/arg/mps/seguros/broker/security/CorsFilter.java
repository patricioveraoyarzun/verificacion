package arg.mps.seguros.broker.security;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(CorsFilter.class);
			
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		logger.info("####### CORSFilter -> doFilter #########");
		
		HttpServletRequest request = (HttpServletRequest) req;
		
		logger.info("####### CORSFilter -> doFilter -> Request Method: {} #########", request.getMethod());
		
        Enumeration<String> headerNames = request.getHeaderNames();

        if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                	String key = headerNames.nextElement();                	
                	String value = request.getHeader(key);
                	logger.info("####### CORSFilter -> doFilter -> Header: key={} - value={} #########", key, value);
                }
        }
		
		HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Expose-Headers", "Authorization");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}

}

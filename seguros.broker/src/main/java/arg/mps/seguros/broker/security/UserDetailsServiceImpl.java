package arg.mps.seguros.broker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import arg.mps.seguros.broker.dao.SecurityDao;
import arg.mps.seguros.broker.security.dto.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService  {
	
	@Autowired
	private SecurityDao securityDao;

	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = this.securityDao.findUserByName(username);
		
		if(user!=null)
			return new UserDetailsImpl(user);
		
		// If user not found. Throw this exception.
		throw new UsernameNotFoundException("Username: " + username + " not found");
	}
	
}

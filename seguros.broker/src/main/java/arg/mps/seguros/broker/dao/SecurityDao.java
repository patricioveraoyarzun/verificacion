package arg.mps.seguros.broker.dao;

import arg.mps.seguros.broker.security.dto.Role;
import arg.mps.seguros.broker.security.dto.User;

public interface SecurityDao {
	
	public void saveUser(User user);
	
	public void saveRole(Role role);
	
	public User findUserByName(String username);

	public User findUserById(Integer id);

}

package arg.mps.seguros.broker.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import arg.mps.seguros.broker.dao.SecurityDao;
import arg.mps.seguros.broker.security.dto.Role;
import arg.mps.seguros.broker.security.dto.User;

@Repository
public class SecurityDaoImpl implements SecurityDao {
	
	@Autowired 
	private HibernateTemplate hibernateTemplate;
	
	@Override
	public void saveUser(User user) {
		this.hibernateTemplate.save(user);
	}
	
	@Override
	public void saveRole(Role role) {
		this.hibernateTemplate.save(role);
	}
	
	@Override
	public User findUserById(Integer id) {
		return this.hibernateTemplate.get(User.class, id);
	}
	
	@Override
	public User findUserByName(String username) {
		return this.hibernateTemplate.execute(session -> {
		     CriteriaBuilder builder = session.getCriteriaBuilder();
		     CriteriaQuery<User> query = builder.createQuery(User.class);
		     Root<User> userRoot = query.from(User.class);
		     Predicate nameCondition = builder.equal(userRoot.get("name"), username); 
		     query.where(nameCondition);
		     return (User)session.createQuery(query).uniqueResult();
		});
	}

}

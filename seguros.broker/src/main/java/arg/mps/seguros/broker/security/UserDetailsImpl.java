package arg.mps.seguros.broker.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import arg.mps.seguros.broker.security.dto.User;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;
	private User user;
	
	public UserDetailsImpl(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
            	.commaSeparatedStringToAuthorityList("ROLE_" + user.getRole());
		
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.user.getAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.user.getAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.user.getCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return this.user.getEnabled();
	}

}

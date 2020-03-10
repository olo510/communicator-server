package wostal.call.of.code.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import wostal.call.of.code.entity.User;

public class MyUserPrincipal implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6661742674242249400L;
	private User user;
	private Collection<GrantedAuthority> authorities;

	public MyUserPrincipal(User user, Collection<GrantedAuthority> authorities) {
		this.user = user;
		this.authorities = authorities;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getPassword() {
		return user.getPassword();
	}
	
	public User getUser() {
		return user;
	}

	public String getUsername() {
		return user.getNick();
	}

	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

}
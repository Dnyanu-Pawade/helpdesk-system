package com.helpdesk.security;
import com.helpdesk.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
@Getter
public class UserDetailsImpl implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final String fullName;
    private final String role;
    private final Collection<? extends GrantedAuthority> authorities;
    public UserDetailsImpl(User u) {
        this.id = u.getId(); this.username = u.getUsername(); this.password = u.getPassword();
        this.email = u.getEmail(); this.fullName = u.getFullName(); this.role = u.getRole().name();
        this.authorities = List.of(new SimpleGrantedAuthority(u.getRole().name()));
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}

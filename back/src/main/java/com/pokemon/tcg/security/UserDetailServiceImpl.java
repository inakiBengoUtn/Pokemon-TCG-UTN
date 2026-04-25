package com.pokemon.tcg.security;

import com.pokemon.tcg.modules.user.models.User;
import com.pokemon.tcg.modules.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("USER SERVICE AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        User userEntity = userRepo.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid Credential"));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");

        return UserDetailImpl.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(List.of(grantedAuthority))
                .build();
    }
}

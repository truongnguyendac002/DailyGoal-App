package com.projectn.projectn.security;

import com.projectn.projectn.model.User;
import com.projectn.projectn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(username);
        if (userOptional.isEmpty()){
            throw new UsernameNotFoundException("User not found with email: "+ username);
        }
        User user = userOptional.get();
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
        );

    }
}

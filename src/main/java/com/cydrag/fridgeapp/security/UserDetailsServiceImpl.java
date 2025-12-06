package com.cydrag.fridgeapp.security;

import com.cydrag.fridgeapp.model.User;
import com.cydrag.fridgeapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return FridgeUserDetails.build(user);
    }

    @Transactional
    public UserDetails loadUserById(String uuid) {
        UUID id = UUID.fromString(uuid);
        return userRepository.findById(id)
                .map(FridgeUserDetails::build)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + uuid));
    }
}

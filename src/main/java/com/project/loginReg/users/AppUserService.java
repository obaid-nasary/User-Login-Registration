package com.project.loginReg.users;

import com.project.loginReg.registration.token.ConfirmationToken;
import com.project.loginReg.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
    private final AppUserRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usersRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(AppUser appUser){

        boolean userExists = usersRepository.findByEmail(appUser.getEmail()).isPresent();

        if(userExists){

//        TODO: Check if attributes are the same and
//        TODO: if email not confirmed send confirmation email

            throw new IllegalStateException("Email already taken");
        }



         String encodePassword = bCryptPasswordEncoder.encode(appUser.getPassword());

        appUser.setPassword(encodePassword);

        usersRepository.save(appUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
            token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), appUser
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

//        TODO: SEND EMAIL

       return token;
    }


    public int enableAppUser(String email) {
        return usersRepository.enableAppUser(email);
    }
}

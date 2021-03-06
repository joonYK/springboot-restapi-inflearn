package jy.learning.bootrestapi.accounts;

import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername() {
        //given
        Set<AccountRole> roles = new HashSet<>();
        roles.add(AccountRole.ADMIN);
        roles.add(AccountRole.USER);

        String password = "1234";
        String username = "joonyeop@email.com";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(roles)
                .build();
        this.accountService.saveAccount(account);

        //when
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        //then
        assertTrue(this.passwordEncoder.matches(password, userDetails.getPassword()));
        assertThat(userDetails.getUsername()).isEqualTo(username);
    }

    @Test
    public void findByUsernameFail() {
        //given
        String username = "random@email.com";

        //when & then
        assertThrows(UsernameNotFoundException.class, () -> {
            accountService.loadUserByUsername(username);
        });
    }
}
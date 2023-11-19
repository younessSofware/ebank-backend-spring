package org.sid;

import org.sid.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SecServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner start(AccountService accountService){
        return args->{
//            accountService.saveUser("customer@customer.com",
//                    "12345678", "12345678");
//            accountService.save(new AppRole(null,"CUSTOMER"));
//            accountService.save(new AppRole(null,"BANKING"));
//            accountService.save(new AppRole(null,"ADMIN"));
//            Stream.of("user1","user2","user3","admin").forEach(un->{
//                accountService.saveUser(un,"1234","1234");
//            });
//            accountService.addRoleToUser("admin","ADMIN");
        };
    }
    @Bean
    BCryptPasswordEncoder getBCPE(){
        return new BCryptPasswordEncoder();
    }

}


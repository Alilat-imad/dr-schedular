package com.ositel.apiserver.db;

import com.ositel.apiserver.model.Role;
import com.ositel.apiserver.model.RoleName;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "populate-default-data", havingValue = "true")
public class DbSeeder implements CommandLineRunner {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public DbSeeder(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Delete All users
        userRepository.deleteAll();
        // Delete all roles
        roleRepository.deleteAll();

        Set<Role> roleSet = new HashSet<>();
        // Add default roles
        Role role1 =  this.roleRepository.save(new Role(RoleName.ROLE_ADMIN));
//        Role role2 =  this.roleRepository.save(new Role(RoleName.ROLE_PM));
        Role role3 =  this.roleRepository.save(new Role(RoleName.ROLE_MEDECIN));

        roleSet.add(role1);
//        roleSet.add(role2);
        roleSet.add(role3);

        // Add default user
//         var user = new User("Imad", "ialilat", "alilat.imad@gmail.com", bCryptPasswordEncoder.encode("2000io1w"));
//         user.setRoles(roleSet);
//         this.userRepository.save(user);

    }
}

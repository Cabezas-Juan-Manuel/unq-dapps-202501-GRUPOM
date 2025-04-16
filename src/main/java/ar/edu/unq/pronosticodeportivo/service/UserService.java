package ar.edu.unq.pronosticodeportivo.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ar.edu.unq.pronosticodeportivo.model.User;
import ar.edu.unq.pronosticodeportivo.repositories.IUserRepository;
import ar.edu.unq.pronosticodeportivo.service.Errors.UserErrors;

@Service
public class UserService {
    @Autowired
    private IUserRepository userDao;

    public User createUser(String name, String password){
    User user = userDao.getByName(name);
    if (user != null){
        throw new RuntimeException("user already registered");
    }
    return this.generateNewUser(name, password);
    }

    private User generateNewUser(String name, String password) {        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        User newUser = new User(name, encodedPassword);
        return userDao.save(newUser);
    }

    public User getUser(String name, String password) {
        User user = Optional.ofNullable(this.userDao.getByName(name))
        .orElseThrow(() -> new RuntimeException(UserErrors.INVALID_PASSWORD_OR_USERNAME.getMessage()));
        validatePassWord(user.getPassword(), password);
        return userDao.getByName(name);
    }

    private void validatePassWord(String encodedPassword, String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(rawPassword, encodedPassword)){
            throw  new RuntimeException(UserErrors.INVALID_PASSWORD_OR_USERNAME.getMessage());
        }
    }



}

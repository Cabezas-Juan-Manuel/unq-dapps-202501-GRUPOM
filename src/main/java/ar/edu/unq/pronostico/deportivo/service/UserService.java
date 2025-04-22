package ar.edu.unq.pronostico.deportivo.service;

import java.util.Optional;

import ar.edu.unq.pronostico.deportivo.model.User;
import ar.edu.unq.pronostico.deportivo.service.Errors.UserErrors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ar.edu.unq.pronostico.deportivo.repositories.IUserRepository;

@Service
public class UserService {
    private final IUserRepository userDao;

    public UserService(IUserRepository userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String password){
        User user = userDao.getByName(name);
        if (user != null){
            throw new IllegalArgumentException("user already registered");
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
        User user = Optional
                .ofNullable(this.userDao.getByName(name))
                .orElseThrow(() -> new RuntimeException(
                        UserErrors.INVALID_PASSWORD_OR_USERNAME.getMessage()
                ));
        validatePassWord(user.getPassword(), password);
        return userDao.getByName(name);
    }

    private void validatePassWord(String encodedPassword, String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(rawPassword, encodedPassword)){
            throw new IllegalArgumentException(UserErrors.INVALID_PASSWORD_OR_USERNAME.getMessage());
        }
    }

    public void deleteUsers() {
        userDao.deleteAll();
    }

}

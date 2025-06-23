package ar.edu.unq.pronostico.deportivo.service;

import java.time.LocalDateTime;
import java.util.Optional;
import ar.edu.unq.pronostico.deportivo.model.Activity;
import ar.edu.unq.pronostico.deportivo.model.User;
import ar.edu.unq.pronostico.deportivo.repositories.IActivityRepository;
import ar.edu.unq.pronostico.deportivo.errors.Errors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ar.edu.unq.pronostico.deportivo.repositories.IUserRepository;

@Service
public class UserService {

    private IUserRepository userDao;

    private IActivityRepository activityDao;

    public UserService(IUserRepository userDao, IActivityRepository activityDao) {
        this.userDao = userDao;
        this.activityDao = activityDao;
    }


    public User createUser(String name, String password){
        User user = userDao.getByName(name);
        if (user != null){
            throw new IllegalArgumentException(Errors.ALREADY_REGISTERED.getMessage());
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
                .orElseThrow(() -> new NullPointerException(
                        Errors.INVALID_PASSWORD_OR_USERNAME.getMessage()
                ));
        validatePassWord(user.getPassword(), password);
        return userDao.getByName(name);
    }

    private void validatePassWord(String encodedPassword, String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(rawPassword, encodedPassword)){
            throw new IllegalArgumentException(Errors.INVALID_PASSWORD_OR_USERNAME.getMessage());
        }
    }

    public void deleteUsers() {
        userDao.deleteAll();
    }

    public Page<Activity> getUserActivy(String userName, int page) {
        User user = Optional
                .ofNullable(this.userDao.getByName(userName))
                .orElseThrow(() -> new NullPointerException(
                        Errors.USER_NOT_FOUND.getMessage()
                ));
        Pageable pageable = PageRequest.of(page, 10);
        return activityDao.getActivityByUser(user.getName(), pageable);
    }

    public void registerActivity(String user, String url, String method, String queryParams, LocalDateTime timeStamp) {
        User registereduser = userDao.getByName(user);
        Activity newActivity = new Activity(user, url, method, queryParams, timeStamp);
        registereduser.addActivity(newActivity);
        userDao.save(registereduser);
    }
}

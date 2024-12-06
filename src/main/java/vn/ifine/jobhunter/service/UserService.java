package vn.ifine.jobhunter.service;

import org.springframework.stereotype.Service;

import vn.ifine.jobhunter.domain.User;
import vn.ifine.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void handleCreateUser(User user) {
        this.userRepository.save(user);
    }

}

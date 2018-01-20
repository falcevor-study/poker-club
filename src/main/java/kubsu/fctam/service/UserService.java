package kubsu.fctam.service;

import kubsu.fctam.dao.UserRepository;
import kubsu.fctam.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public void save(User user) { repository.save(user); }

    public Iterable<User> getAll() { return repository.findAll(); }

    public User get(int id) { return repository.findOne(id); }
}

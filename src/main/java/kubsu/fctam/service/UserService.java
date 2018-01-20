package kubsu.fctam.service;

import kubsu.fctam.dao.UserRepository;
import kubsu.fctam.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    public Iterable<User> getAll() { return repository.findAll(); }

    public User get(int id) { return repository.findOne(id); }

    public User getByLogin(String login) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(repository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .filter((user) -> user.getLogin().equals(login))
                .findFirst().orElse(null);
    }
}

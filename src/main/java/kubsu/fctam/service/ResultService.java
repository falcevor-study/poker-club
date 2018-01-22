package kubsu.fctam.service;

import kubsu.fctam.dao.ResultRepository;
import kubsu.fctam.entity.Result;
import kubsu.fctam.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ResultService {
    @Autowired
    private ResultRepository repository;

    public void save(Result result) {
        repository.save(result);
    }

    // метод для того, чтобы получить доступ к первым 10 максимальным выигрышам
    public List<Result> getFirst10(User user) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(repository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .filter(result -> user.equals(result.getUser()))
                .sorted(Comparator.reverseOrder())
                .limit(10)
                .collect(Collectors.toList());
    }
}

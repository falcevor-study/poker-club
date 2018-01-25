package kubsu.fctam.service;

import kubsu.fctam.dao.GameRepository;
import kubsu.fctam.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GameService {
    @Autowired
    private GameRepository repository;

    public void save(Game game) {
        repository.save(game);
    }

    // метод для доступа ко всем играм
    public List<Game> getAll() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(repository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .sorted()
                .collect(Collectors.toList());
    }

    public void endAll() {
        StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(repository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .sorted()
                .filter(game -> game.getEndDtm() == null)
                .forEach(game -> {
                    game.setEndDtm(new Date());
                    repository.save(game);
                });
//        repository.findAll()
//                .forEach(
//                (game) -> {
//                    game.setEndDtm(new Date());
//                    repository.save(game);
//                }
//        );
    }
}

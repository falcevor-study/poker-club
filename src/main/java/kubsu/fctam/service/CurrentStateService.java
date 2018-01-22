package kubsu.fctam.service;

import kubsu.fctam.dao.CurrentStateRepository;
import kubsu.fctam.entity.CurrentState;
import kubsu.fctam.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

@Service
public class CurrentStateService {
    @Autowired
    private CurrentStateRepository stateRepository;

    public void save(CurrentState state){
        stateRepository.save(state);
    }

    public CurrentState getStateByGame(Game game) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(stateRepository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .filter(state -> state.getGame().getId() == game.getId())
                .findFirst().orElse(null);
    }
}

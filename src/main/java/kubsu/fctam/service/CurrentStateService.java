package kubsu.fctam.service;

import kubsu.fctam.dao.CurrentStateRepository;
import kubsu.fctam.entity.CurrentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrentStateService {
    @Autowired
    private CurrentStateRepository stateRepository;

    public void save(CurrentState state){
        stateRepository.save(state);
    }
}

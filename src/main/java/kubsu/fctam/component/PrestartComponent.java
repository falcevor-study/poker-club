package kubsu.fctam.component;

import kubsu.fctam.service.ChairService;
import kubsu.fctam.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class PrestartComponent{
    @Autowired
    GameService gameService;

    @Autowired
    ChairService chairService;

    @PostConstruct
    public void init(){
        gameService.endAll();
        chairService.deleteAll();
    }
}
package kubsu.fctam.controller;

import kubsu.fctam.service.TableService;
import kubsu.fctam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/game")
public class GameController {
    @Autowired
    TableService tableService;

    @Autowired
    UserService userService;


    @RequestMapping(method = RequestMethod.GET)
    public String joinGame(Model model) {
        return "game";
    }
}

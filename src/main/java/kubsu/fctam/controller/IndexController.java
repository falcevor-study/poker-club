package kubsu.fctam.controller;

import kubsu.fctam.entity.User;
import kubsu.fctam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "index")
    public String getIndex(Model model) {
        model.addAttribute("top10Users", userService.getTop10());
        return "index";
    }

    @SubscribeMapping("/out")
    @SendTo("/topic/in")
    public User testConn(User user) throws Exception{
        System.out.println(user.getLogin());
        return userService.getByLogin("ksenia");
    }
}

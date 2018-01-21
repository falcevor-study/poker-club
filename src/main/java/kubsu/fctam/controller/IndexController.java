package kubsu.fctam.controller;

import kubsu.fctam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/index")
public class IndexController {

    @Autowired
    UserService userService;

    @RequestMapping
    public String getIndex(Model model) {
        model.addAttribute("top10Users", userService.getTop10());
        return "index";
    }
}

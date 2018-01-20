package kubsu.fctam.controller;

import kubsu.fctam.entity.User;
import kubsu.fctam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService service;

    @RequestMapping("/register")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("error", "Необходимо заполнить все поля формы регистрации.");
        return "registration";
    }

    @RequestMapping("/register/error")
    public String registrationError(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("error", "Указанный логин уже занят другой учетной записью.");
        return "registration";
    }

    @RequestMapping(value = "/register/submit", method = RequestMethod.POST)
    public String submitRegistration(@ModelAttribute User user) {
        user.setMoney(2000); // Стартовый капитал.
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes())); // Хешируем пароль, ну а как же?

        try {
            service.save(user);
        } catch (Exception ex) {
            return "redirect:error";
        }
        return "redirect:../";
    }
}

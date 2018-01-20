package kubsu.fctam.controller;

import kubsu.fctam.entity.User;
import kubsu.fctam.service.SecurityService;
import kubsu.fctam.service.UserService;
import kubsu.fctam.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService service;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute(
                "error",
                "Для успешной регистрации необходимо заполнить все поля формы."
        );
        return "registration";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registrationSubmit(@ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "error",
                    "Указанный логин занят другой учетной записью."
            );
            return "registration";
        }

        user.setMoney(2000); // Стартовый капитал.
        service.save(user);
        securityService.autologin(user.getLogin(), user.getPassword());
        return "redirect:../";
    }


    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public String login(Model model) {
        return "authorization";
    }


    @RequestMapping(value = "/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession(false);
        SecurityContextHolder.clearContext();
        session= request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        for(Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
        }
    }
}

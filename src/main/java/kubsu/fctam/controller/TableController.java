package kubsu.fctam.controller;

import kubsu.fctam.entity.Table;
import kubsu.fctam.entity.User;
import kubsu.fctam.service.TableService;
import kubsu.fctam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/tables")
public class TableController {
    @Autowired
    private TableService tableService;

    @Autowired
    UserService userService;

    @RequestMapping
    public String mainPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth.getName(); //get logged in username
        User user = userService.getByLogin(login);

        model.addAttribute("tables", tableService.getAll());
        model.addAttribute("user_id", user.getId());
        return "table_selection";
    }

    /**
     * метод для доступа к столу
     * @param model - модель, чтобы передавать данные
     * @param name - имя стола
     * @return - имя стола
     */
    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    public String getTable(Model model, @PathVariable("name") String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth.getName(); //get logged in username

        User user = userService.getByLogin(login);
        Table table = tableService.getTableByName(name);

        model.addAttribute("login", user.getLogin());
        model.addAttribute("user_id", user.getId());
        model.addAttribute("table_name", name);
        model.addAttribute("table_id", table.getId());
        return "game";
    }

}

package kubsu.fctam.controller;

import kubsu.fctam.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping
    public String mainPage(Model model) {
        model.addAttribute("tables", tableService.getAll());
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
        model.addAttribute("table_name", name);
        return "game";
    }
}

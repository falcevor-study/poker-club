package kubsu.fctam.controller;

import kubsu.fctam.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tables")
public class TableController {
    @Autowired
    private TableService tableService;

    @RequestMapping
    public String mainPage(Model model) {
        model.addAttribute("tables", tableService.getAll());
        return "index";
    }
}

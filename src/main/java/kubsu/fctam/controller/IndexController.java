package kubsu.fctam.controller;

import kubsu.fctam.entity.Chair;
import kubsu.fctam.entity.Game;
import kubsu.fctam.entity.Table;
import kubsu.fctam.entity.User;
import kubsu.fctam.service.TableService;
import kubsu.fctam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Controller
public class IndexController {

    @Autowired
    UserService userService;

    @Autowired
    TableService tableService;

    @RequestMapping(value = "index")
    public String getIndex(Model model) {
        model.addAttribute("top10Users", userService.getTop10());
        return "index";
    }

    /**
     * метод для общения с клиентом
     * @param map - то, что отправляет клиент
     * @return - возвращает объекты в виде JSON
     */
    @SubscribeMapping("/out")
    @SendTo("/topic/in")
    public Chair testConn(HashMap map) {
        User user = userService.getByLogin((String) map.get("login"));
        Table table = tableService.getTableByName((String) map.get("table_name"));
        if (table != null) {
            Chair chair = new Chair(
                    user,
                    null,
                    -1,
                    "watcher",
                    0,
                    null,
                    null);
            table.addChair(chair);
            return chair;
        }
        else
            return null;
    }
}

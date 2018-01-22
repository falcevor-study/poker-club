package kubsu.fctam.controller;

import kubsu.fctam.entity.CurrentState;
import kubsu.fctam.entity.Table;
import kubsu.fctam.entity.User;
import kubsu.fctam.service.TableService;
import kubsu.fctam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;

@Controller
@RequestMapping("/game")
public class GameController {
    @Autowired
    TableService tableService;

    @Autowired
    UserService userService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @RequestMapping(method = RequestMethod.GET)
    public String joinGame(Model model) {
        return "game";
    }


    /**
     * Обработка сообщения о действии пользователя в торгах.
     *
     * @param action - набор параметров действия (пользователь, стол, тип действия, величина ставки)
     */
    @MessageMapping("/game/action")
    public void getAction(HashMap action) {
        int table_id = Integer.parseInt(action.get("table_id").toString());
        int user_id = Integer.parseInt(action.get("table_id").toString());

        int value = Integer.parseInt(action.get("value").toString());
        String actionType = action.get("action").toString();
        User user = userService.get(user_id);
        Table table = tableService.get(table_id);





        // TODO: Логика изменения состояния сущностей текущей игры на столе table после хода игрока user, который
        // TODO: заключается в действии actionType на сумму value.
        // TODO: В результате должны быть изменены сущности, состояния, порождены новые сущности, если нужно и т.д.
        // TODO: В сущностях карт масти и значения должны быть идентичны тем классам в deck.css
        // TODO: Если игрок сбросил карты, то масть и значение у его карт должны быть нан none.
        // TODO: И необходим некоторый набор дополнительных параметров, которые могут просто вычисляться здесь, либо
        // TODO: их можно включить в какие-нибудь сущности.
        // TODO: А именно, нужно получить следующие данные, чтобы они были доступны прямо здесь:

        // В Chair должно быть 2 поля с количеством фишек: 1 - (pot) число фишек на руках, 2 - (bet) число фишек, которое
        // игрок поставил в текущих торгах, но эти фишки еще не включены в общий банк игры.

        User currentUser = userService.get(10); // Пользователь, который будет торговаться следующим. Идем против часовой
        // стрелки и смотрим, есть ли еще живые, то есть у них ненулевой баланс (на руках, не всего) и они не сбросили карты.

        Integer minBet = 100; // Число, на которое необходимо поднять свою ставку, чтобы продолжить игру и не сбрасывать.
        // по идее это разница между максимальной ставкой в торгах и ставкой текущего пользователя.

        // Эти поля, кстати, можно в CurrentState запихать. Наверное, так и стоит поступить. Еще для того, чтобы логику
        // реализовать тебе все равно кое-какие поля придется добавлять, наверное.
        CurrentState currentState = new CurrentState(); // Его, кстати тоже нужно сюда записать будет, создаю новый, чтобы не ругалось.





        // А вот это уже по моей части))))))))))
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("currentUser", user.getId());
        parameters.put("minBet", minBet);
        parameters.put("currentState", currentState);

        simpMessagingTemplate.convertAndSend("/topic/game/state/" + table.getName(), parameters);
    }


    /**
     * Обработка запроса на обновление визуализации карт под определенного пользователя.
     * То есть, отображаются только карты пользователя, который отправил запрос, остальные скрыты или отсутствуют.
     * @param request - набор параметров для обновления(пользователь, стол)
     */
    @MessageMapping("/game/card")
    public void updateCards(HashMap request) {

    }

}

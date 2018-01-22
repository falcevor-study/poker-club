package kubsu.fctam.controller;

import kubsu.fctam.entity.*;
import kubsu.fctam.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class IndexController {

    @Autowired
    UserService userService;

    @Autowired
    GameService gameService;

    @Autowired
    TableService tableService;

    @Autowired
    ChairService chairService;

    @Autowired
    CurrentStateService stateService;

    @Autowired
    CardService cardService;

    @Autowired
    ResultService resultService;

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
    @SubscribeMapping("/tableInvitation")
    @SendTo("/topic/invitation")
    public Chair testConn(HashMap map) {
        User user = userService.get(Integer.parseInt((String) map.get("user_id")));
        Table table = tableService.get(Integer.parseInt((String) map.get("table_id")));
        if (table != null) {
            Chair chair = new Chair(
                    user,
                    null,
                    -1,
                    "watcher",
                    0,
                    Integer.parseInt((String) map.get("userPot")),
                    null,
                    null);
            table.addChair(chair);
            chairService.save(chair);
            return chair;
        }
        else
            return null;
    }


    /**
     * метод удаляет стул юзера при выходе из игры
     * @param map - тут содержится table_id и user_id
     * @return - какой-то объект, иначе сокеты не работают, мда
     */
    @SubscribeMapping("/disconnect/out")
    @SendTo("/topic/disconnect/in")
    public List<Table> disconnect(HashMap map) {
        int tableId = Integer.parseInt((String) map.get("table_id"));
        int userId = Integer.parseInt((String) map.get("user_id"));
        Chair chair = chairService.getChair(tableId, userId);
        chairService.delete(chair);

        Table table = tableService.get(tableId);
        Game currentGame = tableService.getCurrentGame(table);

        if (currentGame != null) {
            if (table.getChairs().size() == 1) {
                Chair winChair = table.getChairs().get(0);
                User user = winChair.getUser();
                CurrentState currentState = stateService.getStateByGame(currentGame);
                Result result = new Result(user, currentGame, chair.getBet(), currentState.getPot());
                resultService.save(result);
            }
            if (table.getChairs().size() <= 1) {
                currentGame.setEndDtm(new Date());
                gameService.save(currentGame);
            }
        }
        return tableService.getAll(); // надо передавать какой-то объект, а то сокеты не хотят без объекта коннектиться
    }


    /**
     * метод для вывода списка стульев
     * @param map - тут находится table_id
     * @return - список стульев у данного стола
     */
    @SubscribeMapping("/chairs/out")
    @SendTo("/topic/chairs/in")
    public List<Chair> getTableChairs(HashMap map) {
        Table table = tableService.get(Integer.parseInt((String) map.get("table_id")));
        return table.getChairs();
    }


    /**
     * метод, который раздает карты на всех моментах игры (и в начале, и в конце)
     * @param map - то, что пришло от клиента
     *            используется пока что только table_id, в дальнейшем надо будет юзать и chair (для реализации ставок)
     * @return - isNewGame - булева переменная, показывает, что игра еще не началась
     *           currentGameState - текущее состояние игры (эклемпляр объекта CurrentState)
     *           chairs - список стульев около стола
     */
    @SubscribeMapping("/startgame/out")
    @SendTo("/topic/startgame/in")
    public HashMap<String, Object> startGame(HashMap map) {
        Table table = tableService.get(Integer.parseInt((String) map.get("table_id")));
        Game currentGame = tableService.getCurrentGame(table);
        HashMap<String, Object> outputMap = new HashMap<>();


        if (currentGame == null && table.chairsCount() > 1) {
            // Игроков хватает, но игры нет. Создадим ее!
            Game newGame = new Game(null, null, null);
            table.addGame(newGame);
            gameService.save(newGame);
            outputMap.put("isNewGame", false);

            CurrentState state = new CurrentState(null, 0, null, null, null, null, null);
            newGame.setState(state);
            stateService.save(state);
            outputMap.put("currentGameState", state);

            int[] randomCards = randomWithoutDuplicates(table.chairsCount()*2, null);
            int card_number = 0;
            for (Chair chair : table.getChairs()) {
                chair.setStatus("player");
                chair.setCard1(cardService.get(randomCards[card_number]));
                chair.setCard2(cardService.get(randomCards[card_number+1]));
                card_number += 2;
                chairService.save(chair);
            }
            outputMap.put("chairs", table.getChairs());

            return outputMap;
        }

        if (currentGame != null && table.chairsCount() == 1) {
            // Один игрок. Играет. Один?? Не, не пойдет. Завершаем игру.
            currentGame.setEndDtm(new Date());
            gameService.save(currentGame);
            currentGame = null;
        }

        if (currentGame == null && table.chairsCount() == 1) {
            // Один игрок. Игры, естественно, нет. Пускай ждет. Но ответ все равно получит.
            outputMap.put("isNewGame", true);
            outputMap.put("currentGameState", null);
            outputMap.put("chairs", table.getChairs());
            return outputMap;
        }

        // Иначе отдаем текущее состояние игры.
        outputMap.put("isNewGame", false);
        outputMap.put("currentGameState", stateService.getStateByGame(currentGame));
        outputMap.put("chairs", table.getChairs());
        return outputMap;

            // Блок перехода на следующий этап торгов, он должен быть не тут.
//            if (currentGame != null && table.chairsCount() > 1) {
//                CurrentState state = currentGame.getState();
//                HashSet<Integer> exclude = new HashSet<>();
//                for (Chair chair : table.getChairs()) {
//                    exclude.add(chair.getCard1().getId());
//                    exclude.add(chair.getCard2().getId());
//                }
//                if (amountOfTableCards(state) == 0) {
//                    int[] randomCards = randomWithoutDuplicates(3, exclude);
//                    state.setTableCard1(cardService.get(randomCards[0]));
//                    state.setTableCard2(cardService.get(randomCards[1]));
//                    state.setTableCard3(cardService.get(randomCards[2]));
//                }
//                if (amountOfTableCards(state) == 3) {
//                    exclude.add(state.getTableCard1().getId());
//                    exclude.add(state.getTableCard2().getId());
//                    exclude.add(state.getTableCard3().getId());
//                    int[] randomCards = randomWithoutDuplicates(1, exclude);
//                    state.setTableCard4(cardService.get(randomCards[0]));
//                }
//                if (amountOfTableCards(state) == 4) {
//                    exclude.add(state.getTableCard1().getId());
//                    exclude.add(state.getTableCard2().getId());
//                    exclude.add(state.getTableCard3().getId());
//                    exclude.add(state.getTableCard4().getId());
//                    int[] randomCards = randomWithoutDuplicates(1, exclude);
//                    state.setTableCard5(cardService.get(randomCards[0]));
//                }
//                stateService.save(state);
//                outputMap.put("currentGameState", state);
//                outputMap.put("chairs", table.getChairs());
//                outputMap.put("isNewGame", false);
//                return outputMap;
//            }
    }


    /**
     * метод, который возвращает массив рандомных неповторяющихся чисел на отрезке [3; 54]
     * @param arr_size - количество рандомных чисел в результирующем массиве
     * @param exclude - те числа, которые надо исключить из результирующего массива
     * @return - массив рандомных уникальных чисел
     */
    private int[] randomWithoutDuplicates(int arr_size, HashSet<Integer> exclude){
        int[] result = new int[arr_size];
        Random random = new Random();
        HashSet<Integer> used = new HashSet<Integer>();
        for (int i = 0; i < arr_size; i++) {
            int add = random.nextInt(52) + 3; //this is the int we are adding
            if (exclude == null) {
                while (used.contains(add)) { //while we have already used the number
                    add = random.nextInt(52) + 3; //generate a new one because it's already used
                }
            }
            else {
                while (used.contains(add) && exclude.contains(add)) { //while we have already used the number
                    add = random.nextInt(52) + 3; //generate a new one because it's already used
                }
            }
            used.add(add);
            result[i] = add;
        }
        return result;
    }

    /**
     * метод для того, чтобы выяснить, сколько карт на столе
     * @param state - состояние, у которого есть 5 полей для карт
     * @return количество инициализованный карт состояния
     */
    private int amountOfTableCards(CurrentState state) {
        int cardCount = 0;
        if (state.getTableCard1() != null)
            ++cardCount;
        if (state.getTableCard2() != null)
            ++cardCount;
        if (state.getTableCard3() != null)
            ++cardCount;
        if (state.getTableCard4() != null)
            ++cardCount;
        if (state.getTableCard5() != null)
            ++cardCount;
        return cardCount;
    }
}

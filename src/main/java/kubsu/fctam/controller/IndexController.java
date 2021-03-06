package kubsu.fctam.controller;

import kubsu.fctam.cardComparator.CombinationRecognizer;
import kubsu.fctam.cardComparator.HandRating;
import kubsu.fctam.entity.*;
import kubsu.fctam.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private HandRating handRating;

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
                    Integer.parseInt((String) map.get("userPot")),
                    new Date(),
                    null,
                    null,
                    false);
            table.addChair(chair);
            chairService.save(chair);
            return chair;
        }
        else
            return null;
    }


    /**
     * Обработка события выхода игрока из-за стола.
     * Ответ летит в поток обновления визуализации.
     * @param map - тут содержится table_id и user_id
     */
    @SubscribeMapping("/disconnect/out")
    @SendTo("/topic/startgame/in")
    public HashMap<String, Object> disconnect(HashMap map) {
        int tableId = Integer.parseInt((String) map.get("table_id"));
        int userId = Integer.parseInt((String) map.get("user_id"));
        // TODO тут ошибка с удалением стула. Там почему-то 2 запроса и оптимистичная-блокировка-эксепшн
        System.out.println(chairService.getChair(tableId, userId).getId());
        Chair chair = chairService.getChair(tableId, userId);
        chairService.delete(chair);

        Table table = tableService.get(tableId);
        Game currentGame = tableService.getCurrentGame(table);

        // Если он вышел в процессе торгов, пересмотерть состояние игры.
        if (currentGame != null) {
            // Если остался последний игрок, значит он срывает куш.
            if (table.getChairs().size() == 1) {
                Chair winChair = table.getChairs().get(0);
                User user = winChair.getUser();
                CurrentState currentState = currentGame.getState();
                Result result = new Result(user, currentGame, currentState.getPot());
                resultService.save(result);
            }
            // Если вышел предпоследний или последний игрок, игра заканчивается.
            if (table.getChairs().size() <= 1) {
                currentGame.setEndDtm(new Date());
                gameService.save(currentGame);
            }
        }
        HashMap<String, Object> outputMap = new HashMap<>();
        if (currentGame == null) {
            outputMap.put("isNewGame", true);
            outputMap.put("currentGameState", null);
        }
        else {
            outputMap.put("isNewGame", false);
            outputMap.put("currentGameState", currentGame.getState());
        }
        outputMap.put("chairs", table.getChairs());
        return outputMap;
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
     * метод, который создает новую игру и раздает карты игрокам
     * @param map - то, что пришло от клиента
     *            используется пока что только table_id
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

        // почему-то при запросе с двух клиентов прога проходит по этой ветке несколько раз
        // то есть, в первый раз прога не распознает, что игра уже создана
        if (currentGame == null && table.chairsCount() > 1) {
            // Игроков хватает, но игры нет. Создадим ее!
            Game newGame = new Game(null, null, null);
            table.addGame(newGame);
            gameService.save(newGame);
            outputMap.put("isNewGame", false);

            int[] randomCards = randomWithoutDuplicates(table.chairsCount()*2, null);
            int card_number = 0;
            for (Chair chair : table.getChairs()) {
                chair.setStatus("player");
                chair.setCard1(cardService.get(randomCards[card_number]));
                chair.setCard2(cardService.get(randomCards[card_number+1]));
                card_number += 2;
                chairService.save(chair);
            }

            CurrentState state = new CurrentState(null, 0, null, null, null, null, null);
            List<Chair> chairs = chairService.getAll(table.getId());
            chairs.get(0).setBet(table.getMinBet() / 2);
            chairService.save(chairs.get(0));
            state.setCurrentDealer(chairs.get(0).getUser());
            state.setCurrentTrader(chairs.get(1).getUser());
            newGame.setState(state);
            stateService.save(state);
            outputMap.put("currentGameState", state);
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
        outputMap.put("currentGameState", currentGame.getState());
        outputMap.put("chairs", table.getChairs());
        return outputMap;
    }


    /**
     * Обработка сообщения о действии пользователя в торгах.
     *
     * @param action - набор параметров действия (пользователь, стол, тип действия, величина ставки)
     */
    @MessageMapping("/game/action")
    @SendTo("/topic/startgame/in")
    public HashMap<String, Object> getAction(HashMap action) throws Exception {
        int table_id = Integer.parseInt(action.get("table_id").toString());
        int user_id = Integer.parseInt(action.get("user_id").toString());

        Table table = tableService.get(table_id);

        int value = Integer.parseInt(action.get("value").toString());
        String actionType = action.get("action").toString();
        Game currentGame = tableService.getCurrentGame(table);
        CurrentState currentState = currentGame.getState();
        List<Chair> chairs = table.getChairs();
        Chair currentChair = chairs.stream()
                                            .filter(chair -> chair.getUser().getId() == user_id)
                                            .findFirst().orElse(null);

        if (currentChair == null)
            return null;

        switch (actionType) {
            case "check":
                break;
            case "bet":
                currentChair.setBet(value);
                break;
            case "call":
                currentChair.setBet(currentChair.getBet() + value);
                break;
            case "raise":
                currentChair.setBet(currentChair.getBet() + value);
                break;
            case "fold":
                currentChair.setStatus("watcher");
                break;
            default:
                System.out.println("Haven't such action: " + actionType);
                return null;
        }

        currentChair.setIsChecked(true);
        chairService.save(currentChair);
        if (table.amountOfActivePlayers() < 2) {
            System.out.println("only one player here");
            return null;
        }

        boolean tradeEnd = true;
        int bet = chairs.get(0).getBet();
        for (Chair chair : chairs) {
            tradeEnd &= chair.getBet() == bet && chair.getIsChecked();
        }

        if (tradeEnd) {
            HashMap<String, Object> request = nextTermination(table);
            if (request != null) {
                for (Chair chair : chairs){
                    chair.setIsChecked(false);
                    chairService.save(chair);
                }
                return request;
            } else {
                return finishGame(table);
            }
        }

        currentState.setCurrentTrader(chairs.get(currentChair.getNumber() % chairs.size()).getUser());
        stateService.save(currentState);

        HashMap<String, Object> outputMap = new HashMap<>();
        outputMap.put("isNewGame", false);
        outputMap.put("currentGameState", currentState);
        outputMap.put("chairs", table.getChairs());
        return outputMap;
    }


    /**
     * метод, который заканчивает игру
     * @param table - объект стола, на котором надо закончить игру
     * @return - структуру данных для клиента (список стульев и текущее состояние игры)
     */
    public HashMap<String, Object> finishGame(Table table) throws Exception {
        Game currentGame = tableService.getCurrentGame(table);
        CurrentState currentState = currentGame.getState();
        currentGame.setEndDtm(new Date());
        gameService.save(currentGame);
        // TODO: Как-то находить победителя.
        handRating.setHandRating(table.getChairs(), currentState);
        List<Chair> winners = handRating.getWinner();
        for (Chair winner : winners) {
            winner.setUserPot(winner.getUserPot() + (currentState.getPot()/winners.size()));
            chairService.save(winner);
        }
        for (Chair chair : table.getChairs()) {
            Result result = new Result(chair.getUser(), currentGame, chair.getUserPot()-chair.getStartUserPot());
            resultService.save(result);
            chair.getUser().setMoney(chair.getUser().getMoney() + result.getGain());
            userService.save(chair.getUser());
        }

        HashMap<String, Object> tableIdMap = new HashMap<>();
        tableIdMap.put("table_id", Integer.toString(table.getId()));
        return startGame(tableIdMap);
    }


    /**
     * Произвести следующую раздачу. Вызывается при окончании очередных торгов.
     * @param  table - стол, на котором необходимо раздать карты.
     * @return структура, представляющая новое состояние игры. null, если игра закончена.
     */
    public HashMap<String, Object> nextTermination(Table table) {
        Game currentGame = tableService.getCurrentGame(table);
        HashSet<Integer> exclude = new HashSet<>();
        CurrentState state = currentGame.getState();

        for (Chair chair : table.getChairs()) {
            state.setPot(state.getPot() + chair.getBet()); // добавляем ставки игроков в общий банк
            chair.setBet(0); // обнуляем текущие ставки игроков
            chairService.save(chair);
            exclude.add(chair.getCard1().getId());
            exclude.add(chair.getCard2().getId());
        }

        int amount = amountOfTableCards(state);
        switch (amount) {
            case 0:
                int[] randomCards = randomWithoutDuplicates(3, exclude);
                state.setTableCard1(cardService.get(randomCards[0]));
                state.setTableCard2(cardService.get(randomCards[1]));
                state.setTableCard3(cardService.get(randomCards[2]));
                break;
            case 3:
                exclude.add(state.getTableCard1().getId());
                exclude.add(state.getTableCard2().getId());
                exclude.add(state.getTableCard3().getId());
                randomCards = randomWithoutDuplicates(1, exclude);
                state.setTableCard4(cardService.get(randomCards[0]));
                break;
            case 4:
                exclude.add(state.getTableCard1().getId());
                exclude.add(state.getTableCard2().getId());
                exclude.add(state.getTableCard3().getId());
                exclude.add(state.getTableCard4().getId());
                randomCards = randomWithoutDuplicates(1, exclude);
                state.setTableCard5(cardService.get(randomCards[0]));
                break;
            default:
                return null;
        }

        stateService.save(state);
        HashMap<String, Object> outputMap = new HashMap<>();
        outputMap.put("currentGameState", state);
        outputMap.put("chairs", table.getChairs());
        outputMap.put("isNewGame", false);
        return outputMap;
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
            int add = random.nextInt(52) + 3;
            if (exclude == null) {
                while (used.contains(add)) {
                    add = random.nextInt(52) + 3;
                }
            }
            else {
                while (used.contains(add) || exclude.contains(add)) {
                    add = random.nextInt(52) + 3;
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

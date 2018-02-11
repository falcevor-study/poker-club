var stompClient = null;
// window.addEventListener("beforeunload", disconnect);

function connect() {
    disable_buttons();
    var socket = new SockJS('/tables/games/socket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        // это запускается автоматически при открытии страницы с игрой пользователем
        stompClient.subscribe('/topic/chairs/in', function(message){
            var chairs = JSON.parse(message.body);
            placeUsers(chairs);
        });

        // это запускается в том случае, если стульев за столом больше 1
        stompClient.subscribe('/topic/startgame/in', function(message){
            var responce = JSON.parse(message.body);
            console.log(responce);
            var state = responce.currentGameState;
            if (responce.isNewGame == false) {
                if (state.tableCard1 == null && state.tableCard2 == null && state.tableCard3 == null && state.tableCard4 == null && state.tableCard5 == null) {
                    preflop(state, responce.chairs)
                }
                if (state.tableCard1 != null && state.tableCard2 != null && state.tableCard3 != null && state.tableCard4 == null && state.tableCard5 == null) {
                    flop(state, responce.chairs)
                }
                if (state.tableCard1 != null && state.tableCard2 != null && state.tableCard3 != null && state.tableCard4 != null && state.tableCard5 == null) {
                    tern(state, responce.chairs)
                }
                if (state.tableCard1 != null && state.tableCard2 != null && state.tableCard3 != null && state.tableCard4 != null && state.tableCard5 != null) {
                    river(state, responce.chairs)
                }
                if (state.currentTrader.id == parseInt($('#user_id').text())) {
                    console.log('ВАШ ХОД');
                    prepare_for_action(state, responce.chairs);
                }
            }
        });
    });
}


function disconnect() {
    var json = JSON.stringify(
        {
            'table_id': $('#table_id').text(),
            'user_id': $('#user_id').text()
        }
    );
    stompClient.send("/app/disconnect/out", {}, json);
}


function river(currentGameState, chairs) {
    console.log('river');
    update_view(currentGameState, chairs);
}


function tern(currentGameState, chairs) {
    console.log('tern');
    update_view(currentGameState, chairs);
}


function flop(currentGameState, chairs) {
    console.log('flop');
    update_view(currentGameState, chairs);
}


function preflop(currentGameState, chairs) {
    console.log('preflop');
    update_view(currentGameState, chairs);
}


function update_view(currentGameState, chairs) {
    update_cards(currentGameState, chairs);
    update_bets(currentGameState, chairs);
    update_user_pots(chairs);
}


/**
 * функция размечает юзеров
 * @param chairs - список стульев в формате json
 */
function placeUsers(chairs) {
    var chairCount = 1;
    var currentChair = null;
    for (var key in chairs){
        var h4_id = '#player'+chairCount+'_name';
        var h5_pot = '#player'+chairCount+'_pot';
        $(h4_id).text(chairs[key].user.login);
        $(h5_pot).text(chairs[key].userPot);
        chairCount += 1;
        if (parseInt($('#user_id').text()) === chairs[key].user.id) {
            currentChair = chairs[key]
        }
    }
    if (chairCount >= 2) {
        startGame(chairs, currentChair);
    }
}


function startGame(chairs, currentChair) {
    for (var key in chairs) {
        if (currentChair.connectionDate < chairs[key].connectionDate)
            return;
    }
    var json = JSON.stringify(
        {
            'chairs': chairs,
            'table_id': $('#table_id').text()
        }
    );
    stompClient.send("/app/startgame/out", {}, json);
}


/**
 * функция получает список стульев за текущим столом
 * таймаут нужен, чтобы соединение по сокету успело установиться
 */
function getChairs() {
    setTimeout( function(){
        var json = JSON.stringify({'table_id': $('#table_id').text()});
        stompClient.send("/app/chairs/out", {}, json);
    }  , 1000 );
}


/**
 * Обновить визуализацию карт.
 */
function update_cards(state, chairs) {
    if (state.tableCard1 != null) {
        $("#table_card_1").attr('class', 'card ' + state.tableCard1.suit + ' ' + state.tableCard1.value);
    } else {
        $("#table_card_1").attr('class', 'card none');
    }

    if (state.tableCard2 != null) {
        $("#table_card_2").attr('class', 'card ' + state.tableCard2.suit + ' ' + state.tableCard2.value);
    } else {
        $("#table_card_2").attr('class', 'card none');
    }

    if (state.tableCard3 != null) {
        $("#table_card_3").attr('class', 'card ' + state.tableCard3.suit + ' ' + state.tableCard3.value);
    } else {
        $("#table_card_3").attr('class', 'card none');
    }

    if (state.tableCard4 != null) {
        $("#table_card_4").attr('class', 'card ' + state.tableCard4.suit + ' ' + state.tableCard4.value);
    } else {
        $("#table_card_4").attr('class', 'card none');
    }

    if (state.tableCard5 != null) {
        $("#table_card_5").attr('class', 'card ' + state.tableCard5.suit + ' ' + state.tableCard5.value);
    } else {
        $("#table_card_5").attr('class', 'card none');
    }

    var chairCount = 1;
    for (var key in chairs){
        var new_card1_class = 'card';
        var new_card2_class = 'card';
        if (parseInt($('#user_id').text()) == chairs[key].user.id && chairs[key].status == "player") {
            new_card1_class = 'card ' + chairs[key].card1.suit +' '+chairs[key].card1.value;
            new_card2_class = 'card ' + chairs[key].card2.suit +' '+chairs[key].card2.value;
        }
        else {
            if (chairs[key].status == "player") {
                new_card1_class = 'card reverse';
                new_card2_class = 'card reverse';
            }
            else {
                new_card1_class = 'card none';
                new_card2_class = 'card none';
            }
        }
        var old_card1_class = '#player'+chairCount+'_card1';
        var old_card2_class = '#player'+chairCount+'_card2';
        $(old_card1_class).attr('class', new_card1_class);
        $(old_card2_class).attr('class', new_card2_class);
        chairCount += 1;
    }
}


function update_bets(state, chairs) {
    $("#table_pot").text(state.pot);

    var chairCount = 1;
    for (var key in chairs){
        var bet = null;
        if (chairs[key].status == "player") {
            bet = chairs[key].bet;
        } else {
            bet = '';
        }
        var bet_id = '#bet_'+chairCount;
        $(bet_id).text(bet);
        chairCount += 1;
    }
}


function update_user_pots(chairs) {
    var chairCount = 1;
    for (var key in chairs){
        var userPot = null;
        if (chairs[key].status == "player") {
            console.log(chairs[key].userPot);
            userPot = chairs[key].userPot;
        } else {
            userPot = '';
        }
        var userPot_id = '#player'+chairCount+"_pot";
        $(userPot_id).text(userPot);
        chairCount += 1;
    }
}


/**
 * Отправить на сервер действие пользователя на торгах.
 * Данные вытягиваются из формы и из hidden-полей.
 */
function sendAction(action) {
    var json = JSON.stringify(
        {
            'table_id': $('#table_id').text(),
            'user_id': $('#user_id').text(),
            'action': action,
            'value': document.getElementById('value').value
        }
    );
    stompClient.send("/app/game/action", {}, json);
    disable_buttons();
}


/**
 * Подготовить интерфейс пользователя к торгам.
 *
 * @param state - структура текущего состояния.
 * @param chairs - массив структур стульев.
 */
function prepare_for_action(state, chairs) {
    var maxBet = 0;
    var userBet = 0;
    var userPot = 0;
    var minUserBet = 0;
    var chairCount = 1;
    for (var key in chairs){
        if (chairs[key].bet > maxBet) { maxBet = chairs[key].bet; }

        if (parseInt($('#user_id').text()) == chairs[key].user.id) {
            userBet = chairs[key].bet;
            userPot = chairs[key].userPot;
        }

        chairCount += 1;
    }

    if (userBet == maxBet) {
        minUserBet = state.game.table.minBet;
    } else {
        minUserBet = maxBet - userBet;
    }

    if (minUserBet > userPot) {
        minUserBet = userPot;
    }

    document.getElementById('value').value = minUserBet;
    $('#value').attr('min', minUserBet);
    $('#value').attr('map', userPot);

    document.getElementById("fold").disabled = false;

    if (userBet == maxBet) {
        document.getElementById("check").disabled = false;
        document.getElementById("bet").disabled = false;
    } else {
        document.getElementById("call").disabled = false;
        document.getElementById("raise").disabled = false;
    }
}


/**
 * Выключить все кнопки интерфейса, сбросить величину ставки.
 */
function disable_buttons() {
    document.getElementById("check").disabled = true;
    document.getElementById("call").disabled = true;
    document.getElementById("bet").disabled = true;
    document.getElementById("raise").disabled = true;
    document.getElementById("fold").disabled = true;
    document.getElementById('value').value = "0";
    $('#value').attr('min', 0);
}

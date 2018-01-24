package kubsu.fctam.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity(name = "tTable")
public class Table implements Comparable<Table> {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private int minBet;

    @Column(length = 50)
    private String name;

    @OneToMany(mappedBy = "table")
    @LazyCollection(LazyCollectionOption.FALSE) // чтобы стулья загружались сразу
    @OrderBy("number ASC")
    @JsonBackReference // нужно, чтобы не было StackOverFlowException при добавлении стула
    private List<Chair> chairs;

    @OneToMany(mappedBy = "table")
    @LazyCollection(LazyCollectionOption.FALSE) // чтобы игры загружались сразу
    @JsonBackReference // нужно, чтобы не было StackOverFlowException при добавлении игры
    private List<Game> games;


    public Table() {}

    public Table(String name, int minBet) {
        this.minBet = minBet;
        this.name = name;
    }


    public void setMinBet(int minBet) { this.minBet = minBet; }
    public void setName(String name) { this.name = name; }
    public void setChairs(List<Chair> chairs) { this.chairs = chairs; }
    public void setGames(List<Game> games) { this.games = games; }

    public int getId() { return id; }
    public int getMinBet() { return minBet; }
    public String getName() { return name; }
    public List<Chair> getChairs() { return chairs; }
    public List<Game> getGames() { return games; }

    /**
     * Добавить участника к столу. Если мест нет, то ничего не делаем.
     *
     * @param chair - стул, который нужно пристроить у стола.
     */
    public void addChair(Chair chair) {
        // Если все места за столом заняты, новый стул поставить некуда.
        if (chairs.size() == 5) { return; }

        if (chairs.size() == 0) {
            chair.setNumber(1); // если никого нет за столом, то садимся первыми
        } else {
            chair.setNumber(chairs.size() + 1); // если есть, то садимся на свободное место
        }
        chair.setTable(this);
        this.chairs.add(chair);
    }


    public void addGame(Game game) {
        game.setTable(this);
        this.games.add(game);
    }


    public int chairsCount() {
        return chairs.size();
    }


    public int amountOfActivePlayers() {
        int amountOfActivePlayers = 0;
        for (Chair chair : chairs)
            if (chair.getStatus().equals("player"))
                ++amountOfActivePlayers;
        return amountOfActivePlayers;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Table
                && ((Table)another).getId() == this.id;
    }

    @Override
    public int compareTo(Table that) {
        return Integer.compare(this.minBet, that.minBet);
    }
}

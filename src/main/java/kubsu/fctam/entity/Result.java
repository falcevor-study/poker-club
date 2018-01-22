package kubsu.fctam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Result implements Comparable<Result>{
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "gameId")
    private Game game;

    @Column
    private int bet;

    @Column
    private int gain;


    public Result() {}

    public Result(User user, Game game, int bet, int gain) {
        this.user = user;
        this.game = game;
        this.bet = bet;
        this.gain = gain;
    }


    public void setUser(User user) { this.user = user; }
    public void setGame(Game game) { this.game = game; }
    public void setBet(int bet) { this.bet = bet; }
    public void setGain(int gain) { this.gain = gain; }

    public int getId() { return id; }
    public User getUser() { return user; }
    public Game getGame() { return game; }
    public int getBet() { return bet; }
    public int getGain() { return gain; }

    @Override
    public boolean equals(Object another) {
        return another instanceof  Result
                && ((Result)another).getId() == this.id;
    }

    @Override
    public int compareTo(Result another) { return Integer.compare(this.gain, another.gain); }
}

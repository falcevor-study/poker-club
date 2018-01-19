package kubsu.fctam.entity;

import javax.persistence.*;

@Entity(name = "tRound")
public class Round {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "gameId")
    private Game game;

    public void setId(int id) { this.id = id; }
    public void setGame(Game game) { this.game = game; }

    public int getId() { return id; }
    public Game getGame() { return game; }

    @Override
    public boolean equals(Object another) {
        return another instanceof Round
                && ((Round)another).getId() == this.id;
    }
}

package kubsu.fctam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "tRound")
public class Round {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private int gameId;

    public void setId(int id) { this.id = id; }
    public void setGameId(int gameId) { this.gameId = gameId; }

    public int getId() { return id; }
    public int getGameId() { return gameId; }

    @Override
    public boolean equals(Object another) {
        return another instanceof Round
                && ((Round)another).getId() == this.id
                && ((Round)another).getGameId() == this.gameId;
    }
}

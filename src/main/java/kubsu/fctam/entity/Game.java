package kubsu.fctam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "tGame")
public class Game {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private int minBet;

    public void setId(int id) { this.id = id; }
    public void setMinBet(int minBet) { this.minBet = minBet; }

    public int getId() { return id; }
    public int getMinBet() { return minBet; }

    @Override
    public boolean equals(Object another) {
        return another instanceof Game
                && ((Game)another).getId() == this.id;
    }
}

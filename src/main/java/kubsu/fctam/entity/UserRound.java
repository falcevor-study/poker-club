package kubsu.fctam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "tUserRound")
public class UserRound {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private int userId;

    @Column
    private int roundId;

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setRoundId(int roundId) { this.roundId = roundId; }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getRoundId() { return roundId; }

    @Override
    public boolean equals(Object another) {
        return another instanceof UserRound
                && ((UserRound)another).getId() == this.id;
    }
}

package kubsu.fctam.entity;

import javax.persistence.*;

@Entity(name = "tUserRound")
public class UserRound {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "roundId")
    private Round round;

    public void setId(int id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setRound(Round round) { this.round = round; }

    public int getId() { return id; }
    public User getUser() { return user; }
    public Round getRound() { return round; }

    @Override
    public boolean equals(Object another) {
        return another instanceof UserRound
                && ((UserRound)another).getId() == this.id;
    }
}

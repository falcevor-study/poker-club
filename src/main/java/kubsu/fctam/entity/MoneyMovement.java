package kubsu.fctam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "tMoneyMovement")
public class MoneyMovement implements Comparable<MoneyMovement> {
    @Id
    @GeneratedValue
    private int id;

    @Column(length = 200)
    private String description;

    @Column
    private int value;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;


    public MoneyMovement() {}

    public MoneyMovement(User user, int value, String description) {
        this.user = user;
        this.value = value;
        this.description = description;
    }


    public void setDescription(String description) { this.description = description; }
    public void setValue(int value) { this.value = value; }
    public void setUser(User user) { this.user = user; }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public int getValue() { return value; }
    public User getUser() { return user; }

    @Override
    public boolean equals(Object another) {
        return another instanceof MoneyMovement
                && ((MoneyMovement)another).getId() == this.id;
    }

    @Override
    public int compareTo(MoneyMovement another) {
        return Integer.compare(this.value, another.getValue());
    }
}

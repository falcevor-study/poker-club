package kubsu.fctam.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "tChair")
public class Chair implements Comparable<Chair> {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tableId")
    @JsonManagedReference // нужно, чтобы не было StackOverFlowException при добавлении стула к столу
    private Table table;

    @Column
    private int number;

    @Column(length = 50)
    private String status;

    @Column
    private long bet;

    @Column
    private long userPot;

    @ManyToOne
    @JoinColumn(name = "card1Id")
    private Card card1;

    @ManyToOne
    @JoinColumn(name = "card2Id")
    private Card card2;


    public Chair() {}

    public Chair(User user, Table table, int number, String status, long bet, long userPot, Card card1, Card card2) {
        this.user = user;
        this.table = table;
        this.number = number;
        this.status = status;
        this.bet = bet;
        this.userPot = userPot;
        this.card1 = card1;
        this.card2 = card2;
    }

    public void setUser(User user) { this.user = user; }
    public void setTable(Table table) { this.table = table; }
    public void setNumber(int number) { this.number = number; }
    public void setStatus(String status) { this.status = status; }
    public void setBet(long bet) { this.bet = bet; }
    public void setUserPot(long userPot) { this.userPot = userPot; }
    public void setCard1(Card card1) { this.card1 = card1; }
    public void setCard2(Card card2) { this.card2 = card2; }

    public int getId() { return id; }
    public User getUser() { return user; }
    public Table getTable() { return table; }
    public int getNumber() { return number; }
    public String getStatus() { return status; }
    public long getBet() { return bet; }
    public long getUserPot() { return userPot; }
    public Card getCard1() { return card1; }
    public Card getCard2() { return card2; }

    @Override
    public boolean equals(Object another) {
        return another instanceof Chair
                && ((Chair)another).getId() == this.id;
    }

    @Override
    public int compareTo(Chair another) {
        return Integer.compare(this.number, another.getNumber());
    }
}

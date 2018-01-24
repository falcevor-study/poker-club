package kubsu.fctam.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

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
    private int bet;

    @Column
    private int userPot;

    @ManyToOne
    @JoinColumn(name = "card1Id")
    private Card card1;

    @ManyToOne
    @JoinColumn(name = "card2Id")
    private Card card2;

    @Column
    private Date connectionDate;

    @Column
    private Boolean isChecked;



    public Chair() {}

    public Chair(User user, Table table, int number, String status, int bet, int userPot, Date connectionDate, Card card1, Card card2, Boolean isChecked) {
        this.user = user;
        this.table = table;
        this.number = number;
        this.status = status;
        this.bet = bet;
        this.userPot = userPot;
        this.connectionDate = connectionDate;
        this.card1 = card1;
        this.card2 = card2;
        this.isChecked = isChecked;
    }

    public void setUser(User user) { this.user = user; }
    public void setTable(Table table) { this.table = table; }
    public void setNumber(int number) { this.number = number; }
    public void setStatus(String status) { this.status = status; }
    public void setBet(int bet) { this.bet = bet; }
    public void setUserPot(int userPot) { this.userPot = userPot; }
    public void setCard1(Card card1) { this.card1 = card1; }
    public void setCard2(Card card2) { this.card2 = card2; }
    public void setConnectionDate(Date connectionDate) { this.connectionDate = connectionDate; }
    public void setIsChecked(Boolean isChecked) { this.isChecked = isChecked; }

    public int getId() { return id; }
    public User getUser() { return user; }
    public Table getTable() { return table; }
    public int getNumber() { return number; }
    public String getStatus() { return status; }
    public int getBet() { return bet; }
    public int getUserPot() { return userPot; }
    public Card getCard1() { return card1; }
    public Card getCard2() { return card2; }
    public Date getConnectionDate() { return connectionDate; }
    public Boolean getIsChecked() { return isChecked; }

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

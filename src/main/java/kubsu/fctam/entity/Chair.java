package kubsu.fctam.entity;

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
    private Table table;

    @Column
    private int number;

    @Column(length = 50)
    private String status;


    public Chair() {}

    public Chair(User user, Table table, int number, String status) {
        this.user = user;
        this.table = table;
        this.number = number;
        this.status = status;
    }


    public void setUser(User user) { this.user = user; }
    public void setTable(Table table) { this.table = table; }
    public void setNumber(int number) { this.number = number; }
    public void setStatus(String status) { this.status = status; }

    public int getId() { return id; }
    public User getUser() { return user; }
    public Table getTable() { return table; }
    public int getNumber() { return number; }
    public String getStatus() { return status; }

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

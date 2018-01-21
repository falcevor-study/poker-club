package kubsu.fctam.entity;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "tGame")
public class Game {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "tableId")
    private Table table;

    @Column
    private Date endDtm;

    @OneToOne(mappedBy = "game")
    private CurrentState state;


    public Game() {}

    public Game(Table table, Date endDtm) {
        this.table = table;
        this.endDtm = endDtm;
    }


    public void setTable(Table table) { this.table = table; }
    public void setEndDtm(Date endDtm) { this.endDtm = endDtm; }
    public void setState(CurrentState state) { this.state = state; }

    public int getId() { return id; }
    public Table getTable() { return table; }
    public Date getEndDtm() { return endDtm; }
    public CurrentState getState() { return state; }


    @Override
    public boolean equals(Object another) {
        return another instanceof Game
                && ((Game)another).getId() == this.id;
    }
}

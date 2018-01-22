package kubsu.fctam.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "tGame")
public class Game {
    @Id
    @GeneratedValue
    private int id;

//    @ManyToOne
//    @JoinColumn(name = "tableId")
//    private Table table;

    @ManyToOne
    @JoinColumn(name = "tableId")
    @JsonManagedReference // нужно, чтобы не было StackOverFlowException при добавлении стула к столу
    private Table table;

    @Column
    private Date endDtm;

    @OneToOne(mappedBy = "game")
    @JsonBackReference // нужно, чтобы не было StackOverFlowException при добавлении игры
    private CurrentState state;


    public Game() {}

    public Game(Table table, CurrentState state, Date endDtm) {
        this.table = table;
        this.state = state;
        this.endDtm = endDtm;
    }


    public void setTable(Table table) { this.table = table; }
    public void setEndDtm(Date endDtm) { this.endDtm = endDtm; }
    public void setState(CurrentState state) {
        state.setGame(this);
        this.state = state;
    }

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

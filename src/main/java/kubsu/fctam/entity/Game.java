package kubsu.fctam.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "tGame")
public class Game {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "tableId")
    private Table table;


    public Game() {}

    public Game(Table table) {
        this.table = table;
    }


    public void setTable(Table table) { this.table = table; }

    public int getId() { return id; }
    public Table getTable() { return table; }

    @Override
    public boolean equals(Object another) {
        return another instanceof Game
                && ((Game)another).getId() == this.id;
    }
}

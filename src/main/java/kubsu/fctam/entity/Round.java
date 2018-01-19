package kubsu.fctam.entity;

import javax.persistence.*;

@Entity(name = "tRound")
public class Round {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "gameId")
    private Table table;

    public void setId(int id) { this.id = id; }
    public void setTable(Table table) { this.table = table; }

    public int getId() { return id; }
    public Table getTable() { return table; }

    @Override
    public boolean equals(Object another) {
        return another instanceof Round
                && ((Round)another).getId() == this.id;
    }
}

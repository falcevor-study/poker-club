package kubsu.fctam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "tGame")

public class Table implements Comparable<Table> { // интерфейс Comparable был унаследован, чтобы
    // объекты можно было сортировать в методе getAll класса GameService
    @Id
    @GeneratedValue
    private int id;

    @Column
    private int minBet;

    @Column
    private String name;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMinBet() { return minBet; }
    public void setMinBet(int minBet) { this.minBet = minBet; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Table(String name, int minBet) {
        this.minBet = minBet;
        this.name = name;
    }

    public Table() {
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Table
                && ((Table)another).getId() == this.id;
    }

    @Override
    public int compareTo(Table that) {
        return Integer.compare(this.minBet, that.minBet);
    }
}

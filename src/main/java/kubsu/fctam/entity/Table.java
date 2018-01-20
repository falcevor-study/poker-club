package kubsu.fctam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.List;

@Entity(name = "tTable")
public class Table implements Comparable<Table> {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private int minBet;

    @Column(length = 50)
    private String name;

    @OneToMany(mappedBy = "table")
    @OrderBy("number ASC")
    private List<Chair> chairs;


    public Table() {}

    public Table(String name, int minBet) {
        this.minBet = minBet;
        this.name = name;
    }


    public void setMinBet(int minBet) { this.minBet = minBet; }
    public void setName(String name) { this.name = name; }
    public void setChairs(List<Chair> chairs) { this.chairs = chairs; }

    public int getId() { return id; }
    public int getMinBet() { return minBet; }
    public String getName() { return name; }
    public List<Chair> getChairs() { return chairs; }

    /**
     * Добавить участника к столу. Если мест нет, то ничего не делаем.
     *
     * @param chair - стул, который нужно пристроить у стола.
     */
    public void addChair(Chair chair) {
        // Если все места за столом заняты, новый стул поставить некуда.
        if (chairs.size() == 5) { return; }

        // Идем по кругу и садимся за первое попавшееся свободное место.
        int free;
        for (free = 0; free < 5; ++free) {
            if (chairs.get(free).getNumber() != free) { break; }
        }
        chair.setNumber(free);
        chair.setTable(this);
        this.chairs.add(chair);
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

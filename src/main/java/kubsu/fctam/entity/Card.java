package kubsu.fctam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "tCard")
public class Card implements Comparable<Card>{
    @Id
    @GeneratedValue
    private int id;

    @Column
    private String suit;

    @Column
    private String value;

    @Column
    private int rate;

    public Card() {}

    public Card(String suit, String value, int rate) {
        this.suit = suit;
        this.value = value;
        this.rate = rate;
    }

    public int getId() { return id; }
    public String getSuit() { return suit; }
    public String getValue() { return value; }
    public int getRate() { return rate; }

    public void setSuit(String suit) { this.suit = suit; }
    public void setValue(String value) { this.value = value; }
    public void setRate(int rate) { this.rate = rate; }

    @Override
    public int compareTo(Card that) {
        if (this.rate > that.getRate())
            return 1;
        else if (this.rate < that.getRate())
            return -1;
        else
            return Integer.compare(this.id, that.getId());
    }
}

package kubsu.fctam.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity(name = "tCurrentState")
public class CurrentState {
    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumn(name = "gameId")
    @JsonManagedReference // нужно, чтобы не было StackOverFlowException
    private Game game;

    @Column
    private long pot;

    @ManyToOne
    @JoinColumn(name = "tableCard1Id")
    private Card tableCard1;

    @ManyToOne
    @JoinColumn(name = "tableCard2Id")
    private Card tableCard2;

    @ManyToOne
    @JoinColumn(name = "tableCard3Id")
    private Card tableCard3;

    @ManyToOne
    @JoinColumn(name = "tableCard4Id")
    private Card tableCard4;

    @ManyToOne
    @JoinColumn(name = "tableCard5Id")
    private Card tableCard5;


    public CurrentState() {}

    public CurrentState(Game game, long pot, Card tableCard1, Card tableCard2, Card tableCard3, Card tableCard4, Card tableCard5) {
        this.game = game;
        this.pot = pot;
        this.tableCard1 = tableCard1;
        this.tableCard2 = tableCard2;
        this.tableCard3 = tableCard3;
        this.tableCard4 = tableCard4;
        this.tableCard5 = tableCard5;
    }

    public int getId() { return id; }
    public Game getGame() { return game; }
    public long getPot() { return pot; }
    public Card getTableCard1() { return tableCard1; }
    public Card getTableCard2() { return tableCard2; }
    public Card getTableCard3() { return tableCard3; }
    public Card getTableCard4() { return tableCard4; }
    public Card getTableCard5() { return tableCard5; }

    public void setGame(Game game) { this.game = game; }
    public void setPot(long pot) { this.pot = pot; }
    public void setTableCard1(Card tableCard1) { this.tableCard1 = tableCard1; }
    public void setTableCard2(Card tableCard2) { this.tableCard2 = tableCard2; }
    public void setTableCard3(Card tableCard3) { this.tableCard3 = tableCard3; }
    public void setTableCard4(Card tableCard4) { this.tableCard4 = tableCard4; }
    public void setTableCard5(Card tableCard5) { this.tableCard5 = tableCard5; }
}

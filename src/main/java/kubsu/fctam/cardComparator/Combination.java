package kubsu.fctam.cardComparator;

import kubsu.fctam.entity.Card;

import java.util.ArrayList;
import java.util.List;

public class Combination implements Comparable<Combination>{
    private List<Card> cards;
    private Card highCard;
    private Card highCardInCombination;
    private int value;

    public Combination() {
        this.cards = new ArrayList<>();
        this.highCard = null;
        this.highCardInCombination = null;
        this.value = 0;
    }

    public Combination(List<Card> cards, Card highCardInCombination, Card highCard, int value) {
        this.cards = cards;
        this.highCard = highCard;
        this.highCardInCombination = highCardInCombination;
        this.value = value;
    }

    public void setCards(List<Card> cards) { this.cards = cards; }
    public void setHighCard(Card highCard) { this.highCard = highCard; }
    public void setHighCardInCombination(Card highCardInCombination) { this.highCardInCombination = highCardInCombination; }
    public void setValue(int value) { this.value = value; }

    public List<Card> getCards() { return cards; }
    public Card getHighCard() { return highCard; }
    public Card getHighCardInCombination() { return highCardInCombination; }
    public int getValue() { return value; }

    public void addCard(Card card) {
        cards.add(card);
    }

    @Override
    public int compareTo(Combination that) {
        return Integer.compare(this.highCardInCombination.getRate(), that.highCardInCombination.getRate());
    }

    @Override
    public String toString() {
        String retStr = "cards: ";
        for (Card card : this.cards)
            retStr += card.getId() + " ";
        return retStr + "highCard: " + this.highCard.getId() + " highCardInCombination: " + this.highCardInCombination.getId() + " value: " + this.value;
    }
}

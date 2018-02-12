package kubsu.fctam.cardComparator;

import kubsu.fctam.entity.Card;
import kubsu.fctam.entity.Chair;
import kubsu.fctam.entity.CurrentState;
import kubsu.fctam.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class CombinationRecognizer {

    @Autowired
    private CardService service;

    private TreeMap<Card, Boolean> cards; // TreeMap - для того, чтобы массив был отсортирован по ключам

    public void setCards(Chair chair, CurrentState state) throws Exception{
        this.cards = new TreeMap<>();
        this.cards.put(chair.getCard1(), false);
        this.cards.put(chair.getCard2(), false);
        this.cards.put(state.getTableCard1(), false);
        this.cards.put(state.getTableCard2(), false);
        this.cards.put(state.getTableCard3(), false);
        this.cards.put(state.getTableCard4(), false);
        this.cards.put(state.getTableCard5(), false);
        if (this.cards.size() != 7)
            throw new Exception("incorrect size of cards treemap");
    }

    public void setCards(List<Card> cards) throws Exception {
        this.cards = new TreeMap<>();
        this.cards.put(cards.get(0), false);
        this.cards.put(cards.get(1), false);
        this.cards.put(cards.get(2), false);
        this.cards.put(cards.get(3), false);
        this.cards.put(cards.get(4), false);
        this.cards.put(cards.get(5), false);
        this.cards.put(cards.get(6), false);
        if (this.cards.size() != 7)
            throw new Exception("incorrect size of cards treemap");
    }


    public Combination getCombination(){
        Combination combination = findRoyalFlush();
        if (combination != null) return combination;

        combination = findStraightFlush();
        if (combination != null) return combination;

        combination = findQuads();
        if (combination != null) return combination;

        combination = findFullHouse();
        if (combination != null) return combination;

        combination = findFlush();
        if (combination != null) return combination;

        combination = findStraight();
        if (combination != null) return combination;

        combination = findSet();
        if (combination != null) return combination;

        combination = findTwoPairs();
        if (combination != null) return combination;

        combination = findOnePair();
        if (combination != null) return combination;

        return findHighCard();
    }

    private Combination findRoyalFlush(){
        ArrayList<List<Card>> lists = new ArrayList<>();
        lists.add(Arrays.asList(service.get(50), service.get(51), service.get(52), service.get(53), service.get(54)));
        lists.add(Arrays.asList(service.get(37), service.get(38), service.get(39), service.get(40), service.get(41)));
        lists.add(Arrays.asList(service.get(24), service.get(25), service.get(26), service.get(27), service.get(28)));
        lists.add(Arrays.asList(service.get(11), service.get(12), service.get(13), service.get(14), service.get(15)));
        for (List<Card> combination : lists)
            if (this.cards.keySet().containsAll(combination)) {
                for (Card card : combination)
                    this.cards.put(card, true);
                return new Combination(combination, combination.get(4), combination.get(4), 10);
            }
        return null;
    }

    private Combination findStraightFlush(){
        List<String> suits = Arrays.asList("club", "diamond", "heart", "spade");
        List<Card> cards = new ArrayList<>();
        for (String suit : suits)
            if (this.cards.keySet().stream().filter(card -> suit.equals(card.getSuit())).count() >= 5)
                cards = this.cards.keySet().stream()
                        .filter(card -> suit.equals(card.getSuit())).collect(Collectors.toList());
        if (cards.size() == 0)
            return null;
        Combination straightFlush = getBiggestStraight(cards);
        if (straightFlush != null){
            straightFlush.setValue(9);
        }
        return straightFlush;
    }

    private Combination findQuads(){
        for (int i = 13; i>0; --i) {
            int finalI = i;
            List<Card> quads = this.cards.keySet().stream()
                                                    .filter(card -> card.getRate() == finalI)
                                                    .collect(Collectors.toList());
            if (quads.size() == 4){
                for (Card card : quads)
                    this.cards.put(card, true);
                return new Combination(quads, quads.get(0), this.cards.lastKey(), 8);
            }
        }
        return null;
    }

    private Combination findFullHouse(){
        Combination set = findSet();
        if (set == null)
            return null;
        Combination pair  = findOnePair();
        if (pair != null){
            Card maxCard;
            if (set.getCards().get(0).getRate() > pair.getCards().get(0).getRate())
                maxCard = set.getCards().get(0);
            else
                maxCard = pair.getCards().get(0);
            return new Combination(Stream
                                           .concat(set.getCards().stream(), pair.getCards().stream())
                                           .collect(Collectors.toList()),
                                   maxCard,
                                   this.cards.lastKey(),
                                   7);
        } else {
            this.cards.put(set.getCards().get(0), false);
            this.cards.put(set.getCards().get(1), false);
            this.cards.put(set.getCards().get(2), false);
        }
        return null;
    }

    private Combination findFlush(){
        List<String> suits = Arrays.asList("club", "diamond", "heart", "spade");
        for (String suit : suits) {
            long count = this.cards.keySet().stream().filter(card -> card.getSuit().equals(suit)).count();
            if (count >= 5) {
                TreeSet<Card> combination = this.cards.keySet().stream()
                                                                .filter(card -> card.getSuit().equals(suit))
                                                                .sorted(Comparator.reverseOrder())
                                                                .limit(5)
                                                                .collect(Collectors.toCollection(TreeSet::new));
                for (Card card : combination)
                    this.cards.put(card, true);
                return new Combination(new ArrayList<>(combination), combination.last(), this.cards.lastKey(), 6);
            }
        }
        return null;
    }

    private Combination findStraight(){
        return getBiggestStraight(new ArrayList<>(this.cards.keySet()));
    }

    private Combination findSet(){
        List<Card> cards = new ArrayList<>(this.cards.keySet());
        Combination maxSet = null;
        for (int i = 0; i<cards.size()-2; ++i) {
            if (cards.get(i).getRate() == cards.get(i + 1).getRate() &&
                cards.get(i + 1).getRate() == cards.get(i + 2).getRate() &&
                !this.cards.get(cards.get(i)) &&
                !this.cards.get(cards.get(i + 1)) &&
                !this.cards.get(cards.get(i + 2))) {

                if (maxSet == null || maxSet.getHighCardInCombination().getRate() < cards.get(i).getRate())
                    maxSet = new Combination(Arrays.asList(cards.get(i),
                                                           cards.get(i + 1),
                                                           cards.get(i + 2)),
                                             cards.get(i),
                                             this.cards.lastKey(),
                                             4);
            }
        }
        if (maxSet != null) {
            for (Card card : maxSet.getCards())
                this.cards.put(card, true);
        }
        return maxSet;
    }

    private Combination findTwoPairs(){
        Combination firstPair = findOnePair();
        if (firstPair == null)
            return null;
        Combination secondPair = findOnePair();
        if (secondPair != null) {
            Card maxCard;
            if (firstPair.getCards().get(0).getRate() > secondPair.getCards().get(0).getRate())
                maxCard = firstPair.getCards().get(0);
            else
                maxCard = secondPair.getCards().get(0);
            return new Combination(Stream
                                          .concat(firstPair.getCards().stream(), secondPair.getCards().stream())
                                          .collect(Collectors.toList()),
                                   maxCard,
                                   this.cards.lastKey(),
                                   3);
        } else {
            this.cards.put(firstPair.getCards().get(0), false);
            this.cards.put(firstPair.getCards().get(1), false);
        }
        return null;
    }

    private Combination findOnePair(){
        List<Card> cards = new ArrayList<>(this.cards.keySet());
        Combination maxPair = null;
        for (int i = 0; i<cards.size()-1; ++i) {
            if (cards.get(i).getRate() == cards.get(i + 1).getRate() &&
                !this.cards.get(cards.get(i)) &&
                !this.cards.get(cards.get(i + 1))) {

                if (maxPair == null || maxPair.getHighCardInCombination().getRate() < cards.get(i).getRate())
                    maxPair = new Combination(Arrays.asList(cards.get(i), cards.get(i + 1)),
                                              cards.get(i),
                                              this.cards.lastKey(),
                                              2);
            }
        }
        if (maxPair != null){
            this.cards.put(maxPair.getCards().get(0), true);
            this.cards.put(maxPair.getCards().get(1), true);
        }
        return maxPair;
    }

    private Combination findHighCard(){
        this.cards.put(this.cards.lastKey(), true);
        return new Combination(Arrays.asList(this.cards.lastKey()), this.cards.lastKey(), this.cards.lastKey(), 1);
    }


    /**
     * метод нужен, чтобы 2 раза не писать одно и то же в методах findStraight() и findStraightFlush()
     * @param cards - массив карт, среди которых будет искаться максимальный стрит
     * @return
     */
    private Combination getBiggestStraight(List<Card> cards) {
        Combination straight = null;
        for (int i = 0; i<cards.size()-4; ++i) {
            if (cards.get(i).getRate() + 1 == cards.get(i + 1).getRate() &&
                cards.get(i + 1).getRate() + 1 == cards.get(i + 2).getRate() &&
                cards.get(i + 2).getRate() + 1 == cards.get(i + 3).getRate() &&
                cards.get(i + 3).getRate() + 1 == cards.get(i + 4).getRate()) {

                if (straight == null || straight.getHighCardInCombination().getRate() < cards.get(i+4).getRate())
                    straight = new Combination(Arrays.asList(cards.get(i),
                                                             cards.get(i + 1),
                                                             cards.get(i + 2),
                                                             cards.get(i + 3),
                                                             cards.get(i + 4)),
                                               cards.get(i+4),
                                               this.cards.lastKey(),
                                               5);
            }
        }
        for (int i = 0; i<cards.size()-3; ++i) {
            if (this.cards.lastKey().getRate() == 13 &&
                cards.get(i).getRate() == 1 &&
                cards.get(i + 1).getRate() == 2 &&
                cards.get(i + 2).getRate() == 3 &&
                cards.get(i + 3).getRate() == 4) {

                if (straight == null || straight.getHighCardInCombination().getRate() < cards.get(i+4).getRate())
                    straight = new Combination(Arrays.asList(cards.get(i),
                                                             cards.get(i + 1),
                                                             cards.get(i + 2),
                                                             cards.get(i + 3),
                                                             this.cards.lastKey()),
                                               cards.get(i + 3),
                                               this.cards.lastKey(),
                                               5);
                }
        }
        if (straight != null) {
            for (Card card : straight.getCards())
                this.cards.put(card, true);
        }
        return straight;
    }
}

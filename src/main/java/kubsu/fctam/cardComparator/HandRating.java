package kubsu.fctam.cardComparator;

import kubsu.fctam.entity.Chair;
import kubsu.fctam.entity.CurrentState;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HandRating {

    @Autowired
    private CombinationRecognizer recognizer;

    private List<Chair> chairs;

    private CurrentState state;

    public void setHandRating(List<Chair> chairs, CurrentState state) {
        this.chairs = chairs;
        this.state = state;
    }

    public List<Chair> getWinner() throws Exception {
        HashMap<Combination, Chair> chairs = new HashMap<>();
        Integer maxCombValue = 0;
        for (Chair chair : this.chairs) {
            this.recognizer.setCards(chair, this.state);
            Combination combination = this.recognizer.getCombination();
            chairs.put(combination, chair);
            if (maxCombValue == 0){
                maxCombValue = this.recognizer.getCombination().getValue();
            } else if (maxCombValue < combination.getValue()) {
                maxCombValue = combination.getValue();
            }
        }

        List<Chair> winners = new ArrayList<>();
        for (Map.Entry<Combination, Chair> entry : chairs.entrySet()) {
            System.out.println(entry.getKey().toString() + " => " + entry.getValue().toString());
            if (entry.getKey().getValue() == maxCombValue)
                winners.add(entry.getValue());
        }
        System.out.println(winners);
        return winners;
    }
}

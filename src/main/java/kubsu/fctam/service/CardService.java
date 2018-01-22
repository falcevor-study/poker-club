package kubsu.fctam.service;

import kubsu.fctam.dao.CardRepository;
import kubsu.fctam.entity.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {
    @Autowired
    private CardRepository cardRepository;

    // метод получения карты по id
    public Card get(int id) {
        return cardRepository.findOne(id);
    }
}

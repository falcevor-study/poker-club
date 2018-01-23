package kubsu.fctam.service;

import kubsu.fctam.dao.ChairRepository;
import kubsu.fctam.entity.Chair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ChairService {
    @Autowired
    private ChairRepository chairRepository;

    public void save(Chair chair) {
        chairRepository.save(chair);
    }

    public void delete(Chair chair) { chairRepository.delete(chair); }

    public void deleteAll() { chairRepository.deleteAll(); }

    public Chair getChair(int table_id, int user_id){
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(chairRepository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .filter(chair -> chair.getTable().getId() == table_id && chair.getUser().getId() == user_id)
                .findFirst().orElse(null);
    }

    public List<Chair> getAll(int table_id){
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(chairRepository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .filter(chair -> chair.getTable().getId() == table_id && chair.getStatus().equals("player") )
                .sorted()
                .collect(Collectors.toList());
    }

}

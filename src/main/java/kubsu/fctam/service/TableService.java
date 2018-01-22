package kubsu.fctam.service;

import kubsu.fctam.dao.TableRepository;
import kubsu.fctam.entity.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TableService {

    @Autowired
    private TableRepository repository;

    public Table get(int id) {
        return repository.findOne(id);
    }

    // метод для сохранения одной игры
    public void save(Table table) {
        repository.save(table);
    }

    // метод для доступа ко всем играм
    public List<Table> getAll() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(repository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .sorted()
                .collect(Collectors.toList());
    }

    public Table getTableByName(String name){
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(repository.findAll().iterator(), Spliterator.NONNULL),
                        false)
                .filter(table -> table.getName().equals(name))
                .findFirst().orElse(null);
    }
}

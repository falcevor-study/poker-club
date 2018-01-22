package kubsu.fctam.dao;

import kubsu.fctam.entity.Chair;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChairRepository extends CrudRepository<Chair, Integer> {
}

package kubsu.fctam.dao;

import kubsu.fctam.entity.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends CrudRepository<Table, Integer>{
}

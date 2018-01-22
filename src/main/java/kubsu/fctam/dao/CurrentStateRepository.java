package kubsu.fctam.dao;

import kubsu.fctam.entity.CurrentState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentStateRepository extends CrudRepository<CurrentState, Integer> {}

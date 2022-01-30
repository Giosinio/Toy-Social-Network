package repository.paging;

import model.Entity;
import repository.Repository;

public interface PagingRepository <ID, E extends Entity<ID>> extends Repository<ID, E> {
    Page<E> findAll(Pageable pageable);
}

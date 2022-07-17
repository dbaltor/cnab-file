package online.dbaltor.cnab.adapter.persistence;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ShopDbRepository extends PagingAndSortingRepository<ShopDb, Long> {

    public Optional<ShopDb> findByName(String name);
    
}

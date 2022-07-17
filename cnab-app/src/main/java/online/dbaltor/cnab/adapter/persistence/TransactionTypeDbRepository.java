package online.dbaltor.cnab.adapter.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TransactionTypeDbRepository extends CrudRepository<TransactionTypeDb, Long> {

    public Optional<TransactionTypeDb> findByType(Integer type);
    
}

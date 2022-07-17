package online.dbaltor.cnab.adapter.persistence;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface TransactionDbRepository extends PagingAndSortingRepository<TransactionDb, Long> {

    public List<TransactionDb> findByShopId(Long shopId);
}

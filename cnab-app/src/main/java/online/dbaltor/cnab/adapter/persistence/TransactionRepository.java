package online.dbaltor.cnab.adapter.persistence;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.StreamSupport;
import static java.util.stream.Collectors.*;

import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import online.dbaltor.cnab.dto.Shop;
import online.dbaltor.cnab.dto.ShopOpsAndBalance;
import online.dbaltor.cnab.dto.Transaction;
import online.dbaltor.cnab.dto.TransactionType.Nature;
import online.dbaltor.cnab.usecase.OpsManException;
import online.dbaltor.cnab.usecase.OpsManException.ErrorType;

@Component
@RequiredArgsConstructor
public class TransactionRepository {

    private @NonNull ShopDbRepository shopDbRepository;
    private @NonNull TransactionDbRepository txDbRepository;
    private @NonNull TransactionTypeDbRepository txTypeDbRepository;

    public List<Transaction> findTxsOfAGivenShop(Shop shop) throws OpsManException {

        return shopDbRepository.findById(shop.getId())
            .map(shopDb -> 
                StreamSupport.stream(txDbRepository.findByShopId(shopDb.getId()).spliterator(),false)
                    .map(txDb -> 
                        txTypeDbRepository.findById(txDb.getTypeId().getId())
                            .map(txTypeDb -> txDb.transaction(txTypeDb, shopDb))
                            .orElseThrow(() -> OpsManException.of(ErrorType.TXTYPE_NOT_FOUND)))
                    .toList()
            )
            .orElseThrow(() -> OpsManException.of(ErrorType.SHOP_NOT_FOUND));
    }

    public ShopOpsAndBalance findTxsWithBalanceOfAGivenShop(Shop shop) throws OpsManException {

        return shopDbRepository.findById(shop.getId())
            .map(shopDb -> StreamSupport.stream(txDbRepository.findByShopId(shopDb.getId()).spliterator(),false)
                .map(txDb -> {
                    val txTypeDb = txTypeDbRepository.findById(txDb.getTypeId().getId())
                        .orElseThrow(() -> OpsManException.of(ErrorType.TXTYPE_NOT_FOUND));
                    return txDb.transaction(txTypeDb, shopDb);
                })
                .collect(
                    teeing(
                        toList(), 
                        mapping(
                            tx -> (tx.getType().getNature() == Nature.IN) ? 
                                new BigDecimal(tx.getValue()) : 
                                new BigDecimal(tx.getValue()).negate(),
                            reducing(BigDecimal.ZERO, BigDecimal::add)),
                        (txs, bal) -> new ShopOpsAndBalance(shopDb.shop(), txs, bal.toPlainString()))))
            .orElseThrow(() -> OpsManException.of(ErrorType.SHOP_NOT_FOUND));
    }

    public List<ShopOpsAndBalance> findTxsWithBalancePerShop() {

        return StreamSupport.stream(shopDbRepository.findAll().spliterator(), false)
            .map(shopDb -> StreamSupport.stream(txDbRepository.findByShopId(shopDb.getId()).spliterator(),false)
                .map(txDb -> {
                    val txTypeDb = txTypeDbRepository.findById(txDb.getTypeId().getId())
                        .orElseThrow(() -> OpsManException.of(ErrorType.TXTYPE_NOT_FOUND));
                    return txDb.transaction(txTypeDb, shopDb);
                })
                .collect(
                    teeing(
                        toList(), 
                        mapping(
                            tx -> (tx.getType().getNature() == Nature.IN) ? 
                                new BigDecimal(tx.getValue()) : 
                                new BigDecimal(tx.getValue()).negate(),
                            reducing(BigDecimal.ZERO, BigDecimal::add)),
                        (txs, bal) -> new ShopOpsAndBalance(shopDb.shop(), txs, bal.toPlainString()))))
            .toList();
    }

    public Transaction save(Transaction tx) throws OpsManException {

        // Load Transaction Type or throw an exception
        val txTypeDb = txTypeDbRepository.findByType(tx.getType().getType())
            .orElseThrow(() -> OpsManException.of(ErrorType.TXTYPE_NOT_FOUND));
        tx.setType(txTypeDb.txType());

        // Load Shop or create a new one
        val shopDb = this.shopDbRepository.findByName(tx.getShop().getName())
            .orElseGet(() ->
                shopDbRepository.save(
                    ShopDb.of(
                        tx.getShop().getName(), 
                        tx.getShop().getOwner())));           
        tx.setShop(shopDb.shop());

        // Save transaction
        return txDbRepository.save(TransactionDb.of(tx))
            .transaction(txTypeDb, shopDb);
    }
}

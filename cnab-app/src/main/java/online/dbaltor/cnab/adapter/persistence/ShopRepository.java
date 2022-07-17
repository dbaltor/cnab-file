package online.dbaltor.cnab.adapter.persistence;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import online.dbaltor.cnab.dto.Shop;
import online.dbaltor.cnab.usecase.OpsManException;
import online.dbaltor.cnab.usecase.OpsManException.ErrorType;

@Component
@RequiredArgsConstructor
public class ShopRepository {

    private @NonNull ShopDbRepository shopDbRepository;

    public List<Shop> findAll() {
        return StreamSupport.stream(shopDbRepository.findAll().spliterator(),false)
            .map(ShopDb::shop)
            .toList();
    }

    public Shop findById(Long id) throws OpsManException {
        return shopDbRepository.findById(id)
            .map(ShopDb::shop)
            .orElseThrow(() -> OpsManException.of(ErrorType.SHOP_NOT_FOUND));
    }
    
    public Shop findByName(String name) throws OpsManException {
        return shopDbRepository.findByName(name)
            .map(ShopDb::shop)
            .orElseThrow(() -> OpsManException.of(ErrorType.SHOP_NOT_FOUND));
    }

    public Shop save(Shop shop) {
        return shopDbRepository.save(ShopDb.of(shop)).shop();
    }
}

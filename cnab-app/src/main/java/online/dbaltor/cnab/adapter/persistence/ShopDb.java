package online.dbaltor.cnab.adapter.persistence;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;
import online.dbaltor.cnab.dto.Shop;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@ToString @Getter @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class ShopDb{

    private @Setter @Id Long id;
    private @NonNull String name;
    private @NonNull String owner;

    public Shop shop() {
        val shop = Shop.of(
            this.name,
            this.owner);
        shop.setId(this.id);
        return shop;
    }

    public static ShopDb of(Shop shop) {
        val shopDb = ShopDb.of(
            shop.getName(), 
            shop.getOwner());
        shopDb.setId(shop.getId());
        return shopDb;
    }
}

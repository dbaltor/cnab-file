package online.dbaltor.cnab.adapter.persistence;
import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import online.dbaltor.cnab.dto.Transaction;

@ToString @Getter @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class TransactionDb {
   private @Setter @Id Long id;
   private @NonNull AggregateReference<TransactionTypeDb, Long> typeId; // aggregate id
   private @NonNull BigDecimal value;
   private @NonNull String recipientCpf;
   private @NonNull String cardNumber;
   private @NonNull String date;
   private @NonNull String time;
   private @NonNull AggregateReference<ShopDb, Long> shopId; // aggregate id

   public Transaction transaction(TransactionTypeDb txTypeDb, ShopDb shopDb) {
      val tx = Transaction.of(
         txTypeDb.txType(),
         this.value.toPlainString(), 
         this.recipientCpf,
         this.cardNumber,
         this.date,
         this.time,
         shopDb.shop());
      tx.setId(this.id);
      return tx;
  }

  public static TransactionDb of(Transaction tx) {
      val txDb = TransactionDb.of(
         AggregateReference.to(tx.getType().getId()),
         new BigDecimal(tx.getValue()), 
         tx.getRecipientCpf(),
         tx.getCardNumber(),
         tx.getDate(),
         tx.getTime(),
         AggregateReference.to(tx.getShop().getId()));
      txDb.setId(tx.getId());
      return txDb;
  }

}

package online.dbaltor.cnab.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class Transaction {
   private Long id;
   private @NonNull TransactionType type;
   private @NonNull String value;
   private @NonNull String recipientCpf;
   private @NonNull String cardNumber;
   private @NonNull String date;
   private @NonNull String time;
   private @NonNull Shop shop;
}

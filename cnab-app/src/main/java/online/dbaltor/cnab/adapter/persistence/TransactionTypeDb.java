package online.dbaltor.cnab.adapter.persistence;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

import online.dbaltor.cnab.dto.TransactionType;
import online.dbaltor.cnab.dto.TransactionType.Nature;

@ToString @Getter @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class TransactionTypeDb {
    private @Setter @Id Long id;
    private @NonNull Integer type;
    private @NonNull String description;
    private @NonNull String nature;

    public TransactionType txType() {
        val txType = new TransactionType(
            this.id,
            this.type, 
            this.description,
            Nature.valueOf(this.nature));
        return txType;
    }

    public static TransactionTypeDb of(TransactionType txType) {
        val txTypeDb = TransactionTypeDb.of(
            txType.getType(), 
            txType.getDescription(),
            txType.getNature().name());
        txTypeDb.setId(txType.getId());
        return txTypeDb;
    }
}

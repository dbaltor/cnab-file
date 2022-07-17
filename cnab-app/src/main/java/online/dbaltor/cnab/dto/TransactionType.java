package online.dbaltor.cnab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class TransactionType {
    public enum Nature {
        IN,
        OUT
    }

    private Long id;
    private @NonNull Integer type;
    private String description;
    private Nature nature;
}

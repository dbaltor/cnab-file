package online.dbaltor.cnab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class Shop {
    private Long id;
    private @NonNull String name;
    private @NonNull String owner;
}

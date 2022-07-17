package online.dbaltor.cnab.usecase;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class OpsManException extends RuntimeException{

    public static enum ErrorType {
        SHOP_NOT_FOUND,
        TXTYPE_NOT_FOUND
    }

    private @NonNull @Getter ErrorType errorType;
}

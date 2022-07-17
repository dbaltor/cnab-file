package online.dbaltor.cnab.adapter.persistence;

import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import online.dbaltor.cnab.dto.TransactionType;
import online.dbaltor.cnab.usecase.OpsManException;
import online.dbaltor.cnab.usecase.OpsManException.ErrorType;

@Component
@RequiredArgsConstructor
public class TransactionTypeRepository {

    private @NonNull TransactionTypeDbRepository txTypeDbRepository;

    public TransactionType findTxTypeById(Long id) throws OpsManException {

        return txTypeDbRepository.findById(id)
            .map(TransactionTypeDb::txType)
            .orElseThrow(() -> OpsManException.of(ErrorType.TXTYPE_NOT_FOUND));
    } 

    public TransactionType findTxTypeByType(Integer type) throws OpsManException {

        return txTypeDbRepository.findByType(type)
            .map(TransactionTypeDb::txType)
            .orElseThrow(() -> OpsManException.of(ErrorType.TXTYPE_NOT_FOUND));
    }
}

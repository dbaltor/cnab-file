package online.dbaltor.cnab.adapter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import online.dbaltor.cnab.dto.ShopOpsAndBalance;
import online.dbaltor.cnab.usecase.OperationsManager;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "cnab/v1",  produces = MediaType.APPLICATION_JSON_VALUE)
public class OperationsController{
    private @NonNull OperationsManager operationsManager;
    
    @GetMapping("operations")
    public ResponseEntity<List<ShopOpsAndBalance>> getOpsAndBalancePerShop() {
        return ResponseEntity.ok(operationsManager.retrieveOpsAndBalancePerShop());
    }
}

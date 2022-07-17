/***************************\
   Unit Tests
\***************************/
package online.dbaltor.cnab.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.val;
import online.dbaltor.cnab.dto.Shop;
import online.dbaltor.cnab.dto.ShopOpsAndBalance;
import online.dbaltor.cnab.dto.Transaction;
import online.dbaltor.cnab.dto.TransactionType;
import online.dbaltor.cnab.usecase.OpsManException.ErrorType;
import online.dbaltor.cnab.adapter.persistence.ShopDb;
import online.dbaltor.cnab.adapter.persistence.ShopDbRepository;
import online.dbaltor.cnab.adapter.persistence.ShopRepository;
import online.dbaltor.cnab.adapter.persistence.TransactionDb;
import online.dbaltor.cnab.adapter.persistence.TransactionDbRepository;
import online.dbaltor.cnab.adapter.persistence.TransactionRepository;
import online.dbaltor.cnab.adapter.persistence.TransactionTypeDb;
import online.dbaltor.cnab.adapter.persistence.TransactionTypeDbRepository;


public class OperationsManagerTest {

    private static final Shop SHOP1 = new Shop(1L, "BAR DO JOÃO", "JOÃO MACEDO");
    private static final Shop SHOP2 = new Shop(2L, "LOJA DO Ó - MATRIZ","MARIA JOSEFINA");
    private static final TransactionType TYPE1 = new TransactionType(1L,1, "Débito", TransactionType.Nature.IN);
    private static final TransactionType TYPE2 = new TransactionType(2L, 2, "Boleto", TransactionType.Nature.OUT);
    private static final Transaction SHOP1OP1 = Transaction.of(
        TYPE1,
        "100.00",
        "12345678901",
        "1111222233334444",
        LocalDate.of(2022, 06, 22).toString(),
        OffsetTime.of(9, 6, 0, 0, OperationsManager.ZONE_OFFSET).toString(),
        SHOP1);
    private static final Transaction SHOP1OP2 = Transaction.of(
        TYPE2,
        "80.00",
        "12345678901",
        "1111222233334444",
        LocalDate.of(2022, 06, 22).toString(),
        OffsetTime.of(10, 33, 0, 0, OperationsManager.ZONE_OFFSET).toString(),
        SHOP1);
    private static final Transaction SHOP2OP1 = Transaction.of(
        TYPE1,
        "100.00",
        "12345678901",
        "1111222233334444",
        LocalDate.of(2022, 06, 22).toString(),
        OffsetTime.of(19,15, 0, 0, OperationsManager.ZONE_OFFSET).toString(),
        SHOP2);
    private static final Transaction SHOP2OP2 = Transaction.of(
        TYPE2,
        "180.00",
        "12345678901",
        "1111222233334444",
        LocalDate.of(2022, 06, 22).toString(),
        OffsetTime.of(22, 0, 0, 0, OperationsManager.ZONE_OFFSET).toString(),
        SHOP2);
        
    private OperationsManager operationsManager;
    private ShopRepository shopRepository;
    private ShopDbRepository shopDbRepository;
    private TransactionRepository transactionRepository;
    private TransactionDbRepository transactionDbRepository;
    private TransactionTypeDbRepository transactionTypeDbRepository;

    @BeforeEach void setup() {
        this.shopDbRepository = mock(ShopDbRepository.class);
        this.transactionDbRepository = mock(TransactionDbRepository.class);
        this.transactionTypeDbRepository = mock(TransactionTypeDbRepository.class);
        this.shopRepository = new ShopRepository(this.shopDbRepository);
        this.transactionRepository = new TransactionRepository(this.shopDbRepository, this.transactionDbRepository, transactionTypeDbRepository);
        this.operationsManager = new OperationsManager(this.shopRepository, this.transactionRepository);
    }

    @Test void givenThereAreShopsSaved_whenListOfShopsIsRequested_thenListOfAllSavedShopsIsReturned() {
        // fail("Not yet implemented");
        // given
        val shopsGiven = List.of(SHOP1, SHOP2);
        when(shopDbRepository.findAll())
            .thenReturn(List.of(
                ShopDb.of(SHOP1),
                ShopDb.of(SHOP2)
            ));
        // when
        val shopsRetrieved = this.operationsManager.retriveShops();
        // then
        assertEquals(shopsGiven, shopsRetrieved);
    } 

    @Test void givenThereIsAShop_whenOpsPerStoreIsRequested_thenListOfOpsIsReturned() {
        // fail("Not yet implemented");
        // given
        val opsPerShopGiven = List.of(SHOP1OP1, SHOP1OP2);
        when(shopDbRepository.findById(eq(SHOP1.getId())))
            .thenReturn(Optional.of(ShopDb.of(SHOP1)));
        when(transactionTypeDbRepository.findById(eq(TYPE1.getId())))
            .thenReturn(Optional.of(TransactionTypeDb.of(TYPE1)));
        when(transactionTypeDbRepository.findById(eq(TYPE2.getId())))
            .thenReturn(Optional.of(TransactionTypeDb.of(TYPE2)));
        when(transactionDbRepository.findByShopId(eq(SHOP1.getId())))
            .thenReturn(List.of(
                TransactionDb.of(SHOP1OP1),
                TransactionDb.of(SHOP1OP2)
            ));
        try {
            // when
            val opsPerShopRetrieved = this.operationsManager.retrieveOpsPerShop(SHOP1);
            // then
            assertEquals(opsPerShopGiven, opsPerShopRetrieved);
        } catch (OpsManException ome) {
            fail(ome.getErrorType().toString());
        }
    }

    @Test void givenThereIsNoShop_whenOpsPerStoreIsRequested_thenAnExceptionIsThrown() {
        // fail("Not yet implemented");
        // when
        val exception = assertThrows(
            OpsManException.class,
            () -> {
                this.operationsManager.retrieveOpsPerShop(SHOP1);
            });
        // then
        assertEquals(ErrorType.SHOP_NOT_FOUND, exception.getErrorType());
    }

    @Test void givenThereIsAShop_whenOpsWithBalancePerStoreIsRequested_thenListOfOpsAndBalanceIsReturned() {
        // fail("Not yet implemented");
        // given
        val opsAndBalanceOfAShopGiven = new ShopOpsAndBalance(
            SHOP1,
            List.of(SHOP1OP1, SHOP1OP2),
            "20.00");
        when(shopDbRepository.findById(eq(SHOP1.getId())))
            .thenReturn(Optional.of(ShopDb.of(SHOP1)));
        when(transactionTypeDbRepository.findById(eq(TYPE1.getId())))
            .thenReturn(Optional.of(TransactionTypeDb.of(TYPE1)));
        when(transactionTypeDbRepository.findById(eq(TYPE2.getId())))
            .thenReturn(Optional.of(TransactionTypeDb.of(TYPE2)));
        when(transactionDbRepository.findByShopId(eq(SHOP1.getId())))
            .thenReturn(List.of(
                TransactionDb.of(SHOP1OP1),
                TransactionDb.of(SHOP1OP2)
            ));
        try {
            // when
            val opsAndBalanceOfAShopRetrieved = this.operationsManager.retrieveOpsAndBalanceOfAGivenShop(SHOP1);
            // then
            assertEquals(opsAndBalanceOfAShopGiven, opsAndBalanceOfAShopRetrieved);
        } catch (OpsManException ome) {
            fail(ome.getErrorType().toString());
        }
    }

    @Test void givenThereIsNoShop_whenOpsAndBalancePerStoreIsRequested_thenAnExceptionIsThrown() {
        // fail("Not yet implemented");
        // when
        val exception = assertThrows(
            OpsManException.class,
            () -> {
                this.operationsManager.retrieveOpsAndBalanceOfAGivenShop(SHOP1);
            });
        // then
        assertEquals(ErrorType.SHOP_NOT_FOUND, exception.getErrorType());
    } 
    
    @Test void whenOpsWithBalancePerStoreIsRequested_thenListOfOpsAndBalancePerStoreIsReturned() {
        // fail("Not yet implemented");
        // given
        val opsAndBalancePerShopGiven = List.of(
            new ShopOpsAndBalance(
                SHOP1,
                List.of(SHOP1OP1, SHOP1OP2),
                "20.00"),
            new ShopOpsAndBalance(
                SHOP2,
                List.of(SHOP2OP1, SHOP2OP2),
                "-80.00"));
        when(shopDbRepository.findAll())
            .thenReturn(List.of(
                ShopDb.of(SHOP1),
                ShopDb.of(SHOP2)
            ));
        when(transactionTypeDbRepository.findById(eq(TYPE1.getId())))
            .thenReturn(Optional.of(TransactionTypeDb.of(TYPE1)));
        when(transactionTypeDbRepository.findById(eq(TYPE2.getId())))
            .thenReturn(Optional.of(TransactionTypeDb.of(TYPE2)));
        when(transactionDbRepository.findByShopId(eq(SHOP1.getId())))
            .thenReturn(List.of(
                TransactionDb.of(SHOP1OP1),
                TransactionDb.of(SHOP1OP2)
            ));
        when(transactionDbRepository.findByShopId(eq(SHOP2.getId())))
        .thenReturn(List.of(
            TransactionDb.of(SHOP2OP1),
            TransactionDb.of(SHOP2OP2)
        ));
        // when
        val opsAndBalancePerShopRetrieved = this.operationsManager.retrieveOpsAndBalancePerShop();
        // then
        assertEquals(opsAndBalancePerShopGiven, opsAndBalancePerShopRetrieved);
    }

    @Test void givenDataIsValid_whenSaveIsRequested_thenTransactionIsSuccessfullySaved() {
        // fail("Not yet implemented");
        // given
        when(transactionTypeDbRepository.findByType(eq(TYPE1.getType())))
            .thenReturn(Optional.of(TransactionTypeDb.of(TYPE1)));
        when(shopDbRepository.findByName(eq(SHOP1.getName())))
            .thenReturn(Optional.of(ShopDb.of(SHOP1)));
        when(transactionDbRepository.save(any(TransactionDb.class)))
            .thenReturn(TransactionDb.of(SHOP1OP1));
        // when
        val savedTx = this.transactionRepository.save(SHOP1OP1);
        // then
        assertEquals(SHOP1OP1, savedTx);
    }

    @Test void givenTrasactionTypeIsNotFound_whenSaveIsRequested_thenAnExceptionIsThrown() {
        // fail("Not yet implemented");
        // given
        when(shopDbRepository.findByName(eq(SHOP1.getName())))
            .thenReturn(Optional.of(ShopDb.of(SHOP1)));
        when(transactionDbRepository.save(any(TransactionDb.class)))
            .thenReturn(TransactionDb.of(SHOP1OP1));
        // when
        val exception = assertThrows(
            OpsManException.class,
            () -> {
                this.transactionRepository.save(SHOP1OP1);
            });
        // then
        assertEquals(ErrorType.TXTYPE_NOT_FOUND, exception.getErrorType());
    }


    @Test void givenTheCNABContentIsCorrectlyFormatted_whenContentIsParsed_thenAllTransactionsAreCreated() {
        // fail("Not yet implemented");
        // given
        val FILE_CONTENT = 
"1201903010000015200096206760171234****7890233000JOÃO MACEDO   BAR DO JOÃO\n"+
"2201903010000011200096206760173648****0099234234JOÃO MACEDO   BAR DO JOÃO";
        val file = new ByteArrayInputStream(FILE_CONTENT.getBytes());
        val listOfTransactionsGiven = List.of(
            Transaction.of(
                TransactionType.of(1),
                "152.00",
                "09620676017",
                "1234****7890",
                LocalDate.of(2019, 03, 01).toString(),
                OffsetTime.of(23, 30, 0, 0, OperationsManager.ZONE_OFFSET).toString(),
                Shop.of("BAR DO JOÃO", "JOÃO MACEDO   ")),
            Transaction.of(
                TransactionType.of(2),
                "112.00",
                "09620676017",
                "3648****0099",
                LocalDate.of(2019, 03, 01).toString(),
                OffsetTime.of(23, 42, 34, 0, OperationsManager.ZONE_OFFSET).toString(),
                Shop.of("BAR DO JOÃO", "JOÃO MACEDO   ")));
        // when
        val listOfTransactionsParsed = this.operationsManager.parseCNABContent(file)
            .toList();
        // then
        assertEquals(listOfTransactionsGiven, listOfTransactionsParsed);
    }

    @Test void givenTheCNABContentContainsWrongTypeOrDateOrTime_whenContentIsParsed_thenWrongLinesAreSkipped() {
        // fail("Not yet implemented");
        // given
        val FILE_CONTENT = 
"X201903010000015200096206760171234****7890233000JOÃO MACEDO   BAR DO JOÃO\n"+ // wrong type = X
"1201913010000015200096206760171234****7890233000JOÃO MACEDO   BAR DO JOÃO\n"+ // wrong month = 13
"1201903010000015200096206760171234****78902330X0JOÃO MACEDO   BAR DO JOÃO\n"+ // wrong second = 0X
"2201903010000011200096206760173648****0099234234JOÃO MACEDO   BAR DO JOÃO";
        val file = new ByteArrayInputStream(FILE_CONTENT.getBytes());
        val listOfTransactionsGiven = List.of(
            Transaction.of(
                TransactionType.of(2),
                "112.00",
                "09620676017",
                "3648****0099",
                LocalDate.of(2019, 03, 01).toString(),
                OffsetTime.of(23, 42, 34, 0, OperationsManager.ZONE_OFFSET).toString(),
                Shop.of("BAR DO JOÃO", "JOÃO MACEDO   ")));
        // when
        val listOfTransactionsParsed = this.operationsManager.parseCNABContent(file)
            .toList();
        // then
        assertEquals(listOfTransactionsGiven, listOfTransactionsParsed);
    }

}

package online.dbaltor.cnab.usecase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import online.dbaltor.cnab.adapter.persistence.ShopRepository;
import online.dbaltor.cnab.adapter.persistence.TransactionRepository;
import online.dbaltor.cnab.dto.Shop;
import online.dbaltor.cnab.dto.Transaction;
import online.dbaltor.cnab.dto.TransactionType;
import online.dbaltor.cnab.dto.ShopOpsAndBalance;
@Service
@RequiredArgsConstructor @Slf4j
public class OperationsManager {

    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.of("-03:00");

    private @NonNull ShopRepository shopRepository;
    private @NonNull TransactionRepository txRepository;

    public List<Shop> retriveShops() {
        return shopRepository.findAll();
    }

    public List<Transaction> retrieveOpsPerShop(Shop shop) throws OpsManException {
        return txRepository.findTxsOfAGivenShop(shop);
    }

    public ShopOpsAndBalance retrieveOpsAndBalanceOfAGivenShop(Shop shop) throws OpsManException {
        return txRepository.findTxsWithBalanceOfAGivenShop(shop);
    }

    public List<ShopOpsAndBalance> retrieveOpsAndBalancePerShop() {
        return txRepository.findTxsWithBalancePerShop();
    }

    public Stream<Transaction> parseCNABContent(InputStream file) {
        return new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8))
            .lines()
            .map(OperationsManager::parseCNABline)
            .flatMap(Optional::stream);
    }

    private static Optional<Transaction> parseCNABline(String line) {
        TransactionType txType;
        Transaction transaction;
        Shop shop;

        // Parse Transaction Type
        val strTxType = line.substring(0, 1);
        try {
            txType = TransactionType.of(Integer.valueOf(strTxType));
        } catch (NumberFormatException nfe) {
            log.error("Invalid CNAB line - Transaction Type: %s", strTxType);
            return Optional.empty();
        }

        // Parse Shop
        val strShopName = line.substring(62);
        val strShopOwner = line.substring(48, 62);
        shop = Shop.of(strShopName, strShopOwner);
        
        // Parse Transaction
        val strTxDate = line.substring(1, 9);
        val strTxValue = line.substring(9, 19);
        val strTxCpf = line.substring(19, 30);
        val strTxCard = line.substring(30, 42);
        val strTxTime = line.substring(42, 48);
        try { 
            transaction = Transaction.of(
                txType, 
                new BigDecimal(strTxValue).movePointLeft(2).toPlainString(), 
                strTxCpf, 
                strTxCard, 
                LocalDate.parse(strTxDate, DateTimeFormatter.ofPattern("yyyyMMdd")).toString(), 
                LocalTime.parse(strTxTime, DateTimeFormatter.ofPattern("HHmmss"))
                    .atOffset(ZONE_OFFSET).toString(),
                shop);
        } catch (NumberFormatException nfe) {
            log.error("Invalid CNAB line - Transaction value: %s", strTxValue);
            return Optional.empty();
        } catch (DateTimeParseException dtpe) {
            log.error("Invalid CNAB line - Transaction date or time: %s, %s", strTxDate, strTxTime);
            return Optional.empty();            
        }
        return Optional.of(transaction);
    }

}

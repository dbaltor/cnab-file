/***************************\
   Integration Tests
\***************************/
package online.dbaltor.cnab;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
// import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.MediaType.*;

import online.dbaltor.cnab.adapter.persistence.TransactionRepository;
import online.dbaltor.cnab.dto.Shop;
import online.dbaltor.cnab.dto.Transaction;
import online.dbaltor.cnab.dto.TransactionType;
import online.dbaltor.cnab.usecase.OperationsManager;
@SpringBootTest( 
    classes = App.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class AppTest {

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

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private WebTestClient webTestClient;

    private List<Transaction> txs;

    // Setup database for tests
    @BeforeAll void setup() {
        txs = List.of(SHOP1OP1, SHOP1OP2, SHOP2OP1, SHOP2OP2);
        txs.forEach(transactionRepository::save);
    }

    @Test void contextLoads() {
        
    }

    @Test void whenOpsWithBalancePerStoreIsRequested_thenListOfOpsAndBalancePerStoreIsReturned() {
        // fail("Not yet implemented");
		// when
		this.webTestClient
			.get()
			.uri("/cnab/v1/operations")
			.exchange()
		// then
			.expectHeader()
			.contentType(APPLICATION_JSON)
			.expectStatus()
			.isOk()
			.expectBody()
			.jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[0].balance")
                .isEqualTo(new BigDecimal(SHOP1OP1.getValue()).subtract(new BigDecimal(SHOP1OP2.getValue())))
            .jsonPath("$[1].balance")
                .isEqualTo(new BigDecimal(SHOP2OP1.getValue()).subtract(new BigDecimal(SHOP2OP2.getValue())));


    }
}

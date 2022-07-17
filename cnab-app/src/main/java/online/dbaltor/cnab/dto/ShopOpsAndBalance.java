package online.dbaltor.cnab.dto;

import java.util.List;

public record ShopOpsAndBalance(Shop shop, List<Transaction> operations, String balance) {}

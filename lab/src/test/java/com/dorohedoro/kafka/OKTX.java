package com.dorohedoro.kafka;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OKTX {

    public String chainShortName;
    public String txid;
    public String blockHash;
    public Long height;
    public Long transactionTime;
    public String from;
    public String to;
    public boolean isFromContract;
    public boolean isToContract;
    public String fromTag;
    public String toTag;
    public BigDecimal amount;
    public String transactionSymbol;
    public BigDecimal txfee;
    public String state;
    public String tokenId;
    public String tokenContractAddress;
}

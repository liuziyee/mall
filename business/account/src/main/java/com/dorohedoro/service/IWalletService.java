package com.dorohedoro.service;

import com.dorohedoro.dto.WalletDTO;

public interface IWalletService {

    WalletDTO getBalanceByUID();

    WalletDTO deductBalance(Long fee);
}

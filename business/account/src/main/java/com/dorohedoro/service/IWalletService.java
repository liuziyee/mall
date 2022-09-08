package com.dorohedoro.service;

import com.dorohedoro.dto.WalletDTO;

public interface IWalletService {

    WalletDTO getBalanceByUserId();

    WalletDTO deductBalance(Long fee);
}

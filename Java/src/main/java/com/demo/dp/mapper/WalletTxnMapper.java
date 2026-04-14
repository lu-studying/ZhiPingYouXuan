package com.demo.dp.mapper;

import com.demo.dp.domain.entity.WalletTxn;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WalletTxnMapper {
    int insert(WalletTxn txn);
}

package com.payment.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		String dataSourceType = isReadOnly ? "slave" : "master";
		// log.info("데이터베이스: {}", dataSourceType);
		MDC.put("datasource", dataSourceType);
		try {
			return dataSourceType;
		} finally {
			MDC.remove("datasource"); // 컨텍스트 정리
		}
	}

}

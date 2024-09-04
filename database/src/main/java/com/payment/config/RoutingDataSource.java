package com.payment.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import lombok.extern.slf4j.Slf4j;

@Profile("!dev") // dev 프로파일이 아닌 경우에만 적용
@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		String dataSourceType = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "slave" : "master";
		MDC.put("datasource", dataSourceType);
		try {
			return dataSourceType;
		} finally {
			MDC.remove("datasource"); // 컨텍스트 정리
		}
	}

}

package com.payment.config;

import org.slf4j.MDC;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDataSource extends AbstractRoutingDataSource {
	@Override
	protected Object determineCurrentLookupKey() { // (1)
		String dataSourceType = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "slave" : "master";
		MDC.put("datasource", dataSourceType);
		return (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) ? "slave" : "master"; //(2)
	}
}

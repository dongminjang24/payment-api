// package com.payment.common.config.properties;
//
//
// import java.util.HashMap;
// import java.util.Map;
//
// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.stereotype.Component;
//
// import lombok.Getter;
// import lombok.Setter;
//
// @Component
// @ConfigurationProperties(prefix = "rest.template")
// @Getter
// @Setter
// public class RestTemplateProperties {
// 	private Map<String, ClientProperties> clients = new HashMap<>();
//
// 	@Getter
// 	@Setter
// 	public static class ClientProperties {
// 		private int connectTimeout;
// 		private int readTimeout;
// 	}
// }
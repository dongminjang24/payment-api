package com.payment.common.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheService {

	private final CacheManager cacheManager;
	private static final long DEFAULT_CACHE_EXPIRY_MS = 300000; // 5 minutes

	public boolean isCacheable(String cacheName, String key, long expiryMs) {
		Cache cache = cacheManager.getCache(cacheName + ":last_update");
		if (cache != null) {
			Cache.ValueWrapper wrapper = cache.get(key);
			if (wrapper != null) {
				long lastUpdateTime = (long) wrapper.get();
				return System.currentTimeMillis() - lastUpdateTime > expiryMs;
			}
		}
		return true;
	}

	public boolean isCacheable(String cacheName, String key) {
		return isCacheable(cacheName, key, DEFAULT_CACHE_EXPIRY_MS);
	}

	public void updateLastCacheTime(String cacheName, String key) {
		Cache cache = cacheManager.getCache(cacheName + ":last_update");
		if (cache != null) {
			cache.put(key, System.currentTimeMillis());
		}
	}

	public void invalidateCache(String cacheName, String key) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.evict(key);
		}
		updateLastCacheTime(cacheName, key);
	}

	public void invalidateAllCache(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.clear();
		}
	}
}
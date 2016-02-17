package com.easyshop.core.ehcache;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * 缓存管理
 * 
 * @author luocz
 *
 */
@IocBean
public class EhcacheManager {

	@Inject
	CacheManager cacheManager;

	private Cache cache;

	private void initCache() {
		if (cache == null) {
			cache = cacheManager.getCache("eternalCache");
		}
	}

	/**
	 * 缓存存储
	 * 
	 * @param key
	 *            EhcacheContstant
	 * @param cacheObject
	 */
	public void put(String key, Serializable cacheObject) {
		initCache();
		cache.put(new Element(key, cacheObject));
	}

	public Serializable get(String key) {
		initCache();
		Element element = cache.get(key);
		return element != null ? element.getValue() : null;
	}

	public void remove(String key) {
		cache.remove(key);
	}

}

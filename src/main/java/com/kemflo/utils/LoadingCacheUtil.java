package com.kemflo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoadingCacheUtil {

    /**
     * 缓存项最大数量
     */
    private static final long GUAVA_CACHE_SIZE = 10000;
    /**
     * 缓存时间：分钟
     */
    private static final long GUAVA_CACHE_SECONDS = 5400;
    /**
     * 缓存操作对象
     */
    private static LoadingCache<String, Object> GLOBAL_CACHE = null;

    static {
        try {
            GLOBAL_CACHE = loadCache(new CacheLoader<String, Object>() {
                @Override
                public Object load(String key) throws Exception {
                    return ObjectUtils.NULL;
                }
            });
        } catch (Exception e) {
            log.error("初始化Guava Cache出错", e);
        }
    }

    /**
     * 全局缓存设置 缓存项最大数量：100000 缓存有效时间（分钟）：10
     * 
     * @param cacheLoader
     * @return
     * @throws Exception
     */
    private static LoadingCache<String, Object> loadCache(CacheLoader<String, Object> cacheLoader) throws Exception {
        LoadingCache<String, Object> cache = CacheBuilder.newBuilder()
            // 缓存池大小，在缓存项接近该大小时， Guava开始回收旧的缓存项
            .maximumSize(GUAVA_CACHE_SIZE)
            // 设置时间对象没有被读/写访问则对象从内存中删除(在另外的线程里面不定期维护)
            .refreshAfterWrite(GUAVA_CACHE_SECONDS, TimeUnit.SECONDS).build(cacheLoader);
        return cache;
    }

    /**
     * 设置缓存值 注: 若已有该key值，则会先移除(会触发removalListener移除监听器)，再添加
     *
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        try {
            GLOBAL_CACHE.put(key, value);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("设置缓存值出错", e);
        }
    }

    /**
     * 批量设置缓存值
     *
     * @param map
     */
    public static void putAll(Map<? extends String, ? extends Object> map) {
        try {
            GLOBAL_CACHE.putAll(map);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("批量设置缓存值出错", e);
        }
    }

    /**
     * 获取缓存值 注：如果键不存在值，将调用CacheLoader的load方法加载新值到该键中
     *
     * @param key
     * @return
     */
    public static Object get(String key) {
        Object token = null;
        try {
            token = GLOBAL_CACHE.get(key);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("获取缓存值出错", e);
        }
        return token;
    }

    /**
     * 获取缓存值 注：如果键不存在值，将直接返回 NULL
     *
     * @param key
     * @return
     */
    public static Object getIfPresent(String key) {
        Object obj = null;
        try {
            obj = GLOBAL_CACHE.getIfPresent(key);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("获取缓存值出错", e);
        }
        return obj;
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    public static void remove(String key) {
        try {
            GLOBAL_CACHE.invalidate(key);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("移除缓存出错", e);
        }
    }

    /**
     * 批量移除缓存
     *
     * @param keys
     */
    public static void removeAll(Iterable<String> keys) {
        try {
            GLOBAL_CACHE.invalidateAll(keys);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("批量移除缓存出错", e);
        }
    }

    /**
     * 清空所有缓存
     */
    public static void removeAll() {
        try {
            GLOBAL_CACHE.invalidateAll();
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("清空所有缓存出错", e);
        }
    }

    /**
     * 获取缓存项数量
     *
     * @return
     */
    public static long size() {
        long size = 0;
        try {
            size = GLOBAL_CACHE.size();
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("获取缓存项数量出错", e);
        }
        return size;
    }

    /**
     * 获取所有缓存项的键
     *
     * @return
     */
    public static List<String> keys() {
        List<String> list = new ArrayList<>();
        try {
            ConcurrentMap<String, Object> map = GLOBAL_CACHE.asMap();
            for (Map.Entry<String, Object> item : map.entrySet()) {
                list.add(item.getKey());
            }
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("获取所有缓存项的键出错", e);
        }
        return list;
    }

    /**
     * 缓存命中率
     *
     * @return
     */
    public static double getHitRate() {
        return GLOBAL_CACHE.stats().hitRate();
    }

    /**
     * 加载新值的平均时间，单位为纳秒
     *
     * @return
     */
    public static double getAverageLoadPenalty() {
        return GLOBAL_CACHE.stats().averageLoadPenalty();
    }

    /**
     * 缓存项被回收的总数，不包括显式清除
     *
     * @return
     */
    public static long getEvictionCount() {
        return GLOBAL_CACHE.stats().evictionCount();
    }
}

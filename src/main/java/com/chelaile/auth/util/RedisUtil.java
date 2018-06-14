package com.chelaile.auth.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

public class RedisUtil {

	public static Logger log = LogManager.getLogger(RedisUtil.class);

	private static JedisPool jedisPool = (JedisPool) SpringContextUtil.getBean("jedisPool");

	private static String REDIS_HOST = null;

	private static Integer REDIS_PORT = 6379;

	private int db_index = 2;

	static {
		try {
			REDIS_HOST = ConfigPropertiesUtil.getValue("redis.host");
			REDIS_PORT = Integer.parseInt(ConfigPropertiesUtil.getValue("redis.port"));
		} catch (Exception e) {
			throw new RuntimeException("Config error, msg=" + e.getMessage(), e);
		}
	}

	public static String getRedisHost() {
		return REDIS_HOST;
	}

	public static int getRedisPort() {
		return REDIS_PORT;
	}

	public RedisUtil() {
	}

	/**
	 * 构造方法
	 * 
	 * @param _db_index DB索引
	 */
	public RedisUtil(int _db_index) {
		this.db_index = _db_index;
	}

	/**
	 * 获取一个RedisUtil实例
	 * 
	 * @return
	 */
	public static RedisUtil getInstance() {
		return new RedisUtil();
	}

	/**
	 * 获取一个带参RedisUtil实例
	 * 
	 * @param _db_index
	 * @return
	 */
	public static RedisUtil getInstance(int _db_index) {
		return new RedisUtil(_db_index);
	}

	/**
	 * 获取连接池
	 * 
	 * @return Jedis
	 */
	public static Jedis getJedisPool() {
		Jedis jedis = null;
		if (jedisPool != null) {
			jedis = jedisPool.getResource();
		} else {
			log.error("连接池连接失败");
		}

		return jedis;

	}

	/**
	 * 将jedis对象释放回连接池中
	 * 
	 * @param jedis 使用完毕的Jedis对象
	 * @return true 释放成功；否则返回false
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static boolean release(Jedis jedis) throws Exception {
		if (jedisPool != null && jedis != null) {
			try {
				jedisPool.returnResource(jedis);
			} catch (Exception e) {
				log.error("jedis对象释放失败");
				throw new Exception("jedis对象释放失败:" + e.toString());
			}
			return true;
		}
		return false;
	}

	/**
	 * 将jedis对象释放回连接池中
	 * 
	 * @param jedis 使用完毕的Jedis对象
	 * @return true 释放成功；否则返回false
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static boolean releaseBroken(Jedis jedis) throws Exception {
		if (jedisPool != null && jedis != null) {
			try {
				jedisPool.returnResource(jedis);
			} catch (Exception e) {
				log.error("jedis对象释放失败");
				throw new Exception("jedis对象释放失败:" + e.toString());
			}
			return true;
		}
		return false;
	}

	/**
	 * 将jedis对象get操作-String
	 * 
	 * @param key
	 * @return String
	 * @throws Exception
	 */
	public String get(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = getJedisPool();
			jedis.select(db_index);
			return jedis.get(key);
		} catch (Exception e) {
			releaseBroken(jedis);
			log.error(e.toString());
			throw new Exception(e.toString());
		} finally {
			release(jedis);
		}
	}

	/**
	 * 将jedis对象get操作-byte[]
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public byte[] get(byte[] key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = getJedisPool();
			jedis.select(db_index);
			return jedis.get(key);
		} catch (Exception e) {
			releaseBroken(jedis);
			log.error(e.toString());
			throw new Exception(e.toString());
		} finally {
			release(jedis);
		}
	}

	/**
	 * 将jedis对象set操作-String
	 * 
	 * @param key
	 * @param value
	 * @param expireTime
	 * @return
	 * @throws Exception
	 */
	public boolean set(String key, String value, int expireTime) throws Exception {
		Jedis jedis = null;
		try {
			jedis = getJedisPool();
			jedis.select(db_index);
			jedis.setex(key, expireTime, value);
			return true;
		} catch (Exception e) {
			releaseBroken(jedis);
			log.error(e.toString());
			throw new Exception(e.toString());
		} finally {
			release(jedis);
		}
	}

	/**
	 * 将jedis对象set操作-byte[]
	 * 
	 * @param key
	 * @param value
	 * @param expireTime
	 * @return
	 * @throws Exception
	 */
	public boolean set(byte[] key, byte[] value, int expireTime) throws Exception {
		Jedis jedis = null;
		try {
			jedis = getJedisPool();
			jedis.select(db_index);
			jedis.setex(key, expireTime, value);
			return true;
		} catch (Exception e) {
			releaseBroken(jedis);
			log.error(e.toString());
			throw new Exception(e.toString());
		} finally {
			release(jedis);
		}
	}

	/**
	 * 将jedis对象delete操作-String
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public boolean del(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = getJedisPool();
			jedis.select(db_index);
			jedis.del(key);
			return true;
		} catch (Exception e) {
			releaseBroken(jedis);
			log.error(e.toString());
			throw new Exception(e.toString());
		} finally {
			release(jedis);
		}
	}

	/**
	 * 将jedis对象delete操作-byte[]
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public boolean del(byte[] key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = getJedisPool();
			jedis.select(db_index);
			jedis.del(key);
			return true;
		} catch (Exception e) {
			releaseBroken(jedis);
			log.error(e.toString());
			throw new Exception(e.toString());
		} finally {
			release(jedis);
		}
	}

	/**
	 * 将jedis对象进行DB flushDB
	 * 
	 * @throws Exception
	 */
	public void flushDB() throws Exception {
		Jedis jedis = null;
		try {
			jedis = getJedisPool();
			jedis.select(db_index);
			jedis.flushDB();
		} catch (Exception e) {
			releaseBroken(jedis);
			log.error(e.toString());
			throw new Exception(e.toString());
		} finally {
			release(jedis);
		}
	}

	/**
	 * 将jedis对象进行dbSize查询
	 * 
	 * @return
	 * @throws Exception
	 */
	public long dbSize() throws Exception {
		Jedis jedis = null;
		try {
			jedis = getJedisPool();
			jedis.select(db_index);
			return jedis.dbSize();
		} catch (Exception e) {
			releaseBroken(jedis);
			log.error(e.toString());
			throw new Exception(e.toString());
		} finally {
			release(jedis);
		}
	}

	/**
	 * 查询匹配制定模式的所有key
	 * 
	 * @param pattern
	 * @return
	 * @throws Exception
	 */
	public Set<String> keys(String pattern) throws Exception {
		Jedis jedis = null;
		try {
			jedis = getJedisPool();
			jedis.select(db_index);
			return jedis.keys(pattern);
		} catch (Exception e) {
			releaseBroken(jedis);
			log.error(e.toString());
			throw new Exception(e.toString());
		} finally {
			release(jedis);
		}
	}
}
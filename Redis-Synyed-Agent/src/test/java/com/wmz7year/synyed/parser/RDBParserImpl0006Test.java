package com.wmz7year.synyed.parser;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.apache.commons.io.HexDump;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wmz7year.synyed.Booter;
import com.wmz7year.synyed.parser.entry.RedisDB;

/**
 * redis rdb0006版本的解析器测试
 * 
 * @Title: RDBParserImpl0006Test.java
 * @Package com.wmz7year.synyed.parser
 * @author jiangwei (ydswcy513@gmail.com)
 * @date 2015年12月14日 下午2:37:44
 * @version V1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Booter.class)
public class RDBParserImpl0006Test {
	private static final Logger logger = LoggerFactory.getLogger(RDBParserImpl0006Test.class);
	private static final byte[] rdbData = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54, -2, 0, 0, 1, 97, 1, 98, 0, 1,
			99, 1, 100, 0, 1, 98, 1, 99, -1, 16, 79, 24, -53, 114, -99, 102, 122 };

	/**
	 * 显示RDB字节内容以及格式化
	 */
	@Test
	public void showRDBDumpData() throws Exception {
		HexDump.dump(rdbData, 0, System.out, 0);
	}

	/**
	 * 测试解析RDB文件内容
	 */
	@Test
	public void testParseRDBFile() throws Exception {
		byte[] rdbHeader = new byte[9];
		System.arraycopy(rdbData, 0, rdbHeader, 0, 9);
		RDBParser rdbParser = RDBParserFactory.createRDBParser(rdbHeader);
		rdbParser.parse(rdbData);

		Collection<RedisDB> redisDBs = rdbParser.getRedisDBs();
		for (RedisDB redisDB : redisDBs) {
			List<String> commands = redisDB.getCommands();
			for (String command : commands) {
				logger.info("redis command rdb:" + redisDB.getNum() + " command：" + command);
			}
		}
	}

	/**
	 * 测试正常校验crc64值
	 */
	@Test
	public void testCheckRDBCRCSum() throws Exception {
		byte[] rdbData = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54, -1, -36, -77, 67, -16, 90, -36, -14, 86 };
		byte[] rdbHeader = new byte[9];
		System.arraycopy(rdbData, 0, rdbHeader, 0, 9);
		RDBParser rdbParser = RDBParserFactory.createRDBParser(rdbHeader);
		rdbParser.parse(rdbData);
	}

	/**
	 * 测试正常校验crc64值
	 */
	@Test
	public void testCheckRDBCRCSumOnerror() throws Exception {
		byte[] rdbData = new byte[] { 82, 69, 68, 73, 83, 48, 48, 48, 54, -1, -36, -77, 67, -16, 90, -36, -14, 87 };
		byte[] rdbHeader = new byte[9];
		System.arraycopy(rdbData, 0, rdbHeader, 0, 9);
		try {
			RDBParser rdbParser = RDBParserFactory.createRDBParser(rdbHeader);
			rdbParser.parse(rdbData);
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(true);
		}
	}

}

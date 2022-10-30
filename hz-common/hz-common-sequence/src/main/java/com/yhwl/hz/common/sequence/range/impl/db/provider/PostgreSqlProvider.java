package com.yhwl.hz.common.sequence.range.impl.db.provider;

import com.baomidou.mybatisplus.annotation.DbType;
import org.springframework.stereotype.Component;

/**
 * 数据源提供者
 *
 * @author hz
 * @date 2022-01-13
 */
@Component
public class PostgreSqlProvider implements SqlProvider {

	/**
	 * 获取建表语句
	 * @return
	 */
	@Override
	public String getCreateTableSql() {
		return "CREATE TABLE IF NOT EXISTS #tableName (ID int8 PRIMARY KEY NOT NULL"
				+ ",VALUE int8 NOT NULL,NAME VARCHAR (266) NOT NULL,gmt_create TIMESTAMP (6) NOT NULL"
				+ ",gmt_modified TIMESTAMP (6) NOT NULL)";
	}

	@Override
	public Boolean support(DbType dbType) {
		return DbType.POSTGRE_SQL.equals(dbType);
	}

}

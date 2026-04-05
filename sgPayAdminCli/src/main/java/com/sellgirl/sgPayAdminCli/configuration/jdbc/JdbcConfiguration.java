package com.sellgirl.sgPayAdminCli.configuration.jdbc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sellgirl.sgJavaHelper.ISGUnProGuard;
import com.sellgirl.sgJavaHelper.sql.ISGJdbc;
import com.sellgirl.sgJavaHelper.sql.PFJdbcBase;
//import pf.java.pfMaxWellHelper.jdbc.IJdbcConfig;
import com.sellgirl.sgJavaHelper.sql.SGJdbc;


/**
 * google注入方式
 * 
 * @author Administrator
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JdbcConfiguration implements ISGUnProGuard//implements pf.java.pfMaxWellHelper.jdbc.IJdbcConfig
{
	@JsonProperty
	private SGJdbc shop;

	public SGJdbc getShop() {
		return shop;
	}

	public void setDay(SGJdbc shop) {
		this.shop = shop;
	}

}

package com.sellgirl.sgPayAdminCli;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.sellgirl.sgJavaHelper.sql.ISGJdbc;
import com.sellgirl.sgJavaHelper.sql.PFJdbcBase;
//import pf.java.pfMaxWellHelper.jdbc.IJdbcConfig;
import com.sellgirl.sgJavaHelper.sql.SGJdbc;
import com.sellgirl.sgPayAdminCli.configuration.jdbc.JdbcConfiguration;


/**
 * google注入方式
 * 
 * @author Administrator
 *
 */
public class AppConfiguration //implements pf.java.pfMaxWellHelper.jdbc.IJdbcConfig
{
	private JdbcConfiguration jdbc;

	public JdbcConfiguration getJdbc() {
		return jdbc;
	}

	public void setJdbc(JdbcConfiguration jdbc) {
		this.jdbc = jdbc;
	}
}

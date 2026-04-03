package com.sellgirl.sgPayAdminCli;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sellgirl.sgJavaHelper.SGDate;
import com.sellgirl.sgJavaHelper.config.SGDataHelper;
import com.sellgirl.sgPayAdminCli.configuration.SGConfigMapper;
import com.sellgirl.sgPayAdminCli.configuration.jdbc.JdbcConfiguration;

/**
 * Hello world!
 *
 */
public class PayAdminCliApp 
{
    public static void main( String[] args )
    {
        SGDataHelper.SetConfigMapper(new SGConfigMapper());
        SGDataHelper.setAppArg(args);
        
        String s=SGDataHelper.ReadLocalResourceWithEnvironmentVariable("jdbc-config.yml");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JdbcConfiguration jdbcConfig=null;
        try {
			jdbcConfig= mapper.readValue(s, JdbcConfiguration.class);

			System.out.println("jdbc__"+ mapper.writeValueAsString(jdbcConfig));
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return;
		}
        
    	TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
    	TimeZone.setDefault(timeZone);
    	
 	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String dateStr = sdf.format(Calendar.getInstance().getTime());
        System.out.println("PfTransferTaskAppTest__"+ dateStr);
        System.out.println("PfTransferTaskAppTest__"+ SGDate.Now().toString());
    }
}

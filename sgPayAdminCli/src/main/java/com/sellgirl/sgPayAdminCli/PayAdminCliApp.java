package com.sellgirl.sgPayAdminCli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Hello world!
 *
 */
@Command(name = "PayAdminCliApp")
public class PayAdminCliApp 
{
    public static void main( String[] args )
    {
    	boolean isTest=false;
        SGDataHelper.SetConfigMapper(new SGConfigMapper());
        SGDataHelper.setAppArg(args);

//		StringBuilder sb=new StringBuilder();
//		sb.append("aa");
//		sb.append("bb");
//		ArrayList<String> list=new ArrayList<String>();
//		list.add("aa");
//		list.add("bb");
//        System.out.println( String.join("\r\n",list ));
//        System.out.println(SGDataHelper.CurrentEnvironmental);
        
        //String s=SGDataHelper.ReadLocalResourceWithEnvironmentVariable("jdbc-config.yml");
        String s=SGDataHelper.readAnylResource("jdbc-config.yml", -1);
        if(isTest) {
		System.out.println("---------------jdbc-config.yml-------------");
		System.out.println(s);
        }
        if(SGDataHelper.StringIsNullOrWhiteSpace(s)) {
        	SGDataHelper.getLog().print("没有找到jdbc-config.yml,请确保其存在");
            System.exit(-1);
            return;
        }
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JdbcConfiguration jdbcConfig=null;
        try {
			jdbcConfig= mapper.readValue(s, JdbcConfiguration.class);

			System.out.println("---------------jdbc.yml-------------");
			System.out.println(mapper.writeValueAsString(jdbcConfig));
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return;
		}
        
    	TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
    	TimeZone.setDefault(timeZone);

    	AppConfiguration app=new AppConfiguration();
    	app.setJdbc(jdbcConfig);
        System.out.println("PayAdminCliApp__"+ SGDate.Now().toString());
        
//        DataImporter.main(args);

        CommandLine cli = new CommandLine(new PayAdminCliApp()); //如果只有单指令, 这里MyCli直接换成DataImporter即可
        cli.addSubcommand("import", new DataImporter(app)); 
        cli.addSubcommand("upload", new ImgUpload(app)); 
        cli.addSubcommand("delete", new DataDeleter(app));
        cli.addSubcommand("total", new DataTotal(app));
        int exitCode = cli.execute(args);
        System.exit(exitCode);
    }
}

package com.sellgirl.sgPayAdminCli;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sellgirl.sgJavaHelper.ISGUnProGuard;
import com.sellgirl.sgJavaHelper.SGDate;
import com.sellgirl.sgJavaHelper.SGYamlHelper;
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
public class PayAdminCliApp implements ISGUnProGuard
{
    public static void main( String[] args )
    {
		//System.out.println(1);
    	boolean isTest=false;
        SGDataHelper.SetConfigMapper(new SGConfigMapper());
        //System.out.println(2);
        SGDataHelper.setAppArg(args);
        //System.out.println(3);

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
        //System.out.println(4);
        if(isTest) {
		System.out.println("---------------jdbc-config.yml-------------");
		System.out.println(s);
        }
        if(SGDataHelper.StringIsNullOrWhiteSpace(s)) {
        	SGDataHelper.getLog().print("没有找到jdbc-config.yml,请确保其存在");
            System.exit(-1);
            return;
        }
        //System.out.println(5);

    	AppConfiguration app=new AppConfiguration();

    	//System.out.println(6);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        //System.out.println(7);
        JdbcConfiguration jdbcConfig=null;
        //System.out.println(8);
        try {
			jdbcConfig= mapper.readValue(s, JdbcConfiguration.class);

			//System.out.println("---------------jdbc.yml-------------");
			//System.out.println(mapper.writeValueAsString(jdbcConfig));
			Map<String, String> map=SGYamlHelper.yamlToMap(s);
//			Map<String, String> map=SGYamlHelper.loadMap(SGDataHelper.readAnylResource("jdbc-config.yml", -1));
			if(map.containsKey("hy")&&!SGDataHelper.StringIsNullOrWhiteSpace(map.get("hy"))) {
				app.setHy(ImgUpload.getHy(map.get("hy")));
			}
			if(map.containsKey("ssh")&&!SGDataHelper.StringIsNullOrWhiteSpace(map.get("ssh"))) {
				app.setSsh(map.get("ssh"));
			}
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
        //System.out.println(9);
        
    	TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
    	TimeZone.setDefault(timeZone);

    	//System.out.println(10);
    	app.setJdbc(jdbcConfig);
    	//System.out.println(11);
        System.out.println("PayAdminCliApp__"+ SGDate.Now().toString());
        
//        DataImporter.main(args);

        CommandLine cli = new CommandLine(new PayAdminCliApp()); //如果只有单指令, 这里MyCli直接换成DataImporter即可
        cli.addSubcommand("import", new DataImporter(app)); 
        cli.addSubcommand("upload", new ImgUpload(app)); 
        cli.addSubcommand("delete", new DataDeleter(app));
        cli.addSubcommand("total", new DataTotal(app));
        cli.addSubcommand("refund", new OrderRefund(app));
        int exitCode = cli.execute(args);
        System.exit(exitCode);

    }
}

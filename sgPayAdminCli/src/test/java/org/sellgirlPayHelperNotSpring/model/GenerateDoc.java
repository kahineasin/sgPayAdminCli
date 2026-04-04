package org.sellgirlPayHelperNotSpring.model;

import picocli.CommandLine;
import picocli.codegen.docgen.manpage.ManPageGenerator;

import java.io.File;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sellgirl.sgJavaHelper.config.SGDataHelper;
import com.sellgirl.sgPayAdminCli.AppConfiguration;
import com.sellgirl.sgPayAdminCli.DataImporter;
import com.sellgirl.sgPayAdminCli.ImgUpload;
import com.sellgirl.sgPayAdminCli.PayAdminCliApp;
import com.sellgirl.sgPayAdminCli.configuration.jdbc.JdbcConfiguration;

public class GenerateDoc {
    public static void main(String[] args) throws Exception {
        // 指定生成的文档输出目录
        File outputDir = new File("docs/manpages");

        String s=SGDataHelper.readAnylResource("jdbc-config.yml", -1);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JdbcConfiguration jdbcConfig=null;
        try {
			jdbcConfig= mapper.readValue(s, JdbcConfiguration.class);

//			System.out.println("---------------jdbc.yml-------------");
//			System.out.println(mapper.writeValueAsString(jdbcConfig));
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return;
		}
        
    	AppConfiguration app=new AppConfiguration();
    	app.setJdbc(jdbcConfig);
        CommandLine cli = new CommandLine(new PayAdminCliApp()); //如果只有单指令, 这里MyCli直接换成DataImporter即可
        cli.addSubcommand("import", new DataImporter(app)); //DataImporter的定义, 见下面deepseek的说明
        cli.addSubcommand("upload", new ImgUpload(app));        
        // 调用 ManPageGenerator，传入你的主命令类
        int exitCode = ManPageGenerator.generateManPage(
            outputDir, // 输出目录
            null,      // 可自定义模板的目录，通常用 null
            new boolean[]{true}, // 详细输出
            false,      // 是否覆盖自定义页面
            // 你的主命令类的 CommandSpec（Picocli 会自动解析）
            new CommandLine(new DataImporter(app)).getCommandSpec()
        );
        
        if (exitCode == 0) {
            System.out.println("文档生成成功！目录：" + outputDir.getAbsolutePath());
        } else {
            System.err.println("文档生成失败！");
        }
    }
}
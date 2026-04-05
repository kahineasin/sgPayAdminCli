package com.sellgirl.sgPayAdminCli;


import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Workbook;

import com.sellgirl.sellgirlPayService.product.ResourceService;
import com.sellgirl.sellgirlPayService.product.model.*;
import com.sellgirl.sgJavaHelper.AES;
import com.sellgirl.sgJavaHelper.ISGUnProGuard;
import com.sellgirl.sgJavaHelper.PFPoint;
import com.sellgirl.sgJavaHelper.SGDate;
import com.sellgirl.sgJavaHelper.SGLine;
import com.sellgirl.sgJavaHelper.SGRef;
import com.sellgirl.sgJavaHelper.SGSpeedCounter;
import com.sellgirl.sgJavaHelper.SGSqlCommandString;
import com.sellgirl.sgJavaHelper.config.SGDataHelper;
import com.sellgirl.sgJavaHelper.file.SGDirectory;
import com.sellgirl.sgJavaHelper.file.SGEncryptByte;
import com.sellgirl.sgJavaHelper.file.SGPath;
import com.sellgirl.sgJavaHelper.sql.ISGJdbc;
import com.sellgirl.sgJavaHelper.sql.ISqlExecute;
import com.sellgirl.sgJavaHelper.sql.SGSqlExecute;
import com.sellgirl.sgJavaHelper.sql.SGSqlInsertCollection;
import com.sellgirl.sgJavaHelper.time.SGWaiter;
import com.sellgirl.sgHelperExport.SGExcelHelper;

@Command(name = "upload", 
         mixinStandardHelpOptions = true, 
         version = "1.0",
         description = "上传图片到云ubuntu")
public class ImgUpload implements Callable<Integer>  , ISGUnProGuard{
	private AppConfiguration app;
	public ImgUpload(AppConfiguration app) {
		this.app=app;
	}

//    @Parameters(index = "0", description = "Excel 文件路径")
//    private File excelFile;

//    @Option(names = {"-s", "--server"}, description = "服务器地址", required = true)
//    private String server;

//    @Option(names = {"-u", "--user"}, description = "SSH 用户名", defaultValue = "ubuntu")
//    private String user;
//
//    @Option(names = {"-p", "--password"}, description = "SSH 密码（也可用私钥）", interactive = true)
//    private String password;
//
//    @Option(names = {"-k", "--key"}, description = "私钥路径")
//    private File privateKey;

    @Option(names = {"--json"}, description = "输出 JSON 格式结果")
    private boolean jsonOutput;

//    @Option(names = {"-e", "--excel"}, description = "excelPath")
//    private String excelPath;
    @Option(names = {"-l", "--local"}, description = "localRoot")
    private String localRoot;
    @Option(names = {"-r", "--remote"}, description = "remoteRoot")
    private String remoteRoot;
//    @Option(names = {"-t", "--type"}, description = "resourceType")
//    private ResourceType resourceType;

//	String excelPath,String resourcePath,String outImgPath,
//	ResourceType resourceType
	
    @Override
    public Integer call() throws Exception {
        try {
            // 复用你现有的导入逻辑
            // 比如：ImportService.importData(excelFile, server, user, password, privateKey);

            if (jsonOutput) {
                System.out.println("{\"status\":\"success\", \"rows\":100}");
            } else {
            	System.out.println("localRoot:"+localRoot);
            	System.out.println("remoteRoot:"+remoteRoot);
            	
            	doUpload();
            }
            return 0;  // 成功返回 0
        } catch (Exception e) {
            if (jsonOutput) {
                System.err.println("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
            } else {
                System.err.println("导入失败：" + e.getMessage());
            }
            return 1;  // 失败返回非零
        }
    }

//    public static void main(String[] args) {
//        int exitCode = new CommandLine(new DataImporter()).execute(args);
//        System.exit(exitCode);
//    }
    //-----------------上传逻辑------------------------

	public void doUpload() {
		ConcurrentSftpUpload2 uploader=(new ConcurrentSftpUpload2());
		if(null!=app.getHy()) { uploader.setPassword(app.getHy());}
		if(null!=app.getSsh()) { uploader.setSSH(app.getSsh());}
		uploader.upload(this.localRoot, this.remoteRoot);
	}
	public static String getHy(String s) throws IOException, Exception {
		String s2=getKey();
    	if(null!=s2) { return AES.AESDecryptDemo(s,getKey());}else {return null;}
	}
    public static String getKey() throws IOException {
    	File encFile = new File(Paths.get(SGDataHelper.GetBaseDirectoryAbsolutePath(),"b.jpg").toString());
    	if(encFile.exists()) {
		FileInputStream fis = new FileInputStream(encFile);
		return SGEncryptByte.DecryptByteToString(fis, SGEncryptByte.DEFAULT_BUFFER_SIZE, 0x123456);
    	}else {return null;}

    }
}

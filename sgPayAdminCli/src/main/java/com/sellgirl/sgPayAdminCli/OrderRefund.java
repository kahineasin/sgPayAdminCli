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
import java.math.BigDecimal;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Workbook;

import com.sellgirl.sellgirlPayService.pay.OrderService;
import com.sellgirl.sellgirlPayService.pay.ZPayNativeService;
import com.sellgirl.sellgirlPayService.pay.model.vipOrder;
import com.sellgirl.sellgirlPayService.product.ResourceService;
import com.sellgirl.sellgirlPayService.product.model.*;
import com.sellgirl.sgJavaHelper.ISGUnProGuard;
import com.sellgirl.sgJavaHelper.PFPoint;
import com.sellgirl.sgJavaHelper.SGDate;
import com.sellgirl.sgJavaHelper.SGLine;
import com.sellgirl.sgJavaHelper.SGRef;
import com.sellgirl.sgJavaHelper.SGRequestResult;
import com.sellgirl.sgJavaHelper.SGSpeedCounter;
import com.sellgirl.sgJavaHelper.SGSqlCommandString;
import com.sellgirl.sgJavaHelper.SqlExpressionOperator;
import com.sellgirl.sgJavaHelper.config.SGDataHelper;
import com.sellgirl.sgJavaHelper.file.SGDirectory;
import com.sellgirl.sgJavaHelper.file.SGPath;
import com.sellgirl.sgJavaHelper.sql.ISGJdbc;
import com.sellgirl.sgJavaHelper.sql.ISqlExecute;
import com.sellgirl.sgJavaHelper.sql.SGSqlExecute;
import com.sellgirl.sgJavaHelper.sql.SGSqlInsertCollection;
import com.sellgirl.sgJavaHelper.sql.SGSqlWhereCollection;
import com.sellgirl.sgJavaHelper.time.SGWaiter;
import com.sellgirl.sgHelperExport.SGExcelHelper;

@Command(name = "refund", 
         mixinStandardHelpOptions = true, 
         version = "1.0",
         description = "订单退款")
public class OrderRefund implements Callable<Integer>, ISGUnProGuard{
	private final String TAG="OrderRefund";
	private AppConfiguration app;
	public OrderRefund(AppConfiguration app) {
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
//    @Option(names = {"-r", "--resource"}, description = "resourcePath")
//    private String resourcePath;
    @Option(names = {"-o", "--outno"}, description = "sg订单号(外部交易号)")
    private long outNo;
    @Option(names = {"-no", "--no"}, description = "zpay订单号(交易号)")
    private String no;

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
//                System.out.println("------param------");
//                System.out.println("excelFile:"+excelFile.getAbsolutePath());
//                System.out.println("resourcePath:"+this.resourcePath);
//                System.out.println("outImgPath:"+this.outImgPath);
//                System.out.println("resourceType:"+resourceType);
//                System.out.println("导入成功！共处理 100 条记录");
            	SGRequestResult r=this.doRefund(//excelFile, resourcePath, outImgPath, null
            			);

        		System.out.println(r.content);
            	if(r.success) {
            		if("{\"code\":0,\"msg\":\"订单编号不存在！\"}".equals(r.content)) {
                		
                		System.out.println("refund failed");
            		}else {
                		System.out.println("refund success");
            		}
        		}
            	else {
            		System.out.println("refund failed");
            		}
            }
            return 0;  // 成功返回 0
        } catch (Exception e) {
            if (jsonOutput) {
                System.err.println("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
            } else {
                System.err.println("delete failed:" + e.getMessage());
            }
            return 1;  // 失败返回非零
        }
    }

//    public static void main(String[] args) {
//        int exitCode = new CommandLine(new DataImporter()).execute(args);
//        System.exit(exitCode);
//    }
    //-----------------删除逻辑------------------------


	public SGRequestResult doRefund() {
//    	JdbcHelper.setShop(app.getJdbc());
    	OrderService orderService = new OrderService();
    	orderService.setJdbc(app.getJdbc().getShop());
    	vipOrder order=orderService.GetOnevipOrder(outNo);
    	ZPayNativeService zPayService=new ZPayNativeService();
    	return zPayService.refund3(order.getVip_order_id(), order.getOrder_no(), order.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
	}

}

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
import com.sellgirl.sgJavaHelper.PFPoint;
import com.sellgirl.sgJavaHelper.SGDate;
import com.sellgirl.sgJavaHelper.SGLine;
import com.sellgirl.sgJavaHelper.SGRef;
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

@Command(name = "import", 
         mixinStandardHelpOptions = true, 
         version = "1.0",
         description = "从 Excel 导入数据到服务器")
public class DataImporter implements Callable<Integer> {
	private final String TAG="DataImporter";
	private AppConfiguration app;
	public DataImporter(AppConfiguration app) {
		this.app=app;
	}

    @Parameters(index = "0", description = "Excel 文件路径")
    private File excelFile;

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
    @Option(names = {"-r", "--resource"}, description = "resourcePath")
    private String resourcePath;
    @Option(names = {"-o", "--out"}, description = "outImgPath")
    private String outImgPath;
    @Option(names = {"-t", "--type"}, description = "resourceType")
    private ResourceType resourceType;
    @Option(names = {"-b", "--begin"}, description = "beginRow")
    private long beginRow;

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
                System.out.println("------param------");
                System.out.println("excelFile:"+excelFile.getAbsolutePath());
                System.out.println("resourcePath:"+this.resourcePath);
                System.out.println("outImgPath:"+this.outImgPath);
                System.out.println("resourceType:"+resourceType);
//                System.out.println("导入成功！共处理 100 条记录");
            	this.testImportResource(//excelFile, resourcePath, outImgPath, null
            			);
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
    //-----------------导入逻辑------------------------

	private boolean clear=false;
	private boolean printBug=true;
	private boolean printProgress=true;
	
	/**
	 * 从爬取资源导入mysql, 同时为最新的4条数据生成resource-index用到的120px和200px规格图片
	 * @param excelPath "D:\\cache\\html1\\20260324mix2\\"+resourceType+".xlsx";
	 * @param resourcePath "D:\\cache\\html1\\20260324mix2\\图片\\P预览图"
	 * @param outImgPath "D:\\cache\\html1\\resourceImg\\"+resourceType
	 * @param resourceType ResourceType.image
	 */
	public void testImportResource(
//			String excelPath,String resourcePath,String outImgPath,
//			ResourceType resourceType
			) {
		  SGSpeedCounter speed=null;
		  SGWaiter waiter=null;
		  int total=0;
		
		int err=0;
		StringBuilder deny=new StringBuilder(); 
		
		long resourceId=-1;
//		initresource();
		try {
////			ISGJdbc srcJdbc = JdbcHelperTest.GetLiGeOrderProdJdbc();
//			ISGJdbc dstJdbc = JdbcHelperTest.GetSgShopJdbc();
			ISGJdbc dstJdbc = app.getJdbc().getShop();
//			ISGJdbc lrJdbc = JdbcHelperTest.GetMySqlTest2Jdbc();

////			String resourcePath="D:\\cache\\html1\\resource_data\\提取示例";
////			String resourcePath="D:\\cache\\html1\\1";
////			String resourcePath="D:\\cache\\html1\\电子书资源成品\\电子书资源成品\\电子书资源成品\\电子书资源成品";
//			//String resourcePath="D:\\cache\\html1\\resource_data\\bugData";
//			
////			String resourcePath="D:\\cache\\html1\\20260324mix2\\1-2500预览图\\1-2500预览图";
////			String resourcePath="D:\\cache\\html1\\20260324mix2\\漫画\\漫画预览图";
//			String resourcePath="D:\\cache\\html1\\20260324mix2\\图片\\P预览图";
//			
////			String outImgPath="D:\\cache\\html1\\resourceImg\\cover";
////			String outImgPath2="D:\\cache\\html1\\resourceImg\\content";
//			ResourceType resourceType=ResourceType.image;
//			String outImgPath="D:\\cache\\html1\\resourceImg\\"+resourceType;
//			//String outImgPath2="D:\\cache\\html1\\resourceImg\\content";
//
////			String excelPath="D:\\cache\\html1\\20260324mix2\\视频信息(1).csv";
//			String excelPath="D:\\cache\\html1\\20260324mix2\\"+resourceType+".xlsx";
			
//			Workbook wb1 = SGExcelHelper.create(new FileInputStream(new File(excelPath)));
			Workbook wb1 = SGExcelHelper.create(new FileInputStream(excelFile));

//			ResourceType resourceType=ResourceType.movie;
			
			SGDirectory.EnsureExists(outImgPath);
			//SGDirectory.EnsureExists(outImgPath2);
//			File root=new File(resourcePath);
	        File[] files = new File(resourcePath).listFiles();
	        SGSqlInsertCollection insert=null;
//	        SGSqlInsertCollection insert2=null;

        	ArrayList<File> covers=new ArrayList<File>();
			try (ISqlExecute dstExec = SGSqlExecute.Init(dstJdbc)) {
				dstExec.AutoCloseConn(false);



				ResourceService service=new ResourceService();
//				service.setResourceType(resourceType);
				
				if(clear) {
					dstExec.TruncateTable(service.getTableName(resourceType));
					//dstExec.TruncateTable("sg_resource_chap");
				}


				  if(printProgress) {
					  speed=new SGSpeedCounter(com.sellgirl.sgJavaHelper.SGDate.Now());
					  total=files.length;
					  waiter=new SGWaiter(2000);
				  }

		            
				SGSqlCommandString sql2=new SGSqlCommandString(
						SGDataHelper.FormatString(
								"select max(resource_id) from {0}",
								service.getTableName(resourceType)
						));
				long maxId=SGDataHelper.ObjectToLong0(dstExec.QuerySingleValue(sql2.toString()));
						
				List<Map<String, Object>> list1=SGExcelHelper.ExcelToDictList(wb1);
				int idx=0;//idx应严格和excel的行对应
//		        for(File i:files) {
			    for(Map<String,Object> row:list1) {
		        	idx++;
		        	if(0<this.beginRow) {
		        		if(this.beginRow>idx) {
		        			continue;
		        		}
		        	}
		        	
//		        	if(200<idx) {
//		        		throw new Exception("测试错误,lastId:"+dstExec.GetLastInsertedId());
//		        	}
//		        	String title=SGDataHelper.ReadFileToString(Paths.get(i.getAbsolutePath(), "title.txt").toString());
//		        	Map<String,Object> row=list1.get(idx);
		        	String title=SGDataHelper.ObjectToString(row.get("文件名")) ;
//		        	String autor=SGDataHelper.ReadFileToString(Paths.get(i.getAbsolutePath(), "autor.txt").toString());
		        	String autor=this.getAuthor(title);
//		        	String autor=SGDataHelper.ObjectToString(row.get("链接")) ;
//		        	String autor=SGDataHelper.ObjectToString(row.get("链接")) ;
//		        	String autor=SGDataHelper.ObjectToString(row.get("链接")) ;
		        	
//		        	List<String> chap=SGDataHelper.ReadFileToLines(Paths.get(i.getAbsolutePath(), "contents.txt").toString());
//					File coverImg=new File(Paths.get(i.getAbsolutePath(), "cover.jpg").toUri());
//		        	
//		        	if(SGDataHelper.StringIsNullOrWhiteSpace(title)) {
//		        		if(printBug) {
//		        		System.out.println("resource title none, folder:"+i.getName());
//		        		}
//		        		deny.append(i.getName()+"\r\n");
//		        		continue;
//		        	}
		        	
		        	int j=0;
		        	String tmpCover="";
		        	covers.clear();
		        	boolean hasAnyCover=false;
		        	
		        	//如果图片名连续固定
//		        	while(true) {
//		        		j++;
////			        	File coverI=new File(Paths.get(resourcePath,title,j+".jpg").toUri());
//			        	File coverI=new File(Paths.get(resourcePath,title,title+"-"+j+".jpg").toUri());
//		        		if(coverI.exists()) {
//		        			if(!hasAnyCover) {hasAnyCover=true;}
//		        			covers.add(coverI);
//		        			tmpCover+=j+".jpg"+",";
////		        			tmpCover+="resourceImg/cover/"+coverI.getName();
////							SGPath.copyFile(
////									coverI,
////			        			new File(Paths.get(outImgPath, resourceId+".jpg").toUri())
////			        			);
//		        		}else {
//		        			break;
//		        		}
//		        	}
		        	

		        	//如果图片名不连续
		        	j=0;
		        	File coverFolder=new File(Paths.get(resourcePath,title).toUri());
		        	for(File coverI:coverFolder.listFiles()) {
		        		j++;
//			        	File coverI=new File(Paths.get(resourcePath,title,j+".jpg").toUri());
//			        	File coverI=new File(Paths.get(resourcePath,title,title+"-"+j+".jpg").toUri());
		        		if(coverI.exists()) {
		        			if(!hasAnyCover) {hasAnyCover=true;}
		        			covers.add(coverI);
		        			tmpCover+=j+".jpg"+",";
//		        			tmpCover+="resourceImg/cover/"+coverI.getName();
//							SGPath.copyFile(
//									coverI,
//			        			new File(Paths.get(outImgPath, resourceId+".jpg").toUri())
//			        			);
		        		}else {
		        			break;
		        		}
		        	}
		        	if(hasAnyCover) {
		        		tmpCover=tmpCover.substring(0,tmpCover.length()-1);
		        	}else {
//		        		int aa=1;
		        		if(printBug) {
		        		System.out.println("resource cover error, "+resourceType+":"+title);
		        		System.out.println("lastId:"+dstExec.GetLastInsertedId());
		        		}
		        	}

//					String c=new String(new char[] {SGDataHelper.getFirstLetter(title.charAt(0))});
					//String c=ProjHelper.getFirstLetter(title);
					
					resourceCreate model=new resourceCreate();
		        	if(0<this.beginRow) {
		        		model.setResource_id(maxId+idx-beginRow);
		        	}else {
		        		model.setResource_id(maxId+idx);
		        	}
					model.setResource_name(title);
					model.setResource_author(autor);
//					model.setLetter(c);
//					model.setCover(coverImg.exists()?"id":"");
					model.setCover(tmpCover);
					model.setNetdisk(SGDataHelper.ObjectToString(row.get("链接")));
					model.setExtractCode(SGDataHelper.ObjectToString(row.get("提取码")));
					model.setUnlockPassword(SGDataHelper.ObjectToString(row.get("解压密码")));
					if(ResourceType.image==resourceType) {
//						model.setResource_name(SGDataHelper.ObjectToString(row.get("标题")));
						model.setDuration(SGDataHelper.ObjectToInt0(row.get("图片数量")));
						Double size=SGDataHelper.ObjectToDouble0(row.get("总大小(字节)"));
						model.setSize(SGDataHelper.ObjectToInt0(size/1024));
					}else if(ResourceType.comic==resourceType) {
						model.setResource_name(SGDataHelper.ObjectToString(row.get("标题")));
						model.setDuration(SGDataHelper.ObjectToInt0(row.get("图片数量")));
						Double size=SGDataHelper.ObjectToDouble0(row.get("总大小(字节)"));
						model.setSize(SGDataHelper.ObjectToInt0(size/1024));
					}else {
						model.setDuration(SGDataHelper.ObjectToInt0(row.get("时长(秒)")));
						Double size=SGDataHelper.ObjectToDouble0(row.get("大小(字节)"));
						model.setSize(SGDataHelper.ObjectToInt0(size/1024));
					}
					model.setCreate_date(SGDate.Now());
					if(null==insert) {
						insert=dstExec.getInsertCollection();					
						insert.InitItemByModel(model);
					}else {
						insert.UpdateModelValueAutoConvert(model);
					}

					SGSqlCommandString sql=new SGSqlCommandString(
							SGDataHelper.FormatString(
									"insert into {2} ({0}) values ({1})",
									insert.ToKeysSql(),
									insert.ToValuesSql(),
									service.getTableName(resourceType)
									)
							);
					int r=dstExec.ExecuteSqlInt(sql, null, false);
					if(1>r) {
						err++;
		        		continue;
					}
//					System.out.println("id:"+r);
//					System.out.println("id2:"+dstExec.GetLastInsertedId());
					//后面复制图片等等操作,都要以resourceId这个准确数据库id为准
					 resourceId=dstExec.GetLastInsertedId();
					if(hasAnyCover) {
						j=0;
//						SGDirectory.EnsureExists(Paths.get(outImgPath, ""+resourceId).toString());
						SGDirectory.EnsureExists(Paths.get(outImgPath,resourceType.toString(), ""+resourceId).toString());
						for(File k:covers) {
							j++;
//							SGPath.copyFile(
//								k,
//		        			new File(Paths.get(outImgPath, ""+resourceId,""+j+".jpg").toUri())
//		        			);
							SGPath.copyFile(
									k,
			        			new File(Paths.get(outImgPath,resourceType.toString(), ""+resourceId,""+j+".jpg").toUri())
			        			);
					    	this.testGenSmallImg(outImgPath, resourceId,""+j+".jpg", resourceType, 60);
						}
					}


//					//封面，如果路径是用id,那么cover字段留空可以了，否则要反写id到cover,更麻烦
//					if(coverImg.exists()) {
//						SGPath.copyFile(
//		        			coverImg,
//		        			new File(Paths.get(outImgPath, resourceId+".jpg").toUri())
//		        			);
//					}
					



			        
			        if(printProgress&&waiter.isOK()) {
						System.out.println("lastId:"+resourceId+"----"+speed.getEnSpeed(idx,com.sellgirl.sgJavaHelper.SGDate.Now()));
			        }
		        }
			    
			    //复制resource-index的12个图的120和200规格
			    //Paths.get(outImgPath, ""+resourceId,""+j+".jpg").toUri()
			    long lastId=dstExec.GetLastInsertedId();
			    for(long i=lastId;lastId-4<i&&0<i;i--) {
			    	this.testGenSmallImg(outImgPath, i,"1.jpg", resourceType, 120);
			    	this.testGenSmallImg(outImgPath, i,"1.jpg", resourceType, 200);
			    }
				
				
				dstExec.close();
			} catch (Exception e) {
				e.printStackTrace();
				err++;
			}
			System.out.println("total err: "+err);
			if(0<deny.length()) {System.out.println("deny: "+deny);}


		} catch (Exception e) {
			SGDataHelper.getLog().printException(e, TAG);
			SGDataHelper.getLog().print("lastId:"+resourceId);
		}
		if(printProgress) {
			System.out.println(speed.getEnSpeed(total,com.sellgirl.sgJavaHelper.SGDate.Now()));
		}
	}
	//-----------------------------缩略图-------------------------

	/**
	 * 生成1张缩略图图
	 * @param jpgName 如"1.jpg"
	 * @throws IOException
	 */
	public void testGenSmallImg(String outImgPath,long resourceId,String jpgName,
			ResourceType resourceType,int size) throws IOException {
		
		//for(int i=0;resourceIds.length>i;i++) {
//			long resourceId=resourceIds[i];
//			String srcImgPath=Paths.get("D:\\cache\\html1\\resourceImg",imgs[i]).toString();
//			String dstImgPath=Paths.get("D:\\cache\\html1\\resourceImg",outImgs[i]).toString();
//			URI srcImgPath=Paths.get(outImgPath,resourceType.toString(), String.valueOf(resourceId),jpgName).toUri();
//			URI dstImgPath2=Paths.get(outImgPath,resourceType.toString()+String.valueOf(size), String.valueOf(resourceId),jpgName).toUri();
			String srcImgPath=Paths.get(outImgPath,resourceType.toString(), String.valueOf(resourceId),jpgName).toString();
			String dstImgPath=Paths.get(outImgPath,resourceType.toString()+String.valueOf(size), String.valueOf(resourceId),jpgName).toString();
//			//String dstImgPathStr=dstImgPath.toString();
//			SGDataHelper.getLog().print("aa-----"+dstImgPath2.toString());
//			SGDataHelper.getLog().print("bb-----"+dstImgPath);
			File file=new File(srcImgPath);
//			String path=Paths.get(dstImgPath,String.valueOf( sizes[i]),"1.jpg").toString();

			SGDirectory.EnsureFilePath(dstImgPath);

			//方法2. 20秒 (推荐)
			int backW = size; // 1920;
			int backH = size;// 1080;
			SGLine imgLine=SGLine.FitHeightAndCenterHorizontally();
			SGLine backLine=new SGLine(new PFPoint(0, 0), new PFPoint(backW, backH));
			
			SGRef<Canvas> canvasRef = new SGRef<Canvas>(null);
			SGRef<Graphics> ctx1Ref = new SGRef<Graphics>(null);
			BufferedImage paintBi = null;
			BufferedImage image = ImageIO.read(file);
			paintBi = SGDataHelper.backgroundImgInBuffer(
					canvasRef, ctx1Ref, null, 
					new Dimension(backW, backH), image, null,
					backLine,imgLine, 
					//Color.RED
					null
					, false);
			File file2 = new File(dstImgPath);
			ImageIO.write(paintBi, SGPath.getFileExtension(dstImgPath), file2);
//		}
	}

	/**
	 * 生成少量几张测试图(看手机200图效果)
	 * @throws IOException
	 */
	@Deprecated
	public void testGenSomeSmallImg(String outImgPath,long[] resourceIds,
			ResourceType resourceType,int size) throws IOException {
////		int size=80;
////		ResourceType resourceType=ResourceType.movie;
//		String[] imgs=new String[] {
//				"movie\\2500\\1.jpg",
//				"movie\\2499\\1.jpg",
//				"movie\\2498\\1.jpg",
//				"movie\\2497\\1.jpg",
//				"image\\334\\1.jpg",
//				"image\\333\\1.jpg",
//				"image\\332\\1.jpg",
//				"image\\331\\1.jpg",
//				"comic\\423\\1.jpg",
//				"comic\\422\\1.jpg",
//				"comic\\421\\1.jpg",
//				"comic\\420\\1.jpg"
//		}; 
//		String[] outImgs=new String[] {
//				"movie200\\2500\\1.jpg",
//				"movie200\\2499\\1.jpg",
//				"movie200\\2498\\1.jpg",
//				"movie200\\2497\\1.jpg",
//				"image200\\334\\1.jpg",
//				"image200\\333\\1.jpg",
//				"image200\\332\\1.jpg",
//				"image200\\331\\1.jpg",
//				"comic200\\423\\1.jpg",
//				"comic200\\422\\1.jpg",
//				"comic200\\421\\1.jpg",
//				"comic200\\420\\1.jpg"
//		}; 
//		int size=120;
//		for(int i=0;resourceIds.length>i;i++) {
//			outImgs[i]=outImgs[i].replace(ResourceType.movie.toString()+"200", ResourceType.movie.toString()+size);
//			outImgs[i]=outImgs[i].replace(ResourceType.image.toString()+"200", ResourceType.image.toString()+size);
//			outImgs[i]=outImgs[i].replace(ResourceType.comic.toString()+"200", ResourceType.comic.toString()+size);
//		}
//		String srcImgPath="D:\\cache\\html1\\resourceImg\\image\\334\\1.jpg";
//		String dstImgPath="D:\\cache\\html1\\testImg";
		//int[] sizes=new int[] {90,100,110,120,130,140,150};
		
		for(int i=0;resourceIds.length>i;i++) {
//			int size=sizes[i];
			long resourceId=resourceIds[i];
//			String srcImgPath=Paths.get("D:\\cache\\html1\\resourceImg",imgs[i]).toString();
//			String dstImgPath=Paths.get("D:\\cache\\html1\\resourceImg",outImgs[i]).toString();
			URI srcImgPath=Paths.get(outImgPath, String.valueOf(resourceId),"1.jpg").toUri();
			URI dstImgPath=Paths.get(outImgPath, String.valueOf(resourceId)+String.valueOf(size),"1.jpg").toUri();
			String dstImgPathStr=dstImgPath.toString();
			File file=new File(srcImgPath);
//			String path=Paths.get(dstImgPath,String.valueOf( sizes[i]),"1.jpg").toString();

			SGDirectory.EnsureFilePath(dstImgPathStr);

			//方法2. 20秒 (推荐)
			int backW = size; // 1920;
			int backH = size;// 1080;
			SGLine imgLine=SGLine.FitHeightAndCenterHorizontally();
			SGLine backLine=new SGLine(new PFPoint(0, 0), new PFPoint(backW, backH));
			
			SGRef<Canvas> canvasRef = new SGRef<Canvas>(null);
			SGRef<Graphics> ctx1Ref = new SGRef<Graphics>(null);
			BufferedImage paintBi = null;
			BufferedImage image = ImageIO.read(file);
			paintBi = SGDataHelper.backgroundImgInBuffer(
					canvasRef, ctx1Ref, null, 
					new Dimension(backW, backH), image, null,
					backLine,imgLine, 
					//Color.RED
					null
					, false);
			File file2 = new File(dstImgPath);
			ImageIO.write(paintBi, SGPath.getFileExtension(dstImgPathStr), file2);
		}
	}
//-------------------------------作者----------------------
    String[] uploaderNames = new String[] {
            "深海里的鱼", "云中漫步", "星空下的猫", "风一样的男子", "雨夜带刀",
            "书虫小七", "影视收藏家", "资源搬运工", "分享快乐", "逍遥客",
            "追风少年", "静听花开", "月下独酌", "雪落无声", "飞鸟与鱼",
            "青衫故人", "白衣卿相", "梦里不知身是客", "此间少年", "南风知我意",
            "北冥有鱼", "东篱把酒", "西窗烛", "南山南", "北海北",
            "江湖故人", "红尘客栈", "天涯过客", "孤舟蓑笠翁", "独钓寒江雪"
};
	private String getAuthor(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = ((hash << 5) - hash) + str.charAt(i);
            hash |= 0;
        }
        int idx= Math.abs(hash) % uploaderNames.length;
		return uploaderNames[idx];
	}
    
}

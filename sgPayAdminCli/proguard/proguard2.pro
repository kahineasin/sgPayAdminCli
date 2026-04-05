-injars       ../target/sgPayAdminCli-0.0.1-jar-with-dependencies.jar
-outjars      ../target/sgPayAdminCli-0.0.1-fusion.jar  
-libraryjars  D:/Program Files/Java/jdk1.8.0_201/jre/lib/resources.jar  
-libraryjars  D:/Program Files/Java/jdk1.8.0_201/jre/lib/rt.jar  
-libraryjars  D:/Program Files/Java/jdk1.8.0_201/jre/lib/jsse.jar  
-libraryjars  D:/Program Files/Java/jdk1.8.0_201/jre/lib/jce.jar  
-libraryjars  D:/Program Files/Java/jdk1.8.0_201/jre/lib/charsets.jar  
-libraryjars  D:/Program Files/Java/jdk1.8.0_201/jre/lib/ext/dnsns.jar  
-libraryjars  D:/Program Files/Java/jdk1.8.0_201/jre/lib/ext/localedata.jar  
-libraryjars  D:/Program Files/Java/jdk1.8.0_201/jre/lib/ext/sunjce_provider.jar   
-dontwarn
-keepattributes *Annotation*
-keepattributes Signature
-printmapping proguard.map  
-overloadaggressively  
-repackageclasses ''  
# -allowaccessmodification #deepseek说会主动改变访问权限,必须去掉...  

-keep class com.fasterxml.jackson.** { *; }
-keepclassmembers class com.fasterxml.jackson.** { *; }

# 保留Jackson核心功能
-keep class com.fasterxml.jackson.core.** { *; }
-keepclassmembers class com.fasterxml.jackson.core.** { *; }

# 保留Jackson注解处理相关类和方法
-keep class com.fasterxml.jackson.annotation.** { *; }
-keepclassmembers class com.fasterxml.jackson.annotation.** { *; }


# === picocli 配置 ===
# 1. 保留核心库
-keep class picocli.** { *; }
-keep interface picocli.** { *; }

# 2. 保留注解字段（支持 @Option, @Parameters 等）
-keepclassmembers class * {
    @picocli.CommandLine$Option *;
    @picocli.CommandLine$Parameters *;
    @picocli.CommandLine$Command *;
    @picocli.CommandLine$ArgGroup *;
    @picocli.CommandLine$Mixin *;
}

# 3. 保留必需的Java属性，确保反射正常工作
-keepattributes Signature, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, AnnotationDefault

# 可选：如果仍遇到问题，可临时添加此规则辅助排查
# -keep class * {
#     @picocli.CommandLine$Option <fields>;
# }


-keep class com.bea.xml.stream.** { *; }
-keep class javax.xml.stream.** { *; }
-keepattributes Signature, RuntimeVisibleAnnotations, AnnotationDefault
-keepclassmembers class * {
    @javax.xml.stream.* <methods>;
}
-keepdirectories META-INF/services/

-keeppackagenames aavax.**
-keeppackagenames org.apache.poi.**
-keep class org.apache.poi.** { *; }
-keep class org.openxmlformats.** { *; }

# 保留 jce 包下的所有类，防止被混淆或移除
-keep class com.jcraft.jsch.jce.** { *; }
-keep class com.jcraft.jsch.DHEC256 { *; }
-keep class com.jcraft.jsch.* {public *;}

# 1. 保留 XMLBeans 核心包
-keep class org.apache.xmlbeans.** { *; }
-keep class schemaorg_apache_xmlbeans.** { *; }
-keep class com.bea.xml.** { *; }

# 2. 保留 POI 及其相关包（作为保障，避免后续问题）
-keep class org.apache.poi.** { *; }
-keep class org.openxmlformats.** { *; }

# 3. 抑制因依赖缺失可能产生的警告
# -dontwarn org.apache.xmlbeans.**
# -dontwarn org.apache.poi.**

# 4. 保留必需的 Java 属性，确保反射和泛型正常
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod


-keep class com.sellgirl.sgJavaHelper.config.SGDataHelper {public *;}
-keep class com.sellgirl.sgJavaHelper.file.SGEncryptByte {public *;}
-keep class com.sellgirl.sgJavaHelper.file.SGDecryptByte {public *;}
-keep class com.sellgirl.sgJavaHelper.file.SGFileSplit {public *;}
-keep class com.sellgirl.sgJavaHelper.file.SGFileMerge {public *;}
-keep class com.sellgirl.sgJavaHelper.file.SGDirectorySplit {public *;}
-keep class com.sellgirl.sgJavaHelper.sql.SGJdbc {public *;}
-keep enum com.sellgirl.sgJavaHelper.sql.SGSqlType{*;}
-keep class com.sellgirl.sellgirlPayService.product.model.ResourceType {public *;}
-keep class org.apache.http.client.methods.HttpRequestBase {public *;}
-keep class org.apache.http.** { *; }

-keep class com.sellgirl.sellgirlPayService.** implements com.sellgirl.sgJavaHelper.ISGUnProGuard{*;}
-keep class com.sellgirl.sgPayAdminCli.** implements com.sellgirl.sgJavaHelper.ISGUnProGuard{*;}
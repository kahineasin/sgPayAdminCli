package com.sellgirl.sgPayAdminCli;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.sellgirl.sgJavaHelper.SGDate;

/**
 * Hello world!
 *
 */
public class PayAdminCliApp 
{
    public static void main( String[] args )
    {
    	TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
    	TimeZone.setDefault(timeZone);
    	
 	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String dateStr = sdf.format(Calendar.getInstance().getTime());
        System.out.println("PfTransferTaskAppTest__"+ dateStr);
        System.out.println("PfTransferTaskAppTest__"+ SGDate.Now().toString());
    }
}

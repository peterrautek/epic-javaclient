package org.mobilesynergies.epic.client;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Log {
	
	public static void d(String tag, String msg){
		System.out.println("["+tag+"] "+msg);
	}

	public static void e(String tag, String msg) {
		System.out.println("["+tag+"] "+msg);
		
	}

	public static void w(String tag, String msg) {
		System.out.println("["+tag+"] "+msg);
		
	}

	public static void w(String tag, String msg, Exception e) {
		System.out.println("["+tag+"] "+msg+ "(Exception: "+e.getMessage()+")");
		
	}

	public static String getStackTraceString(RuntimeException runtimeException) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
     * Handy function to get a loggable stack trace from a Throwable
     * @param tr An exception to log
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

}

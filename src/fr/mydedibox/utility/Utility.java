package fr.mydedibox.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

public class Utility 
{
	public static String TAG = "EmuFrontend";
	
	public static void setTAG( final String pTag )
	{
		TAG = pTag;
	}
	
	public static String getTAG()
	{
		return TAG;
	}
	
	public static void log( final String pString )
	{
		Log.d( TAG, pString );
	}
	
	public static void loge( final String pString )
	{
		Log.e( TAG, pString );
	}
	
	public static String dumpPrefs(Context pContext) 
	{
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(pContext);
        String ret = "Preferences:\n";
        for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) 
        {
            Object val = entry.getValue();
            if (val == null) 
            {
            	ret += String.format("%s = <null>%n", entry.getKey());
            } 
            else 
            {
            	ret +=String.format("%s = %s (%s)%n", entry.getKey(), String.valueOf(val), val.getClass().getSimpleName());
            }
        }
        return ret;
    }
	
	public static void getLogsToFile(Context pContext)
	{

		String deviceInfo = "BOARD: " + android.os.Build.BOARD + "\n"
				+ "BRAND: " + android.os.Build.BRAND + "\n"
				+ "CPU_ABI: " + android.os.Build.CPU_ABI + "\n"
				+ "DEVICE: " + android.os.Build.DEVICE + "\n"
				+ "DISPLAY: " + android.os.Build.DISPLAY + "\n"
				+ "FINGERPRINT: " + android.os.Build.FINGERPRINT + "\n"
				+ "HARDWARE: " + android.os.Build.HARDWARE + "\n"
				+ "HOST: " + android.os.Build.HOST + "\n"
				+ "ID: " + android.os.Build.ID + "\n"
				+ "MODEL: " + android.os.Build.MODEL + "\n"
				+ "PRODUCT: " + android.os.Build.PRODUCT + "\n"
				+ "CODENAME: " + android.os.Build.VERSION.CODENAME + "\n"
				+ "RELEASE: " + android.os.Build.VERSION.RELEASE + "\n"
				+ "SDK_INT: " + android.os.Build.VERSION.SDK_INT + "\n";
		
		File dest = new File( Environment.getExternalStorageDirectory(), "device_logs.txt" );
		dest.delete();
		try 
		{
			FileWriter writer = new FileWriter( dest );
			writer.append( deviceInfo );
			writer.flush();
	        writer.close();
		} 
		catch (IOException e) 
		{
			loge( "unable to get device information" );
		}
		
		dest = new File( Environment.getExternalStorageDirectory(), "neodroid_logs.txt" );
		dest.delete();
		try 
		{
			Process process = Runtime.getRuntime().exec("logcat -d NeoDroid:V *:S");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			FileWriter writer = new FileWriter( dest );
			while ((line = bufferedReader.readLine()) != null)
			{
				writer.append( line );
				writer.append( "\n" );
			}
			writer.flush();
	        writer.close();
		} 
		catch (IOException e) 
		{
			loge( "unable to get logs" );
		}
		
		dest = new File( Environment.getExternalStorageDirectory(), "all_logs.txt" );
		dest.delete();
		try 
		{
			Process process = Runtime.getRuntime().exec("logcat -d");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			FileWriter writer = new FileWriter( dest );
			while ((line = bufferedReader.readLine()) != null)
			{
				writer.append( line );
				writer.append( "\n" );
			}
			writer.flush();
	        writer.close();
		} 
		catch (IOException e) 
		{
			loge( "unable to get logs" );
		}
	}

	public static void sendLogsMail( Activity pActivity )
	{
		getLogsToFile( pActivity );
		
		ArrayList<Uri> uris = new ArrayList<Uri>();
		//uris.add( Uri.fromFile( new File( Environment.getExternalStorageDirectory(), "preferences_logs.txt" ) ) );
		uris.add( Uri.fromFile( new File( Environment.getExternalStorageDirectory(), "device_logs.txt" ) ) );
		uris.add( Uri.fromFile( new File( Environment.getExternalStorageDirectory(), "neodroid_logs.txt" ) ) );
		uris.add( Uri.fromFile( new File( Environment.getExternalStorageDirectory(), "all_logs.txt" ) ) );
		
		Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
		i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "cpasjuste@gmail.com" } );
		i.putExtra(Intent.EXTRA_SUBJECT, TAG + " -> BUG");
		i.putExtra(Intent.EXTRA_TEXT, "This is a " + TAG + " bug report, you can review the attached files before submitting and/or add a note here.");
		i.putExtra( Intent.EXTRA_STREAM, uris );
		i.setType( "text/plain" );
		pActivity.startActivity(Intent.createChooser(i, "Send mail"));
	}
	
	public static String formatFileSize( final long size ) 
	{
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	public static String formatSecondes( final int pSecs )
	{
		int hours = pSecs / 3600,
		remainder = pSecs % 3600,
		minutes = remainder / 60,
		seconds = remainder % 60;
	
		return ( (hours < 10 ? "0" : "") + hours
				+ ":" + (minutes < 10 ? "0" : "") + minutes
				+ ":" + (seconds< 10 ? "0" : "") + seconds );
	}
	
	public static int parseInt( String pString )
	{
		int ret = 0;
		try
		{
			ret = Integer.parseInt( pString );
		}
		catch(NumberFormatException e) {}
		return ret;
	}
	
	public static long parseLong( String pString )
	{
		long ret = 0;
		try
		{
			ret = Long.parseLong( pString );
		}
		catch(NumberFormatException e) {}
		return ret;
	}
	
	public static boolean parseBoolean( String pString )
	{
		boolean ret = false;
		try
		{
			ret = Boolean.parseBoolean( pString );
		}
		catch(NumberFormatException e) {}
		return ret;
	}
}


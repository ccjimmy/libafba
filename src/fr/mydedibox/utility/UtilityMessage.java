package fr.mydedibox.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

public class UtilityMessage 
{
	public ProgressDialog dialog;
	
	private Activity activity;
	
	private String message;
	private int progress_max = 100;
	private int progress_now = 0;
	private boolean indeterminate = true;
	public boolean isShowing = false;
	
	public UtilityMessage ( Activity pActivity )
	{
		activity = pActivity;
		
		message = new String( "Please Wait" );
		
		dialog = new ProgressDialog( activity );
		dialog.setTitle( Utility.getTAG() );
		dialog.setMessage( message );
		dialog.setIndeterminate( true );
		dialog.setCancelable( false );
	}
	
	public void updateDialog()
	{
		Runnable r = new Runnable()
        {
        	public void run()
        	{
				try
				{
					if( dialog != null )
					{
						dialog.setIndeterminate( indeterminate );
						
						if( ! indeterminate )
							dialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
						else
							dialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
						
						dialog.setProgress( progress_now );
						dialog.setMax( progress_max );
						
						if( message == null )
							message = new String( "Please Wait" );
						
						if( message.length() <= 0 )
							message = "Please Wait";
						
						if( isShowing ) 
						{
							dialog.setMessage( message );
							if(!dialog.isShowing() )
								dialog.show();
						}
						else
							dialog.hide();
					}
				}
				catch( Exception e )
				{
					Utility.loge( e.toString() );
				}
        	}
    	};
    	activity.runOnUiThread( r );
	}
	
	public void show( final String pMessage, final int pNow, final int pMax )
	{
		message = pMessage;
		progress_max = pMax;
		progress_now = pNow;
		indeterminate = false;
		isShowing = true;
		
		updateDialog();
	}
	
	public void show( final String pMessage )
	{
		message = pMessage;
		indeterminate = true;
		isShowing = true;
		
		updateDialog();
	}
	
	public void hide()
	{
		indeterminate = true;
		isShowing = false;
		
		updateDialog();
	}
	
	public void showToastMessageLong( final String pMessage )
	{
		Toast.makeText( activity, pMessage, Toast.LENGTH_LONG ).show();
	}
	
	public void showToastMessageShort( final String pMessage )
	{
		Toast.makeText( activity, pMessage, Toast.LENGTH_SHORT ).show();
	}
	
	public void showMessageInfo( final String pMessage )
    {
    	Runnable r = new Runnable()
        {
        	public void run()
        	{
        		Log.i( Utility.getTAG(), pMessage );
            	
            	new AlertDialog.Builder( activity )
                .setTitle( "Info" )
                .setMessage( pMessage )
                .setPositiveButton( "Ok", new DialogInterface.OnClickListener() 
                {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .create().show();
        	}
    	};
    	activity.runOnUiThread( r );
    }
	
	public void showMessageError( final String pMessage )
    {
    	Runnable r = new Runnable()
        {
        	public void run()
        	{
        		Log.e( Utility.getTAG(), pMessage );
            	
            	new AlertDialog.Builder( activity )
                .setTitle( "Error" )
                .setMessage( pMessage )
                .setPositiveButton( "Ok", new DialogInterface.OnClickListener() 
                {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .create().show();
        	}
    	};
    	activity.runOnUiThread( r );
    }
	
	public void showMessageErrorExit( final String pMessage )
    {
    	Runnable r = new Runnable()
        {
        	public void run()
        	{
        		Log.e( Utility.getTAG(), pMessage );
            	
            	new AlertDialog.Builder( activity )
                .setTitle( "Error" )
                .setMessage( pMessage )
                .setCancelable( false )
                .setPositiveButton( "Ok", new DialogInterface.OnClickListener() 
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    { 
                    	activity.finish();
                    }
                })
                .create().show();
        	}
    	};
    	activity.runOnUiThread( r );
    }
}


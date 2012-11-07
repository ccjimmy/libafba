package fr.mydedibox.libafba;

import fr.mydedibox.libafba.input.HardwareInput;
import fr.mydedibox.utility.Utility;
import fr.mydedibox.utility.EmuPreferences;
import fr.mydedibox.utility.UtilityMessage;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;

public class ActivityPreferences extends PreferenceActivity implements OnPreferenceClickListener, 
																		OnSharedPreferenceChangeListener, OnKeyListener
{
	public static final String KEY_USEHARDWAREBUTTON = "usehardware";
	public static final String KEY_MAP_BUTTONS =  "map_hardware_key";
	public static final String KEY_DOWNLOAD_PREVIEWS = "previewsdownload";
	
	public static final String KEY_CUSTOMIZE_OSD2 = "customizeosd2";
	public static final String KEY_CUSTOMIZE_OSD3 = "customizeosd3";
	public static final String KEY_CUSTOMIZE_OSD4 = "customizeosd4";
	public static final String KEY_CUSTOMIZE_OSD6 = "customizeosd6";
	
	private UtilityMessage mMessage;
	private EmuPreferences mPreferences;
	private AlertDialog mInputDialog;

	private int mButtonNow = 0;
	private boolean mButtonsEdit = false;
	
	@Override
	public void onCreate( Bundle pSavedInstanceState ) 
	{ 
		super.onCreate( pSavedInstanceState ); 	
		
		//if( Utility.TAG.contentEquals( "CPSEmu" ) )
		//	addPreferencesFromResource( R.xml.cps2prefs );
		//else if( Utility.TAG.contentEquals( "NeoDroid" ) )
		//	addPreferencesFromResource( R.xml.neoprefs );
		
		addPreferencesFromResource( R.xml.preferences );
		
		mMessage = new UtilityMessage( this );
		
		mPreferences = new EmuPreferences( this );
		mPreferences.getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
		
		final Preference pref1 = findPreference( KEY_MAP_BUTTONS );
		if( pref1 != null )
			pref1.setOnPreferenceClickListener( this );
		
		final Preference pref2 = findPreference( KEY_DOWNLOAD_PREVIEWS );
		if( pref2 != null )
			pref2.setOnPreferenceClickListener( this );
		
		final Preference pref3 = findPreference( KEY_CUSTOMIZE_OSD2 );
		if( pref3 != null )
		{
			if( Utility.TAG.contentEquals( "NeoDroid" ) )
				pref3.setEnabled( false );
			else
				pref3.setOnPreferenceClickListener( this );
		}
		
		final Preference pref4 = findPreference( KEY_CUSTOMIZE_OSD3 );
		if( pref4 != null )
		{
			if( Utility.TAG.contentEquals( "NeoDroid" ) )
				pref4.setEnabled( false );
			else
				pref4.setOnPreferenceClickListener( this );
		}
		
		final Preference pref5 = findPreference( KEY_CUSTOMIZE_OSD4 );
		if( pref5 != null )
		{
			pref5.setOnPreferenceClickListener( this );
		}
		
		final Preference pref6 = findPreference( KEY_CUSTOMIZE_OSD6 );
		if( pref6 != null )
		{
			if( Utility.TAG.contentEquals( "NeoDroid" ) )
				pref6.setEnabled( false );
			else
				pref6.setOnPreferenceClickListener( this );
		}
	}
	
	 @Override     
	 protected void onResume()
	 {
	     super.onResume();    
	 }
	 
	 @Override
	 protected void onPause()
	 {         
	     super.onPause();          
	 }
	 
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	    super.onConfigurationChanged(newConfig);
	    
	    //if( mMessage.isShowing )
	    	//mMessage.reset();
	}
	
	@Override
	public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key )
	{
		/* TODO: gngeo 
		if( key.contentEquals( "system" ) )
		{
			String system = mNeoPrefs.getSystem();
			if( system.contentEquals( "home" ) )
				mMessage.showMessageInfo( "You selected home system, if you have some problems, be sure to have the bios \"aes-bios.bin\" in your neogeo.zip file." );
			else if( system.contentEquals( "unibios" ) )
				mMessage.showMessageInfo( "You selected unibios system, if you have some problems, be sure to have the file \"uni-bios.rom\" in your roms directory." );
		}
		*/
	}
	
	@Override
	public boolean onPreferenceClick( Preference preference ) 
	{
		String key = preference.getKey();
		if( key.contentEquals( KEY_MAP_BUTTONS ) )
		{
			showInputDialog();
			return true;
		}
		/*
		else if( key.contentEquals( KEY_DOWNLOAD_PREVIEWS) )
		{
			//Intent i = new Intent( Intent.ACTION_VIEW );
			//i.setData( Uri.parse( "http://android.mydedibox.fr/cpsemu/" ) );
			//startActivity( Intent.createChooser( i, "Choose a browser" ) );
			Utility.log( "Downloading and extracting previews" );
			mMessage.showDialogWait( "Please wait while downloading previews" );
			final UtilityExtractRaw raw = new UtilityExtractRaw( this );
			new File( mPreferences.getDataPath() + "/previews" ).mkdirs();
			raw.add( "previews", mPreferences.getDataPath() + "/previews/previews.zip" );
			
			new Thread( new Runnable()
			{
				@Override
				public void run() 
				{
					raw.extract();
					String zipFilename = mPreferences.getDataPath() + "/previews/previews.zip"; 
					String unzipLocation = mPreferences.getDataPath() + "/previews/"; 
					UtilityDecompressZip d = new UtilityDecompressZip(zipFilename, unzipLocation); 
					d.unzip(); 
					new File( mPreferences.getDataPath() + "/previews/previews.zip" ).delete();
			        mMessage.hideDialog();
				}
			}).start();
			return true;
		}
		else if( key.contentEquals( KEY_CUSTOMIZE_OSD2 ) )
		{
			if( android.os.Build.VERSION.SDK_INT >=11  )
			{
				Intent i = new Intent( ActivityPreferences.this, ActivitySoftwareInputViewEdit.class );
				i.putExtra( "buttons", 2 );
				startActivity( i );
			}
			else
			{
				mMessage.showMessageInfo( "Sorry, custom gamepad configuration is only available on android 3.0+ devices" );
			}
			return true;
		}
		else if( key.contentEquals( KEY_CUSTOMIZE_OSD3 ) )
		{
			if( android.os.Build.VERSION.SDK_INT >=11  )
			{
				Intent i = new Intent( ActivityPreferences.this, ActivitySoftwareInputViewEdit.class );
				i.putExtra( "buttons", 3 );
				startActivity( i );
			}
			else
			{
				mMessage.showMessageInfo( "Sorry, custom gamepad configuration is only available on android 3.0+ devices" );
			}
			return true;
		}
		else if( key.contentEquals( KEY_CUSTOMIZE_OSD4 ) )
		{
			if( android.os.Build.VERSION.SDK_INT >=11  )
			{
				Intent i = new Intent( ActivityPreferences.this, ActivitySoftwareInputViewEdit.class );
				i.putExtra( "buttons", 4 );
				startActivity( i );
			}
			else
			{
				mMessage.showMessageInfo( "Sorry, custom gamepad configuration is only available on android 3.0+ devices" );
			}
			return true;
		}
		else if( key.contentEquals( KEY_CUSTOMIZE_OSD6 ) )
		{
			if( android.os.Build.VERSION.SDK_INT >=11  )
			{
				Intent i = new Intent( ActivityPreferences.this, ActivitySoftwareInputViewEdit.class );
				i.putExtra( "buttons", 6 );
				startActivity( i );
			}
			else
			{
				mMessage.showMessageInfo( "Sorry, custom gamepad configuration is only available on android 3.0+ devices" );
			}
			return true;
		}
		*/
		return false;
	}
	
	private void showInputDialog(  )
	{	
		mButtonsEdit = true;
		mButtonNow = 0;
		mInputDialog = new AlertDialog.Builder( this )
        .setTitle( "Map hardware keys" )
        .setMessage( "\nPlease press a button for: " + HardwareInput.ButtonKeys.get( mButtonNow ) + "\n" )
        .create();

		mInputDialog.setOnKeyListener( new OnKeyListener()
		{
			@Override
			public boolean onKey( DialogInterface dialog, int keyCode, KeyEvent event) 
			{
				if( event.getAction() == KeyEvent.ACTION_DOWN )
				{
					mPreferences.setPad( HardwareInput.ButtonKeys.get( mButtonNow ), keyCode );
					mButtonNow++;
					if( mButtonNow < HardwareInput.ButtonKeys.size() )
					{
						mInputDialog.setMessage( "\nPlease press a button for: " + HardwareInput.ButtonKeys.get( mButtonNow ) + "\n" );
					}
					else
					{
						dialog.dismiss();
						mButtonsEdit = false;
					}
				}
				return true;
			}
		});
		mInputDialog.show();
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
	{
		if( mButtonsEdit )
			return true;
		
		return false;
	}
}

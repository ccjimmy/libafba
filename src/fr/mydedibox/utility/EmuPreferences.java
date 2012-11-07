package fr.mydedibox.utility;

import java.io.File;

import fr.mydedibox.libafba.effects.EffectList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.preference.PreferenceManager;

public class EmuPreferences 
{
	public static String ROM_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EmuFrontend/roms";
	public static String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EmuFrontend";
	//public static String PREVIEW_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EmuFrontend/previews";
	//public static String STATE_PATH = DATA_PATH;
	
	private final Context mCtx;
	private SharedPreferences mPrefs;
	private SharedPreferences.Editor mEditor;
	
	final static public int  CONTROL_DIGITAL = 1;
	final static public int  CONTROL_ANALOG_FAST = 2;
	
	public EmuPreferences( Context pCtx ) 
	{
		this.mCtx = pCtx;
		this.mPrefs = PreferenceManager.getDefaultSharedPreferences(pCtx);
		this.mEditor = this.mPrefs.edit();
	}
	
	public SharedPreferences getSharedPreferences()
	{
		return this.mPrefs;
	}
	
	public boolean licenceRead()
	{
		return this.mPrefs.getBoolean( "licenceread", false );
	}
	
	public void setLicenceRead( boolean pValue )
	{
		this.mEditor.putBoolean( "licenceread", pValue );
		this.mEditor.commit();
	}
	
	/*
	public boolean updatePrefs( )
	{
		int thisversion = 0;
		int savedversion = Utility.parseInt( this.mPrefs.getString( "version", "0" ) );	
		
		try 
		{
			PackageInfo packageInfo = this.mCtx.getPackageManager().getPackageInfo( this.mCtx.getPackageName() ,0 );
			thisversion = packageInfo.versionCode;
			Utility.log( "Version " + thisversion );
		} 
		catch ( NameNotFoundException e )
		{
			e.printStackTrace();
		}
		
		if( savedversion < thisversion )
		{
			Utility.log( "new version installed, preferences need to be cleared" );
			this.mEditor.clear();
			this.mEditor.commit();
			this.mEditor.putString( "version", Integer.toString(thisversion) );
			this.mEditor.commit();
			return true;
		}
		Utility.log( "Package up to date" );
		return false;
	}
	
	public void setRomsPath( final String pPath )
	{
		this.mEditor.putString( "rompath", pPath );
		this.mEditor.commit();
	}
	
	public String getRomsPath()
	{
		File rompath = new File( this.mPrefs.getString( "rompath", ROM_PATH ) );
		if( !rompath.exists() )
		{
			if( !rompath.mkdirs() )
			{
				Utility.log( "Could not create rom path, reseting to: " + ROM_PATH );
				this.mEditor.putString( "rompath", ROM_PATH );
				this.mEditor.commit();
				return ROM_PATH;
			}
			this.mEditor.putString( "rompath", rompath.getAbsolutePath() );
			this.mEditor.commit();
		}
		return rompath.getAbsolutePath();
	}
	
	public String getCachePath()
	{
		return getRomsPath() + "/cache";
	}

	public void setDataPath( final String pPath )
	{
		this.mEditor.putString( "datapath", pPath );
		this.mEditor.commit();
	}
	public String getDataPath()
	{
		File datapath = new File( this.mPrefs.getString( "datapath", DATA_PATH ) );
		if( !datapath.exists() )
		{
			if( !datapath.mkdirs() )
			{
				Utility.log( "Could not create data path, reseting to: " + DATA_PATH );
				this.mEditor.putString( "datapath", DATA_PATH );
				this.mEditor.commit();
				return DATA_PATH;
			}
			this.mEditor.putString( "datapath", datapath.getAbsolutePath() );
			this.mEditor.commit();
		}
		return datapath.getAbsolutePath();
	}
	
	public String getRom()
	{
		return this.mPrefs.getString( "rom", "" );
	}
	public void setRom( final String pRomName )
	{
		this.mEditor.putString( "rom", pRomName );
		this.mEditor.commit();
	}
	*/
	
	/*
	 * Effects
	 */
	public int getScreenSize()
	{
		return Utility.parseInt( this.mPrefs.getString( "screensize", ""+EffectList.EFFECT_FITSCREEN ) );
	}
	
	public void setScreenSize(final int pScreenSize )
	{
		//this.mEditor.putInt( "screensize", pScreenSize );
		this.mEditor.putString( "screensize", ""+pScreenSize );
		this.mEditor.commit();
	}
	
	public String getEffectFast()
	{
		return this.mPrefs.getString( "effectfast", EffectList.effect_scanlines25_name );
	}
	
	public void setEffectFast( String pEffectName )
	{
		this.mEditor.putString( "effectfast", pEffectName );
		this.mEditor.commit();
	}
	
	public void setFrameSkip( int fskip )
	{
		this.mEditor.putInt( "fskip", fskip );
		this.mEditor.commit();
	}
	
	public int getFrameSkip( )
	{
		return this.mPrefs.getInt( "fskip", 5 );
	}
	public boolean useVibration()
	{
		return this.mPrefs.getBoolean( "vibrate", true );
	}
	public void useVibration( boolean pValue )
	{
		this.mEditor.putBoolean( "vibrate", pValue );
		this.mEditor.commit();
	}
	
	/*
	 * Hardware controls
	 */
	public boolean useHardwareButtons()
	{
		return this.mPrefs.getBoolean( "usehardware", false );
	}
	public void useHardwareButtons( boolean pValue )
	{
		this.mEditor.putBoolean( "usehardware", pValue );
		this.mEditor.commit();
	}
	public int getPad( final String pKey )
	{
		return Utility.parseInt(this.mPrefs.getString( pKey, "0" ));
	}
	public void setPad( final String pKey, final int pValue )
	{
		this.mEditor.putString( pKey, ""+pValue );
		this.mEditor.commit();
	}
	public int getPadUp()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_up", "19" ));
	}
	public int getPadDown()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_down", "20" ));
	}
	public int getPadLeft()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_left", "21" ));
	}
	public int getPadRight()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_right", "22" ));
	}
	public int getPad1()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_1", "23" ));
	}
	public int getPad2()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_2", "4" ));
	}
	public int getPad3()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_3", "99" ));
	}
	public int getPad4()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_4", "100" ));
	}
	public int getPad5()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_5", "102" ));
	}
	public int getPad6()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_6", "103" ));
	}
	public int getPadCoins()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_coins", "109" ));
	}
	public int getPadStart()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_start", "108" ));
	}
	public int getPadMenu()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_menu", "82" ));
	}
	/*
	public int getPadSwitch()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_switch", "0" ));
	}
	public int getPadCustom1()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_custom_1", "0" ));
	}
	*/
	public int getPadExit()
	{
		return Utility.parseInt(this.mPrefs.getString( "pad_exit", "84" ));
	}
}




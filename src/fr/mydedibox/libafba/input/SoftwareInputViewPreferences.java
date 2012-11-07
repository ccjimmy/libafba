package fr.mydedibox.libafba.input;

import fr.mydedibox.utility.Utility;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;

public class SoftwareInputViewPreferences 
{
	private final Context mCtx;
	private SharedPreferences mPrefs;
	private SharedPreferences.Editor mEditor;
	
	public SoftwareInputViewPreferences( Context pCtx ) 
	{
		this.mCtx = pCtx;
		this.mPrefs = PreferenceManager.getDefaultSharedPreferences(pCtx);
		this.mEditor = this.mPrefs.edit();
	}
	
	public void reset( int pButtonsCount )
	{
		for(int i=0; i<10;i++)
			this.mEditor.remove( "button_pos_"+pButtonsCount+"_"+i );
		this.mEditor.remove( "button_scale_"+pButtonsCount );
		this.mEditor.remove( "stick_scale" );
		this.mEditor.remove( "stickCenter" );
		
		this.mEditor.commit();
	}
	
	public void setButtonsScaleFactor( int pButtonsCount, float pScale )
	{
		this.mEditor.putFloat( "button_scale_"+pButtonsCount, pScale );
		this.mEditor.commit();
	}
	public float getButtonsScaleFactor( int pButtonsCount )
	{
		return this.mPrefs.getFloat( "button_scale_"+pButtonsCount, 1.0f );
	}
	
	public void setStickScaleFactor( float pScale )
	{
		this.mEditor.putFloat( "stick_scale", pScale );
		this.mEditor.commit();
	}
	public float getStickScaleFactor()
	{
		return this.mPrefs.getFloat( "stick_scale", 1.0f );
	}
	
	public void setStickCenter( int x, int y )
	{
		this.mEditor.putString( "stickCenter", x + "x" + y );
		this.mEditor.commit();
	}
	public Point getStickCenter()
	{
		final String pos = this.mPrefs.getString( "stickCenter", "none" );
		if( pos.contains( "none" ) )
			return null;
		
		final String[] split = pos.split( "x" );
		Point p = new Point( Utility.parseInt( split[0] ), Utility.parseInt( split[1] ) );
		return p;
	}
	
	public void setButtonCenter( int pButtonsCount, int pButtonID, int x, int y )
	{
		final String tag = "button_pos_"+pButtonsCount+"_"+pButtonID;
		Utility.log( "SoftwareInputViewPreferences: setButtonCenter: " +  tag + " (" + x + "," + y + ")" );
				
		this.mEditor.putString( tag, x + "x" + y );
		this.mEditor.commit();
	}
	public Point getButtonCenter( int pButtonsCount, int pButtonID )
	{
		final String tag = "button_pos_"+pButtonsCount+"_"+pButtonID;
		final String pos = this.mPrefs.getString( tag, "none" );
		if( pos.contains( "none" ) )
		{
			Utility.log( "SoftwareInputViewPreferences: getButtonCenter: " +  tag + " (none)" );
			return null;
		}
		
		final String[] split = pos.split( "x" );
		Point p = new Point( Utility.parseInt( split[0] ), Utility.parseInt( split[1] ) );
		Utility.log( "SoftwareInputViewPreferences: getButtonCenter: " +  tag + " (" + p.x + "," + p.y + ")" );
		return p;
	}
	
	public void setButtonsCount( int pCount )
	{
		this.mEditor.putInt( "buttonscount", pCount );
		this.mEditor.commit();
	}
	public int getButtonsCount()
	{
		return this.mPrefs.getInt( "buttonscount", 4 ); 
	}
	
	public void setOSDAlpha( int pAlpha )
	{
		//this.mEditor.putInt( "osdalpha", pAlpha );
		this.mEditor.putString( "osdalpha", ""+pAlpha );
		this.mEditor.commit();
	}
	public int getOSDAlpha()
	{
		return Utility.parseInt(this.mPrefs.getString( "osdalpha", "70" ));
	}
	
	public boolean useVibration()
	{
		return this.mPrefs.getBoolean( "vibrations", true );
	}
	public void useVibration( boolean pValue )
	{
		this.mEditor.putBoolean( "vibrations", pValue );
		this.mEditor.commit();
	}
}


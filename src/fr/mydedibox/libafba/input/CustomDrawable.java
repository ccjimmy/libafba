package fr.mydedibox.libafba.input;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.Log;

public class CustomDrawable 
{
	SharedPreferences prefs;
	int resid = -1;
	BitmapDrawable drawable;
	Rect bounds = new Rect();
	Point center = new Point();
	int alpha = 255;
	float scale = 1;
	
	public CustomDrawable( Context pCtx, int pResID )
	{
		prefs = PreferenceManager.getDefaultSharedPreferences( pCtx );
		resid = pResID;
		drawable = (BitmapDrawable)pCtx.getResources().getDrawable( pResID );
		drawable.setBounds( 0, 0, drawable.getBitmap().getWidth(), drawable.getBitmap().getHeight() );
		bounds = drawable.getBounds();
		center.set( bounds.centerX(), bounds.centerY() );
	}

	public boolean setFromPrefs( int pButtonCount )
	{
		Log.v( "CustomDrawable", "setFromPrefs: resid="+resid );
		
		int cx = prefs.getInt( resid+pButtonCount+"_cx", -1 );
		int cy = prefs.getInt( resid+pButtonCount+"_cy", -1 );
		float scale = prefs.getFloat( resid+pButtonCount+"_scale", -1 );
		
		if( cx > -1 && cy > -1 && scale > 0 )
		{
			Log.v( "CustomDrawable", "setFromPrefs: preferences found" );
			setCenter( cx, cy );
			setScale( scale );
			return true;
		}
		return false;
	}
	
	public void saveToPrefs( int pButtonCount )
	{
		prefs.edit().putInt( resid+pButtonCount+"_cx", getCenter().x );
		prefs.edit().putInt( resid+pButtonCount+"_cy", getCenter().y );
		prefs.edit().putFloat( resid+pButtonCount+"_scale", scale );
		prefs.edit().commit();
	}
	
	public void setCenter( int x, int y )
	{
		bounds.set( x-bounds.width()/2, y-bounds.height()/2, x+bounds.width()/2, y+bounds.height()/2 );
		center.set( bounds.centerX(), bounds.centerY() );
		//drawable.setBounds( bounds );
	}
	public void setPosition( int x, int y )
	{
		bounds.set( x, y, x+bounds.width(), y+bounds.height() );
		center.set( bounds.centerX(), bounds.centerY() );
		//drawable.setBounds( bounds );
	}
	
	public void setScale( float _scale )
	{
		Point c = new Point( center );
		scale = _scale;
		bounds.set( bounds.left, bounds.top,
				bounds.left+(int)((float)bounds.width()*scale), bounds.top+(int)((float)bounds.height()*scale) );
		setCenter( c.x, c.y );
		//drawable.setBounds( bounds );
	}
	public float getScale()
	{
		return this.scale;
	}
	public void setVisibility( boolean pVisible )
	{
		this.drawable.setVisible( pVisible, false );
	}
	public Bitmap getBitmap()
	{
		return this.drawable.getBitmap();
	}
	public Rect getBounds()
	{
		return this.bounds;
	}
	public Point getCenter()
	{
		return center;
	}
	public BitmapDrawable getDrawable()
	{
		return this.drawable;
	}
	public int getAlpha()
	{
		return this.alpha;
	}
	public void setAlpha( int _alpha )
	{
		alpha = _alpha;
		this.drawable.setAlpha( alpha );
	}
	public int getWidth()
	{
		return this.bounds.width();
	}
	public int getHeight()
	{
		return this.bounds.height();
	}
}


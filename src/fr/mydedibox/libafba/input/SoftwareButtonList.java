package fr.mydedibox.libafba.input;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class SoftwareButtonList implements IButtons
{
	private ArrayList<SoftwareButtonInfo> mButtons;
	private final SoftwareInputViewPreferences mPreferences;
	private final Context mCtx;
	private int mButtonsCount;
	
	public SoftwareButtonList( final SoftwareInputViewPreferences pPrefs, final Context pCtx, final View pView, int pButtonCount )
	{
		mCtx = pCtx;
		mButtonsCount = pButtonCount;
		mPreferences = pPrefs;
		mButtons = new ArrayList<SoftwareButtonInfo>();
		
		mButtons.add( new SoftwareButtonInfo( pView,  BTN_START ) );
		mButtons.add( new SoftwareButtonInfo( pView,  BTN_COINS ) );
//		mButtons.add( new SoftwareButtonInfo( pView,  BTN_CUSTOM_1 ) );
		
		for( int i=0; i<pButtonCount;i++ )
		{
			SoftwareButtonInfo button = new SoftwareButtonInfo( pView,  i );
			mButtons.add( button );
		}
	}
	
	public void setRects()
	{
		for( int i=0; i<mButtons.size(); i++ )
		{
			mButtons.get(i).image.getGlobalVisibleRect( mButtons.get(i).rect );
		}
	}
	
	public SoftwareButtonInfo getButtonInfo( int pButton )
	{
		for(int i=0; i<mButtons.size(); i++)
		{
			if( mButtons.get(i).id == pButton )
			{
				return mButtons.get(i);
			}
		}
		return null;
	}
	
	public void setCenter( int pButton, int x, int y )
	{
		for(int i=0; i<mButtons.size(); i++)
		{
			final SoftwareButtonInfo button = mButtons.get(i);
			
			if( button.id == pButton )
			{
				final int xRounded = ( x + 16/2 ) / 16 * 16;
				final int yRounded = ( y + 16/2 ) / 16 * 16;
				button.image.setX( xRounded - button.image.getWidth()/2 );
				button.image.setY( yRounded - button.image.getHeight()/2 );
				break;
			}
		}
	}
	
	public void savePosition( int pButtonID )
	{
		for(int i=0; i<mButtons.size(); i++)
		{
			final SoftwareButtonInfo button = mButtons.get(i);
			if( button.id == pButtonID )
			{
				mPreferences.setButtonCenter( mButtonsCount, button.id, (int)button.image.getX(), (int)button.image.getY() );
				break;
			}
		}
	}

	public ArrayList<SoftwareButtonInfo> getButtons()
	{
		return this.mButtons;
	}
	
	public void setAlpha( int pAlpha )
	{
		for( int i=0; i<mButtons.size(); i++ )
			mButtons.get(i).setAlpha( pAlpha );
	}
	
	public void setVisibility( int pVisibility )
	{
		for( int i=0; i<mButtons.size(); i++ )
			mButtons.get(i).setVisibility( pVisibility );
	}
	
	public void setScale( float pScale )
	{
		for( int i=0; i<mButtons.size(); i++ )
		{
			mButtons.get(i).setScale( pScale );
			mPreferences.setButtonsScaleFactor( mButtonsCount, pScale );
		}
	}
}


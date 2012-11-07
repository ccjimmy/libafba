package fr.mydedibox.libafba.input;

import fr.mydedibox.libafba.R;
import fr.mydedibox.utility.Utility;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

public class SoftwareStick implements IButtons
{
	private final View mView;
	private final SoftwareInputViewPreferences mPreferences;
	
	//public BitmapDrawable stick;
	public ImageView stickBottom;
	//public ImageView stick;
	public CustomDrawable stick;
	
	public Rect mStickRect = new Rect();
	public Rect[] mStickRectDirection = new Rect[9]; // Stick touchable rect's for on touch
	public Rect[] mScreenRectDirection = new Rect[9]; // Screen touchable rect's for on move
	public int mStickPointerID = INVALID_POINTER_ID;
	
	public SoftwareStick( final SoftwareInputViewPreferences pPrefs, final View pView )
	{
		this.mPreferences = pPrefs;
		this.mView = pView;
		this.stickBottom = (ImageView)mView.findViewById( R.id.stick_bottom );
		//this.stick = (BitmapDrawable) pView.getContext().getResources().getDrawable( R.drawable.stick );
		
		this.stick = new CustomDrawable( pView.getContext(), R.drawable.stick );//(ImageView)mView.findViewById( R.id.stick );
		
		this.stick.setCenter( this.stickBottom.getDrawable().getBounds().centerX(), 
				this.stickBottom.getDrawable().getBounds().centerY() );
	}
	
	public void setRects()
	{		
		this.stickBottom.getGlobalVisibleRect( mStickRect );
		
		float new_stick_width = mStickRect.width()/2;
		float old_stick_width = this.stick.getWidth();
		this.stick.setScale( new_stick_width/old_stick_width );
		Utility.log( "new_stick_width:"+new_stick_width );
		Utility.log( "old_stick_width:"+old_stick_width );
		Utility.log( "stick.setScale:"+new_stick_width/old_stick_width );
		
		final int width = mStickRect.width()/3;
		
		for(int i=0;i<9;i++)
		{
			if( this.mStickRectDirection[i] == null )
				this.mStickRectDirection[i] = new Rect();
			
			if( this.mScreenRectDirection[i] == null )
				this.mScreenRectDirection[i] = new Rect();
		}
		
		// UP_LEFT
		this.mStickRectDirection[STICK_UP_LEFT].set( mStickRect.left, mStickRect.top, 
													mStickRect.left + width, mStickRect.top + width );
		this.mScreenRectDirection[STICK_UP_LEFT].set( 0, 0, 
													mStickRect.left + width, mStickRect.top + width );
		
		// UP_RIGHT
		this.mStickRectDirection[STICK_UP_RIGHT].set( mStickRect.right - width, mStickRect.top, 
														mStickRect.right, mStickRect.top + width  );
		this.mScreenRectDirection[STICK_UP_RIGHT].set( mStickRect.right - width, 0, 
				this.mView.getWidth(), mStickRect.top + width  );
		
		// DOWN_RIGHT
		this.mStickRectDirection[STICK_DOWN_RIGHT].set( mStickRectDirection[STICK_UP_RIGHT].left, mStickRect.bottom - width, 
													mStickRect.right, mStickRect.bottom );
		this.mScreenRectDirection[STICK_DOWN_RIGHT].set( mStickRectDirection[STICK_UP_RIGHT].left, mStickRect.bottom - width, 
				this.mView.getWidth(), this.mView.getHeight() );
		
		// DOWN_LEFT
		this.mStickRectDirection[STICK_DOWN_LEFT].set( mStickRect.left, mStickRect.bottom - width, 
									mStickRect.left + width, mStickRect.bottom );
		this.mScreenRectDirection[STICK_DOWN_LEFT].set( 0, mStickRect.bottom - width, 
				mStickRect.left + width, this.mView.getHeight() );
		
		
		this.mStickRectDirection[STICK_UP].set( mStickRectDirection[STICK_UP_LEFT].right, mStickRect.top, 
				mStickRectDirection[STICK_UP_RIGHT].left, mStickRectDirection[STICK_UP_RIGHT].bottom );
		this.mScreenRectDirection[STICK_UP].set( mStickRectDirection[STICK_UP_LEFT].right, 0, 
				mStickRectDirection[STICK_UP_RIGHT].left, mStickRectDirection[STICK_UP_RIGHT].bottom );
		
		
		this.mStickRectDirection[STICK_DOWN].set( mStickRectDirection[STICK_DOWN_LEFT].right, mStickRectDirection[STICK_DOWN_LEFT].top, 
				mStickRectDirection[STICK_DOWN_RIGHT].left, mStickRect.bottom );
		this.mScreenRectDirection[STICK_DOWN].set( mStickRectDirection[STICK_DOWN_LEFT].right, mStickRectDirection[STICK_DOWN_LEFT].top, 
				mStickRectDirection[STICK_DOWN_RIGHT].left, this.mView.getHeight() );
		
		
		this.mStickRectDirection[STICK_LEFT].set( mStickRect.left, mStickRectDirection[STICK_UP_LEFT].bottom, 
				mStickRectDirection[STICK_UP_LEFT].right, mStickRectDirection[STICK_DOWN_LEFT].top );
		this.mScreenRectDirection[STICK_LEFT].set( 0, mStickRectDirection[STICK_UP_LEFT].bottom, 
				mStickRectDirection[STICK_UP_LEFT].right, mStickRectDirection[STICK_DOWN_LEFT].top );
		
		
		this.mStickRectDirection[STICK_RIGHT].set( mStickRectDirection[STICK_UP_RIGHT].left, mStickRectDirection[STICK_UP_RIGHT].bottom, 
				mStickRect.right, mStickRectDirection[STICK_DOWN_RIGHT].top );
		this.mScreenRectDirection[STICK_RIGHT].set( mStickRectDirection[STICK_UP_RIGHT].left, mStickRectDirection[STICK_UP_RIGHT].bottom, 
				this.mView.getWidth(), mStickRectDirection[STICK_DOWN_RIGHT].top );
	}
	
	public void setScale( float pScale )
	{
		//this.stickBottom.setScaleX( pScale );
		//this.stickBottom.setScaleY( pScale );

		mPreferences.setStickScaleFactor( pScale );
	}
	
	public void setAlpha( int pAlpha )
	{
		this.stickBottom.setAlpha( pAlpha );
		//stick.setAlpha( pAlpha );
		this.stick.setAlpha( pAlpha );
	}
	
	public void setVisibility( int pVisibility )
	{
		this.stickBottom.setVisibility( pVisibility );
		this.stick.setVisibility( pVisibility == 1 ? true : false );
	}
	
	public void setCenter( int x, int y )
	{
		final int xRounded = ( x + 16/2 ) / 16 * 16;
		final int yRounded = ( y + 16/2 ) / 16 * 16;
		
		//this.stickBottom.setX( xRounded - this.stickBottom.getWidth()/2 );
		//this.stickBottom.setY( yRounded - this.stickBottom.getHeight()/2 );
		
	}
	
	public void setStickBottomCenter( int x, int y )
	{
		this.stick.setCenter(x, y);
	}

	public void savePosition()
	{
		//mPreferences.setStickCenter( (int)this.stickBottom.getX() + (int)(this.stickBottom.getWidth()/2), (int)this.stickBottom.getY() + (int)(this.stickBottom.getHeight()/2) );
	}
	
	public ImageView getView()
	{
		return this.stickBottom;
	}
}

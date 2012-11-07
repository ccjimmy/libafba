package fr.mydedibox.libafba.input;

import fr.mydedibox.libafba.R;
import fr.mydedibox.libafba.activity.Main;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.View.OnTouchListener;

public class SoftwareInputView extends RelativeLayout implements IButtons, OnTouchListener
{
	private static final int INVALID_POINTER_ID = -1;
	
	private boolean mActive = true;
	private boolean mEditMode = false;
	private int mButtonCount = 4;
	
	private final Context mCtx;
	private final View parent;
	private SoftwareButtonList mButtons;
	private SoftwareStick mStick;
	private SoftwareInputViewPreferences mPreferences;
	private Button mScaleButtonUp;
	private Button mScaleButtonDown;
	
	private Vibrator mVibrator = null;
	
	private int mActionPrev = STICK_NONE;
	private int mInputData = 0;
	private int mInputDataLast = 0;
	private boolean mVertical = false;
	
	Paint paint = new Paint();
	
	LinearLayout mEditButtonsLayout;
	
	public SoftwareInputView( View pParent, int pButtonCount, boolean pVibrate, boolean pVertical ) 
	{
		super( pParent.getContext() );
		mCtx = pParent.getContext();
		parent = pParent;
		
		this.setWillNotDraw( false );
		
	
		paint.setARGB(255, 255, 255, 255);
		mPreferences = new SoftwareInputViewPreferences( mCtx );
		
		if( pVibrate )
			this.mVibrator = (Vibrator) mCtx.getSystemService(Context.VIBRATOR_SERVICE);
		
		//this.mEditMode = pEdit;
		this.mButtonCount = pButtonCount;
		
		LayoutInflater layoutInflater = (LayoutInflater)mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int layoutid = R.layout.gamepadview4buttons;
		switch( this.mButtonCount )
		{
			case 6: layoutid = R.layout.gamepadview6buttons; break;
			case 4: layoutid = R.layout.gamepadview4buttons; break;
			case 3: layoutid = R.layout.gamepadview3buttons; break;
			default: layoutid = R.layout.gamepadview2buttons; break;
		}
		layoutInflater.inflate( layoutid, this );
		
		mStick = new SoftwareStick( mPreferences, this );
		mButtons = new SoftwareButtonList( mPreferences, mCtx, this, this.mButtonCount );

		//initEditView();
		
		this.setOnTouchListener( this );
	}


	@Override 
	protected void onLayout( boolean changed, int l, int t, int r, int b )
	{
		Log.v( "SoftwareInputView", "onLayout: " + changed );
		super.onLayout(changed, l, t, r, b);
		//updateLayoutFromPreferences();
		this.setRects();
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	{
		//Log.v( "SoftwareInputView", "onMeasure: " + parent.getWidth() + "x" + parent.getHeight() );
		
		super.onMeasure( widthMeasureSpec, heightMeasureSpec );
		//setMeasuredDimension( 1280, 800 );
		/*
		setMeasuredDimension( widthMeasureSpec, heightMeasureSpec );
		
		if( parent != null )
		{
			Log.v( "SoftwareInputView", "onMeasure: " + parent.getWidth() + "x" + parent.getHeight() );
			setMeasuredDimension( parent.getWidth(), parent.getHeight() );
		}
		*/
	}

	
	@Override
    protected void onDraw( Canvas canvas ) 
	{
		//Log.v( "SoftwareInputView", "onDraw" );
		super.onDraw( canvas );

		canvas.drawBitmap( mStick.stick.getBitmap(), null, mStick.stick.getBounds(), paint );
	}
	
	/*
	private void reset()
	{
		Intent i = new Intent( mCtx, ActivitySoftwareInputViewEdit.class );
		i.putExtra( "buttons", mButtonCount );
		i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP ); 
		mCtx.startActivity( i );
	}
	*/
	/*
	private void updateLayoutFromPreferences()
	{
		if( android.os.Build.VERSION.SDK_INT >=11  )
		{
			// Restore saved positions from preferences
			final Point stickPos = mPreferences.getStickCenter();
			if( stickPos != null )
			{
				mStick.setCenter( stickPos.x, stickPos.y );
				mStick.setScale( mPreferences.getStickScaleFactor() );
			}
			for( int i=0; i<mButtons.getButtons().size(); i++ )
			{
				final Point p = mPreferences.getButtonCenter( this.mButtonCount, mButtons.getButtons().get(i).id );
				if( p != null )
				{
					mButtons.getButtons().get(i).image.setX( p.x );
					mButtons.getButtons().get(i).image.setY( p.y );
				}
				mButtons.getButtons().get(i).setScale( mPreferences.getButtonsScaleFactor( this.mButtonCount ) );
			}
		}
		this.setRects();
	}
	
	private void initEditView()
	{
		mEditButtonsLayout = new LinearLayout( mCtx );
		mEditButtonsLayout.setOrientation(LinearLayout.VERTICAL);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT );
		p.addRule( RelativeLayout.CENTER_HORIZONTAL );
		p.addRule( RelativeLayout.CENTER_VERTICAL );
		mEditButtonsLayout.setLayoutParams( p );
		
		mEditButtonsLayout.setVisibility( View.GONE );
		mScaleButtonUp = new Button( mCtx );
		mScaleButtonUp.setText( "Scale stick +" );
		mScaleButtonUp.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if( mScaleButtonUp.getText().toString().contains( "stick" ) )
				{
					final float scale = mStick.getView().getScaleX();
					if ( scale < 10f )
						mStick.setScale( scale+0.1f );
				}
				else
				{
					final float scale = mButtons.getButtons().get(0).getScale();
					if ( scale < 10f )
						mButtons.setScale( scale+0.1f );
				}
			}
			
		});
		mEditButtonsLayout.addView( mScaleButtonUp );
		
		mScaleButtonDown = new Button( mCtx );
		mScaleButtonDown.setText( "Scale stick -" );
		mScaleButtonDown.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if( mScaleButtonDown.getText().toString().contains( "stick" ) )
				{
					final float scale = mStick.getView().getScaleX();
					if ( scale > 0.1f )
					{
						mStick.setScale( scale-0.1f );
					}
				}
				else
				{
					final float scale = mButtons.getButtons().get(0).getScale();
					if ( scale > 0.1f )
						mButtons.setScale( scale-0.1f );
				}
			}
			
		});
		mEditButtonsLayout.addView( mScaleButtonDown );
		
		Button resetButton = new Button( mCtx );
		resetButton.setText( "Reset" );
		resetButton.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				mPreferences.reset( mButtonCount );
				reset();
			}
			
		});
		mEditButtonsLayout.addView( resetButton );
		this.addView(mEditButtonsLayout);
		setEditMode( false );
	}
	*/
	
	public void setEditMode( boolean enabled )
	{
		this.mEditMode = enabled;
		mEditButtonsLayout.setVisibility( enabled ? View.VISIBLE : View.GONE );
	}

	public void setActive( boolean pValue )
	{
		this.mActive = pValue;
	}
	
	public void setAlpha( int pAlpha )
	{
		if( this.mStick != null )
			this.mStick.setAlpha( pAlpha );
		
		mButtons.setAlpha( pAlpha );
	}
	
	private void setRects()
	{
		// get images rect (size)
		mButtons.setRects();
		mStick.setRects();
		mStick.setStickBottomCenter( mStick.mStickRect.centerX(), mStick.mStickRect.centerY() );
	}
	
	private void handleStick( int event, int x, int y )
	{
		if( this.mEditMode )
		{
			mStick.setCenter( x, y );
			this.setRects();
			return;
		}
		
		final Rect[] rects = ( event == MotionEvent.ACTION_DOWN ? mStick.mStickRectDirection : mStick.mScreenRectDirection );
		
		mInputData &= ~UP_VALUE;
		mInputData &= ~LEFT_VALUE;
		mInputData &= ~DOWN_VALUE;
		mInputData &= ~RIGHT_VALUE;
		
		if( rects[STICK_UP_LEFT].contains( x, y ) )
		{
			//Utility.log( "STICK_UP_LEFT" );
			if( mActionPrev != IButtons.STICK_UP_LEFT )
			{
				//this.mStick.setImageDrawable( this.mStickUPLEFT );
				mStick.setStickBottomCenter( mStick.mStickRectDirection[STICK_UP_LEFT].left+(int)((float)mStick.stick.getWidth()/1.5f),
						mStick.mStickRectDirection[STICK_UP_LEFT].top+(int)((float)mStick.stick.getHeight()/1.5f) );
				invalidate();
				if( mVibrator != null )
					mVibrator.vibrate(15);
			}
			mInputData |= UP_VALUE;
			mInputData |= LEFT_VALUE;
			mActionPrev = IButtons.STICK_UP_LEFT;
		}
		else if( rects[STICK_UP].contains( x, y ) )
		{
			//Utility.log( "STICK_UP" );
			if( mActionPrev != IButtons.STICK_UP )
			{
				//this.mStick.setImageDrawable( this.mStickUP );
				mStick.setStickBottomCenter( mStick.mStickRectDirection[STICK_UP].centerX(), 
						mStick.mStickRectDirection[STICK_UP].top+mStick.stick.getHeight()/2 );
				invalidate();
				if( mVibrator != null )
					mVibrator.vibrate(15);
			}
			mInputData |= UP_VALUE;
			mActionPrev = IButtons.STICK_UP;
		}
		else if( rects[STICK_UP_RIGHT].contains( x, y ) )
		{
			//Utility.log( "STICK_UP_RIGHT" );
			if( mActionPrev != IButtons.STICK_UP_RIGHT )
			{
				//this.mStick.setImageDrawable( this.mStickUPRIGHT );
				mStick.setStickBottomCenter( mStick.mStickRectDirection[STICK_UP_RIGHT].right-(int)((float)mStick.stick.getWidth()/1.5f),
						mStick.mStickRectDirection[STICK_UP_RIGHT].top+(int)((float)mStick.stick.getHeight()/1.5f) );
				invalidate();
				if( mVibrator != null )
					mVibrator.vibrate(15);
			}
			mInputData |= UP_VALUE;
			mInputData |= RIGHT_VALUE;
			mActionPrev = IButtons.STICK_UP_RIGHT;
		}
		else if( rects[STICK_RIGHT].contains( x, y ) )
		{
			//Utility.log( "STICK_RIGHT" );
			if( mActionPrev != IButtons.STICK_RIGHT )
			{
				//this.mStick.setImageDrawable( this.mStickRIGHT );
				mStick.setStickBottomCenter( mStick.mStickRectDirection[STICK_RIGHT].right-mStick.stick.getWidth()/2, 
						mStick.mStickRectDirection[STICK_RIGHT].centerY() );
				invalidate();
				if( mVibrator != null )
					mVibrator.vibrate(15);
			}
			mInputData |= RIGHT_VALUE;
			mActionPrev = IButtons.STICK_RIGHT;
		}
		else if( rects[STICK_DOWN_RIGHT].contains( x, y ) )
		{
			//Utility.log( "STICK_DOWN_RIGHT" );
			if( mActionPrev != IButtons.STICK_DOWN_RIGHT )
			{
				//this.mStick.setImageDrawable( this.mStickDOWNRIGHT );
				mStick.setStickBottomCenter( mStick.mStickRectDirection[STICK_DOWN_RIGHT].right-(int)((float)mStick.stick.getWidth()/1.5f), 
						mStick.mStickRectDirection[STICK_DOWN_RIGHT].bottom-(int)((float)mStick.stick.getHeight()/1.5f) );
				invalidate();
				if( mVibrator != null )
					mVibrator.vibrate(15);
			}
			mInputData |= DOWN_VALUE;
			mInputData |= RIGHT_VALUE;
			mActionPrev = IButtons.STICK_DOWN_RIGHT;
		}
		else if( rects[STICK_DOWN].contains( x, y ) )
		{
			//Utility.log( "STICK_DOWN" );
			if( mActionPrev != IButtons.STICK_DOWN )
			{
				//this.mStick.setImageDrawable( this.mStickDOWN );
				mStick.setStickBottomCenter( mStick.mStickRectDirection[STICK_DOWN].centerX(), 
						mStick.mStickRectDirection[STICK_DOWN].bottom-mStick.stick.getHeight()/2 );
				invalidate();
				if( mVibrator != null )
					mVibrator.vibrate(15);
			}
			mInputData |= DOWN_VALUE;
			mActionPrev = IButtons.STICK_DOWN;
		}
		else if( rects[STICK_DOWN_LEFT].contains( x, y ) )
		{
			//Utility.log( "STICK_DOWN_LEFT" );
			if( mActionPrev != IButtons.STICK_DOWN_LEFT )
			{
				//this.mStick.setImageDrawable( this.mStickDOWNLEFT );
				mStick.setStickBottomCenter( mStick.mStickRectDirection[STICK_DOWN_LEFT].left+(int)((float)mStick.stick.getWidth()/1.5f), 
						mStick.mStickRectDirection[STICK_DOWN_LEFT].bottom-(int)((float)mStick.stick.getHeight()/1.5f) );
				invalidate();
				if( mVibrator != null )
					mVibrator.vibrate(15);
			}
			mInputData |= DOWN_VALUE;
			mInputData |= LEFT_VALUE;
			mActionPrev = IButtons.STICK_DOWN_LEFT;
		}
		else if( rects[STICK_LEFT].contains( x, y ) )
		{
			//Utility.log( "STICK_LEFT" );
			if( mActionPrev != IButtons.STICK_LEFT )
			{
				//this.mStick.setImageDrawable( this.mStickLEFT );
				mStick.setStickBottomCenter( mStick.mStickRectDirection[STICK_LEFT].left + mStick.stick.getWidth()/2, 
						mStick.mStickRectDirection[STICK_LEFT].centerY() );
				invalidate();
				if( mVibrator != null )
					mVibrator.vibrate(15);
			}
			mInputData |= LEFT_VALUE;
			mActionPrev = IButtons.STICK_LEFT;
		}
	}

	@Override
	public boolean onTouch( View v, MotionEvent ev ) 
	{
		if( ! this.mActive )
			return true;
		
        final int action = ev.getAction();
        final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    	final int pointerId = ev.getPointerId(pointerIndex);
    	
        switch ( action & MotionEvent.ACTION_MASK ) 
        {
        	case MotionEvent.ACTION_DOWN:
        	case MotionEvent.ACTION_POINTER_DOWN:
        	{
        		final int x = ( action & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_DOWN ? (int)ev.getX() : (int)ev.getX( pointerIndex );
        		final int y = ( action & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_DOWN ? (int)ev.getY() : (int)ev.getY( pointerIndex );
	        		
        		if( mStick.mStickRect.contains( x, y ) )
        		{
        			if( this.mEditMode )
        			{
        				mScaleButtonUp.setText( "Scale stick +" );
        				mScaleButtonDown.setText( "Scale stick -" );
        			}
        			mStick.mStickPointerID = pointerId;
        			handleStick( MotionEvent.ACTION_DOWN, x, y );
        		}
        		else
        		{
        			for( int i=0; i<mButtons.getButtons().size(); i++ )
        			{
        				if( mButtons.getButtons().get(i).rect.contains( x, y ) )
        				{
        					if( this.mEditMode )
                			{
        						mScaleButtonUp.setText( "Scale buttons +" );
        						mScaleButtonDown.setText( "Scale buttons -" );
                			}
        					mButtons.getButtons().get(i).pointer = pointerId;
        					mInputData |= mButtons.getButtons().get(i).value;
        					if( mVibrator != null )
        						mVibrator.vibrate(15);
        					break;
        				}
        			}
        		}
        		break;
        	}

	    	case MotionEvent.ACTION_MOVE: 
	    	case MotionEvent.ACTION_OUTSIDE:
	    	{
	    		final int index = ev.findPointerIndex( mStick.mStickPointerID );
	    		if( index != INVALID_POINTER_ID )
	    		{
	    			final int x = (int)ev.getX( index );
		    		final int y = (int)ev.getY( index );
		    		handleStick( MotionEvent.ACTION_MOVE, x, y );
	    		}
	    		
	    		if( this.mEditMode )
				{
	    			final int x = ( action & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_MOVE ? (int)ev.getX() : (int)ev.getX( pointerIndex );
	    			final int y = ( action & MotionEvent.ACTION_MASK ) == MotionEvent.ACTION_MOVE ? (int)ev.getY() : (int)ev.getY( pointerIndex );
	        		
	    			for( int i=0; i<mButtons.getButtons().size(); i++ )
        			{
        				if( mButtons.getButtons().get(i).rect.contains( x, y ) )
        				{
							mButtons.setCenter( mButtons.getButtons().get(i).id, x, y );
							this.setRects();
							break;
        				}
        			}	
				}
	    		break;    
	    	}
		        
	    	case MotionEvent.ACTION_UP:
	    	case MotionEvent.ACTION_POINTER_UP:
	    	case MotionEvent.ACTION_CANCEL:
	    	//case MotionEvent.ACTION_OUTSIDE:
	    	{
	    		if( pointerId == mStick.mStickPointerID )
	    		{
	    			//Utility.log( "Stick released" );
	    			//if( this.mActionPrev != IButtons.STICK_NONE )
	    				//this.mStick.setImageDrawable( this.mStickDrawable );
	    			
	    			if( this.mEditMode )
						mStick.savePosition();
	    			else
	    			{
	    				mStick.setStickBottomCenter( mStick.mStickRect.centerX(), mStick.mStickRect.centerY() );
	    				invalidate();
	    			}
	    			
	    			mInputData &= ~UP_VALUE;
	    			mInputData &= ~LEFT_VALUE;
	    			mInputData &= ~DOWN_VALUE;
	    			mInputData &= ~RIGHT_VALUE;
	    			mStick.mStickPointerID = INVALID_POINTER_ID;
	    			this.mActionPrev = IButtons.STICK_NONE;
	    		}

	    		for( int i=0; i<mButtons.getButtons().size(); i++ )
    			{
    				if( pointerId == mButtons.getButtons().get(i).pointer )
    				{
    					mButtons.getButtons().get(i).pointer = INVALID_POINTER_ID;
    					mInputData &= ~mButtons.getButtons().get(i).value;
    					
    					if( this.mEditMode )
    						mButtons.savePosition( mButtons.getButtons().get(i).id );
    					
    					break;
    				}
    			}
	    		break;
	    	}
        }
        
        if( ! this.mEditMode )
        {
        	if( mInputDataLast != mInputData )
        	{
        		mInputDataLast = mInputData;
        		Main.setPadData( 0, mInputData );
        	}
        }
        
		return true;
	}
}


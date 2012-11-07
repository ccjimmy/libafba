package fr.mydedibox.libafba.input;

import fr.mydedibox.libafba.R;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SoftwareButtonInfo implements IButtons
{
	public ImageView image;
	public Rect rect;
	public int value;
	public int id;
	public int pointer = INVALID_POINTER_ID;
	
	public SoftwareButtonInfo( View v, int pButtonNum )
	{
		this.rect = new Rect();
		this.id = pButtonNum;
		
		switch ( pButtonNum )
		{
			case BTN_1:
				value = VALUE_1;
				image = (ImageView)v.findViewById( R.id.button1 );
			break;
			
			case BTN_2:
				value = VALUE_2;
				image = (ImageView)v.findViewById( R.id.button2 );
			break;
			
			case BTN_3:
				value = VALUE_3;
				image = (ImageView)v.findViewById( R.id.button3 );
			break;
			
			case BTN_4:
				value = VALUE_4;
				image = (ImageView)v.findViewById( R.id.button4 );
			break;
			
			case BTN_5:
				value = VALUE_5;
				image = (ImageView)v.findViewById( R.id.button5 );
			break;
			
			case BTN_6:
				value = VALUE_6;
				image = (ImageView)v.findViewById( R.id.button6 );
			break;
			
			case BTN_START:
				value = VALUE_START;
				image = (ImageView)v.findViewById( R.id.buttonstart );
			break;
			
			case BTN_COINS:
				value = VALUE_COINS;
				image = (ImageView)v.findViewById( R.id.buttoncoins );
			break;
			
		/*
			case BTN_CUSTOM_1:
				value = VALUE_CUSTOM_1;
				image = (ImageView)v.findViewById( R.id.buttoncustom1 );
			break;
		*/
		}
	}
	
	public void setAlpha( int pAlpha )
	{
		if( this.image != null )
			this.image.setAlpha( pAlpha );
	}
	
	public void setVisibility( int pVisibility )
	{
		if( this.image != null )
			this.image.setVisibility( pVisibility );
	}
	
	public void setScale( float pScale )
	{
		if( this.image != null )
		{
			this.image.setScaleX( pScale );
			this.image.setScaleY( pScale );
		}
	}
	public float getScale()
	{
		if( this.image != null )
		{
			return this.image.getScaleX();
		}
		return 1f;
	}
}

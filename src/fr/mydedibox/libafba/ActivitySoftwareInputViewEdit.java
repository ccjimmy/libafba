package fr.mydedibox.libafba;

import fr.mydedibox.libafba.input.SoftwareInputView;
import fr.mydedibox.libafba.input.SoftwareInputViewPreferences;
import fr.mydedibox.utility.EmuPreferences;
import fr.mydedibox.utility.UtilityMessage;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public class ActivitySoftwareInputViewEdit extends Activity 
{
	private EmuPreferences mPreferences;
	private SoftwareInputViewPreferences mInputViewPreferences;
	private UtilityMessage mMessage;
	private SoftwareInputView mInputView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mMessage = new UtilityMessage( this );
		mPreferences = new EmuPreferences( this );
		mInputViewPreferences = new SoftwareInputViewPreferences( this );
				
		RelativeLayout mainView = new RelativeLayout(this);
		mainView.setBackgroundColor( Color.WHITE );
        mainView.setLayoutParams( new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT ) );
        
        int buttonscount = this.getIntent().getExtras().getInt( "buttons" );
        mInputView = new SoftwareInputView( mainView, buttonscount, mInputViewPreferences.useVibration(), true );
		mInputView.setAlpha( mInputViewPreferences.getOSDAlpha() );
        mainView.addView( mInputView );
        
        setContentView( mainView );
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if( event.getAction() == KeyEvent.ACTION_DOWN )
	    {
	        switch( keyCode )
	        {
	        	case KeyEvent.KEYCODE_BACK:		
    			break;
	        }
	    }
		return super.onKeyDown( keyCode, event );
	}
	
	@Override 
	public void onResume()
	{
		super.onResume();
	}
	
	@Override 
	public void onDestroy()
	{
		super.onDestroy();
	}
}


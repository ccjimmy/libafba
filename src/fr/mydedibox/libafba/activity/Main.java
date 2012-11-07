package fr.mydedibox.libafba.activity;

import java.lang.reflect.Field;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.mydedibox.libafba.R;
import fr.mydedibox.libafba.StateAdapter;
import fr.mydedibox.libafba.StateInfo;
import fr.mydedibox.libafba.StateList;
import fr.mydedibox.libafba.effects.Effect;
import fr.mydedibox.libafba.effects.EffectList;
import fr.mydedibox.libafba.input.HardwareInput;
import fr.mydedibox.libafba.input.IButtons;
import fr.mydedibox.libafba.input.SoftwareInputView;
import fr.mydedibox.libafba.input.SoftwareInputViewPreferences;
import fr.mydedibox.libafba.sdl.SDLAudio;
import fr.mydedibox.libafba.sdl.SDLJni;
import fr.mydedibox.libafba.sdl.SDLSurface;
import fr.mydedibox.utility.Utility;
import fr.mydedibox.utility.UtilityMessage;
import fr.mydedibox.utility.EmuPreferences;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.view.*;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.os.*;

/**
    SDL Activity
*/
public class Main extends SherlockListActivity implements OnKeyListener
{
    public static Main activity;
    
    private static UtilityMessage mMessage;
 
    public EmuPreferences mPrefs;
    private SoftwareInputViewPreferences inputViewPreferences;
    public Effect effectView;
    public EffectList mEffectList;

    //public MenuView menuView;
    private RelativeLayout mainView;
    private SoftwareInputView inputView;
    private HardwareInput inputHardware;
    public static SDLSurface surfaceView;

    public static int mScreenHolderSizeX = 320;
    public static int buttonCount = 4;
	public static int mScreenHolderSizeY = 240;
	public static int mScreenEmuSizeX = 320;
	public static int mScreenEmuSizeY = 240;
	public static boolean vertical = false;
	public static String[] args = null;
	
	
	private AlertDialog inputHardwareDialog;
	private int inputHardwareButtonNow = 0;
	private boolean inputHardwareEdit = false;
	
	private ActionBar actionBar;
	private Menu menu;
	public ListView stateMenu; //"@+id/android:list"
	public StateAdapter statesAdapter;
	
    // Setup
    protected void onCreate( Bundle savedInstanceState ) 
    {
    	System.loadLibrary( "SDL" );
		System.loadLibrary( "afba" );
		
        super.onCreate( savedInstanceState );
        
        // go fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // force overflow menu
        try 
        {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField( "sHasPermanentMenuKey" );
            if( menuKeyField != null )
            {
                menuKeyField.setAccessible( true );
                menuKeyField.setBoolean( config, false );
            }
        } 
        catch (Exception ex){}
        
        if( this.getIntent().getExtras() != null )
        {
        	vertical = this.getIntent().getExtras().getBoolean( "vertical" );
	        buttonCount = this.getIntent().getExtras().getInt( "buttons" );
	        mScreenHolderSizeX = this.getIntent().getExtras().getInt( "screenW" );
	    	mScreenHolderSizeY = this.getIntent().getExtras().getInt( "screenH" );
	    	mScreenEmuSizeX = mScreenHolderSizeX;
	    	mScreenEmuSizeY = mScreenHolderSizeY;	
	        SDLJni.datapath = this.getIntent().getExtras().getString( "data" );
	        SDLJni.statespath = this.getIntent().getExtras().getString( "states" );
			SDLJni.rompath = this.getIntent().getExtras().getString( "roms" );
			SDLJni.rom = this.getIntent().getExtras().getString( "rom" );
        }
        else
        {
        	vertical = true;
	        buttonCount = 2;
	        mScreenHolderSizeX = 384;
	    	mScreenHolderSizeY = 224;
	    	mScreenEmuSizeX = mScreenHolderSizeX;
	    	mScreenEmuSizeY = mScreenHolderSizeY;
	        SDLJni.datapath = "/mnt/sdcard/aFBA";
	        SDLJni.statespath = "/mnt/sdcard/aFBA/states";
			SDLJni.rompath = "/mnt/sdcard/Emulation/cps2";
			SDLJni.rom = "19xx";
        }
       
        if( vertical )
        {
	        int newWidth = mScreenHolderSizeY;
	        int newHeight = mScreenHolderSizeX;
	        mScreenHolderSizeX  = newWidth;
	        mScreenHolderSizeY = newHeight;
	        mScreenEmuSizeX = newWidth;
	        mScreenEmuSizeY = newHeight;
        }
        
		args = new String[1];
		args[0] = SDLJni.rom;
 
        activity = this;
        actionBar = activity.getSupportActionBar();
        actionBar.hide();
        
        mPrefs = new EmuPreferences(this);
        mMessage = new UtilityMessage(this);
        mEffectList = new EffectList();
        inputHardware = new HardwareInput( this );
        inputViewPreferences = new SoftwareInputViewPreferences( this );
        
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mainView = (RelativeLayout)layoutInflater.inflate( R.layout.emulator, null );

		
        inputView = new SoftwareInputView( mainView, buttonCount, inputViewPreferences.useVibration(), false );
		inputView.setAlpha( inputViewPreferences.getOSDAlpha() );
		mainView.addView( inputView );
        
        setContentView( mainView );

        surfaceView = (SDLSurface)this.findViewById( R.id.SDLSurface );
        surfaceView.setKeepScreenOn( true );
        surfaceView.setOnKeyListener( this );
        
        effectView = new Effect( this );
       	mainView.addView( effectView );
       	
       	stateMenu = (ListView)this.findViewById( android.R.id.list );
		stateMenu.setOnItemClickListener( statesListener );
		stateMenu.setVisibility( View.GONE );
        statesAdapter = new StateAdapter( activity, R.layout.statelist );
        activity.setListAdapter( this.statesAdapter );

		applyRatioAndEffect();  
		
		if( mPrefs.useHardwareButtons() )
			inputView.setVisibility( View.GONE );
		else
		{
			inputView.requestFocus();
			inputView.bringToFront();
		}
    }

    public void updateStatesList()
    {
    	activity.runOnUiThread( new Runnable()
		{
			@Override
			public void run() 
			{
				statesAdapter.clear();
				statesAdapter.add( new StateInfo( getResources().getDrawable( R.drawable.state ) ) );
				
				final StateList statelist = new StateList( SDLJni.statespath, SDLJni.rom );
				for( int i=0; i<statelist.getStates().size(); i++ )
				{
					statesAdapter.add( statelist.getStates().get(i) );
				}
				stateMenu.setVisibility( View.VISIBLE );
				stateMenu.bringToFront();
				stateMenu.setFocusable(true);
				stateMenu.requestFocus();
				//statesView.setOnItemClickListener( statesListener );
			}
		});
    }
    
	private OnItemClickListener statesListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick( AdapterView<?> parent, View v, final int position, final long id ) 
		{
			final StateInfo state = statesAdapter.getItem( (int)id );
			if( state.date.contentEquals( "Create new save" ) )
			{
				final int num = statesAdapter.getCount() - 1;
				Utility.log( "Saving state in slot " + num );
				SDLJni.statesave( num );
				
				updateStatesList();
			}
			else
			{
				//dialogStates( state );
				Utility.log( "Loading state from slot " + state.id );
				SDLJni.stateload( state.id );
				handlePauseMenu();
			}
		}
	};

    @Override
    public boolean onCreateOptionsMenu ( Menu pMenu )
    {
    	getSupportMenuInflater().inflate( R.menu.menu, pMenu );
    	menu = pMenu;
    	menu.findItem( R.id.menu_input_usehw ).setChecked( mPrefs.useHardwareButtons() );
    	menu.findItem( R.id.menu_input_vibrate ).setChecked( mPrefs.useVibration() );
    	updateFskip( mPrefs.getFrameSkip() );
    	return super.onCreateOptionsMenu( menu );
    }
    
    public void updateFskip( int fskip )
    {
    	// handle fskip checkbox's
    	menu.findItem( R.id.menu_fskip_0 ).setChecked( fskip == 0 ? true : false );
    	menu.findItem( R.id.menu_fskip_1 ).setChecked( fskip == 1 ? true : false );
    	menu.findItem( R.id.menu_fskip_2 ).setChecked( fskip == 2 ? true : false );
    	menu.findItem( R.id.menu_fskip_3 ).setChecked( fskip == 3 ? true : false );
    	menu.findItem( R.id.menu_fskip_4 ).setChecked( fskip == 4 ? true : false );
    	menu.findItem( R.id.menu_fskip_5 ).setChecked( fskip == 5 ? true : false );
    	menu.findItem( R.id.menu_fskip_6 ).setChecked( fskip == 6 ? true : false );
    	menu.findItem( R.id.menu_fskip_7 ).setChecked( fskip == 7 ? true : false );
    	menu.findItem( R.id.menu_fskip_8 ).setChecked( fskip == 8 ? true : false );
    	menu.findItem( R.id.menu_fskip_9 ).setChecked( fskip == 9 ? true : false );
    	
    	SDLJni.setfskip( fskip );
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	int pad_data = 0;
    	
        switch (item.getItemId())
        {
        	case R.id.menu_scale:
        		switch ( mPrefs.getScreenSize() )
                {
                	case EffectList.EFFECT_FITSCREEN:
                		mPrefs.setScreenSize( EffectList.EFFECT_FULLSCREEN );
                		break;
                		
                	case EffectList.EFFECT_FULLSCREEN:
                		mPrefs.setScreenSize( EffectList.EFFECT_ORIGINALSCREEN );
                		break;
                		
                	case EffectList.EFFECT_ORIGINALSCREEN:
                		mPrefs.setScreenSize( EffectList.EFFECT_FITSCREEN );
                		break;
                }
        		applyRatioAndEffect();
        		return true;
        	
        	case R.id.menu_effects:
        		selectEffect();
        		return true;
        		
        	case R.id.menu_fskip_0:
        		mPrefs.setFrameSkip( 0 );
        		updateFskip( 0 );
        		return true;
        	case R.id.menu_fskip_1:
        		mPrefs.setFrameSkip( 1 );
        		updateFskip( 1 );
        		return true;
        	case R.id.menu_fskip_2:
        		mPrefs.setFrameSkip( 2 );
        		updateFskip( 2 );
        		return true;
        	case R.id.menu_fskip_3:
        		mPrefs.setFrameSkip( 3 );
        		updateFskip( 3 );
        		return true;
        	case R.id.menu_fskip_4:
        		mPrefs.setFrameSkip( 4 );
        		updateFskip( 4 );
        		return true;
        	case R.id.menu_fskip_5:
        		mPrefs.setFrameSkip( 5 );
        		updateFskip( 5 );
        		return true;
        	case R.id.menu_fskip_6:
        		mPrefs.setFrameSkip( 6 );
        		updateFskip( 6 );
        		return true;
        	case R.id.menu_fskip_7:
        		mPrefs.setFrameSkip( 7 );
        		updateFskip( 7 );
        		return true;
        	case R.id.menu_fskip_8:
        		mPrefs.setFrameSkip( 8 );
        		updateFskip( 8 );
        		return true;
        	case R.id.menu_fskip_9:
        		mPrefs.setFrameSkip( 9 );
        		updateFskip( 9 );
        		return true;
        	
        	case R.id.menu_states:
        		updateStatesList();
        		return true;
        	
        	case R.id.menu_input_vibrate:
        		boolean vibrate = !mPrefs.useVibration();
        		mPrefs.useVibration( vibrate );
            	item.setChecked( vibrate );
        		return true;
        		
        	case R.id.menu_input_usehw:
        		boolean useHardware = !mPrefs.useHardwareButtons();
            	mPrefs.useHardwareButtons( useHardware );
            	inputView.setVisibility( useHardware ? View.GONE : View.VISIBLE );
            	item.setChecked( useHardware );
            	return true;
        	
        	case R.id.menu_input_sethw:
        		showInputHardwareDialog();
        		return true;
        		
        	case R.id.menu_switchs_service:
        		handlePauseMenu();
            	pad_data = 0;
            	pad_data |= IButtons.VALUE_TEST;
            	Main.setPadData( 0, pad_data );
        		return true;
        		
        	case R.id.menu_switchs_reset:
        		handlePauseMenu();
            	pad_data = 0;
            	pad_data |= IButtons.VALUE_RESET;
            	Main.setPadData( 0, pad_data );
        		return true;
        	
        	case R.id.menu_quit:
        		dialogConfirmExit();
        		return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void selectEffect()
    {
    	final CharSequence[] charseq = mEffectList.getCharSequenceList();
		new AlertDialog.Builder(activity)
        .setTitle("Choose an effect")
        .setItems( charseq, new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int which) 
            {
            	Utility.log( "Selected effect: " + charseq[which].toString() );
            	mPrefs.setEffectFast( charseq[which].toString() );
            	applyRatioAndEffect();
            }
        })
        .create().show(); 
    }
    
    public void applyRatioAndEffect()
    {
    	float w = RelativeLayout.LayoutParams.FILL_PARENT, h = RelativeLayout.LayoutParams.FILL_PARENT;
		switch ( mPrefs.getScreenSize() )
        {
        	case EffectList.EFFECT_FITSCREEN:
        		final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        		final float width = display.getWidth();
        		final float height = display.getHeight();
        		final float ratio = ((float)mScreenEmuSizeX/(float)mScreenEmuSizeY);
        		Utility.log( "Display: " + (int)width+"x"+(int)height+ "(ratio:"+ratio+")" );
        		
        		final float scaledw = (float)height * ratio;
        		
        		if( scaledw > width )
        		{
        			w = width;
        			h = (float)w / ratio;
        		}
        		else
        		{
        			w = scaledw;
        			h = display.getHeight();
        		}
        		break;
        		
        	case EffectList.EFFECT_FULLSCREEN:
        		break;
        		
        	case EffectList.EFFECT_ORIGINALSCREEN:
        		w = mScreenEmuSizeX; h = mScreenEmuSizeY;
        		break;
        }
		Utility.log( "View: " + (int)w + "x" + (int)h );
		int orientation = getResources().getConfiguration().orientation;
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams( (int)w, (int)h );
		p.addRule( RelativeLayout.CENTER_HORIZONTAL );
		p.addRule( orientation == Configuration.ORIENTATION_PORTRAIT ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL );
		surfaceView.setLayoutParams( p );
		
		effectView.applyEffect( p, mEffectList.getByName( mPrefs.getEffectFast() ) );
    }
    
    public void dialogConfirmExit( )
	{
    	activity.runOnUiThread( new Runnable()
        {
        	public void run()
        	{
				new AlertDialog.Builder( activity )
				.setTitle( "Confirm" )
				.setMessage( "\nStop emulation ?\n" )
				.setPositiveButton( "Confirm", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						resume();
						activity.finish();
					}
				})
				.setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton){}
				}).create().show();
            }
        });
	}
  
    // Controls
    public void setAnalogData(int i, float x, float y){ }
    public static void setPadData( int i, long data )
    {
    	SDLJni.setPadData(i, data);
    }
    
    @Override
	public boolean onKey(View v, int keyCode, KeyEvent event)
    {
    	if( this.inputHardwareEdit )
			return true;
    	
    	if( !inputView.isShown() && !actionBar.isShowing() )
    		return inputHardware.onKey( v, keyCode, event );
    	
		return false;
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
    	if( this.inputHardwareEdit )
			return true;
    	
	    switch( keyCode )
	    {
	    	case KeyEvent.KEYCODE_SEARCH:
	    		if( mPrefs.useHardwareButtons() )
	    			return true;
	    	
	    	case KeyEvent.KEYCODE_BACK:
	    		return handlePauseMenu();
	    		
	    	case KeyEvent.KEYCODE_MENU:
	    		if( actionBar.isShowing() )
	    			return super.onKeyDown( keyCode, event );
	    		else 
	    			return handlePauseMenu();
	    }
		return super.onKeyDown( keyCode, event );
	}
 
    public boolean handlePauseMenu()
    {
    	if( actionBar.isShowing() )
    	{
    		stateMenu.setVisibility( View.GONE );
    		actionBar.hide();
    		resume();
    	}
    	else
    	{
    		actionBar.show();
    		pause();
    	}
    	
    	return true;
    }
  
    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
    	// ignore orientation/keyboard change
    	super.onConfigurationChanged(newConfig);
    	applyRatioAndEffect();
    }

    @Override
    protected void onPause() 
    {
    	Utility.log("onPause()");
        super.onPause();
        
        pause();
    }
   
    @Override
    protected void onResume() 
    {
    	Utility.log("onResume()");
        super.onResume();

        resume();
    }
    
    @Override
    protected void onDestroy()
    {
    	Utility.log( "onDestroy()" );
    	stop();
        super.onDestroy();
    }
    
    private void showInputHardwareDialog()
	{	
		inputHardwareEdit = true;
		inputHardwareButtonNow = 0;
		inputHardwareDialog = new AlertDialog.Builder( this )
        .setTitle( "Map hardware keys" )
        .setMessage( "\nPlease press a button for: " + HardwareInput.ButtonKeys.get( inputHardwareButtonNow ) + "\n" )
        .create();

		inputHardwareDialog.setOnKeyListener( new DialogInterface.OnKeyListener()
		{
			@Override
			public boolean onKey( DialogInterface dialog, int keyCode, KeyEvent event) 
			{
				if( event.getAction() == KeyEvent.ACTION_DOWN )
				{
					mPrefs.setPad( HardwareInput.ButtonKeys.get( inputHardwareButtonNow ), keyCode );
					inputHardwareButtonNow++;
					if( inputHardwareButtonNow < HardwareInput.ButtonKeys.size() )
					{
						inputHardwareDialog.setMessage( "\nPlease press a button for: " 
														+ HardwareInput.ButtonKeys.get( inputHardwareButtonNow ) + "\n" );
					}
					else
					{
						dialog.dismiss();
						inputHardwareEdit = false;
					}
				}
				return true;
			}
		});
		inputHardwareDialog.show();
	}
    
    public static void resume()
    {
    	if( SDLJni.ispaused() == 0 )
    		return;
    	
    	SDLJni.resumeemu();
    	SDLAudio.play();
    }
    public static void pause()
    {
    	if( SDLJni.ispaused() == 1 )
    		return;
    	
    	SDLJni.pauseemu();
    	SDLAudio.pause();
    }
    public static void stop()
    {
    	if( SDLJni.ispaused() == 1 )
    		resume();
    		
    	SDLJni.emustop();
    	
        // Now wait for the SDL thread to quit
        if ( surfaceView.mSDLThread != null) 
        {
        	Utility.log( "Emulator thread is running, waiting for it to finnish..." );
            try 
            {
            	surfaceView.mSDLThread.join();
            } 
            catch(Exception e) 
            {
            	Utility.loge("Problem stopping thread: " + e);
            }
            surfaceView.mSDLThread = null;
            Utility.log("Finished waiting for emulator thread");
        }
        else
        {
        	Utility.log( "Emulator thread not running" );
        }
        System.gc();
    }
    
    //private static String mErrorMessage = null;
    private static int progress = 0;
    private static int max = 0;
    private static String progressMessage = "Please Wait";
    private static Handler mHandler = new Handler();
    private static Runnable updateProgressBar = new Runnable()
    {
		@Override
		public void run() 
		{
			//mMessage.setDialogProgress( progressMessage, progress );
			mMessage.show( progressMessage, progress, max );
			mHandler.postDelayed( updateProgressBar, 500 );
		}
    };
    
    public static void setErrorMessage( String pMessage ) 
    {
    	Utility.loge( "###setErrorMessage### ==> " + pMessage );
    	Intent i = activity.getIntent();
    	i.putExtra( "error", pMessage );
    	activity.setResult( RESULT_OK, i );
    	activity.finish();
    }
    public static void showProgressBar( String pMessage, int pMax ) 
    {
    	//Utility.loge( "###showProgressBar### ==> " + pMessage );
    	max = pMax;
        mMessage.show( pMessage, 0, pMax );
        mHandler.post( updateProgressBar );
    }
    public static void hideProgressBar() 
    {
    	//Utility.loge( "###hideProgressBar###" );
    	mHandler.removeCallbacks(updateProgressBar);
        mMessage.hide();
    }
    public static void setProgressBar ( String pMessage, int pProgress ) 
    {
    	//Utility.loge( "###setProgressBar### ==> " + pProgress );
    	//progressMessage = pMessage;
    	//progress = pProgress;
    	mMessage.show( pMessage );
    }
    
}

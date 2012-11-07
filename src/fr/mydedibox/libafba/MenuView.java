package fr.mydedibox.libafba;

import fr.mydedibox.libafba.sdl.SDLJni;
import fr.mydedibox.utility.Utility;
import android.app.ListActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class MenuView extends RelativeLayout
{
	public final static int MENU = 0;
	public final static int MENU_INPUT = 1;
	public final static int MENU_DIP = 2;
	public final static int MENU_STATE = 3;
	
	ListActivity activity;
	View parent;
	Context ctx;

	
	public StateAdapter statesAdapter;
	
	// Main menu bar
	public ImageView mainScaleButton; //"@+id/scaleButton"
	public ImageView mainEffectButton; //"@+id/effectButton"
	public ImageView mainInputButton; //"@+id/inputButton"
		public LinearLayout inputMenu; //"@+id/menuInput"
		public TextView inputMenuUseHWButton; //"@+id/useHardwareButtons"
		public TextView inputMenuSetHWButton; //"@+id/setHardwareButtons"
	public ImageView mainDipButton; //"@+id/dipButton"
		public LinearLayout dipMenu; //"@+id/menuDipSwitchs"
		public TextView dipMenuServiceButton; //"@+id/dipServiceButton"
		public TextView dipMenuTestButton; //"@+id/dipTestButton"
		public TextView dipMenuResetButton; //"@+id/dipResetButton"
	public ImageView mainStateButton; //"@+id/statesButton"
		public ListView stateMenu; //"@+id/android:list"
	public ImageView mainExitButton; //"@+id/exitButton"  

	public void setVisibility( int menu, boolean visible )
	{
		switch (menu)
		{
			case MENU:
				inputMenu.setVisibility( View.GONE );
				dipMenu.setVisibility( View.GONE );
				stateMenu.setVisibility( View.GONE );
				this.setVisibility( visible ? View.VISIBLE : View.GONE );
				if( visible )
				{
					this.bringToFront();
					this.requestFocus();
				}
			break;
			
			case MENU_INPUT:
				this.setVisibility( View.VISIBLE );
				inputMenu.setVisibility( visible ? View.VISIBLE : View.GONE  );
				dipMenu.setVisibility( View.GONE );
				stateMenu.setVisibility( View.GONE );
				if( visible )
				{
					inputMenu.bringToFront();
					inputMenu.requestFocus();
				}
				else
				{
					this.bringToFront();
					this.requestFocus();
				}
			break;
			
			case MENU_DIP:
				this.setVisibility( View.VISIBLE );
				inputMenu.setVisibility( View.GONE );
				dipMenu.setVisibility( visible ? View.VISIBLE : View.GONE );
				stateMenu.setVisibility( View.GONE );
				if( visible )
				{
					dipMenu.bringToFront();
					dipMenu.requestFocus();
				}
				else
				{
					this.bringToFront();
					this.requestFocus();
				}
			break;
			
			case MENU_STATE:
				this.setVisibility( View.VISIBLE );
				inputMenu.setVisibility( View.GONE );
				dipMenu.setVisibility( View.GONE );
				stateMenu.setVisibility( visible ? View.VISIBLE : View.GONE );
				if( visible )
				{
					stateMenu.bringToFront();
					stateMenu.requestFocus();
				}
				else
				{
					this.bringToFront();
					this.requestFocus();
				}
			break;
		}
	}
	
	public MenuView( View pParent, ListActivity pActivity ) 
	{
		super( pParent.getContext() );
		ctx = pParent.getContext();
		activity = pActivity;
		init();
	}
	
	public void init()
	{
		LayoutInflater layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate( R.layout.menu, this );
		
		
		mainScaleButton = (ImageView)this.findViewById( R.id.scaleButton );
		mainEffectButton = (ImageView)this.findViewById( R.id.effectButton );
		mainInputButton = (ImageView)this.findViewById( R.id.inputButton );
		inputMenu = (LinearLayout)this.findViewById( R.id.menuInput );
		inputMenuUseHWButton = (TextView)this.findViewById( R.id.useHardwareButtons );
		inputMenuSetHWButton = (TextView)this.findViewById( R.id.setHardwareButtons );
		mainDipButton = (ImageView)this.findViewById( R.id.dipButton );
		dipMenu = (LinearLayout)this.findViewById( R.id.menuDipSwitchs );
		dipMenuServiceButton = (TextView)this.findViewById( R.id.dipServiceButton );
		dipMenuTestButton = (TextView)this.findViewById( R.id.dipTestButton );
		dipMenuResetButton = (TextView)this.findViewById( R.id.dipResetButton );
		
		mainStateButton = (ImageView)this.findViewById( R.id.statesButton );
		stateMenu = (ListView)this.findViewById( android.R.id.list );
		mainExitButton = (ImageView)this.findViewById( R.id.exitButton );
		
		
		inputMenu.setVisibility( View.GONE );
		stateMenu.setOnItemClickListener( statesListener );
		stateMenu.setVisibility( View.GONE );
        statesAdapter = new StateAdapter( activity, R.layout.statelist );
        activity.setListAdapter( this.statesAdapter );
        
		this.setVisibility( View.GONE );

		//TODO: inputViewUseHardwareButtons.setText( mPrefs.useHardwareButtons() ? "Use on screen input" : "Use hardware input" );
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
				Utility.log( "Loading state from slot " + state.id );
				SDLJni.stateload( state.id );
				
				// TODO: handlePauseMenu( true );
			}
		}
	};
}


package fr.mydedibox.libafba;

import java.io.File;
import java.util.ArrayList;


import fr.mydedibox.libafba.input.SoftwareInputViewPreferences;
import fr.mydedibox.utility.Utility;
import fr.mydedibox.utility.UtilityMessage;
import fr.mydedibox.utility.EmuPreferences;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class EmuFrontendActivity extends ListActivity
{
	/*
	public EmuPreferences mPrefs;
	private SoftwareInputViewPreferences mInputViewPreferences;
	public UtilityMessage mMessage;
	
	public CompatibilityList mCompatList = null;
	
	private Filer mFiler;
	private FilesAdapter mFilesAdapter;
	private SearchRomsAdapter mSearchRomsAdapter;
	private TextView mPath;
	private ImageView mPreview;
	private ColorStateList mColorDefault = null;
	private boolean mShowClone = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		//Utility.log( "Starting frontend" );

        super.onCreate( savedInstanceState );
        setContentView( R.layout.filerview );
        
        mMessage = new UtilityMessage( this );
        mPrefs = new EmuPreferences( this );   
        mInputViewPreferences = new SoftwareInputViewPreferences( this );
        
        mPreview = (ImageView)findViewById( R.id.preview );
        
        if( mCompatList == null )
        	mCompatList = new CompatibilityList();
        
        mFiler = new Filer( this, mCompatList );
        mPath = (TextView)findViewById( R.id.filer_path );
        mPath.setText( mPrefs.getRomsPath() );
        
        this.mSearchRomsAdapter = new SearchRomsAdapter( this, R.layout.filerlist );
        updateRomAdapter();
		
        this.mFilesAdapter = new FilesAdapter( this, R.layout.filerlist );
        setListAdapter( this.mFilesAdapter );
        ListView lv = getListView();
        lv.setFastScrollEnabled( true );
        lv.setOnItemClickListener( mFilesListener );
        lv.setOnItemLongClickListener( mFilesLongClickListener );
        lv.setKeepScreenOn( true );
        
        openDir( mPrefs.getRomsPath() );
	}
	
	@Override 
	public void onResume()
	{
		super.onResume();
	}
	
	OnItemClickListener mFilesListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick( AdapterView<?> parent, View v, final int position, final long id ) 
		{
			final FileInfo file = mFilesAdapter.getItem((int)id);
			
			if( file.isDirectory )
			{
				openDir( file.path );
			}
			else if( file.isZIP )
			{
				if( file.isRom )
				{
					String rom = file.rom.nameShort;
					String rompath = file.parent;
					Utility.log( "Opening rom: rompath: " + rompath + " rom:" + rom );
					mPrefs.setRom( rom );
					mPrefs.setRomsPath( rompath );
					if( Utility.getTAG().contentEquals( "PCEEmu" ) )
						mInputViewPreferences.setButtonsCount( 2 );
					else
					{
						if( mCompatList.getRom( file.name ) != null )
							mInputViewPreferences.setButtonsCount( mCompatList.getRom( file.name ).buttons );
					}
				}
				runEmulator( file );
			}
		}
	};
	
	OnItemLongClickListener mFilesLongClickListener = new OnItemLongClickListener() 
	{
		@Override
		public boolean onItemLongClick( AdapterView<?> parent, View v, final int position, final long id ) 
		{
			final FileInfo file = mFilesAdapter.getItem((int)id);
			dialogConfirmDelete( file );
			return true;
		}
	};
	
	OnItemClickListener mSearchRomsListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick( AdapterView<?> parent, View v, final int position, final long id ) 
		{
			final CompatibilityList rom = mSearchRomsAdapter.getItem((int)id);
			
			Intent i = new Intent( Intent.ACTION_VIEW );
			i.setData( Uri.parse( rom.url ) );
			startActivity( Intent.createChooser( i, "Choose a browser" ) );
		}
	};
	
	public void onButtonClone(View v)
	{
		final ImageView img = (ImageView)v;
		
		mShowClone = !mShowClone;
		mMessage.showToastMessageShort( "Clones are " + ( mShowClone ? "enabled" : "disabled" ) );
		
		if( mShowClone )
			img.setImageDrawable( getResources().getDrawable( R.drawable.clone ) );
		else 
			img.setImageDrawable( getResources().getDrawable( R.drawable.noclone ) );
		
		if( this.getListAdapter() == this.mFilesAdapter )
			this.openDir( mPath.getText().toString() );
		else
			this.updateRomAdapter();
	}
	
	public void onPreviewClick(View v)
	{
		mPreview.setVisibility( View.GONE );
	}
	
	public void onButtonSettings(View v)
	{
		startActivity( new Intent( EmuFrontendActivity.this, ActivityPreferences.class ) );
	}
	
	public void onButtonQuit(View v)
	{
		finish();
	}
	
	public void onButtonMail(View v)
	{
		dialogConfirmReportCrash();
	}
	
	public void onButtonSearch(View v)
	{
		final ImageView img = (ImageView)v;
		
		if( this.getListAdapter() == this.mFilesAdapter )
		{
			mMessage.showToastMessageShort( "Showing compatibility list" );
			img.setImageDrawable( getResources().getDrawable( R.drawable.folder ) );
			this.setListAdapter( this.mSearchRomsAdapter );
			this.getListView().setOnItemClickListener( mSearchRomsListener );
		}
		else
		{
			mMessage.showToastMessageShort( "Showing filer" );
			img.setImageDrawable( getResources().getDrawable( R.drawable.search ) );
			this.setListAdapter( this.mFilesAdapter );
			this.getListView().setOnItemClickListener( mFilesListener );
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	    if( event.getAction() == KeyEvent.ACTION_DOWN )
	    {
	        switch( keyCode )
	        {
	        	case KeyEvent.KEYCODE_BACK:
	        		if( mPreview.isShown() )
	        		{
	        			mPreview.setVisibility( View.GONE );
	        			return true;
	        		}
	        		
	        		if( this.getListAdapter() == this.mSearchRomsAdapter )
	        		{
	        			this.setListAdapter( this.mFilesAdapter );
	        			this.getListView().setOnItemClickListener( mFilesListener );
	        			return true;
	        		}
	        		final FileInfo fi = new FileInfo( this, mCompatList, new File( mPath.getText().toString() ) );
    				if( fi.parent == null )
    					return super.onKeyDown( keyCode, event );
    				File top = new File( fi.parent );
    				if( top.isDirectory() && top.canRead() )
    				{
    					openDir( top.getAbsolutePath() );
    					return true;
    				}
    				break;
	        }
	    }
		return super.onKeyDown( keyCode, event );
	}
	
	private class SearchRomsAdapter extends ArrayAdapter<CompatibilityList>
	{
    	public synchronized void add(CompatibilityList object) 
        {
            super.add(object);
        }
        
        public synchronized CompatibilityList getItem(int position) 
        {
            return super.getItem(position);
        }
        
        public synchronized void remove(CompatibilityList object) 
        {
            super.remove(object);
        }
        
        public synchronized void insert(CompatibilityList object, int index) 
        {
            super.insert(object, index);
        }
        
    	public SearchRomsAdapter( Context context, int textViewResourceId )
        {
        	super( context, textViewResourceId );
        }
    	
		@Override
        public View getView( int position, View convertView, ViewGroup parent )
        { 		
        	View v = convertView;
        	if ( v == null )
        	{
        		LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        		v = vi.inflate( R.layout.filerlist, null );
        	}

        	final CompatibilityList rom = getItem(position);
        	if ( rom != null )
        	{
        		final File file = new File ( mPrefs.getRomsPath() + "/" + rom.filename );
        		
        		TextView name = (TextView) v.findViewById( R.id.file_name );
        		if( name != null )
        		{
        			if( mColorDefault == null )
        				mColorDefault = name.getTextColors();
        			
        			name.setText( rom.name );
					if( rom.status == CompatibilityList.WORKING  )
						name.setTextColor( Color.rgb( 0, 204, 0 ) ); // GREEN
					else if( rom.status == CompatibilityList.PARTIAL )
						name.setTextColor( Color.rgb( 255, 140, 0 ) ); // YELLOW
					else
						name.setTextColor( Color.RED );
        		}
        		TextView info = (TextView) v.findViewById( R.id.file_info );
        		if( info != null )
        		{
        			if( mColorDefault == null )
        				mColorDefault = name.getTextColors();
        			
        			String text = rom.year + " - " + rom.filename;
        			if( rom.parent != null )
        				text += " (parent: " + rom.parent + ")";
        			text += "\n";
        			if( rom.status == CompatibilityList.WORKING )
						text += "Working";
					else if( rom.status == CompatibilityList.PARTIAL )
						text += "Partially working. You could maybe have more luck with another dump.";
					else
						text += "Could not get it to work. You could maybe have more luck with another dump.";
        			
        			if( ! file.exists() ) 
        				text += " (Missing rom)";
        				
        			info.setText( text );
        			
        			if( rom.status == CompatibilityList.WORKING && file.exists() )
        				info.setTextColor( mColorDefault );
					else if( rom.status == CompatibilityList.PARTIAL )
						info.setTextColor( Color.rgb( 255, 140, 0 ) ); // YELLOW
					else
						info.setTextColor( Color.RED );
        			
        		}
        		ImageView icon = (ImageView) v.findViewById( R.id.file_icon );
        		if( icon != null )
        		{
        			int rom_resid = 0;
        			if( rom.parent != null )
            		{
            			if( rom.filename.contains( "1944" ) )
        					rom_resid = EmuFrontendActivity.this.getResources().getIdentifier( "aaa1944", "drawable", EmuFrontendActivity.this.getPackageName() );
        				else if( rom.filename.contains( "19xx" ) )
        					rom_resid = EmuFrontendActivity.this.getResources().getIdentifier( "aaa19xx", "drawable", EmuFrontendActivity.this.getPackageName() );
        				else
        					rom_resid = EmuFrontendActivity.this.getResources().getIdentifier( rom.parent, "drawable", EmuFrontendActivity.this.getPackageName() );
            		}
            		else
            		{
	        			if( rom.filename.contentEquals( "2020bb.zip" ) )
	    					rom_resid = EmuFrontendActivity.this.getResources().getIdentifier( "aaa2020bb", "drawable", EmuFrontendActivity.this.getPackageName() );
	    				else if( rom.filename.contentEquals( "3countb.zip" ) )
	    					rom_resid = EmuFrontendActivity.this.getResources().getIdentifier( "aaa3countb", "drawable", EmuFrontendActivity.this.getPackageName() );
	    				else if( rom.filename.contentEquals( "1944.zip" ) )
	    					rom_resid = EmuFrontendActivity.this.getResources().getIdentifier( "aaa1944", "drawable", EmuFrontendActivity.this.getPackageName() );
	    				else if( rom.filename.contentEquals( "19xx.zip" ) )
	    					rom_resid = EmuFrontendActivity.this.getResources().getIdentifier( "aaa19xx", "drawable", EmuFrontendActivity.this.getPackageName() );
	    				else
	    					rom_resid = EmuFrontendActivity.this.getResources().getIdentifier( rom.nameShort, "drawable", EmuFrontendActivity.this.getPackageName() );
            		}
        			
        			if( rom_resid != 0 )
					{
        				final int id = rom_resid;
						icon.setImageResource( rom_resid );
						icon.setOnClickListener( new OnClickListener()
						{
							@Override
							public void onClick(View v) 
							{
								mPreview.setImageResource( id );
								mPreview.setVisibility( View.VISIBLE );
								mPreview.bringToFront();
							}
						});
					}
					else
					{
						File img = null;
    					if( rom.parent != null )
    						img = new File( EmuPreferences.PREVIEW_PATH + "/" + rom.parent + ".png" );
    					else
    						img = new File( EmuPreferences.PREVIEW_PATH + "/" + rom.nameShort + ".png" );
    					
    					
						if( img != null )
						{
							if( img.exists() )
							{
								final Bitmap bitmap = BitmapFactory.decodeFile( img.getAbsolutePath() );
								icon.setImageBitmap(bitmap);
								icon.setOnClickListener( new OnClickListener()
								{
									@Override
									public void onClick(View v) 
									{
										mPreview.setImageBitmap( bitmap );
										mPreview.setVisibility( View.VISIBLE );
										mPreview.bringToFront();
									}
								});
							}
							else
								icon.setImageResource( R.drawable.icon );
						}
						else
							icon.setImageResource( R.drawable.icon );
					}
        		}
        	}
        	return v;
        }
    }
	
	private class FilesAdapter extends ArrayAdapter<FileInfo>
    {
    	public synchronized void add(FileInfo object) 
        {
            super.add(object);
        }
        
        public synchronized FileInfo getItem(int position) 
        {
            return super.getItem(position);
        }
        
        public synchronized void remove(FileInfo object) 
        {
            super.remove(object);
        }
        
        public synchronized void insert(FileInfo object, int index) 
        {
            super.insert(object, index);
        }
        
    	public FilesAdapter( Context context, int textViewResourceId )
        {
        	super( context, textViewResourceId );
        }
    	
		@Override
        public View getView( int position, View convertView, ViewGroup parent )
        { 		
        	View v = convertView;
        	if ( v == null )
        	{
        		LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        		v = vi.inflate( R.layout.filerlist, null );
        	}
               
        	final FileInfo file = getItem(position);
        	if ( file != null )
        	{
        		TextView name = (TextView) v.findViewById( R.id.file_name );
        		if( name != null )
        		{
        			if( mColorDefault == null )
        				mColorDefault = name.getTextColors();
        				
        			if( file.isZIP )
        			{
        				if( file.isRom )
        				{
        					name.setText( file.rom.name );
        					if( file.rom.status == CompatibilityList.WORKING )
        						name.setTextColor( mColorDefault );
        					else if( file.rom.status == CompatibilityList.PARTIAL )
        						name.setTextColor( Color.rgb( 255, 140, 0 ) );
        					else
        						name.setTextColor( Color.RED );
        				}
        				else
        				{
        					name.setTextColor( mColorDefault );
        					name.setText( file.name );
        				}
        			}
        			else if( file.isDirectory )
        			{
        				name.setTextColor( mColorDefault );
        				name.setText( file.name );
        			}
        		}
        		TextView info = (TextView) v.findViewById( R.id.file_info );
        		if( info != null )
        		{
        			if( file.isZIP )
        			{
        				if( file.isRom )
        				{
        					info.setText( file.rom.year + " - " + file.name + " (" + Utility.formatFileSize( file.size ) + ")" );
        				}
        				else
        					info.setText( Utility.formatFileSize( file.size ) );
        			}
        			else if( file.isDirectory )
        				info.setText( file.path + "\n" + file.count+" files" );
        		}
        		ImageView icon = (ImageView) v.findViewById( R.id.file_icon );
        		if( icon != null )
        		{
        			if( file.isZIP )
        			{
        				if( file.isRom )
        				{
        					if( file.rom.resid != 0 )
        					{
        						icon.setImageResource( file.rom.resid );
        						icon.setOnClickListener( new OnClickListener()
        						{
        							@Override
        							public void onClick(View v) 
        							{
        								mPreview.setImageResource( file.rom.resid );
        								mPreview.setVisibility( View.VISIBLE );
        								mPreview.bringToFront();
        							}
        						});
        					}
        					else
        					{	// try to load previews from sdcard
        						File img = null;
            					if( file.rom.parent != null )
            						img = new File( EmuPreferences.PREVIEW_PATH + "/" + file.rom.parent + ".png" );
            					else
            						img = new File( EmuPreferences.PREVIEW_PATH + "/" + file.rom.nameShort + ".png" );
            					
        						if( img != null )
        						{
        							if( img.exists() )
        							{
        								final Bitmap bitmap = BitmapFactory.decodeFile( img.getAbsolutePath() );
        								icon.setImageBitmap(bitmap);
        								icon.setOnClickListener( new OnClickListener()
        								{
        									@Override
        									public void onClick(View v) 
        									{
        										mPreview.setImageBitmap( bitmap );
        										mPreview.setVisibility( View.VISIBLE );
        										mPreview.bringToFront();
        									}
        								});
        							}
        							else
        								icon.setImageResource( R.drawable.icon );
        						}
        					}
        				}
        				else
        					icon.setImageResource( R.drawable.icon );
        			}
        			else if( file.isDirectory )
        				icon.setImageResource( R.drawable.folder );
        		}
        	}
        	return v;
        }
    }

	public void runEmulator( final FileInfo pFile )
	{
		Utility.log( "Starting emulator activity" );
		
		runOnUiThread( new Runnable()
		{
			@Override
			public void run() 
			{
				Intent intent = new Intent(EmuFrontendActivity.this, SDLActivity.class);
				//Bundle bundle = new Bundle(); 
				//bundle.putStringArray( "args", pArgs );
				
				EmuFrontendActivity.this.startActivityForResult( intent
																.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
																.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 99 );
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		Utility.log( "onActivityResult: " + resultCode + " - " + requestCode );
		if( resultCode==RESULT_OK && requestCode==99 )
		{
			String msg = data.getStringExtra( "error" );
			mMessage.showMessageError( msg );
		}
	}

	private void updateRomAdapter()
	{
		this.mSearchRomsAdapter.clear();
		
		for( int i=0; i<mCompatList.list.size(); i++ )
		{
			if( !mShowClone && mCompatList.list.get( i ).parent != null )
				continue;
				
			this.mSearchRomsAdapter.add( mCompatList.list.get( i ) );
		}
	}
	
	public void openDir( final String pPath )
	{
		EmuFrontendActivity.this.runOnUiThread( new Runnable()
		{
            public void run()
            {
				mMessage.showDialogWait( "Listing files in " + pPath );
				
				mPath.setText( pPath );
				mFilesAdapter.clear();

				new Thread( new Runnable()
				{
					public void run() 
					{
						final ArrayList<FileInfo> files = mFiler.getFiles( pPath );
						for( int i=0; i<files.size(); i++ )
						{
							//final int j = i;
							final FileInfo file = files.get(i);
							
							if( !mShowClone && file.isRom && file.rom.parent != null )
								continue;
							
							if( file.isFile && !file.isRom )
								continue;
							
							EmuFrontendActivity.this.runOnUiThread( new Runnable()
							{
					            public void run()
					            {
					            	mFilesAdapter.add( file );
					            }
							});
						}
						mMessage.hideDialog();
					}
				}).start();
            }
		}); 
	}
	
	private void dialogConfirmReportCrash( )
	{
    	runOnUiThread( new Runnable()
        {
        	public void run()
        	{
				new AlertDialog.Builder( EmuFrontendActivity.this )
				.setTitle( "Confirm" )
				.setMessage( "\nBefore reporting a bug, please see xda-forums for help.\n" )
				.setPositiveButton( "Send", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						Utility.sendLogsMail( EmuFrontendActivity.this );
					}
				})
				.setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton){}
				}).create().show();
            }
        });
	}
	
	private void dialogConfirmDelete( final FileInfo pFile )
	{
    	runOnUiThread( new Runnable()
        {
        	public void run()
        	{
				new AlertDialog.Builder( EmuFrontendActivity.this )
				.setTitle( "Confirm" )
				.setMessage( "\nDelete \'" + pFile.name + "\' ?\n" )
				.setPositiveButton( "Delete", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						Thread th = new Thread( new Runnable() 
						{
							@Override
							public void run() 
							{
								mMessage.showDialogWait( "\nPlease wait while deleting \'" + pFile.name + "\' ?\n" );
								pFile.delete();
								mMessage.hideDialog();
								openDir( pFile.parent );
							}
						});
						th.start();
					}
				})
				.setNegativeButton( "Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton){}
				}).create().show();
            }
        });
	}
	*/
}



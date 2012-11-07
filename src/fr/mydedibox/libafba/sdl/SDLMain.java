package fr.mydedibox.libafba.sdl;

import android.content.Context;
import fr.mydedibox.libafba.activity.Main;
import fr.mydedibox.utility.Utility;

public class SDLMain implements Runnable 
{
	private final Context ctx;
		
	public SDLMain( final Context pCtx )
	{
		this.ctx = pCtx;
	}

	public void run()
	{
		Utility.log( "Starting emulator thread" );
		Utility.log( Utility.dumpPrefs( ctx ) );
		SDLJni.nativeInitWithArgs( Main.args );
		Utility.log( "emualator thread returned" );
	}
}


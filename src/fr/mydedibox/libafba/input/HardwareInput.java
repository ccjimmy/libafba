package fr.mydedibox.libafba.input;

import java.util.Arrays;
import java.util.List;

import fr.mydedibox.libafba.activity.Main;
import fr.mydedibox.utility.EmuPreferences;

import android.view.KeyEvent;
import android.view.View;

public class HardwareInput implements IButtons
{
	private final Main mActivity;
	private int pad_data = 0;
	private int pad_up;
	private int pad_down;
	private int pad_left;
	private int pad_right;
	private int pad_1;
	private int pad_2;
	private int pad_3;
	private int pad_4;
	private int pad_5;
	private int pad_6;
	private int pad_start;
	private int pad_coins;
	private int pad_menu;
//	private int pad_switch;
//	private int pad_custom_1;
	private int pad_exit;
	
	public HardwareInput( Main pActivity )
	{
		this.mActivity = pActivity;
		final EmuPreferences mPrefs = this.mActivity.mPrefs;
		pad_up = mPrefs.getPadUp();
		pad_down = mPrefs.getPadDown();
		pad_left = mPrefs.getPadLeft();
		pad_right = mPrefs.getPadRight();
		pad_1 = mPrefs.getPad1();
		pad_2 = mPrefs.getPad2();
		pad_3 = mPrefs.getPad3();
		pad_4 = mPrefs.getPad4();
		pad_5 = mPrefs.getPad5();
		pad_6 = mPrefs.getPad6();
		pad_start = mPrefs.getPadStart();
		pad_coins = mPrefs.getPadCoins();
//		pad_switch = mPrefs.getPadSwitch();
//		pad_custom_1 = mPrefs.getPadCustom1();
		pad_menu = mPrefs.getPadMenu();
		pad_exit = mPrefs.getPadExit();
	}

	public boolean onKey( View v, int keyCode, KeyEvent event )
	{
		final boolean pressed = event.getAction() == 0 ? true : false;
		boolean handled = false;
		
		if( pressed && keyCode == pad_menu )
		{
			return mActivity.handlePauseMenu();
		}
		else if( pressed && keyCode == pad_exit )
		{
			mActivity.dialogConfirmExit();
			return true;
		}
		else if( keyCode == pad_up )
		{
			if( pressed )
				pad_data |= UP_VALUE;
			else
				pad_data &= ~UP_VALUE;
			handled = true;
		}
		else if( keyCode == pad_down )
		{
			if( pressed )
				pad_data |= DOWN_VALUE;
			else
				pad_data &= ~DOWN_VALUE;	
			handled = true;
		}
		else if( keyCode == pad_left )
		{
			if( pressed )
				pad_data |= LEFT_VALUE;
			else
				pad_data &= ~LEFT_VALUE;	
			handled = true;
		}
		else if( keyCode == pad_right )
		{
			if( pressed )
				pad_data |= RIGHT_VALUE;
			else
				pad_data &= ~RIGHT_VALUE;	
			handled = true;
		}
		else if( keyCode == pad_1 )
		{
			if( pressed )
				pad_data |= VALUE_1;
			else
				pad_data &= ~VALUE_1;	
			handled = true;
		}
		else if( keyCode == pad_2 )
		{
			if( pressed )
				pad_data |= VALUE_2;
			else
				pad_data &= ~VALUE_2;	
			handled = true;
		}
		else if( keyCode == pad_3 )
		{
			if( pressed )
				pad_data |= VALUE_3;
			else
				pad_data &= ~VALUE_3;	
			handled = true;
		}
		else if( keyCode == pad_4 )
		{
			if( pressed )
				pad_data |= VALUE_4;
			else
				pad_data &= ~VALUE_4;	
			handled = true;
		}
		else if( keyCode == pad_5 )
		{
			if( pressed )
				pad_data |= VALUE_5;
			else
				pad_data &= ~VALUE_5;	
			handled = true;
		}
		else if( keyCode == pad_6 )
		{
			if( pressed )
				pad_data |= VALUE_6;
			else
				pad_data &= ~VALUE_6;	
			handled = true;
		}
		else if( keyCode == pad_start )
		{
			if( pressed )
				pad_data |= VALUE_START;
			else
				pad_data &= ~VALUE_START;	
			handled = true;
		}
		else if( keyCode == pad_coins )
		{
			if( pressed )
				pad_data |= VALUE_COINS;
			else
				pad_data &= ~VALUE_COINS;	
			handled = true;
		}
		/*
		else if( keyCode == pad_switch )
		{
			if( pressed )
				pad_data |= VALUE_SWITCH;
			else
				pad_data &= ~VALUE_SWITCH;	
			handled = true;
		}
		else if( keyCode == pad_custom_1 )
		{
			if( pressed )
				pad_data |= VALUE_CUSTOM_1;
			else
				pad_data &= ~VALUE_CUSTOM_1;
			handled = true;
		}
		*/
		Main.setPadData( 0, pad_data );
		return handled;
	}
	
	public static List<String> ButtonKeys = Arrays.asList(
		"pad_up",
		"pad_down",
		"pad_left",
		"pad_right",
		"pad_1",
		"pad_2",
		"pad_3",
		"pad_4",
		"pad_5",
		"pad_6",
		"pad_start",
		"pad_coins",
		"pad_menu"
//		"pad_switch",
//		"pad_custom_1"
	);
}


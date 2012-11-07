package fr.mydedibox.libafba.sdl;

import fr.mydedibox.utility.Utility;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class SDLAudio 
{
	private static Thread mAudioThread;
    private static AudioTrack mAudioTrack;
    private static Object buf;
    private static int audioBufSize;
    
    public static Object init( int rate, boolean is16Bit, boolean isStereo, int desiredFrames ) 
    {
    	int channels = isStereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : 
									AudioFormat.CHANNEL_CONFIGURATION_MONO;
    	int encoding = is16Bit ? AudioFormat.ENCODING_PCM_16BIT :
									AudioFormat.ENCODING_PCM_8BIT;
   
    	//int frameSize = (isStereo ? 2 : 1) * (is16Bit ? 2 : 1);
    	//audioBufSize = desiredFrames * frameSize;
    	audioBufSize = desiredFrames;
    	int bufSize = audioBufSize;
    	
    	if( mAudioTrack == null )
		{
    		Utility.log( "audioInit: requested frames: " + desiredFrames );
			Utility.log( "audioInit: requested buffer size: " + audioBufSize );
				
			if( AudioTrack.getMinBufferSize( rate, channels, encoding ) > audioBufSize )
			{
				Utility.log( "audioInit: getMinBufferSize > audioBufSize" );
				bufSize = AudioTrack.getMinBufferSize( rate, channels, encoding );
				Utility.log( "audioInit: new bufSize: " + bufSize );
			}
			
			mAudioTrack = new AudioTrack( AudioManager.STREAM_MUSIC,
											rate,
											channels,
											encoding,
											bufSize,
											AudioTrack.MODE_STREAM );
			start();
			
			buf = is16Bit ? new short[audioBufSize] : new byte[audioBufSize];
		}
		return buf;
    }
    
    /*
    public static Object audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) 
    {
        int channelConfig = isStereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int audioFormat = is16Bit ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
        int frameSize = (isStereo ? 2 : 1) * (is16Bit ? 2 : 1);
        
        Utility.log("Audio: wanted " + (isStereo ? "stereo" : "mono") + " " + (is16Bit ? "16-bit" : "8-bit") + " " + ((float)sampleRate / 1000f) + "kHz, " + desiredFrames + " frames buffer");
        
        // Let the user pick a larger buffer if they really want -- but ye
        // gods they probably shouldn't, the minimums are horrifyingly high
        // latency already
        mAudioCps2Buffer = desiredFrames;
        mAudioFbaBuffer = desiredFrames;
        
        desiredFrames = Math.max(desiredFrames, (AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat) + frameSize - 1) / frameSize);
        
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                channelConfig, audioFormat, desiredFrames * frameSize, AudioTrack.MODE_STREAM);
      
        audioStartThread();
       
        Utility.log("Audio: got " + ((mAudioTrack.getChannelCount() >= 2) ? "stereo" : "mono") + " " + ((mAudioTrack.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) ? "16-bit" : "8-bit") + " " + ((float)mAudioTrack.getSampleRate() / 1000f) + "kHz, " + desiredFrames + " frames buffer");
        
        if (is16Bit) {
            buf = new short[desiredFrames * (isStereo ? 2 : 1)];
        } else {
            buf = new byte[desiredFrames * (isStereo ? 2 : 1)]; 
        }
        
        short[] size = (short[])buf;
        
        Utility.log( "buf size: " + size.length );
        return buf;
    }
    */
    
    public static void start() 
    {
        mAudioThread = new Thread(new Runnable() 
        {
            public void run() 
            {
                mAudioTrack.play();
                SDLJni.nativeRunAudioThread();
            }
        });
        
        // I'd take REALTIME if I could get it!
        mAudioThread.setPriority(Thread.MAX_PRIORITY);
        mAudioThread.start();
    }
 
    public static void writeShortBuffer( short[] buffer ) 
    {
    	//Utility.log( "audioWriteShortBuffer:" + buffer.length );
    	//mAudioTrack.write( buffer, 0, audioBufSize );
    	
    	/*
    	int len = buffer.length;
    	if( mCPS2 )
    		len = mAudioCps2Buffer;
    		//len = buffer.length/4;
    	else if( mFBA )
    		len = audioBufSize;
  		*/
    	
    	
        for ( int i = 0; i < audioBufSize; ) 
        {
            int result = mAudioTrack.write( buffer, i, audioBufSize - i );
            if( result > 0 ) 
            {
                i += result;
            } 
            else if( result == 0 ) 
            {
                try 
                {
                    Thread.sleep(1);
                } 
                catch(InterruptedException e) 
                {
                }
            } 
            else 
            {
                //Utility.log("SDL audio: error return from write(short)");
                return;
            }
        }
    	
        //mAudioTrack.flush();
        //buffer = null;
    }
    
    public static void writeByteBuffer(byte[] buffer) 
    {
        for (int i = 0; i < buffer.length; ) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    // Nom nom
                }
            } else {
                Utility.log("SDL audio: error return from write(short)");
                return;
            }
        }
    }
    
    public static void quit() 
    {
    	Utility.log("audioQuit");
    	
        if (mAudioThread != null) 
        {
        	Utility.log("AudioThread != null");
            try 
            {
                mAudioThread.join();
            } 
            catch(Exception e) 
            {
                Utility.loge("Problem stopping audio thread: " + e);
            }
            mAudioThread = null;
            Utility.log("Finished waiting for audio thread");
        }
        else
        	Utility.log("mAudioThread == null");  
        
        if (mAudioTrack != null) 
        {
        	Utility.log("mAudioTrack != null");
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
        else
        {
        	Utility.log("mAudioTrack == null");
        }
    }
    
    public static void pause()
    {
    	if( mAudioTrack != null )
    		mAudioTrack.pause();
    }
    
    public static void play()
    {
    	if( mAudioTrack != null )
    		mAudioTrack.play();
    }
}

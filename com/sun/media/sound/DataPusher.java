package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public final class DataPusher
  implements Runnable
{
  private static final int AUTO_CLOSE_TIME = 5000;
  private static final boolean DEBUG = false;
  private final SourceDataLine source;
  private final AudioFormat format;
  private final AudioInputStream ais;
  private final byte[] audioData;
  private final int audioDataByteLength;
  private int pos;
  private int newPos = -1;
  private boolean looping;
  private Thread pushThread = null;
  private int wantedState;
  private int threadState;
  private final int STATE_NONE = 0;
  private final int STATE_PLAYING = 1;
  private final int STATE_WAITING = 2;
  private final int STATE_STOPPING = 3;
  private final int STATE_STOPPED = 4;
  private final int BUFFER_SIZE = 16384;
  
  public DataPusher(SourceDataLine paramSourceDataLine, AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt)
  {
    this(paramSourceDataLine, paramAudioFormat, null, paramArrayOfByte, paramInt);
  }
  
  public DataPusher(SourceDataLine paramSourceDataLine, AudioInputStream paramAudioInputStream)
  {
    this(paramSourceDataLine, paramAudioInputStream.getFormat(), paramAudioInputStream, null, 0);
  }
  
  private DataPusher(SourceDataLine paramSourceDataLine, AudioFormat paramAudioFormat, AudioInputStream paramAudioInputStream, byte[] paramArrayOfByte, int paramInt)
  {
    source = paramSourceDataLine;
    format = paramAudioFormat;
    ais = paramAudioInputStream;
    audioDataByteLength = paramInt;
    audioData = (paramArrayOfByte == null ? null : Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length));
  }
  
  public synchronized void start()
  {
    start(false);
  }
  
  public synchronized void start(boolean paramBoolean)
  {
    try
    {
      if (threadState == 3) {
        stop();
      }
      looping = paramBoolean;
      newPos = 0;
      wantedState = 1;
      if (!source.isOpen()) {
        source.open(format);
      }
      source.flush();
      source.start();
      if (pushThread == null) {
        pushThread = JSSecurityManager.createThread(this, null, false, -1, true);
      }
      notifyAll();
    }
    catch (Exception localException) {}
  }
  
  public synchronized void stop()
  {
    if ((threadState == 3) || (threadState == 4) || (pushThread == null)) {
      return;
    }
    wantedState = 2;
    if (source != null) {
      source.flush();
    }
    notifyAll();
    int i = 50;
    while ((i-- >= 0) && (threadState == 1)) {
      try
      {
        wait(100L);
      }
      catch (InterruptedException localInterruptedException) {}
    }
  }
  
  synchronized void close()
  {
    if (source != null) {
      source.close();
    }
  }
  
  public void run()
  {
    byte[] arrayOfByte = null;
    int i = ais != null ? 1 : 0;
    if (i != 0) {
      arrayOfByte = new byte['䀀'];
    } else {
      arrayOfByte = audioData;
    }
    while (wantedState != 3) {
      if (wantedState == 2)
      {
        try
        {
          synchronized (this)
          {
            threadState = 2;
            wantedState = 3;
            wait(5000L);
          }
        }
        catch (InterruptedException ???) {}
      }
      else
      {
        if (newPos >= 0)
        {
          pos = newPos;
          newPos = -1;
        }
        threadState = 1;
        ??? = 16384;
        if (i != 0)
        {
          try
          {
            pos = 0;
            ??? = ais.read(arrayOfByte, 0, arrayOfByte.length);
          }
          catch (IOException localIOException)
          {
            ??? = -1;
          }
        }
        else
        {
          if (??? > audioDataByteLength - pos) {
            ??? = audioDataByteLength - pos;
          }
          if (??? == 0) {
            ??? = -1;
          }
        }
        if (??? < 0)
        {
          if ((i == 0) && (looping))
          {
            pos = 0;
          }
          else
          {
            wantedState = 2;
            source.drain();
          }
        }
        else
        {
          int j = source.write(arrayOfByte, pos, ???);
          pos += j;
        }
      }
    }
    threadState = 3;
    source.flush();
    source.stop();
    source.flush();
    source.close();
    threadState = 4;
    synchronized (this)
    {
      pushThread = null;
      notifyAll();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\DataPusher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
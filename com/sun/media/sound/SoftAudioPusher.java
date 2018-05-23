package com.sun.media.sound;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public final class SoftAudioPusher
  implements Runnable
{
  private volatile boolean active = false;
  private SourceDataLine sourceDataLine = null;
  private Thread audiothread;
  private final AudioInputStream ais;
  private final byte[] buffer;
  
  public SoftAudioPusher(SourceDataLine paramSourceDataLine, AudioInputStream paramAudioInputStream, int paramInt)
  {
    ais = paramAudioInputStream;
    buffer = new byte[paramInt];
    sourceDataLine = paramSourceDataLine;
  }
  
  public synchronized void start()
  {
    if (active) {
      return;
    }
    active = true;
    audiothread = new Thread(this);
    audiothread.setDaemon(true);
    audiothread.setPriority(10);
    audiothread.start();
  }
  
  public synchronized void stop()
  {
    if (!active) {
      return;
    }
    active = false;
    try
    {
      audiothread.join();
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  public void run()
  {
    byte[] arrayOfByte = buffer;
    AudioInputStream localAudioInputStream = ais;
    SourceDataLine localSourceDataLine = sourceDataLine;
    try
    {
      while (active)
      {
        int i = localAudioInputStream.read(arrayOfByte);
        if (i < 0) {
          break;
        }
        localSourceDataLine.write(arrayOfByte, 0, i);
      }
    }
    catch (IOException localIOException)
    {
      active = false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftAudioPusher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
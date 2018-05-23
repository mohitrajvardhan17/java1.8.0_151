package com.sun.media.sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;

public final class SoftMixingMixer
  implements Mixer
{
  static final String INFO_NAME = "Gervill Sound Mixer";
  static final String INFO_VENDOR = "OpenJDK Proposal";
  static final String INFO_DESCRIPTION = "Software Sound Mixer";
  static final String INFO_VERSION = "1.0";
  static final Mixer.Info info = new Info();
  final Object control_mutex = this;
  boolean implicitOpen = false;
  private boolean open = false;
  private SoftMixingMainMixer mainmixer = null;
  private AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
  private SourceDataLine sourceDataLine = null;
  private SoftAudioPusher pusher = null;
  private AudioInputStream pusher_stream = null;
  private final float controlrate = 147.0F;
  private final long latency = 100000L;
  private final boolean jitter_correction = false;
  private final List<LineListener> listeners = new ArrayList();
  private final Line.Info[] sourceLineInfo = new Line.Info[2];
  
  public SoftMixingMixer()
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 1; i <= 2; i++)
    {
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, 8, i, i, -1.0F, false));
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, 8, i, i, -1.0F, false));
      for (int j = 16; j < 32; j += 8)
      {
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, j, i, i * j / 8, -1.0F, false));
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, j, i, i * j / 8, -1.0F, false));
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0F, j, i, i * j / 8, -1.0F, true));
        localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, -1.0F, j, i, i * j / 8, -1.0F, true));
      }
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, i, i * 4, -1.0F, false));
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 32, i, i * 4, -1.0F, true));
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, i, i * 8, -1.0F, false));
      localArrayList.add(new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, -1.0F, 64, i, i * 8, -1.0F, true));
    }
    AudioFormat[] arrayOfAudioFormat = (AudioFormat[])localArrayList.toArray(new AudioFormat[localArrayList.size()]);
    sourceLineInfo[0] = new DataLine.Info(SourceDataLine.class, arrayOfAudioFormat, -1, -1);
    sourceLineInfo[1] = new DataLine.Info(Clip.class, arrayOfAudioFormat, -1, -1);
  }
  
  public Line getLine(Line.Info paramInfo)
    throws LineUnavailableException
  {
    if (!isLineSupported(paramInfo)) {
      throw new IllegalArgumentException("Line unsupported: " + paramInfo);
    }
    if (paramInfo.getLineClass() == SourceDataLine.class) {
      return new SoftMixingSourceDataLine(this, (DataLine.Info)paramInfo);
    }
    if (paramInfo.getLineClass() == Clip.class) {
      return new SoftMixingClip(this, (DataLine.Info)paramInfo);
    }
    throw new IllegalArgumentException("Line unsupported: " + paramInfo);
  }
  
  public int getMaxLines(Line.Info paramInfo)
  {
    if (paramInfo.getLineClass() == SourceDataLine.class) {
      return -1;
    }
    if (paramInfo.getLineClass() == Clip.class) {
      return -1;
    }
    return 0;
  }
  
  public Mixer.Info getMixerInfo()
  {
    return info;
  }
  
  public Line.Info[] getSourceLineInfo()
  {
    Line.Info[] arrayOfInfo = new Line.Info[sourceLineInfo.length];
    System.arraycopy(sourceLineInfo, 0, arrayOfInfo, 0, sourceLineInfo.length);
    return arrayOfInfo;
  }
  
  public Line.Info[] getSourceLineInfo(Line.Info paramInfo)
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < sourceLineInfo.length; i++) {
      if (paramInfo.matches(sourceLineInfo[i])) {
        localArrayList.add(sourceLineInfo[i]);
      }
    }
    return (Line.Info[])localArrayList.toArray(new Line.Info[localArrayList.size()]);
  }
  
  public Line[] getSourceLines()
  {
    Line[] arrayOfLine;
    synchronized (control_mutex)
    {
      if (mainmixer == null) {
        return new Line[0];
      }
      SoftMixingDataLine[] arrayOfSoftMixingDataLine = mainmixer.getOpenLines();
      arrayOfLine = new Line[arrayOfSoftMixingDataLine.length];
      for (int i = 0; i < arrayOfLine.length; i++) {
        arrayOfLine[i] = arrayOfSoftMixingDataLine[i];
      }
    }
    return arrayOfLine;
  }
  
  public Line.Info[] getTargetLineInfo()
  {
    return new Line.Info[0];
  }
  
  public Line.Info[] getTargetLineInfo(Line.Info paramInfo)
  {
    return new Line.Info[0];
  }
  
  public Line[] getTargetLines()
  {
    return new Line[0];
  }
  
  public boolean isLineSupported(Line.Info paramInfo)
  {
    if (paramInfo != null) {
      for (int i = 0; i < sourceLineInfo.length; i++) {
        if (paramInfo.matches(sourceLineInfo[i])) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean isSynchronizationSupported(Line[] paramArrayOfLine, boolean paramBoolean)
  {
    return false;
  }
  
  public void synchronize(Line[] paramArrayOfLine, boolean paramBoolean)
  {
    throw new IllegalArgumentException("Synchronization not supported by this mixer.");
  }
  
  public void unsynchronize(Line[] paramArrayOfLine)
  {
    throw new IllegalArgumentException("Synchronization not supported by this mixer.");
  }
  
  public void addLineListener(LineListener paramLineListener)
  {
    synchronized (control_mutex)
    {
      listeners.add(paramLineListener);
    }
  }
  
  private void sendEvent(LineEvent paramLineEvent)
  {
    if (listeners.size() == 0) {
      return;
    }
    LineListener[] arrayOfLineListener1 = (LineListener[])listeners.toArray(new LineListener[listeners.size()]);
    for (LineListener localLineListener : arrayOfLineListener1) {
      localLineListener.update(paramLineEvent);
    }
  }
  
  public void close()
  {
    if (!isOpen()) {
      return;
    }
    sendEvent(new LineEvent(this, LineEvent.Type.CLOSE, -1L));
    SoftAudioPusher localSoftAudioPusher = null;
    AudioInputStream localAudioInputStream = null;
    synchronized (control_mutex)
    {
      if (pusher != null)
      {
        localSoftAudioPusher = pusher;
        localAudioInputStream = pusher_stream;
        pusher = null;
        pusher_stream = null;
      }
    }
    if (localSoftAudioPusher != null)
    {
      localSoftAudioPusher.stop();
      try
      {
        localAudioInputStream.close();
      }
      catch (IOException ???)
      {
        ((IOException)???).printStackTrace();
      }
    }
    synchronized (control_mutex)
    {
      if (mainmixer != null) {
        mainmixer.close();
      }
      open = false;
      if (sourceDataLine != null)
      {
        sourceDataLine.drain();
        sourceDataLine.close();
        sourceDataLine = null;
      }
    }
  }
  
  public Control getControl(Control.Type paramType)
  {
    throw new IllegalArgumentException("Unsupported control type : " + paramType);
  }
  
  public Control[] getControls()
  {
    return new Control[0];
  }
  
  public Line.Info getLineInfo()
  {
    return new Line.Info(Mixer.class);
  }
  
  public boolean isControlSupported(Control.Type paramType)
  {
    return false;
  }
  
  /* Error */
  public boolean isOpen()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 353	com/sun/media/sound/SoftMixingMixer:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 350	com/sun/media/sound/SoftMixingMixer:open	Z
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SoftMixingMixer
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void open()
    throws LineUnavailableException
  {
    if (isOpen())
    {
      implicitOpen = false;
      return;
    }
    open(null);
  }
  
  public void open(SourceDataLine paramSourceDataLine)
    throws LineUnavailableException
  {
    if (isOpen())
    {
      implicitOpen = false;
      return;
    }
    synchronized (control_mutex)
    {
      try
      {
        if (paramSourceDataLine != null) {
          format = paramSourceDataLine.getFormat();
        }
        AudioInputStream localAudioInputStream = openStream(getFormat());
        if (paramSourceDataLine == null)
        {
          synchronized (SoftMixingMixerProvider.mutex)
          {
            SoftMixingMixerProvider.lockthread = Thread.currentThread();
          }
          try
          {
            ??? = AudioSystem.getMixer(null);
            if (??? != null)
            {
              Object localObject2 = null;
              AudioFormat localAudioFormat1 = null;
              Line.Info[] arrayOfInfo = ((Mixer)???).getSourceLineInfo();
              for (int k = 0; k < arrayOfInfo.length; k++) {
                if (arrayOfInfo[k].getLineClass() == SourceDataLine.class)
                {
                  DataLine.Info localInfo = (DataLine.Info)arrayOfInfo[k];
                  AudioFormat[] arrayOfAudioFormat = localInfo.getFormats();
                  for (int m = 0; m < arrayOfAudioFormat.length; m++)
                  {
                    AudioFormat localAudioFormat2 = arrayOfAudioFormat[m];
                    if (((localAudioFormat2.getChannels() == 2) || (localAudioFormat2.getChannels() == -1)) && ((localAudioFormat2.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) || (localAudioFormat2.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))) && ((localAudioFormat2.getSampleRate() == -1.0F) || (localAudioFormat2.getSampleRate() == 48000.0D)) && ((localAudioFormat2.getSampleSizeInBits() == -1) || (localAudioFormat2.getSampleSizeInBits() == 16)))
                    {
                      localObject2 = localInfo;
                      int n = localAudioFormat2.getChannels();
                      boolean bool1 = localAudioFormat2.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED);
                      float f = localAudioFormat2.getSampleRate();
                      boolean bool2 = localAudioFormat2.isBigEndian();
                      int i1 = localAudioFormat2.getSampleSizeInBits();
                      if (i1 == -1) {
                        i1 = 16;
                      }
                      if (n == -1) {
                        n = 2;
                      }
                      if (f == -1.0F) {
                        f = 48000.0F;
                      }
                      localAudioFormat1 = new AudioFormat(f, i1, n, bool1, bool2);
                      break label358;
                    }
                  }
                }
              }
              label358:
              if (localAudioFormat1 != null)
              {
                format = localAudioFormat1;
                paramSourceDataLine = (SourceDataLine)((Mixer)???).getLine((Line.Info)localObject2);
              }
            }
            if (paramSourceDataLine == null) {
              paramSourceDataLine = AudioSystem.getSourceDataLine(format);
            }
          }
          finally
          {
            synchronized (SoftMixingMixerProvider.mutex)
            {
              SoftMixingMixerProvider.lockthread = null;
            }
          }
          if (paramSourceDataLine == null) {
            throw new IllegalArgumentException("No line matching " + info.toString() + " is supported.");
          }
        }
        getClass();
        double d = 100000.0D;
        if (!paramSourceDataLine.isOpen())
        {
          i = getFormat().getFrameSize() * (int)(getFormat().getFrameRate() * (d / 1000000.0D));
          paramSourceDataLine.open(getFormat(), i);
          sourceDataLine = paramSourceDataLine;
        }
        if (!paramSourceDataLine.isActive()) {
          paramSourceDataLine.start();
        }
        int i = 512;
        try
        {
          i = localAudioInputStream.available();
        }
        catch (IOException localIOException) {}
        int j = paramSourceDataLine.getBufferSize();
        j -= j % i;
        if (j < 3 * i) {
          j = 3 * i;
        }
        pusher = new SoftAudioPusher(paramSourceDataLine, localAudioInputStream, i);
        pusher_stream = localAudioInputStream;
        pusher.start();
      }
      catch (LineUnavailableException localLineUnavailableException)
      {
        if (isOpen()) {
          close();
        }
        throw new LineUnavailableException(localLineUnavailableException.toString());
      }
    }
  }
  
  public AudioInputStream openStream(AudioFormat paramAudioFormat)
    throws LineUnavailableException
  {
    if (isOpen()) {
      throw new LineUnavailableException("Mixer is already open");
    }
    synchronized (control_mutex)
    {
      open = true;
      implicitOpen = false;
      if (paramAudioFormat != null) {
        format = paramAudioFormat;
      }
      mainmixer = new SoftMixingMainMixer(this);
      sendEvent(new LineEvent(this, LineEvent.Type.OPEN, -1L));
      return mainmixer.getInputStream();
    }
  }
  
  public void removeLineListener(LineListener paramLineListener)
  {
    synchronized (control_mutex)
    {
      listeners.remove(paramLineListener);
    }
  }
  
  /* Error */
  public long getLatency()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 353	com/sun/media/sound/SoftMixingMixer:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: ldc2_w 173
    //   10: aload_1
    //   11: monitorexit
    //   12: lreturn
    //   13: astore_2
    //   14: aload_1
    //   15: monitorexit
    //   16: aload_2
    //   17: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	18	0	this	SoftMixingMixer
    //   5	10	1	Ljava/lang/Object;	Object
    //   13	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	12	13	finally
    //   13	16	13	finally
  }
  
  /* Error */
  public AudioFormat getFormat()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 353	com/sun/media/sound/SoftMixingMixer:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 355	com/sun/media/sound/SoftMixingMixer:format	Ljavax/sound/sampled/AudioFormat;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SoftMixingMixer
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  float getControlRate()
  {
    return 147.0F;
  }
  
  SoftMixingMainMixer getMainMixer()
  {
    if (!isOpen()) {
      return null;
    }
    return mainmixer;
  }
  
  private static class Info
    extends Mixer.Info
  {
    Info()
    {
      super("OpenJDK Proposal", "Software Sound Mixer", "1.0");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftMixingMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
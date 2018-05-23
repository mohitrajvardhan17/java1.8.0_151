package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineUnavailableException;

abstract class AbstractDataLine
  extends AbstractLine
  implements DataLine
{
  private final AudioFormat defaultFormat;
  private final int defaultBufferSize;
  protected final Object lock = new Object();
  protected AudioFormat format;
  protected int bufferSize;
  protected boolean running = false;
  private boolean started = false;
  private boolean active = false;
  
  protected AbstractDataLine(DataLine.Info paramInfo, AbstractMixer paramAbstractMixer, Control[] paramArrayOfControl)
  {
    this(paramInfo, paramAbstractMixer, paramArrayOfControl, null, -1);
  }
  
  protected AbstractDataLine(DataLine.Info paramInfo, AbstractMixer paramAbstractMixer, Control[] paramArrayOfControl, AudioFormat paramAudioFormat, int paramInt)
  {
    super(paramInfo, paramAbstractMixer, paramArrayOfControl);
    if (paramAudioFormat != null) {
      defaultFormat = paramAudioFormat;
    } else {
      defaultFormat = new AudioFormat(44100.0F, 16, 2, true, Platform.isBigEndian());
    }
    if (paramInt > 0) {
      defaultBufferSize = paramInt;
    } else {
      defaultBufferSize = ((int)(defaultFormat.getFrameRate() / 2.0F) * defaultFormat.getFrameSize());
    }
    format = defaultFormat;
    bufferSize = defaultBufferSize;
  }
  
  public final void open(AudioFormat paramAudioFormat, int paramInt)
    throws LineUnavailableException
  {
    synchronized (mixer)
    {
      if (!isOpen())
      {
        Toolkit.isFullySpecifiedAudioFormat(paramAudioFormat);
        mixer.open(this);
        try
        {
          implOpen(paramAudioFormat, paramInt);
          setOpen(true);
        }
        catch (LineUnavailableException localLineUnavailableException)
        {
          mixer.close(this);
          throw localLineUnavailableException;
        }
      }
      else
      {
        if (!paramAudioFormat.matches(getFormat())) {
          throw new IllegalStateException("Line is already open with format " + getFormat() + " and bufferSize " + getBufferSize());
        }
        if (paramInt > 0) {
          setBufferSize(paramInt);
        }
      }
    }
  }
  
  public final void open(AudioFormat paramAudioFormat)
    throws LineUnavailableException
  {
    open(paramAudioFormat, -1);
  }
  
  public int available()
  {
    return 0;
  }
  
  public void drain() {}
  
  public void flush() {}
  
  public final void start()
  {
    synchronized (mixer)
    {
      if ((isOpen()) && (!isStartedRunning()))
      {
        mixer.start(this);
        implStart();
        running = true;
      }
    }
    synchronized (lock)
    {
      lock.notifyAll();
    }
  }
  
  public final void stop()
  {
    synchronized (mixer)
    {
      if ((isOpen()) && (isStartedRunning()))
      {
        implStop();
        mixer.stop(this);
        running = false;
        if ((started) && (!isActive())) {
          setStarted(false);
        }
      }
    }
    synchronized (lock)
    {
      lock.notifyAll();
    }
  }
  
  public final boolean isRunning()
  {
    return started;
  }
  
  public final boolean isActive()
  {
    return active;
  }
  
  public final long getMicrosecondPosition()
  {
    long l = getLongFramePosition();
    if (l != -1L) {
      l = Toolkit.frames2micros(getFormat(), l);
    }
    return l;
  }
  
  public final AudioFormat getFormat()
  {
    return format;
  }
  
  public final int getBufferSize()
  {
    return bufferSize;
  }
  
  public final int setBufferSize(int paramInt)
  {
    return getBufferSize();
  }
  
  public final float getLevel()
  {
    return -1.0F;
  }
  
  final boolean isStartedRunning()
  {
    return running;
  }
  
  final void setActive(boolean paramBoolean)
  {
    synchronized (this)
    {
      if (active != paramBoolean) {
        active = paramBoolean;
      }
    }
  }
  
  final void setStarted(boolean paramBoolean)
  {
    int i = 0;
    long l = getLongFramePosition();
    synchronized (this)
    {
      if (started != paramBoolean)
      {
        started = paramBoolean;
        i = 1;
      }
    }
    if (i != 0) {
      if (paramBoolean) {
        sendEvents(new LineEvent(this, LineEvent.Type.START, l));
      } else {
        sendEvents(new LineEvent(this, LineEvent.Type.STOP, l));
      }
    }
  }
  
  final void setEOM()
  {
    setStarted(false);
  }
  
  public final void open()
    throws LineUnavailableException
  {
    open(format, bufferSize);
  }
  
  public final void close()
  {
    synchronized (mixer)
    {
      if (isOpen())
      {
        stop();
        setOpen(false);
        implClose();
        mixer.close(this);
        format = defaultFormat;
        bufferSize = defaultBufferSize;
      }
    }
  }
  
  abstract void implOpen(AudioFormat paramAudioFormat, int paramInt)
    throws LineUnavailableException;
  
  abstract void implClose();
  
  abstract void implStart();
  
  abstract void implStop();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AbstractDataLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
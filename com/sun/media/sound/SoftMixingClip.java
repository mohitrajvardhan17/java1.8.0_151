package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineUnavailableException;

public final class SoftMixingClip
  extends SoftMixingDataLine
  implements Clip
{
  private AudioFormat format;
  private int framesize;
  private byte[] data;
  private final InputStream datastream = new InputStream()
  {
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      int i = read(arrayOfByte);
      if (i < 0) {
        return i;
      }
      return arrayOfByte[0] & 0xFF;
    }
    
    public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
      throws IOException
    {
      if (_loopcount != 0)
      {
        i = _loopend * framesize;
        j = _loopstart * framesize;
        int k = _frameposition * framesize;
        if ((k + paramAnonymousInt2 >= i) && (k < i))
        {
          int m = paramAnonymousInt1 + paramAnonymousInt2;
          int n = paramAnonymousInt1;
          int i1;
          while (paramAnonymousInt1 != m)
          {
            if (k == i)
            {
              if (_loopcount == 0) {
                break;
              }
              k = j;
              if (_loopcount != -1) {
                SoftMixingClip.access$010(SoftMixingClip.this);
              }
            }
            paramAnonymousInt2 = m - paramAnonymousInt1;
            i1 = i - k;
            if (paramAnonymousInt2 > i1) {
              paramAnonymousInt2 = i1;
            }
            System.arraycopy(data, k, paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
            paramAnonymousInt1 += paramAnonymousInt2;
          }
          if (_loopcount == 0)
          {
            paramAnonymousInt2 = m - paramAnonymousInt1;
            i1 = i - k;
            if (paramAnonymousInt2 > i1) {
              paramAnonymousInt2 = i1;
            }
            System.arraycopy(data, k, paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
            paramAnonymousInt1 += paramAnonymousInt2;
          }
          _frameposition = (k / framesize);
          return n - paramAnonymousInt1;
        }
      }
      int i = _frameposition * framesize;
      int j = bufferSize - i;
      if (j == 0) {
        return -1;
      }
      if (paramAnonymousInt2 > j) {
        paramAnonymousInt2 = j;
      }
      System.arraycopy(data, i, paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
      _frameposition = (_frameposition + paramAnonymousInt2 / framesize);
      return paramAnonymousInt2;
    }
  };
  private int offset;
  private int bufferSize;
  private float[] readbuffer;
  private boolean open = false;
  private AudioFormat outputformat;
  private int out_nrofchannels;
  private int in_nrofchannels;
  private int frameposition = 0;
  private boolean frameposition_sg = false;
  private boolean active_sg = false;
  private int loopstart = 0;
  private int loopend = -1;
  private boolean active = false;
  private int loopcount = 0;
  private boolean _active = false;
  private int _frameposition = 0;
  private boolean loop_sg = false;
  private int _loopcount = 0;
  private int _loopstart = 0;
  private int _loopend = -1;
  private float _rightgain;
  private float _leftgain;
  private float _eff1gain;
  private float _eff2gain;
  private AudioFloatInputStream afis;
  
  SoftMixingClip(SoftMixingMixer paramSoftMixingMixer, DataLine.Info paramInfo)
  {
    super(paramSoftMixingMixer, paramInfo);
  }
  
  protected void processControlLogic()
  {
    _rightgain = rightgain;
    _leftgain = leftgain;
    _eff1gain = eff1gain;
    _eff2gain = eff2gain;
    if (active_sg)
    {
      _active = active;
      active_sg = false;
    }
    else
    {
      active = _active;
    }
    if (frameposition_sg)
    {
      _frameposition = frameposition;
      frameposition_sg = false;
      afis = null;
    }
    else
    {
      frameposition = _frameposition;
    }
    if (loop_sg)
    {
      _loopcount = loopcount;
      _loopstart = loopstart;
      _loopend = loopend;
    }
    if (afis == null)
    {
      afis = AudioFloatInputStream.getInputStream(new AudioInputStream(datastream, format, -1L));
      if (Math.abs(format.getSampleRate() - outputformat.getSampleRate()) > 1.0E-6D) {
        afis = new SoftMixingDataLine.AudioFloatInputStreamResampler(afis, outputformat);
      }
    }
  }
  
  protected void processAudioLogic(SoftAudioBuffer[] paramArrayOfSoftAudioBuffer)
  {
    if (_active)
    {
      float[] arrayOfFloat1 = paramArrayOfSoftAudioBuffer[0].array();
      float[] arrayOfFloat2 = paramArrayOfSoftAudioBuffer[1].array();
      int i = paramArrayOfSoftAudioBuffer[0].getSize();
      int j = i * in_nrofchannels;
      if ((readbuffer == null) || (readbuffer.length < j)) {
        readbuffer = new float[j];
      }
      int k = 0;
      try
      {
        k = afis.read(readbuffer);
        if (k == -1)
        {
          _active = false;
          return;
        }
        if (k != in_nrofchannels) {
          Arrays.fill(readbuffer, k, j, 0.0F);
        }
      }
      catch (IOException localIOException) {}
      int m = in_nrofchannels;
      int n = 0;
      int i1 = 0;
      while (n < i)
      {
        arrayOfFloat1[n] += readbuffer[i1] * _leftgain;
        n++;
        i1 += m;
      }
      if (out_nrofchannels != 1) {
        if (in_nrofchannels == 1)
        {
          n = 0;
          i1 = 0;
          while (n < i)
          {
            arrayOfFloat2[n] += readbuffer[i1] * _rightgain;
            n++;
            i1 += m;
          }
        }
        else
        {
          n = 0;
          i1 = 1;
          while (n < i)
          {
            arrayOfFloat2[n] += readbuffer[i1] * _rightgain;
            n++;
            i1 += m;
          }
        }
      }
      float[] arrayOfFloat3;
      int i2;
      if (_eff1gain > 2.0E-4D)
      {
        arrayOfFloat3 = paramArrayOfSoftAudioBuffer[2].array();
        i1 = 0;
        i2 = 0;
        while (i1 < i)
        {
          arrayOfFloat3[i1] += readbuffer[i2] * _eff1gain;
          i1++;
          i2 += m;
        }
        if (in_nrofchannels == 2)
        {
          i1 = 0;
          i2 = 1;
          while (i1 < i)
          {
            arrayOfFloat3[i1] += readbuffer[i2] * _eff1gain;
            i1++;
            i2 += m;
          }
        }
      }
      if (_eff2gain > 2.0E-4D)
      {
        arrayOfFloat3 = paramArrayOfSoftAudioBuffer[3].array();
        i1 = 0;
        i2 = 0;
        while (i1 < i)
        {
          arrayOfFloat3[i1] += readbuffer[i2] * _eff2gain;
          i1++;
          i2 += m;
        }
        if (in_nrofchannels == 2)
        {
          i1 = 0;
          i2 = 1;
          while (i1 < i)
          {
            arrayOfFloat3[i1] += readbuffer[i2] * _eff2gain;
            i1++;
            i2 += m;
          }
        }
      }
    }
  }
  
  public int getFrameLength()
  {
    return bufferSize / format.getFrameSize();
  }
  
  public long getMicrosecondLength()
  {
    return (getFrameLength() * (1000000.0D / getFormat().getSampleRate()));
  }
  
  public void loop(int paramInt)
  {
    LineEvent localLineEvent = null;
    synchronized (control_mutex)
    {
      if (isOpen())
      {
        if (active) {
          return;
        }
        active = true;
        active_sg = true;
        loopcount = paramInt;
        localLineEvent = new LineEvent(this, LineEvent.Type.START, getLongFramePosition());
      }
    }
    if (localLineEvent != null) {
      sendEvent(localLineEvent);
    }
  }
  
  public void open(AudioInputStream paramAudioInputStream)
    throws LineUnavailableException, IOException
  {
    if (isOpen()) {
      throw new IllegalStateException("Clip is already open with format " + getFormat() + " and frame lengh of " + getFrameLength());
    }
    if (AudioFloatConverter.getConverter(paramAudioInputStream.getFormat()) == null) {
      throw new IllegalArgumentException("Invalid format : " + paramAudioInputStream.getFormat().toString());
    }
    Object localObject;
    int j;
    if (paramAudioInputStream.getFrameLength() != -1L)
    {
      localObject = new byte[(int)paramAudioInputStream.getFrameLength() * paramAudioInputStream.getFormat().getFrameSize()];
      int i = 512 * paramAudioInputStream.getFormat().getFrameSize();
      j = 0;
      while (j != localObject.length)
      {
        if (i > localObject.length - j) {
          i = localObject.length - j;
        }
        int k = paramAudioInputStream.read((byte[])localObject, j, i);
        if (k == -1) {
          break;
        }
        if (k == 0) {
          Thread.yield();
        }
        j += k;
      }
      open(paramAudioInputStream.getFormat(), (byte[])localObject, 0, j);
    }
    else
    {
      localObject = new ByteArrayOutputStream();
      byte[] arrayOfByte = new byte[512 * paramAudioInputStream.getFormat().getFrameSize()];
      j = 0;
      while ((j = paramAudioInputStream.read(arrayOfByte)) != -1)
      {
        if (j == 0) {
          Thread.yield();
        }
        ((ByteArrayOutputStream)localObject).write(arrayOfByte, 0, j);
      }
      open(paramAudioInputStream.getFormat(), ((ByteArrayOutputStream)localObject).toByteArray(), 0, ((ByteArrayOutputStream)localObject).size());
    }
  }
  
  public void open(AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws LineUnavailableException
  {
    synchronized (control_mutex)
    {
      if (isOpen()) {
        throw new IllegalStateException("Clip is already open with format " + getFormat() + " and frame lengh of " + getFrameLength());
      }
      if (AudioFloatConverter.getConverter(paramAudioFormat) == null) {
        throw new IllegalArgumentException("Invalid format : " + paramAudioFormat.toString());
      }
      if (paramInt2 % paramAudioFormat.getFrameSize() != 0) {
        throw new IllegalArgumentException("Buffer size does not represent an integral number of sample frames!");
      }
      if (paramArrayOfByte != null) {
        data = Arrays.copyOf(paramArrayOfByte, paramArrayOfByte.length);
      }
      offset = paramInt1;
      bufferSize = paramInt2;
      format = paramAudioFormat;
      framesize = paramAudioFormat.getFrameSize();
      loopstart = 0;
      loopend = -1;
      loop_sg = true;
      if (!mixer.isOpen())
      {
        mixer.open();
        mixer.implicitOpen = true;
      }
      outputformat = mixer.getFormat();
      out_nrofchannels = outputformat.getChannels();
      in_nrofchannels = paramAudioFormat.getChannels();
      open = true;
      mixer.getMainMixer().openLine(this);
    }
  }
  
  public void setFramePosition(int paramInt)
  {
    synchronized (control_mutex)
    {
      frameposition_sg = true;
      frameposition = paramInt;
    }
  }
  
  public void setLoopPoints(int paramInt1, int paramInt2)
  {
    synchronized (control_mutex)
    {
      if (paramInt2 != -1)
      {
        if (paramInt2 < paramInt1) {
          throw new IllegalArgumentException("Invalid loop points : " + paramInt1 + " - " + paramInt2);
        }
        if (paramInt2 * framesize > bufferSize) {
          throw new IllegalArgumentException("Invalid loop points : " + paramInt1 + " - " + paramInt2);
        }
      }
      if (paramInt1 * framesize > bufferSize) {
        throw new IllegalArgumentException("Invalid loop points : " + paramInt1 + " - " + paramInt2);
      }
      if (0 < paramInt1) {
        throw new IllegalArgumentException("Invalid loop points : " + paramInt1 + " - " + paramInt2);
      }
      loopstart = paramInt1;
      loopend = paramInt2;
      loop_sg = true;
    }
  }
  
  public void setMicrosecondPosition(long paramLong)
  {
    setFramePosition((int)(paramLong * (getFormat().getSampleRate() / 1000000.0D)));
  }
  
  public int available()
  {
    return 0;
  }
  
  public void drain() {}
  
  public void flush() {}
  
  public int getBufferSize()
  {
    return bufferSize;
  }
  
  public AudioFormat getFormat()
  {
    return format;
  }
  
  /* Error */
  public int getFramePosition()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 344	com/sun/media/sound/SoftMixingClip:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 325	com/sun/media/sound/SoftMixingClip:frameposition	I
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
    //   0	19	0	this	SoftMixingClip
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public float getLevel()
  {
    return -1.0F;
  }
  
  public long getLongFramePosition()
  {
    return getFramePosition();
  }
  
  public long getMicrosecondPosition()
  {
    return (getFramePosition() * (1000000.0D / getFormat().getSampleRate()));
  }
  
  /* Error */
  public boolean isActive()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 344	com/sun/media/sound/SoftMixingClip:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 334	com/sun/media/sound/SoftMixingClip:active	Z
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
    //   0	19	0	this	SoftMixingClip
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  /* Error */
  public boolean isRunning()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 344	com/sun/media/sound/SoftMixingClip:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 334	com/sun/media/sound/SoftMixingClip:active	Z
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
    //   0	19	0	this	SoftMixingClip
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void start()
  {
    LineEvent localLineEvent = null;
    synchronized (control_mutex)
    {
      if (isOpen())
      {
        if (active) {
          return;
        }
        active = true;
        active_sg = true;
        loopcount = 0;
        localLineEvent = new LineEvent(this, LineEvent.Type.START, getLongFramePosition());
      }
    }
    if (localLineEvent != null) {
      sendEvent(localLineEvent);
    }
  }
  
  public void stop()
  {
    LineEvent localLineEvent = null;
    synchronized (control_mutex)
    {
      if (isOpen())
      {
        if (!active) {
          return;
        }
        active = false;
        active_sg = true;
        localLineEvent = new LineEvent(this, LineEvent.Type.STOP, getLongFramePosition());
      }
    }
    if (localLineEvent != null) {
      sendEvent(localLineEvent);
    }
  }
  
  public void close()
  {
    LineEvent localLineEvent = null;
    synchronized (control_mutex)
    {
      if (!isOpen()) {
        return;
      }
      stop();
      localLineEvent = new LineEvent(this, LineEvent.Type.CLOSE, getLongFramePosition());
      open = false;
      mixer.getMainMixer().closeLine(this);
    }
    if (localLineEvent != null) {
      sendEvent(localLineEvent);
    }
  }
  
  public boolean isOpen()
  {
    return open;
  }
  
  public void open()
    throws LineUnavailableException
  {
    if (data == null) {
      throw new IllegalArgumentException("Illegal call to open() in interface Clip");
    }
    open(format, data, offset, bufferSize);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftMixingClip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
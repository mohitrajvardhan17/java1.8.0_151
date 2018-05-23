package com.sun.media.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public final class SoftMixingSourceDataLine
  extends SoftMixingDataLine
  implements SourceDataLine
{
  private boolean open = false;
  private AudioFormat format = new AudioFormat(44100.0F, 16, 2, true, false);
  private int framesize;
  private int bufferSize = -1;
  private float[] readbuffer;
  private boolean active = false;
  private byte[] cycling_buffer;
  private int cycling_read_pos = 0;
  private int cycling_write_pos = 0;
  private int cycling_avail = 0;
  private long cycling_framepos = 0L;
  private AudioFloatInputStream afis;
  private boolean _active = false;
  private AudioFormat outputformat;
  private int out_nrofchannels;
  private int in_nrofchannels;
  private float _rightgain;
  private float _leftgain;
  private float _eff1gain;
  private float _eff2gain;
  
  SoftMixingSourceDataLine(SoftMixingMixer paramSoftMixingMixer, DataLine.Info paramInfo)
  {
    super(paramSoftMixingMixer, paramInfo);
  }
  
  public int write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (!isOpen()) {
      return 0;
    }
    if (paramInt2 % framesize != 0) {
      throw new IllegalArgumentException("Number of bytes does not represent an integral number of sample frames.");
    }
    if (paramInt1 < 0) {
      throw new ArrayIndexOutOfBoundsException(paramInt1);
    }
    if (paramInt1 + paramInt2 > paramArrayOfByte.length) {
      throw new ArrayIndexOutOfBoundsException(paramArrayOfByte.length);
    }
    byte[] arrayOfByte = cycling_buffer;
    int i = cycling_buffer.length;
    int j = 0;
    while (j != paramInt2)
    {
      int k;
      synchronized (cycling_buffer)
      {
        int m = cycling_write_pos;
        k = cycling_avail;
        while ((j != paramInt2) && (k != i))
        {
          arrayOfByte[(m++)] = paramArrayOfByte[(paramInt1++)];
          j++;
          k++;
          if (m == i) {
            m = 0;
          }
        }
        cycling_avail = k;
        cycling_write_pos = m;
        if (j == paramInt2) {
          return j;
        }
      }
      if (k == i)
      {
        try
        {
          Thread.sleep(1L);
        }
        catch (InterruptedException localInterruptedException)
        {
          return j;
        }
        if (!isRunning()) {
          return j;
        }
      }
    }
    return j;
  }
  
  protected void processControlLogic()
  {
    _active = active;
    _rightgain = rightgain;
    _leftgain = leftgain;
    _eff1gain = eff1gain;
    _eff2gain = eff2gain;
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
      if (_eff1gain > 1.0E-4D)
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
      if (_eff2gain > 1.0E-4D)
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
  
  public void open()
    throws LineUnavailableException
  {
    open(format);
  }
  
  public void open(AudioFormat paramAudioFormat)
    throws LineUnavailableException
  {
    if (bufferSize == -1) {
      bufferSize = ((int)(paramAudioFormat.getFrameRate() / 2.0F) * paramAudioFormat.getFrameSize());
    }
    open(paramAudioFormat, bufferSize);
  }
  
  public void open(AudioFormat paramAudioFormat, int paramInt)
    throws LineUnavailableException
  {
    LineEvent localLineEvent = null;
    if (paramInt < paramAudioFormat.getFrameSize() * 32) {
      paramInt = paramAudioFormat.getFrameSize() * 32;
    }
    synchronized (control_mutex)
    {
      if (!isOpen())
      {
        if (!mixer.isOpen())
        {
          mixer.open();
          mixer.implicitOpen = true;
        }
        localLineEvent = new LineEvent(this, LineEvent.Type.OPEN, 0L);
        bufferSize = (paramInt - paramInt % paramAudioFormat.getFrameSize());
        format = paramAudioFormat;
        framesize = paramAudioFormat.getFrameSize();
        outputformat = mixer.getFormat();
        out_nrofchannels = outputformat.getChannels();
        in_nrofchannels = paramAudioFormat.getChannels();
        open = true;
        mixer.getMainMixer().openLine(this);
        cycling_buffer = new byte[framesize * paramInt];
        cycling_read_pos = 0;
        cycling_write_pos = 0;
        cycling_avail = 0;
        cycling_framepos = 0L;
        InputStream local1 = new InputStream()
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
          
          /* Error */
          public int available()
            throws IOException
          {
            // Byte code:
            //   0: aload_0
            //   1: getfield 57	com/sun/media/sound/SoftMixingSourceDataLine$1:this$0	Lcom/sun/media/sound/SoftMixingSourceDataLine;
            //   4: invokestatic 62	com/sun/media/sound/SoftMixingSourceDataLine:access$000	(Lcom/sun/media/sound/SoftMixingSourceDataLine;)[B
            //   7: dup
            //   8: astore_1
            //   9: monitorenter
            //   10: aload_0
            //   11: getfield 57	com/sun/media/sound/SoftMixingSourceDataLine$1:this$0	Lcom/sun/media/sound/SoftMixingSourceDataLine;
            //   14: invokestatic 58	com/sun/media/sound/SoftMixingSourceDataLine:access$100	(Lcom/sun/media/sound/SoftMixingSourceDataLine;)I
            //   17: aload_1
            //   18: monitorexit
            //   19: ireturn
            //   20: astore_2
            //   21: aload_1
            //   22: monitorexit
            //   23: aload_2
            //   24: athrow
            // Local variable table:
            //   start	length	slot	name	signature
            //   0	25	0	this	1
            //   8	14	1	Ljava/lang/Object;	Object
            //   20	4	2	localObject1	Object
            // Exception table:
            //   from	to	target	type
            //   10	19	20	finally
            //   20	23	20	finally
          }
          
          public int read(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
            throws IOException
          {
            synchronized (cycling_buffer)
            {
              if (paramAnonymousInt2 > cycling_avail) {
                paramAnonymousInt2 = cycling_avail;
              }
              int i = cycling_read_pos;
              byte[] arrayOfByte = cycling_buffer;
              int j = arrayOfByte.length;
              for (int k = 0; k < paramAnonymousInt2; k++)
              {
                paramAnonymousArrayOfByte[(paramAnonymousInt1++)] = arrayOfByte[i];
                i++;
                if (i == j) {
                  i = 0;
                }
              }
              cycling_read_pos = i;
              cycling_avail = (cycling_avail - paramAnonymousInt2);
              cycling_framepos = (cycling_framepos + paramAnonymousInt2 / framesize);
            }
            return paramAnonymousInt2;
          }
        };
        afis = AudioFloatInputStream.getInputStream(new AudioInputStream(local1, paramAudioFormat, -1L));
        afis = new NonBlockingFloatInputStream(afis);
        if (Math.abs(paramAudioFormat.getSampleRate() - outputformat.getSampleRate()) > 1.0E-6D) {
          afis = new SoftMixingDataLine.AudioFloatInputStreamResampler(afis, outputformat);
        }
      }
      else if (!paramAudioFormat.matches(getFormat()))
      {
        throw new IllegalStateException("Line is already open with format " + getFormat() + " and bufferSize " + getBufferSize());
      }
    }
    if (localLineEvent != null) {
      sendEvent(localLineEvent);
    }
  }
  
  /* Error */
  public int available()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 298	com/sun/media/sound/SoftMixingSourceDataLine:cycling_buffer	[B
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 298	com/sun/media/sound/SoftMixingSourceDataLine:cycling_buffer	[B
    //   11: arraylength
    //   12: aload_0
    //   13: getfield 288	com/sun/media/sound/SoftMixingSourceDataLine:cycling_avail	I
    //   16: isub
    //   17: aload_1
    //   18: monitorexit
    //   19: ireturn
    //   20: astore_2
    //   21: aload_1
    //   22: monitorexit
    //   23: aload_2
    //   24: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	25	0	this	SoftMixingSourceDataLine
    //   5	17	1	Ljava/lang/Object;	Object
    //   20	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	19	20	finally
    //   20	23	20	finally
  }
  
  public void drain()
  {
    for (;;)
    {
      int i;
      synchronized (cycling_buffer)
      {
        i = cycling_avail;
      }
      if (i != 0) {
        return;
      }
      try
      {
        Thread.sleep(1L);
      }
      catch (InterruptedException localInterruptedException)
      {
        return;
      }
    }
  }
  
  public void flush()
  {
    synchronized (cycling_buffer)
    {
      cycling_read_pos = 0;
      cycling_write_pos = 0;
      cycling_avail = 0;
    }
  }
  
  /* Error */
  public int getBufferSize()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 302	com/sun/media/sound/SoftMixingSourceDataLine:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 287	com/sun/media/sound/SoftMixingSourceDataLine:bufferSize	I
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
    //   0	19	0	this	SoftMixingSourceDataLine
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  /* Error */
  public AudioFormat getFormat()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 302	com/sun/media/sound/SoftMixingSourceDataLine:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 303	com/sun/media/sound/SoftMixingSourceDataLine:format	Ljavax/sound/sampled/AudioFormat;
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
    //   0	19	0	this	SoftMixingSourceDataLine
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public int getFramePosition()
  {
    return (int)getLongFramePosition();
  }
  
  public float getLevel()
  {
    return -1.0F;
  }
  
  /* Error */
  public long getLongFramePosition()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 298	com/sun/media/sound/SoftMixingSourceDataLine:cycling_buffer	[B
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 294	com/sun/media/sound/SoftMixingSourceDataLine:cycling_framepos	J
    //   11: aload_1
    //   12: monitorexit
    //   13: lreturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SoftMixingSourceDataLine
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public long getMicrosecondPosition()
  {
    return (getLongFramePosition() * (1000000.0D / getFormat().getSampleRate()));
  }
  
  /* Error */
  public boolean isActive()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 302	com/sun/media/sound/SoftMixingSourceDataLine:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 296	com/sun/media/sound/SoftMixingSourceDataLine:active	Z
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
    //   0	19	0	this	SoftMixingSourceDataLine
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
    //   1: getfield 302	com/sun/media/sound/SoftMixingSourceDataLine:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 296	com/sun/media/sound/SoftMixingSourceDataLine:active	Z
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
    //   0	19	0	this	SoftMixingSourceDataLine
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
  
  /* Error */
  public boolean isOpen()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 302	com/sun/media/sound/SoftMixingSourceDataLine:control_mutex	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 297	com/sun/media/sound/SoftMixingSourceDataLine:open	Z
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
    //   0	19	0	this	SoftMixingSourceDataLine
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  private static class NonBlockingFloatInputStream
    extends AudioFloatInputStream
  {
    AudioFloatInputStream ais;
    
    NonBlockingFloatInputStream(AudioFloatInputStream paramAudioFloatInputStream)
    {
      ais = paramAudioFloatInputStream;
    }
    
    public int available()
      throws IOException
    {
      return ais.available();
    }
    
    public void close()
      throws IOException
    {
      ais.close();
    }
    
    public AudioFormat getFormat()
    {
      return ais.getFormat();
    }
    
    public long getFrameLength()
    {
      return ais.getFrameLength();
    }
    
    public void mark(int paramInt)
    {
      ais.mark(paramInt);
    }
    
    public boolean markSupported()
    {
      return ais.markSupported();
    }
    
    public int read(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = available();
      if (paramInt2 > i)
      {
        int j = ais.read(paramArrayOfFloat, paramInt1, i);
        Arrays.fill(paramArrayOfFloat, paramInt1 + j, paramInt1 + paramInt2, 0.0F);
        return paramInt2;
      }
      return ais.read(paramArrayOfFloat, paramInt1, paramInt2);
    }
    
    public void reset()
      throws IOException
    {
      ais.reset();
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      return ais.skip(paramLong);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftMixingSourceDataLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
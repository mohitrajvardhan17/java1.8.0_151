package com.sun.media.sound;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public final class SoftJitterCorrector
  extends AudioInputStream
{
  public SoftJitterCorrector(AudioInputStream paramAudioInputStream, int paramInt1, int paramInt2)
  {
    super(new JitterStream(paramAudioInputStream, paramInt1, paramInt2), paramAudioInputStream.getFormat(), paramAudioInputStream.getFrameLength());
  }
  
  private static class JitterStream
    extends InputStream
  {
    static int MAX_BUFFER_SIZE = 1048576;
    boolean active = true;
    Thread thread;
    AudioInputStream stream;
    int writepos = 0;
    int readpos = 0;
    byte[][] buffers;
    private final Object buffers_mutex = new Object();
    int w_count = 1000;
    int w_min_tol = 2;
    int w_max_tol = 10;
    int w = 0;
    int w_min = -1;
    int bbuffer_pos = 0;
    int bbuffer_max = 0;
    byte[] bbuffer = null;
    
    public byte[] nextReadBuffer()
    {
      int i;
      synchronized (buffers_mutex)
      {
        if (writepos > readpos)
        {
          i = writepos - readpos;
          if (i < w_min) {
            w_min = i;
          }
          int j = readpos;
          readpos += 1;
          return buffers[(j % buffers.length)];
        }
        w_min = -1;
        w = (w_count - 1);
      }
      for (;;)
      {
        try
        {
          Thread.sleep(1L);
        }
        catch (InterruptedException ???)
        {
          return null;
        }
        synchronized (buffers_mutex)
        {
          if (writepos > readpos)
          {
            w = 0;
            w_min = -1;
            w = (w_count - 1);
            i = readpos;
            readpos += 1;
            return buffers[(i % buffers.length)];
          }
        }
      }
    }
    
    /* Error */
    public byte[] nextWriteBuffer()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 123	com/sun/media/sound/SoftJitterCorrector$JitterStream:buffers_mutex	Ljava/lang/Object;
      //   4: dup
      //   5: astore_1
      //   6: monitorenter
      //   7: aload_0
      //   8: getfield 122	com/sun/media/sound/SoftJitterCorrector$JitterStream:buffers	[[B
      //   11: aload_0
      //   12: getfield 119	com/sun/media/sound/SoftJitterCorrector$JitterStream:writepos	I
      //   15: aload_0
      //   16: getfield 122	com/sun/media/sound/SoftJitterCorrector$JitterStream:buffers	[[B
      //   19: arraylength
      //   20: irem
      //   21: aaload
      //   22: aload_1
      //   23: monitorexit
      //   24: areturn
      //   25: astore_2
      //   26: aload_1
      //   27: monitorexit
      //   28: aload_2
      //   29: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	30	0	this	JitterStream
      //   5	22	1	Ljava/lang/Object;	Object
      //   25	4	2	localObject1	Object
      // Exception table:
      //   from	to	target	type
      //   7	24	25	finally
      //   25	28	25	finally
    }
    
    public void commit()
    {
      synchronized (buffers_mutex)
      {
        writepos += 1;
        if (writepos - readpos > buffers.length)
        {
          int i = writepos - readpos + 10;
          i = Math.max(buffers.length * 2, i);
          buffers = new byte[i][buffers[0].length];
        }
      }
    }
    
    JitterStream(AudioInputStream paramAudioInputStream, int paramInt1, int paramInt2)
    {
      w_count = (10 * (paramInt1 / paramInt2));
      if (w_count < 100) {
        w_count = 100;
      }
      buffers = new byte[paramInt1 / paramInt2 + 10][paramInt2];
      bbuffer_max = (MAX_BUFFER_SIZE / paramInt2);
      stream = paramAudioInputStream;
      Runnable local1 = new Runnable()
      {
        public void run()
        {
          AudioFormat localAudioFormat = stream.getFormat();
          int i = buffers[0].length;
          int j = i / localAudioFormat.getFrameSize();
          long l1 = (j * 1.0E9D / localAudioFormat.getSampleRate());
          long l2 = System.nanoTime();
          long l3 = l2 + l1;
          int k = 0;
          for (;;)
          {
            synchronized (SoftJitterCorrector.JitterStream.this)
            {
              if (!active) {
                break;
              }
            }
            int m;
            synchronized (buffers)
            {
              m = writepos - readpos;
              if (k == 0)
              {
                w += 1;
                if ((w_min != Integer.MAX_VALUE) && (w == w_count))
                {
                  k = 0;
                  if (w_min < w_min_tol) {
                    k = (w_min_tol + w_max_tol) / 2 - w_min;
                  }
                  if (w_min > w_max_tol) {
                    k = (w_min_tol + w_max_tol) / 2 - w_min;
                  }
                  w = 0;
                  w_min = Integer.MAX_VALUE;
                }
              }
            }
            while (m > bbuffer_max)
            {
              synchronized (buffers)
              {
                m = writepos - readpos;
              }
              synchronized (SoftJitterCorrector.JitterStream.this)
              {
                if (!active) {
                  break;
                }
              }
              try
              {
                Thread.sleep(1L);
              }
              catch (InterruptedException localInterruptedException1) {}
            }
            if (k < 0)
            {
              k++;
            }
            else
            {
              byte[] arrayOfByte = nextWriteBuffer();
              try
              {
                int n = 0;
                while (n != arrayOfByte.length)
                {
                  int i1 = stream.read(arrayOfByte, n, arrayOfByte.length - n);
                  if (i1 < 0) {
                    throw new EOFException();
                  }
                  if (i1 == 0) {
                    Thread.yield();
                  }
                  n += i1;
                }
              }
              catch (IOException localIOException) {}
              commit();
            }
            if (k > 0)
            {
              k--;
              l3 = System.nanoTime() + l1;
            }
            else
            {
              long l4 = l3 - System.nanoTime();
              if (l4 > 0L) {
                try
                {
                  Thread.sleep(l4 / 1000000L);
                }
                catch (InterruptedException localInterruptedException2) {}
              }
              l3 += l1;
            }
          }
        }
      };
      thread = new Thread(local1);
      thread.setDaemon(true);
      thread.setPriority(10);
      thread.start();
    }
    
    public void close()
      throws IOException
    {
      synchronized (this)
      {
        active = false;
      }
      try
      {
        thread.join();
      }
      catch (InterruptedException localInterruptedException) {}
      stream.close();
    }
    
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      if (read(arrayOfByte) == -1) {
        return -1;
      }
      return arrayOfByte[0] & 0xFF;
    }
    
    public void fillBuffer()
    {
      bbuffer = nextReadBuffer();
      bbuffer_pos = 0;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (bbuffer == null) {
        fillBuffer();
      }
      int i = bbuffer.length;
      int j = paramInt1 + paramInt2;
      while (paramInt1 < j) {
        if (available() == 0)
        {
          fillBuffer();
        }
        else
        {
          byte[] arrayOfByte = bbuffer;
          int k = bbuffer_pos;
          while ((paramInt1 < j) && (k < i)) {
            paramArrayOfByte[(paramInt1++)] = arrayOfByte[(k++)];
          }
          bbuffer_pos = k;
        }
      }
      return paramInt2;
    }
    
    public int available()
    {
      return bbuffer.length - bbuffer_pos;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\SoftJitterCorrector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
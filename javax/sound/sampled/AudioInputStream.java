package javax.sound.sampled;

import java.io.IOException;
import java.io.InputStream;

public class AudioInputStream
  extends InputStream
{
  private InputStream stream;
  protected AudioFormat format;
  protected long frameLength;
  protected int frameSize;
  protected long framePos;
  private long markpos;
  private byte[] pushBackBuffer = null;
  private int pushBackLen = 0;
  private byte[] markPushBackBuffer = null;
  private int markPushBackLen = 0;
  
  public AudioInputStream(InputStream paramInputStream, AudioFormat paramAudioFormat, long paramLong)
  {
    format = paramAudioFormat;
    frameLength = paramLong;
    frameSize = paramAudioFormat.getFrameSize();
    if ((frameSize == -1) || (frameSize <= 0)) {
      frameSize = 1;
    }
    stream = paramInputStream;
    framePos = 0L;
    markpos = 0L;
  }
  
  public AudioInputStream(TargetDataLine paramTargetDataLine)
  {
    TargetDataLineInputStream localTargetDataLineInputStream = new TargetDataLineInputStream(paramTargetDataLine);
    format = paramTargetDataLine.getFormat();
    frameLength = -1L;
    frameSize = format.getFrameSize();
    if ((frameSize == -1) || (frameSize <= 0)) {
      frameSize = 1;
    }
    stream = localTargetDataLineInputStream;
    framePos = 0L;
    markpos = 0L;
  }
  
  public AudioFormat getFormat()
  {
    return format;
  }
  
  public long getFrameLength()
  {
    return frameLength;
  }
  
  public int read()
    throws IOException
  {
    if (frameSize != 1) {
      throw new IOException("cannot read a single byte if frame size > 1");
    }
    byte[] arrayOfByte = new byte[1];
    int i = read(arrayOfByte);
    if (i <= 0) {
      return -1;
    }
    return arrayOfByte[0] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 % frameSize != 0)
    {
      paramInt2 -= paramInt2 % frameSize;
      if (paramInt2 == 0) {
        return 0;
      }
    }
    if (frameLength != -1L)
    {
      if (framePos >= frameLength) {
        return -1;
      }
      if (paramInt2 / frameSize > frameLength - framePos) {
        paramInt2 = (int)(frameLength - framePos) * frameSize;
      }
    }
    int i = 0;
    int j = paramInt1;
    if ((pushBackLen > 0) && (paramInt2 >= pushBackLen))
    {
      System.arraycopy(pushBackBuffer, 0, paramArrayOfByte, paramInt1, pushBackLen);
      j += pushBackLen;
      paramInt2 -= pushBackLen;
      i += pushBackLen;
      pushBackLen = 0;
    }
    int k = stream.read(paramArrayOfByte, j, paramInt2);
    if (k == -1) {
      return -1;
    }
    if (k > 0) {
      i += k;
    }
    if (i > 0)
    {
      pushBackLen = (i % frameSize);
      if (pushBackLen > 0)
      {
        if (pushBackBuffer == null) {
          pushBackBuffer = new byte[frameSize];
        }
        System.arraycopy(paramArrayOfByte, paramInt1 + i - pushBackLen, pushBackBuffer, 0, pushBackLen);
        i -= pushBackLen;
      }
      framePos += i / frameSize;
    }
    return i;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong % frameSize != 0L) {
      paramLong -= paramLong % frameSize;
    }
    if ((frameLength != -1L) && (paramLong / frameSize > frameLength - framePos)) {
      paramLong = (frameLength - framePos) * frameSize;
    }
    long l = stream.skip(paramLong);
    if (l % frameSize != 0L) {
      throw new IOException("Could not skip an integer number of frames.");
    }
    if (l >= 0L) {
      framePos += l / frameSize;
    }
    return l;
  }
  
  public int available()
    throws IOException
  {
    int i = stream.available();
    if ((frameLength != -1L) && (i / frameSize > frameLength - framePos)) {
      return (int)(frameLength - framePos) * frameSize;
    }
    return i;
  }
  
  public void close()
    throws IOException
  {
    stream.close();
  }
  
  public void mark(int paramInt)
  {
    stream.mark(paramInt);
    if (markSupported())
    {
      markpos = framePos;
      markPushBackLen = pushBackLen;
      if (markPushBackLen > 0)
      {
        if (markPushBackBuffer == null) {
          markPushBackBuffer = new byte[frameSize];
        }
        System.arraycopy(pushBackBuffer, 0, markPushBackBuffer, 0, markPushBackLen);
      }
    }
  }
  
  public void reset()
    throws IOException
  {
    stream.reset();
    framePos = markpos;
    pushBackLen = markPushBackLen;
    if (pushBackLen > 0)
    {
      if (pushBackBuffer == null) {
        pushBackBuffer = new byte[frameSize - 1];
      }
      System.arraycopy(markPushBackBuffer, 0, pushBackBuffer, 0, pushBackLen);
    }
  }
  
  public boolean markSupported()
  {
    return stream.markSupported();
  }
  
  private class TargetDataLineInputStream
    extends InputStream
  {
    TargetDataLine line;
    
    TargetDataLineInputStream(TargetDataLine paramTargetDataLine)
    {
      line = paramTargetDataLine;
    }
    
    public int available()
      throws IOException
    {
      return line.available();
    }
    
    public void close()
      throws IOException
    {
      if (line.isActive())
      {
        line.flush();
        line.stop();
      }
      line.close();
    }
    
    public int read()
      throws IOException
    {
      byte[] arrayOfByte = new byte[1];
      int i = read(arrayOfByte, 0, 1);
      if (i == -1) {
        return -1;
      }
      i = arrayOfByte[0];
      if (line.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
        i += 128;
      }
      return i;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      try
      {
        return line.read(paramArrayOfByte, paramInt1, paramInt2);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw new IOException(localIllegalArgumentException.getMessage());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\AudioInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
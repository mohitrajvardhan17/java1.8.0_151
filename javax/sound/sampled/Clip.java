package javax.sound.sampled;

import java.io.IOException;

public abstract interface Clip
  extends DataLine
{
  public static final int LOOP_CONTINUOUSLY = -1;
  
  public abstract void open(AudioFormat paramAudioFormat, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws LineUnavailableException;
  
  public abstract void open(AudioInputStream paramAudioInputStream)
    throws LineUnavailableException, IOException;
  
  public abstract int getFrameLength();
  
  public abstract long getMicrosecondLength();
  
  public abstract void setFramePosition(int paramInt);
  
  public abstract void setMicrosecondPosition(long paramLong);
  
  public abstract void setLoopPoints(int paramInt1, int paramInt2);
  
  public abstract void loop(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\Clip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
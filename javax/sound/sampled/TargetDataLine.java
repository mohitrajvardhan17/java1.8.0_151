package javax.sound.sampled;

public abstract interface TargetDataLine
  extends DataLine
{
  public abstract void open(AudioFormat paramAudioFormat, int paramInt)
    throws LineUnavailableException;
  
  public abstract void open(AudioFormat paramAudioFormat)
    throws LineUnavailableException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\TargetDataLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
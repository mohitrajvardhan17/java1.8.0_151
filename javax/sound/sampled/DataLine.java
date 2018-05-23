package javax.sound.sampled;

import java.util.Arrays;

public abstract interface DataLine
  extends Line
{
  public abstract void drain();
  
  public abstract void flush();
  
  public abstract void start();
  
  public abstract void stop();
  
  public abstract boolean isRunning();
  
  public abstract boolean isActive();
  
  public abstract AudioFormat getFormat();
  
  public abstract int getBufferSize();
  
  public abstract int available();
  
  public abstract int getFramePosition();
  
  public abstract long getLongFramePosition();
  
  public abstract long getMicrosecondPosition();
  
  public abstract float getLevel();
  
  public static class Info
    extends Line.Info
  {
    private final AudioFormat[] formats;
    private final int minBufferSize;
    private final int maxBufferSize;
    
    public Info(Class<?> paramClass, AudioFormat[] paramArrayOfAudioFormat, int paramInt1, int paramInt2)
    {
      super();
      if (paramArrayOfAudioFormat == null) {
        formats = new AudioFormat[0];
      } else {
        formats = ((AudioFormat[])Arrays.copyOf(paramArrayOfAudioFormat, paramArrayOfAudioFormat.length));
      }
      minBufferSize = paramInt1;
      maxBufferSize = paramInt2;
    }
    
    public Info(Class<?> paramClass, AudioFormat paramAudioFormat, int paramInt)
    {
      super();
      if (paramAudioFormat == null) {
        formats = new AudioFormat[0];
      } else {
        formats = new AudioFormat[] { paramAudioFormat };
      }
      minBufferSize = paramInt;
      maxBufferSize = paramInt;
    }
    
    public Info(Class<?> paramClass, AudioFormat paramAudioFormat)
    {
      this(paramClass, paramAudioFormat, -1);
    }
    
    public AudioFormat[] getFormats()
    {
      return (AudioFormat[])Arrays.copyOf(formats, formats.length);
    }
    
    public boolean isFormatSupported(AudioFormat paramAudioFormat)
    {
      for (int i = 0; i < formats.length; i++) {
        if (paramAudioFormat.matches(formats[i])) {
          return true;
        }
      }
      return false;
    }
    
    public int getMinBufferSize()
    {
      return minBufferSize;
    }
    
    public int getMaxBufferSize()
    {
      return maxBufferSize;
    }
    
    public boolean matches(Line.Info paramInfo)
    {
      if (!super.matches(paramInfo)) {
        return false;
      }
      Info localInfo = (Info)paramInfo;
      if ((getMaxBufferSize() >= 0) && (localInfo.getMaxBufferSize() >= 0) && (getMaxBufferSize() > localInfo.getMaxBufferSize())) {
        return false;
      }
      if ((getMinBufferSize() >= 0) && (localInfo.getMinBufferSize() >= 0) && (getMinBufferSize() < localInfo.getMinBufferSize())) {
        return false;
      }
      AudioFormat[] arrayOfAudioFormat = getFormats();
      if (arrayOfAudioFormat != null) {
        for (int i = 0; i < arrayOfAudioFormat.length; i++) {
          if ((arrayOfAudioFormat[i] != null) && (!localInfo.isFormatSupported(arrayOfAudioFormat[i]))) {
            return false;
          }
        }
      }
      return true;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      if ((formats.length == 1) && (formats[0] != null)) {
        localStringBuffer.append(" supporting format " + formats[0]);
      } else if (getFormats().length > 1) {
        localStringBuffer.append(" supporting " + getFormats().length + " audio formats");
      }
      if ((minBufferSize != -1) && (maxBufferSize != -1)) {
        localStringBuffer.append(", and buffers of " + minBufferSize + " to " + maxBufferSize + " bytes");
      } else if ((minBufferSize != -1) && (minBufferSize > 0)) {
        localStringBuffer.append(", and buffers of at least " + minBufferSize + " bytes");
      } else if (maxBufferSize != -1) {
        localStringBuffer.append(", and buffers of up to " + minBufferSize + " bytes");
      }
      return new String(super.toString() + localStringBuffer);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\DataLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
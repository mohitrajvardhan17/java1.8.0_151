package javax.sound.sampled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AudioFormat
{
  protected Encoding encoding;
  protected float sampleRate;
  protected int sampleSizeInBits;
  protected int channels;
  protected int frameSize;
  protected float frameRate;
  protected boolean bigEndian;
  private HashMap<String, Object> properties;
  
  public AudioFormat(Encoding paramEncoding, float paramFloat1, int paramInt1, int paramInt2, int paramInt3, float paramFloat2, boolean paramBoolean)
  {
    encoding = paramEncoding;
    sampleRate = paramFloat1;
    sampleSizeInBits = paramInt1;
    channels = paramInt2;
    frameSize = paramInt3;
    frameRate = paramFloat2;
    bigEndian = paramBoolean;
    properties = null;
  }
  
  public AudioFormat(Encoding paramEncoding, float paramFloat1, int paramInt1, int paramInt2, int paramInt3, float paramFloat2, boolean paramBoolean, Map<String, Object> paramMap)
  {
    this(paramEncoding, paramFloat1, paramInt1, paramInt2, paramInt3, paramFloat2, paramBoolean);
    properties = new HashMap(paramMap);
  }
  
  public AudioFormat(float paramFloat, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramBoolean1 == true ? Encoding.PCM_SIGNED : Encoding.PCM_UNSIGNED, paramFloat, paramInt1, paramInt2, (paramInt2 == -1) || (paramInt1 == -1) ? -1 : (paramInt1 + 7) / 8 * paramInt2, paramFloat, paramBoolean2);
  }
  
  public Encoding getEncoding()
  {
    return encoding;
  }
  
  public float getSampleRate()
  {
    return sampleRate;
  }
  
  public int getSampleSizeInBits()
  {
    return sampleSizeInBits;
  }
  
  public int getChannels()
  {
    return channels;
  }
  
  public int getFrameSize()
  {
    return frameSize;
  }
  
  public float getFrameRate()
  {
    return frameRate;
  }
  
  public boolean isBigEndian()
  {
    return bigEndian;
  }
  
  public Map<String, Object> properties()
  {
    Object localObject;
    if (properties == null) {
      localObject = new HashMap(0);
    } else {
      localObject = (Map)properties.clone();
    }
    return Collections.unmodifiableMap((Map)localObject);
  }
  
  public Object getProperty(String paramString)
  {
    if (properties == null) {
      return null;
    }
    return properties.get(paramString);
  }
  
  public boolean matches(AudioFormat paramAudioFormat)
  {
    return (paramAudioFormat.getEncoding().equals(getEncoding())) && ((paramAudioFormat.getChannels() == -1) || (paramAudioFormat.getChannels() == getChannels())) && ((paramAudioFormat.getSampleRate() == -1.0F) || (paramAudioFormat.getSampleRate() == getSampleRate())) && ((paramAudioFormat.getSampleSizeInBits() == -1) || (paramAudioFormat.getSampleSizeInBits() == getSampleSizeInBits())) && ((paramAudioFormat.getFrameRate() == -1.0F) || (paramAudioFormat.getFrameRate() == getFrameRate())) && ((paramAudioFormat.getFrameSize() == -1) || (paramAudioFormat.getFrameSize() == getFrameSize())) && ((getSampleSizeInBits() <= 8) || (paramAudioFormat.isBigEndian() == isBigEndian()));
  }
  
  public String toString()
  {
    String str1 = "";
    if (getEncoding() != null) {
      str1 = getEncoding().toString() + " ";
    }
    String str2;
    if (getSampleRate() == -1.0F) {
      str2 = "unknown sample rate, ";
    } else {
      str2 = "" + getSampleRate() + " Hz, ";
    }
    String str3;
    if (getSampleSizeInBits() == -1.0F) {
      str3 = "unknown bits per sample, ";
    } else {
      str3 = "" + getSampleSizeInBits() + " bit, ";
    }
    String str4;
    if (getChannels() == 1) {
      str4 = "mono, ";
    } else if (getChannels() == 2) {
      str4 = "stereo, ";
    } else if (getChannels() == -1) {
      str4 = " unknown number of channels, ";
    } else {
      str4 = "" + getChannels() + " channels, ";
    }
    String str5;
    if (getFrameSize() == -1.0F) {
      str5 = "unknown frame size, ";
    } else {
      str5 = "" + getFrameSize() + " bytes/frame, ";
    }
    String str6 = "";
    if (Math.abs(getSampleRate() - getFrameRate()) > 1.0E-5D) {
      if (getFrameRate() == -1.0F) {
        str6 = "unknown frame rate, ";
      } else {
        str6 = getFrameRate() + " frames/second, ";
      }
    }
    String str7 = "";
    if (((getEncoding().equals(Encoding.PCM_SIGNED)) || (getEncoding().equals(Encoding.PCM_UNSIGNED))) && ((getSampleSizeInBits() > 8) || (getSampleSizeInBits() == -1))) {
      if (isBigEndian()) {
        str7 = "big-endian";
      } else {
        str7 = "little-endian";
      }
    }
    return str1 + str2 + str3 + str4 + str5 + str6 + str7;
  }
  
  public static class Encoding
  {
    public static final Encoding PCM_SIGNED = new Encoding("PCM_SIGNED");
    public static final Encoding PCM_UNSIGNED = new Encoding("PCM_UNSIGNED");
    public static final Encoding PCM_FLOAT = new Encoding("PCM_FLOAT");
    public static final Encoding ULAW = new Encoding("ULAW");
    public static final Encoding ALAW = new Encoding("ALAW");
    private String name;
    
    public Encoding(String paramString)
    {
      name = paramString;
    }
    
    public final boolean equals(Object paramObject)
    {
      if (toString() == null) {
        return (paramObject != null) && (paramObject.toString() == null);
      }
      if ((paramObject instanceof Encoding)) {
        return toString().equals(paramObject.toString());
      }
      return false;
    }
    
    public final int hashCode()
    {
      if (toString() == null) {
        return 0;
      }
      return toString().hashCode();
    }
    
    public final String toString()
    {
      return name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\AudioFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
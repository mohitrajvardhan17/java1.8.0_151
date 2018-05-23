package javax.sound.sampled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AudioFileFormat
{
  private Type type;
  private int byteLength;
  private AudioFormat format;
  private int frameLength;
  private HashMap<String, Object> properties;
  
  protected AudioFileFormat(Type paramType, int paramInt1, AudioFormat paramAudioFormat, int paramInt2)
  {
    type = paramType;
    byteLength = paramInt1;
    format = paramAudioFormat;
    frameLength = paramInt2;
    properties = null;
  }
  
  public AudioFileFormat(Type paramType, AudioFormat paramAudioFormat, int paramInt)
  {
    this(paramType, -1, paramAudioFormat, paramInt);
  }
  
  public AudioFileFormat(Type paramType, AudioFormat paramAudioFormat, int paramInt, Map<String, Object> paramMap)
  {
    this(paramType, -1, paramAudioFormat, paramInt);
    properties = new HashMap(paramMap);
  }
  
  public Type getType()
  {
    return type;
  }
  
  public int getByteLength()
  {
    return byteLength;
  }
  
  public AudioFormat getFormat()
  {
    return format;
  }
  
  public int getFrameLength()
  {
    return frameLength;
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
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (type != null) {
      localStringBuffer.append(type.toString() + " (." + type.getExtension() + ") file");
    } else {
      localStringBuffer.append("unknown file format");
    }
    if (byteLength != -1) {
      localStringBuffer.append(", byte length: " + byteLength);
    }
    localStringBuffer.append(", data format: " + format);
    if (frameLength != -1) {
      localStringBuffer.append(", frame length: " + frameLength);
    }
    return new String(localStringBuffer);
  }
  
  public static class Type
  {
    public static final Type WAVE = new Type("WAVE", "wav");
    public static final Type AU = new Type("AU", "au");
    public static final Type AIFF = new Type("AIFF", "aif");
    public static final Type AIFC = new Type("AIFF-C", "aifc");
    public static final Type SND = new Type("SND", "snd");
    private final String name;
    private final String extension;
    
    public Type(String paramString1, String paramString2)
    {
      name = paramString1;
      extension = paramString2;
    }
    
    public final boolean equals(Object paramObject)
    {
      if (toString() == null) {
        return (paramObject != null) && (paramObject.toString() == null);
      }
      if ((paramObject instanceof Type)) {
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
    
    public String getExtension()
    {
      return extension;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\sampled\AudioFileFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
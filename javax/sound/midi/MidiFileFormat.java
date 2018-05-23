package javax.sound.midi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MidiFileFormat
{
  public static final int UNKNOWN_LENGTH = -1;
  protected int type;
  protected float divisionType;
  protected int resolution;
  protected int byteLength;
  protected long microsecondLength;
  private HashMap<String, Object> properties;
  
  public MidiFileFormat(int paramInt1, float paramFloat, int paramInt2, int paramInt3, long paramLong)
  {
    type = paramInt1;
    divisionType = paramFloat;
    resolution = paramInt2;
    byteLength = paramInt3;
    microsecondLength = paramLong;
    properties = null;
  }
  
  public MidiFileFormat(int paramInt1, float paramFloat, int paramInt2, int paramInt3, long paramLong, Map<String, Object> paramMap)
  {
    this(paramInt1, paramFloat, paramInt2, paramInt3, paramLong);
    properties = new HashMap(paramMap);
  }
  
  public int getType()
  {
    return type;
  }
  
  public float getDivisionType()
  {
    return divisionType;
  }
  
  public int getResolution()
  {
    return resolution;
  }
  
  public int getByteLength()
  {
    return byteLength;
  }
  
  public long getMicrosecondLength()
  {
    return microsecondLength;
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sound\midi\MidiFileFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
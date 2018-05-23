package sun.java2d.cmm.lcms;

import java.util.Arrays;
import java.util.HashMap;
import sun.java2d.cmm.Profile;

final class LCMSProfile
  extends Profile
{
  private final TagCache tagCache;
  private final Object disposerReferent;
  
  LCMSProfile(long paramLong, Object paramObject)
  {
    super(paramLong);
    disposerReferent = paramObject;
    tagCache = new TagCache(this);
  }
  
  final long getLcmsPtr()
  {
    return getNativePtr();
  }
  
  TagData getTag(int paramInt)
  {
    return tagCache.getTag(paramInt);
  }
  
  void clearTagCache()
  {
    tagCache.clear();
  }
  
  static class TagCache
  {
    final LCMSProfile profile;
    private HashMap<Integer, LCMSProfile.TagData> tags;
    
    TagCache(LCMSProfile paramLCMSProfile)
    {
      profile = paramLCMSProfile;
      tags = new HashMap();
    }
    
    LCMSProfile.TagData getTag(int paramInt)
    {
      LCMSProfile.TagData localTagData = (LCMSProfile.TagData)tags.get(Integer.valueOf(paramInt));
      if (localTagData == null)
      {
        byte[] arrayOfByte = LCMS.getTagNative(profile.getNativePtr(), paramInt);
        if (arrayOfByte != null)
        {
          localTagData = new LCMSProfile.TagData(paramInt, arrayOfByte);
          tags.put(Integer.valueOf(paramInt), localTagData);
        }
      }
      return localTagData;
    }
    
    void clear()
    {
      tags.clear();
    }
  }
  
  static class TagData
  {
    private int signature;
    private byte[] data;
    
    TagData(int paramInt, byte[] paramArrayOfByte)
    {
      signature = paramInt;
      data = paramArrayOfByte;
    }
    
    int getSize()
    {
      return data.length;
    }
    
    byte[] getData()
    {
      return Arrays.copyOf(data, data.length);
    }
    
    void copyDataTo(byte[] paramArrayOfByte)
    {
      System.arraycopy(data, 0, paramArrayOfByte, 0, data.length);
    }
    
    int getSignature()
    {
      return signature;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\lcms\LCMSProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
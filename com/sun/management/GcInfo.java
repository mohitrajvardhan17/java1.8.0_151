package com.sun.management;

import java.lang.management.MemoryUsage;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import jdk.Exported;
import sun.management.GcInfoBuilder;
import sun.management.GcInfoCompositeData;

@Exported
public class GcInfo
  implements CompositeData, CompositeDataView
{
  private final long index;
  private final long startTime;
  private final long endTime;
  private final Map<String, MemoryUsage> usageBeforeGc;
  private final Map<String, MemoryUsage> usageAfterGc;
  private final Object[] extAttributes;
  private final CompositeData cdata;
  private final GcInfoBuilder builder;
  
  private GcInfo(GcInfoBuilder paramGcInfoBuilder, long paramLong1, long paramLong2, long paramLong3, MemoryUsage[] paramArrayOfMemoryUsage1, MemoryUsage[] paramArrayOfMemoryUsage2, Object[] paramArrayOfObject)
  {
    builder = paramGcInfoBuilder;
    index = paramLong1;
    startTime = paramLong2;
    endTime = paramLong3;
    String[] arrayOfString = paramGcInfoBuilder.getPoolNames();
    usageBeforeGc = new HashMap(arrayOfString.length);
    usageAfterGc = new HashMap(arrayOfString.length);
    for (int i = 0; i < arrayOfString.length; i++)
    {
      usageBeforeGc.put(arrayOfString[i], paramArrayOfMemoryUsage1[i]);
      usageAfterGc.put(arrayOfString[i], paramArrayOfMemoryUsage2[i]);
    }
    extAttributes = paramArrayOfObject;
    cdata = new GcInfoCompositeData(this, paramGcInfoBuilder, paramArrayOfObject);
  }
  
  private GcInfo(CompositeData paramCompositeData)
  {
    GcInfoCompositeData.validateCompositeData(paramCompositeData);
    index = GcInfoCompositeData.getId(paramCompositeData);
    startTime = GcInfoCompositeData.getStartTime(paramCompositeData);
    endTime = GcInfoCompositeData.getEndTime(paramCompositeData);
    usageBeforeGc = GcInfoCompositeData.getMemoryUsageBeforeGc(paramCompositeData);
    usageAfterGc = GcInfoCompositeData.getMemoryUsageAfterGc(paramCompositeData);
    extAttributes = null;
    builder = null;
    cdata = paramCompositeData;
  }
  
  public long getId()
  {
    return index;
  }
  
  public long getStartTime()
  {
    return startTime;
  }
  
  public long getEndTime()
  {
    return endTime;
  }
  
  public long getDuration()
  {
    return endTime - startTime;
  }
  
  public Map<String, MemoryUsage> getMemoryUsageBeforeGc()
  {
    return Collections.unmodifiableMap(usageBeforeGc);
  }
  
  public Map<String, MemoryUsage> getMemoryUsageAfterGc()
  {
    return Collections.unmodifiableMap(usageAfterGc);
  }
  
  public static GcInfo from(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      return null;
    }
    if ((paramCompositeData instanceof GcInfoCompositeData)) {
      return ((GcInfoCompositeData)paramCompositeData).getGcInfo();
    }
    return new GcInfo(paramCompositeData);
  }
  
  public boolean containsKey(String paramString)
  {
    return cdata.containsKey(paramString);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return cdata.containsValue(paramObject);
  }
  
  public boolean equals(Object paramObject)
  {
    return cdata.equals(paramObject);
  }
  
  public Object get(String paramString)
  {
    return cdata.get(paramString);
  }
  
  public Object[] getAll(String[] paramArrayOfString)
  {
    return cdata.getAll(paramArrayOfString);
  }
  
  public CompositeType getCompositeType()
  {
    return cdata.getCompositeType();
  }
  
  public int hashCode()
  {
    return cdata.hashCode();
  }
  
  public String toString()
  {
    return cdata.toString();
  }
  
  public Collection values()
  {
    return cdata.values();
  }
  
  public CompositeData toCompositeData(CompositeType paramCompositeType)
  {
    return cdata;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\GcInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
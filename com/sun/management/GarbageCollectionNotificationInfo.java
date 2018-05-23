package com.sun.management;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import jdk.Exported;
import sun.management.GarbageCollectionNotifInfoCompositeData;

@Exported
public class GarbageCollectionNotificationInfo
  implements CompositeDataView
{
  private final String gcName;
  private final String gcAction;
  private final String gcCause;
  private final GcInfo gcInfo;
  private final CompositeData cdata;
  public static final String GARBAGE_COLLECTION_NOTIFICATION = "com.sun.management.gc.notification";
  
  public GarbageCollectionNotificationInfo(String paramString1, String paramString2, String paramString3, GcInfo paramGcInfo)
  {
    if (paramString1 == null) {
      throw new NullPointerException("Null gcName");
    }
    if (paramString2 == null) {
      throw new NullPointerException("Null gcAction");
    }
    if (paramString3 == null) {
      throw new NullPointerException("Null gcCause");
    }
    gcName = paramString1;
    gcAction = paramString2;
    gcCause = paramString3;
    gcInfo = paramGcInfo;
    cdata = new GarbageCollectionNotifInfoCompositeData(this);
  }
  
  GarbageCollectionNotificationInfo(CompositeData paramCompositeData)
  {
    GarbageCollectionNotifInfoCompositeData.validateCompositeData(paramCompositeData);
    gcName = GarbageCollectionNotifInfoCompositeData.getGcName(paramCompositeData);
    gcAction = GarbageCollectionNotifInfoCompositeData.getGcAction(paramCompositeData);
    gcCause = GarbageCollectionNotifInfoCompositeData.getGcCause(paramCompositeData);
    gcInfo = GarbageCollectionNotifInfoCompositeData.getGcInfo(paramCompositeData);
    cdata = paramCompositeData;
  }
  
  public String getGcName()
  {
    return gcName;
  }
  
  public String getGcAction()
  {
    return gcAction;
  }
  
  public String getGcCause()
  {
    return gcCause;
  }
  
  public GcInfo getGcInfo()
  {
    return gcInfo;
  }
  
  public static GarbageCollectionNotificationInfo from(CompositeData paramCompositeData)
  {
    if (paramCompositeData == null) {
      return null;
    }
    if ((paramCompositeData instanceof GarbageCollectionNotifInfoCompositeData)) {
      return ((GarbageCollectionNotifInfoCompositeData)paramCompositeData).getGarbageCollectionNotifInfo();
    }
    return new GarbageCollectionNotificationInfo(paramCompositeData);
  }
  
  public CompositeData toCompositeData(CompositeType paramCompositeType)
  {
    return cdata;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\GarbageCollectionNotificationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
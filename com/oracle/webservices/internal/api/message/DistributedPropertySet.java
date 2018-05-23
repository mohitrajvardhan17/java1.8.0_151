package com.oracle.webservices.internal.api.message;

import com.sun.istack.internal.Nullable;
import java.util.Map;

public abstract interface DistributedPropertySet
  extends PropertySet
{
  @Nullable
  public abstract <T extends PropertySet> T getSatellite(Class<T> paramClass);
  
  public abstract Map<Class<? extends PropertySet>, PropertySet> getSatellites();
  
  public abstract void addSatellite(PropertySet paramPropertySet);
  
  public abstract void addSatellite(Class<? extends PropertySet> paramClass, PropertySet paramPropertySet);
  
  public abstract void removeSatellite(PropertySet paramPropertySet);
  
  public abstract void copySatelliteInto(MessageContext paramMessageContext);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\message\DistributedPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
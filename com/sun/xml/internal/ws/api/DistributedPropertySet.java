package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;
import com.sun.istack.internal.NotNull;

/**
 * @deprecated
 */
public abstract class DistributedPropertySet
  extends BaseDistributedPropertySet
{
  public DistributedPropertySet() {}
  
  /**
   * @deprecated
   */
  public void addSatellite(@NotNull PropertySet paramPropertySet)
  {
    super.addSatellite(paramPropertySet);
  }
  
  /**
   * @deprecated
   */
  public void addSatellite(@NotNull Class paramClass, @NotNull PropertySet paramPropertySet)
  {
    super.addSatellite(paramClass, paramPropertySet);
  }
  
  /**
   * @deprecated
   */
  public void copySatelliteInto(@NotNull DistributedPropertySet paramDistributedPropertySet)
  {
    super.copySatelliteInto(paramDistributedPropertySet);
  }
  
  /**
   * @deprecated
   */
  public void removeSatellite(PropertySet paramPropertySet)
  {
    super.removeSatellite(paramPropertySet);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\DistributedPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
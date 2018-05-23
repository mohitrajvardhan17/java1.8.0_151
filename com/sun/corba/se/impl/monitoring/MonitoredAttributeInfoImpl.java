package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfo;

public class MonitoredAttributeInfoImpl
  implements MonitoredAttributeInfo
{
  private final String description;
  private final Class type;
  private final boolean writableFlag;
  private final boolean statisticFlag;
  
  MonitoredAttributeInfoImpl(String paramString, Class paramClass, boolean paramBoolean1, boolean paramBoolean2)
  {
    description = paramString;
    type = paramClass;
    writableFlag = paramBoolean1;
    statisticFlag = paramBoolean2;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public Class type()
  {
    return type;
  }
  
  public boolean isWritable()
  {
    return writableFlag;
  }
  
  public boolean isStatistic()
  {
    return statisticFlag;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\monitoring\MonitoredAttributeInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
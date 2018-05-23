package com.sun.corba.se.spi.orb;

import java.util.Properties;

public abstract interface DataCollector
{
  public abstract boolean isApplet();
  
  public abstract boolean initialHostIsLocal();
  
  public abstract void setParser(PropertyParser paramPropertyParser);
  
  public abstract Properties getProperties();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orb\DataCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
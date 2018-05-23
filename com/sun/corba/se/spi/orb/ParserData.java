package com.sun.corba.se.spi.orb;

import java.util.Properties;

public abstract interface ParserData
{
  public abstract String getPropertyName();
  
  public abstract Operation getOperation();
  
  public abstract String getFieldName();
  
  public abstract Object getDefaultValue();
  
  public abstract Object getTestValue();
  
  public abstract void addToParser(PropertyParser paramPropertyParser);
  
  public abstract void addToProperties(Properties paramProperties);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\orb\ParserData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
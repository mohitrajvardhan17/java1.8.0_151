package com.sun.corba.se.impl.orb;

import java.util.Properties;

public abstract interface ParserAction
{
  public abstract String getPropertyName();
  
  public abstract boolean isPrefix();
  
  public abstract String getFieldName();
  
  public abstract Object apply(Properties paramProperties);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\ParserAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
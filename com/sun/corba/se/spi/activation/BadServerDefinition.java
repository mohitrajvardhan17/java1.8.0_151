package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class BadServerDefinition
  extends UserException
{
  public String reason = null;
  
  public BadServerDefinition()
  {
    super(BadServerDefinitionHelper.id());
  }
  
  public BadServerDefinition(String paramString)
  {
    super(BadServerDefinitionHelper.id());
    reason = paramString;
  }
  
  public BadServerDefinition(String paramString1, String paramString2)
  {
    super(BadServerDefinitionHelper.id() + "  " + paramString1);
    reason = paramString2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\BadServerDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
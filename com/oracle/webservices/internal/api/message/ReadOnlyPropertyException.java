package com.oracle.webservices.internal.api.message;

public class ReadOnlyPropertyException
  extends IllegalArgumentException
{
  private final String propertyName;
  
  public ReadOnlyPropertyException(String paramString)
  {
    super(paramString + " is a read-only property.");
    propertyName = paramString;
  }
  
  public String getPropertyName()
  {
    return propertyName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\message\ReadOnlyPropertyException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
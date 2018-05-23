package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.orb.ParserData;

public abstract class ParserDataBase
  implements ParserData
{
  private String propertyName;
  private Operation operation;
  private String fieldName;
  private Object defaultValue;
  private Object testValue;
  
  protected ParserDataBase(String paramString1, Operation paramOperation, String paramString2, Object paramObject1, Object paramObject2)
  {
    propertyName = paramString1;
    operation = paramOperation;
    fieldName = paramString2;
    defaultValue = paramObject1;
    testValue = paramObject2;
  }
  
  public String getPropertyName()
  {
    return propertyName;
  }
  
  public Operation getOperation()
  {
    return operation;
  }
  
  public String getFieldName()
  {
    return fieldName;
  }
  
  public Object getDefaultValue()
  {
    return defaultValue;
  }
  
  public Object getTestValue()
  {
    return testValue;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\ParserDataBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
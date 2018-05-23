package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.Operation;
import java.util.Properties;

public abstract class ParserActionBase
  implements ParserAction
{
  private String propertyName;
  private boolean prefix;
  private Operation operation;
  private String fieldName;
  
  public int hashCode()
  {
    return propertyName.hashCode() ^ operation.hashCode() ^ fieldName.hashCode() ^ (prefix ? 0 : 1);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof ParserActionBase)) {
      return false;
    }
    ParserActionBase localParserActionBase = (ParserActionBase)paramObject;
    return (propertyName.equals(propertyName)) && (prefix == prefix) && (operation.equals(operation)) && (fieldName.equals(fieldName));
  }
  
  public ParserActionBase(String paramString1, boolean paramBoolean, Operation paramOperation, String paramString2)
  {
    propertyName = paramString1;
    prefix = paramBoolean;
    operation = paramOperation;
    fieldName = paramString2;
  }
  
  public String getPropertyName()
  {
    return propertyName;
  }
  
  public boolean isPrefix()
  {
    return prefix;
  }
  
  public String getFieldName()
  {
    return fieldName;
  }
  
  public abstract Object apply(Properties paramProperties);
  
  protected Operation getOperation()
  {
    return operation;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\ParserActionBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
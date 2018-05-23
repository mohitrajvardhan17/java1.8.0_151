package com.sun.xml.internal.ws.api.model;

import com.sun.xml.internal.bind.api.Bridge;

public abstract interface CheckedException
{
  public abstract SEIModel getOwner();
  
  public abstract JavaMethod getParent();
  
  public abstract Class getExceptionClass();
  
  public abstract Class getDetailBean();
  
  /**
   * @deprecated
   */
  public abstract Bridge getBridge();
  
  public abstract ExceptionType getExceptionType();
  
  public abstract String getMessageName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\CheckedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
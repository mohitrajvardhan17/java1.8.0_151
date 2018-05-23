package com.sun.xml.internal.ws.addressing.model;

import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.ws.WebServiceException;

public class ActionNotSupportedException
  extends WebServiceException
{
  private String action;
  
  public ActionNotSupportedException(String paramString)
  {
    super(AddressingMessages.ACTION_NOT_SUPPORTED_EXCEPTION(paramString));
    action = paramString;
  }
  
  public String getAction()
  {
    return action;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\model\ActionNotSupportedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
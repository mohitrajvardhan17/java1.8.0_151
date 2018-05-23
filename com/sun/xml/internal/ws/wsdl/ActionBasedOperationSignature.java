package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;

public class ActionBasedOperationSignature
{
  private final String action;
  private final QName payloadQName;
  
  public ActionBasedOperationSignature(@NotNull String paramString, @NotNull QName paramQName)
  {
    action = paramString;
    payloadQName = paramQName;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    ActionBasedOperationSignature localActionBasedOperationSignature = (ActionBasedOperationSignature)paramObject;
    if (!action.equals(action)) {
      return false;
    }
    return payloadQName.equals(payloadQName);
  }
  
  public int hashCode()
  {
    int i = action.hashCode();
    i = 31 * i + payloadQName.hashCode();
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\ActionBasedOperationSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
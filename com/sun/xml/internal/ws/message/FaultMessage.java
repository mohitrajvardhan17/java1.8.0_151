package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.FilterMessageImpl;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.namespace.QName;

public class FaultMessage
  extends FilterMessageImpl
{
  @Nullable
  private final QName detailEntryName;
  
  public FaultMessage(Message paramMessage, @Nullable QName paramQName)
  {
    super(paramMessage);
    detailEntryName = paramQName;
  }
  
  @Nullable
  public QName getFirstDetailEntryName()
  {
    return detailEntryName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\FaultMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
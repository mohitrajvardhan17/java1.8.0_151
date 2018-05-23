package com.sun.xml.internal.ws.addressing.model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class MissingAddressingHeaderException
  extends WebServiceException
{
  private final QName name;
  private final transient Packet packet;
  
  public MissingAddressingHeaderException(@NotNull QName paramQName)
  {
    this(paramQName, null);
  }
  
  public MissingAddressingHeaderException(@NotNull QName paramQName, @Nullable Packet paramPacket)
  {
    super(AddressingMessages.MISSING_HEADER_EXCEPTION(paramQName));
    name = paramQName;
    packet = paramPacket;
  }
  
  public QName getMissingHeaderQName()
  {
    return name;
  }
  
  public Packet getPacket()
  {
    return packet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\model\MissingAddressingHeaderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
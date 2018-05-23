package com.sun.xml.internal.ws.api.databinding;

import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public abstract interface Databinding
  extends com.oracle.webservices.internal.api.databinding.Databinding
{
  public abstract EndpointCallBridge getEndpointBridge(Packet paramPacket)
    throws DispatchException;
  
  public abstract ClientCallBridge getClientBridge(Method paramMethod);
  
  public abstract void generateWSDL(WSDLGenInfo paramWSDLGenInfo);
  
  /**
   * @deprecated
   */
  public abstract ContentType encode(Packet paramPacket, OutputStream paramOutputStream)
    throws IOException;
  
  /**
   * @deprecated
   */
  public abstract void decode(InputStream paramInputStream, String paramString, Packet paramPacket)
    throws IOException;
  
  public abstract MessageContextFactory getMessageContextFactory();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\databinding\Databinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
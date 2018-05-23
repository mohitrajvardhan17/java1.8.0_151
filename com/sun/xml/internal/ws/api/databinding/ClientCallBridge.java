package com.sun.xml.internal.ws.api.databinding;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import java.lang.reflect.Method;

public abstract interface ClientCallBridge
{
  public abstract Packet createRequestPacket(JavaCallInfo paramJavaCallInfo);
  
  public abstract JavaCallInfo readResponse(Packet paramPacket, JavaCallInfo paramJavaCallInfo)
    throws Throwable;
  
  public abstract Method getMethod();
  
  public abstract JavaMethod getOperationModel();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\databinding\ClientCallBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
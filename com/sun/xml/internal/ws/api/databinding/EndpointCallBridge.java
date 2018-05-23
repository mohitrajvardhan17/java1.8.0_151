package com.sun.xml.internal.ws.api.databinding;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;

public abstract interface EndpointCallBridge
{
  public abstract JavaCallInfo deserializeRequest(Packet paramPacket);
  
  public abstract Packet serializeResponse(JavaCallInfo paramJavaCallInfo);
  
  public abstract JavaMethod getOperationModel();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\databinding\EndpointCallBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
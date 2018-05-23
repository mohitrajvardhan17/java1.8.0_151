package com.sun.xml.internal.ws.api.pipe;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;

public class ThrowableContainerPropertySet
  extends BasePropertySet
{
  public static final String FIBER_COMPLETION_THROWABLE = "com.sun.xml.internal.ws.api.pipe.fiber-completion-throwable";
  private Throwable throwable;
  public static final String FAULT_MESSAGE = "com.sun.xml.internal.ws.api.pipe.fiber-completion-fault-message";
  private Message faultMessage;
  public static final String RESPONSE_PACKET = "com.sun.xml.internal.ws.api.pipe.fiber-completion-response-packet";
  private Packet responsePacket;
  public static final String IS_FAULT_CREATED = "com.sun.xml.internal.ws.api.pipe.fiber-completion-is-fault-created";
  private boolean isFaultCreated = false;
  private static final BasePropertySet.PropertyMap model = parse(ThrowableContainerPropertySet.class);
  
  public ThrowableContainerPropertySet(Throwable paramThrowable)
  {
    throwable = paramThrowable;
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.api.pipe.fiber-completion-throwable"})
  public Throwable getThrowable()
  {
    return throwable;
  }
  
  public void setThrowable(Throwable paramThrowable)
  {
    throwable = paramThrowable;
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.api.pipe.fiber-completion-fault-message"})
  public Message getFaultMessage()
  {
    return faultMessage;
  }
  
  public void setFaultMessage(Message paramMessage)
  {
    faultMessage = paramMessage;
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.api.pipe.fiber-completion-response-packet"})
  public Packet getResponsePacket()
  {
    return responsePacket;
  }
  
  public void setResponsePacket(Packet paramPacket)
  {
    responsePacket = paramPacket;
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.api.pipe.fiber-completion-is-fault-created"})
  public boolean isFaultCreated()
  {
    return isFaultCreated;
  }
  
  public void setFaultCreated(boolean paramBoolean)
  {
    isFaultCreated = paramBoolean;
  }
  
  protected BasePropertySet.PropertyMap getPropertyMap()
  {
    return model;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\pipe\ThrowableContainerPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
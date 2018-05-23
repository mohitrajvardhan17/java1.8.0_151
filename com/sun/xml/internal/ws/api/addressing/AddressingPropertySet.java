package com.sun.xml.internal.ws.api.addressing;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import com.oracle.webservices.internal.api.message.PropertySet.Property;

public class AddressingPropertySet
  extends BasePropertySet
{
  public static final String ADDRESSING_FAULT_TO = "com.sun.xml.internal.ws.api.addressing.fault.to";
  private String faultTo;
  public static final String ADDRESSING_MESSAGE_ID = "com.sun.xml.internal.ws.api.addressing.message.id";
  private String messageId;
  public static final String ADDRESSING_RELATES_TO = "com.sun.xml.internal.ws.api.addressing.relates.to";
  @PropertySet.Property({"com.sun.xml.internal.ws.api.addressing.relates.to"})
  private String relatesTo;
  public static final String ADDRESSING_REPLY_TO = "com.sun.xml.internal.ws.api.addressing.reply.to";
  @PropertySet.Property({"com.sun.xml.internal.ws.api.addressing.reply.to"})
  private String replyTo;
  private static final BasePropertySet.PropertyMap model = parse(AddressingPropertySet.class);
  
  public AddressingPropertySet() {}
  
  @PropertySet.Property({"com.sun.xml.internal.ws.api.addressing.fault.to"})
  public String getFaultTo()
  {
    return faultTo;
  }
  
  public void setFaultTo(String paramString)
  {
    faultTo = paramString;
  }
  
  public String getMessageId()
  {
    return messageId;
  }
  
  public void setMessageId(String paramString)
  {
    messageId = paramString;
  }
  
  public String getRelatesTo()
  {
    return relatesTo;
  }
  
  public void setRelatesTo(String paramString)
  {
    relatesTo = paramString;
  }
  
  public String getReplyTo()
  {
    return replyTo;
  }
  
  public void setReplyTo(String paramString)
  {
    replyTo = paramString;
  }
  
  protected BasePropertySet.PropertyMap getPropertyMap()
  {
    return model;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\addressing\AddressingPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
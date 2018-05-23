package com.sun.xml.internal.bind.api;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

/**
 * @deprecated
 */
public abstract class BridgeContext
{
  protected BridgeContext() {}
  
  public abstract void setErrorHandler(ValidationEventHandler paramValidationEventHandler);
  
  public abstract void setAttachmentMarshaller(AttachmentMarshaller paramAttachmentMarshaller);
  
  public abstract void setAttachmentUnmarshaller(AttachmentUnmarshaller paramAttachmentUnmarshaller);
  
  public abstract AttachmentMarshaller getAttachmentMarshaller();
  
  public abstract AttachmentUnmarshaller getAttachmentUnmarshaller();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\api\BridgeContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
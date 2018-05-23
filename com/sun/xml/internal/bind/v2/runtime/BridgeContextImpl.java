package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

public final class BridgeContextImpl
  extends BridgeContext
{
  public final UnmarshallerImpl unmarshaller;
  public final MarshallerImpl marshaller;
  
  BridgeContextImpl(JAXBContextImpl paramJAXBContextImpl)
  {
    unmarshaller = paramJAXBContextImpl.createUnmarshaller();
    marshaller = paramJAXBContextImpl.createMarshaller();
  }
  
  public void setErrorHandler(ValidationEventHandler paramValidationEventHandler)
  {
    try
    {
      unmarshaller.setEventHandler(paramValidationEventHandler);
      marshaller.setEventHandler(paramValidationEventHandler);
    }
    catch (JAXBException localJAXBException)
    {
      throw new Error(localJAXBException);
    }
  }
  
  public void setAttachmentMarshaller(AttachmentMarshaller paramAttachmentMarshaller)
  {
    marshaller.setAttachmentMarshaller(paramAttachmentMarshaller);
  }
  
  public void setAttachmentUnmarshaller(AttachmentUnmarshaller paramAttachmentUnmarshaller)
  {
    unmarshaller.setAttachmentUnmarshaller(paramAttachmentUnmarshaller);
  }
  
  public AttachmentMarshaller getAttachmentMarshaller()
  {
    return marshaller.getAttachmentMarshaller();
  }
  
  public AttachmentUnmarshaller getAttachmentUnmarshaller()
  {
    return unmarshaller.getAttachmentUnmarshaller();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\BridgeContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
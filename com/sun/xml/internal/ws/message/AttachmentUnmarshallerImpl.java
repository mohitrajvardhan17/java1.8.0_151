package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.resources.EncodingMessages;
import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.ws.WebServiceException;

public final class AttachmentUnmarshallerImpl
  extends AttachmentUnmarshaller
{
  private final AttachmentSet attachments;
  
  public AttachmentUnmarshallerImpl(AttachmentSet paramAttachmentSet)
  {
    attachments = paramAttachmentSet;
  }
  
  public DataHandler getAttachmentAsDataHandler(String paramString)
  {
    Attachment localAttachment = attachments.get(stripScheme(paramString));
    if (localAttachment == null) {
      throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(paramString));
    }
    return localAttachment.asDataHandler();
  }
  
  public byte[] getAttachmentAsByteArray(String paramString)
  {
    Attachment localAttachment = attachments.get(stripScheme(paramString));
    if (localAttachment == null) {
      throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(paramString));
    }
    return localAttachment.asByteArray();
  }
  
  private String stripScheme(String paramString)
  {
    if (paramString.startsWith("cid:")) {
      paramString = paramString.substring(4);
    }
    return paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\AttachmentUnmarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
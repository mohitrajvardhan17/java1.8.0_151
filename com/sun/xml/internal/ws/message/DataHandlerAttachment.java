package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Attachment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

public final class DataHandlerAttachment
  implements Attachment
{
  private final DataHandler dh;
  private final String contentId;
  String contentIdNoAngleBracket;
  
  public DataHandlerAttachment(@NotNull String paramString, @NotNull DataHandler paramDataHandler)
  {
    dh = paramDataHandler;
    contentId = paramString;
  }
  
  public String getContentId()
  {
    if (contentIdNoAngleBracket == null)
    {
      contentIdNoAngleBracket = contentId;
      if ((contentIdNoAngleBracket != null) && (contentIdNoAngleBracket.charAt(0) == '<')) {
        contentIdNoAngleBracket = contentIdNoAngleBracket.substring(1, contentIdNoAngleBracket.length() - 1);
      }
    }
    return contentIdNoAngleBracket;
  }
  
  public String getContentType()
  {
    return dh.getContentType();
  }
  
  public byte[] asByteArray()
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      dh.writeTo(localByteArrayOutputStream);
      return localByteArrayOutputStream.toByteArray();
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
  }
  
  public DataHandler asDataHandler()
  {
    return dh;
  }
  
  public Source asSource()
  {
    try
    {
      return new StreamSource(dh.getInputStream());
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
  }
  
  public InputStream asInputStream()
  {
    try
    {
      return dh.getInputStream();
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    dh.writeTo(paramOutputStream);
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    AttachmentPart localAttachmentPart = paramSOAPMessage.createAttachmentPart();
    localAttachmentPart.setDataHandler(dh);
    localAttachmentPart.setContentId(contentId);
    paramSOAPMessage.addAttachmentPart(localAttachmentPart);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\DataHandlerAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
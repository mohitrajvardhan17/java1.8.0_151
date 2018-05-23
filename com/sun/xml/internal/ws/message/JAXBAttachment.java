package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceException;

public final class JAXBAttachment
  implements Attachment, DataSource
{
  private final String contentId;
  private final String mimeType;
  private final Object jaxbObject;
  private final XMLBridge bridge;
  
  public JAXBAttachment(@NotNull String paramString1, Object paramObject, XMLBridge paramXMLBridge, String paramString2)
  {
    contentId = paramString1;
    jaxbObject = paramObject;
    bridge = paramXMLBridge;
    mimeType = paramString2;
  }
  
  public String getContentId()
  {
    return contentId;
  }
  
  public String getContentType()
  {
    return mimeType;
  }
  
  public byte[] asByteArray()
  {
    ByteArrayBuffer localByteArrayBuffer = new ByteArrayBuffer();
    try
    {
      writeTo(localByteArrayBuffer);
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
    return localByteArrayBuffer.getRawData();
  }
  
  public DataHandler asDataHandler()
  {
    return new DataSourceStreamingDataHandler(this);
  }
  
  public Source asSource()
  {
    return new StreamSource(asInputStream());
  }
  
  public InputStream asInputStream()
  {
    ByteArrayBuffer localByteArrayBuffer = new ByteArrayBuffer();
    try
    {
      writeTo(localByteArrayBuffer);
    }
    catch (IOException localIOException)
    {
      throw new WebServiceException(localIOException);
    }
    return localByteArrayBuffer.newInputStream();
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    try
    {
      bridge.marshal(jaxbObject, paramOutputStream, null, null);
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException(localJAXBException);
    }
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    AttachmentPart localAttachmentPart = paramSOAPMessage.createAttachmentPart();
    localAttachmentPart.setDataHandler(asDataHandler());
    localAttachmentPart.setContentId(contentId);
    paramSOAPMessage.addAttachmentPart(localAttachmentPart);
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    return asInputStream();
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
  
  public String getName()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\JAXBAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
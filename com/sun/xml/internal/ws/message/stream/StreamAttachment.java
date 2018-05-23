package com.sun.xml.internal.ws.message.stream;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.encoding.DataSourceStreamingDataHandler;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

public class StreamAttachment
  implements Attachment
{
  private final String contentId;
  private final String contentType;
  private final ByteArrayBuffer byteArrayBuffer;
  private final byte[] data;
  private final int len;
  
  public StreamAttachment(ByteArrayBuffer paramByteArrayBuffer, String paramString1, String paramString2)
  {
    contentId = paramString1;
    contentType = paramString2;
    byteArrayBuffer = paramByteArrayBuffer;
    data = byteArrayBuffer.getRawData();
    len = byteArrayBuffer.size();
  }
  
  public String getContentId()
  {
    return contentId;
  }
  
  public String getContentType()
  {
    return contentType;
  }
  
  public byte[] asByteArray()
  {
    return byteArrayBuffer.toByteArray();
  }
  
  public DataHandler asDataHandler()
  {
    return new DataSourceStreamingDataHandler(new ByteArrayDataSource(data, 0, len, getContentType()));
  }
  
  public Source asSource()
  {
    return new StreamSource(new ByteArrayInputStream(data, 0, len));
  }
  
  public InputStream asInputStream()
  {
    return byteArrayBuffer.newInputStream();
  }
  
  public Base64Data asBase64Data()
  {
    Base64Data localBase64Data = new Base64Data();
    localBase64Data.set(data, len, contentType);
    return localBase64Data;
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    byteArrayBuffer.writeTo(paramOutputStream);
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    AttachmentPart localAttachmentPart = paramSOAPMessage.createAttachmentPart();
    localAttachmentPart.setRawContentBytes(data, 0, len, getContentType());
    localAttachmentPart.setContentId(contentId);
    paramSOAPMessage.addAttachmentPart(localAttachmentPart);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\stream\StreamAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
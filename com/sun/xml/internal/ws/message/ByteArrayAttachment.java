package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.encoding.DataSourceStreamingDataHandler;
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

public final class ByteArrayAttachment
  implements Attachment
{
  private final String contentId;
  private byte[] data;
  private int start;
  private final int len;
  private final String mimeType;
  
  public ByteArrayAttachment(@NotNull String paramString1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString2)
  {
    contentId = paramString1;
    data = paramArrayOfByte;
    start = paramInt1;
    len = paramInt2;
    mimeType = paramString2;
  }
  
  public ByteArrayAttachment(@NotNull String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    this(paramString1, paramArrayOfByte, 0, paramArrayOfByte.length, paramString2);
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
    if ((start != 0) || (len != data.length))
    {
      byte[] arrayOfByte = new byte[len];
      System.arraycopy(data, start, arrayOfByte, 0, len);
      start = 0;
      data = arrayOfByte;
    }
    return data;
  }
  
  public DataHandler asDataHandler()
  {
    return new DataSourceStreamingDataHandler(new ByteArrayDataSource(data, start, len, getContentType()));
  }
  
  public Source asSource()
  {
    return new StreamSource(asInputStream());
  }
  
  public InputStream asInputStream()
  {
    return new ByteArrayInputStream(data, start, len);
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(asByteArray());
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    AttachmentPart localAttachmentPart = paramSOAPMessage.createAttachmentPart();
    localAttachmentPart.setDataHandler(asDataHandler());
    localAttachmentPart.setContentId(contentId);
    paramSOAPMessage.addAttachmentPart(localAttachmentPart);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\ByteArrayAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
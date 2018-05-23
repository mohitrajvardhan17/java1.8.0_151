package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import javax.activation.DataSource;

public final class MimePartDataSource
  implements DataSource
{
  private final MimeBodyPart part;
  
  public MimePartDataSource(MimeBodyPart paramMimeBodyPart)
  {
    part = paramMimeBodyPart;
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    try
    {
      InputStream localInputStream = part.getContentStream();
      String str = part.getEncoding();
      if (str != null) {
        return MimeUtility.decode(localInputStream, str);
      }
      return localInputStream;
    }
    catch (MessagingException localMessagingException)
    {
      throw new IOException(localMessagingException.getMessage());
    }
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    throw new UnknownServiceException();
  }
  
  public String getContentType()
  {
    return part.getContentType();
  }
  
  public String getName()
  {
    try
    {
      return part.getFileName();
    }
    catch (MessagingException localMessagingException) {}
    return "";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\MimePartDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
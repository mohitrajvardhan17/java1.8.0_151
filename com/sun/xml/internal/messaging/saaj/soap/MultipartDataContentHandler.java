package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class MultipartDataContentHandler
  implements DataContentHandler
{
  private ActivationDataFlavor myDF = new ActivationDataFlavor(MimeMultipart.class, "multipart/mixed", "Multipart");
  
  public MultipartDataContentHandler() {}
  
  public DataFlavor[] getTransferDataFlavors()
  {
    return new DataFlavor[] { myDF };
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
  {
    if (myDF.equals(paramDataFlavor)) {
      return getContent(paramDataSource);
    }
    return null;
  }
  
  public Object getContent(DataSource paramDataSource)
  {
    try
    {
      return new MimeMultipart(paramDataSource, new ContentType(paramDataSource.getContentType()));
    }
    catch (Exception localException) {}
    return null;
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if ((paramObject instanceof MimeMultipart)) {
      try
      {
        ByteOutputStream localByteOutputStream = null;
        if ((paramOutputStream instanceof ByteOutputStream)) {
          localByteOutputStream = (ByteOutputStream)paramOutputStream;
        } else {
          throw new IOException("Input Stream expected to be a com.sun.xml.internal.messaging.saaj.util.ByteOutputStream, but found " + paramOutputStream.getClass().getName());
        }
        ((MimeMultipart)paramObject).writeTo(localByteOutputStream);
      }
      catch (Exception localException)
      {
        throw new IOException(localException.toString());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\MultipartDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
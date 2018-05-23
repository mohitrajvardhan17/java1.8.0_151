package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
import javax.xml.transform.Source;

public class FastInfosetDataContentHandler
  implements DataContentHandler
{
  public static final String STR_SRC = "com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource";
  
  public FastInfosetDataContentHandler() {}
  
  public DataFlavor[] getTransferDataFlavors()
  {
    DataFlavor[] arrayOfDataFlavor = new DataFlavor[1];
    arrayOfDataFlavor[0] = new ActivationDataFlavor(FastInfosetReflection.getFastInfosetSource_class(), "application/fastinfoset", "Fast Infoset");
    return arrayOfDataFlavor;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
    throws IOException
  {
    if (paramDataFlavor.getMimeType().startsWith("application/fastinfoset")) {
      try
      {
        if (paramDataFlavor.getRepresentationClass().getName().equals("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource")) {
          return FastInfosetReflection.FastInfosetSource_new(paramDataSource.getInputStream());
        }
      }
      catch (Exception localException)
      {
        throw new IOException(localException.getMessage());
      }
    }
    return null;
  }
  
  public Object getContent(DataSource paramDataSource)
    throws IOException
  {
    try
    {
      return FastInfosetReflection.FastInfosetSource_new(paramDataSource.getInputStream());
    }
    catch (Exception localException)
    {
      throw new IOException(localException.getMessage());
    }
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if (!paramString.equals("application/fastinfoset")) {
      throw new IOException("Invalid content type \"" + paramString + "\" for FastInfosetDCH");
    }
    try
    {
      InputStream localInputStream = FastInfosetReflection.FastInfosetSource_getInputStream((Source)paramObject);
      byte[] arrayOfByte = new byte['က'];
      int i;
      while ((i = localInputStream.read(arrayOfByte)) != -1) {
        paramOutputStream.write(arrayOfByte, 0, i);
      }
    }
    catch (Exception localException)
    {
      throw new IOException("Error copying FI source to output stream " + localException.getMessage());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\FastInfosetDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
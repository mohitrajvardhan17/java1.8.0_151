package com.sun.xml.internal.messaging.saaj.soap;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class GifDataContentHandler
  extends Component
  implements DataContentHandler
{
  private static ActivationDataFlavor myDF = new ActivationDataFlavor(Image.class, "image/gif", "GIF Image");
  
  public GifDataContentHandler() {}
  
  protected ActivationDataFlavor getDF()
  {
    return myDF;
  }
  
  public DataFlavor[] getTransferDataFlavors()
  {
    return new DataFlavor[] { getDF() };
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
    throws IOException
  {
    if (getDF().equals(paramDataFlavor)) {
      return getContent(paramDataSource);
    }
    return null;
  }
  
  public Object getContent(DataSource paramDataSource)
    throws IOException
  {
    InputStream localInputStream = paramDataSource.getInputStream();
    int i = 0;
    Object localObject = new byte['Ѐ'];
    int j;
    while ((j = localInputStream.read((byte[])localObject, i, localObject.length - i)) != -1)
    {
      i += j;
      if (i >= localObject.length)
      {
        int k = localObject.length;
        if (k < 262144) {
          k += k;
        } else {
          k += 262144;
        }
        byte[] arrayOfByte = new byte[k];
        System.arraycopy(localObject, 0, arrayOfByte, 0, i);
        localObject = arrayOfByte;
      }
    }
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    return localToolkit.createImage((byte[])localObject, 0, i);
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if ((paramObject != null) && (!(paramObject instanceof Image))) {
      throw new IOException("\"" + getDF().getMimeType() + "\" DataContentHandler requires Image object, was given object of type " + paramObject.getClass().toString());
    }
    throw new IOException(getDF().getMimeType() + " encoding not supported");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\GifDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
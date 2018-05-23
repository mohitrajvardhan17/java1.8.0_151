package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

class ObjectDataContentHandler
  implements DataContentHandler
{
  private DataFlavor[] transferFlavors = null;
  private Object obj;
  private String mimeType;
  private DataContentHandler dch = null;
  
  public ObjectDataContentHandler(DataContentHandler paramDataContentHandler, Object paramObject, String paramString)
  {
    obj = paramObject;
    mimeType = paramString;
    dch = paramDataContentHandler;
  }
  
  public DataContentHandler getDCH()
  {
    return dch;
  }
  
  public synchronized DataFlavor[] getTransferDataFlavors()
  {
    if (transferFlavors == null) {
      if (dch != null)
      {
        transferFlavors = dch.getTransferDataFlavors();
      }
      else
      {
        transferFlavors = new DataFlavor[1];
        transferFlavors[0] = new ActivationDataFlavor(obj.getClass(), mimeType, mimeType);
      }
    }
    return transferFlavors;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource)
    throws UnsupportedFlavorException, IOException
  {
    if (dch != null) {
      return dch.getTransferData(paramDataFlavor, paramDataSource);
    }
    if (paramDataFlavor.equals(getTransferDataFlavors()[0])) {
      return obj;
    }
    throw new UnsupportedFlavorException(paramDataFlavor);
  }
  
  public Object getContent(DataSource paramDataSource)
  {
    return obj;
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if (dch != null)
    {
      dch.writeTo(paramObject, paramString, paramOutputStream);
    }
    else if ((paramObject instanceof byte[]))
    {
      paramOutputStream.write((byte[])paramObject);
    }
    else if ((paramObject instanceof String))
    {
      OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(paramOutputStream);
      localOutputStreamWriter.write((String)paramObject);
      localOutputStreamWriter.flush();
    }
    else
    {
      throw new UnsupportedDataTypeException("no object DCH for MIME type " + mimeType);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\ObjectDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
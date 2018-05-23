package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;

class DataSourceDataContentHandler
  implements DataContentHandler
{
  private DataSource ds = null;
  private DataFlavor[] transferFlavors = null;
  private DataContentHandler dch = null;
  
  public DataSourceDataContentHandler(DataContentHandler paramDataContentHandler, DataSource paramDataSource)
  {
    ds = paramDataSource;
    dch = paramDataContentHandler;
  }
  
  public DataFlavor[] getTransferDataFlavors()
  {
    if (transferFlavors == null) {
      if (dch != null)
      {
        transferFlavors = dch.getTransferDataFlavors();
      }
      else
      {
        transferFlavors = new DataFlavor[1];
        transferFlavors[0] = new ActivationDataFlavor(ds.getContentType(), ds.getContentType());
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
      return paramDataSource.getInputStream();
    }
    throw new UnsupportedFlavorException(paramDataFlavor);
  }
  
  public Object getContent(DataSource paramDataSource)
    throws IOException
  {
    if (dch != null) {
      return dch.getContent(paramDataSource);
    }
    return paramDataSource.getInputStream();
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if (dch != null) {
      dch.writeTo(paramObject, paramString, paramOutputStream);
    } else {
      throw new UnsupportedDataTypeException("no DCH for content type " + ds.getContentType());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\DataSourceDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
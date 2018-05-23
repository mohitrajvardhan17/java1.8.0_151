package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DataHandlerDataSource
  implements DataSource
{
  DataHandler dataHandler = null;
  
  public DataHandlerDataSource(DataHandler paramDataHandler)
  {
    dataHandler = paramDataHandler;
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    return dataHandler.getInputStream();
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    return dataHandler.getOutputStream();
  }
  
  public String getContentType()
  {
    return dataHandler.getContentType();
  }
  
  public String getName()
  {
    return dataHandler.getName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\DataHandlerDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
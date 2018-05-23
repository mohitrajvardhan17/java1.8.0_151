package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface DataSource
{
  public abstract InputStream getInputStream()
    throws IOException;
  
  public abstract OutputStream getOutputStream()
    throws IOException;
  
  public abstract String getContentType();
  
  public abstract String getName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\DataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
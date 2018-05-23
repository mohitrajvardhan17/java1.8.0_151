package sun.util.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public abstract class XmlPropertiesProvider
{
  protected XmlPropertiesProvider() {}
  
  public abstract void load(Properties paramProperties, InputStream paramInputStream)
    throws IOException, InvalidPropertiesFormatException;
  
  public abstract void store(Properties paramProperties, OutputStream paramOutputStream, String paramString1, String paramString2)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\spi\XmlPropertiesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
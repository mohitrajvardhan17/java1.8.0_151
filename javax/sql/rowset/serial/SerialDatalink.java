package javax.sql.rowset.serial;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class SerialDatalink
  implements Serializable, Cloneable
{
  private URL url;
  private int baseType;
  private String baseTypeName;
  static final long serialVersionUID = 2826907821828733626L;
  
  public SerialDatalink(URL paramURL)
    throws SerialException
  {
    if (paramURL == null) {
      throw new SerialException("Cannot serialize empty URL instance");
    }
    url = paramURL;
  }
  
  public URL getDatalink()
    throws SerialException
  {
    URL localURL = null;
    try
    {
      localURL = new URL(url.toString());
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new SerialException("MalformedURLException: " + localMalformedURLException.getMessage());
    }
    return localURL;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof SerialDatalink))
    {
      SerialDatalink localSerialDatalink = (SerialDatalink)paramObject;
      return url.equals(url);
    }
    return false;
  }
  
  public int hashCode()
  {
    return 31 + url.hashCode();
  }
  
  public Object clone()
  {
    try
    {
      SerialDatalink localSerialDatalink = (SerialDatalink)super.clone();
      return localSerialDatalink;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\serial\SerialDatalink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
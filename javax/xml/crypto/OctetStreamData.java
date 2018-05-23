package javax.xml.crypto;

import java.io.InputStream;

public class OctetStreamData
  implements Data
{
  private InputStream octetStream;
  private String uri;
  private String mimeType;
  
  public OctetStreamData(InputStream paramInputStream)
  {
    if (paramInputStream == null) {
      throw new NullPointerException("octetStream is null");
    }
    octetStream = paramInputStream;
  }
  
  public OctetStreamData(InputStream paramInputStream, String paramString1, String paramString2)
  {
    if (paramInputStream == null) {
      throw new NullPointerException("octetStream is null");
    }
    octetStream = paramInputStream;
    uri = paramString1;
    mimeType = paramString2;
  }
  
  public InputStream getOctetStream()
  {
    return octetStream;
  }
  
  public String getURI()
  {
    return uri;
  }
  
  public String getMimeType()
  {
    return mimeType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\crypto\OctetStreamData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
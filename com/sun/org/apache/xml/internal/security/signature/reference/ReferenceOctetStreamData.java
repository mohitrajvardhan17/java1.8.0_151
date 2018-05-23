package com.sun.org.apache.xml.internal.security.signature.reference;

import java.io.InputStream;

public class ReferenceOctetStreamData
  implements ReferenceData
{
  private InputStream octetStream;
  private String uri;
  private String mimeType;
  
  public ReferenceOctetStreamData(InputStream paramInputStream)
  {
    if (paramInputStream == null) {
      throw new NullPointerException("octetStream is null");
    }
    octetStream = paramInputStream;
  }
  
  public ReferenceOctetStreamData(InputStream paramInputStream, String paramString1, String paramString2)
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\reference\ReferenceOctetStreamData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
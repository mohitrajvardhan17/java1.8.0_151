package javax.naming.ldap;

import com.sun.jndi.ldap.BerDecoder;
import java.io.IOException;

public final class PagedResultsResponseControl
  extends BasicControl
{
  public static final String OID = "1.2.840.113556.1.4.319";
  private static final long serialVersionUID = -8819778744844514666L;
  private int resultSize;
  private byte[] cookie;
  
  public PagedResultsResponseControl(String paramString, boolean paramBoolean, byte[] paramArrayOfByte)
    throws IOException
  {
    super(paramString, paramBoolean, paramArrayOfByte);
    BerDecoder localBerDecoder = new BerDecoder(paramArrayOfByte, 0, paramArrayOfByte.length);
    localBerDecoder.parseSeq(null);
    resultSize = localBerDecoder.parseInt();
    cookie = localBerDecoder.parseOctetString(4, null);
  }
  
  public int getResultSize()
  {
    return resultSize;
  }
  
  public byte[] getCookie()
  {
    if (cookie.length == 0) {
      return null;
    }
    return cookie;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\PagedResultsResponseControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
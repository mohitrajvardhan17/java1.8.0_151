package javax.naming.ldap;

import com.sun.jndi.ldap.BerEncoder;
import java.io.IOException;

public final class PagedResultsControl
  extends BasicControl
{
  public static final String OID = "1.2.840.113556.1.4.319";
  private static final byte[] EMPTY_COOKIE = new byte[0];
  private static final long serialVersionUID = 6684806685736844298L;
  
  public PagedResultsControl(int paramInt, boolean paramBoolean)
    throws IOException
  {
    super("1.2.840.113556.1.4.319", paramBoolean, null);
    value = setEncodedValue(paramInt, EMPTY_COOKIE);
  }
  
  public PagedResultsControl(int paramInt, byte[] paramArrayOfByte, boolean paramBoolean)
    throws IOException
  {
    super("1.2.840.113556.1.4.319", paramBoolean, null);
    if (paramArrayOfByte == null) {
      paramArrayOfByte = EMPTY_COOKIE;
    }
    value = setEncodedValue(paramInt, paramArrayOfByte);
  }
  
  private byte[] setEncodedValue(int paramInt, byte[] paramArrayOfByte)
    throws IOException
  {
    BerEncoder localBerEncoder = new BerEncoder(10 + paramArrayOfByte.length);
    localBerEncoder.beginSeq(48);
    localBerEncoder.encodeInt(paramInt);
    localBerEncoder.encodeOctetString(paramArrayOfByte, 4);
    localBerEncoder.endSeq();
    return localBerEncoder.getTrimmedBuf();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\PagedResultsControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.naming.ldap;

import com.sun.jndi.ldap.BerDecoder;
import com.sun.jndi.ldap.LdapCtx;
import java.io.IOException;
import javax.naming.NamingException;

public final class SortResponseControl
  extends BasicControl
{
  public static final String OID = "1.2.840.113556.1.4.474";
  private static final long serialVersionUID = 5142939176006310877L;
  private int resultCode = 0;
  private String badAttrId = null;
  
  public SortResponseControl(String paramString, boolean paramBoolean, byte[] paramArrayOfByte)
    throws IOException
  {
    super(paramString, paramBoolean, paramArrayOfByte);
    BerDecoder localBerDecoder = new BerDecoder(paramArrayOfByte, 0, paramArrayOfByte.length);
    localBerDecoder.parseSeq(null);
    resultCode = localBerDecoder.parseEnumeration();
    if ((localBerDecoder.bytesLeft() > 0) && (localBerDecoder.peekByte() == 128)) {
      badAttrId = localBerDecoder.parseStringWithTag(128, true, null);
    }
  }
  
  public boolean isSorted()
  {
    return resultCode == 0;
  }
  
  public int getResultCode()
  {
    return resultCode;
  }
  
  public String getAttributeID()
  {
    return badAttrId;
  }
  
  public NamingException getException()
  {
    return LdapCtx.mapErrorCode(resultCode, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\SortResponseControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
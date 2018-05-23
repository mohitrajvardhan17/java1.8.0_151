package javax.naming.ldap;

import com.sun.jndi.ldap.BerEncoder;
import java.io.IOException;

public final class SortControl
  extends BasicControl
{
  public static final String OID = "1.2.840.113556.1.4.473";
  private static final long serialVersionUID = -1965961680233330744L;
  
  public SortControl(String paramString, boolean paramBoolean)
    throws IOException
  {
    super("1.2.840.113556.1.4.473", paramBoolean, null);
    value = setEncodedValue(new SortKey[] { new SortKey(paramString) });
  }
  
  public SortControl(String[] paramArrayOfString, boolean paramBoolean)
    throws IOException
  {
    super("1.2.840.113556.1.4.473", paramBoolean, null);
    SortKey[] arrayOfSortKey = new SortKey[paramArrayOfString.length];
    for (int i = 0; i < paramArrayOfString.length; i++) {
      arrayOfSortKey[i] = new SortKey(paramArrayOfString[i]);
    }
    value = setEncodedValue(arrayOfSortKey);
  }
  
  public SortControl(SortKey[] paramArrayOfSortKey, boolean paramBoolean)
    throws IOException
  {
    super("1.2.840.113556.1.4.473", paramBoolean, null);
    value = setEncodedValue(paramArrayOfSortKey);
  }
  
  private byte[] setEncodedValue(SortKey[] paramArrayOfSortKey)
    throws IOException
  {
    BerEncoder localBerEncoder = new BerEncoder(30 * paramArrayOfSortKey.length + 10);
    localBerEncoder.beginSeq(48);
    for (int i = 0; i < paramArrayOfSortKey.length; i++)
    {
      localBerEncoder.beginSeq(48);
      localBerEncoder.encodeString(paramArrayOfSortKey[i].getAttributeID(), true);
      String str;
      if ((str = paramArrayOfSortKey[i].getMatchingRuleID()) != null) {
        localBerEncoder.encodeString(str, 128, true);
      }
      if (!paramArrayOfSortKey[i].isAscending()) {
        localBerEncoder.encodeBoolean(true, 129);
      }
      localBerEncoder.endSeq();
    }
    localBerEncoder.endSeq();
    return localBerEncoder.getTrimmedBuf();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\SortControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.security.krb5.internal.ktab;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;

public class KeyTabEntry
  implements KeyTabConstants
{
  PrincipalName service;
  Realm realm;
  KerberosTime timestamp;
  int keyVersion;
  int keyType;
  byte[] keyblock = null;
  boolean DEBUG = Krb5.DEBUG;
  
  public KeyTabEntry(PrincipalName paramPrincipalName, Realm paramRealm, KerberosTime paramKerberosTime, int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    service = paramPrincipalName;
    realm = paramRealm;
    timestamp = paramKerberosTime;
    keyVersion = paramInt1;
    keyType = paramInt2;
    if (paramArrayOfByte != null) {
      keyblock = ((byte[])paramArrayOfByte.clone());
    }
  }
  
  public PrincipalName getService()
  {
    return service;
  }
  
  public EncryptionKey getKey()
  {
    EncryptionKey localEncryptionKey = new EncryptionKey(keyblock, keyType, new Integer(keyVersion));
    return localEncryptionKey;
  }
  
  public String getKeyString()
  {
    StringBuffer localStringBuffer = new StringBuffer("0x");
    for (int i = 0; i < keyblock.length; i++) {
      localStringBuffer.append(String.format("%02x", new Object[] { Integer.valueOf(keyblock[i] & 0xFF) }));
    }
    return localStringBuffer.toString();
  }
  
  public int entryLength()
  {
    int i = 0;
    String[] arrayOfString = service.getNameStrings();
    for (int j = 0; j < arrayOfString.length; j++) {
      try
      {
        i += 2 + arrayOfString[j].getBytes("8859_1").length;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException1) {}
    }
    j = 0;
    try
    {
      j = realm.toString().getBytes("8859_1").length;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException2) {}
    int k = 4 + j + i + 4 + 4 + 1 + 2 + 2 + keyblock.length;
    if (DEBUG) {
      System.out.println(">>> KeyTabEntry: key tab entry size is " + k);
    }
    return k;
  }
  
  public KerberosTime getTimeStamp()
  {
    return timestamp;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ktab\KeyTabEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
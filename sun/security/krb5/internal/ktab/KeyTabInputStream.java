package sun.security.krb5.internal.ktab;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.Realm;
import sun.security.krb5.RealmException;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.util.KrbDataInputStream;

public class KeyTabInputStream
  extends KrbDataInputStream
  implements KeyTabConstants
{
  boolean DEBUG = Krb5.DEBUG;
  int index;
  
  public KeyTabInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  int readEntryLength()
    throws IOException
  {
    return read(4);
  }
  
  KeyTabEntry readEntry(int paramInt1, int paramInt2)
    throws IOException, RealmException
  {
    index = paramInt1;
    if (index == 0) {
      return null;
    }
    if (index < 0)
    {
      skip(Math.abs(index));
      return null;
    }
    int i = read(2);
    index -= 2;
    if (paramInt2 == 1281) {
      i--;
    }
    Realm localRealm = new Realm(readName());
    String[] arrayOfString = new String[i];
    for (int j = 0; j < i; j++) {
      arrayOfString[j] = readName();
    }
    j = read(4);
    index -= 4;
    PrincipalName localPrincipalName = new PrincipalName(j, arrayOfString, localRealm);
    KerberosTime localKerberosTime = readTimeStamp();
    int k = read() & 0xFF;
    index -= 1;
    int m = read(2);
    index -= 2;
    int n = read(2);
    index -= 2;
    byte[] arrayOfByte = readKey(n);
    index -= n;
    if (index >= 4)
    {
      int i1 = read(4);
      if (i1 != 0) {
        k = i1;
      }
      index -= 4;
    }
    if (index < 0) {
      throw new RealmException("Keytab is corrupted");
    }
    skip(index);
    return new KeyTabEntry(localPrincipalName, localRealm, localKerberosTime, k, m, arrayOfByte);
  }
  
  byte[] readKey(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    read(arrayOfByte, 0, paramInt);
    return arrayOfByte;
  }
  
  KerberosTime readTimeStamp()
    throws IOException
  {
    index -= 4;
    return new KerberosTime(read(4) * 1000L);
  }
  
  String readName()
    throws IOException
  {
    int i = read(2);
    index -= 2;
    byte[] arrayOfByte = new byte[i];
    read(arrayOfByte, 0, i);
    index -= i;
    String str = new String(arrayOfByte);
    if (DEBUG) {
      System.out.println(">>> KeyTabInputStream, readName(): " + str);
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ktab\KeyTabInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
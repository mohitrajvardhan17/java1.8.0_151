package sun.security.krb5.internal.ktab;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.util.KrbDataOutputStream;

public class KeyTabOutputStream
  extends KrbDataOutputStream
  implements KeyTabConstants
{
  private KeyTabEntry entry;
  private int keyType;
  private byte[] keyValue;
  public int version;
  
  public KeyTabOutputStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }
  
  public void writeVersion(int paramInt)
    throws IOException
  {
    version = paramInt;
    write16(paramInt);
  }
  
  public void writeEntry(KeyTabEntry paramKeyTabEntry)
    throws IOException
  {
    write32(paramKeyTabEntry.entryLength());
    String[] arrayOfString = service.getNameStrings();
    int i = arrayOfString.length;
    if (version == 1281) {
      write16(i + 1);
    } else {
      write16(i);
    }
    byte[] arrayOfByte = null;
    try
    {
      arrayOfByte = service.getRealmString().getBytes("8859_1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException1) {}
    write16(arrayOfByte.length);
    write(arrayOfByte);
    for (int j = 0; j < i; j++) {
      try
      {
        write16(arrayOfString[j].getBytes("8859_1").length);
        write(arrayOfString[j].getBytes("8859_1"));
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException2) {}
    }
    write32(service.getNameType());
    write32((int)(timestamp.getTime() / 1000L));
    write8(keyVersion % 256);
    write16(keyType);
    write16(keyblock.length);
    write(keyblock);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\ktab\KeyTabOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
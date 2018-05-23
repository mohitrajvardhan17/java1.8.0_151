package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosFlags;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

public class APOptions
  extends KerberosFlags
{
  public APOptions()
  {
    super(32);
  }
  
  public APOptions(int paramInt)
    throws Asn1Exception
  {
    super(32);
    set(paramInt, true);
  }
  
  public APOptions(int paramInt, byte[] paramArrayOfByte)
    throws Asn1Exception
  {
    super(paramInt, paramArrayOfByte);
    if ((paramInt > paramArrayOfByte.length * 8) || (paramInt > 32)) {
      throw new Asn1Exception(502);
    }
  }
  
  public APOptions(boolean[] paramArrayOfBoolean)
    throws Asn1Exception
  {
    super(paramArrayOfBoolean);
    if (paramArrayOfBoolean.length > 32) {
      throw new Asn1Exception(502);
    }
  }
  
  public APOptions(DerValue paramDerValue)
    throws IOException, Asn1Exception
  {
    this(paramDerValue.getUnalignedBitString(true).toBooleanArray());
  }
  
  public static APOptions parse(DerInputStream paramDerInputStream, byte paramByte, boolean paramBoolean)
    throws Asn1Exception, IOException
  {
    if ((paramBoolean) && (((byte)paramDerInputStream.peekByte() & 0x1F) != paramByte)) {
      return null;
    }
    DerValue localDerValue1 = paramDerInputStream.getDerValue();
    if (paramByte != (localDerValue1.getTag() & 0x1F)) {
      throw new Asn1Exception(906);
    }
    DerValue localDerValue2 = localDerValue1.getData().getDerValue();
    return new APOptions(localDerValue2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\APOptions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
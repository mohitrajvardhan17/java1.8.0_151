package sun.security.krb5.internal.util;

import java.io.IOException;
import java.util.Arrays;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;

public class KerberosFlags
{
  BitArray bits;
  protected static final int BITS_PER_UNIT = 8;
  
  public KerberosFlags(int paramInt)
    throws IllegalArgumentException
  {
    bits = new BitArray(paramInt);
  }
  
  public KerberosFlags(int paramInt, byte[] paramArrayOfByte)
    throws IllegalArgumentException
  {
    bits = new BitArray(paramInt, paramArrayOfByte);
    if (paramInt != 32) {
      bits = new BitArray(Arrays.copyOf(bits.toBooleanArray(), 32));
    }
  }
  
  public KerberosFlags(boolean[] paramArrayOfBoolean)
  {
    bits = new BitArray(paramArrayOfBoolean.length == 32 ? paramArrayOfBoolean : Arrays.copyOf(paramArrayOfBoolean, 32));
  }
  
  public void set(int paramInt, boolean paramBoolean)
  {
    bits.set(paramInt, paramBoolean);
  }
  
  public boolean get(int paramInt)
  {
    return bits.get(paramInt);
  }
  
  public boolean[] toBooleanArray()
  {
    return bits.toBooleanArray();
  }
  
  public byte[] asn1Encode()
    throws IOException
  {
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putUnalignedBitString(bits);
    return localDerOutputStream.toByteArray();
  }
  
  public String toString()
  {
    return bits.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\util\KerberosFlags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
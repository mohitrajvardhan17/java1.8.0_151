package sun.security.x509;

import java.io.IOException;
import sun.security.util.BitArray;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class UniqueIdentity
{
  private BitArray id;
  
  public UniqueIdentity(BitArray paramBitArray)
  {
    id = paramBitArray;
  }
  
  public UniqueIdentity(byte[] paramArrayOfByte)
  {
    id = new BitArray(paramArrayOfByte.length * 8, paramArrayOfByte);
  }
  
  public UniqueIdentity(DerInputStream paramDerInputStream)
    throws IOException
  {
    DerValue localDerValue = paramDerInputStream.getDerValue();
    id = localDerValue.getUnalignedBitString(true);
  }
  
  public UniqueIdentity(DerValue paramDerValue)
    throws IOException
  {
    id = paramDerValue.getUnalignedBitString(true);
  }
  
  public String toString()
  {
    return "UniqueIdentity:" + id.toString() + "\n";
  }
  
  public void encode(DerOutputStream paramDerOutputStream, byte paramByte)
    throws IOException
  {
    byte[] arrayOfByte = id.toByteArray();
    int i = arrayOfByte.length * 8 - id.length();
    paramDerOutputStream.write(paramByte);
    paramDerOutputStream.putLength(arrayOfByte.length + 1);
    paramDerOutputStream.write(i);
    paramDerOutputStream.write(arrayOfByte);
  }
  
  public boolean[] getId()
  {
    if (id == null) {
      return null;
    }
    return id.toBooleanArray();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\UniqueIdentity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
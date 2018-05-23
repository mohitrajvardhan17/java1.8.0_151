package java.security.cert;

import java.io.IOException;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class PolicyQualifierInfo
{
  private byte[] mEncoded;
  private String mId;
  private byte[] mData;
  private String pqiString;
  
  public PolicyQualifierInfo(byte[] paramArrayOfByte)
    throws IOException
  {
    mEncoded = ((byte[])paramArrayOfByte.clone());
    DerValue localDerValue = new DerValue(mEncoded);
    if (tag != 48) {
      throw new IOException("Invalid encoding for PolicyQualifierInfo");
    }
    mId = data.getDerValue().getOID().toString();
    byte[] arrayOfByte = data.toByteArray();
    if (arrayOfByte == null)
    {
      mData = null;
    }
    else
    {
      mData = new byte[arrayOfByte.length];
      System.arraycopy(arrayOfByte, 0, mData, 0, arrayOfByte.length);
    }
  }
  
  public final String getPolicyQualifierId()
  {
    return mId;
  }
  
  public final byte[] getEncoded()
  {
    return (byte[])mEncoded.clone();
  }
  
  public final byte[] getPolicyQualifier()
  {
    return mData == null ? null : (byte[])mData.clone();
  }
  
  public String toString()
  {
    if (pqiString != null) {
      return pqiString;
    }
    HexDumpEncoder localHexDumpEncoder = new HexDumpEncoder();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("PolicyQualifierInfo: [\n");
    localStringBuffer.append("  qualifierID: " + mId + "\n");
    localStringBuffer.append("  qualifier: " + (mData == null ? "null" : localHexDumpEncoder.encodeBuffer(mData)) + "\n");
    localStringBuffer.append("]");
    pqiString = localStringBuffer.toString();
    return pqiString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\cert\PolicyQualifierInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
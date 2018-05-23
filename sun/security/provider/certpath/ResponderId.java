package sun.security.provider.certpath;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;
import javax.security.auth.x500.X500Principal;
import sun.security.util.DerValue;
import sun.security.x509.KeyIdentifier;

public final class ResponderId
{
  private Type type;
  private X500Principal responderName;
  private KeyIdentifier responderKeyId;
  private byte[] encodedRid;
  
  public ResponderId(X500Principal paramX500Principal)
    throws IOException
  {
    responderName = paramX500Principal;
    responderKeyId = null;
    encodedRid = principalToBytes();
    type = Type.BY_NAME;
  }
  
  public ResponderId(PublicKey paramPublicKey)
    throws IOException
  {
    responderKeyId = new KeyIdentifier(paramPublicKey);
    responderName = null;
    encodedRid = keyIdToBytes();
    type = Type.BY_KEY;
  }
  
  public ResponderId(byte[] paramArrayOfByte)
    throws IOException
  {
    DerValue localDerValue = new DerValue(paramArrayOfByte);
    if ((localDerValue.isContextSpecific((byte)Type.BY_NAME.value())) && (localDerValue.isConstructed()))
    {
      responderName = new X500Principal(localDerValue.getDataBytes());
      encodedRid = principalToBytes();
      type = Type.BY_NAME;
    }
    else if ((localDerValue.isContextSpecific((byte)Type.BY_KEY.value())) && (localDerValue.isConstructed()))
    {
      responderKeyId = new KeyIdentifier(new DerValue(localDerValue.getDataBytes()));
      encodedRid = keyIdToBytes();
      type = Type.BY_KEY;
    }
    else
    {
      throw new IOException("Invalid ResponderId content");
    }
  }
  
  public byte[] getEncoded()
  {
    return (byte[])encodedRid.clone();
  }
  
  public Type getType()
  {
    return type;
  }
  
  public int length()
  {
    return encodedRid.length;
  }
  
  public X500Principal getResponderName()
  {
    return responderName;
  }
  
  public KeyIdentifier getKeyIdentifier()
  {
    return responderKeyId;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ResponderId))
    {
      ResponderId localResponderId = (ResponderId)paramObject;
      return Arrays.equals(encodedRid, localResponderId.getEncoded());
    }
    return false;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(encodedRid);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    switch (type)
    {
    case BY_NAME: 
      localStringBuilder.append(type).append(": ").append(responderName);
      break;
    case BY_KEY: 
      localStringBuilder.append(type).append(": ");
      for (byte b : responderKeyId.getIdentifier()) {
        localStringBuilder.append(String.format("%02X", new Object[] { Byte.valueOf(b) }));
      }
      break;
    default: 
      localStringBuilder.append("Unknown ResponderId Type: ").append(type);
    }
    return localStringBuilder.toString();
  }
  
  private byte[] principalToBytes()
    throws IOException
  {
    DerValue localDerValue = new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)Type.BY_NAME.value()), responderName.getEncoded());
    return localDerValue.toByteArray();
  }
  
  private byte[] keyIdToBytes()
    throws IOException
  {
    DerValue localDerValue1 = new DerValue((byte)4, responderKeyId.getIdentifier());
    DerValue localDerValue2 = new DerValue(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)Type.BY_KEY.value()), localDerValue1.toByteArray());
    return localDerValue2.toByteArray();
  }
  
  public static enum Type
  {
    BY_NAME(1, "byName"),  BY_KEY(2, "byKey");
    
    private final int tagNumber;
    private final String ridTypeName;
    
    private Type(int paramInt, String paramString)
    {
      tagNumber = paramInt;
      ridTypeName = paramString;
    }
    
    public int value()
    {
      return tagNumber;
    }
    
    public String toString()
    {
      return ridTypeName;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\certpath\ResponderId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
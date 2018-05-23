package sun.security.x509;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class OtherName
  implements GeneralNameInterface
{
  private String name;
  private ObjectIdentifier oid;
  private byte[] nameValue = null;
  private GeneralNameInterface gni = null;
  private static final byte TAG_VALUE = 0;
  private int myhash = -1;
  
  public OtherName(ObjectIdentifier paramObjectIdentifier, byte[] paramArrayOfByte)
    throws IOException
  {
    if ((paramObjectIdentifier == null) || (paramArrayOfByte == null)) {
      throw new NullPointerException("parameters may not be null");
    }
    oid = paramObjectIdentifier;
    nameValue = paramArrayOfByte;
    gni = getGNI(paramObjectIdentifier, paramArrayOfByte);
    if (gni != null) {
      name = gni.toString();
    } else {
      name = ("Unrecognized ObjectIdentifier: " + paramObjectIdentifier.toString());
    }
  }
  
  public OtherName(DerValue paramDerValue)
    throws IOException
  {
    DerInputStream localDerInputStream = paramDerValue.toDerInputStream();
    oid = localDerInputStream.getOID();
    DerValue localDerValue = localDerInputStream.getDerValue();
    nameValue = localDerValue.toByteArray();
    gni = getGNI(oid, nameValue);
    if (gni != null) {
      name = gni.toString();
    } else {
      name = ("Unrecognized ObjectIdentifier: " + oid.toString());
    }
  }
  
  public ObjectIdentifier getOID()
  {
    return oid;
  }
  
  public byte[] getNameValue()
  {
    return (byte[])nameValue.clone();
  }
  
  private GeneralNameInterface getGNI(ObjectIdentifier paramObjectIdentifier, byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      Class localClass = OIDMap.getClass(paramObjectIdentifier);
      if (localClass == null) {
        return null;
      }
      Class[] arrayOfClass = { Object.class };
      Constructor localConstructor = localClass.getConstructor(arrayOfClass);
      Object[] arrayOfObject = { paramArrayOfByte };
      GeneralNameInterface localGeneralNameInterface = (GeneralNameInterface)localConstructor.newInstance(arrayOfObject);
      return localGeneralNameInterface;
    }
    catch (Exception localException)
    {
      throw new IOException("Instantiation error: " + localException, localException);
    }
  }
  
  public int getType()
  {
    return 0;
  }
  
  public void encode(DerOutputStream paramDerOutputStream)
    throws IOException
  {
    if (gni != null)
    {
      gni.encode(paramDerOutputStream);
      return;
    }
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putOID(oid);
    localDerOutputStream.write(DerValue.createTag((byte)Byte.MIN_VALUE, true, (byte)0), nameValue);
    paramDerOutputStream.write((byte)48, localDerOutputStream);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof OtherName)) {
      return false;
    }
    OtherName localOtherName = (OtherName)paramObject;
    if (!oid.equals(oid)) {
      return false;
    }
    GeneralNameInterface localGeneralNameInterface = null;
    try
    {
      localGeneralNameInterface = getGNI(oid, nameValue);
    }
    catch (IOException localIOException)
    {
      return false;
    }
    boolean bool;
    if (localGeneralNameInterface != null) {
      try
      {
        bool = localGeneralNameInterface.constrains(this) == 0;
      }
      catch (UnsupportedOperationException localUnsupportedOperationException)
      {
        bool = false;
      }
    } else {
      bool = Arrays.equals(nameValue, nameValue);
    }
    return bool;
  }
  
  public int hashCode()
  {
    if (myhash == -1)
    {
      myhash = (37 + oid.hashCode());
      for (int i = 0; i < nameValue.length; i++) {
        myhash = (37 * myhash + nameValue[i]);
      }
    }
    return myhash;
  }
  
  public String toString()
  {
    return "Other-Name: " + name;
  }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface)
  {
    int i;
    if (paramGeneralNameInterface == null) {
      i = -1;
    } else if (paramGeneralNameInterface.getType() != 0) {
      i = -1;
    } else {
      throw new UnsupportedOperationException("Narrowing, widening, and matching are not supported for OtherName.");
    }
    return i;
  }
  
  public int subtreeDepth()
  {
    throw new UnsupportedOperationException("subtreeDepth() not supported for generic OtherName");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\x509\OtherName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
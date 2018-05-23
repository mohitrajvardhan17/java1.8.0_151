package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class StubIORImpl
{
  private int hashCode;
  private byte[] typeData;
  private int[] profileTags;
  private byte[][] profileData;
  
  public StubIORImpl()
  {
    hashCode = 0;
    typeData = null;
    profileTags = null;
    profileData = ((byte[][])null);
  }
  
  public String getRepositoryId()
  {
    if (typeData == null) {
      return null;
    }
    return new String(typeData);
  }
  
  public StubIORImpl(org.omg.CORBA.Object paramObject)
  {
    OutputStream localOutputStream = StubAdapter.getORB(paramObject).create_output_stream();
    localOutputStream.write_Object(paramObject);
    InputStream localInputStream = localOutputStream.create_input_stream();
    int i = localInputStream.read_long();
    typeData = new byte[i];
    localInputStream.read_octet_array(typeData, 0, i);
    int j = localInputStream.read_long();
    profileTags = new int[j];
    profileData = new byte[j][];
    for (int k = 0; k < j; k++)
    {
      profileTags[k] = localInputStream.read_long();
      profileData[k] = new byte[localInputStream.read_long()];
      localInputStream.read_octet_array(profileData[k], 0, profileData[k].length);
    }
  }
  
  public Delegate getDelegate(ORB paramORB)
  {
    OutputStream localOutputStream = paramORB.create_output_stream();
    localOutputStream.write_long(typeData.length);
    localOutputStream.write_octet_array(typeData, 0, typeData.length);
    localOutputStream.write_long(profileTags.length);
    for (int i = 0; i < profileTags.length; i++)
    {
      localOutputStream.write_long(profileTags[i]);
      localOutputStream.write_long(profileData[i].length);
      localOutputStream.write_octet_array(profileData[i], 0, profileData[i].length);
    }
    InputStream localInputStream = localOutputStream.create_input_stream();
    org.omg.CORBA.Object localObject = localInputStream.read_Object();
    return StubAdapter.getDelegate(localObject);
  }
  
  public void doRead(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    int i = paramObjectInputStream.readInt();
    typeData = new byte[i];
    paramObjectInputStream.readFully(typeData);
    int j = paramObjectInputStream.readInt();
    profileTags = new int[j];
    profileData = new byte[j][];
    for (int k = 0; k < j; k++)
    {
      profileTags[k] = paramObjectInputStream.readInt();
      profileData[k] = new byte[paramObjectInputStream.readInt()];
      paramObjectInputStream.readFully(profileData[k]);
    }
  }
  
  public void doWrite(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(typeData.length);
    paramObjectOutputStream.write(typeData);
    paramObjectOutputStream.writeInt(profileTags.length);
    for (int i = 0; i < profileTags.length; i++)
    {
      paramObjectOutputStream.writeInt(profileTags[i]);
      paramObjectOutputStream.writeInt(profileData[i].length);
      paramObjectOutputStream.write(profileData[i]);
    }
  }
  
  public synchronized int hashCode()
  {
    if (hashCode == 0)
    {
      for (int i = 0; i < typeData.length; i++) {
        hashCode = (hashCode * 37 + typeData[i]);
      }
      for (i = 0; i < profileTags.length; i++)
      {
        hashCode = (hashCode * 37 + profileTags[i]);
        for (int j = 0; j < profileData[i].length; j++) {
          hashCode = (hashCode * 37 + profileData[i][j]);
        }
      }
    }
    return hashCode;
  }
  
  private boolean equalArrays(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    if (paramArrayOfInt1.length != paramArrayOfInt2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfInt1.length; i++) {
      if (paramArrayOfInt1[i] != paramArrayOfInt2[i]) {
        return false;
      }
    }
    return true;
  }
  
  private boolean equalArrays(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if (paramArrayOfByte1.length != paramArrayOfByte2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfByte1.length; i++) {
      if (paramArrayOfByte1[i] != paramArrayOfByte2[i]) {
        return false;
      }
    }
    return true;
  }
  
  private boolean equalArrays(byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2)
  {
    if (paramArrayOfByte1.length != paramArrayOfByte2.length) {
      return false;
    }
    for (int i = 0; i < paramArrayOfByte1.length; i++) {
      if (!equalArrays(paramArrayOfByte1[i], paramArrayOfByte2[i])) {
        return false;
      }
    }
    return true;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof StubIORImpl)) {
      return false;
    }
    StubIORImpl localStubIORImpl = (StubIORImpl)paramObject;
    if (localStubIORImpl.hashCode() != hashCode()) {
      return false;
    }
    return (equalArrays(typeData, typeData)) && (equalArrays(profileTags, profileTags)) && (equalArrays(profileData, profileData));
  }
  
  private void appendByteArray(StringBuffer paramStringBuffer, byte[] paramArrayOfByte)
  {
    for (int i = 0; i < paramArrayOfByte.length; i++) {
      paramStringBuffer.append(Integer.toHexString(paramArrayOfByte[i]));
    }
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("SimpleIORImpl[");
    String str = new String(typeData);
    localStringBuffer.append(str);
    for (int i = 0; i < profileTags.length; i++)
    {
      localStringBuffer.append(",(");
      localStringBuffer.append(profileTags[i]);
      localStringBuffer.append(")");
      appendByteArray(localStringBuffer, profileData[i]);
    }
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\StubIORImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
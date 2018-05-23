package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.copyobject.ObjectCopier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.rmi.Remote;
import org.omg.CORBA.ORB;

public class JavaStreamObjectCopierImpl
  implements ObjectCopier
{
  private ORB orb;
  
  public JavaStreamObjectCopierImpl(ORB paramORB)
  {
    orb = paramORB;
  }
  
  public Object copy(Object paramObject)
  {
    if ((paramObject instanceof Remote)) {
      return Utility.autoConnect(paramObject, orb, true);
    }
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(10000);
      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localByteArrayOutputStream);
      localObjectOutputStream.writeObject(paramObject);
      byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      ObjectInputStream localObjectInputStream = new ObjectInputStream(localByteArrayInputStream);
      return localObjectInputStream.readObject();
    }
    catch (Exception localException)
    {
      System.out.println("Failed with exception:" + localException);
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\copyobject\JavaStreamObjectCopierImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
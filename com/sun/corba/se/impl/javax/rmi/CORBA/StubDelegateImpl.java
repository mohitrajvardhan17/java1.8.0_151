package com.sun.corba.se.impl.javax.rmi.CORBA;

import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.presentation.rmi.StubConnectImpl;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.StubDelegate;
import org.omg.CORBA.ORB;

public class StubDelegateImpl
  implements StubDelegate
{
  static UtilSystemException wrapper = UtilSystemException.get("rmiiiop");
  private StubIORImpl ior = null;
  
  public StubIORImpl getIOR()
  {
    return ior;
  }
  
  public StubDelegateImpl() {}
  
  private void init(Stub paramStub)
  {
    if (ior == null) {
      ior = new StubIORImpl(paramStub);
    }
  }
  
  public int hashCode(Stub paramStub)
  {
    init(paramStub);
    return ior.hashCode();
  }
  
  public boolean equals(Stub paramStub, Object paramObject)
  {
    if (paramStub == paramObject) {
      return true;
    }
    if (!(paramObject instanceof Stub)) {
      return false;
    }
    Stub localStub = (Stub)paramObject;
    if (localStub.hashCode() != paramStub.hashCode()) {
      return false;
    }
    return paramStub.toString().equals(localStub.toString());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof StubDelegateImpl)) {
      return false;
    }
    StubDelegateImpl localStubDelegateImpl = (StubDelegateImpl)paramObject;
    if (ior == null) {
      return ior == ior;
    }
    return ior.equals(ior);
  }
  
  public int hashCode()
  {
    if (ior == null) {
      return 0;
    }
    return ior.hashCode();
  }
  
  public String toString(Stub paramStub)
  {
    if (ior == null) {
      return null;
    }
    return ior.toString();
  }
  
  public void connect(Stub paramStub, ORB paramORB)
    throws RemoteException
  {
    ior = StubConnectImpl.connect(ior, paramStub, paramStub, paramORB);
  }
  
  public void readObject(Stub paramStub, ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (ior == null) {
      ior = new StubIORImpl();
    }
    ior.doRead(paramObjectInputStream);
  }
  
  public void writeObject(Stub paramStub, ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    init(paramStub);
    ior.doWrite(paramObjectOutputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\javax\rmi\CORBA\StubDelegateImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
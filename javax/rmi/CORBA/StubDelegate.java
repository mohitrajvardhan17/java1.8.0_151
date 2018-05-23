package javax.rmi.CORBA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;

public abstract interface StubDelegate
{
  public abstract int hashCode(Stub paramStub);
  
  public abstract boolean equals(Stub paramStub, Object paramObject);
  
  public abstract String toString(Stub paramStub);
  
  public abstract void connect(Stub paramStub, ORB paramORB)
    throws RemoteException;
  
  public abstract void readObject(Stub paramStub, ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException;
  
  public abstract void writeObject(Stub paramStub, ObjectOutputStream paramObjectOutputStream)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\rmi\CORBA\StubDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
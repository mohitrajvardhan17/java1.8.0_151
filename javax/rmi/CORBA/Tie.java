package javax.rmi.CORBA;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InvokeHandler;

public abstract interface Tie
  extends InvokeHandler
{
  public abstract org.omg.CORBA.Object thisObject();
  
  public abstract void deactivate()
    throws NoSuchObjectException;
  
  public abstract ORB orb();
  
  public abstract void orb(ORB paramORB);
  
  public abstract void setTarget(Remote paramRemote);
  
  public abstract Remote getTarget();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\rmi\CORBA\Tie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
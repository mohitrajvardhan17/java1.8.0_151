package sun.rmi.server;

import java.io.InputStream;
import java.io.PrintStream;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;

public abstract class ActivationGroupInit
{
  public ActivationGroupInit() {}
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(new SecurityManager());
      }
      MarshalInputStream localMarshalInputStream = new MarshalInputStream(System.in);
      ActivationGroupID localActivationGroupID = (ActivationGroupID)localMarshalInputStream.readObject();
      ActivationGroupDesc localActivationGroupDesc = (ActivationGroupDesc)localMarshalInputStream.readObject();
      long l = localMarshalInputStream.readLong();
      ActivationGroup.createGroup(localActivationGroupID, localActivationGroupDesc, l);
      return;
    }
    catch (Exception localException2)
    {
      System.err.println("Exception in starting ActivationGroupInit:");
      localException2.printStackTrace();
    }
    finally
    {
      try
      {
        System.in.close();
      }
      catch (Exception localException4) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\server\ActivationGroupInit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
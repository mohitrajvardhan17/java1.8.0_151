package com.sun.corba.se.internal.CosNaming;

import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import com.sun.corba.se.spi.resolver.ResolverDefault;
import java.io.File;
import java.io.PrintStream;
import java.util.Properties;
import org.omg.CORBA.ORBPackage.InvalidName;

public class BootstrapServer
{
  private com.sun.corba.se.spi.orb.ORB orb;
  
  public BootstrapServer() {}
  
  public static final void main(String[] paramArrayOfString)
  {
    String str = null;
    int i = 900;
    for (int j = 0; j < paramArrayOfString.length; j++)
    {
      if ((paramArrayOfString[j].equals("-InitialServicesFile")) && (j < paramArrayOfString.length - 1)) {
        str = paramArrayOfString[(j + 1)];
      }
      if ((paramArrayOfString[j].equals("-ORBInitialPort")) && (j < paramArrayOfString.length - 1)) {
        i = Integer.parseInt(paramArrayOfString[(j + 1)]);
      }
    }
    if (str == null)
    {
      System.out.println(CorbaResourceUtil.getText("bootstrap.usage", "BootstrapServer"));
      return;
    }
    File localFile = new File(str);
    if ((localFile.exists() == true) && (!localFile.canRead()))
    {
      System.err.println(CorbaResourceUtil.getText("bootstrap.filenotreadable", localFile.getAbsolutePath()));
      return;
    }
    System.out.println(CorbaResourceUtil.getText("bootstrap.success", Integer.toString(i), localFile.getAbsolutePath()));
    Properties localProperties = new Properties();
    localProperties.put("com.sun.CORBA.ORBServerPort", Integer.toString(i));
    com.sun.corba.se.spi.orb.ORB localORB = (com.sun.corba.se.spi.orb.ORB)org.omg.CORBA.ORB.init(paramArrayOfString, localProperties);
    LocalResolver localLocalResolver1 = localORB.getLocalResolver();
    Resolver localResolver1 = ResolverDefault.makeFileResolver(localORB, localFile);
    Resolver localResolver2 = ResolverDefault.makeCompositeResolver(localResolver1, localLocalResolver1);
    LocalResolver localLocalResolver2 = ResolverDefault.makeSplitLocalResolver(localResolver2, localLocalResolver1);
    localORB.setLocalResolver(localLocalResolver2);
    try
    {
      localORB.resolve_initial_references("RootPOA");
    }
    catch (InvalidName localInvalidName)
    {
      RuntimeException localRuntimeException = new RuntimeException("This should not happen");
      localRuntimeException.initCause(localInvalidName);
      throw localRuntimeException;
    }
    localORB.run();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\internal\CosNaming\BootstrapServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
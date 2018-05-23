package sun.corba;

import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class OutputStreamFactory
{
  private OutputStreamFactory() {}
  
  public static TypeCodeOutputStream newTypeCodeOutputStream(ORB paramORB)
  {
    (TypeCodeOutputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public TypeCodeOutputStream run()
      {
        return new TypeCodeOutputStream(val$orb);
      }
    });
  }
  
  public static TypeCodeOutputStream newTypeCodeOutputStream(ORB paramORB, final boolean paramBoolean)
  {
    (TypeCodeOutputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public TypeCodeOutputStream run()
      {
        return new TypeCodeOutputStream(val$orb, paramBoolean);
      }
    });
  }
  
  public static EncapsOutputStream newEncapsOutputStream(ORB paramORB)
  {
    (EncapsOutputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public EncapsOutputStream run()
      {
        return new EncapsOutputStream(val$orb);
      }
    });
  }
  
  public static EncapsOutputStream newEncapsOutputStream(ORB paramORB, final GIOPVersion paramGIOPVersion)
  {
    (EncapsOutputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public EncapsOutputStream run()
      {
        return new EncapsOutputStream(val$orb, paramGIOPVersion);
      }
    });
  }
  
  public static EncapsOutputStream newEncapsOutputStream(ORB paramORB, final boolean paramBoolean)
  {
    (EncapsOutputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public EncapsOutputStream run()
      {
        return new EncapsOutputStream(val$orb, paramBoolean);
      }
    });
  }
  
  public static CDROutputObject newCDROutputObject(ORB paramORB, final MessageMediator paramMessageMediator, final Message paramMessage, final byte paramByte)
  {
    (CDROutputObject)AccessController.doPrivileged(new PrivilegedAction()
    {
      public CDROutputObject run()
      {
        return new CDROutputObject(val$orb, paramMessageMediator, paramMessage, paramByte);
      }
    });
  }
  
  public static CDROutputObject newCDROutputObject(ORB paramORB, final MessageMediator paramMessageMediator, final Message paramMessage, final byte paramByte, final int paramInt)
  {
    (CDROutputObject)AccessController.doPrivileged(new PrivilegedAction()
    {
      public CDROutputObject run()
      {
        return new CDROutputObject(val$orb, paramMessageMediator, paramMessage, paramByte, paramInt);
      }
    });
  }
  
  public static CDROutputObject newCDROutputObject(ORB paramORB, final CorbaMessageMediator paramCorbaMessageMediator, final GIOPVersion paramGIOPVersion, final CorbaConnection paramCorbaConnection, final Message paramMessage, final byte paramByte)
  {
    (CDROutputObject)AccessController.doPrivileged(new PrivilegedAction()
    {
      public CDROutputObject run()
      {
        return new CDROutputObject(val$orb, paramCorbaMessageMediator, paramGIOPVersion, paramCorbaConnection, paramMessage, paramByte);
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\corba\OutputStreamFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
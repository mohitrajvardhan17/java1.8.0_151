package sun.corba;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.TypeCodeInputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.org.omg.SendingContext.CodeBase;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.ORB;

public class EncapsInputStreamFactory
{
  public EncapsInputStreamFactory() {}
  
  public static EncapsInputStream newEncapsInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt, final boolean paramBoolean, final GIOPVersion paramGIOPVersion)
  {
    (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public EncapsInputStream run()
      {
        return new EncapsInputStream(val$orb, paramArrayOfByte, paramInt, paramBoolean, paramGIOPVersion);
      }
    });
  }
  
  public static EncapsInputStream newEncapsInputStream(ORB paramORB, final ByteBuffer paramByteBuffer, final int paramInt, final boolean paramBoolean, final GIOPVersion paramGIOPVersion)
  {
    (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public EncapsInputStream run()
      {
        return new EncapsInputStream(val$orb, paramByteBuffer, paramInt, paramBoolean, paramGIOPVersion);
      }
    });
  }
  
  public static EncapsInputStream newEncapsInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt)
  {
    (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public EncapsInputStream run()
      {
        return new EncapsInputStream(val$orb, paramArrayOfByte, paramInt);
      }
    });
  }
  
  public static EncapsInputStream newEncapsInputStream(EncapsInputStream paramEncapsInputStream)
  {
    (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public EncapsInputStream run()
      {
        return new EncapsInputStream(val$eis);
      }
    });
  }
  
  public static EncapsInputStream newEncapsInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt, final GIOPVersion paramGIOPVersion)
  {
    (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public EncapsInputStream run()
      {
        return new EncapsInputStream(val$orb, paramArrayOfByte, paramInt, paramGIOPVersion);
      }
    });
  }
  
  public static EncapsInputStream newEncapsInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt, final GIOPVersion paramGIOPVersion, final CodeBase paramCodeBase)
  {
    (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public EncapsInputStream run()
      {
        return new EncapsInputStream(val$orb, paramArrayOfByte, paramInt, paramGIOPVersion, paramCodeBase);
      }
    });
  }
  
  public static TypeCodeInputStream newTypeCodeInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt, final boolean paramBoolean, final GIOPVersion paramGIOPVersion)
  {
    (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public TypeCodeInputStream run()
      {
        return new TypeCodeInputStream(val$orb, paramArrayOfByte, paramInt, paramBoolean, paramGIOPVersion);
      }
    });
  }
  
  public static TypeCodeInputStream newTypeCodeInputStream(ORB paramORB, final ByteBuffer paramByteBuffer, final int paramInt, final boolean paramBoolean, final GIOPVersion paramGIOPVersion)
  {
    (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public TypeCodeInputStream run()
      {
        return new TypeCodeInputStream(val$orb, paramByteBuffer, paramInt, paramBoolean, paramGIOPVersion);
      }
    });
  }
  
  public static TypeCodeInputStream newTypeCodeInputStream(ORB paramORB, final byte[] paramArrayOfByte, final int paramInt)
  {
    (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction()
    {
      public TypeCodeInputStream run()
      {
        return new TypeCodeInputStream(val$orb, paramArrayOfByte, paramInt);
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\corba\EncapsInputStreamFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
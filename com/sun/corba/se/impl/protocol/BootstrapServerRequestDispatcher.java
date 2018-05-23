package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.resolver.LocalResolver;
import java.util.Iterator;
import java.util.Set;
import org.omg.CORBA.SystemException;

public class BootstrapServerRequestDispatcher
  implements CorbaServerRequestDispatcher
{
  private ORB orb;
  ORBUtilSystemException wrapper;
  private static final boolean debug = false;
  
  public BootstrapServerRequestDispatcher(ORB paramORB)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
  }
  
  public void dispatch(MessageMediator paramMessageMediator)
  {
    CorbaMessageMediator localCorbaMessageMediator1 = (CorbaMessageMediator)paramMessageMediator;
    CorbaMessageMediator localCorbaMessageMediator2 = null;
    try
    {
      MarshalInputStream localMarshalInputStream = (MarshalInputStream)localCorbaMessageMediator1.getInputObject();
      localObject1 = localCorbaMessageMediator1.getOperationName();
      localCorbaMessageMediator2 = localCorbaMessageMediator1.getProtocolHandler().createResponse(localCorbaMessageMediator1, null);
      MarshalOutputStream localMarshalOutputStream = (MarshalOutputStream)localCorbaMessageMediator2.getOutputObject();
      Object localObject2;
      Object localObject3;
      if (((String)localObject1).equals("get"))
      {
        localObject2 = localMarshalInputStream.read_string();
        localObject3 = orb.getLocalResolver().resolve((String)localObject2);
        localMarshalOutputStream.write_Object((org.omg.CORBA.Object)localObject3);
      }
      else if (((String)localObject1).equals("list"))
      {
        localObject2 = orb.getLocalResolver().list();
        localMarshalOutputStream.write_long(((Set)localObject2).size());
        localObject3 = ((Set)localObject2).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          String str = (String)((Iterator)localObject3).next();
          localMarshalOutputStream.write_string(str);
        }
      }
      else
      {
        throw wrapper.illegalBootstrapOperation(localObject1);
      }
    }
    catch (SystemException localSystemException)
    {
      localCorbaMessageMediator2 = localCorbaMessageMediator1.getProtocolHandler().createSystemExceptionResponse(localCorbaMessageMediator1, localSystemException, null);
    }
    catch (RuntimeException localRuntimeException)
    {
      localObject1 = wrapper.bootstrapRuntimeException(localRuntimeException);
      localCorbaMessageMediator2 = localCorbaMessageMediator1.getProtocolHandler().createSystemExceptionResponse(localCorbaMessageMediator1, (SystemException)localObject1, null);
    }
    catch (Exception localException)
    {
      Object localObject1 = wrapper.bootstrapException(localException);
      localCorbaMessageMediator2 = localCorbaMessageMediator1.getProtocolHandler().createSystemExceptionResponse(localCorbaMessageMediator1, (SystemException)localObject1, null);
    }
  }
  
  public IOR locate(ObjectKey paramObjectKey)
  {
    return null;
  }
  
  public int getId()
  {
    throw wrapper.genericNoImpl();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\BootstrapServerRequestDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
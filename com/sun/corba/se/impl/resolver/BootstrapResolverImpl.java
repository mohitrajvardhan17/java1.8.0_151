package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class BootstrapResolverImpl
  implements Resolver
{
  private Delegate bootstrapDelegate;
  private ORBUtilSystemException wrapper;
  
  public BootstrapResolverImpl(ORB paramORB, String paramString, int paramInt)
  {
    wrapper = ORBUtilSystemException.get(paramORB, "orb.resolver");
    byte[] arrayOfByte = "INIT".getBytes();
    ObjectKey localObjectKey = paramORB.getObjectKeyFactory().create(arrayOfByte);
    IIOPAddress localIIOPAddress = IIOPFactories.makeIIOPAddress(paramORB, paramString, paramInt);
    IIOPProfileTemplate localIIOPProfileTemplate = IIOPFactories.makeIIOPProfileTemplate(paramORB, GIOPVersion.V1_0, localIIOPAddress);
    IORTemplate localIORTemplate = IORFactories.makeIORTemplate(localObjectKey.getTemplate());
    localIORTemplate.add(localIIOPProfileTemplate);
    IOR localIOR = localIORTemplate.makeIOR(paramORB, "", localObjectKey.getId());
    bootstrapDelegate = ORBUtility.makeClientDelegate(localIOR);
  }
  
  private InputStream invoke(String paramString1, String paramString2)
  {
    int i = 1;
    InputStream localInputStream = null;
    while (i != 0)
    {
      org.omg.CORBA.Object localObject = null;
      i = 0;
      OutputStream localOutputStream = bootstrapDelegate.request(localObject, paramString1, true);
      if (paramString2 != null) {
        localOutputStream.write_string(paramString2);
      }
      try
      {
        localInputStream = bootstrapDelegate.invoke(localObject, localOutputStream);
      }
      catch (ApplicationException localApplicationException)
      {
        throw wrapper.bootstrapApplicationException(localApplicationException);
      }
      catch (RemarshalException localRemarshalException)
      {
        i = 1;
      }
    }
    return localInputStream;
  }
  
  public org.omg.CORBA.Object resolve(String paramString)
  {
    InputStream localInputStream = null;
    org.omg.CORBA.Object localObject = null;
    try
    {
      localInputStream = invoke("get", paramString);
      localObject = localInputStream.read_Object();
    }
    finally
    {
      bootstrapDelegate.releaseReply(null, localInputStream);
    }
    return localObject;
  }
  
  public Set list()
  {
    InputStream localInputStream = null;
    HashSet localHashSet = new HashSet();
    try
    {
      localInputStream = invoke("list", null);
      int i = localInputStream.read_long();
      for (int j = 0; j < i; j++) {
        localHashSet.add(localInputStream.read_string());
      }
    }
    finally
    {
      bootstrapDelegate.releaseReply(null, localInputStream);
    }
    return localHashSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\resolver\BootstrapResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
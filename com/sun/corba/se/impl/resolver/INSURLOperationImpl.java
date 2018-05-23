package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.naming.namingutil.CorbalocURL;
import com.sun.corba.se.impl.naming.namingutil.CorbanameURL;
import com.sun.corba.se.impl.naming.namingutil.IIOPEndpointInfo;
import com.sun.corba.se.impl.naming.namingutil.INSURL;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
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
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.resolver.LocalResolver;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import sun.corba.EncapsInputStreamFactory;

public class INSURLOperationImpl
  implements Operation
{
  ORB orb;
  ORBUtilSystemException wrapper;
  OMGSystemException omgWrapper;
  Resolver bootstrapResolver;
  private NamingContextExt rootNamingContextExt;
  private Object rootContextCacheLock = new Object();
  private INSURLHandler insURLHandler = INSURLHandler.getINSURLHandler();
  private static final int NIBBLES_PER_BYTE = 2;
  private static final int UN_SHIFT = 4;
  
  public INSURLOperationImpl(ORB paramORB, Resolver paramResolver)
  {
    orb = paramORB;
    wrapper = ORBUtilSystemException.get(paramORB, "orb.resolver");
    omgWrapper = OMGSystemException.get(paramORB, "orb.resolver");
    bootstrapResolver = paramResolver;
  }
  
  private org.omg.CORBA.Object getIORFromString(String paramString)
  {
    if ((paramString.length() & 0x1) == 1) {
      throw wrapper.badStringifiedIorLen();
    }
    byte[] arrayOfByte = new byte[(paramString.length() - "IOR:".length()) / 2];
    int i = "IOR:".length();
    for (int j = 0; i < paramString.length(); j++)
    {
      arrayOfByte[j] = ((byte)(ORBUtility.hexOf(paramString.charAt(i)) << 4 & 0xF0));
      int tmp72_70 = j;
      byte[] tmp72_69 = arrayOfByte;
      tmp72_69[tmp72_70] = ((byte)(tmp72_69[tmp72_70] | (byte)(ORBUtility.hexOf(paramString.charAt(i + 1)) & 0xF)));
      i += 2;
    }
    EncapsInputStream localEncapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(orb, arrayOfByte, arrayOfByte.length, orb.getORBData().getGIOPVersion());
    localEncapsInputStream.consumeEndian();
    return localEncapsInputStream.read_Object();
  }
  
  public Object operate(Object paramObject)
  {
    if ((paramObject instanceof String))
    {
      String str = (String)paramObject;
      if (str.startsWith("IOR:")) {
        return getIORFromString(str);
      }
      INSURL localINSURL = insURLHandler.parseURL(str);
      if (localINSURL == null) {
        throw omgWrapper.soBadSchemeName();
      }
      return resolveINSURL(localINSURL);
    }
    throw wrapper.stringExpected();
  }
  
  private org.omg.CORBA.Object resolveINSURL(INSURL paramINSURL)
  {
    if (paramINSURL.isCorbanameURL()) {
      return resolveCorbaname((CorbanameURL)paramINSURL);
    }
    return resolveCorbaloc((CorbalocURL)paramINSURL);
  }
  
  private org.omg.CORBA.Object resolveCorbaloc(CorbalocURL paramCorbalocURL)
  {
    org.omg.CORBA.Object localObject = null;
    if (paramCorbalocURL.getRIRFlag()) {
      localObject = bootstrapResolver.resolve(paramCorbalocURL.getKeyString());
    } else {
      localObject = getIORUsingCorbaloc(paramCorbalocURL);
    }
    return localObject;
  }
  
  private org.omg.CORBA.Object resolveCorbaname(CorbanameURL paramCorbanameURL)
  {
    Object localObject1 = null;
    try
    {
      NamingContextExt localNamingContextExt = null;
      if (paramCorbanameURL.getRIRFlag())
      {
        localNamingContextExt = getDefaultRootNamingContext();
      }
      else
      {
        localObject2 = getIORUsingCorbaloc(paramCorbanameURL);
        if (localObject2 == null) {
          return null;
        }
        localNamingContextExt = NamingContextExtHelper.narrow((org.omg.CORBA.Object)localObject2);
      }
      Object localObject2 = paramCorbanameURL.getStringifiedName();
      if (localObject2 == null) {
        return localNamingContextExt;
      }
      return localNamingContextExt.resolve_str((String)localObject2);
    }
    catch (Exception localException)
    {
      clearRootNamingContextCache();
    }
    return null;
  }
  
  private org.omg.CORBA.Object getIORUsingCorbaloc(INSURL paramINSURL)
  {
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList = new ArrayList();
    List localList = paramINSURL.getEndpointInfo();
    String str = paramINSURL.getKeyString();
    if (str == null) {
      return null;
    }
    ObjectKey localObjectKey = orb.getObjectKeyFactory().create(str.getBytes());
    IORTemplate localIORTemplate = IORFactories.makeIORTemplate(localObjectKey.getTemplate());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (IIOPEndpointInfo)localIterator.next();
      localObject2 = IIOPFactories.makeIIOPAddress(orb, ((IIOPEndpointInfo)localObject1).getHost(), ((IIOPEndpointInfo)localObject1).getPort());
      localObject3 = GIOPVersion.getInstance((byte)((IIOPEndpointInfo)localObject1).getMajor(), (byte)((IIOPEndpointInfo)localObject1).getMinor());
      localObject4 = null;
      if (((GIOPVersion)localObject3).equals(GIOPVersion.V1_0))
      {
        localObject4 = IIOPFactories.makeIIOPProfileTemplate(orb, (GIOPVersion)localObject3, (IIOPAddress)localObject2);
        localArrayList.add(localObject4);
      }
      else if (localHashMap.get(localObject3) == null)
      {
        localObject4 = IIOPFactories.makeIIOPProfileTemplate(orb, (GIOPVersion)localObject3, (IIOPAddress)localObject2);
        localHashMap.put(localObject3, localObject4);
      }
      else
      {
        localObject4 = (IIOPProfileTemplate)localHashMap.get(localObject3);
        localObject5 = IIOPFactories.makeAlternateIIOPAddressComponent((IIOPAddress)localObject2);
        ((IIOPProfileTemplate)localObject4).add(localObject5);
      }
    }
    Object localObject1 = orb.getORBData().getGIOPVersion();
    Object localObject2 = (IIOPProfileTemplate)localHashMap.get(localObject1);
    if (localObject2 != null)
    {
      localIORTemplate.add(localObject2);
      localHashMap.remove(localObject1);
    }
    Object localObject3 = new Comparator()
    {
      public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2)
      {
        GIOPVersion localGIOPVersion1 = (GIOPVersion)paramAnonymousObject1;
        GIOPVersion localGIOPVersion2 = (GIOPVersion)paramAnonymousObject2;
        return localGIOPVersion1.equals(localGIOPVersion2) ? 0 : localGIOPVersion1.lessThan(localGIOPVersion2) ? 1 : -1;
      }
    };
    Object localObject4 = new ArrayList(localHashMap.keySet());
    Collections.sort((List)localObject4, (Comparator)localObject3);
    Object localObject5 = ((List)localObject4).iterator();
    while (((Iterator)localObject5).hasNext())
    {
      localObject6 = (IIOPProfileTemplate)localHashMap.get(((Iterator)localObject5).next());
      localIORTemplate.add(localObject6);
    }
    localIORTemplate.addAll(localArrayList);
    Object localObject6 = localIORTemplate.makeIOR(orb, "", localObjectKey.getId());
    return ORBUtility.makeObjectReference((IOR)localObject6);
  }
  
  private NamingContextExt getDefaultRootNamingContext()
  {
    synchronized (rootContextCacheLock)
    {
      if (rootNamingContextExt == null) {
        try
        {
          rootNamingContextExt = NamingContextExtHelper.narrow(orb.getLocalResolver().resolve("NameService"));
        }
        catch (Exception localException)
        {
          rootNamingContextExt = null;
        }
      }
    }
    return rootNamingContextExt;
  }
  
  private void clearRootNamingContextCache()
  {
    synchronized (rootContextCacheLock)
    {
      rootNamingContextExt = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\resolver\INSURLOperationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
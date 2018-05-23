package com.sun.corba.se.spi.oa;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public abstract class ObjectAdapterBase
  extends LocalObject
  implements ObjectAdapter
{
  private ORB orb;
  private final POASystemException _iorWrapper;
  private final POASystemException _invocationWrapper;
  private final POASystemException _lifecycleWrapper;
  private final OMGSystemException _omgInvocationWrapper;
  private final OMGSystemException _omgLifecycleWrapper;
  private IORTemplate iortemp;
  private byte[] adapterId;
  private ObjectReferenceTemplate adapterTemplate;
  private ObjectReferenceFactory currentFactory;
  
  public ObjectAdapterBase(ORB paramORB)
  {
    orb = paramORB;
    _iorWrapper = POASystemException.get(paramORB, "oa.ior");
    _lifecycleWrapper = POASystemException.get(paramORB, "oa.lifecycle");
    _omgLifecycleWrapper = OMGSystemException.get(paramORB, "oa.lifecycle");
    _invocationWrapper = POASystemException.get(paramORB, "oa.invocation");
    _omgInvocationWrapper = OMGSystemException.get(paramORB, "oa.invocation");
  }
  
  public final POASystemException iorWrapper()
  {
    return _iorWrapper;
  }
  
  public final POASystemException lifecycleWrapper()
  {
    return _lifecycleWrapper;
  }
  
  public final OMGSystemException omgLifecycleWrapper()
  {
    return _omgLifecycleWrapper;
  }
  
  public final POASystemException invocationWrapper()
  {
    return _invocationWrapper;
  }
  
  public final OMGSystemException omgInvocationWrapper()
  {
    return _omgInvocationWrapper;
  }
  
  public final void initializeTemplate(ObjectKeyTemplate paramObjectKeyTemplate, boolean paramBoolean, Policies paramPolicies, String paramString1, String paramString2, ObjectAdapterId paramObjectAdapterId)
  {
    adapterId = paramObjectKeyTemplate.getAdapterId();
    iortemp = IORFactories.makeIORTemplate(paramObjectKeyTemplate);
    orb.getCorbaTransportManager().addToIORTemplate(iortemp, paramPolicies, paramString1, paramString2, paramObjectAdapterId);
    adapterTemplate = IORFactories.makeObjectReferenceTemplate(orb, iortemp);
    currentFactory = adapterTemplate;
    if (paramBoolean)
    {
      PIHandler localPIHandler = orb.getPIHandler();
      if (localPIHandler != null) {
        localPIHandler.objectAdapterCreated(this);
      }
    }
    iortemp.makeImmutable();
  }
  
  public final org.omg.CORBA.Object makeObject(String paramString, byte[] paramArrayOfByte)
  {
    return currentFactory.make_object(paramString, paramArrayOfByte);
  }
  
  public final byte[] getAdapterId()
  {
    return adapterId;
  }
  
  public final ORB getORB()
  {
    return orb;
  }
  
  public abstract Policy getEffectivePolicy(int paramInt);
  
  public final IORTemplate getIORTemplate()
  {
    return iortemp;
  }
  
  public abstract int getManagerId();
  
  public abstract short getState();
  
  public final ObjectReferenceTemplate getAdapterTemplate()
  {
    return adapterTemplate;
  }
  
  public final ObjectReferenceFactory getCurrentFactory()
  {
    return currentFactory;
  }
  
  public final void setCurrentFactory(ObjectReferenceFactory paramObjectReferenceFactory)
  {
    currentFactory = paramObjectReferenceFactory;
  }
  
  public abstract org.omg.CORBA.Object getLocalServant(byte[] paramArrayOfByte);
  
  public abstract void getInvocationServant(OAInvocationInfo paramOAInvocationInfo);
  
  public abstract void returnServant();
  
  public abstract void enter()
    throws OADestroyed;
  
  public abstract void exit();
  
  protected abstract ObjectCopierFactory getObjectCopierFactory();
  
  public OAInvocationInfo makeInvocationInfo(byte[] paramArrayOfByte)
  {
    OAInvocationInfo localOAInvocationInfo = new OAInvocationInfo(this, paramArrayOfByte);
    localOAInvocationInfo.setCopierFactory(getObjectCopierFactory());
    return localOAInvocationInfo;
  }
  
  public abstract String[] getInterfaces(Object paramObject, byte[] paramArrayOfByte);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\oa\ObjectAdapterBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
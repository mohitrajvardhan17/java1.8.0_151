package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.TaggedComponentFactoryFinder;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.legacy.interceptor.IORInfoExt;
import com.sun.corba.se.spi.legacy.interceptor.UnknownType;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

public final class IORInfoImpl
  extends LocalObject
  implements IORInfo, IORInfoExt
{
  private static final int STATE_INITIAL = 0;
  private static final int STATE_ESTABLISHED = 1;
  private static final int STATE_DONE = 2;
  private int state = 0;
  private ObjectAdapter adapter;
  private ORB orb;
  private ORBUtilSystemException orbutilWrapper;
  private InterceptorsSystemException wrapper;
  private OMGSystemException omgWrapper;
  
  IORInfoImpl(ObjectAdapter paramObjectAdapter)
  {
    orb = paramObjectAdapter.getORB();
    orbutilWrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
    wrapper = InterceptorsSystemException.get(orb, "rpc.protocol");
    omgWrapper = OMGSystemException.get(orb, "rpc.protocol");
    adapter = paramObjectAdapter;
  }
  
  public Policy get_effective_policy(int paramInt)
  {
    checkState(0, 1);
    return adapter.getEffectivePolicy(paramInt);
  }
  
  public void add_ior_component(org.omg.IOP.TaggedComponent paramTaggedComponent)
  {
    checkState(0);
    if (paramTaggedComponent == null) {
      nullParam();
    }
    addIORComponentToProfileInternal(paramTaggedComponent, adapter.getIORTemplate().iterator());
  }
  
  public void add_ior_component_to_profile(org.omg.IOP.TaggedComponent paramTaggedComponent, int paramInt)
  {
    checkState(0);
    if (paramTaggedComponent == null) {
      nullParam();
    }
    addIORComponentToProfileInternal(paramTaggedComponent, adapter.getIORTemplate().iteratorById(paramInt));
  }
  
  public int getServerPort(String paramString)
    throws UnknownType
  {
    checkState(0, 1);
    int i = orb.getLegacyServerSocketManager().legacyGetTransientOrPersistentServerPort(paramString);
    if (i == -1) {
      throw new UnknownType();
    }
    return i;
  }
  
  public ObjectAdapter getObjectAdapter()
  {
    return adapter;
  }
  
  public int manager_id()
  {
    checkState(0, 1);
    return adapter.getManagerId();
  }
  
  public short state()
  {
    checkState(0, 1);
    return adapter.getState();
  }
  
  public ObjectReferenceTemplate adapter_template()
  {
    checkState(1);
    return adapter.getAdapterTemplate();
  }
  
  public ObjectReferenceFactory current_factory()
  {
    checkState(1);
    return adapter.getCurrentFactory();
  }
  
  public void current_factory(ObjectReferenceFactory paramObjectReferenceFactory)
  {
    checkState(1);
    adapter.setCurrentFactory(paramObjectReferenceFactory);
  }
  
  private void addIORComponentToProfileInternal(org.omg.IOP.TaggedComponent paramTaggedComponent, Iterator paramIterator)
  {
    TaggedComponentFactoryFinder localTaggedComponentFactoryFinder = orb.getTaggedComponentFactoryFinder();
    com.sun.corba.se.spi.ior.TaggedComponent localTaggedComponent = localTaggedComponentFactoryFinder.create(orb, paramTaggedComponent);
    int i = 0;
    while (paramIterator.hasNext())
    {
      i = 1;
      TaggedProfileTemplate localTaggedProfileTemplate = (TaggedProfileTemplate)paramIterator.next();
      localTaggedProfileTemplate.add(localTaggedComponent);
    }
    if (i == 0) {
      throw omgWrapper.invalidProfileId();
    }
  }
  
  private void nullParam()
  {
    throw orbutilWrapper.nullParam();
  }
  
  private void checkState(int paramInt)
  {
    if (paramInt != state) {
      throw wrapper.badState1(new Integer(paramInt), new Integer(state));
    }
  }
  
  private void checkState(int paramInt1, int paramInt2)
  {
    if ((paramInt1 != state) && (paramInt2 != state)) {
      throw wrapper.badState2(new Integer(paramInt1), new Integer(paramInt2), new Integer(state));
    }
  }
  
  void makeStateEstablished()
  {
    checkState(0);
    state = 1;
  }
  
  void makeStateDone()
  {
    checkState(1);
    state = 2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\IORInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.legacy.interceptor.ORBInitInfoExt;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.PIHandler;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public final class ORBInitInfoImpl
  extends LocalObject
  implements ORBInitInfo, ORBInitInfoExt
{
  private ORB orb;
  private InterceptorsSystemException wrapper;
  private ORBUtilSystemException orbutilWrapper;
  private OMGSystemException omgWrapper;
  private String[] args;
  private String orbId;
  private CodecFactory codecFactory;
  private int stage = 0;
  public static final int STAGE_PRE_INIT = 0;
  public static final int STAGE_POST_INIT = 1;
  public static final int STAGE_CLOSED = 2;
  private static final String MESSAGE_ORBINITINFO_INVALID = "ORBInitInfo object is only valid during ORB_init";
  
  ORBInitInfoImpl(ORB paramORB, String[] paramArrayOfString, String paramString, CodecFactory paramCodecFactory)
  {
    orb = paramORB;
    wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
    orbutilWrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    omgWrapper = OMGSystemException.get(paramORB, "rpc.protocol");
    args = paramArrayOfString;
    orbId = paramString;
    codecFactory = paramCodecFactory;
  }
  
  public ORB getORB()
  {
    return orb;
  }
  
  void setStage(int paramInt)
  {
    stage = paramInt;
  }
  
  private void checkStage()
  {
    if (stage == 2) {
      throw wrapper.orbinitinfoInvalid();
    }
  }
  
  public String[] arguments()
  {
    checkStage();
    return args;
  }
  
  public String orb_id()
  {
    checkStage();
    return orbId;
  }
  
  public CodecFactory codec_factory()
  {
    checkStage();
    return codecFactory;
  }
  
  public void register_initial_reference(String paramString, Object paramObject)
    throws org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName
  {
    checkStage();
    if (paramString == null) {
      nullParam();
    }
    if (paramObject == null) {
      throw omgWrapper.rirWithNullObject();
    }
    try
    {
      orb.register_initial_reference(paramString, paramObject);
    }
    catch (org.omg.CORBA.ORBPackage.InvalidName localInvalidName)
    {
      org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName localInvalidName1 = new org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName(localInvalidName.getMessage());
      localInvalidName1.initCause(localInvalidName);
      throw localInvalidName1;
    }
  }
  
  public Object resolve_initial_references(String paramString)
    throws org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName
  {
    checkStage();
    if (paramString == null) {
      nullParam();
    }
    if (stage == 0) {
      throw wrapper.rirInvalidPreInit();
    }
    Object localObject = null;
    try
    {
      localObject = orb.resolve_initial_references(paramString);
    }
    catch (org.omg.CORBA.ORBPackage.InvalidName localInvalidName)
    {
      throw new org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName();
    }
    return localObject;
  }
  
  public void add_client_request_interceptor_with_policy(ClientRequestInterceptor paramClientRequestInterceptor, Policy[] paramArrayOfPolicy)
    throws DuplicateName
  {
    add_client_request_interceptor(paramClientRequestInterceptor);
  }
  
  public void add_client_request_interceptor(ClientRequestInterceptor paramClientRequestInterceptor)
    throws DuplicateName
  {
    checkStage();
    if (paramClientRequestInterceptor == null) {
      nullParam();
    }
    orb.getPIHandler().register_interceptor(paramClientRequestInterceptor, 0);
  }
  
  public void add_server_request_interceptor_with_policy(ServerRequestInterceptor paramServerRequestInterceptor, Policy[] paramArrayOfPolicy)
    throws DuplicateName, PolicyError
  {
    add_server_request_interceptor(paramServerRequestInterceptor);
  }
  
  public void add_server_request_interceptor(ServerRequestInterceptor paramServerRequestInterceptor)
    throws DuplicateName
  {
    checkStage();
    if (paramServerRequestInterceptor == null) {
      nullParam();
    }
    orb.getPIHandler().register_interceptor(paramServerRequestInterceptor, 1);
  }
  
  public void add_ior_interceptor_with_policy(IORInterceptor paramIORInterceptor, Policy[] paramArrayOfPolicy)
    throws DuplicateName, PolicyError
  {
    add_ior_interceptor(paramIORInterceptor);
  }
  
  public void add_ior_interceptor(IORInterceptor paramIORInterceptor)
    throws DuplicateName
  {
    checkStage();
    if (paramIORInterceptor == null) {
      nullParam();
    }
    orb.getPIHandler().register_interceptor(paramIORInterceptor, 2);
  }
  
  public int allocate_slot_id()
  {
    checkStage();
    return ((PICurrent)orb.getPIHandler().getPICurrent()).allocateSlotId();
  }
  
  public void register_policy_factory(int paramInt, PolicyFactory paramPolicyFactory)
  {
    checkStage();
    if (paramPolicyFactory == null) {
      nullParam();
    }
    orb.getPIHandler().registerPolicyFactory(paramInt, paramPolicyFactory);
  }
  
  private void nullParam()
    throws BAD_PARAM
  {
    throw orbutilWrapper.nullParam();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\ORBInitInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.corba.se.impl.naming.pcosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl;
import com.sun.corba.se.impl.naming.cosnaming.NamingContextDataStore;
import com.sun.corba.se.impl.naming.cosnaming.NamingUtils;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import com.sun.corba.se.spi.orb.ORB;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExtPOA;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class NamingContextImpl
  extends NamingContextExtPOA
  implements NamingContextDataStore, Serializable
{
  private transient ORB orb;
  private final String objKey;
  private final Hashtable theHashtable = new Hashtable();
  private transient NameService theNameServiceHandle;
  private transient ServantManagerImpl theServantManagerImplHandle;
  private transient InterOperableNamingImpl insImpl;
  private transient NamingSystemException readWrapper;
  private transient NamingSystemException updateWrapper;
  private static POA biPOA = null;
  private static boolean debug;
  
  public NamingContextImpl(ORB paramORB, String paramString, NameService paramNameService, ServantManagerImpl paramServantManagerImpl)
    throws Exception
  {
    orb = paramORB;
    readWrapper = NamingSystemException.get(paramORB, "naming.read");
    updateWrapper = NamingSystemException.get(paramORB, "naming.update");
    debug = true;
    objKey = paramString;
    theNameServiceHandle = paramNameService;
    theServantManagerImplHandle = paramServantManagerImpl;
    insImpl = new InterOperableNamingImpl();
  }
  
  InterOperableNamingImpl getINSImpl()
  {
    if (insImpl == null) {
      insImpl = new InterOperableNamingImpl();
    }
    return insImpl;
  }
  
  public void setRootNameService(NameService paramNameService)
  {
    theNameServiceHandle = paramNameService;
  }
  
  public void setORB(ORB paramORB)
  {
    orb = paramORB;
  }
  
  public void setServantManagerImpl(ServantManagerImpl paramServantManagerImpl)
  {
    theServantManagerImplHandle = paramServantManagerImpl;
  }
  
  public POA getNSPOA()
  {
    return theNameServiceHandle.getNSPOA();
  }
  
  public void bind(NameComponent[] paramArrayOfNameComponent, org.omg.CORBA.Object paramObject)
    throws NotFound, CannotProceed, InvalidName, AlreadyBound
  {
    if (paramObject == null) {
      throw updateWrapper.objectIsNull();
    }
    if (debug) {
      dprint("bind " + nameToString(paramArrayOfNameComponent) + " to " + paramObject);
    }
    NamingContextImpl localNamingContextImpl = this;
    doBind(localNamingContextImpl, paramArrayOfNameComponent, paramObject, false, BindingType.nobject);
  }
  
  public void bind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext)
    throws NotFound, CannotProceed, InvalidName, AlreadyBound
  {
    if (paramNamingContext == null) {
      throw updateWrapper.objectIsNull();
    }
    NamingContextImpl localNamingContextImpl = this;
    doBind(localNamingContextImpl, paramArrayOfNameComponent, paramNamingContext, false, BindingType.ncontext);
  }
  
  public void rebind(NameComponent[] paramArrayOfNameComponent, org.omg.CORBA.Object paramObject)
    throws NotFound, CannotProceed, InvalidName
  {
    if (paramObject == null) {
      throw updateWrapper.objectIsNull();
    }
    try
    {
      if (debug) {
        dprint("rebind " + nameToString(paramArrayOfNameComponent) + " to " + paramObject);
      }
      NamingContextImpl localNamingContextImpl = this;
      doBind(localNamingContextImpl, paramArrayOfNameComponent, paramObject, true, BindingType.nobject);
    }
    catch (AlreadyBound localAlreadyBound)
    {
      throw updateWrapper.namingCtxRebindAlreadyBound(localAlreadyBound);
    }
  }
  
  public void rebind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext)
    throws NotFound, CannotProceed, InvalidName
  {
    try
    {
      if (debug) {
        dprint("rebind_context " + nameToString(paramArrayOfNameComponent) + " to " + paramNamingContext);
      }
      NamingContextImpl localNamingContextImpl = this;
      doBind(localNamingContextImpl, paramArrayOfNameComponent, paramNamingContext, true, BindingType.ncontext);
    }
    catch (AlreadyBound localAlreadyBound)
    {
      throw updateWrapper.namingCtxRebindAlreadyBound(localAlreadyBound);
    }
  }
  
  public org.omg.CORBA.Object resolve(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, CannotProceed, InvalidName
  {
    if (debug) {
      dprint("resolve " + nameToString(paramArrayOfNameComponent));
    }
    NamingContextImpl localNamingContextImpl = this;
    return doResolve(localNamingContextImpl, paramArrayOfNameComponent);
  }
  
  public void unbind(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, CannotProceed, InvalidName
  {
    if (debug) {
      dprint("unbind " + nameToString(paramArrayOfNameComponent));
    }
    NamingContextImpl localNamingContextImpl = this;
    doUnbind(localNamingContextImpl, paramArrayOfNameComponent);
  }
  
  public void list(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder)
  {
    if (debug) {
      dprint("list(" + paramInt + ")");
    }
    NamingContextImpl localNamingContextImpl = this;
    synchronized (localNamingContextImpl)
    {
      localNamingContextImpl.List(paramInt, paramBindingListHolder, paramBindingIteratorHolder);
    }
    if ((debug) && (value != null)) {
      dprint("list(" + paramInt + ") -> bindings[" + value.length + "] + iterator: " + value);
    }
  }
  
  /* Error */
  public synchronized NamingContext new_context()
  {
    // Byte code:
    //   0: getstatic 482	com/sun/corba/se/impl/naming/pcosnaming/NamingContextImpl:debug	Z
    //   3: ifeq +8 -> 11
    //   6: ldc 21
    //   8: invokestatic 537	com/sun/corba/se/impl/naming/pcosnaming/NamingContextImpl:dprint	(Ljava/lang/String;)V
    //   11: aload_0
    //   12: astore_1
    //   13: aload_1
    //   14: dup
    //   15: astore_2
    //   16: monitorenter
    //   17: aload_1
    //   18: invokeinterface 589 1 0
    //   23: aload_2
    //   24: monitorexit
    //   25: areturn
    //   26: astore_3
    //   27: aload_2
    //   28: monitorexit
    //   29: aload_3
    //   30: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	NamingContextImpl
    //   12	6	1	localNamingContextImpl	NamingContextImpl
    //   15	13	2	Ljava/lang/Object;	Object
    //   26	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   17	25	26	finally
    //   26	29	26	finally
  }
  
  public NamingContext bind_new_context(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, AlreadyBound, CannotProceed, InvalidName
  {
    NamingContext localNamingContext1 = null;
    localNamingContext2 = null;
    try
    {
      if (debug) {
        dprint("bind_new_context " + nameToString(paramArrayOfNameComponent));
      }
      localNamingContext1 = new_context();
      bind_context(paramArrayOfNameComponent, localNamingContext1);
      localNamingContext2 = localNamingContext1;
      localNamingContext1 = null;
      return localNamingContext2;
    }
    finally
    {
      try
      {
        if (localNamingContext1 != null) {
          localNamingContext1.destroy();
        }
      }
      catch (NotEmpty localNotEmpty2) {}
    }
  }
  
  public void destroy()
    throws NotEmpty
  {
    if (debug) {
      dprint("destroy ");
    }
    NamingContextImpl localNamingContextImpl = this;
    synchronized (localNamingContextImpl)
    {
      if (localNamingContextImpl.IsEmpty() == true) {
        localNamingContextImpl.Destroy();
      } else {
        throw new NotEmpty();
      }
    }
  }
  
  private void doBind(NamingContextDataStore paramNamingContextDataStore, NameComponent[] paramArrayOfNameComponent, org.omg.CORBA.Object paramObject, boolean paramBoolean, BindingType paramBindingType)
    throws NotFound, CannotProceed, InvalidName, AlreadyBound
  {
    if (paramArrayOfNameComponent.length < 1) {
      throw new InvalidName();
    }
    Object localObject1;
    Object localObject2;
    if (paramArrayOfNameComponent.length == 1)
    {
      if ((0id.length() == 0) && (0kind.length() == 0)) {
        throw new InvalidName();
      }
      synchronized (paramNamingContextDataStore)
      {
        localObject1 = new BindingTypeHolder();
        if (paramBoolean)
        {
          localObject2 = paramNamingContextDataStore.Resolve(paramArrayOfNameComponent[0], (BindingTypeHolder)localObject1);
          if (localObject2 != null)
          {
            if (value.value() == BindingType.nobject.value())
            {
              if (paramBindingType.value() == BindingType.ncontext.value()) {
                throw new NotFound(NotFoundReason.not_context, paramArrayOfNameComponent);
              }
            }
            else if (paramBindingType.value() == BindingType.nobject.value()) {
              throw new NotFound(NotFoundReason.not_object, paramArrayOfNameComponent);
            }
            paramNamingContextDataStore.Unbind(paramArrayOfNameComponent[0]);
          }
        }
        else if (paramNamingContextDataStore.Resolve(paramArrayOfNameComponent[0], (BindingTypeHolder)localObject1) != null)
        {
          throw new AlreadyBound();
        }
        paramNamingContextDataStore.Bind(paramArrayOfNameComponent[0], paramObject, paramBindingType);
      }
    }
    else
    {
      ??? = resolveFirstAsContext(paramNamingContextDataStore, paramArrayOfNameComponent);
      localObject1 = new NameComponent[paramArrayOfNameComponent.length - 1];
      System.arraycopy(paramArrayOfNameComponent, 1, localObject1, 0, paramArrayOfNameComponent.length - 1);
      switch (paramBindingType.value())
      {
      case 0: 
        if (paramBoolean) {
          ((NamingContext)???).rebind((NameComponent[])localObject1, paramObject);
        } else {
          ((NamingContext)???).bind((NameComponent[])localObject1, paramObject);
        }
        break;
      case 1: 
        localObject2 = (NamingContext)paramObject;
        if (paramBoolean) {
          ((NamingContext)???).rebind_context((NameComponent[])localObject1, (NamingContext)localObject2);
        } else {
          ((NamingContext)???).bind_context((NameComponent[])localObject1, (NamingContext)localObject2);
        }
        break;
      default: 
        throw updateWrapper.namingCtxBadBindingtype();
      }
    }
  }
  
  public static org.omg.CORBA.Object doResolve(NamingContextDataStore paramNamingContextDataStore, NameComponent[] paramArrayOfNameComponent)
    throws NotFound, CannotProceed, InvalidName
  {
    org.omg.CORBA.Object localObject = null;
    BindingTypeHolder localBindingTypeHolder = new BindingTypeHolder();
    if (paramArrayOfNameComponent.length < 1) {
      throw new InvalidName();
    }
    if (paramArrayOfNameComponent.length == 1)
    {
      synchronized (paramNamingContextDataStore)
      {
        localObject = paramNamingContextDataStore.Resolve(paramArrayOfNameComponent[0], localBindingTypeHolder);
      }
      if (localObject == null) {
        throw new NotFound(NotFoundReason.missing_node, paramArrayOfNameComponent);
      }
      return localObject;
    }
    if ((1id.length() == 0) && (1kind.length() == 0)) {
      throw new InvalidName();
    }
    ??? = resolveFirstAsContext(paramNamingContextDataStore, paramArrayOfNameComponent);
    NameComponent[] arrayOfNameComponent = new NameComponent[paramArrayOfNameComponent.length - 1];
    System.arraycopy(paramArrayOfNameComponent, 1, arrayOfNameComponent, 0, paramArrayOfNameComponent.length - 1);
    return ((NamingContext)???).resolve(arrayOfNameComponent);
  }
  
  public static void doUnbind(NamingContextDataStore paramNamingContextDataStore, NameComponent[] paramArrayOfNameComponent)
    throws NotFound, CannotProceed, InvalidName
  {
    if (paramArrayOfNameComponent.length < 1) {
      throw new InvalidName();
    }
    if (paramArrayOfNameComponent.length == 1)
    {
      if ((0id.length() == 0) && (0kind.length() == 0)) {
        throw new InvalidName();
      }
      localObject1 = null;
      synchronized (paramNamingContextDataStore)
      {
        localObject1 = paramNamingContextDataStore.Unbind(paramArrayOfNameComponent[0]);
      }
      if (localObject1 == null) {
        throw new NotFound(NotFoundReason.missing_node, paramArrayOfNameComponent);
      }
      return;
    }
    Object localObject1 = resolveFirstAsContext(paramNamingContextDataStore, paramArrayOfNameComponent);
    ??? = new NameComponent[paramArrayOfNameComponent.length - 1];
    System.arraycopy(paramArrayOfNameComponent, 1, ???, 0, paramArrayOfNameComponent.length - 1);
    ((NamingContext)localObject1).unbind((NameComponent[])???);
  }
  
  protected static NamingContext resolveFirstAsContext(NamingContextDataStore paramNamingContextDataStore, NameComponent[] paramArrayOfNameComponent)
    throws NotFound
  {
    org.omg.CORBA.Object localObject = null;
    BindingTypeHolder localBindingTypeHolder = new BindingTypeHolder();
    NamingContext localNamingContext = null;
    synchronized (paramNamingContextDataStore)
    {
      localObject = paramNamingContextDataStore.Resolve(paramArrayOfNameComponent[0], localBindingTypeHolder);
      if (localObject == null) {
        throw new NotFound(NotFoundReason.missing_node, paramArrayOfNameComponent);
      }
    }
    if (value != BindingType.ncontext) {
      throw new NotFound(NotFoundReason.not_context, paramArrayOfNameComponent);
    }
    try
    {
      localNamingContext = NamingContextHelper.narrow(localObject);
    }
    catch (BAD_PARAM localBAD_PARAM)
    {
      throw new NotFound(NotFoundReason.not_context, paramArrayOfNameComponent);
    }
    return localNamingContext;
  }
  
  public static String nameToString(NameComponent[] paramArrayOfNameComponent)
  {
    StringBuffer localStringBuffer = new StringBuffer("{");
    if ((paramArrayOfNameComponent != null) || (paramArrayOfNameComponent.length > 0)) {
      for (int i = 0; i < paramArrayOfNameComponent.length; i++)
      {
        if (i > 0) {
          localStringBuffer.append(",");
        }
        localStringBuffer.append("[").append(id).append(",").append(kind).append("]");
      }
    }
    localStringBuffer.append("}");
    return localStringBuffer.toString();
  }
  
  private static void dprint(String paramString)
  {
    NamingUtils.dprint("NamingContextImpl(" + Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " ems): " + paramString);
  }
  
  public void Bind(NameComponent paramNameComponent, org.omg.CORBA.Object paramObject, BindingType paramBindingType)
  {
    if (paramObject == null) {
      return;
    }
    InternalBindingKey localInternalBindingKey = new InternalBindingKey(paramNameComponent);
    try
    {
      InternalBindingValue localInternalBindingValue;
      if (paramBindingType.value() == 0)
      {
        localInternalBindingValue = new InternalBindingValue(paramBindingType, orb.object_to_string(paramObject));
        localInternalBindingValue.setObjectRef(paramObject);
      }
      else
      {
        localObject = theNameServiceHandle.getObjectKey(paramObject);
        localInternalBindingValue = new InternalBindingValue(paramBindingType, (String)localObject);
        localInternalBindingValue.setObjectRef(paramObject);
      }
      Object localObject = (InternalBindingValue)theHashtable.put(localInternalBindingKey, localInternalBindingValue);
      if (localObject != null) {
        throw updateWrapper.namingCtxRebindAlreadyBound();
      }
      try
      {
        theServantManagerImplHandle.updateContext(objKey, this);
      }
      catch (Exception localException2)
      {
        throw updateWrapper.bindUpdateContextFailed(localException2);
      }
    }
    catch (Exception localException1)
    {
      throw updateWrapper.bindFailure(localException1);
    }
  }
  
  public org.omg.CORBA.Object Resolve(NameComponent paramNameComponent, BindingTypeHolder paramBindingTypeHolder)
    throws SystemException
  {
    if ((id.length() == 0) && (kind.length() == 0))
    {
      value = BindingType.ncontext;
      return theNameServiceHandle.getObjectReferenceFromKey(objKey);
    }
    InternalBindingKey localInternalBindingKey = new InternalBindingKey(paramNameComponent);
    InternalBindingValue localInternalBindingValue = (InternalBindingValue)theHashtable.get(localInternalBindingKey);
    if (localInternalBindingValue == null) {
      return null;
    }
    org.omg.CORBA.Object localObject = null;
    value = theBindingType;
    try
    {
      if (strObjectRef.startsWith("NC"))
      {
        value = BindingType.ncontext;
        return theNameServiceHandle.getObjectReferenceFromKey(strObjectRef);
      }
      localObject = localInternalBindingValue.getObjectRef();
      if (localObject == null) {
        try
        {
          localObject = orb.string_to_object(strObjectRef);
          localInternalBindingValue.setObjectRef(localObject);
        }
        catch (Exception localException1)
        {
          throw readWrapper.resolveConversionFailure(CompletionStatus.COMPLETED_MAYBE, localException1);
        }
      }
    }
    catch (Exception localException2)
    {
      throw readWrapper.resolveFailure(CompletionStatus.COMPLETED_MAYBE, localException2);
    }
    return localObject;
  }
  
  public org.omg.CORBA.Object Unbind(NameComponent paramNameComponent)
    throws SystemException
  {
    try
    {
      InternalBindingKey localInternalBindingKey = new InternalBindingKey(paramNameComponent);
      InternalBindingValue localInternalBindingValue = null;
      try
      {
        localInternalBindingValue = (InternalBindingValue)theHashtable.remove(localInternalBindingKey);
      }
      catch (Exception localException2) {}
      theServantManagerImplHandle.updateContext(objKey, this);
      if (localInternalBindingValue == null) {
        return null;
      }
      if (strObjectRef.startsWith("NC"))
      {
        theServantManagerImplHandle.readInContext(strObjectRef);
        localObject = theNameServiceHandle.getObjectReferenceFromKey(strObjectRef);
        return localObject;
      }
      org.omg.CORBA.Object localObject = localInternalBindingValue.getObjectRef();
      if (localObject == null) {
        localObject = orb.string_to_object(strObjectRef);
      }
      return localObject;
    }
    catch (Exception localException1)
    {
      throw updateWrapper.unbindFailure(CompletionStatus.COMPLETED_MAYBE, localException1);
    }
  }
  
  public void List(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder)
    throws SystemException
  {
    if (biPOA == null) {
      createbiPOA();
    }
    try
    {
      PersistentBindingIterator localPersistentBindingIterator = new PersistentBindingIterator(orb, (Hashtable)theHashtable.clone(), biPOA);
      localPersistentBindingIterator.list(paramInt, paramBindingListHolder);
      byte[] arrayOfByte = biPOA.activate_object(localPersistentBindingIterator);
      org.omg.CORBA.Object localObject = biPOA.id_to_reference(arrayOfByte);
      BindingIterator localBindingIterator = BindingIteratorHelper.narrow(localObject);
      value = localBindingIterator;
    }
    catch (SystemException localSystemException)
    {
      throw localSystemException;
    }
    catch (Exception localException)
    {
      throw readWrapper.transNcListGotExc(localException);
    }
  }
  
  private synchronized void createbiPOA()
  {
    if (biPOA != null) {
      return;
    }
    try
    {
      POA localPOA = (POA)orb.resolve_initial_references("RootPOA");
      localPOA.the_POAManager().activate();
      int i = 0;
      Policy[] arrayOfPolicy = new Policy[3];
      arrayOfPolicy[(i++)] = localPOA.create_lifespan_policy(LifespanPolicyValue.TRANSIENT);
      arrayOfPolicy[(i++)] = localPOA.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID);
      arrayOfPolicy[(i++)] = localPOA.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN);
      biPOA = localPOA.create_POA("BindingIteratorPOA", null, arrayOfPolicy);
      biPOA.the_POAManager().activate();
    }
    catch (Exception localException)
    {
      throw readWrapper.namingCtxBindingIteratorCreate(localException);
    }
  }
  
  public NamingContext NewContext()
    throws SystemException
  {
    try
    {
      return theNameServiceHandle.NewContext();
    }
    catch (SystemException localSystemException)
    {
      throw localSystemException;
    }
    catch (Exception localException)
    {
      throw updateWrapper.transNcNewctxGotExc(localException);
    }
  }
  
  public void Destroy()
    throws SystemException
  {}
  
  public String to_string(NameComponent[] paramArrayOfNameComponent)
    throws InvalidName
  {
    if ((paramArrayOfNameComponent == null) || (paramArrayOfNameComponent.length == 0)) {
      throw new InvalidName();
    }
    String str = getINSImpl().convertToString(paramArrayOfNameComponent);
    if (str == null) {
      throw new InvalidName();
    }
    return str;
  }
  
  public NameComponent[] to_name(String paramString)
    throws InvalidName
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new InvalidName();
    }
    NameComponent[] arrayOfNameComponent = getINSImpl().convertToNameComponent(paramString);
    if ((arrayOfNameComponent == null) || (arrayOfNameComponent.length == 0)) {
      throw new InvalidName();
    }
    for (int i = 0; i < arrayOfNameComponent.length; i++) {
      if (((id == null) || (id.length() == 0)) && ((kind == null) || (kind.length() == 0))) {
        throw new InvalidName();
      }
    }
    return arrayOfNameComponent;
  }
  
  public String to_url(String paramString1, String paramString2)
    throws InvalidAddress, InvalidName
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      throw new InvalidName();
    }
    if (paramString1 == null) {
      throw new InvalidAddress();
    }
    String str = null;
    try
    {
      str = getINSImpl().createURLBasedAddress(paramString1, paramString2);
    }
    catch (Exception localException)
    {
      str = null;
    }
    try
    {
      INSURLHandler.getINSURLHandler().parseURL(str);
    }
    catch (BAD_PARAM localBAD_PARAM)
    {
      throw new InvalidAddress();
    }
    return str;
  }
  
  public org.omg.CORBA.Object resolve_str(String paramString)
    throws NotFound, CannotProceed, InvalidName
  {
    org.omg.CORBA.Object localObject = null;
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new InvalidName();
    }
    NameComponent[] arrayOfNameComponent = getINSImpl().convertToNameComponent(paramString);
    if ((arrayOfNameComponent == null) || (arrayOfNameComponent.length == 0)) {
      throw new InvalidName();
    }
    localObject = resolve(arrayOfNameComponent);
    return localObject;
  }
  
  public boolean IsEmpty()
  {
    return theHashtable.isEmpty();
  }
  
  public void printSize()
  {
    System.out.println("Hashtable Size = " + theHashtable.size());
    Enumeration localEnumeration = theHashtable.keys();
    while (localEnumeration.hasMoreElements())
    {
      InternalBindingValue localInternalBindingValue = (InternalBindingValue)theHashtable.get(localEnumeration.nextElement());
      if (localInternalBindingValue != null) {
        System.out.println("value = " + strObjectRef);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\pcosnaming\NamingContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
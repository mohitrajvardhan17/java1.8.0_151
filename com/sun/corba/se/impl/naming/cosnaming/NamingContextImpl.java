package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import com.sun.corba.se.spi.orb.ORB;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.BAD_PARAM;
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
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public abstract class NamingContextImpl
  extends NamingContextExtPOA
  implements NamingContextDataStore
{
  protected POA nsPOA;
  private Logger readLogger;
  private Logger updateLogger;
  private Logger lifecycleLogger;
  private NamingSystemException wrapper;
  private static NamingSystemException staticWrapper = NamingSystemException.get("naming.update");
  private InterOperableNamingImpl insImpl;
  protected transient ORB orb;
  public static final boolean debug = false;
  
  public NamingContextImpl(ORB paramORB, POA paramPOA)
    throws Exception
  {
    orb = paramORB;
    wrapper = NamingSystemException.get(paramORB, "naming.update");
    insImpl = new InterOperableNamingImpl();
    nsPOA = paramPOA;
    readLogger = paramORB.getLogger("naming.read");
    updateLogger = paramORB.getLogger("naming.update");
    lifecycleLogger = paramORB.getLogger("naming.lifecycle");
  }
  
  public POA getNSPOA()
  {
    return nsPOA;
  }
  
  public void bind(NameComponent[] paramArrayOfNameComponent, org.omg.CORBA.Object paramObject)
    throws NotFound, CannotProceed, InvalidName, AlreadyBound
  {
    if (paramObject == null)
    {
      updateLogger.warning("<<NAMING BIND>> unsuccessful because NULL Object cannot be Bound ");
      throw wrapper.objectIsNull();
    }
    NamingContextImpl localNamingContextImpl = this;
    doBind(localNamingContextImpl, paramArrayOfNameComponent, paramObject, false, BindingType.nobject);
    if (updateLogger.isLoggable(Level.FINE)) {
      updateLogger.fine("<<NAMING BIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent));
    }
  }
  
  public void bind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext)
    throws NotFound, CannotProceed, InvalidName, AlreadyBound
  {
    if (paramNamingContext == null)
    {
      updateLogger.warning("<<NAMING BIND>><<FAILURE>> NULL Context cannot be Bound ");
      throw new BAD_PARAM("Naming Context should not be null ");
    }
    NamingContextImpl localNamingContextImpl = this;
    doBind(localNamingContextImpl, paramArrayOfNameComponent, paramNamingContext, false, BindingType.ncontext);
    if (updateLogger.isLoggable(Level.FINE)) {
      updateLogger.fine("<<NAMING BIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent));
    }
  }
  
  public void rebind(NameComponent[] paramArrayOfNameComponent, org.omg.CORBA.Object paramObject)
    throws NotFound, CannotProceed, InvalidName
  {
    if (paramObject == null)
    {
      updateLogger.warning("<<NAMING REBIND>><<FAILURE>> NULL Object cannot be Bound ");
      throw wrapper.objectIsNull();
    }
    try
    {
      NamingContextImpl localNamingContextImpl = this;
      doBind(localNamingContextImpl, paramArrayOfNameComponent, paramObject, true, BindingType.nobject);
    }
    catch (AlreadyBound localAlreadyBound)
    {
      updateLogger.warning("<<NAMING REBIND>><<FAILURE>>" + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent) + " is already bound to a Naming Context");
      throw wrapper.namingCtxRebindAlreadyBound(localAlreadyBound);
    }
    if (updateLogger.isLoggable(Level.FINE)) {
      updateLogger.fine("<<NAMING REBIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent));
    }
  }
  
  public void rebind_context(NameComponent[] paramArrayOfNameComponent, NamingContext paramNamingContext)
    throws NotFound, CannotProceed, InvalidName
  {
    if (paramNamingContext == null)
    {
      updateLogger.warning("<<NAMING REBIND>><<FAILURE>> NULL Context cannot be Bound ");
      throw wrapper.objectIsNull();
    }
    try
    {
      NamingContextImpl localNamingContextImpl = this;
      doBind(localNamingContextImpl, paramArrayOfNameComponent, paramNamingContext, true, BindingType.ncontext);
    }
    catch (AlreadyBound localAlreadyBound)
    {
      updateLogger.warning("<<NAMING REBIND>><<FAILURE>>" + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent) + " is already bound to a CORBA Object");
      throw wrapper.namingCtxRebindctxAlreadyBound(localAlreadyBound);
    }
    if (updateLogger.isLoggable(Level.FINE)) {
      updateLogger.fine("<<NAMING REBIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent));
    }
  }
  
  public org.omg.CORBA.Object resolve(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, CannotProceed, InvalidName
  {
    NamingContextImpl localNamingContextImpl = this;
    org.omg.CORBA.Object localObject = doResolve(localNamingContextImpl, paramArrayOfNameComponent);
    if (localObject != null)
    {
      if (readLogger.isLoggable(Level.FINE)) {
        readLogger.fine("<<NAMING RESOLVE>><<SUCCESS>> Name: " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent));
      }
    }
    else {
      readLogger.warning("<<NAMING RESOLVE>><<FAILURE>> Name: " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent));
    }
    return localObject;
  }
  
  public void unbind(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, CannotProceed, InvalidName
  {
    NamingContextImpl localNamingContextImpl = this;
    doUnbind(localNamingContextImpl, paramArrayOfNameComponent);
    if (updateLogger.isLoggable(Level.FINE)) {
      updateLogger.fine("<<NAMING UNBIND>><<SUCCESS>> Name: " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent));
    }
  }
  
  public void list(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder)
  {
    NamingContextImpl localNamingContextImpl = this;
    synchronized (localNamingContextImpl)
    {
      localNamingContextImpl.List(paramInt, paramBindingListHolder, paramBindingIteratorHolder);
    }
    if ((readLogger.isLoggable(Level.FINE)) && (value != null)) {
      readLogger.fine("<<NAMING LIST>><<SUCCESS>>list(" + paramInt + ") -> bindings[" + value.length + "] + iterator: " + value);
    }
  }
  
  public synchronized NamingContext new_context()
  {
    lifecycleLogger.fine("Creating New Naming Context ");
    NamingContextImpl localNamingContextImpl = this;
    synchronized (localNamingContextImpl)
    {
      NamingContext localNamingContext = localNamingContextImpl.NewContext();
      if (localNamingContext != null) {
        lifecycleLogger.fine("<<LIFECYCLE CREATE>><<SUCCESS>>");
      } else {
        lifecycleLogger.severe("<<LIFECYCLE CREATE>><<FAILURE>>");
      }
      return localNamingContext;
    }
  }
  
  public NamingContext bind_new_context(NameComponent[] paramArrayOfNameComponent)
    throws NotFound, AlreadyBound, CannotProceed, InvalidName
  {
    NamingContext localNamingContext1 = null;
    NamingContext localNamingContext2 = null;
    try
    {
      localNamingContext1 = new_context();
      bind_context(paramArrayOfNameComponent, localNamingContext1);
      localNamingContext2 = localNamingContext1;
      localNamingContext1 = null;
      try
      {
        if (localNamingContext1 != null) {
          localNamingContext1.destroy();
        }
      }
      catch (NotEmpty localNotEmpty1) {}
      if (!updateLogger.isLoggable(Level.FINE)) {
        return localNamingContext2;
      }
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
    updateLogger.fine("<<NAMING BIND>>New Context Bound To " + NamingUtils.getDirectoryStructuredName(paramArrayOfNameComponent));
    return localNamingContext2;
  }
  
  public void destroy()
    throws NotEmpty
  {
    lifecycleLogger.fine("Destroying Naming Context ");
    NamingContextImpl localNamingContextImpl = this;
    synchronized (localNamingContextImpl)
    {
      if (localNamingContextImpl.IsEmpty() == true)
      {
        localNamingContextImpl.Destroy();
        lifecycleLogger.fine("<<LIFECYCLE DESTROY>><<SUCCESS>>");
      }
      else
      {
        lifecycleLogger.warning("<<LIFECYCLE DESTROY>><<FAILURE>> NamingContext children are not destroyed still..");
        throw new NotEmpty();
      }
    }
  }
  
  public static void doBind(NamingContextDataStore paramNamingContextDataStore, NameComponent[] paramArrayOfNameComponent, org.omg.CORBA.Object paramObject, boolean paramBoolean, BindingType paramBindingType)
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
        throw staticWrapper.namingCtxBadBindingtype();
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
    try
    {
      Servant localServant = paramNamingContextDataStore.getNSPOA().reference_to_servant((org.omg.CORBA.Object)???);
      return doResolve((NamingContextDataStore)localServant, arrayOfNameComponent);
    }
    catch (Exception localException) {}
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
  
  public String to_string(NameComponent[] paramArrayOfNameComponent)
    throws InvalidName
  {
    if ((paramArrayOfNameComponent == null) || (paramArrayOfNameComponent.length == 0)) {
      throw new InvalidName();
    }
    NamingContextImpl localNamingContextImpl = this;
    String str = insImpl.convertToString(paramArrayOfNameComponent);
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
    NamingContextImpl localNamingContextImpl = this;
    NameComponent[] arrayOfNameComponent = insImpl.convertToNameComponent(paramString);
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
    NamingContextImpl localNamingContextImpl = this;
    String str = null;
    str = insImpl.createURLBasedAddress(paramString1, paramString2);
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
    NamingContextImpl localNamingContextImpl = this;
    NameComponent[] arrayOfNameComponent = insImpl.convertToNameComponent(paramString);
    if ((arrayOfNameComponent == null) || (arrayOfNameComponent.length == 0)) {
      throw new InvalidName();
    }
    localObject = resolve(arrayOfNameComponent);
    return localObject;
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\NamingContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
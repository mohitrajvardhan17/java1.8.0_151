package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;

public class TransientNamingContext
  extends NamingContextImpl
  implements NamingContextDataStore
{
  private Logger readLogger;
  private Logger updateLogger;
  private Logger lifecycleLogger;
  private NamingSystemException wrapper;
  private final Hashtable theHashtable = new Hashtable();
  public Object localRoot;
  
  public TransientNamingContext(ORB paramORB, Object paramObject, POA paramPOA)
    throws Exception
  {
    super(paramORB, paramPOA);
    wrapper = NamingSystemException.get(paramORB, "naming");
    localRoot = paramObject;
    readLogger = paramORB.getLogger("naming.read");
    updateLogger = paramORB.getLogger("naming.update");
    lifecycleLogger = paramORB.getLogger("naming.lifecycle");
    lifecycleLogger.fine("Root TransientNamingContext LIFECYCLE.CREATED");
  }
  
  public final void Bind(NameComponent paramNameComponent, Object paramObject, BindingType paramBindingType)
    throws SystemException
  {
    InternalBindingKey localInternalBindingKey = new InternalBindingKey(paramNameComponent);
    NameComponent[] arrayOfNameComponent = new NameComponent[1];
    arrayOfNameComponent[0] = paramNameComponent;
    Binding localBinding = new Binding(arrayOfNameComponent, paramBindingType);
    InternalBindingValue localInternalBindingValue1 = new InternalBindingValue(localBinding, null);
    theObjectRef = paramObject;
    InternalBindingValue localInternalBindingValue2 = (InternalBindingValue)theHashtable.put(localInternalBindingKey, localInternalBindingValue1);
    if (localInternalBindingValue2 != null)
    {
      updateLogger.warning("<<NAMING BIND>>Name " + getName(paramNameComponent) + " Was Already Bound");
      throw wrapper.transNcBindAlreadyBound();
    }
    if (updateLogger.isLoggable(Level.FINE)) {
      updateLogger.fine("<<NAMING BIND>><<SUCCESS>>Name Component: " + id + "." + kind);
    }
  }
  
  public final Object Resolve(NameComponent paramNameComponent, BindingTypeHolder paramBindingTypeHolder)
    throws SystemException
  {
    if ((id.length() == 0) && (kind.length() == 0))
    {
      value = BindingType.ncontext;
      return localRoot;
    }
    InternalBindingKey localInternalBindingKey = new InternalBindingKey(paramNameComponent);
    InternalBindingValue localInternalBindingValue = (InternalBindingValue)theHashtable.get(localInternalBindingKey);
    if (localInternalBindingValue == null) {
      return null;
    }
    if (readLogger.isLoggable(Level.FINE)) {
      readLogger.fine("<<NAMING RESOLVE>><<SUCCESS>>Namecomponent :" + getName(paramNameComponent));
    }
    value = theBinding.binding_type;
    return theObjectRef;
  }
  
  public final Object Unbind(NameComponent paramNameComponent)
    throws SystemException
  {
    InternalBindingKey localInternalBindingKey = new InternalBindingKey(paramNameComponent);
    InternalBindingValue localInternalBindingValue = (InternalBindingValue)theHashtable.remove(localInternalBindingKey);
    if (localInternalBindingValue == null)
    {
      if (updateLogger.isLoggable(Level.FINE)) {
        updateLogger.fine("<<NAMING UNBIND>><<FAILURE>> There was no binding with the name " + getName(paramNameComponent) + " to Unbind ");
      }
      return null;
    }
    if (updateLogger.isLoggable(Level.FINE)) {
      updateLogger.fine("<<NAMING UNBIND>><<SUCCESS>> NameComponent:  " + getName(paramNameComponent));
    }
    return theObjectRef;
  }
  
  public final void List(int paramInt, BindingListHolder paramBindingListHolder, BindingIteratorHolder paramBindingIteratorHolder)
    throws SystemException
  {
    try
    {
      TransientBindingIterator localTransientBindingIterator = new TransientBindingIterator(orb, (Hashtable)theHashtable.clone(), nsPOA);
      localTransientBindingIterator.list(paramInt, paramBindingListHolder);
      byte[] arrayOfByte = nsPOA.activate_object(localTransientBindingIterator);
      Object localObject = nsPOA.id_to_reference(arrayOfByte);
      BindingIterator localBindingIterator = BindingIteratorHelper.narrow(localObject);
      value = localBindingIterator;
    }
    catch (SystemException localSystemException)
    {
      readLogger.warning("<<NAMING LIST>><<FAILURE>>" + localSystemException);
      throw localSystemException;
    }
    catch (Exception localException)
    {
      readLogger.severe("<<NAMING LIST>><<FAILURE>>" + localException);
      throw wrapper.transNcListGotExc(localException);
    }
  }
  
  public final NamingContext NewContext()
    throws SystemException
  {
    try
    {
      TransientNamingContext localTransientNamingContext = new TransientNamingContext(orb, localRoot, nsPOA);
      byte[] arrayOfByte = nsPOA.activate_object(localTransientNamingContext);
      Object localObject = nsPOA.id_to_reference(arrayOfByte);
      lifecycleLogger.fine("TransientNamingContext LIFECYCLE.CREATE SUCCESSFUL");
      return NamingContextHelper.narrow(localObject);
    }
    catch (SystemException localSystemException)
    {
      lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE CREATE>><<FAILURE>>", localSystemException);
      throw localSystemException;
    }
    catch (Exception localException)
    {
      lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE CREATE>><<FAILURE>>", localException);
      throw wrapper.transNcNewctxGotExc(localException);
    }
  }
  
  public final void Destroy()
    throws SystemException
  {
    try
    {
      byte[] arrayOfByte = nsPOA.servant_to_id(this);
      if (arrayOfByte != null) {
        nsPOA.deactivate_object(arrayOfByte);
      }
      if (lifecycleLogger.isLoggable(Level.FINE)) {
        lifecycleLogger.fine("<<LIFECYCLE DESTROY>><<SUCCESS>>");
      }
    }
    catch (SystemException localSystemException)
    {
      lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE DESTROY>><<FAILURE>>", localSystemException);
      throw localSystemException;
    }
    catch (Exception localException)
    {
      lifecycleLogger.log(Level.WARNING, "<<LIFECYCLE DESTROY>><<FAILURE>>", localException);
      throw wrapper.transNcDestroyGotExc(localException);
    }
  }
  
  private String getName(NameComponent paramNameComponent)
  {
    return id + "." + kind;
  }
  
  public final boolean IsEmpty()
  {
    return theHashtable.isEmpty();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\TransientNamingContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
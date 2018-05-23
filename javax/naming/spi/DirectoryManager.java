package javax.naming.spi;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

public class DirectoryManager
  extends NamingManager
{
  DirectoryManager() {}
  
  public static DirContext getContinuationDirContext(CannotProceedException paramCannotProceedException)
    throws NamingException
  {
    Hashtable localHashtable = paramCannotProceedException.getEnvironment();
    if (localHashtable == null) {
      localHashtable = new Hashtable(7);
    } else {
      localHashtable = (Hashtable)localHashtable.clone();
    }
    localHashtable.put("java.naming.spi.CannotProceedException", paramCannotProceedException);
    return new ContinuationDirContext(paramCannotProceedException, localHashtable);
  }
  
  public static Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes)
    throws Exception
  {
    ObjectFactoryBuilder localObjectFactoryBuilder = getObjectFactoryBuilder();
    ObjectFactory localObjectFactory;
    if (localObjectFactoryBuilder != null)
    {
      localObjectFactory = localObjectFactoryBuilder.createObjectFactory(paramObject, paramHashtable);
      if ((localObjectFactory instanceof DirObjectFactory)) {
        return ((DirObjectFactory)localObjectFactory).getObjectInstance(paramObject, paramName, paramContext, paramHashtable, paramAttributes);
      }
      return localObjectFactory.getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
    }
    Reference localReference = null;
    if ((paramObject instanceof Reference)) {
      localReference = (Reference)paramObject;
    } else if ((paramObject instanceof Referenceable)) {
      localReference = ((Referenceable)paramObject).getReference();
    }
    if (localReference != null)
    {
      String str = localReference.getFactoryClassName();
      if (str != null)
      {
        localObjectFactory = getObjectFactoryFromReference(localReference, str);
        if ((localObjectFactory instanceof DirObjectFactory)) {
          return ((DirObjectFactory)localObjectFactory).getObjectInstance(localReference, paramName, paramContext, paramHashtable, paramAttributes);
        }
        if (localObjectFactory != null) {
          return localObjectFactory.getObjectInstance(localReference, paramName, paramContext, paramHashtable);
        }
        return paramObject;
      }
      localObject = processURLAddrs(localReference, paramName, paramContext, paramHashtable);
      if (localObject != null) {
        return localObject;
      }
    }
    Object localObject = createObjectFromFactories(paramObject, paramName, paramContext, paramHashtable, paramAttributes);
    return localObject != null ? localObject : paramObject;
  }
  
  private static Object createObjectFromFactories(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes)
    throws Exception
  {
    FactoryEnumeration localFactoryEnumeration = ResourceManager.getFactories("java.naming.factory.object", paramHashtable, paramContext);
    if (localFactoryEnumeration == null) {
      return null;
    }
    Object localObject = null;
    while ((localObject == null) && (localFactoryEnumeration.hasMore()))
    {
      ObjectFactory localObjectFactory = (ObjectFactory)localFactoryEnumeration.next();
      if ((localObjectFactory instanceof DirObjectFactory)) {
        localObject = ((DirObjectFactory)localObjectFactory).getObjectInstance(paramObject, paramName, paramContext, paramHashtable, paramAttributes);
      } else {
        localObject = localObjectFactory.getObjectInstance(paramObject, paramName, paramContext, paramHashtable);
      }
    }
    return localObject;
  }
  
  public static DirStateFactory.Result getStateToBind(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable, Attributes paramAttributes)
    throws NamingException
  {
    FactoryEnumeration localFactoryEnumeration = ResourceManager.getFactories("java.naming.factory.state", paramHashtable, paramContext);
    if (localFactoryEnumeration == null) {
      return new DirStateFactory.Result(paramObject, paramAttributes);
    }
    DirStateFactory.Result localResult = null;
    while ((localResult == null) && (localFactoryEnumeration.hasMore()))
    {
      StateFactory localStateFactory = (StateFactory)localFactoryEnumeration.next();
      if ((localStateFactory instanceof DirStateFactory))
      {
        localResult = ((DirStateFactory)localStateFactory).getStateToBind(paramObject, paramName, paramContext, paramHashtable, paramAttributes);
      }
      else
      {
        Object localObject = localStateFactory.getStateToBind(paramObject, paramName, paramContext, paramHashtable);
        if (localObject != null) {
          localResult = new DirStateFactory.Result(localObject, paramAttributes);
        }
      }
    }
    return localResult != null ? localResult : new DirStateFactory.Result(paramObject, paramAttributes);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\spi\DirectoryManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
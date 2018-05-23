package javax.security.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.DomainCombiner;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.text.MessageFormat;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import sun.security.util.ResourcesMgr;

public final class Subject
  implements Serializable
{
  private static final long serialVersionUID = -8308522755600156056L;
  Set<Principal> principals;
  transient Set<Object> pubCredentials;
  transient Set<Object> privCredentials;
  private volatile boolean readOnly = false;
  private static final int PRINCIPAL_SET = 1;
  private static final int PUB_CREDENTIAL_SET = 2;
  private static final int PRIV_CREDENTIAL_SET = 3;
  private static final ProtectionDomain[] NULL_PD_ARRAY = new ProtectionDomain[0];
  
  public Subject()
  {
    principals = Collections.synchronizedSet(new SecureSet(this, 1));
    pubCredentials = Collections.synchronizedSet(new SecureSet(this, 2));
    privCredentials = Collections.synchronizedSet(new SecureSet(this, 3));
  }
  
  public Subject(boolean paramBoolean, Set<? extends Principal> paramSet, Set<?> paramSet1, Set<?> paramSet2)
  {
    if ((paramSet == null) || (paramSet1 == null) || (paramSet2 == null)) {
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.input.s."));
    }
    principals = Collections.synchronizedSet(new SecureSet(this, 1, paramSet));
    pubCredentials = Collections.synchronizedSet(new SecureSet(this, 2, paramSet1));
    privCredentials = Collections.synchronizedSet(new SecureSet(this, 3, paramSet2));
    readOnly = paramBoolean;
  }
  
  public void setReadOnly()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(AuthPermissionHolder.SET_READ_ONLY_PERMISSION);
    }
    readOnly = true;
  }
  
  public boolean isReadOnly()
  {
    return readOnly;
  }
  
  public static Subject getSubject(AccessControlContext paramAccessControlContext)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(AuthPermissionHolder.GET_SUBJECT_PERMISSION);
    }
    if (paramAccessControlContext == null) {
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.AccessControlContext.provided"));
    }
    (Subject)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Subject run()
      {
        DomainCombiner localDomainCombiner = val$acc.getDomainCombiner();
        if (!(localDomainCombiner instanceof SubjectDomainCombiner)) {
          return null;
        }
        SubjectDomainCombiner localSubjectDomainCombiner = (SubjectDomainCombiner)localDomainCombiner;
        return localSubjectDomainCombiner.getSubject();
      }
    });
  }
  
  public static <T> T doAs(Subject paramSubject, PrivilegedAction<T> paramPrivilegedAction)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(AuthPermissionHolder.DO_AS_PERMISSION);
    }
    if (paramPrivilegedAction == null) {
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
    }
    AccessControlContext localAccessControlContext = AccessController.getContext();
    return (T)AccessController.doPrivileged(paramPrivilegedAction, createContext(paramSubject, localAccessControlContext));
  }
  
  public static <T> T doAs(Subject paramSubject, PrivilegedExceptionAction<T> paramPrivilegedExceptionAction)
    throws PrivilegedActionException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(AuthPermissionHolder.DO_AS_PERMISSION);
    }
    if (paramPrivilegedExceptionAction == null) {
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
    }
    AccessControlContext localAccessControlContext = AccessController.getContext();
    return (T)AccessController.doPrivileged(paramPrivilegedExceptionAction, createContext(paramSubject, localAccessControlContext));
  }
  
  public static <T> T doAsPrivileged(Subject paramSubject, PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(AuthPermissionHolder.DO_AS_PRIVILEGED_PERMISSION);
    }
    if (paramPrivilegedAction == null) {
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
    }
    AccessControlContext localAccessControlContext = paramAccessControlContext == null ? new AccessControlContext(NULL_PD_ARRAY) : paramAccessControlContext;
    return (T)AccessController.doPrivileged(paramPrivilegedAction, createContext(paramSubject, localAccessControlContext));
  }
  
  public static <T> T doAsPrivileged(Subject paramSubject, PrivilegedExceptionAction<T> paramPrivilegedExceptionAction, AccessControlContext paramAccessControlContext)
    throws PrivilegedActionException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(AuthPermissionHolder.DO_AS_PRIVILEGED_PERMISSION);
    }
    if (paramPrivilegedExceptionAction == null) {
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.action.provided"));
    }
    AccessControlContext localAccessControlContext = paramAccessControlContext == null ? new AccessControlContext(NULL_PD_ARRAY) : paramAccessControlContext;
    return (T)AccessController.doPrivileged(paramPrivilegedExceptionAction, createContext(paramSubject, localAccessControlContext));
  }
  
  private static AccessControlContext createContext(Subject paramSubject, final AccessControlContext paramAccessControlContext)
  {
    (AccessControlContext)AccessController.doPrivileged(new PrivilegedAction()
    {
      public AccessControlContext run()
      {
        if (val$subject == null) {
          return new AccessControlContext(paramAccessControlContext, null);
        }
        return new AccessControlContext(paramAccessControlContext, new SubjectDomainCombiner(val$subject));
      }
    });
  }
  
  public Set<Principal> getPrincipals()
  {
    return principals;
  }
  
  public <T extends Principal> Set<T> getPrincipals(Class<T> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided"));
    }
    return new ClassSet(1, paramClass);
  }
  
  public Set<Object> getPublicCredentials()
  {
    return pubCredentials;
  }
  
  public Set<Object> getPrivateCredentials()
  {
    return privCredentials;
  }
  
  public <T> Set<T> getPublicCredentials(Class<T> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided"));
    }
    return new ClassSet(2, paramClass);
  }
  
  public <T> Set<T> getPrivateCredentials(Class<T> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.Class.provided"));
    }
    return new ClassSet(3, paramClass);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof Subject))
    {
      Subject localSubject = (Subject)paramObject;
      HashSet localHashSet;
      synchronized (principals)
      {
        localHashSet = new HashSet(principals);
      }
      if (!principals.equals(localHashSet)) {
        return false;
      }
      synchronized (pubCredentials)
      {
        ??? = new HashSet(pubCredentials);
      }
      if (!pubCredentials.equals(???)) {
        return false;
      }
      synchronized (privCredentials)
      {
        ??? = new HashSet(privCredentials);
      }
      return privCredentials.equals(???);
    }
    return false;
  }
  
  public String toString()
  {
    return toString(true);
  }
  
  String toString(boolean paramBoolean)
  {
    String str1 = ResourcesMgr.getString("Subject.");
    String str2 = "";
    Iterator localIterator;
    Object localObject1;
    synchronized (principals)
    {
      localIterator = principals.iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (Principal)localIterator.next();
        str2 = str2 + ResourcesMgr.getString(".Principal.") + ((Principal)localObject1).toString() + ResourcesMgr.getString("NEWLINE");
      }
    }
    synchronized (pubCredentials)
    {
      localIterator = pubCredentials.iterator();
      while (localIterator.hasNext())
      {
        localObject1 = localIterator.next();
        str2 = str2 + ResourcesMgr.getString(".Public.Credential.") + localObject1.toString() + ResourcesMgr.getString("NEWLINE");
      }
    }
    if (paramBoolean) {
      synchronized (privCredentials)
      {
        localIterator = privCredentials.iterator();
        for (;;)
        {
          if (localIterator.hasNext()) {
            try
            {
              localObject1 = localIterator.next();
              str2 = str2 + ResourcesMgr.getString(".Private.Credential.") + localObject1.toString() + ResourcesMgr.getString("NEWLINE");
            }
            catch (SecurityException localSecurityException)
            {
              str2 = str2 + ResourcesMgr.getString(".Private.Credential.inaccessible.");
            }
          }
        }
      }
    }
    return str1 + str2;
  }
  
  public int hashCode()
  {
    int i = 0;
    Iterator localIterator;
    synchronized (principals)
    {
      localIterator = principals.iterator();
      while (localIterator.hasNext())
      {
        Principal localPrincipal = (Principal)localIterator.next();
        i ^= localPrincipal.hashCode();
      }
    }
    synchronized (pubCredentials)
    {
      localIterator = pubCredentials.iterator();
      while (localIterator.hasNext()) {
        i ^= getCredHashCode(localIterator.next());
      }
    }
    return i;
  }
  
  private int getCredHashCode(Object paramObject)
  {
    try
    {
      return paramObject.hashCode();
    }
    catch (IllegalStateException localIllegalStateException) {}
    return paramObject.getClass().toString().hashCode();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    synchronized (principals)
    {
      paramObjectOutputStream.defaultWriteObject();
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    readOnly = localGetField.get("readOnly", false);
    Set localSet = (Set)localGetField.get("principals", null);
    if (localSet == null) {
      throw new NullPointerException(ResourcesMgr.getString("invalid.null.input.s."));
    }
    try
    {
      principals = Collections.synchronizedSet(new SecureSet(this, 1, localSet));
    }
    catch (NullPointerException localNullPointerException)
    {
      principals = Collections.synchronizedSet(new SecureSet(this, 1));
    }
    pubCredentials = Collections.synchronizedSet(new SecureSet(this, 2));
    privCredentials = Collections.synchronizedSet(new SecureSet(this, 3));
  }
  
  static class AuthPermissionHolder
  {
    static final AuthPermission DO_AS_PERMISSION = new AuthPermission("doAs");
    static final AuthPermission DO_AS_PRIVILEGED_PERMISSION = new AuthPermission("doAsPrivileged");
    static final AuthPermission SET_READ_ONLY_PERMISSION = new AuthPermission("setReadOnly");
    static final AuthPermission GET_SUBJECT_PERMISSION = new AuthPermission("getSubject");
    static final AuthPermission MODIFY_PRINCIPALS_PERMISSION = new AuthPermission("modifyPrincipals");
    static final AuthPermission MODIFY_PUBLIC_CREDENTIALS_PERMISSION = new AuthPermission("modifyPublicCredentials");
    static final AuthPermission MODIFY_PRIVATE_CREDENTIALS_PERMISSION = new AuthPermission("modifyPrivateCredentials");
    
    AuthPermissionHolder() {}
  }
  
  private class ClassSet<T>
    extends AbstractSet<T>
  {
    private int which;
    private Class<T> c;
    private Set<T> set;
    
    ClassSet(Class<T> paramClass)
    {
      which = paramClass;
      Class localClass;
      c = localClass;
      set = new HashSet();
      switch (paramClass)
      {
      case 1: 
        synchronized (principals)
        {
          populateSet();
        }
        break;
      case 2: 
        synchronized (pubCredentials)
        {
          populateSet();
        }
        break;
      default: 
        synchronized (privCredentials)
        {
          populateSet();
        }
      }
    }
    
    private void populateSet()
    {
      final Iterator localIterator;
      switch (which)
      {
      case 1: 
        localIterator = principals.iterator();
        break;
      case 2: 
        localIterator = pubCredentials.iterator();
        break;
      default: 
        localIterator = privCredentials.iterator();
      }
      while (localIterator.hasNext())
      {
        Object localObject;
        if (which == 3) {
          localObject = AccessController.doPrivileged(new PrivilegedAction()
          {
            public Object run()
            {
              return localIterator.next();
            }
          });
        } else {
          localObject = localIterator.next();
        }
        if (c.isAssignableFrom(localObject.getClass())) {
          if (which != 3)
          {
            set.add(localObject);
          }
          else
          {
            SecurityManager localSecurityManager = System.getSecurityManager();
            if (localSecurityManager != null) {
              localSecurityManager.checkPermission(new PrivateCredentialPermission(localObject.getClass().getName(), getPrincipals()));
            }
            set.add(localObject);
          }
        }
      }
    }
    
    public int size()
    {
      return set.size();
    }
    
    public Iterator<T> iterator()
    {
      return set.iterator();
    }
    
    public boolean add(T paramT)
    {
      if (!paramT.getClass().isAssignableFrom(c))
      {
        MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("attempting.to.add.an.object.which.is.not.an.instance.of.class"));
        Object[] arrayOfObject = { c.toString() };
        throw new SecurityException(localMessageFormat.format(arrayOfObject));
      }
      return set.add(paramT);
    }
  }
  
  private static class SecureSet<E>
    extends AbstractSet<E>
    implements Serializable
  {
    private static final long serialVersionUID = 7911754171111800359L;
    private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("this$0", Subject.class), new ObjectStreamField("elements", LinkedList.class), new ObjectStreamField("which", Integer.TYPE) };
    Subject subject;
    LinkedList<E> elements;
    private int which;
    
    SecureSet(Subject paramSubject, int paramInt)
    {
      subject = paramSubject;
      which = paramInt;
      elements = new LinkedList();
    }
    
    SecureSet(Subject paramSubject, int paramInt, Set<? extends E> paramSet)
    {
      subject = paramSubject;
      which = paramInt;
      elements = new LinkedList(paramSet);
    }
    
    public int size()
    {
      return elements.size();
    }
    
    public Iterator<E> iterator()
    {
      final LinkedList localLinkedList = elements;
      new Iterator()
      {
        ListIterator<E> i = localLinkedList.listIterator(0);
        
        public boolean hasNext()
        {
          return i.hasNext();
        }
        
        public E next()
        {
          if (which != 3) {
            return (E)i.next();
          }
          SecurityManager localSecurityManager = System.getSecurityManager();
          if (localSecurityManager != null) {
            try
            {
              localSecurityManager.checkPermission(new PrivateCredentialPermission(localLinkedList.get(i.nextIndex()).getClass().getName(), subject.getPrincipals()));
            }
            catch (SecurityException localSecurityException)
            {
              i.next();
              throw localSecurityException;
            }
          }
          return (E)i.next();
        }
        
        public void remove()
        {
          if (subject.isReadOnly()) {
            throw new IllegalStateException(ResourcesMgr.getString("Subject.is.read.only"));
          }
          SecurityManager localSecurityManager = System.getSecurityManager();
          if (localSecurityManager != null) {
            switch (which)
            {
            case 1: 
              localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRINCIPALS_PERMISSION);
              break;
            case 2: 
              localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PUBLIC_CREDENTIALS_PERMISSION);
              break;
            default: 
              localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRIVATE_CREDENTIALS_PERMISSION);
            }
          }
          i.remove();
        }
      };
    }
    
    public boolean add(E paramE)
    {
      if (subject.isReadOnly()) {
        throw new IllegalStateException(ResourcesMgr.getString("Subject.is.read.only"));
      }
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        switch (which)
        {
        case 1: 
          localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRINCIPALS_PERMISSION);
          break;
        case 2: 
          localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PUBLIC_CREDENTIALS_PERMISSION);
          break;
        default: 
          localSecurityManager.checkPermission(Subject.AuthPermissionHolder.MODIFY_PRIVATE_CREDENTIALS_PERMISSION);
        }
      }
      switch (which)
      {
      case 1: 
        if (!(paramE instanceof Principal)) {
          throw new SecurityException(ResourcesMgr.getString("attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set"));
        }
        break;
      }
      if (!elements.contains(paramE)) {
        return elements.add(paramE);
      }
      return false;
    }
    
    public boolean remove(Object paramObject)
    {
      final Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        Object localObject;
        if (which != 3) {
          localObject = localIterator.next();
        } else {
          localObject = AccessController.doPrivileged(new PrivilegedAction()
          {
            public E run()
            {
              return (E)localIterator.next();
            }
          });
        }
        if (localObject == null)
        {
          if (paramObject == null)
          {
            localIterator.remove();
            return true;
          }
        }
        else if (localObject.equals(paramObject))
        {
          localIterator.remove();
          return true;
        }
      }
      return false;
    }
    
    public boolean contains(Object paramObject)
    {
      final Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        Object localObject;
        if (which != 3)
        {
          localObject = localIterator.next();
        }
        else
        {
          SecurityManager localSecurityManager = System.getSecurityManager();
          if (localSecurityManager != null) {
            localSecurityManager.checkPermission(new PrivateCredentialPermission(paramObject.getClass().getName(), subject.getPrincipals()));
          }
          localObject = AccessController.doPrivileged(new PrivilegedAction()
          {
            public E run()
            {
              return (E)localIterator.next();
            }
          });
        }
        if (localObject == null)
        {
          if (paramObject == null) {
            return true;
          }
        }
        else if (localObject.equals(paramObject)) {
          return true;
        }
      }
      return false;
    }
    
    public boolean removeAll(Collection<?> paramCollection)
    {
      Objects.requireNonNull(paramCollection);
      boolean bool = false;
      final Iterator localIterator1 = iterator();
      while (localIterator1.hasNext())
      {
        Object localObject1;
        if (which != 3) {
          localObject1 = localIterator1.next();
        } else {
          localObject1 = AccessController.doPrivileged(new PrivilegedAction()
          {
            public E run()
            {
              return (E)localIterator1.next();
            }
          });
        }
        Iterator localIterator2 = paramCollection.iterator();
        while (localIterator2.hasNext())
        {
          Object localObject2 = localIterator2.next();
          if (localObject1 == null)
          {
            if (localObject2 == null)
            {
              localIterator1.remove();
              bool = true;
              break;
            }
          }
          else if (localObject1.equals(localObject2))
          {
            localIterator1.remove();
            bool = true;
            break;
          }
        }
      }
      return bool;
    }
    
    public boolean retainAll(Collection<?> paramCollection)
    {
      Objects.requireNonNull(paramCollection);
      boolean bool = false;
      int i = 0;
      final Iterator localIterator1 = iterator();
      while (localIterator1.hasNext())
      {
        i = 0;
        Object localObject1;
        if (which != 3) {
          localObject1 = localIterator1.next();
        } else {
          localObject1 = AccessController.doPrivileged(new PrivilegedAction()
          {
            public E run()
            {
              return (E)localIterator1.next();
            }
          });
        }
        Iterator localIterator2 = paramCollection.iterator();
        while (localIterator2.hasNext())
        {
          Object localObject2 = localIterator2.next();
          if (localObject1 == null)
          {
            if (localObject2 == null)
            {
              i = 1;
              break;
            }
          }
          else if (localObject1.equals(localObject2))
          {
            i = 1;
            break;
          }
        }
        if (i == 0)
        {
          localIterator1.remove();
          i = 0;
          bool = true;
        }
      }
      return bool;
    }
    
    public void clear()
    {
      final Iterator localIterator = iterator();
      while (localIterator.hasNext())
      {
        Object localObject;
        if (which != 3) {
          localObject = localIterator.next();
        } else {
          localObject = AccessController.doPrivileged(new PrivilegedAction()
          {
            public E run()
            {
              return (E)localIterator.next();
            }
          });
        }
        localIterator.remove();
      }
    }
    
    private void writeObject(ObjectOutputStream paramObjectOutputStream)
      throws IOException
    {
      if (which == 3)
      {
        localObject = iterator();
        while (((Iterator)localObject).hasNext()) {
          ((Iterator)localObject).next();
        }
      }
      Object localObject = paramObjectOutputStream.putFields();
      ((ObjectOutputStream.PutField)localObject).put("this$0", subject);
      ((ObjectOutputStream.PutField)localObject).put("elements", elements);
      ((ObjectOutputStream.PutField)localObject).put("which", which);
      paramObjectOutputStream.writeFields();
    }
    
    private void readObject(ObjectInputStream paramObjectInputStream)
      throws IOException, ClassNotFoundException
    {
      ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
      subject = ((Subject)localGetField.get("this$0", null));
      which = localGetField.get("which", 0);
      LinkedList localLinkedList = (LinkedList)localGetField.get("elements", null);
      if (localLinkedList.getClass() != LinkedList.class) {
        elements = new LinkedList(localLinkedList);
      } else {
        elements = localLinkedList;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\Subject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
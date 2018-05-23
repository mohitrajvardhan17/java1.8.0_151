package sun.security.provider;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Set;
import javax.security.auth.Subject;
import sun.security.util.Debug;

class SubjectCodeSource
  extends CodeSource
  implements Serializable
{
  private static final long serialVersionUID = 6039418085604715275L;
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  private Subject subject;
  private LinkedList<PolicyParser.PrincipalEntry> principals;
  private static final Class<?>[] PARAMS = { String.class };
  private static final Debug debug = Debug.getInstance("auth", "\t[Auth Access]");
  private ClassLoader sysClassLoader;
  
  SubjectCodeSource(Subject paramSubject, LinkedList<PolicyParser.PrincipalEntry> paramLinkedList, URL paramURL, Certificate[] paramArrayOfCertificate)
  {
    super(paramURL, paramArrayOfCertificate);
    subject = paramSubject;
    principals = (paramLinkedList == null ? new LinkedList() : new LinkedList(paramLinkedList));
    sysClassLoader = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        return ClassLoader.getSystemClassLoader();
      }
    }));
  }
  
  LinkedList<PolicyParser.PrincipalEntry> getPrincipals()
  {
    return principals;
  }
  
  Subject getSubject()
  {
    return subject;
  }
  
  public boolean implies(CodeSource paramCodeSource)
  {
    LinkedList localLinkedList = null;
    if ((paramCodeSource == null) || (!(paramCodeSource instanceof SubjectCodeSource)) || (!super.implies(paramCodeSource)))
    {
      if (debug != null) {
        debug.println("\tSubjectCodeSource.implies: FAILURE 1");
      }
      return false;
    }
    SubjectCodeSource localSubjectCodeSource = (SubjectCodeSource)paramCodeSource;
    if (principals == null)
    {
      if (debug != null) {
        debug.println("\tSubjectCodeSource.implies: PASS 1");
      }
      return true;
    }
    if ((localSubjectCodeSource.getSubject() == null) || (localSubjectCodeSource.getSubject().getPrincipals().size() == 0))
    {
      if (debug != null) {
        debug.println("\tSubjectCodeSource.implies: FAILURE 2");
      }
      return false;
    }
    ListIterator localListIterator = principals.listIterator(0);
    while (localListIterator.hasNext())
    {
      PolicyParser.PrincipalEntry localPrincipalEntry1 = (PolicyParser.PrincipalEntry)localListIterator.next();
      try
      {
        Class localClass = Class.forName(principalClass, true, sysClassLoader);
        if (!Principal.class.isAssignableFrom(localClass)) {
          throw new ClassCastException(principalClass + " is not a Principal");
        }
        localObject = localClass.getConstructor(PARAMS);
        localPrincipal = (Principal)((Constructor)localObject).newInstance(new Object[] { principalName });
        if (!localPrincipal.implies(localSubjectCodeSource.getSubject()))
        {
          if (debug != null) {
            debug.println("\tSubjectCodeSource.implies: FAILURE 3");
          }
          return false;
        }
        if (debug != null) {
          debug.println("\tSubjectCodeSource.implies: PASS 2");
        }
        return true;
      }
      catch (Exception localException)
      {
        Object localObject;
        Principal localPrincipal;
        if (localLinkedList == null)
        {
          if (localSubjectCodeSource.getSubject() == null)
          {
            if (debug != null) {
              debug.println("\tSubjectCodeSource.implies: FAILURE 4");
            }
            return false;
          }
          localObject = localSubjectCodeSource.getSubject().getPrincipals().iterator();
          localLinkedList = new LinkedList();
          while (((Iterator)localObject).hasNext())
          {
            localPrincipal = (Principal)((Iterator)localObject).next();
            PolicyParser.PrincipalEntry localPrincipalEntry2 = new PolicyParser.PrincipalEntry(localPrincipal.getClass().getName(), localPrincipal.getName());
            localLinkedList.add(localPrincipalEntry2);
          }
        }
        if (!subjectListImpliesPrincipalEntry(localLinkedList, localPrincipalEntry1))
        {
          if (debug != null) {
            debug.println("\tSubjectCodeSource.implies: FAILURE 5");
          }
          return false;
        }
      }
    }
    if (debug != null) {
      debug.println("\tSubjectCodeSource.implies: PASS 3");
    }
    return true;
  }
  
  private boolean subjectListImpliesPrincipalEntry(LinkedList<PolicyParser.PrincipalEntry> paramLinkedList, PolicyParser.PrincipalEntry paramPrincipalEntry)
  {
    ListIterator localListIterator = paramLinkedList.listIterator(0);
    while (localListIterator.hasNext())
    {
      PolicyParser.PrincipalEntry localPrincipalEntry = (PolicyParser.PrincipalEntry)localListIterator.next();
      if (((paramPrincipalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS")) || (paramPrincipalEntry.getPrincipalClass().equals(localPrincipalEntry.getPrincipalClass()))) && ((paramPrincipalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME")) || (paramPrincipalEntry.getPrincipalName().equals(localPrincipalEntry.getPrincipalName())))) {
        return true;
      }
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!super.equals(paramObject)) {
      return false;
    }
    if (!(paramObject instanceof SubjectCodeSource)) {
      return false;
    }
    SubjectCodeSource localSubjectCodeSource = (SubjectCodeSource)paramObject;
    try
    {
      if (getSubject() != localSubjectCodeSource.getSubject()) {
        return false;
      }
    }
    catch (SecurityException localSecurityException)
    {
      return false;
    }
    if (((principals == null) && (principals != null)) || ((principals != null) && (principals == null))) {
      return false;
    }
    return (principals == null) || (principals == null) || ((principals.containsAll(principals)) && (principals.containsAll(principals)));
  }
  
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public String toString()
  {
    String str = super.toString();
    final Object localObject;
    if (getSubject() != null) {
      if (debug != null)
      {
        localObject = getSubject();
        str = str + "\n" + (String)AccessController.doPrivileged(new PrivilegedAction()
        {
          public String run()
          {
            return localObject.toString();
          }
        });
      }
      else
      {
        str = str + "\n" + getSubject().toString();
      }
    }
    if (principals != null)
    {
      localObject = principals.listIterator();
      while (((ListIterator)localObject).hasNext())
      {
        PolicyParser.PrincipalEntry localPrincipalEntry = (PolicyParser.PrincipalEntry)((ListIterator)localObject).next();
        str = str + rb.getString("NEWLINE") + localPrincipalEntry.getPrincipalClass() + " " + localPrincipalEntry.getPrincipalName();
      }
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\SubjectCodeSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
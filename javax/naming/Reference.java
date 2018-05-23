package javax.naming;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

public class Reference
  implements Cloneable, Serializable
{
  protected String className;
  protected Vector<RefAddr> addrs = null;
  protected String classFactory = null;
  protected String classFactoryLocation = null;
  private static final long serialVersionUID = -1673475790065791735L;
  
  public Reference(String paramString)
  {
    className = paramString;
    addrs = new Vector();
  }
  
  public Reference(String paramString, RefAddr paramRefAddr)
  {
    className = paramString;
    addrs = new Vector();
    addrs.addElement(paramRefAddr);
  }
  
  public Reference(String paramString1, String paramString2, String paramString3)
  {
    this(paramString1);
    classFactory = paramString2;
    classFactoryLocation = paramString3;
  }
  
  public Reference(String paramString1, RefAddr paramRefAddr, String paramString2, String paramString3)
  {
    this(paramString1, paramRefAddr);
    classFactory = paramString2;
    classFactoryLocation = paramString3;
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public String getFactoryClassName()
  {
    return classFactory;
  }
  
  public String getFactoryClassLocation()
  {
    return classFactoryLocation;
  }
  
  public RefAddr get(String paramString)
  {
    int i = addrs.size();
    for (int j = 0; j < i; j++)
    {
      RefAddr localRefAddr = (RefAddr)addrs.elementAt(j);
      if (localRefAddr.getType().compareTo(paramString) == 0) {
        return localRefAddr;
      }
    }
    return null;
  }
  
  public RefAddr get(int paramInt)
  {
    return (RefAddr)addrs.elementAt(paramInt);
  }
  
  public Enumeration<RefAddr> getAll()
  {
    return addrs.elements();
  }
  
  public int size()
  {
    return addrs.size();
  }
  
  public void add(RefAddr paramRefAddr)
  {
    addrs.addElement(paramRefAddr);
  }
  
  public void add(int paramInt, RefAddr paramRefAddr)
  {
    addrs.insertElementAt(paramRefAddr, paramInt);
  }
  
  public Object remove(int paramInt)
  {
    Object localObject = addrs.elementAt(paramInt);
    addrs.removeElementAt(paramInt);
    return localObject;
  }
  
  public void clear()
  {
    addrs.setSize(0);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof Reference)))
    {
      Reference localReference = (Reference)paramObject;
      if ((className.equals(className)) && (localReference.size() == size()))
      {
        Enumeration localEnumeration1 = getAll();
        Enumeration localEnumeration2 = localReference.getAll();
        while (localEnumeration1.hasMoreElements()) {
          if (!((RefAddr)localEnumeration1.nextElement()).equals(localEnumeration2.nextElement())) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = className.hashCode();
    Enumeration localEnumeration = getAll();
    while (localEnumeration.hasMoreElements()) {
      i += ((RefAddr)localEnumeration.nextElement()).hashCode();
    }
    return i;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("Reference Class Name: " + className + "\n");
    int i = addrs.size();
    for (int j = 0; j < i; j++) {
      localStringBuffer.append(get(j).toString());
    }
    return localStringBuffer.toString();
  }
  
  public Object clone()
  {
    Reference localReference = new Reference(className, classFactory, classFactoryLocation);
    Enumeration localEnumeration = getAll();
    addrs = new Vector();
    while (localEnumeration.hasMoreElements()) {
      addrs.addElement(localEnumeration.nextElement());
    }
    return localReference;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\Reference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
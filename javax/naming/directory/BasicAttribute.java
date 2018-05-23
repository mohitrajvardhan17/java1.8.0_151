package javax.naming.directory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

public class BasicAttribute
  implements Attribute
{
  protected String attrID;
  protected transient Vector<Object> values;
  protected boolean ordered = false;
  private static final long serialVersionUID = 6743528196119291326L;
  
  public Object clone()
  {
    BasicAttribute localBasicAttribute;
    try
    {
      localBasicAttribute = (BasicAttribute)super.clone();
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localBasicAttribute = new BasicAttribute(attrID, ordered);
    }
    values = ((Vector)values.clone());
    return localBasicAttribute;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof Attribute)))
    {
      Attribute localAttribute = (Attribute)paramObject;
      if (isOrdered() != localAttribute.isOrdered()) {
        return false;
      }
      int i;
      if ((attrID.equals(localAttribute.getID())) && ((i = size()) == localAttribute.size()))
      {
        try
        {
          if (isOrdered())
          {
            for (int j = 0; j < i; j++) {
              if (!valueEquals(get(j), localAttribute.get(j))) {
                return false;
              }
            }
          }
          else
          {
            NamingEnumeration localNamingEnumeration = localAttribute.getAll();
            while (localNamingEnumeration.hasMoreElements()) {
              if (find(localNamingEnumeration.nextElement()) < 0) {
                return false;
              }
            }
          }
        }
        catch (NamingException localNamingException)
        {
          return false;
        }
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = attrID.hashCode();
    int j = values.size();
    for (int k = 0; k < j; k++)
    {
      Object localObject1 = values.elementAt(k);
      if (localObject1 != null) {
        if (localObject1.getClass().isArray())
        {
          int m = Array.getLength(localObject1);
          for (int n = 0; n < m; n++)
          {
            Object localObject2 = Array.get(localObject1, n);
            if (localObject2 != null) {
              i += localObject2.hashCode();
            }
          }
        }
        else
        {
          i += localObject1.hashCode();
        }
      }
    }
    return i;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer(attrID + ": ");
    if (values.size() == 0)
    {
      localStringBuffer.append("No values");
    }
    else
    {
      int i = 1;
      Enumeration localEnumeration = values.elements();
      while (localEnumeration.hasMoreElements())
      {
        if (i == 0) {
          localStringBuffer.append(", ");
        }
        localStringBuffer.append(localEnumeration.nextElement());
        i = 0;
      }
    }
    return localStringBuffer.toString();
  }
  
  public BasicAttribute(String paramString)
  {
    this(paramString, false);
  }
  
  public BasicAttribute(String paramString, Object paramObject)
  {
    this(paramString, paramObject, false);
  }
  
  public BasicAttribute(String paramString, boolean paramBoolean)
  {
    attrID = paramString;
    values = new Vector();
    ordered = paramBoolean;
  }
  
  public BasicAttribute(String paramString, Object paramObject, boolean paramBoolean)
  {
    this(paramString, paramBoolean);
    values.addElement(paramObject);
  }
  
  public NamingEnumeration<?> getAll()
    throws NamingException
  {
    return new ValuesEnumImpl();
  }
  
  public Object get()
    throws NamingException
  {
    if (values.size() == 0) {
      throw new NoSuchElementException("Attribute " + getID() + " has no value");
    }
    return values.elementAt(0);
  }
  
  public int size()
  {
    return values.size();
  }
  
  public String getID()
  {
    return attrID;
  }
  
  public boolean contains(Object paramObject)
  {
    return find(paramObject) >= 0;
  }
  
  private int find(Object paramObject)
  {
    int i;
    if (paramObject == null)
    {
      i = values.size();
      for (int j = 0; j < i; j++) {
        if (values.elementAt(j) == null) {
          return j;
        }
      }
    }
    else
    {
      Class localClass;
      if ((localClass = paramObject.getClass()).isArray())
      {
        i = values.size();
        for (int k = 0; k < i; k++)
        {
          Object localObject = values.elementAt(k);
          if ((localObject != null) && (localClass == localObject.getClass()) && (arrayEquals(paramObject, localObject))) {
            return k;
          }
        }
      }
      else
      {
        return values.indexOf(paramObject, 0);
      }
    }
    return -1;
  }
  
  private static boolean valueEquals(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == paramObject2) {
      return true;
    }
    if (paramObject1 == null) {
      return false;
    }
    if ((paramObject1.getClass().isArray()) && (paramObject2.getClass().isArray())) {
      return arrayEquals(paramObject1, paramObject2);
    }
    return paramObject1.equals(paramObject2);
  }
  
  private static boolean arrayEquals(Object paramObject1, Object paramObject2)
  {
    int i;
    if ((i = Array.getLength(paramObject1)) != Array.getLength(paramObject2)) {
      return false;
    }
    for (int j = 0; j < i; j++)
    {
      Object localObject1 = Array.get(paramObject1, j);
      Object localObject2 = Array.get(paramObject2, j);
      if ((localObject1 == null) || (localObject2 == null))
      {
        if (localObject1 != localObject2) {
          return false;
        }
      }
      else if (!localObject1.equals(localObject2)) {
        return false;
      }
    }
    return true;
  }
  
  public boolean add(Object paramObject)
  {
    if ((isOrdered()) || (find(paramObject) < 0))
    {
      values.addElement(paramObject);
      return true;
    }
    return false;
  }
  
  public boolean remove(Object paramObject)
  {
    int i = find(paramObject);
    if (i >= 0)
    {
      values.removeElementAt(i);
      return true;
    }
    return false;
  }
  
  public void clear()
  {
    values.setSize(0);
  }
  
  public boolean isOrdered()
  {
    return ordered;
  }
  
  public Object get(int paramInt)
    throws NamingException
  {
    return values.elementAt(paramInt);
  }
  
  public Object remove(int paramInt)
  {
    Object localObject = values.elementAt(paramInt);
    values.removeElementAt(paramInt);
    return localObject;
  }
  
  public void add(int paramInt, Object paramObject)
  {
    if ((!isOrdered()) && (contains(paramObject))) {
      throw new IllegalStateException("Cannot add duplicate to unordered attribute");
    }
    values.insertElementAt(paramObject, paramInt);
  }
  
  public Object set(int paramInt, Object paramObject)
  {
    if ((!isOrdered()) && (contains(paramObject))) {
      throw new IllegalStateException("Cannot add duplicate to unordered attribute");
    }
    Object localObject = values.elementAt(paramInt);
    values.setElementAt(paramObject, paramInt);
    return localObject;
  }
  
  public DirContext getAttributeSyntaxDefinition()
    throws NamingException
  {
    throw new OperationNotSupportedException("attribute syntax");
  }
  
  public DirContext getAttributeDefinition()
    throws NamingException
  {
    throw new OperationNotSupportedException("attribute definition");
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(values.size());
    for (int i = 0; i < values.size(); i++) {
      paramObjectOutputStream.writeObject(values.elementAt(i));
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    values = new Vector(Math.min(1024, i));
    for (;;)
    {
      i--;
      if (i < 0) {
        break;
      }
      values.addElement(paramObjectInputStream.readObject());
    }
  }
  
  class ValuesEnumImpl
    implements NamingEnumeration<Object>
  {
    Enumeration<Object> list = values.elements();
    
    ValuesEnumImpl() {}
    
    public boolean hasMoreElements()
    {
      return list.hasMoreElements();
    }
    
    public Object nextElement()
    {
      return list.nextElement();
    }
    
    public Object next()
      throws NamingException
    {
      return list.nextElement();
    }
    
    public boolean hasMore()
      throws NamingException
    {
      return list.hasMoreElements();
    }
    
    public void close()
      throws NamingException
    {
      list = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\directory\BasicAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
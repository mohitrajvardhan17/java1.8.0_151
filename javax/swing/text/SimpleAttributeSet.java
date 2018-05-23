package javax.swing.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;

public class SimpleAttributeSet
  implements MutableAttributeSet, Serializable, Cloneable
{
  private static final long serialVersionUID = -6631553454711782652L;
  public static final AttributeSet EMPTY = new EmptyAttributeSet();
  private transient LinkedHashMap<Object, Object> table = new LinkedHashMap(3);
  
  public SimpleAttributeSet() {}
  
  public SimpleAttributeSet(AttributeSet paramAttributeSet)
  {
    addAttributes(paramAttributeSet);
  }
  
  public boolean isEmpty()
  {
    return table.isEmpty();
  }
  
  public int getAttributeCount()
  {
    return table.size();
  }
  
  public boolean isDefined(Object paramObject)
  {
    return table.containsKey(paramObject);
  }
  
  public boolean isEqual(AttributeSet paramAttributeSet)
  {
    return (getAttributeCount() == paramAttributeSet.getAttributeCount()) && (containsAttributes(paramAttributeSet));
  }
  
  public AttributeSet copyAttributes()
  {
    return (AttributeSet)clone();
  }
  
  public Enumeration<?> getAttributeNames()
  {
    return Collections.enumeration(table.keySet());
  }
  
  public Object getAttribute(Object paramObject)
  {
    Object localObject = table.get(paramObject);
    if (localObject == null)
    {
      AttributeSet localAttributeSet = getResolveParent();
      if (localAttributeSet != null) {
        localObject = localAttributeSet.getAttribute(paramObject);
      }
    }
    return localObject;
  }
  
  public boolean containsAttribute(Object paramObject1, Object paramObject2)
  {
    return paramObject2.equals(getAttribute(paramObject1));
  }
  
  public boolean containsAttributes(AttributeSet paramAttributeSet)
  {
    boolean bool = true;
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    while ((bool) && (localEnumeration.hasMoreElements()))
    {
      Object localObject = localEnumeration.nextElement();
      bool = paramAttributeSet.getAttribute(localObject).equals(getAttribute(localObject));
    }
    return bool;
  }
  
  public void addAttribute(Object paramObject1, Object paramObject2)
  {
    table.put(paramObject1, paramObject2);
  }
  
  public void addAttributes(AttributeSet paramAttributeSet)
  {
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      addAttribute(localObject, paramAttributeSet.getAttribute(localObject));
    }
  }
  
  public void removeAttribute(Object paramObject)
  {
    table.remove(paramObject);
  }
  
  public void removeAttributes(Enumeration<?> paramEnumeration)
  {
    while (paramEnumeration.hasMoreElements()) {
      removeAttribute(paramEnumeration.nextElement());
    }
  }
  
  public void removeAttributes(AttributeSet paramAttributeSet)
  {
    if (paramAttributeSet == this)
    {
      table.clear();
    }
    else
    {
      Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
      while (localEnumeration.hasMoreElements())
      {
        Object localObject1 = localEnumeration.nextElement();
        Object localObject2 = paramAttributeSet.getAttribute(localObject1);
        if (localObject2.equals(getAttribute(localObject1))) {
          removeAttribute(localObject1);
        }
      }
    }
  }
  
  public AttributeSet getResolveParent()
  {
    return (AttributeSet)table.get(StyleConstants.ResolveAttribute);
  }
  
  public void setResolveParent(AttributeSet paramAttributeSet)
  {
    addAttribute(StyleConstants.ResolveAttribute, paramAttributeSet);
  }
  
  public Object clone()
  {
    SimpleAttributeSet localSimpleAttributeSet;
    try
    {
      localSimpleAttributeSet = (SimpleAttributeSet)super.clone();
      table = ((LinkedHashMap)table.clone());
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      localSimpleAttributeSet = null;
    }
    return localSimpleAttributeSet;
  }
  
  public int hashCode()
  {
    return table.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof AttributeSet))
    {
      AttributeSet localAttributeSet = (AttributeSet)paramObject;
      return isEqual(localAttributeSet);
    }
    return false;
  }
  
  public String toString()
  {
    String str = "";
    Enumeration localEnumeration = getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject1 = localEnumeration.nextElement();
      Object localObject2 = getAttribute(localObject1);
      if ((localObject2 instanceof AttributeSet)) {
        str = str + localObject1 + "=**AttributeSet** ";
      } else {
        str = str + localObject1 + "=" + localObject2 + " ";
      }
    }
    return str;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    StyleContext.writeAttributeSet(paramObjectOutputStream, this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    table = new LinkedHashMap(3);
    StyleContext.readAttributeSet(paramObjectInputStream, this);
  }
  
  static class EmptyAttributeSet
    implements AttributeSet, Serializable
  {
    static final long serialVersionUID = -8714803568785904228L;
    
    EmptyAttributeSet() {}
    
    public int getAttributeCount()
    {
      return 0;
    }
    
    public boolean isDefined(Object paramObject)
    {
      return false;
    }
    
    public boolean isEqual(AttributeSet paramAttributeSet)
    {
      return paramAttributeSet.getAttributeCount() == 0;
    }
    
    public AttributeSet copyAttributes()
    {
      return this;
    }
    
    public Object getAttribute(Object paramObject)
    {
      return null;
    }
    
    public Enumeration getAttributeNames()
    {
      return Collections.emptyEnumeration();
    }
    
    public boolean containsAttribute(Object paramObject1, Object paramObject2)
    {
      return false;
    }
    
    public boolean containsAttributes(AttributeSet paramAttributeSet)
    {
      return paramAttributeSet.getAttributeCount() == 0;
    }
    
    public AttributeSet getResolveParent()
    {
      return null;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      return ((paramObject instanceof AttributeSet)) && (((AttributeSet)paramObject).getAttributeCount() == 0);
    }
    
    public int hashCode()
    {
      return 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\SimpleAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
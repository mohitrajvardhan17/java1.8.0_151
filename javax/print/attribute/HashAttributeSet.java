package javax.print.attribute;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class HashAttributeSet
  implements AttributeSet, Serializable
{
  private static final long serialVersionUID = 5311560590283707917L;
  private Class myInterface;
  private transient HashMap attrMap = new HashMap();
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    Attribute[] arrayOfAttribute = toArray();
    paramObjectOutputStream.writeInt(arrayOfAttribute.length);
    for (int i = 0; i < arrayOfAttribute.length; i++) {
      paramObjectOutputStream.writeObject(arrayOfAttribute[i]);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    paramObjectInputStream.defaultReadObject();
    attrMap = new HashMap();
    int i = paramObjectInputStream.readInt();
    for (int j = 0; j < i; j++)
    {
      Attribute localAttribute = (Attribute)paramObjectInputStream.readObject();
      add(localAttribute);
    }
  }
  
  public HashAttributeSet()
  {
    this(Attribute.class);
  }
  
  public HashAttributeSet(Attribute paramAttribute)
  {
    this(paramAttribute, Attribute.class);
  }
  
  public HashAttributeSet(Attribute[] paramArrayOfAttribute)
  {
    this(paramArrayOfAttribute, Attribute.class);
  }
  
  public HashAttributeSet(AttributeSet paramAttributeSet)
  {
    this(paramAttributeSet, Attribute.class);
  }
  
  protected HashAttributeSet(Class<?> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException("null interface");
    }
    myInterface = paramClass;
  }
  
  protected HashAttributeSet(Attribute paramAttribute, Class<?> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException("null interface");
    }
    myInterface = paramClass;
    add(paramAttribute);
  }
  
  protected HashAttributeSet(Attribute[] paramArrayOfAttribute, Class<?> paramClass)
  {
    if (paramClass == null) {
      throw new NullPointerException("null interface");
    }
    myInterface = paramClass;
    int i = paramArrayOfAttribute == null ? 0 : paramArrayOfAttribute.length;
    for (int j = 0; j < i; j++) {
      add(paramArrayOfAttribute[j]);
    }
  }
  
  protected HashAttributeSet(AttributeSet paramAttributeSet, Class<?> paramClass)
  {
    myInterface = paramClass;
    if (paramAttributeSet != null)
    {
      Attribute[] arrayOfAttribute = paramAttributeSet.toArray();
      int i = arrayOfAttribute == null ? 0 : arrayOfAttribute.length;
      for (int j = 0; j < i; j++) {
        add(arrayOfAttribute[j]);
      }
    }
  }
  
  public Attribute get(Class<?> paramClass)
  {
    return (Attribute)attrMap.get(AttributeSetUtilities.verifyAttributeCategory(paramClass, Attribute.class));
  }
  
  public boolean add(Attribute paramAttribute)
  {
    Object localObject = attrMap.put(paramAttribute.getCategory(), AttributeSetUtilities.verifyAttributeValue(paramAttribute, myInterface));
    return !paramAttribute.equals(localObject);
  }
  
  public boolean remove(Class<?> paramClass)
  {
    return (paramClass != null) && (AttributeSetUtilities.verifyAttributeCategory(paramClass, Attribute.class) != null) && (attrMap.remove(paramClass) != null);
  }
  
  public boolean remove(Attribute paramAttribute)
  {
    return (paramAttribute != null) && (attrMap.remove(paramAttribute.getCategory()) != null);
  }
  
  public boolean containsKey(Class<?> paramClass)
  {
    return (paramClass != null) && (AttributeSetUtilities.verifyAttributeCategory(paramClass, Attribute.class) != null) && (attrMap.get(paramClass) != null);
  }
  
  public boolean containsValue(Attribute paramAttribute)
  {
    return (paramAttribute != null) && ((paramAttribute instanceof Attribute)) && (paramAttribute.equals(attrMap.get(paramAttribute.getCategory())));
  }
  
  public boolean addAll(AttributeSet paramAttributeSet)
  {
    Attribute[] arrayOfAttribute = paramAttributeSet.toArray();
    boolean bool = false;
    for (int i = 0; i < arrayOfAttribute.length; i++)
    {
      Attribute localAttribute = AttributeSetUtilities.verifyAttributeValue(arrayOfAttribute[i], myInterface);
      Object localObject = attrMap.put(localAttribute.getCategory(), localAttribute);
      bool = (!localAttribute.equals(localObject)) || (bool);
    }
    return bool;
  }
  
  public int size()
  {
    return attrMap.size();
  }
  
  public Attribute[] toArray()
  {
    Attribute[] arrayOfAttribute = new Attribute[size()];
    attrMap.values().toArray(arrayOfAttribute);
    return arrayOfAttribute;
  }
  
  public void clear()
  {
    attrMap.clear();
  }
  
  public boolean isEmpty()
  {
    return attrMap.isEmpty();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof AttributeSet))) {
      return false;
    }
    AttributeSet localAttributeSet = (AttributeSet)paramObject;
    if (localAttributeSet.size() != size()) {
      return false;
    }
    Attribute[] arrayOfAttribute = toArray();
    for (int i = 0; i < arrayOfAttribute.length; i++) {
      if (!localAttributeSet.containsValue(arrayOfAttribute[i])) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 0;
    Attribute[] arrayOfAttribute = toArray();
    for (int j = 0; j < arrayOfAttribute.length; j++) {
      i += arrayOfAttribute[j].hashCode();
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\print\attribute\HashAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
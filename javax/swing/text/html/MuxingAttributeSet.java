package javax.swing.text.html;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

class MuxingAttributeSet
  implements AttributeSet, Serializable
{
  private AttributeSet[] attrs;
  
  public MuxingAttributeSet(AttributeSet[] paramArrayOfAttributeSet)
  {
    attrs = paramArrayOfAttributeSet;
  }
  
  protected MuxingAttributeSet() {}
  
  protected synchronized void setAttributes(AttributeSet[] paramArrayOfAttributeSet)
  {
    attrs = paramArrayOfAttributeSet;
  }
  
  protected synchronized AttributeSet[] getAttributes()
  {
    return attrs;
  }
  
  protected synchronized void insertAttributeSetAt(AttributeSet paramAttributeSet, int paramInt)
  {
    int i = attrs.length;
    AttributeSet[] arrayOfAttributeSet = new AttributeSet[i + 1];
    if (paramInt < i)
    {
      if (paramInt > 0)
      {
        System.arraycopy(attrs, 0, arrayOfAttributeSet, 0, paramInt);
        System.arraycopy(attrs, paramInt, arrayOfAttributeSet, paramInt + 1, i - paramInt);
      }
      else
      {
        System.arraycopy(attrs, 0, arrayOfAttributeSet, 1, i);
      }
    }
    else {
      System.arraycopy(attrs, 0, arrayOfAttributeSet, 0, i);
    }
    arrayOfAttributeSet[paramInt] = paramAttributeSet;
    attrs = arrayOfAttributeSet;
  }
  
  protected synchronized void removeAttributeSetAt(int paramInt)
  {
    int i = attrs.length;
    AttributeSet[] arrayOfAttributeSet = new AttributeSet[i - 1];
    if (i > 0) {
      if (paramInt == 0)
      {
        System.arraycopy(attrs, 1, arrayOfAttributeSet, 0, i - 1);
      }
      else if (paramInt < i - 1)
      {
        System.arraycopy(attrs, 0, arrayOfAttributeSet, 0, paramInt);
        System.arraycopy(attrs, paramInt + 1, arrayOfAttributeSet, paramInt, i - paramInt - 1);
      }
      else
      {
        System.arraycopy(attrs, 0, arrayOfAttributeSet, 0, i - 1);
      }
    }
    attrs = arrayOfAttributeSet;
  }
  
  public int getAttributeCount()
  {
    AttributeSet[] arrayOfAttributeSet = getAttributes();
    int i = 0;
    for (int j = 0; j < arrayOfAttributeSet.length; j++) {
      i += arrayOfAttributeSet[j].getAttributeCount();
    }
    return i;
  }
  
  public boolean isDefined(Object paramObject)
  {
    AttributeSet[] arrayOfAttributeSet = getAttributes();
    for (int i = 0; i < arrayOfAttributeSet.length; i++) {
      if (arrayOfAttributeSet[i].isDefined(paramObject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isEqual(AttributeSet paramAttributeSet)
  {
    return (getAttributeCount() == paramAttributeSet.getAttributeCount()) && (containsAttributes(paramAttributeSet));
  }
  
  public AttributeSet copyAttributes()
  {
    AttributeSet[] arrayOfAttributeSet = getAttributes();
    SimpleAttributeSet localSimpleAttributeSet = new SimpleAttributeSet();
    int i = 0;
    for (int j = arrayOfAttributeSet.length - 1; j >= 0; j--) {
      localSimpleAttributeSet.addAttributes(arrayOfAttributeSet[j]);
    }
    return localSimpleAttributeSet;
  }
  
  public Object getAttribute(Object paramObject)
  {
    AttributeSet[] arrayOfAttributeSet = getAttributes();
    int i = arrayOfAttributeSet.length;
    for (int j = 0; j < i; j++)
    {
      Object localObject = arrayOfAttributeSet[j].getAttribute(paramObject);
      if (localObject != null) {
        return localObject;
      }
    }
    return null;
  }
  
  public Enumeration getAttributeNames()
  {
    return new MuxingAttributeNameEnumeration();
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
  
  public AttributeSet getResolveParent()
  {
    return null;
  }
  
  private class MuxingAttributeNameEnumeration
    implements Enumeration
  {
    private int attrIndex;
    private Enumeration currentEnum;
    
    MuxingAttributeNameEnumeration()
    {
      updateEnum();
    }
    
    public boolean hasMoreElements()
    {
      if (currentEnum == null) {
        return false;
      }
      return currentEnum.hasMoreElements();
    }
    
    public Object nextElement()
    {
      if (currentEnum == null) {
        throw new NoSuchElementException("No more names");
      }
      Object localObject = currentEnum.nextElement();
      if (!currentEnum.hasMoreElements()) {
        updateEnum();
      }
      return localObject;
    }
    
    void updateEnum()
    {
      AttributeSet[] arrayOfAttributeSet = getAttributes();
      currentEnum = null;
      while ((currentEnum == null) && (attrIndex < arrayOfAttributeSet.length))
      {
        currentEnum = arrayOfAttributeSet[(attrIndex++)].getAttributeNames();
        if (!currentEnum.hasMoreElements()) {
          currentEnum = null;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\MuxingAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
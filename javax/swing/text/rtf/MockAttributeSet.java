package javax.swing.text.rtf;

import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

class MockAttributeSet
  implements AttributeSet, MutableAttributeSet
{
  public Dictionary<Object, Object> backing;
  
  MockAttributeSet() {}
  
  public boolean isEmpty()
  {
    return backing.isEmpty();
  }
  
  public int getAttributeCount()
  {
    return backing.size();
  }
  
  public boolean isDefined(Object paramObject)
  {
    return backing.get(paramObject) != null;
  }
  
  public boolean isEqual(AttributeSet paramAttributeSet)
  {
    throw new InternalError("MockAttributeSet: charade revealed!");
  }
  
  public AttributeSet copyAttributes()
  {
    throw new InternalError("MockAttributeSet: charade revealed!");
  }
  
  public Object getAttribute(Object paramObject)
  {
    return backing.get(paramObject);
  }
  
  public void addAttribute(Object paramObject1, Object paramObject2)
  {
    backing.put(paramObject1, paramObject2);
  }
  
  public void addAttributes(AttributeSet paramAttributeSet)
  {
    Enumeration localEnumeration = paramAttributeSet.getAttributeNames();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      backing.put(localObject, paramAttributeSet.getAttribute(localObject));
    }
  }
  
  public void removeAttribute(Object paramObject)
  {
    backing.remove(paramObject);
  }
  
  public void removeAttributes(AttributeSet paramAttributeSet)
  {
    throw new InternalError("MockAttributeSet: charade revealed!");
  }
  
  public void removeAttributes(Enumeration<?> paramEnumeration)
  {
    throw new InternalError("MockAttributeSet: charade revealed!");
  }
  
  public void setResolveParent(AttributeSet paramAttributeSet)
  {
    throw new InternalError("MockAttributeSet: charade revealed!");
  }
  
  public Enumeration getAttributeNames()
  {
    return backing.keys();
  }
  
  public boolean containsAttribute(Object paramObject1, Object paramObject2)
  {
    throw new InternalError("MockAttributeSet: charade revealed!");
  }
  
  public boolean containsAttributes(AttributeSet paramAttributeSet)
  {
    throw new InternalError("MockAttributeSet: charade revealed!");
  }
  
  public AttributeSet getResolveParent()
  {
    throw new InternalError("MockAttributeSet: charade revealed!");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\rtf\MockAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
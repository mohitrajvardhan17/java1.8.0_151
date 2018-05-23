package javax.swing.text;

import java.util.Enumeration;

public abstract interface MutableAttributeSet
  extends AttributeSet
{
  public abstract void addAttribute(Object paramObject1, Object paramObject2);
  
  public abstract void addAttributes(AttributeSet paramAttributeSet);
  
  public abstract void removeAttribute(Object paramObject);
  
  public abstract void removeAttributes(Enumeration<?> paramEnumeration);
  
  public abstract void removeAttributes(AttributeSet paramAttributeSet);
  
  public abstract void setResolveParent(AttributeSet paramAttributeSet);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\MutableAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
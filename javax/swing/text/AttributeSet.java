package javax.swing.text;

import java.util.Enumeration;

public abstract interface AttributeSet
{
  public static final Object NameAttribute = StyleConstants.NameAttribute;
  public static final Object ResolveAttribute = StyleConstants.ResolveAttribute;
  
  public abstract int getAttributeCount();
  
  public abstract boolean isDefined(Object paramObject);
  
  public abstract boolean isEqual(AttributeSet paramAttributeSet);
  
  public abstract AttributeSet copyAttributes();
  
  public abstract Object getAttribute(Object paramObject);
  
  public abstract Enumeration<?> getAttributeNames();
  
  public abstract boolean containsAttribute(Object paramObject1, Object paramObject2);
  
  public abstract boolean containsAttributes(AttributeSet paramAttributeSet);
  
  public abstract AttributeSet getResolveParent();
  
  public static abstract interface CharacterAttribute {}
  
  public static abstract interface ColorAttribute {}
  
  public static abstract interface FontAttribute {}
  
  public static abstract interface ParagraphAttribute {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\AttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
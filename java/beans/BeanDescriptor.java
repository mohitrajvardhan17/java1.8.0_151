package java.beans;

import java.lang.ref.Reference;

public class BeanDescriptor
  extends FeatureDescriptor
{
  private Reference<? extends Class<?>> beanClassRef;
  private Reference<? extends Class<?>> customizerClassRef;
  
  public BeanDescriptor(Class<?> paramClass)
  {
    this(paramClass, null);
  }
  
  public BeanDescriptor(Class<?> paramClass1, Class<?> paramClass2)
  {
    beanClassRef = getWeakReference(paramClass1);
    customizerClassRef = getWeakReference(paramClass2);
    for (String str = paramClass1.getName(); str.indexOf('.') >= 0; str = str.substring(str.indexOf('.') + 1)) {}
    setName(str);
  }
  
  public Class<?> getBeanClass()
  {
    return beanClassRef != null ? (Class)beanClassRef.get() : null;
  }
  
  public Class<?> getCustomizerClass()
  {
    return customizerClassRef != null ? (Class)customizerClassRef.get() : null;
  }
  
  BeanDescriptor(BeanDescriptor paramBeanDescriptor)
  {
    super(paramBeanDescriptor);
    beanClassRef = beanClassRef;
    customizerClassRef = customizerClassRef;
  }
  
  void appendTo(StringBuilder paramStringBuilder)
  {
    appendTo(paramStringBuilder, "beanClass", beanClassRef);
    appendTo(paramStringBuilder, "customizerClass", customizerClassRef);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\BeanDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
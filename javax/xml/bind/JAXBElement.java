package javax.xml.bind;

import java.io.Serializable;
import javax.xml.namespace.QName;

public class JAXBElement<T>
  implements Serializable
{
  protected final QName name;
  protected final Class<T> declaredType;
  protected final Class scope;
  protected T value;
  protected boolean nil = false;
  private static final long serialVersionUID = 1L;
  
  public JAXBElement(QName paramQName, Class<T> paramClass, Class paramClass1, T paramT)
  {
    if ((paramClass == null) || (paramQName == null)) {
      throw new IllegalArgumentException();
    }
    declaredType = paramClass;
    if (paramClass1 == null) {
      paramClass1 = GlobalScope.class;
    }
    scope = paramClass1;
    name = paramQName;
    setValue(paramT);
  }
  
  public JAXBElement(QName paramQName, Class<T> paramClass, T paramT)
  {
    this(paramQName, paramClass, GlobalScope.class, paramT);
  }
  
  public Class<T> getDeclaredType()
  {
    return declaredType;
  }
  
  public QName getName()
  {
    return name;
  }
  
  public void setValue(T paramT)
  {
    value = paramT;
  }
  
  public T getValue()
  {
    return (T)value;
  }
  
  public Class getScope()
  {
    return scope;
  }
  
  public boolean isNil()
  {
    return (value == null) || (nil);
  }
  
  public void setNil(boolean paramBoolean)
  {
    nil = paramBoolean;
  }
  
  public boolean isGlobalScope()
  {
    return scope == GlobalScope.class;
  }
  
  public boolean isTypeSubstituted()
  {
    if (value == null) {
      return false;
    }
    return value.getClass() != declaredType;
  }
  
  public static final class GlobalScope
  {
    public GlobalScope() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\JAXBElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
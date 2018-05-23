package java.lang.annotation;

public abstract interface Annotation
{
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
  
  public abstract Class<? extends Annotation> annotationType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\annotation\Annotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
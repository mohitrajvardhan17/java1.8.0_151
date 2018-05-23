package java.lang.reflect;

public abstract interface AnnotatedWildcardType
  extends AnnotatedType
{
  public abstract AnnotatedType[] getAnnotatedLowerBounds();
  
  public abstract AnnotatedType[] getAnnotatedUpperBounds();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\AnnotatedWildcardType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
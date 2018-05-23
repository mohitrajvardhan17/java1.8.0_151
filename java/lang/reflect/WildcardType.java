package java.lang.reflect;

public abstract interface WildcardType
  extends Type
{
  public abstract Type[] getUpperBounds();
  
  public abstract Type[] getLowerBounds();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\WildcardType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
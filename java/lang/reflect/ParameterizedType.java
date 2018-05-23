package java.lang.reflect;

public abstract interface ParameterizedType
  extends Type
{
  public abstract Type[] getActualTypeArguments();
  
  public abstract Type getRawType();
  
  public abstract Type getOwnerType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\ParameterizedType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
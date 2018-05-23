package java.lang.reflect;

public abstract interface TypeVariable<D extends GenericDeclaration>
  extends Type, AnnotatedElement
{
  public abstract Type[] getBounds();
  
  public abstract D getGenericDeclaration();
  
  public abstract String getName();
  
  public abstract AnnotatedType[] getAnnotatedBounds();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\reflect\TypeVariable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
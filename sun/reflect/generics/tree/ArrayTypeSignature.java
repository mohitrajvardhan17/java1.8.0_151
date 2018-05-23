package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ArrayTypeSignature
  implements FieldTypeSignature
{
  private final TypeSignature componentType;
  
  private ArrayTypeSignature(TypeSignature paramTypeSignature)
  {
    componentType = paramTypeSignature;
  }
  
  public static ArrayTypeSignature make(TypeSignature paramTypeSignature)
  {
    return new ArrayTypeSignature(paramTypeSignature);
  }
  
  public TypeSignature getComponentType()
  {
    return componentType;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitArrayTypeSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\ArrayTypeSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class DoubleSignature
  implements BaseType
{
  private static final DoubleSignature singleton = new DoubleSignature();
  
  private DoubleSignature() {}
  
  public static DoubleSignature make()
  {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitDoubleSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\DoubleSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
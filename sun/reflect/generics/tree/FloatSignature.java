package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class FloatSignature
  implements BaseType
{
  private static final FloatSignature singleton = new FloatSignature();
  
  private FloatSignature() {}
  
  public static FloatSignature make()
  {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitFloatSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\FloatSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
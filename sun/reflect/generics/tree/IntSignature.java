package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class IntSignature
  implements BaseType
{
  private static final IntSignature singleton = new IntSignature();
  
  private IntSignature() {}
  
  public static IntSignature make()
  {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitIntSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\IntSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
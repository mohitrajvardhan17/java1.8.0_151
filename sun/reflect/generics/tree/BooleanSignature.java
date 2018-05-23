package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class BooleanSignature
  implements BaseType
{
  private static final BooleanSignature singleton = new BooleanSignature();
  
  private BooleanSignature() {}
  
  public static BooleanSignature make()
  {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitBooleanSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\BooleanSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
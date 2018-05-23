package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class BottomSignature
  implements FieldTypeSignature
{
  private static final BottomSignature singleton = new BottomSignature();
  
  private BottomSignature() {}
  
  public static BottomSignature make()
  {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitBottomSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\BottomSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
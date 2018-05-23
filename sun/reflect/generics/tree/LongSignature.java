package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class LongSignature
  implements BaseType
{
  private static final LongSignature singleton = new LongSignature();
  
  private LongSignature() {}
  
  public static LongSignature make()
  {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitLongSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\LongSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
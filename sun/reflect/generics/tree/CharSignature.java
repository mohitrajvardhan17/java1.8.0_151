package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class CharSignature
  implements BaseType
{
  private static final CharSignature singleton = new CharSignature();
  
  private CharSignature() {}
  
  public static CharSignature make()
  {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitCharSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\CharSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
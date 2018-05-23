package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ShortSignature
  implements BaseType
{
  private static final ShortSignature singleton = new ShortSignature();
  
  private ShortSignature() {}
  
  public static ShortSignature make()
  {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitShortSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\ShortSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
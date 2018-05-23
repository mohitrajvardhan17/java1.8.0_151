package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ByteSignature
  implements BaseType
{
  private static final ByteSignature singleton = new ByteSignature();
  
  private ByteSignature() {}
  
  public static ByteSignature make()
  {
    return singleton;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitByteSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\ByteSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
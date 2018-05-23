package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class FormalTypeParameter
  implements TypeTree
{
  private final String name;
  private final FieldTypeSignature[] bounds;
  
  private FormalTypeParameter(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature)
  {
    name = paramString;
    bounds = paramArrayOfFieldTypeSignature;
  }
  
  public static FormalTypeParameter make(String paramString, FieldTypeSignature[] paramArrayOfFieldTypeSignature)
  {
    return new FormalTypeParameter(paramString, paramArrayOfFieldTypeSignature);
  }
  
  public FieldTypeSignature[] getBounds()
  {
    return bounds;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitFormalTypeParameter(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\FormalTypeParameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
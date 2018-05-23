package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class SimpleClassTypeSignature
  implements FieldTypeSignature
{
  private final boolean dollar;
  private final String name;
  private final TypeArgument[] typeArgs;
  
  private SimpleClassTypeSignature(String paramString, boolean paramBoolean, TypeArgument[] paramArrayOfTypeArgument)
  {
    name = paramString;
    dollar = paramBoolean;
    typeArgs = paramArrayOfTypeArgument;
  }
  
  public static SimpleClassTypeSignature make(String paramString, boolean paramBoolean, TypeArgument[] paramArrayOfTypeArgument)
  {
    return new SimpleClassTypeSignature(paramString, paramBoolean, paramArrayOfTypeArgument);
  }
  
  public boolean getDollar()
  {
    return dollar;
  }
  
  public String getName()
  {
    return name;
  }
  
  public TypeArgument[] getTypeArguments()
  {
    return typeArgs;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitSimpleClassTypeSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\SimpleClassTypeSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
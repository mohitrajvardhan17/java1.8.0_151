package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class TypeVariableSignature
  implements FieldTypeSignature
{
  private final String identifier;
  
  private TypeVariableSignature(String paramString)
  {
    identifier = paramString;
  }
  
  public static TypeVariableSignature make(String paramString)
  {
    return new TypeVariableSignature(paramString);
  }
  
  public String getIdentifier()
  {
    return identifier;
  }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor)
  {
    paramTypeTreeVisitor.visitTypeVariableSignature(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\tree\TypeVariableSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
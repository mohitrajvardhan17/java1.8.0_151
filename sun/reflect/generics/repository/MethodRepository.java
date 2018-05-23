package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.ReturnType;
import sun.reflect.generics.visitor.Reifier;

public class MethodRepository
  extends ConstructorRepository
{
  private Type returnType;
  
  private MethodRepository(String paramString, GenericsFactory paramGenericsFactory)
  {
    super(paramString, paramGenericsFactory);
  }
  
  public static MethodRepository make(String paramString, GenericsFactory paramGenericsFactory)
  {
    return new MethodRepository(paramString, paramGenericsFactory);
  }
  
  public Type getReturnType()
  {
    if (returnType == null)
    {
      Reifier localReifier = getReifier();
      ((MethodTypeSignature)getTree()).getReturnType().accept(localReifier);
      returnType = localReifier.getResult();
    }
    return returnType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\repository\MethodRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
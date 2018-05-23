package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class ConstructorRepository
  extends GenericDeclRepository<MethodTypeSignature>
{
  private Type[] paramTypes;
  private Type[] exceptionTypes;
  
  protected ConstructorRepository(String paramString, GenericsFactory paramGenericsFactory)
  {
    super(paramString, paramGenericsFactory);
  }
  
  protected MethodTypeSignature parse(String paramString)
  {
    return SignatureParser.make().parseMethodSig(paramString);
  }
  
  public static ConstructorRepository make(String paramString, GenericsFactory paramGenericsFactory)
  {
    return new ConstructorRepository(paramString, paramGenericsFactory);
  }
  
  public Type[] getParameterTypes()
  {
    if (paramTypes == null)
    {
      TypeSignature[] arrayOfTypeSignature = ((MethodTypeSignature)getTree()).getParameterTypes();
      Type[] arrayOfType = new Type[arrayOfTypeSignature.length];
      for (int i = 0; i < arrayOfTypeSignature.length; i++)
      {
        Reifier localReifier = getReifier();
        arrayOfTypeSignature[i].accept(localReifier);
        arrayOfType[i] = localReifier.getResult();
      }
      paramTypes = arrayOfType;
    }
    return (Type[])paramTypes.clone();
  }
  
  public Type[] getExceptionTypes()
  {
    if (exceptionTypes == null)
    {
      FieldTypeSignature[] arrayOfFieldTypeSignature = ((MethodTypeSignature)getTree()).getExceptionTypes();
      Type[] arrayOfType = new Type[arrayOfFieldTypeSignature.length];
      for (int i = 0; i < arrayOfFieldTypeSignature.length; i++)
      {
        Reifier localReifier = getReifier();
        arrayOfFieldTypeSignature[i].accept(localReifier);
        arrayOfType[i] = localReifier.getResult();
      }
      exceptionTypes = arrayOfType;
    }
    return (Type[])exceptionTypes.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\repository\ConstructorRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.ClassSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.TypeTree;
import sun.reflect.generics.visitor.Reifier;

public class ClassRepository
  extends GenericDeclRepository<ClassSignature>
{
  public static final ClassRepository NONE = make("Ljava/lang/Object;", null);
  private volatile Type superclass;
  private volatile Type[] superInterfaces;
  
  private ClassRepository(String paramString, GenericsFactory paramGenericsFactory)
  {
    super(paramString, paramGenericsFactory);
  }
  
  protected ClassSignature parse(String paramString)
  {
    return SignatureParser.make().parseClassSig(paramString);
  }
  
  public static ClassRepository make(String paramString, GenericsFactory paramGenericsFactory)
  {
    return new ClassRepository(paramString, paramGenericsFactory);
  }
  
  public Type getSuperclass()
  {
    Type localType = superclass;
    if (localType == null)
    {
      Reifier localReifier = getReifier();
      ((ClassSignature)getTree()).getSuperclass().accept(localReifier);
      localType = localReifier.getResult();
      superclass = localType;
    }
    return localType;
  }
  
  public Type[] getSuperInterfaces()
  {
    Type[] arrayOfType = superInterfaces;
    if (arrayOfType == null)
    {
      ClassTypeSignature[] arrayOfClassTypeSignature = ((ClassSignature)getTree()).getSuperInterfaces();
      arrayOfType = new Type[arrayOfClassTypeSignature.length];
      for (int i = 0; i < arrayOfClassTypeSignature.length; i++)
      {
        Reifier localReifier = getReifier();
        arrayOfClassTypeSignature[i].accept(localReifier);
        arrayOfType[i] = localReifier.getResult();
      }
      superInterfaces = arrayOfType;
    }
    return (Type[])arrayOfType.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\repository\ClassRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
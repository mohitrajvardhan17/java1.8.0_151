package sun.reflect.generics.repository;

import java.lang.reflect.Type;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.Reifier;

public class FieldRepository
  extends AbstractRepository<TypeSignature>
{
  private Type genericType;
  
  protected FieldRepository(String paramString, GenericsFactory paramGenericsFactory)
  {
    super(paramString, paramGenericsFactory);
  }
  
  protected TypeSignature parse(String paramString)
  {
    return SignatureParser.make().parseTypeSig(paramString);
  }
  
  public static FieldRepository make(String paramString, GenericsFactory paramGenericsFactory)
  {
    return new FieldRepository(paramString, paramGenericsFactory);
  }
  
  public Type getGenericType()
  {
    if (genericType == null)
    {
      Reifier localReifier = getReifier();
      ((TypeSignature)getTree()).accept(localReifier);
      genericType = localReifier.getResult();
    }
    return genericType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\repository\FieldRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
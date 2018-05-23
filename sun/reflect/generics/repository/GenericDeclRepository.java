package sun.reflect.generics.repository;

import java.lang.reflect.TypeVariable;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.Signature;
import sun.reflect.generics.visitor.Reifier;

public abstract class GenericDeclRepository<S extends Signature>
  extends AbstractRepository<S>
{
  private volatile TypeVariable<?>[] typeParams;
  
  protected GenericDeclRepository(String paramString, GenericsFactory paramGenericsFactory)
  {
    super(paramString, paramGenericsFactory);
  }
  
  public TypeVariable<?>[] getTypeParameters()
  {
    TypeVariable[] arrayOfTypeVariable = typeParams;
    if (arrayOfTypeVariable == null)
    {
      FormalTypeParameter[] arrayOfFormalTypeParameter = ((Signature)getTree()).getFormalTypeParameters();
      arrayOfTypeVariable = new TypeVariable[arrayOfFormalTypeParameter.length];
      for (int i = 0; i < arrayOfFormalTypeParameter.length; i++)
      {
        Reifier localReifier = getReifier();
        arrayOfFormalTypeParameter[i].accept(localReifier);
        arrayOfTypeVariable[i] = ((TypeVariable)localReifier.getResult());
      }
      typeParams = arrayOfTypeVariable;
    }
    return (TypeVariable[])arrayOfTypeVariable.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\repository\GenericDeclRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
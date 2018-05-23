package sun.reflect.generics.repository;

import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.tree.Tree;
import sun.reflect.generics.visitor.Reifier;

public abstract class AbstractRepository<T extends Tree>
{
  private final GenericsFactory factory;
  private final T tree = parse(paramString);
  
  private GenericsFactory getFactory()
  {
    return factory;
  }
  
  protected T getTree()
  {
    return tree;
  }
  
  protected Reifier getReifier()
  {
    return Reifier.make(getFactory());
  }
  
  protected AbstractRepository(String paramString, GenericsFactory paramGenericsFactory)
  {
    factory = paramGenericsFactory;
  }
  
  protected abstract T parse(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\repository\AbstractRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
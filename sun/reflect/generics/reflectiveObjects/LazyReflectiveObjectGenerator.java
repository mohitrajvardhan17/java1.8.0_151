package sun.reflect.generics.reflectiveObjects;

import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.visitor.Reifier;

public abstract class LazyReflectiveObjectGenerator
{
  private final GenericsFactory factory;
  
  protected LazyReflectiveObjectGenerator(GenericsFactory paramGenericsFactory)
  {
    factory = paramGenericsFactory;
  }
  
  private GenericsFactory getFactory()
  {
    return factory;
  }
  
  protected Reifier getReifier()
  {
    return Reifier.make(getFactory());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\reflectiveObjects\LazyReflectiveObjectGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
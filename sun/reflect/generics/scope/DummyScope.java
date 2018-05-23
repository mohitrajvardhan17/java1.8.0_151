package sun.reflect.generics.scope;

import java.lang.reflect.TypeVariable;

public class DummyScope
  implements Scope
{
  private static final DummyScope singleton = new DummyScope();
  
  private DummyScope() {}
  
  public static DummyScope make()
  {
    return singleton;
  }
  
  public TypeVariable<?> lookup(String paramString)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\scope\DummyScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
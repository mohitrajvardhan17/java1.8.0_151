package sun.reflect.generics.scope;

import java.lang.reflect.TypeVariable;

public abstract interface Scope
{
  public abstract TypeVariable<?> lookup(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\scope\Scope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
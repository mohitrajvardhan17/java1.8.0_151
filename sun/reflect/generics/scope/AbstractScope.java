package sun.reflect.generics.scope;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;

public abstract class AbstractScope<D extends GenericDeclaration>
  implements Scope
{
  private final D recvr;
  private volatile Scope enclosingScope;
  
  protected AbstractScope(D paramD)
  {
    recvr = paramD;
  }
  
  protected D getRecvr()
  {
    return recvr;
  }
  
  protected abstract Scope computeEnclosingScope();
  
  protected Scope getEnclosingScope()
  {
    Scope localScope = enclosingScope;
    if (localScope == null)
    {
      localScope = computeEnclosingScope();
      enclosingScope = localScope;
    }
    return localScope;
  }
  
  public TypeVariable<?> lookup(String paramString)
  {
    TypeVariable[] arrayOfTypeVariable1 = getRecvr().getTypeParameters();
    for (TypeVariable localTypeVariable : arrayOfTypeVariable1) {
      if (localTypeVariable.getName().equals(paramString)) {
        return localTypeVariable;
      }
    }
    return getEnclosingScope().lookup(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\generics\scope\AbstractScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
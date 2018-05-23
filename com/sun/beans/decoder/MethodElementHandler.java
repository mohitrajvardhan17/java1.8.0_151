package com.sun.beans.decoder;

import com.sun.beans.finder.MethodFinder;
import java.lang.reflect.Method;
import sun.reflect.misc.MethodUtil;

final class MethodElementHandler
  extends NewElementHandler
{
  private String name;
  
  MethodElementHandler() {}
  
  public void addAttribute(String paramString1, String paramString2)
  {
    if (paramString1.equals("name")) {
      name = paramString2;
    } else {
      super.addAttribute(paramString1, paramString2);
    }
  }
  
  protected ValueObject getValueObject(Class<?> paramClass, Object[] paramArrayOfObject)
    throws Exception
  {
    Object localObject1 = getContextBean();
    Class[] arrayOfClass = getArgumentTypes(paramArrayOfObject);
    Method localMethod = paramClass != null ? MethodFinder.findStaticMethod(paramClass, name, arrayOfClass) : MethodFinder.findMethod(localObject1.getClass(), name, arrayOfClass);
    if (localMethod.isVarArgs()) {
      paramArrayOfObject = getArguments(paramArrayOfObject, localMethod.getParameterTypes());
    }
    Object localObject2 = MethodUtil.invoke(localMethod, localObject1, paramArrayOfObject);
    return localMethod.getReturnType().equals(Void.TYPE) ? ValueObjectImpl.VOID : ValueObjectImpl.create(localObject2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\MethodElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
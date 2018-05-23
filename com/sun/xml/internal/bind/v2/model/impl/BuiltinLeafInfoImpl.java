package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.BuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public class BuiltinLeafInfoImpl<TypeT, ClassDeclT>
  extends LeafInfoImpl<TypeT, ClassDeclT>
  implements BuiltinLeafInfo<TypeT, ClassDeclT>
{
  private final QName[] typeNames;
  
  protected BuiltinLeafInfoImpl(TypeT paramTypeT, QName... paramVarArgs)
  {
    super(paramTypeT, paramVarArgs.length > 0 ? paramVarArgs[0] : null);
    typeNames = paramVarArgs;
  }
  
  public final QName[] getTypeNames()
  {
    return typeNames;
  }
  
  /**
   * @deprecated
   */
  public final boolean isElement()
  {
    return false;
  }
  
  /**
   * @deprecated
   */
  public final QName getElementName()
  {
    return null;
  }
  
  /**
   * @deprecated
   */
  public final Element<TypeT, ClassDeclT> asElement()
  {
    return null;
  }
  
  public static <TypeT, ClassDeclT> Map<TypeT, BuiltinLeafInfoImpl<TypeT, ClassDeclT>> createLeaves(Navigator<TypeT, ClassDeclT, ?, ?> paramNavigator)
  {
    HashMap localHashMap = new HashMap();
    Iterator localIterator = RuntimeBuiltinLeafInfoImpl.builtinBeanInfos.iterator();
    while (localIterator.hasNext())
    {
      RuntimeBuiltinLeafInfoImpl localRuntimeBuiltinLeafInfoImpl = (RuntimeBuiltinLeafInfoImpl)localIterator.next();
      Object localObject = paramNavigator.ref(localRuntimeBuiltinLeafInfoImpl.getClazz());
      localHashMap.put(localObject, new BuiltinLeafInfoImpl(localObject, localRuntimeBuiltinLeafInfoImpl.getTypeNames()));
    }
    return localHashMap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\BuiltinLeafInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
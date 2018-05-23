package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.EnumConstant;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;

class EnumConstantImpl<T, C, F, M>
  implements EnumConstant<T, C>
{
  protected final String lexical;
  protected final EnumLeafInfoImpl<T, C, F, M> owner;
  protected final String name;
  protected final EnumConstantImpl<T, C, F, M> next;
  
  public EnumConstantImpl(EnumLeafInfoImpl<T, C, F, M> paramEnumLeafInfoImpl, String paramString1, String paramString2, EnumConstantImpl<T, C, F, M> paramEnumConstantImpl)
  {
    lexical = paramString2;
    owner = paramEnumLeafInfoImpl;
    name = paramString1;
    next = paramEnumConstantImpl;
  }
  
  public EnumLeafInfo<T, C> getEnclosingClass()
  {
    return owner;
  }
  
  public final String getLexicalValue()
  {
    return lexical;
  }
  
  public final String getName()
  {
    return name;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\EnumConstantImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
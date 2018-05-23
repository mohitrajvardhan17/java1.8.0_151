package com.sun.xml.internal.bind.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract class ClassResolver
{
  public ClassResolver() {}
  
  @Nullable
  public abstract Class<?> resolveElementName(@NotNull String paramString1, @NotNull String paramString2)
    throws Exception;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\api\ClassResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
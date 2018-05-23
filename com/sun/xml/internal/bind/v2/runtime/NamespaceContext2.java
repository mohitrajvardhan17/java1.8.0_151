package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import javax.xml.namespace.NamespaceContext;

public abstract interface NamespaceContext2
  extends NamespaceContext
{
  public abstract String declareNamespace(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract int force(@NotNull String paramString1, @NotNull String paramString2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\NamespaceContext2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
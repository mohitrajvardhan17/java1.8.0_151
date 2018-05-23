package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract interface Component
{
  @Nullable
  public abstract <S> S getSPI(@NotNull Class<S> paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\Component.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
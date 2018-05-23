package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;

public abstract interface ComponentEx
  extends Component
{
  @NotNull
  public abstract <S> Iterable<S> getIterableSPI(@NotNull Class<S> paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\ComponentEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.xml.internal.ws.spi.db;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import java.lang.reflect.Type;

public class BindingHelper
{
  public BindingHelper() {}
  
  @NotNull
  public static String mangleNameToVariableName(@NotNull String paramString)
  {
    return NameConverter.standard.toVariableName(paramString);
  }
  
  @NotNull
  public static String mangleNameToClassName(@NotNull String paramString)
  {
    return NameConverter.standard.toClassName(paramString);
  }
  
  @NotNull
  public static String mangleNameToPropertyName(@NotNull String paramString)
  {
    return NameConverter.standard.toPropertyName(paramString);
  }
  
  @Nullable
  public static Type getBaseType(@NotNull Type paramType, @NotNull Class paramClass)
  {
    return (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(paramType, paramClass);
  }
  
  public static <T> Class<T> erasure(Type paramType)
  {
    return (Class)Utils.REFLECTION_NAVIGATOR.erasure(paramType);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\BindingHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
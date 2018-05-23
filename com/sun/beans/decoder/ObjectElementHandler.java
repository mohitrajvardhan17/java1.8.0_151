package com.sun.beans.decoder;

import java.beans.Expression;
import java.util.Locale;

class ObjectElementHandler
  extends NewElementHandler
{
  private String idref;
  private String field;
  private Integer index;
  private String property;
  private String method;
  
  ObjectElementHandler() {}
  
  public final void addAttribute(String paramString1, String paramString2)
  {
    if (paramString1.equals("idref"))
    {
      idref = paramString2;
    }
    else if (paramString1.equals("field"))
    {
      field = paramString2;
    }
    else if (paramString1.equals("index"))
    {
      index = Integer.valueOf(paramString2);
      addArgument(index);
    }
    else if (paramString1.equals("property"))
    {
      property = paramString2;
    }
    else if (paramString1.equals("method"))
    {
      method = paramString2;
    }
    else
    {
      super.addAttribute(paramString1, paramString2);
    }
  }
  
  public final void startElement()
  {
    if ((field != null) || (idref != null)) {
      getValueObject();
    }
  }
  
  protected boolean isArgument()
  {
    return true;
  }
  
  protected final ValueObject getValueObject(Class<?> paramClass, Object[] paramArrayOfObject)
    throws Exception
  {
    if (field != null) {
      return ValueObjectImpl.create(FieldElementHandler.getFieldValue(getContextBean(), field));
    }
    if (idref != null) {
      return ValueObjectImpl.create(getVariable(idref));
    }
    Object localObject = getContextBean();
    String str;
    if (index != null)
    {
      str = paramArrayOfObject.length == 2 ? "set" : "get";
    }
    else if (property != null)
    {
      str = paramArrayOfObject.length == 1 ? "set" : "get";
      if (0 < property.length()) {
        str = str + property.substring(0, 1).toUpperCase(Locale.ENGLISH) + property.substring(1);
      }
    }
    else
    {
      str = (method != null) && (0 < method.length()) ? method : "new";
    }
    Expression localExpression = new Expression(localObject, str, paramArrayOfObject);
    return ValueObjectImpl.create(localExpression.getValue());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\ObjectElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
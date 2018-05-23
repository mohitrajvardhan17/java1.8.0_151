package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public class StringEditor
  extends PropertyEditorSupport
{
  public StringEditor() {}
  
  public String getJavaInitializationString()
  {
    Object localObject = getValue();
    if (localObject == null) {
      return "null";
    }
    String str1 = localObject.toString();
    int i = str1.length();
    StringBuilder localStringBuilder = new StringBuilder(i + 2);
    localStringBuilder.append('"');
    for (int j = 0; j < i; j++)
    {
      char c = str1.charAt(j);
      switch (c)
      {
      case '\b': 
        localStringBuilder.append("\\b");
        break;
      case '\t': 
        localStringBuilder.append("\\t");
        break;
      case '\n': 
        localStringBuilder.append("\\n");
        break;
      case '\f': 
        localStringBuilder.append("\\f");
        break;
      case '\r': 
        localStringBuilder.append("\\r");
        break;
      case '"': 
        localStringBuilder.append("\\\"");
        break;
      case '\\': 
        localStringBuilder.append("\\\\");
        break;
      default: 
        if ((c < ' ') || (c > '~'))
        {
          localStringBuilder.append("\\u");
          String str2 = Integer.toHexString(c);
          for (int k = str2.length(); k < 4; k++) {
            localStringBuilder.append('0');
          }
          localStringBuilder.append(str2);
        }
        else
        {
          localStringBuilder.append(c);
        }
        break;
      }
    }
    localStringBuilder.append('"');
    return localStringBuilder.toString();
  }
  
  public void setAsText(String paramString)
  {
    setValue(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\StringEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
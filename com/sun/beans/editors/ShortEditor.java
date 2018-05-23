package com.sun.beans.editors;

public class ShortEditor
  extends NumberEditor
{
  public ShortEditor() {}
  
  public String getJavaInitializationString()
  {
    Object localObject = getValue();
    return localObject != null ? "((short)" + localObject + ")" : "null";
  }
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    setValue(paramString == null ? null : Short.decode(paramString));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\ShortEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
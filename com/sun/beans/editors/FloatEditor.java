package com.sun.beans.editors;

public class FloatEditor
  extends NumberEditor
{
  public FloatEditor() {}
  
  public String getJavaInitializationString()
  {
    Object localObject = getValue();
    return localObject != null ? localObject + "F" : "null";
  }
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    setValue(paramString == null ? null : Float.valueOf(paramString));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\FloatEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
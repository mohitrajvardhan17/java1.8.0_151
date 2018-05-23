package com.sun.beans.editors;

public class LongEditor
  extends NumberEditor
{
  public LongEditor() {}
  
  public String getJavaInitializationString()
  {
    Object localObject = getValue();
    return localObject != null ? localObject + "L" : "null";
  }
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    setValue(paramString == null ? null : Long.decode(paramString));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\LongEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
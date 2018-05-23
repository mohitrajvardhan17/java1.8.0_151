package com.sun.beans.editors;

public class DoubleEditor
  extends NumberEditor
{
  public DoubleEditor() {}
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    setValue(paramString == null ? null : Double.valueOf(paramString));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\DoubleEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
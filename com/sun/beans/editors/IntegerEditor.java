package com.sun.beans.editors;

public class IntegerEditor
  extends NumberEditor
{
  public IntegerEditor() {}
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    setValue(paramString == null ? null : Integer.decode(paramString));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\IntegerEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
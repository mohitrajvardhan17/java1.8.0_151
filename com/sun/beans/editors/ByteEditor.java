package com.sun.beans.editors;

public class ByteEditor
  extends NumberEditor
{
  public ByteEditor() {}
  
  public String getJavaInitializationString()
  {
    Object localObject = getValue();
    return localObject != null ? "((byte)" + localObject + ")" : "null";
  }
  
  public void setAsText(String paramString)
    throws IllegalArgumentException
  {
    setValue(paramString == null ? null : Byte.decode(paramString));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\editors\ByteEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
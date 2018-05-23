package javax.accessibility;

import javax.swing.text.AttributeSet;

public abstract interface AccessibleEditableText
  extends AccessibleText
{
  public abstract void setTextContents(String paramString);
  
  public abstract void insertTextAtIndex(int paramInt, String paramString);
  
  public abstract String getTextRange(int paramInt1, int paramInt2);
  
  public abstract void delete(int paramInt1, int paramInt2);
  
  public abstract void cut(int paramInt1, int paramInt2);
  
  public abstract void paste(int paramInt);
  
  public abstract void replaceText(int paramInt1, int paramInt2, String paramString);
  
  public abstract void selectText(int paramInt1, int paramInt2);
  
  public abstract void setAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleEditableText.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
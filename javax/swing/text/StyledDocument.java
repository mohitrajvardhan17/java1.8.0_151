package javax.swing.text;

import java.awt.Color;
import java.awt.Font;

public abstract interface StyledDocument
  extends Document
{
  public abstract Style addStyle(String paramString, Style paramStyle);
  
  public abstract void removeStyle(String paramString);
  
  public abstract Style getStyle(String paramString);
  
  public abstract void setCharacterAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, boolean paramBoolean);
  
  public abstract void setParagraphAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, boolean paramBoolean);
  
  public abstract void setLogicalStyle(int paramInt, Style paramStyle);
  
  public abstract Style getLogicalStyle(int paramInt);
  
  public abstract Element getParagraphElement(int paramInt);
  
  public abstract Element getCharacterElement(int paramInt);
  
  public abstract Color getForeground(AttributeSet paramAttributeSet);
  
  public abstract Color getBackground(AttributeSet paramAttributeSet);
  
  public abstract Font getFont(AttributeSet paramAttributeSet);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\StyledDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
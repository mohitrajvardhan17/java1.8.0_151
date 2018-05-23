package javax.swing.text.html;

import java.io.Serializable;
import javax.swing.text.AttributeSet;

public class Option
  implements Serializable
{
  private boolean selected;
  private String label;
  private AttributeSet attr;
  
  public Option(AttributeSet paramAttributeSet)
  {
    attr = paramAttributeSet.copyAttributes();
    selected = (paramAttributeSet.getAttribute(HTML.Attribute.SELECTED) != null);
  }
  
  public void setLabel(String paramString)
  {
    label = paramString;
  }
  
  public String getLabel()
  {
    return label;
  }
  
  public AttributeSet getAttributes()
  {
    return attr;
  }
  
  public String toString()
  {
    return label;
  }
  
  protected void setSelection(boolean paramBoolean)
  {
    selected = paramBoolean;
  }
  
  public boolean isSelected()
  {
    return selected;
  }
  
  public String getValue()
  {
    String str = (String)attr.getAttribute(HTML.Attribute.VALUE);
    if (str == null) {
      str = label;
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\html\Option.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
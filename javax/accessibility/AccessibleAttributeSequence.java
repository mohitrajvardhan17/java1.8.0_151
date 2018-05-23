package javax.accessibility;

import javax.swing.text.AttributeSet;

public class AccessibleAttributeSequence
{
  public int startIndex;
  public int endIndex;
  public AttributeSet attributes;
  
  public AccessibleAttributeSequence(int paramInt1, int paramInt2, AttributeSet paramAttributeSet)
  {
    startIndex = paramInt1;
    endIndex = paramInt2;
    attributes = paramAttributeSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleAttributeSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
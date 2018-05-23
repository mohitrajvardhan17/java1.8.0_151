package javax.accessibility;

import java.awt.Rectangle;

public abstract interface AccessibleExtendedText
{
  public static final int LINE = 4;
  public static final int ATTRIBUTE_RUN = 5;
  
  public abstract String getTextRange(int paramInt1, int paramInt2);
  
  public abstract AccessibleTextSequence getTextSequenceAt(int paramInt1, int paramInt2);
  
  public abstract AccessibleTextSequence getTextSequenceAfter(int paramInt1, int paramInt2);
  
  public abstract AccessibleTextSequence getTextSequenceBefore(int paramInt1, int paramInt2);
  
  public abstract Rectangle getTextBounds(int paramInt1, int paramInt2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\accessibility\AccessibleExtendedText.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
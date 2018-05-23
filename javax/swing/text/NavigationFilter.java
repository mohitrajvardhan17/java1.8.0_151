package javax.swing.text;

import javax.swing.plaf.TextUI;

public class NavigationFilter
{
  public NavigationFilter() {}
  
  public void setDot(FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias)
  {
    paramFilterBypass.setDot(paramInt, paramBias);
  }
  
  public void moveDot(FilterBypass paramFilterBypass, int paramInt, Position.Bias paramBias)
  {
    paramFilterBypass.moveDot(paramInt, paramBias);
  }
  
  public int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias)
    throws BadLocationException
  {
    return paramJTextComponent.getUI().getNextVisualPositionFrom(paramJTextComponent, paramInt1, paramBias, paramInt2, paramArrayOfBias);
  }
  
  public static abstract class FilterBypass
  {
    public FilterBypass() {}
    
    public abstract Caret getCaret();
    
    public abstract void setDot(int paramInt, Position.Bias paramBias);
    
    public abstract void moveDot(int paramInt, Position.Bias paramBias);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\NavigationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.awt.im;

import java.awt.font.TextAttribute;
import java.util.Map;

public class InputMethodHighlight
{
  public static final int RAW_TEXT = 0;
  public static final int CONVERTED_TEXT = 1;
  public static final InputMethodHighlight UNSELECTED_RAW_TEXT_HIGHLIGHT = new InputMethodHighlight(false, 0);
  public static final InputMethodHighlight SELECTED_RAW_TEXT_HIGHLIGHT = new InputMethodHighlight(true, 0);
  public static final InputMethodHighlight UNSELECTED_CONVERTED_TEXT_HIGHLIGHT = new InputMethodHighlight(false, 1);
  public static final InputMethodHighlight SELECTED_CONVERTED_TEXT_HIGHLIGHT = new InputMethodHighlight(true, 1);
  private boolean selected;
  private int state;
  private int variation;
  private Map<TextAttribute, ?> style;
  
  public InputMethodHighlight(boolean paramBoolean, int paramInt)
  {
    this(paramBoolean, paramInt, 0, null);
  }
  
  public InputMethodHighlight(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    this(paramBoolean, paramInt1, paramInt2, null);
  }
  
  public InputMethodHighlight(boolean paramBoolean, int paramInt1, int paramInt2, Map<TextAttribute, ?> paramMap)
  {
    selected = paramBoolean;
    if ((paramInt1 != 0) && (paramInt1 != 1)) {
      throw new IllegalArgumentException("unknown input method highlight state");
    }
    state = paramInt1;
    variation = paramInt2;
    style = paramMap;
  }
  
  public boolean isSelected()
  {
    return selected;
  }
  
  public int getState()
  {
    return state;
  }
  
  public int getVariation()
  {
    return variation;
  }
  
  public Map<TextAttribute, ?> getStyle()
  {
    return style;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\im\InputMethodHighlight.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
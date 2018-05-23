package javax.swing.plaf.synth;

import java.awt.FontMetrics;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.text.View;
import sun.swing.MenuItemLayoutHelper;
import sun.swing.MenuItemLayoutHelper.ColumnAlignment;
import sun.swing.MenuItemLayoutHelper.LayoutResult;
import sun.swing.MenuItemLayoutHelper.RectSize;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.plaf.synth.SynthIcon;

class SynthMenuItemLayoutHelper
  extends MenuItemLayoutHelper
{
  public static final StringUIClientPropertyKey MAX_ACC_OR_ARROW_WIDTH = new StringUIClientPropertyKey("maxAccOrArrowWidth");
  public static final MenuItemLayoutHelper.ColumnAlignment LTR_ALIGNMENT_1 = new MenuItemLayoutHelper.ColumnAlignment(2, 2, 2, 4, 4);
  public static final MenuItemLayoutHelper.ColumnAlignment LTR_ALIGNMENT_2 = new MenuItemLayoutHelper.ColumnAlignment(2, 2, 2, 2, 4);
  public static final MenuItemLayoutHelper.ColumnAlignment RTL_ALIGNMENT_1 = new MenuItemLayoutHelper.ColumnAlignment(4, 4, 4, 2, 2);
  public static final MenuItemLayoutHelper.ColumnAlignment RTL_ALIGNMENT_2 = new MenuItemLayoutHelper.ColumnAlignment(4, 4, 4, 4, 2);
  private SynthContext context;
  private SynthContext accContext;
  private SynthStyle style;
  private SynthStyle accStyle;
  private SynthGraphicsUtils gu;
  private SynthGraphicsUtils accGu;
  private boolean alignAcceleratorText;
  private int maxAccOrArrowWidth;
  
  public SynthMenuItemLayoutHelper(SynthContext paramSynthContext1, SynthContext paramSynthContext2, JMenuItem paramJMenuItem, Icon paramIcon1, Icon paramIcon2, Rectangle paramRectangle, int paramInt, String paramString1, boolean paramBoolean1, boolean paramBoolean2, String paramString2)
  {
    context = paramSynthContext1;
    accContext = paramSynthContext2;
    style = paramSynthContext1.getStyle();
    accStyle = paramSynthContext2.getStyle();
    gu = style.getGraphicsUtils(paramSynthContext1);
    accGu = accStyle.getGraphicsUtils(paramSynthContext2);
    alignAcceleratorText = getAlignAcceleratorText(paramString2);
    reset(paramJMenuItem, paramIcon1, paramIcon2, paramRectangle, paramInt, paramString1, paramBoolean1, style.getFont(paramSynthContext1), accStyle.getFont(paramSynthContext2), paramBoolean2, paramString2);
    setLeadingGap(0);
  }
  
  private boolean getAlignAcceleratorText(String paramString)
  {
    return style.getBoolean(context, paramString + ".alignAcceleratorText", true);
  }
  
  protected void calcWidthsAndHeights()
  {
    if (getIcon() != null)
    {
      getIconSize().setWidth(SynthIcon.getIconWidth(getIcon(), context));
      getIconSize().setHeight(SynthIcon.getIconHeight(getIcon(), context));
    }
    if (!getAccText().equals(""))
    {
      getAccSize().setWidth(accGu.computeStringWidth(getAccContext(), getAccFontMetrics().getFont(), getAccFontMetrics(), getAccText()));
      getAccSize().setHeight(getAccFontMetrics().getHeight());
    }
    if (getText() == null) {
      setText("");
    } else if (!getText().equals("")) {
      if (getHtmlView() != null)
      {
        getTextSize().setWidth((int)getHtmlView().getPreferredSpan(0));
        getTextSize().setHeight((int)getHtmlView().getPreferredSpan(1));
      }
      else
      {
        getTextSize().setWidth(gu.computeStringWidth(context, getFontMetrics().getFont(), getFontMetrics(), getText()));
        getTextSize().setHeight(getFontMetrics().getHeight());
      }
    }
    if (useCheckAndArrow())
    {
      if (getCheckIcon() != null)
      {
        getCheckSize().setWidth(SynthIcon.getIconWidth(getCheckIcon(), context));
        getCheckSize().setHeight(SynthIcon.getIconHeight(getCheckIcon(), context));
      }
      if (getArrowIcon() != null)
      {
        getArrowSize().setWidth(SynthIcon.getIconWidth(getArrowIcon(), context));
        getArrowSize().setHeight(SynthIcon.getIconHeight(getArrowIcon(), context));
      }
    }
    if (isColumnLayout())
    {
      getLabelSize().setWidth(getIconSize().getWidth() + getTextSize().getWidth() + getGap());
      getLabelSize().setHeight(MenuItemLayoutHelper.max(new int[] { getCheckSize().getHeight(), getIconSize().getHeight(), getTextSize().getHeight(), getAccSize().getHeight(), getArrowSize().getHeight() }));
    }
    else
    {
      Rectangle localRectangle1 = new Rectangle();
      Rectangle localRectangle2 = new Rectangle();
      gu.layoutText(context, getFontMetrics(), getText(), getIcon(), getHorizontalAlignment(), getVerticalAlignment(), getHorizontalTextPosition(), getVerticalTextPosition(), getViewRect(), localRectangle2, localRectangle1, getGap());
      width += getLeftTextExtraWidth();
      Rectangle localRectangle3 = localRectangle2.union(localRectangle1);
      getLabelSize().setHeight(height);
      getLabelSize().setWidth(width);
    }
  }
  
  protected void calcMaxWidths()
  {
    calcMaxWidth(getCheckSize(), MAX_CHECK_WIDTH);
    maxAccOrArrowWidth = calcMaxValue(MAX_ACC_OR_ARROW_WIDTH, getArrowSize().getWidth());
    maxAccOrArrowWidth = calcMaxValue(MAX_ACC_OR_ARROW_WIDTH, getAccSize().getWidth());
    int i;
    if (isColumnLayout())
    {
      calcMaxWidth(getIconSize(), MAX_ICON_WIDTH);
      calcMaxWidth(getTextSize(), MAX_TEXT_WIDTH);
      i = getGap();
      if ((getIconSize().getMaxWidth() == 0) || (getTextSize().getMaxWidth() == 0)) {
        i = 0;
      }
      getLabelSize().setMaxWidth(calcMaxValue(MAX_LABEL_WIDTH, getIconSize().getMaxWidth() + getTextSize().getMaxWidth() + i));
    }
    else
    {
      getIconSize().setMaxWidth(getParentIntProperty(MAX_ICON_WIDTH));
      calcMaxWidth(getLabelSize(), MAX_LABEL_WIDTH);
      i = getLabelSize().getMaxWidth() - getIconSize().getMaxWidth();
      if (getIconSize().getMaxWidth() > 0) {
        i -= getGap();
      }
      getTextSize().setMaxWidth(calcMaxValue(MAX_TEXT_WIDTH, i));
    }
  }
  
  public SynthContext getContext()
  {
    return context;
  }
  
  public SynthContext getAccContext()
  {
    return accContext;
  }
  
  public SynthStyle getStyle()
  {
    return style;
  }
  
  public SynthStyle getAccStyle()
  {
    return accStyle;
  }
  
  public SynthGraphicsUtils getGraphicsUtils()
  {
    return gu;
  }
  
  public SynthGraphicsUtils getAccGraphicsUtils()
  {
    return accGu;
  }
  
  public boolean alignAcceleratorText()
  {
    return alignAcceleratorText;
  }
  
  public int getMaxAccOrArrowWidth()
  {
    return maxAccOrArrowWidth;
  }
  
  protected void prepareForLayout(MenuItemLayoutHelper.LayoutResult paramLayoutResult)
  {
    getCheckRectwidth = getCheckSize().getMaxWidth();
    if ((useCheckAndArrow()) && (!"".equals(getAccText()))) {
      getAccRectwidth = maxAccOrArrowWidth;
    } else {
      getArrowRectwidth = maxAccOrArrowWidth;
    }
  }
  
  public MenuItemLayoutHelper.ColumnAlignment getLTRColumnAlignment()
  {
    if (alignAcceleratorText()) {
      return LTR_ALIGNMENT_2;
    }
    return LTR_ALIGNMENT_1;
  }
  
  public MenuItemLayoutHelper.ColumnAlignment getRTLColumnAlignment()
  {
    if (alignAcceleratorText()) {
      return RTL_ALIGNMENT_2;
    }
    return RTL_ALIGNMENT_1;
  }
  
  protected void layoutIconAndTextInLabelRect(MenuItemLayoutHelper.LayoutResult paramLayoutResult)
  {
    paramLayoutResult.setTextRect(new Rectangle());
    paramLayoutResult.setIconRect(new Rectangle());
    gu.layoutText(context, getFontMetrics(), getText(), getIcon(), getHorizontalAlignment(), getVerticalAlignment(), getHorizontalTextPosition(), getVerticalTextPosition(), paramLayoutResult.getLabelRect(), paramLayoutResult.getIconRect(), paramLayoutResult.getTextRect(), getGap());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthMenuItemLayoutHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
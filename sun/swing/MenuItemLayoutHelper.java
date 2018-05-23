package sun.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.View;

public class MenuItemLayoutHelper
{
  public static final StringUIClientPropertyKey MAX_ARROW_WIDTH = new StringUIClientPropertyKey("maxArrowWidth");
  public static final StringUIClientPropertyKey MAX_CHECK_WIDTH = new StringUIClientPropertyKey("maxCheckWidth");
  public static final StringUIClientPropertyKey MAX_ICON_WIDTH = new StringUIClientPropertyKey("maxIconWidth");
  public static final StringUIClientPropertyKey MAX_TEXT_WIDTH = new StringUIClientPropertyKey("maxTextWidth");
  public static final StringUIClientPropertyKey MAX_ACC_WIDTH = new StringUIClientPropertyKey("maxAccWidth");
  public static final StringUIClientPropertyKey MAX_LABEL_WIDTH = new StringUIClientPropertyKey("maxLabelWidth");
  private JMenuItem mi;
  private JComponent miParent;
  private Font font;
  private Font accFont;
  private FontMetrics fm;
  private FontMetrics accFm;
  private Icon icon;
  private Icon checkIcon;
  private Icon arrowIcon;
  private String text;
  private String accText;
  private boolean isColumnLayout;
  private boolean useCheckAndArrow;
  private boolean isLeftToRight;
  private boolean isTopLevelMenu;
  private View htmlView;
  private int verticalAlignment;
  private int horizontalAlignment;
  private int verticalTextPosition;
  private int horizontalTextPosition;
  private int gap;
  private int leadingGap;
  private int afterCheckIconGap;
  private int minTextOffset;
  private int leftTextExtraWidth;
  private Rectangle viewRect;
  private RectSize iconSize;
  private RectSize textSize;
  private RectSize accSize;
  private RectSize checkSize;
  private RectSize arrowSize;
  private RectSize labelSize;
  
  protected MenuItemLayoutHelper() {}
  
  public MenuItemLayoutHelper(JMenuItem paramJMenuItem, Icon paramIcon1, Icon paramIcon2, Rectangle paramRectangle, int paramInt, String paramString1, boolean paramBoolean1, Font paramFont1, Font paramFont2, boolean paramBoolean2, String paramString2)
  {
    reset(paramJMenuItem, paramIcon1, paramIcon2, paramRectangle, paramInt, paramString1, paramBoolean1, paramFont1, paramFont2, paramBoolean2, paramString2);
  }
  
  protected void reset(JMenuItem paramJMenuItem, Icon paramIcon1, Icon paramIcon2, Rectangle paramRectangle, int paramInt, String paramString1, boolean paramBoolean1, Font paramFont1, Font paramFont2, boolean paramBoolean2, String paramString2)
  {
    mi = paramJMenuItem;
    miParent = getMenuItemParent(paramJMenuItem);
    accText = getAccText(paramString1);
    verticalAlignment = paramJMenuItem.getVerticalAlignment();
    horizontalAlignment = paramJMenuItem.getHorizontalAlignment();
    verticalTextPosition = paramJMenuItem.getVerticalTextPosition();
    horizontalTextPosition = paramJMenuItem.getHorizontalTextPosition();
    useCheckAndArrow = paramBoolean2;
    font = paramFont1;
    accFont = paramFont2;
    fm = paramJMenuItem.getFontMetrics(paramFont1);
    accFm = paramJMenuItem.getFontMetrics(paramFont2);
    isLeftToRight = paramBoolean1;
    isColumnLayout = isColumnLayout(paramBoolean1, horizontalAlignment, horizontalTextPosition, verticalTextPosition);
    isTopLevelMenu = (miParent == null);
    checkIcon = paramIcon1;
    icon = getIcon(paramString2);
    arrowIcon = paramIcon2;
    text = paramJMenuItem.getText();
    gap = paramInt;
    afterCheckIconGap = getAfterCheckIconGap(paramString2);
    minTextOffset = getMinTextOffset(paramString2);
    htmlView = ((View)paramJMenuItem.getClientProperty("html"));
    viewRect = paramRectangle;
    iconSize = new RectSize();
    textSize = new RectSize();
    accSize = new RectSize();
    checkSize = new RectSize();
    arrowSize = new RectSize();
    labelSize = new RectSize();
    calcExtraWidths();
    calcWidthsAndHeights();
    setOriginalWidths();
    calcMaxWidths();
    leadingGap = getLeadingGap(paramString2);
    calcMaxTextOffset(paramRectangle);
  }
  
  private void calcExtraWidths()
  {
    leftTextExtraWidth = getLeftExtraWidth(text);
  }
  
  private int getLeftExtraWidth(String paramString)
  {
    int i = SwingUtilities2.getLeftSideBearing(mi, fm, paramString);
    if (i < 0) {
      return -i;
    }
    return 0;
  }
  
  private void setOriginalWidths()
  {
    iconSize.origWidth = iconSize.width;
    textSize.origWidth = textSize.width;
    accSize.origWidth = accSize.width;
    checkSize.origWidth = checkSize.width;
    arrowSize.origWidth = arrowSize.width;
  }
  
  private String getAccText(String paramString)
  {
    String str = "";
    KeyStroke localKeyStroke = mi.getAccelerator();
    if (localKeyStroke != null)
    {
      int i = localKeyStroke.getModifiers();
      if (i > 0)
      {
        str = KeyEvent.getKeyModifiersText(i);
        str = str + paramString;
      }
      int j = localKeyStroke.getKeyCode();
      if (j != 0) {
        str = str + KeyEvent.getKeyText(j);
      } else {
        str = str + localKeyStroke.getKeyChar();
      }
    }
    return str;
  }
  
  private Icon getIcon(String paramString)
  {
    Icon localIcon = null;
    MenuItemCheckIconFactory localMenuItemCheckIconFactory = (MenuItemCheckIconFactory)UIManager.get(paramString + ".checkIconFactory");
    if ((!isColumnLayout) || (!useCheckAndArrow) || (localMenuItemCheckIconFactory == null) || (!localMenuItemCheckIconFactory.isCompatible(checkIcon, paramString))) {
      localIcon = mi.getIcon();
    }
    return localIcon;
  }
  
  private int getMinTextOffset(String paramString)
  {
    int i = 0;
    Object localObject = UIManager.get(paramString + ".minimumTextOffset");
    if ((localObject instanceof Integer)) {
      i = ((Integer)localObject).intValue();
    }
    return i;
  }
  
  private int getAfterCheckIconGap(String paramString)
  {
    int i = gap;
    Object localObject = UIManager.get(paramString + ".afterCheckIconGap");
    if ((localObject instanceof Integer)) {
      i = ((Integer)localObject).intValue();
    }
    return i;
  }
  
  private int getLeadingGap(String paramString)
  {
    if (checkSize.getMaxWidth() > 0) {
      return getCheckOffset(paramString);
    }
    return gap;
  }
  
  private int getCheckOffset(String paramString)
  {
    int i = gap;
    Object localObject = UIManager.get(paramString + ".checkIconOffset");
    if ((localObject instanceof Integer)) {
      i = ((Integer)localObject).intValue();
    }
    return i;
  }
  
  protected void calcWidthsAndHeights()
  {
    if (icon != null)
    {
      iconSize.width = icon.getIconWidth();
      iconSize.height = icon.getIconHeight();
    }
    if (!accText.equals(""))
    {
      accSize.width = SwingUtilities2.stringWidth(mi, accFm, accText);
      accSize.height = accFm.getHeight();
    }
    if (text == null) {
      text = "";
    } else if (!text.equals("")) {
      if (htmlView != null)
      {
        textSize.width = ((int)htmlView.getPreferredSpan(0));
        textSize.height = ((int)htmlView.getPreferredSpan(1));
      }
      else
      {
        textSize.width = SwingUtilities2.stringWidth(mi, fm, text);
        textSize.height = fm.getHeight();
      }
    }
    if (useCheckAndArrow)
    {
      if (checkIcon != null)
      {
        checkSize.width = checkIcon.getIconWidth();
        checkSize.height = checkIcon.getIconHeight();
      }
      if (arrowIcon != null)
      {
        arrowSize.width = arrowIcon.getIconWidth();
        arrowSize.height = arrowIcon.getIconHeight();
      }
    }
    if (isColumnLayout)
    {
      labelSize.width = (iconSize.width + textSize.width + gap);
      labelSize.height = max(new int[] { checkSize.height, iconSize.height, textSize.height, accSize.height, arrowSize.height });
    }
    else
    {
      Rectangle localRectangle1 = new Rectangle();
      Rectangle localRectangle2 = new Rectangle();
      SwingUtilities.layoutCompoundLabel(mi, fm, text, icon, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition, viewRect, localRectangle2, localRectangle1, gap);
      width += leftTextExtraWidth;
      Rectangle localRectangle3 = localRectangle2.union(localRectangle1);
      labelSize.height = height;
      labelSize.width = width;
    }
  }
  
  protected void calcMaxWidths()
  {
    calcMaxWidth(checkSize, MAX_CHECK_WIDTH);
    calcMaxWidth(arrowSize, MAX_ARROW_WIDTH);
    calcMaxWidth(accSize, MAX_ACC_WIDTH);
    int i;
    if (isColumnLayout)
    {
      calcMaxWidth(iconSize, MAX_ICON_WIDTH);
      calcMaxWidth(textSize, MAX_TEXT_WIDTH);
      i = gap;
      if ((iconSize.getMaxWidth() == 0) || (textSize.getMaxWidth() == 0)) {
        i = 0;
      }
      labelSize.maxWidth = calcMaxValue(MAX_LABEL_WIDTH, iconSize.maxWidth + textSize.maxWidth + i);
    }
    else
    {
      iconSize.maxWidth = getParentIntProperty(MAX_ICON_WIDTH);
      calcMaxWidth(labelSize, MAX_LABEL_WIDTH);
      i = labelSize.maxWidth - iconSize.maxWidth;
      if (iconSize.maxWidth > 0) {
        i -= gap;
      }
      textSize.maxWidth = calcMaxValue(MAX_TEXT_WIDTH, i);
    }
  }
  
  protected void calcMaxWidth(RectSize paramRectSize, Object paramObject)
  {
    maxWidth = calcMaxValue(paramObject, width);
  }
  
  protected int calcMaxValue(Object paramObject, int paramInt)
  {
    int i = getParentIntProperty(paramObject);
    if (paramInt > i)
    {
      if (miParent != null) {
        miParent.putClientProperty(paramObject, Integer.valueOf(paramInt));
      }
      return paramInt;
    }
    return i;
  }
  
  protected int getParentIntProperty(Object paramObject)
  {
    Object localObject = null;
    if (miParent != null) {
      localObject = miParent.getClientProperty(paramObject);
    }
    if ((localObject == null) || (!(localObject instanceof Integer))) {
      localObject = Integer.valueOf(0);
    }
    return ((Integer)localObject).intValue();
  }
  
  public static boolean isColumnLayout(boolean paramBoolean, JMenuItem paramJMenuItem)
  {
    assert (paramJMenuItem != null);
    return isColumnLayout(paramBoolean, paramJMenuItem.getHorizontalAlignment(), paramJMenuItem.getHorizontalTextPosition(), paramJMenuItem.getVerticalTextPosition());
  }
  
  public static boolean isColumnLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt3 != 0) {
      return false;
    }
    if (paramBoolean)
    {
      if ((paramInt1 != 10) && (paramInt1 != 2)) {
        return false;
      }
      if ((paramInt2 != 11) && (paramInt2 != 4)) {
        return false;
      }
    }
    else
    {
      if ((paramInt1 != 10) && (paramInt1 != 4)) {
        return false;
      }
      if ((paramInt2 != 11) && (paramInt2 != 2)) {
        return false;
      }
    }
    return true;
  }
  
  private void calcMaxTextOffset(Rectangle paramRectangle)
  {
    if ((!isColumnLayout) || (!isLeftToRight)) {
      return;
    }
    int i = x + leadingGap + checkSize.maxWidth + afterCheckIconGap + iconSize.maxWidth + gap;
    if (checkSize.maxWidth == 0) {
      i -= afterCheckIconGap;
    }
    if (iconSize.maxWidth == 0) {
      i -= gap;
    }
    if (i < minTextOffset) {
      i = minTextOffset;
    }
    calcMaxValue(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET, i);
  }
  
  public LayoutResult layoutMenuItem()
  {
    LayoutResult localLayoutResult = createLayoutResult();
    prepareForLayout(localLayoutResult);
    if (isColumnLayout())
    {
      if (isLeftToRight()) {
        doLTRColumnLayout(localLayoutResult, getLTRColumnAlignment());
      } else {
        doRTLColumnLayout(localLayoutResult, getRTLColumnAlignment());
      }
    }
    else if (isLeftToRight()) {
      doLTRComplexLayout(localLayoutResult, getLTRColumnAlignment());
    } else {
      doRTLComplexLayout(localLayoutResult, getRTLColumnAlignment());
    }
    alignAccCheckAndArrowVertically(localLayoutResult);
    return localLayoutResult;
  }
  
  private LayoutResult createLayoutResult()
  {
    return new LayoutResult(new Rectangle(iconSize.width, iconSize.height), new Rectangle(textSize.width, textSize.height), new Rectangle(accSize.width, accSize.height), new Rectangle(checkSize.width, checkSize.height), new Rectangle(arrowSize.width, arrowSize.height), new Rectangle(labelSize.width, labelSize.height));
  }
  
  public ColumnAlignment getLTRColumnAlignment()
  {
    return ColumnAlignment.LEFT_ALIGNMENT;
  }
  
  public ColumnAlignment getRTLColumnAlignment()
  {
    return ColumnAlignment.RIGHT_ALIGNMENT;
  }
  
  protected void prepareForLayout(LayoutResult paramLayoutResult)
  {
    checkRect.width = checkSize.maxWidth;
    accRect.width = accSize.maxWidth;
    arrowRect.width = arrowSize.maxWidth;
  }
  
  private void alignAccCheckAndArrowVertically(LayoutResult paramLayoutResult)
  {
    accRect.y = ((int)(labelRect.y + labelRect.height / 2.0F - accRect.height / 2.0F));
    fixVerticalAlignment(paramLayoutResult, accRect);
    if (useCheckAndArrow)
    {
      arrowRect.y = ((int)(labelRect.y + labelRect.height / 2.0F - arrowRect.height / 2.0F));
      checkRect.y = ((int)(labelRect.y + labelRect.height / 2.0F - checkRect.height / 2.0F));
      fixVerticalAlignment(paramLayoutResult, arrowRect);
      fixVerticalAlignment(paramLayoutResult, checkRect);
    }
  }
  
  private void fixVerticalAlignment(LayoutResult paramLayoutResult, Rectangle paramRectangle)
  {
    int i = 0;
    if (y < viewRect.y) {
      i = viewRect.y - y;
    } else if (y + height > viewRect.y + viewRect.height) {
      i = viewRect.y + viewRect.height - y - height;
    }
    if (i != 0)
    {
      checkRect.y += i;
      iconRect.y += i;
      textRect.y += i;
      accRect.y += i;
      arrowRect.y += i;
      labelRect.y += i;
    }
  }
  
  private void doLTRColumnLayout(LayoutResult paramLayoutResult, ColumnAlignment paramColumnAlignment)
  {
    iconRect.width = iconSize.maxWidth;
    textRect.width = textSize.maxWidth;
    calcXPositionsLTR(viewRect.x, leadingGap, gap, new Rectangle[] { checkRect, iconRect, textRect });
    if (checkRect.width > 0)
    {
      iconRect.x += afterCheckIconGap - gap;
      textRect.x += afterCheckIconGap - gap;
    }
    calcXPositionsRTL(viewRect.x + viewRect.width, leadingGap, gap, new Rectangle[] { arrowRect, accRect });
    int i = textRect.x - viewRect.x;
    if ((!isTopLevelMenu) && (i < minTextOffset)) {
      textRect.x += minTextOffset - i;
    }
    alignRects(paramLayoutResult, paramColumnAlignment);
    calcTextAndIconYPositions(paramLayoutResult);
    paramLayoutResult.setLabelRect(textRect.union(iconRect));
  }
  
  private void doLTRComplexLayout(LayoutResult paramLayoutResult, ColumnAlignment paramColumnAlignment)
  {
    labelRect.width = labelSize.maxWidth;
    calcXPositionsLTR(viewRect.x, leadingGap, gap, new Rectangle[] { checkRect, labelRect });
    if (checkRect.width > 0) {
      labelRect.x += afterCheckIconGap - gap;
    }
    calcXPositionsRTL(viewRect.x + viewRect.width, leadingGap, gap, new Rectangle[] { arrowRect, accRect });
    int i = labelRect.x - viewRect.x;
    if ((!isTopLevelMenu) && (i < minTextOffset)) {
      labelRect.x += minTextOffset - i;
    }
    alignRects(paramLayoutResult, paramColumnAlignment);
    calcLabelYPosition(paramLayoutResult);
    layoutIconAndTextInLabelRect(paramLayoutResult);
  }
  
  private void doRTLColumnLayout(LayoutResult paramLayoutResult, ColumnAlignment paramColumnAlignment)
  {
    iconRect.width = iconSize.maxWidth;
    textRect.width = textSize.maxWidth;
    calcXPositionsRTL(viewRect.x + viewRect.width, leadingGap, gap, new Rectangle[] { checkRect, iconRect, textRect });
    if (checkRect.width > 0)
    {
      iconRect.x -= afterCheckIconGap - gap;
      textRect.x -= afterCheckIconGap - gap;
    }
    calcXPositionsLTR(viewRect.x, leadingGap, gap, new Rectangle[] { arrowRect, accRect });
    int i = viewRect.x + viewRect.width - (textRect.x + textRect.width);
    if ((!isTopLevelMenu) && (i < minTextOffset)) {
      textRect.x -= minTextOffset - i;
    }
    alignRects(paramLayoutResult, paramColumnAlignment);
    calcTextAndIconYPositions(paramLayoutResult);
    paramLayoutResult.setLabelRect(textRect.union(iconRect));
  }
  
  private void doRTLComplexLayout(LayoutResult paramLayoutResult, ColumnAlignment paramColumnAlignment)
  {
    labelRect.width = labelSize.maxWidth;
    calcXPositionsRTL(viewRect.x + viewRect.width, leadingGap, gap, new Rectangle[] { checkRect, labelRect });
    if (checkRect.width > 0) {
      labelRect.x -= afterCheckIconGap - gap;
    }
    calcXPositionsLTR(viewRect.x, leadingGap, gap, new Rectangle[] { arrowRect, accRect });
    int i = viewRect.x + viewRect.width - (labelRect.x + labelRect.width);
    if ((!isTopLevelMenu) && (i < minTextOffset)) {
      labelRect.x -= minTextOffset - i;
    }
    alignRects(paramLayoutResult, paramColumnAlignment);
    calcLabelYPosition(paramLayoutResult);
    layoutIconAndTextInLabelRect(paramLayoutResult);
  }
  
  private void alignRects(LayoutResult paramLayoutResult, ColumnAlignment paramColumnAlignment)
  {
    alignRect(checkRect, paramColumnAlignment.getCheckAlignment(), checkSize.getOrigWidth());
    alignRect(iconRect, paramColumnAlignment.getIconAlignment(), iconSize.getOrigWidth());
    alignRect(textRect, paramColumnAlignment.getTextAlignment(), textSize.getOrigWidth());
    alignRect(accRect, paramColumnAlignment.getAccAlignment(), accSize.getOrigWidth());
    alignRect(arrowRect, paramColumnAlignment.getArrowAlignment(), arrowSize.getOrigWidth());
  }
  
  private void alignRect(Rectangle paramRectangle, int paramInt1, int paramInt2)
  {
    if (paramInt1 == 4) {
      x = (x + width - paramInt2);
    }
    width = paramInt2;
  }
  
  protected void layoutIconAndTextInLabelRect(LayoutResult paramLayoutResult)
  {
    paramLayoutResult.setTextRect(new Rectangle());
    paramLayoutResult.setIconRect(new Rectangle());
    SwingUtilities.layoutCompoundLabel(mi, fm, text, icon, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition, labelRect, iconRect, textRect, gap);
  }
  
  private void calcXPositionsLTR(int paramInt1, int paramInt2, int paramInt3, Rectangle... paramVarArgs)
  {
    int i = paramInt1 + paramInt2;
    for (Rectangle localRectangle : paramVarArgs)
    {
      x = i;
      if (width > 0) {
        i += width + paramInt3;
      }
    }
  }
  
  private void calcXPositionsRTL(int paramInt1, int paramInt2, int paramInt3, Rectangle... paramVarArgs)
  {
    int i = paramInt1 - paramInt2;
    for (Rectangle localRectangle : paramVarArgs)
    {
      x = (i - width);
      if (width > 0) {
        i -= width + paramInt3;
      }
    }
  }
  
  private void calcTextAndIconYPositions(LayoutResult paramLayoutResult)
  {
    if (verticalAlignment == 1)
    {
      textRect.y = ((int)(viewRect.y + labelRect.height / 2.0F - textRect.height / 2.0F));
      iconRect.y = ((int)(viewRect.y + labelRect.height / 2.0F - iconRect.height / 2.0F));
    }
    else if (verticalAlignment == 0)
    {
      textRect.y = ((int)(viewRect.y + viewRect.height / 2.0F - textRect.height / 2.0F));
      iconRect.y = ((int)(viewRect.y + viewRect.height / 2.0F - iconRect.height / 2.0F));
    }
    else if (verticalAlignment == 3)
    {
      textRect.y = ((int)(viewRect.y + viewRect.height - labelRect.height / 2.0F - textRect.height / 2.0F));
      iconRect.y = ((int)(viewRect.y + viewRect.height - labelRect.height / 2.0F - iconRect.height / 2.0F));
    }
  }
  
  private void calcLabelYPosition(LayoutResult paramLayoutResult)
  {
    if (verticalAlignment == 1) {
      labelRect.y = viewRect.y;
    } else if (verticalAlignment == 0) {
      labelRect.y = ((int)(viewRect.y + viewRect.height / 2.0F - labelRect.height / 2.0F));
    } else if (verticalAlignment == 3) {
      labelRect.y = (viewRect.y + viewRect.height - labelRect.height);
    }
  }
  
  public static JComponent getMenuItemParent(JMenuItem paramJMenuItem)
  {
    Container localContainer = paramJMenuItem.getParent();
    if (((localContainer instanceof JComponent)) && ((!(paramJMenuItem instanceof JMenu)) || (!((JMenu)paramJMenuItem).isTopLevelMenu()))) {
      return (JComponent)localContainer;
    }
    return null;
  }
  
  public static void clearUsedParentClientProperties(JMenuItem paramJMenuItem)
  {
    clearUsedClientProperties(getMenuItemParent(paramJMenuItem));
  }
  
  public static void clearUsedClientProperties(JComponent paramJComponent)
  {
    if (paramJComponent != null)
    {
      paramJComponent.putClientProperty(MAX_ARROW_WIDTH, null);
      paramJComponent.putClientProperty(MAX_CHECK_WIDTH, null);
      paramJComponent.putClientProperty(MAX_ACC_WIDTH, null);
      paramJComponent.putClientProperty(MAX_TEXT_WIDTH, null);
      paramJComponent.putClientProperty(MAX_ICON_WIDTH, null);
      paramJComponent.putClientProperty(MAX_LABEL_WIDTH, null);
      paramJComponent.putClientProperty(SwingUtilities2.BASICMENUITEMUI_MAX_TEXT_OFFSET, null);
    }
  }
  
  public static int max(int... paramVarArgs)
  {
    int i = Integer.MIN_VALUE;
    for (int m : paramVarArgs) {
      if (m > i) {
        i = m;
      }
    }
    return i;
  }
  
  public static Rectangle createMaxRect()
  {
    return new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
  
  public static void addMaxWidth(RectSize paramRectSize, int paramInt, Dimension paramDimension)
  {
    if (maxWidth > 0) {
      width += maxWidth + paramInt;
    }
  }
  
  public static void addWidth(int paramInt1, int paramInt2, Dimension paramDimension)
  {
    if (paramInt1 > 0) {
      width += paramInt1 + paramInt2;
    }
  }
  
  public JMenuItem getMenuItem()
  {
    return mi;
  }
  
  public JComponent getMenuItemParent()
  {
    return miParent;
  }
  
  public Font getFont()
  {
    return font;
  }
  
  public Font getAccFont()
  {
    return accFont;
  }
  
  public FontMetrics getFontMetrics()
  {
    return fm;
  }
  
  public FontMetrics getAccFontMetrics()
  {
    return accFm;
  }
  
  public Icon getIcon()
  {
    return icon;
  }
  
  public Icon getCheckIcon()
  {
    return checkIcon;
  }
  
  public Icon getArrowIcon()
  {
    return arrowIcon;
  }
  
  public String getText()
  {
    return text;
  }
  
  public String getAccText()
  {
    return accText;
  }
  
  public boolean isColumnLayout()
  {
    return isColumnLayout;
  }
  
  public boolean useCheckAndArrow()
  {
    return useCheckAndArrow;
  }
  
  public boolean isLeftToRight()
  {
    return isLeftToRight;
  }
  
  public boolean isTopLevelMenu()
  {
    return isTopLevelMenu;
  }
  
  public View getHtmlView()
  {
    return htmlView;
  }
  
  public int getVerticalAlignment()
  {
    return verticalAlignment;
  }
  
  public int getHorizontalAlignment()
  {
    return horizontalAlignment;
  }
  
  public int getVerticalTextPosition()
  {
    return verticalTextPosition;
  }
  
  public int getHorizontalTextPosition()
  {
    return horizontalTextPosition;
  }
  
  public int getGap()
  {
    return gap;
  }
  
  public int getLeadingGap()
  {
    return leadingGap;
  }
  
  public int getAfterCheckIconGap()
  {
    return afterCheckIconGap;
  }
  
  public int getMinTextOffset()
  {
    return minTextOffset;
  }
  
  public Rectangle getViewRect()
  {
    return viewRect;
  }
  
  public RectSize getIconSize()
  {
    return iconSize;
  }
  
  public RectSize getTextSize()
  {
    return textSize;
  }
  
  public RectSize getAccSize()
  {
    return accSize;
  }
  
  public RectSize getCheckSize()
  {
    return checkSize;
  }
  
  public RectSize getArrowSize()
  {
    return arrowSize;
  }
  
  public RectSize getLabelSize()
  {
    return labelSize;
  }
  
  protected void setMenuItem(JMenuItem paramJMenuItem)
  {
    mi = paramJMenuItem;
  }
  
  protected void setMenuItemParent(JComponent paramJComponent)
  {
    miParent = paramJComponent;
  }
  
  protected void setFont(Font paramFont)
  {
    font = paramFont;
  }
  
  protected void setAccFont(Font paramFont)
  {
    accFont = paramFont;
  }
  
  protected void setFontMetrics(FontMetrics paramFontMetrics)
  {
    fm = paramFontMetrics;
  }
  
  protected void setAccFontMetrics(FontMetrics paramFontMetrics)
  {
    accFm = paramFontMetrics;
  }
  
  protected void setIcon(Icon paramIcon)
  {
    icon = paramIcon;
  }
  
  protected void setCheckIcon(Icon paramIcon)
  {
    checkIcon = paramIcon;
  }
  
  protected void setArrowIcon(Icon paramIcon)
  {
    arrowIcon = paramIcon;
  }
  
  protected void setText(String paramString)
  {
    text = paramString;
  }
  
  protected void setAccText(String paramString)
  {
    accText = paramString;
  }
  
  protected void setColumnLayout(boolean paramBoolean)
  {
    isColumnLayout = paramBoolean;
  }
  
  protected void setUseCheckAndArrow(boolean paramBoolean)
  {
    useCheckAndArrow = paramBoolean;
  }
  
  protected void setLeftToRight(boolean paramBoolean)
  {
    isLeftToRight = paramBoolean;
  }
  
  protected void setTopLevelMenu(boolean paramBoolean)
  {
    isTopLevelMenu = paramBoolean;
  }
  
  protected void setHtmlView(View paramView)
  {
    htmlView = paramView;
  }
  
  protected void setVerticalAlignment(int paramInt)
  {
    verticalAlignment = paramInt;
  }
  
  protected void setHorizontalAlignment(int paramInt)
  {
    horizontalAlignment = paramInt;
  }
  
  protected void setVerticalTextPosition(int paramInt)
  {
    verticalTextPosition = paramInt;
  }
  
  protected void setHorizontalTextPosition(int paramInt)
  {
    horizontalTextPosition = paramInt;
  }
  
  protected void setGap(int paramInt)
  {
    gap = paramInt;
  }
  
  protected void setLeadingGap(int paramInt)
  {
    leadingGap = paramInt;
  }
  
  protected void setAfterCheckIconGap(int paramInt)
  {
    afterCheckIconGap = paramInt;
  }
  
  protected void setMinTextOffset(int paramInt)
  {
    minTextOffset = paramInt;
  }
  
  protected void setViewRect(Rectangle paramRectangle)
  {
    viewRect = paramRectangle;
  }
  
  protected void setIconSize(RectSize paramRectSize)
  {
    iconSize = paramRectSize;
  }
  
  protected void setTextSize(RectSize paramRectSize)
  {
    textSize = paramRectSize;
  }
  
  protected void setAccSize(RectSize paramRectSize)
  {
    accSize = paramRectSize;
  }
  
  protected void setCheckSize(RectSize paramRectSize)
  {
    checkSize = paramRectSize;
  }
  
  protected void setArrowSize(RectSize paramRectSize)
  {
    arrowSize = paramRectSize;
  }
  
  protected void setLabelSize(RectSize paramRectSize)
  {
    labelSize = paramRectSize;
  }
  
  public int getLeftTextExtraWidth()
  {
    return leftTextExtraWidth;
  }
  
  public static boolean useCheckAndArrow(JMenuItem paramJMenuItem)
  {
    boolean bool = true;
    if (((paramJMenuItem instanceof JMenu)) && (((JMenu)paramJMenuItem).isTopLevelMenu())) {
      bool = false;
    }
    return bool;
  }
  
  public static class ColumnAlignment
  {
    private int checkAlignment;
    private int iconAlignment;
    private int textAlignment;
    private int accAlignment;
    private int arrowAlignment;
    public static final ColumnAlignment LEFT_ALIGNMENT = new ColumnAlignment(2, 2, 2, 2, 2);
    public static final ColumnAlignment RIGHT_ALIGNMENT = new ColumnAlignment(4, 4, 4, 4, 4);
    
    public ColumnAlignment(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    {
      checkAlignment = paramInt1;
      iconAlignment = paramInt2;
      textAlignment = paramInt3;
      accAlignment = paramInt4;
      arrowAlignment = paramInt5;
    }
    
    public int getCheckAlignment()
    {
      return checkAlignment;
    }
    
    public int getIconAlignment()
    {
      return iconAlignment;
    }
    
    public int getTextAlignment()
    {
      return textAlignment;
    }
    
    public int getAccAlignment()
    {
      return accAlignment;
    }
    
    public int getArrowAlignment()
    {
      return arrowAlignment;
    }
  }
  
  public static class LayoutResult
  {
    private Rectangle iconRect;
    private Rectangle textRect;
    private Rectangle accRect;
    private Rectangle checkRect;
    private Rectangle arrowRect;
    private Rectangle labelRect;
    
    public LayoutResult()
    {
      iconRect = new Rectangle();
      textRect = new Rectangle();
      accRect = new Rectangle();
      checkRect = new Rectangle();
      arrowRect = new Rectangle();
      labelRect = new Rectangle();
    }
    
    public LayoutResult(Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, Rectangle paramRectangle4, Rectangle paramRectangle5, Rectangle paramRectangle6)
    {
      iconRect = paramRectangle1;
      textRect = paramRectangle2;
      accRect = paramRectangle3;
      checkRect = paramRectangle4;
      arrowRect = paramRectangle5;
      labelRect = paramRectangle6;
    }
    
    public Rectangle getIconRect()
    {
      return iconRect;
    }
    
    public void setIconRect(Rectangle paramRectangle)
    {
      iconRect = paramRectangle;
    }
    
    public Rectangle getTextRect()
    {
      return textRect;
    }
    
    public void setTextRect(Rectangle paramRectangle)
    {
      textRect = paramRectangle;
    }
    
    public Rectangle getAccRect()
    {
      return accRect;
    }
    
    public void setAccRect(Rectangle paramRectangle)
    {
      accRect = paramRectangle;
    }
    
    public Rectangle getCheckRect()
    {
      return checkRect;
    }
    
    public void setCheckRect(Rectangle paramRectangle)
    {
      checkRect = paramRectangle;
    }
    
    public Rectangle getArrowRect()
    {
      return arrowRect;
    }
    
    public void setArrowRect(Rectangle paramRectangle)
    {
      arrowRect = paramRectangle;
    }
    
    public Rectangle getLabelRect()
    {
      return labelRect;
    }
    
    public void setLabelRect(Rectangle paramRectangle)
    {
      labelRect = paramRectangle;
    }
    
    public Map<String, Rectangle> getAllRects()
    {
      HashMap localHashMap = new HashMap();
      localHashMap.put("checkRect", checkRect);
      localHashMap.put("iconRect", iconRect);
      localHashMap.put("textRect", textRect);
      localHashMap.put("accRect", accRect);
      localHashMap.put("arrowRect", arrowRect);
      localHashMap.put("labelRect", labelRect);
      return localHashMap;
    }
  }
  
  public static class RectSize
  {
    private int width;
    private int height;
    private int origWidth;
    private int maxWidth;
    
    public RectSize() {}
    
    public RectSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      width = paramInt1;
      height = paramInt2;
      origWidth = paramInt3;
      maxWidth = paramInt4;
    }
    
    public int getWidth()
    {
      return width;
    }
    
    public int getHeight()
    {
      return height;
    }
    
    public int getOrigWidth()
    {
      return origWidth;
    }
    
    public int getMaxWidth()
    {
      return maxWidth;
    }
    
    public void setWidth(int paramInt)
    {
      width = paramInt;
    }
    
    public void setHeight(int paramInt)
    {
      height = paramInt;
    }
    
    public void setOrigWidth(int paramInt)
    {
      origWidth = paramInt;
    }
    
    public void setMaxWidth(int paramInt)
    {
      maxWidth = paramInt;
    }
    
    public String toString()
    {
      return "[w=" + width + ",h=" + height + ",ow=" + origWidth + ",mw=" + maxWidth + "]";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\MenuItemLayoutHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
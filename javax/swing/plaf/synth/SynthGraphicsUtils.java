package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.text.View;
import sun.swing.MenuItemLayoutHelper;
import sun.swing.MenuItemLayoutHelper.LayoutResult;
import sun.swing.MenuItemLayoutHelper.RectSize;
import sun.swing.SwingUtilities2;
import sun.swing.plaf.synth.SynthIcon;

public class SynthGraphicsUtils
{
  private Rectangle paintIconR = new Rectangle();
  private Rectangle paintTextR = new Rectangle();
  private Rectangle paintViewR = new Rectangle();
  private Insets paintInsets = new Insets(0, 0, 0, 0);
  private Rectangle iconR = new Rectangle();
  private Rectangle textR = new Rectangle();
  private Rectangle viewR = new Rectangle();
  private Insets viewSizingInsets = new Insets(0, 0, 0, 0);
  
  public SynthGraphicsUtils() {}
  
  public void drawLine(SynthContext paramSynthContext, Object paramObject, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGraphics.drawLine(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void drawLine(SynthContext paramSynthContext, Object paramObject1, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject2)
  {
    if ("dashed".equals(paramObject2))
    {
      int i;
      if (paramInt1 == paramInt3)
      {
        paramInt2 += paramInt2 % 2;
        for (i = paramInt2; i <= paramInt4; i += 2) {
          paramGraphics.drawLine(paramInt1, i, paramInt3, i);
        }
      }
      else if (paramInt2 == paramInt4)
      {
        paramInt1 += paramInt1 % 2;
        for (i = paramInt1; i <= paramInt3; i += 2) {
          paramGraphics.drawLine(i, paramInt2, i, paramInt4);
        }
      }
    }
    else
    {
      drawLine(paramSynthContext, paramObject1, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public String layoutText(SynthContext paramSynthContext, FontMetrics paramFontMetrics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3, int paramInt5)
  {
    if ((paramIcon instanceof SynthIcon))
    {
      SynthIconWrapper localSynthIconWrapper = SynthIconWrapper.get((SynthIcon)paramIcon, paramSynthContext);
      String str = SwingUtilities.layoutCompoundLabel(paramSynthContext.getComponent(), paramFontMetrics, paramString, localSynthIconWrapper, paramInt2, paramInt1, paramInt4, paramInt3, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
      SynthIconWrapper.release(localSynthIconWrapper);
      return str;
    }
    return SwingUtilities.layoutCompoundLabel(paramSynthContext.getComponent(), paramFontMetrics, paramString, paramIcon, paramInt2, paramInt1, paramInt4, paramInt3, paramRectangle1, paramRectangle2, paramRectangle3, paramInt5);
  }
  
  public int computeStringWidth(SynthContext paramSynthContext, Font paramFont, FontMetrics paramFontMetrics, String paramString)
  {
    return SwingUtilities2.stringWidth(paramSynthContext.getComponent(), paramFontMetrics, paramString);
  }
  
  public Dimension getMinimumSize(SynthContext paramSynthContext, Font paramFont, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    JComponent localJComponent = paramSynthContext.getComponent();
    Dimension localDimension = getPreferredSize(paramSynthContext, paramFont, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    View localView = (View)localJComponent.getClientProperty("html");
    if (localView != null)
    {
      Dimension tmp48_46 = localDimension;
      4846width = ((int)(4846width - (localView.getPreferredSpan(0) - localView.getMinimumSpan(0))));
    }
    return localDimension;
  }
  
  public Dimension getMaximumSize(SynthContext paramSynthContext, Font paramFont, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    JComponent localJComponent = paramSynthContext.getComponent();
    Dimension localDimension = getPreferredSize(paramSynthContext, paramFont, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
    View localView = (View)localJComponent.getClientProperty("html");
    if (localView != null)
    {
      Dimension tmp48_46 = localDimension;
      4846width = ((int)(4846width + (localView.getMaximumSpan(0) - localView.getPreferredSpan(0))));
    }
    return localDimension;
  }
  
  public int getMaximumCharHeight(SynthContext paramSynthContext)
  {
    FontMetrics localFontMetrics = paramSynthContext.getComponent().getFontMetrics(paramSynthContext.getStyle().getFont(paramSynthContext));
    return localFontMetrics.getAscent() + localFontMetrics.getDescent();
  }
  
  public Dimension getPreferredSize(SynthContext paramSynthContext, Font paramFont, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    JComponent localJComponent = paramSynthContext.getComponent();
    Insets localInsets = localJComponent.getInsets(viewSizingInsets);
    int i = left + right;
    int j = top + bottom;
    if ((paramIcon == null) && ((paramString == null) || (paramFont == null))) {
      return new Dimension(i, j);
    }
    if ((paramString == null) || ((paramIcon != null) && (paramFont == null))) {
      return new Dimension(SynthIcon.getIconWidth(paramIcon, paramSynthContext) + i, SynthIcon.getIconHeight(paramIcon, paramSynthContext) + j);
    }
    FontMetrics localFontMetrics = localJComponent.getFontMetrics(paramFont);
    iconR.x = (iconR.y = iconR.width = iconR.height = 0);
    textR.x = (textR.y = textR.width = textR.height = 0);
    viewR.x = i;
    viewR.y = j;
    viewR.width = (viewR.height = '翿');
    layoutText(paramSynthContext, localFontMetrics, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, viewR, iconR, textR, paramInt5);
    int k = Math.min(iconR.x, textR.x);
    int m = Math.max(iconR.x + iconR.width, textR.x + textR.width);
    int n = Math.min(iconR.y, textR.y);
    int i1 = Math.max(iconR.y + iconR.height, textR.y + textR.height);
    Dimension localDimension = new Dimension(m - k, i1 - n);
    width += i;
    height += j;
    return localDimension;
  }
  
  public void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString, Rectangle paramRectangle, int paramInt)
  {
    paintText(paramSynthContext, paramGraphics, paramString, x, y, paramInt);
  }
  
  public void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramString != null)
    {
      JComponent localJComponent = paramSynthContext.getComponent();
      FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(localJComponent, paramGraphics);
      paramInt2 += localFontMetrics.getAscent();
      SwingUtilities2.drawStringUnderlineCharAt(localJComponent, paramGraphics, paramString, paramInt3, paramInt1, paramInt2);
    }
  }
  
  public void paintText(SynthContext paramSynthContext, Graphics paramGraphics, String paramString, Icon paramIcon, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    if ((paramIcon == null) && (paramString == null)) {
      return;
    }
    JComponent localJComponent = paramSynthContext.getComponent();
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(localJComponent, paramGraphics);
    Insets localInsets = SynthLookAndFeel.getPaintingInsets(paramSynthContext, paintInsets);
    paintViewR.x = left;
    paintViewR.y = top;
    paintViewR.width = (localJComponent.getWidth() - (left + right));
    paintViewR.height = (localJComponent.getHeight() - (top + bottom));
    paintIconR.x = (paintIconR.y = paintIconR.width = paintIconR.height = 0);
    paintTextR.x = (paintTextR.y = paintTextR.width = paintTextR.height = 0);
    String str = layoutText(paramSynthContext, localFontMetrics, paramString, paramIcon, paramInt1, paramInt2, paramInt3, paramInt4, paintViewR, paintIconR, paintTextR, paramInt5);
    Object localObject;
    if (paramIcon != null)
    {
      localObject = paramGraphics.getColor();
      if ((paramSynthContext.getStyle().getBoolean(paramSynthContext, "TableHeader.alignSorterArrow", false)) && ("TableHeader.renderer".equals(localJComponent.getName()))) {
        paintIconR.x = (paintViewR.width - paintIconR.width);
      } else {
        paintIconR.x += paramInt7;
      }
      paintIconR.y += paramInt7;
      SynthIcon.paintIcon(paramIcon, paramSynthContext, paramGraphics, paintIconR.x, paintIconR.y, paintIconR.width, paintIconR.height);
      paramGraphics.setColor((Color)localObject);
    }
    if (paramString != null)
    {
      localObject = (View)localJComponent.getClientProperty("html");
      if (localObject != null)
      {
        ((View)localObject).paint(paramGraphics, paintTextR);
      }
      else
      {
        paintTextR.x += paramInt7;
        paintTextR.y += paramInt7;
        paintText(paramSynthContext, paramGraphics, str, paintTextR, paramInt6);
      }
    }
  }
  
  static Dimension getPreferredMenuItemSize(SynthContext paramSynthContext1, SynthContext paramSynthContext2, JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, int paramInt, String paramString1, boolean paramBoolean, String paramString2)
  {
    JMenuItem localJMenuItem = (JMenuItem)paramJComponent;
    SynthMenuItemLayoutHelper localSynthMenuItemLayoutHelper = new SynthMenuItemLayoutHelper(paramSynthContext1, paramSynthContext2, localJMenuItem, paramIcon1, paramIcon2, MenuItemLayoutHelper.createMaxRect(), paramInt, paramString1, SynthLookAndFeel.isLeftToRight(localJMenuItem), paramBoolean, paramString2);
    Dimension localDimension = new Dimension();
    int i = localSynthMenuItemLayoutHelper.getGap();
    width = 0;
    MenuItemLayoutHelper.addMaxWidth(localSynthMenuItemLayoutHelper.getCheckSize(), i, localDimension);
    MenuItemLayoutHelper.addMaxWidth(localSynthMenuItemLayoutHelper.getLabelSize(), i, localDimension);
    MenuItemLayoutHelper.addWidth(localSynthMenuItemLayoutHelper.getMaxAccOrArrowWidth(), 5 * i, localDimension);
    width -= i;
    height = MenuItemLayoutHelper.max(new int[] { localSynthMenuItemLayoutHelper.getCheckSize().getHeight(), localSynthMenuItemLayoutHelper.getLabelSize().getHeight(), localSynthMenuItemLayoutHelper.getAccSize().getHeight(), localSynthMenuItemLayoutHelper.getArrowSize().getHeight() });
    Insets localInsets = localSynthMenuItemLayoutHelper.getMenuItem().getInsets();
    if (localInsets != null)
    {
      width += left + right;
      height += top + bottom;
    }
    if (width % 2 == 0) {
      width += 1;
    }
    if (height % 2 == 0) {
      height += 1;
    }
    return localDimension;
  }
  
  static void applyInsets(Rectangle paramRectangle, Insets paramInsets, boolean paramBoolean)
  {
    if (paramInsets != null)
    {
      x += (paramBoolean ? left : right);
      y += top;
      width -= (paramBoolean ? right : left) + x;
      height -= bottom + y;
    }
  }
  
  static void paint(SynthContext paramSynthContext1, SynthContext paramSynthContext2, Graphics paramGraphics, Icon paramIcon1, Icon paramIcon2, String paramString1, int paramInt, String paramString2)
  {
    JMenuItem localJMenuItem = (JMenuItem)paramSynthContext1.getComponent();
    SynthStyle localSynthStyle = paramSynthContext1.getStyle();
    paramGraphics.setFont(localSynthStyle.getFont(paramSynthContext1));
    Rectangle localRectangle = new Rectangle(0, 0, localJMenuItem.getWidth(), localJMenuItem.getHeight());
    boolean bool = SynthLookAndFeel.isLeftToRight(localJMenuItem);
    applyInsets(localRectangle, localJMenuItem.getInsets(), bool);
    SynthMenuItemLayoutHelper localSynthMenuItemLayoutHelper = new SynthMenuItemLayoutHelper(paramSynthContext1, paramSynthContext2, localJMenuItem, paramIcon1, paramIcon2, localRectangle, paramInt, paramString1, bool, MenuItemLayoutHelper.useCheckAndArrow(localJMenuItem), paramString2);
    MenuItemLayoutHelper.LayoutResult localLayoutResult = localSynthMenuItemLayoutHelper.layoutMenuItem();
    paintMenuItem(paramGraphics, localSynthMenuItemLayoutHelper, localLayoutResult);
  }
  
  static void paintMenuItem(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult)
  {
    Font localFont = paramGraphics.getFont();
    Color localColor = paramGraphics.getColor();
    paintCheckIcon(paramGraphics, paramSynthMenuItemLayoutHelper, paramLayoutResult);
    paintIcon(paramGraphics, paramSynthMenuItemLayoutHelper, paramLayoutResult);
    paintText(paramGraphics, paramSynthMenuItemLayoutHelper, paramLayoutResult);
    paintAccText(paramGraphics, paramSynthMenuItemLayoutHelper, paramLayoutResult);
    paintArrowIcon(paramGraphics, paramSynthMenuItemLayoutHelper, paramLayoutResult);
    paramGraphics.setColor(localColor);
    paramGraphics.setFont(localFont);
  }
  
  static void paintBackground(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper)
  {
    paintBackground(paramSynthMenuItemLayoutHelper.getContext(), paramGraphics, paramSynthMenuItemLayoutHelper.getMenuItem());
  }
  
  static void paintBackground(SynthContext paramSynthContext, Graphics paramGraphics, JComponent paramJComponent)
  {
    paramSynthContext.getPainter().paintMenuItemBackground(paramSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
  }
  
  static void paintIcon(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult)
  {
    if (paramSynthMenuItemLayoutHelper.getIcon() != null)
    {
      JMenuItem localJMenuItem = paramSynthMenuItemLayoutHelper.getMenuItem();
      ButtonModel localButtonModel = localJMenuItem.getModel();
      Icon localIcon;
      if (!localButtonModel.isEnabled())
      {
        localIcon = localJMenuItem.getDisabledIcon();
      }
      else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
      {
        localIcon = localJMenuItem.getPressedIcon();
        if (localIcon == null) {
          localIcon = localJMenuItem.getIcon();
        }
      }
      else
      {
        localIcon = localJMenuItem.getIcon();
      }
      if (localIcon != null)
      {
        Rectangle localRectangle = paramLayoutResult.getIconRect();
        SynthIcon.paintIcon(localIcon, paramSynthMenuItemLayoutHelper.getContext(), paramGraphics, x, y, width, height);
      }
    }
  }
  
  static void paintCheckIcon(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult)
  {
    if (paramSynthMenuItemLayoutHelper.getCheckIcon() != null)
    {
      Rectangle localRectangle = paramLayoutResult.getCheckRect();
      SynthIcon.paintIcon(paramSynthMenuItemLayoutHelper.getCheckIcon(), paramSynthMenuItemLayoutHelper.getContext(), paramGraphics, x, y, width, height);
    }
  }
  
  static void paintAccText(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult)
  {
    String str = paramSynthMenuItemLayoutHelper.getAccText();
    if ((str != null) && (!str.equals("")))
    {
      paramGraphics.setColor(paramSynthMenuItemLayoutHelper.getAccStyle().getColor(paramSynthMenuItemLayoutHelper.getAccContext(), ColorType.TEXT_FOREGROUND));
      paramGraphics.setFont(paramSynthMenuItemLayoutHelper.getAccStyle().getFont(paramSynthMenuItemLayoutHelper.getAccContext()));
      paramSynthMenuItemLayoutHelper.getAccGraphicsUtils().paintText(paramSynthMenuItemLayoutHelper.getAccContext(), paramGraphics, str, getAccRectx, getAccRecty, -1);
    }
  }
  
  static void paintText(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult)
  {
    if (!paramSynthMenuItemLayoutHelper.getText().equals("")) {
      if (paramSynthMenuItemLayoutHelper.getHtmlView() != null)
      {
        paramSynthMenuItemLayoutHelper.getHtmlView().paint(paramGraphics, paramLayoutResult.getTextRect());
      }
      else
      {
        paramGraphics.setColor(paramSynthMenuItemLayoutHelper.getStyle().getColor(paramSynthMenuItemLayoutHelper.getContext(), ColorType.TEXT_FOREGROUND));
        paramGraphics.setFont(paramSynthMenuItemLayoutHelper.getStyle().getFont(paramSynthMenuItemLayoutHelper.getContext()));
        paramSynthMenuItemLayoutHelper.getGraphicsUtils().paintText(paramSynthMenuItemLayoutHelper.getContext(), paramGraphics, paramSynthMenuItemLayoutHelper.getText(), getTextRectx, getTextRecty, paramSynthMenuItemLayoutHelper.getMenuItem().getDisplayedMnemonicIndex());
      }
    }
  }
  
  static void paintArrowIcon(Graphics paramGraphics, SynthMenuItemLayoutHelper paramSynthMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult)
  {
    if (paramSynthMenuItemLayoutHelper.getArrowIcon() != null)
    {
      Rectangle localRectangle = paramLayoutResult.getArrowRect();
      SynthIcon.paintIcon(paramSynthMenuItemLayoutHelper.getArrowIcon(), paramSynthMenuItemLayoutHelper.getContext(), paramGraphics, x, y, width, height);
    }
  }
  
  private static class SynthIconWrapper
    implements Icon
  {
    private static final List<SynthIconWrapper> CACHE = new ArrayList(1);
    private SynthIcon synthIcon;
    private SynthContext context;
    
    static SynthIconWrapper get(SynthIcon paramSynthIcon, SynthContext paramSynthContext)
    {
      synchronized (CACHE)
      {
        int i = CACHE.size();
        if (i > 0)
        {
          SynthIconWrapper localSynthIconWrapper = (SynthIconWrapper)CACHE.remove(i - 1);
          localSynthIconWrapper.reset(paramSynthIcon, paramSynthContext);
          return localSynthIconWrapper;
        }
      }
      return new SynthIconWrapper(paramSynthIcon, paramSynthContext);
    }
    
    static void release(SynthIconWrapper paramSynthIconWrapper)
    {
      paramSynthIconWrapper.reset(null, null);
      synchronized (CACHE)
      {
        CACHE.add(paramSynthIconWrapper);
      }
    }
    
    SynthIconWrapper(SynthIcon paramSynthIcon, SynthContext paramSynthContext)
    {
      reset(paramSynthIcon, paramSynthContext);
    }
    
    void reset(SynthIcon paramSynthIcon, SynthContext paramSynthContext)
    {
      synthIcon = paramSynthIcon;
      context = paramSynthContext;
    }
    
    public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {}
    
    public int getIconWidth()
    {
      return synthIcon.getIconWidth(context);
    }
    
    public int getIconHeight()
    {
      return synthIcon.getIconHeight(context);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthGraphicsUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
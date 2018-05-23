package javax.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Float;
import java.beans.ConstructorProperties;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class TitledBorder
  extends AbstractBorder
{
  protected String title;
  protected Border border;
  protected int titlePosition;
  protected int titleJustification;
  protected Font titleFont;
  protected Color titleColor;
  private final JLabel label;
  public static final int DEFAULT_POSITION = 0;
  public static final int ABOVE_TOP = 1;
  public static final int TOP = 2;
  public static final int BELOW_TOP = 3;
  public static final int ABOVE_BOTTOM = 4;
  public static final int BOTTOM = 5;
  public static final int BELOW_BOTTOM = 6;
  public static final int DEFAULT_JUSTIFICATION = 0;
  public static final int LEFT = 1;
  public static final int CENTER = 2;
  public static final int RIGHT = 3;
  public static final int LEADING = 4;
  public static final int TRAILING = 5;
  protected static final int EDGE_SPACING = 2;
  protected static final int TEXT_SPACING = 2;
  protected static final int TEXT_INSET_H = 5;
  
  public TitledBorder(String paramString)
  {
    this(null, paramString, 4, 0, null, null);
  }
  
  public TitledBorder(Border paramBorder)
  {
    this(paramBorder, "", 4, 0, null, null);
  }
  
  public TitledBorder(Border paramBorder, String paramString)
  {
    this(paramBorder, paramString, 4, 0, null, null);
  }
  
  public TitledBorder(Border paramBorder, String paramString, int paramInt1, int paramInt2)
  {
    this(paramBorder, paramString, paramInt1, paramInt2, null, null);
  }
  
  public TitledBorder(Border paramBorder, String paramString, int paramInt1, int paramInt2, Font paramFont)
  {
    this(paramBorder, paramString, paramInt1, paramInt2, paramFont, null);
  }
  
  @ConstructorProperties({"border", "title", "titleJustification", "titlePosition", "titleFont", "titleColor"})
  public TitledBorder(Border paramBorder, String paramString, int paramInt1, int paramInt2, Font paramFont, Color paramColor)
  {
    title = paramString;
    border = paramBorder;
    titleFont = paramFont;
    titleColor = paramColor;
    setTitleJustification(paramInt1);
    setTitlePosition(paramInt2);
    label = new JLabel();
    label.setOpaque(false);
    label.putClientProperty("html", null);
  }
  
  public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Border localBorder = getBorder();
    String str = getTitle();
    if ((str != null) && (!str.isEmpty()))
    {
      int i = (localBorder instanceof TitledBorder) ? 0 : 2;
      JLabel localJLabel = getLabel(paramComponent);
      Dimension localDimension = localJLabel.getPreferredSize();
      Insets localInsets = getBorderInsets(localBorder, paramComponent, new Insets(0, 0, 0, 0));
      int j = paramInt1 + i;
      int k = paramInt2 + i;
      int m = paramInt3 - i - i;
      int n = paramInt4 - i - i;
      int i1 = paramInt2;
      int i2 = height;
      int i3 = getPosition();
      switch (i3)
      {
      case 1: 
        left = 0;
        right = 0;
        k += i2 - i;
        n -= i2 - i;
        break;
      case 2: 
        top = (i + top / 2 - i2 / 2);
        if (top < i)
        {
          k -= top;
          n += top;
        }
        else
        {
          i1 += top;
        }
        break;
      case 3: 
        i1 += top + i;
        break;
      case 4: 
        i1 += paramInt4 - i2 - bottom - i;
        break;
      case 5: 
        i1 += paramInt4 - i2;
        bottom = (i + (bottom - i2) / 2);
        if (bottom < i) {
          n += bottom;
        } else {
          i1 -= bottom;
        }
        break;
      case 6: 
        left = 0;
        right = 0;
        i1 += paramInt4 - i2;
        n -= i2 - i;
      }
      left += i + 5;
      right += i + 5;
      int i4 = paramInt1;
      int i5 = paramInt3 - left - right;
      if (i5 > width) {
        i5 = width;
      }
      switch (getJustification(paramComponent))
      {
      case 1: 
        i4 += left;
        break;
      case 3: 
        i4 += paramInt3 - right - i5;
        break;
      case 2: 
        i4 += (paramInt3 - i5) / 2;
      }
      if (localBorder != null) {
        if ((i3 != 2) && (i3 != 5))
        {
          localBorder.paintBorder(paramComponent, paramGraphics, j, k, m, n);
        }
        else
        {
          Graphics localGraphics = paramGraphics.create();
          if ((localGraphics instanceof Graphics2D))
          {
            Graphics2D localGraphics2D = (Graphics2D)localGraphics;
            Path2D.Float localFloat = new Path2D.Float();
            localFloat.append(new Rectangle(j, k, m, i1 - k), false);
            localFloat.append(new Rectangle(j, i1, i4 - j - 2, i2), false);
            localFloat.append(new Rectangle(i4 + i5 + 2, i1, j - i4 + m - i5 - 2, i2), false);
            localFloat.append(new Rectangle(j, i1 + i2, m, k - i1 + n - i2), false);
            localGraphics2D.clip(localFloat);
          }
          localBorder.paintBorder(paramComponent, localGraphics, j, k, m, n);
          localGraphics.dispose();
        }
      }
      paramGraphics.translate(i4, i1);
      localJLabel.setSize(i5, i2);
      localJLabel.paint(paramGraphics);
      paramGraphics.translate(-i4, -i1);
    }
    else if (localBorder != null)
    {
      localBorder.paintBorder(paramComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
  {
    Border localBorder = getBorder();
    paramInsets = getBorderInsets(localBorder, paramComponent, paramInsets);
    String str = getTitle();
    if ((str != null) && (!str.isEmpty()))
    {
      int i = (localBorder instanceof TitledBorder) ? 0 : 2;
      JLabel localJLabel = getLabel(paramComponent);
      Dimension localDimension = localJLabel.getPreferredSize();
      switch (getPosition())
      {
      case 1: 
        top += height - i;
        break;
      case 2: 
        if (top < height) {
          top = (height - i);
        }
        break;
      case 3: 
        top += height;
        break;
      case 4: 
        bottom += height;
        break;
      case 5: 
        if (bottom < height) {
          bottom = (height - i);
        }
        break;
      case 6: 
        bottom += height - i;
      }
      top += i + 2;
      left += i + 2;
      right += i + 2;
      bottom += i + 2;
    }
    return paramInsets;
  }
  
  public boolean isBorderOpaque()
  {
    return false;
  }
  
  public String getTitle()
  {
    return title;
  }
  
  public Border getBorder()
  {
    return border != null ? border : UIManager.getBorder("TitledBorder.border");
  }
  
  public int getTitlePosition()
  {
    return titlePosition;
  }
  
  public int getTitleJustification()
  {
    return titleJustification;
  }
  
  public Font getTitleFont()
  {
    return titleFont == null ? UIManager.getFont("TitledBorder.font") : titleFont;
  }
  
  public Color getTitleColor()
  {
    return titleColor == null ? UIManager.getColor("TitledBorder.titleColor") : titleColor;
  }
  
  public void setTitle(String paramString)
  {
    title = paramString;
  }
  
  public void setBorder(Border paramBorder)
  {
    border = paramBorder;
  }
  
  public void setTitlePosition(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
      titlePosition = paramInt;
      break;
    default: 
      throw new IllegalArgumentException(paramInt + " is not a valid title position.");
    }
  }
  
  public void setTitleJustification(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
    case 1: 
    case 2: 
    case 3: 
    case 4: 
    case 5: 
      titleJustification = paramInt;
      break;
    default: 
      throw new IllegalArgumentException(paramInt + " is not a valid title justification.");
    }
  }
  
  public void setTitleFont(Font paramFont)
  {
    titleFont = paramFont;
  }
  
  public void setTitleColor(Color paramColor)
  {
    titleColor = paramColor;
  }
  
  public Dimension getMinimumSize(Component paramComponent)
  {
    Insets localInsets = getBorderInsets(paramComponent);
    Dimension localDimension1 = new Dimension(right + left, top + bottom);
    String str = getTitle();
    if ((str != null) && (!str.isEmpty()))
    {
      JLabel localJLabel = getLabel(paramComponent);
      Dimension localDimension2 = localJLabel.getPreferredSize();
      int i = getPosition();
      if ((i != 1) && (i != 6)) {
        width += width;
      } else if (width < width) {
        width += width;
      }
    }
    return localDimension1;
  }
  
  public int getBaseline(Component paramComponent, int paramInt1, int paramInt2)
  {
    if (paramComponent == null) {
      throw new NullPointerException("Must supply non-null component");
    }
    if (paramInt1 < 0) {
      throw new IllegalArgumentException("Width must be >= 0");
    }
    if (paramInt2 < 0) {
      throw new IllegalArgumentException("Height must be >= 0");
    }
    Border localBorder = getBorder();
    String str = getTitle();
    if ((str != null) && (!str.isEmpty()))
    {
      int i = (localBorder instanceof TitledBorder) ? 0 : 2;
      JLabel localJLabel = getLabel(paramComponent);
      Dimension localDimension = localJLabel.getPreferredSize();
      Insets localInsets = getBorderInsets(localBorder, paramComponent, new Insets(0, 0, 0, 0));
      int j = localJLabel.getBaseline(width, height);
      switch (getPosition())
      {
      case 1: 
        return j;
      case 2: 
        top = (i + (top - height) / 2);
        return top < i ? j : j + top;
      case 3: 
        return j + top + i;
      case 4: 
        return j + paramInt2 - height - bottom - i;
      case 5: 
        bottom = (i + (bottom - height) / 2);
        return bottom < i ? j + paramInt2 - height : j + paramInt2 - height + bottom;
      case 6: 
        return j + paramInt2 - height;
      }
    }
    return -1;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(Component paramComponent)
  {
    super.getBaselineResizeBehavior(paramComponent);
    switch (getPosition())
    {
    case 1: 
    case 2: 
    case 3: 
      return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    case 4: 
    case 5: 
    case 6: 
      return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
    }
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  private int getPosition()
  {
    int i = getTitlePosition();
    if (i != 0) {
      return i;
    }
    Object localObject = UIManager.get("TitledBorder.position");
    if ((localObject instanceof Integer))
    {
      int j = ((Integer)localObject).intValue();
      if ((0 < j) && (j <= 6)) {
        return j;
      }
    }
    else if ((localObject instanceof String))
    {
      String str = (String)localObject;
      if (str.equalsIgnoreCase("ABOVE_TOP")) {
        return 1;
      }
      if (str.equalsIgnoreCase("TOP")) {
        return 2;
      }
      if (str.equalsIgnoreCase("BELOW_TOP")) {
        return 3;
      }
      if (str.equalsIgnoreCase("ABOVE_BOTTOM")) {
        return 4;
      }
      if (str.equalsIgnoreCase("BOTTOM")) {
        return 5;
      }
      if (str.equalsIgnoreCase("BELOW_BOTTOM")) {
        return 6;
      }
    }
    return 2;
  }
  
  private int getJustification(Component paramComponent)
  {
    int i = getTitleJustification();
    if ((i == 4) || (i == 0)) {
      return paramComponent.getComponentOrientation().isLeftToRight() ? 1 : 3;
    }
    if (i == 5) {
      return paramComponent.getComponentOrientation().isLeftToRight() ? 3 : 1;
    }
    return i;
  }
  
  protected Font getFont(Component paramComponent)
  {
    Font localFont = getTitleFont();
    if (localFont != null) {
      return localFont;
    }
    if (paramComponent != null)
    {
      localFont = paramComponent.getFont();
      if (localFont != null) {
        return localFont;
      }
    }
    return new Font("Dialog", 0, 12);
  }
  
  private Color getColor(Component paramComponent)
  {
    Color localColor = getTitleColor();
    if (localColor != null) {
      return localColor;
    }
    return paramComponent != null ? paramComponent.getForeground() : null;
  }
  
  private JLabel getLabel(Component paramComponent)
  {
    label.setText(getTitle());
    label.setFont(getFont(paramComponent));
    label.setForeground(getColor(paramComponent));
    label.setComponentOrientation(paramComponent.getComponentOrientation());
    label.setEnabled(paramComponent.isEnabled());
    return label;
  }
  
  private static Insets getBorderInsets(Border paramBorder, Component paramComponent, Insets paramInsets)
  {
    if (paramBorder == null)
    {
      paramInsets.set(0, 0, 0, 0);
    }
    else
    {
      Object localObject;
      if ((paramBorder instanceof AbstractBorder))
      {
        localObject = (AbstractBorder)paramBorder;
        paramInsets = ((AbstractBorder)localObject).getBorderInsets(paramComponent, paramInsets);
      }
      else
      {
        localObject = paramBorder.getBorderInsets(paramComponent);
        paramInsets.set(top, left, bottom, right);
      }
    }
    return paramInsets;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\border\TitledBorder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
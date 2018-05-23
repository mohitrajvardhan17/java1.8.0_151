package javax.swing.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.JTree.DropLocation;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import sun.swing.DefaultLookup;

public class DefaultTreeCellRenderer
  extends JLabel
  implements TreeCellRenderer
{
  private JTree tree;
  protected boolean selected;
  protected boolean hasFocus;
  private boolean drawsFocusBorderAroundIcon;
  private boolean drawDashedFocusIndicator;
  private Color treeBGColor;
  private Color focusBGColor;
  protected transient Icon closedIcon;
  protected transient Icon leafIcon;
  protected transient Icon openIcon;
  protected Color textSelectionColor;
  protected Color textNonSelectionColor;
  protected Color backgroundSelectionColor;
  protected Color backgroundNonSelectionColor;
  protected Color borderSelectionColor;
  private boolean isDropCell;
  private boolean fillBackground;
  private boolean inited = true;
  
  public DefaultTreeCellRenderer() {}
  
  public void updateUI()
  {
    super.updateUI();
    if ((!inited) || ((getLeafIcon() instanceof UIResource))) {
      setLeafIcon(DefaultLookup.getIcon(this, ui, "Tree.leafIcon"));
    }
    if ((!inited) || ((getClosedIcon() instanceof UIResource))) {
      setClosedIcon(DefaultLookup.getIcon(this, ui, "Tree.closedIcon"));
    }
    if ((!inited) || ((getOpenIcon() instanceof UIManager))) {
      setOpenIcon(DefaultLookup.getIcon(this, ui, "Tree.openIcon"));
    }
    if ((!inited) || ((getTextSelectionColor() instanceof UIResource))) {
      setTextSelectionColor(DefaultLookup.getColor(this, ui, "Tree.selectionForeground"));
    }
    if ((!inited) || ((getTextNonSelectionColor() instanceof UIResource))) {
      setTextNonSelectionColor(DefaultLookup.getColor(this, ui, "Tree.textForeground"));
    }
    if ((!inited) || ((getBackgroundSelectionColor() instanceof UIResource))) {
      setBackgroundSelectionColor(DefaultLookup.getColor(this, ui, "Tree.selectionBackground"));
    }
    if ((!inited) || ((getBackgroundNonSelectionColor() instanceof UIResource))) {
      setBackgroundNonSelectionColor(DefaultLookup.getColor(this, ui, "Tree.textBackground"));
    }
    if ((!inited) || ((getBorderSelectionColor() instanceof UIResource))) {
      setBorderSelectionColor(DefaultLookup.getColor(this, ui, "Tree.selectionBorderColor"));
    }
    drawsFocusBorderAroundIcon = DefaultLookup.getBoolean(this, ui, "Tree.drawsFocusBorderAroundIcon", false);
    drawDashedFocusIndicator = DefaultLookup.getBoolean(this, ui, "Tree.drawDashedFocusIndicator", false);
    fillBackground = DefaultLookup.getBoolean(this, ui, "Tree.rendererFillBackground", true);
    Insets localInsets = DefaultLookup.getInsets(this, ui, "Tree.rendererMargins");
    if (localInsets != null) {
      setBorder(new EmptyBorder(top, left, bottom, right));
    }
    setName("Tree.cellRenderer");
  }
  
  public Icon getDefaultOpenIcon()
  {
    return DefaultLookup.getIcon(this, ui, "Tree.openIcon");
  }
  
  public Icon getDefaultClosedIcon()
  {
    return DefaultLookup.getIcon(this, ui, "Tree.closedIcon");
  }
  
  public Icon getDefaultLeafIcon()
  {
    return DefaultLookup.getIcon(this, ui, "Tree.leafIcon");
  }
  
  public void setOpenIcon(Icon paramIcon)
  {
    openIcon = paramIcon;
  }
  
  public Icon getOpenIcon()
  {
    return openIcon;
  }
  
  public void setClosedIcon(Icon paramIcon)
  {
    closedIcon = paramIcon;
  }
  
  public Icon getClosedIcon()
  {
    return closedIcon;
  }
  
  public void setLeafIcon(Icon paramIcon)
  {
    leafIcon = paramIcon;
  }
  
  public Icon getLeafIcon()
  {
    return leafIcon;
  }
  
  public void setTextSelectionColor(Color paramColor)
  {
    textSelectionColor = paramColor;
  }
  
  public Color getTextSelectionColor()
  {
    return textSelectionColor;
  }
  
  public void setTextNonSelectionColor(Color paramColor)
  {
    textNonSelectionColor = paramColor;
  }
  
  public Color getTextNonSelectionColor()
  {
    return textNonSelectionColor;
  }
  
  public void setBackgroundSelectionColor(Color paramColor)
  {
    backgroundSelectionColor = paramColor;
  }
  
  public Color getBackgroundSelectionColor()
  {
    return backgroundSelectionColor;
  }
  
  public void setBackgroundNonSelectionColor(Color paramColor)
  {
    backgroundNonSelectionColor = paramColor;
  }
  
  public Color getBackgroundNonSelectionColor()
  {
    return backgroundNonSelectionColor;
  }
  
  public void setBorderSelectionColor(Color paramColor)
  {
    borderSelectionColor = paramColor;
  }
  
  public Color getBorderSelectionColor()
  {
    return borderSelectionColor;
  }
  
  public void setFont(Font paramFont)
  {
    if ((paramFont instanceof FontUIResource)) {
      paramFont = null;
    }
    super.setFont(paramFont);
  }
  
  public Font getFont()
  {
    Font localFont = super.getFont();
    if ((localFont == null) && (tree != null)) {
      localFont = tree.getFont();
    }
    return localFont;
  }
  
  public void setBackground(Color paramColor)
  {
    if ((paramColor instanceof ColorUIResource)) {
      paramColor = null;
    }
    super.setBackground(paramColor);
  }
  
  public Component getTreeCellRendererComponent(JTree paramJTree, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4)
  {
    String str = paramJTree.convertValueToText(paramObject, paramBoolean1, paramBoolean2, paramBoolean3, paramInt, paramBoolean4);
    tree = paramJTree;
    hasFocus = paramBoolean4;
    setText(str);
    Object localObject1 = null;
    isDropCell = false;
    JTree.DropLocation localDropLocation = paramJTree.getDropLocation();
    if ((localDropLocation != null) && (localDropLocation.getChildIndex() == -1) && (paramJTree.getRowForPath(localDropLocation.getPath()) == paramInt))
    {
      localObject2 = DefaultLookup.getColor(this, ui, "Tree.dropCellForeground");
      if (localObject2 != null) {
        localObject1 = localObject2;
      } else {
        localObject1 = getTextSelectionColor();
      }
      isDropCell = true;
    }
    else if (paramBoolean1)
    {
      localObject1 = getTextSelectionColor();
    }
    else
    {
      localObject1 = getTextNonSelectionColor();
    }
    setForeground((Color)localObject1);
    Object localObject2 = null;
    if (paramBoolean3) {
      localObject2 = getLeafIcon();
    } else if (paramBoolean2) {
      localObject2 = getOpenIcon();
    } else {
      localObject2 = getClosedIcon();
    }
    if (!paramJTree.isEnabled())
    {
      setEnabled(false);
      LookAndFeel localLookAndFeel = UIManager.getLookAndFeel();
      Icon localIcon = localLookAndFeel.getDisabledIcon(paramJTree, (Icon)localObject2);
      if (localIcon != null) {
        localObject2 = localIcon;
      }
      setDisabledIcon((Icon)localObject2);
    }
    else
    {
      setEnabled(true);
      setIcon((Icon)localObject2);
    }
    setComponentOrientation(paramJTree.getComponentOrientation());
    selected = paramBoolean1;
    return this;
  }
  
  public void paint(Graphics paramGraphics)
  {
    Color localColor;
    if (isDropCell)
    {
      localColor = DefaultLookup.getColor(this, ui, "Tree.dropCellBackground");
      if (localColor == null) {
        localColor = getBackgroundSelectionColor();
      }
    }
    else if (selected)
    {
      localColor = getBackgroundSelectionColor();
    }
    else
    {
      localColor = getBackgroundNonSelectionColor();
      if (localColor == null) {
        localColor = getBackground();
      }
    }
    int i = -1;
    if ((localColor != null) && (fillBackground))
    {
      i = getLabelStart();
      paramGraphics.setColor(localColor);
      if (getComponentOrientation().isLeftToRight()) {
        paramGraphics.fillRect(i, 0, getWidth() - i, getHeight());
      } else {
        paramGraphics.fillRect(0, 0, getWidth() - i, getHeight());
      }
    }
    if (hasFocus)
    {
      if (drawsFocusBorderAroundIcon) {
        i = 0;
      } else if (i == -1) {
        i = getLabelStart();
      }
      if (getComponentOrientation().isLeftToRight()) {
        paintFocus(paramGraphics, i, 0, getWidth() - i, getHeight(), localColor);
      } else {
        paintFocus(paramGraphics, 0, 0, getWidth() - i, getHeight(), localColor);
      }
    }
    super.paint(paramGraphics);
  }
  
  private void paintFocus(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Color paramColor)
  {
    Color localColor = getBorderSelectionColor();
    if ((localColor != null) && ((selected) || (!drawDashedFocusIndicator)))
    {
      paramGraphics.setColor(localColor);
      paramGraphics.drawRect(paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
    }
    if ((drawDashedFocusIndicator) && (paramColor != null))
    {
      if (treeBGColor != paramColor)
      {
        treeBGColor = paramColor;
        focusBGColor = new Color(paramColor.getRGB() ^ 0xFFFFFFFF);
      }
      paramGraphics.setColor(focusBGColor);
      BasicGraphicsUtils.drawDashedRect(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  private int getLabelStart()
  {
    Icon localIcon = getIcon();
    if ((localIcon != null) && (getText() != null)) {
      return localIcon.getIconWidth() + Math.max(0, getIconTextGap() - 1);
    }
    return 0;
  }
  
  public Dimension getPreferredSize()
  {
    Dimension localDimension = super.getPreferredSize();
    if (localDimension != null) {
      localDimension = new Dimension(width + 3, height);
    }
    return localDimension;
  }
  
  public void validate() {}
  
  public void invalidate() {}
  
  public void revalidate() {}
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void repaint(Rectangle paramRectangle) {}
  
  public void repaint() {}
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    if ((paramString == "text") || (((paramString == "font") || (paramString == "foreground")) && (paramObject1 != paramObject2) && (getClientProperty("html") != null))) {
      super.firePropertyChange(paramString, paramObject1, paramObject2);
    }
  }
  
  public void firePropertyChange(String paramString, byte paramByte1, byte paramByte2) {}
  
  public void firePropertyChange(String paramString, char paramChar1, char paramChar2) {}
  
  public void firePropertyChange(String paramString, short paramShort1, short paramShort2) {}
  
  public void firePropertyChange(String paramString, int paramInt1, int paramInt2) {}
  
  public void firePropertyChange(String paramString, long paramLong1, long paramLong2) {}
  
  public void firePropertyChange(String paramString, float paramFloat1, float paramFloat2) {}
  
  public void firePropertyChange(String paramString, double paramDouble1, double paramDouble2) {}
  
  public void firePropertyChange(String paramString, boolean paramBoolean1, boolean paramBoolean2) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\tree\DefaultTreeCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;

public class DefaultListCellRenderer
  extends JLabel
  implements ListCellRenderer<Object>, Serializable
{
  private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
  protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;
  
  public DefaultListCellRenderer()
  {
    setOpaque(true);
    setBorder(getNoFocusBorder());
    setName("List.cellRenderer");
  }
  
  private Border getNoFocusBorder()
  {
    Border localBorder = DefaultLookup.getBorder(this, ui, "List.cellNoFocusBorder");
    if (System.getSecurityManager() != null)
    {
      if (localBorder != null) {
        return localBorder;
      }
      return SAFE_NO_FOCUS_BORDER;
    }
    if ((localBorder != null) && ((noFocusBorder == null) || (noFocusBorder == DEFAULT_NO_FOCUS_BORDER))) {
      return localBorder;
    }
    return noFocusBorder;
  }
  
  public Component getListCellRendererComponent(JList<?> paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    setComponentOrientation(paramJList.getComponentOrientation());
    Color localColor1 = null;
    Color localColor2 = null;
    JList.DropLocation localDropLocation = paramJList.getDropLocation();
    if ((localDropLocation != null) && (!localDropLocation.isInsert()) && (localDropLocation.getIndex() == paramInt))
    {
      localColor1 = DefaultLookup.getColor(this, ui, "List.dropCellBackground");
      localColor2 = DefaultLookup.getColor(this, ui, "List.dropCellForeground");
      paramBoolean1 = true;
    }
    if (paramBoolean1)
    {
      setBackground(localColor1 == null ? paramJList.getSelectionBackground() : localColor1);
      setForeground(localColor2 == null ? paramJList.getSelectionForeground() : localColor2);
    }
    else
    {
      setBackground(paramJList.getBackground());
      setForeground(paramJList.getForeground());
    }
    if ((paramObject instanceof Icon))
    {
      setIcon((Icon)paramObject);
      setText("");
    }
    else
    {
      setIcon(null);
      setText(paramObject == null ? "" : paramObject.toString());
    }
    setEnabled(paramJList.isEnabled());
    setFont(paramJList.getFont());
    Border localBorder = null;
    if (paramBoolean2)
    {
      if (paramBoolean1) {
        localBorder = DefaultLookup.getBorder(this, ui, "List.focusSelectedCellHighlightBorder");
      }
      if (localBorder == null) {
        localBorder = DefaultLookup.getBorder(this, ui, "List.focusCellHighlightBorder");
      }
    }
    else
    {
      localBorder = getNoFocusBorder();
    }
    setBorder(localBorder);
    return this;
  }
  
  public boolean isOpaque()
  {
    Color localColor = getBackground();
    Container localContainer = getParent();
    if (localContainer != null) {
      localContainer = localContainer.getParent();
    }
    int i = (localColor != null) && (localContainer != null) && (localColor.equals(localContainer.getBackground())) && (localContainer.isOpaque()) ? 1 : 0;
    return (i == 0) && (super.isOpaque());
  }
  
  public void validate() {}
  
  public void invalidate() {}
  
  public void repaint() {}
  
  public void revalidate() {}
  
  public void repaint(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void repaint(Rectangle paramRectangle) {}
  
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
  
  public static class UIResource
    extends DefaultListCellRenderer
    implements UIResource
  {
    public UIResource() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DefaultListCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
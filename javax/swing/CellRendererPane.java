package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Container.AccessibleAWTContainer;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class CellRendererPane
  extends Container
  implements Accessible
{
  protected AccessibleContext accessibleContext = null;
  
  public CellRendererPane()
  {
    setLayout(null);
    setVisible(false);
  }
  
  public void invalidate() {}
  
  public void paint(Graphics paramGraphics) {}
  
  public void update(Graphics paramGraphics) {}
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    if (paramComponent.getParent() == this) {
      return;
    }
    super.addImpl(paramComponent, paramObject, paramInt);
  }
  
  public void paintComponent(Graphics paramGraphics, Component paramComponent, Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (paramComponent == null)
    {
      if (paramContainer != null)
      {
        Color localColor = paramGraphics.getColor();
        paramGraphics.setColor(paramContainer.getBackground());
        paramGraphics.fillRect(paramInt1, paramInt2, paramInt3, paramInt4);
        paramGraphics.setColor(localColor);
      }
      return;
    }
    if (paramComponent.getParent() != this) {
      add(paramComponent);
    }
    paramComponent.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramBoolean) {
      paramComponent.validate();
    }
    int i = 0;
    if (((paramComponent instanceof JComponent)) && (((JComponent)paramComponent).isDoubleBuffered()))
    {
      i = 1;
      ((JComponent)paramComponent).setDoubleBuffered(false);
    }
    Graphics localGraphics = paramGraphics.create(paramInt1, paramInt2, paramInt3, paramInt4);
    try
    {
      paramComponent.paint(localGraphics);
    }
    finally
    {
      localGraphics.dispose();
    }
    if ((i != 0) && ((paramComponent instanceof JComponent))) {
      ((JComponent)paramComponent).setDoubleBuffered(true);
    }
    paramComponent.setBounds(-paramInt3, -paramInt4, 0, 0);
  }
  
  public void paintComponent(Graphics paramGraphics, Component paramComponent, Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paintComponent(paramGraphics, paramComponent, paramContainer, paramInt1, paramInt2, paramInt3, paramInt4, false);
  }
  
  public void paintComponent(Graphics paramGraphics, Component paramComponent, Container paramContainer, Rectangle paramRectangle)
  {
    paintComponent(paramGraphics, paramComponent, paramContainer, x, y, width, height);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    removeAll();
    paramObjectOutputStream.defaultWriteObject();
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleCellRendererPane();
    }
    return accessibleContext;
  }
  
  protected class AccessibleCellRendererPane
    extends Container.AccessibleAWTContainer
  {
    protected AccessibleCellRendererPane()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.PANEL;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\CellRendererPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
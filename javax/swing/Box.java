package javax.swing;

import java.awt.AWTError;
import java.awt.Component;
import java.awt.Component.AccessibleAWTComponent;
import java.awt.Container.AccessibleAWTContainer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.beans.ConstructorProperties;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Box
  extends JComponent
  implements Accessible
{
  public Box(int paramInt)
  {
    super.setLayout(new BoxLayout(this, paramInt));
  }
  
  public static Box createHorizontalBox()
  {
    return new Box(0);
  }
  
  public static Box createVerticalBox()
  {
    return new Box(1);
  }
  
  public static Component createRigidArea(Dimension paramDimension)
  {
    return new Filler(paramDimension, paramDimension, paramDimension);
  }
  
  public static Component createHorizontalStrut(int paramInt)
  {
    return new Filler(new Dimension(paramInt, 0), new Dimension(paramInt, 0), new Dimension(paramInt, 32767));
  }
  
  public static Component createVerticalStrut(int paramInt)
  {
    return new Filler(new Dimension(0, paramInt), new Dimension(0, paramInt), new Dimension(32767, paramInt));
  }
  
  public static Component createGlue()
  {
    return new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 32767));
  }
  
  public static Component createHorizontalGlue()
  {
    return new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
  }
  
  public static Component createVerticalGlue()
  {
    return new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
  }
  
  public void setLayout(LayoutManager paramLayoutManager)
  {
    throw new AWTError("Illegal request");
  }
  
  protected void paintComponent(Graphics paramGraphics)
  {
    if (ui != null)
    {
      super.paintComponent(paramGraphics);
    }
    else if (isOpaque())
    {
      paramGraphics.setColor(getBackground());
      paramGraphics.fillRect(0, 0, getWidth(), getHeight());
    }
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleBox();
    }
    return accessibleContext;
  }
  
  protected class AccessibleBox
    extends Container.AccessibleAWTContainer
  {
    protected AccessibleBox()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.FILLER;
    }
  }
  
  public static class Filler
    extends JComponent
    implements Accessible
  {
    @ConstructorProperties({"minimumSize", "preferredSize", "maximumSize"})
    public Filler(Dimension paramDimension1, Dimension paramDimension2, Dimension paramDimension3)
    {
      setMinimumSize(paramDimension1);
      setPreferredSize(paramDimension2);
      setMaximumSize(paramDimension3);
    }
    
    public void changeShape(Dimension paramDimension1, Dimension paramDimension2, Dimension paramDimension3)
    {
      setMinimumSize(paramDimension1);
      setPreferredSize(paramDimension2);
      setMaximumSize(paramDimension3);
      revalidate();
    }
    
    protected void paintComponent(Graphics paramGraphics)
    {
      if (ui != null)
      {
        super.paintComponent(paramGraphics);
      }
      else if (isOpaque())
      {
        paramGraphics.setColor(getBackground());
        paramGraphics.fillRect(0, 0, getWidth(), getHeight());
      }
    }
    
    public AccessibleContext getAccessibleContext()
    {
      if (accessibleContext == null) {
        accessibleContext = new AccessibleBoxFiller();
      }
      return accessibleContext;
    }
    
    protected class AccessibleBoxFiller
      extends Component.AccessibleAWTComponent
    {
      protected AccessibleBoxFiller()
      {
        super();
      }
      
      public AccessibleRole getAccessibleRole()
      {
        return AccessibleRole.FILLER;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\Box.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
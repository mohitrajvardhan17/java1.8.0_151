package javax.swing.plaf.nimbus;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthStyle;
import sun.swing.plaf.synth.SynthIcon;

class NimbusIcon
  extends SynthIcon
{
  private int width;
  private int height;
  private String prefix;
  private String key;
  
  NimbusIcon(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    width = paramInt1;
    height = paramInt2;
    prefix = paramString1;
    key = paramString2;
  }
  
  public void paintIcon(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Painter localPainter = null;
    if (paramSynthContext != null) {
      localPainter = (Painter)paramSynthContext.getStyle().get(paramSynthContext, key);
    }
    if (localPainter == null) {
      localPainter = (Painter)UIManager.get(prefix + "[Enabled]." + key);
    }
    if ((localPainter != null) && (paramSynthContext != null))
    {
      JComponent localJComponent = paramSynthContext.getComponent();
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      Object localObject1;
      Object localObject2;
      if ((localJComponent instanceof JToolBar))
      {
        localObject1 = (JToolBar)localJComponent;
        i = ((JToolBar)localObject1).getOrientation() == 1 ? 1 : 0;
        j = !((JToolBar)localObject1).getComponentOrientation().isLeftToRight() ? 1 : 0;
        localObject2 = NimbusLookAndFeel.resolveToolbarConstraint((JToolBar)localObject1);
        if ((((JToolBar)localObject1).getBorder() instanceof UIResource)) {
          if (localObject2 == "South") {
            m = 1;
          } else if (localObject2 == "East") {
            k = 1;
          }
        }
      }
      else if ((localJComponent instanceof JMenu))
      {
        j = !localJComponent.getComponentOrientation().isLeftToRight() ? 1 : 0;
      }
      if ((paramGraphics instanceof Graphics2D))
      {
        localObject1 = (Graphics2D)paramGraphics;
        ((Graphics2D)localObject1).translate(paramInt1, paramInt2);
        ((Graphics2D)localObject1).translate(k, m);
        if (i != 0)
        {
          ((Graphics2D)localObject1).rotate(Math.toRadians(90.0D));
          ((Graphics2D)localObject1).translate(0, -paramInt3);
          localPainter.paint((Graphics2D)localObject1, paramSynthContext.getComponent(), paramInt4, paramInt3);
          ((Graphics2D)localObject1).translate(0, paramInt3);
          ((Graphics2D)localObject1).rotate(Math.toRadians(-90.0D));
        }
        else if (j != 0)
        {
          ((Graphics2D)localObject1).scale(-1.0D, 1.0D);
          ((Graphics2D)localObject1).translate(-paramInt3, 0);
          localPainter.paint((Graphics2D)localObject1, paramSynthContext.getComponent(), paramInt3, paramInt4);
          ((Graphics2D)localObject1).translate(paramInt3, 0);
          ((Graphics2D)localObject1).scale(-1.0D, 1.0D);
        }
        else
        {
          localPainter.paint((Graphics2D)localObject1, paramSynthContext.getComponent(), paramInt3, paramInt4);
        }
        ((Graphics2D)localObject1).translate(-k, -m);
        ((Graphics2D)localObject1).translate(-paramInt1, -paramInt2);
      }
      else
      {
        localObject1 = new BufferedImage(paramInt3, paramInt4, 2);
        localObject2 = ((BufferedImage)localObject1).createGraphics();
        if (i != 0)
        {
          ((Graphics2D)localObject2).rotate(Math.toRadians(90.0D));
          ((Graphics2D)localObject2).translate(0, -paramInt3);
          localPainter.paint((Graphics2D)localObject2, paramSynthContext.getComponent(), paramInt4, paramInt3);
        }
        else if (j != 0)
        {
          ((Graphics2D)localObject2).scale(-1.0D, 1.0D);
          ((Graphics2D)localObject2).translate(-paramInt3, 0);
          localPainter.paint((Graphics2D)localObject2, paramSynthContext.getComponent(), paramInt3, paramInt4);
        }
        else
        {
          localPainter.paint((Graphics2D)localObject2, paramSynthContext.getComponent(), paramInt3, paramInt4);
        }
        ((Graphics2D)localObject2).dispose();
        paramGraphics.drawImage((Image)localObject1, paramInt1, paramInt2, null);
        localObject1 = null;
      }
    }
  }
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2)
  {
    Painter localPainter = (Painter)UIManager.get(prefix + "[Enabled]." + key);
    if (localPainter != null)
    {
      Object localObject = (paramComponent instanceof JComponent) ? (JComponent)paramComponent : null;
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
      localGraphics2D.translate(paramInt1, paramInt2);
      localPainter.paint(localGraphics2D, localObject, width, height);
      localGraphics2D.translate(-paramInt1, -paramInt2);
    }
  }
  
  public int getIconWidth(SynthContext paramSynthContext)
  {
    if (paramSynthContext == null) {
      return width;
    }
    JComponent localJComponent = paramSynthContext.getComponent();
    if (((localJComponent instanceof JToolBar)) && (((JToolBar)localJComponent).getOrientation() == 1))
    {
      if ((localJComponent.getBorder() instanceof UIResource)) {
        return localJComponent.getWidth() - 1;
      }
      return localJComponent.getWidth();
    }
    return scale(paramSynthContext, width);
  }
  
  public int getIconHeight(SynthContext paramSynthContext)
  {
    if (paramSynthContext == null) {
      return height;
    }
    JComponent localJComponent = paramSynthContext.getComponent();
    if ((localJComponent instanceof JToolBar))
    {
      JToolBar localJToolBar = (JToolBar)localJComponent;
      if (localJToolBar.getOrientation() == 0)
      {
        if ((localJToolBar.getBorder() instanceof UIResource)) {
          return localJComponent.getHeight() - 1;
        }
        return localJComponent.getHeight();
      }
      return scale(paramSynthContext, width);
    }
    return scale(paramSynthContext, height);
  }
  
  private int scale(SynthContext paramSynthContext, int paramInt)
  {
    if ((paramSynthContext == null) || (paramSynthContext.getComponent() == null)) {
      return paramInt;
    }
    String str = (String)paramSynthContext.getComponent().getClientProperty("JComponent.sizeVariant");
    if (str != null) {
      if ("large".equals(str)) {
        paramInt = (int)(paramInt * 1.15D);
      } else if ("small".equals(str)) {
        paramInt = (int)(paramInt * 0.857D);
      } else if ("mini".equals(str)) {
        paramInt = (int)(paramInt * 0.784D);
      }
    }
    return paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\NimbusIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import sun.swing.CachedPainter;
import sun.swing.ImageIconUIResource;

class MetalUtils
{
  MetalUtils() {}
  
  static void drawFlush3DBorder(Graphics paramGraphics, Rectangle paramRectangle)
  {
    drawFlush3DBorder(paramGraphics, x, y, width, height);
  }
  
  static void drawFlush3DBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
    paramGraphics.drawRect(0, 0, paramInt3 - 2, paramInt4 - 2);
    paramGraphics.setColor(MetalLookAndFeel.getControlHighlight());
    paramGraphics.drawRect(1, 1, paramInt3 - 2, paramInt4 - 2);
    paramGraphics.setColor(MetalLookAndFeel.getControl());
    paramGraphics.drawLine(0, paramInt4 - 1, 1, paramInt4 - 2);
    paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 2, 1);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static void drawPressed3DBorder(Graphics paramGraphics, Rectangle paramRectangle)
  {
    drawPressed3DBorder(paramGraphics, x, y, width, height);
  }
  
  static void drawDisabledBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
    paramGraphics.drawRect(0, 0, paramInt3 - 1, paramInt4 - 1);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static void drawPressed3DBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGraphics.translate(paramInt1, paramInt2);
    drawFlush3DBorder(paramGraphics, 0, 0, paramInt3, paramInt4);
    paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
    paramGraphics.drawLine(1, 1, 1, paramInt4 - 2);
    paramGraphics.drawLine(1, 1, paramInt3 - 2, 1);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static void drawDark3DBorder(Graphics paramGraphics, Rectangle paramRectangle)
  {
    drawDark3DBorder(paramGraphics, x, y, width, height);
  }
  
  static void drawDark3DBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramGraphics.translate(paramInt1, paramInt2);
    drawFlush3DBorder(paramGraphics, 0, 0, paramInt3, paramInt4);
    paramGraphics.setColor(MetalLookAndFeel.getControl());
    paramGraphics.drawLine(1, 1, 1, paramInt4 - 2);
    paramGraphics.drawLine(1, 1, paramInt3 - 2, 1);
    paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
    paramGraphics.drawLine(1, paramInt4 - 2, 1, paramInt4 - 2);
    paramGraphics.drawLine(paramInt3 - 2, 1, paramInt3 - 2, 1);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static void drawButtonBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    if (paramBoolean) {
      drawActiveButtonBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      drawFlush3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  static void drawActiveButtonBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    drawFlush3DBorder(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
    paramGraphics.setColor(MetalLookAndFeel.getPrimaryControl());
    paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 1, paramInt1 + 1, paramInt4 - 3);
    paramGraphics.drawLine(paramInt1 + 1, paramInt2 + 1, paramInt3 - 3, paramInt1 + 1);
    paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
    paramGraphics.drawLine(paramInt1 + 2, paramInt4 - 2, paramInt3 - 2, paramInt4 - 2);
    paramGraphics.drawLine(paramInt3 - 2, paramInt2 + 2, paramInt3 - 2, paramInt4 - 2);
  }
  
  static void drawDefaultButtonBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    drawButtonBorder(paramGraphics, paramInt1 + 1, paramInt2 + 1, paramInt3 - 1, paramInt4 - 1, paramBoolean);
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
    paramGraphics.drawRect(0, 0, paramInt3 - 3, paramInt4 - 3);
    paramGraphics.drawLine(paramInt3 - 2, 0, paramInt3 - 2, 0);
    paramGraphics.drawLine(0, paramInt4 - 2, 0, paramInt4 - 2);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static void drawDefaultButtonPressedBorder(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    drawPressed3DBorder(paramGraphics, paramInt1 + 1, paramInt2 + 1, paramInt3 - 1, paramInt4 - 1);
    paramGraphics.translate(paramInt1, paramInt2);
    paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
    paramGraphics.drawRect(0, 0, paramInt3 - 3, paramInt4 - 3);
    paramGraphics.drawLine(paramInt3 - 2, 0, paramInt3 - 2, 0);
    paramGraphics.drawLine(0, paramInt4 - 2, 0, paramInt4 - 2);
    paramGraphics.setColor(MetalLookAndFeel.getControl());
    paramGraphics.drawLine(paramInt3 - 1, 0, paramInt3 - 1, 0);
    paramGraphics.drawLine(0, paramInt4 - 1, 0, paramInt4 - 1);
    paramGraphics.translate(-paramInt1, -paramInt2);
  }
  
  static boolean isLeftToRight(Component paramComponent)
  {
    return paramComponent.getComponentOrientation().isLeftToRight();
  }
  
  static int getInt(Object paramObject, int paramInt)
  {
    Object localObject = UIManager.get(paramObject);
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    if ((localObject instanceof String)) {
      try
      {
        return Integer.parseInt((String)localObject);
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return paramInt;
  }
  
  static boolean drawGradient(Component paramComponent, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
  {
    List localList = (List)UIManager.get(paramString);
    if ((localList == null) || (!(paramGraphics instanceof Graphics2D))) {
      return false;
    }
    if ((paramInt3 <= 0) || (paramInt4 <= 0)) {
      return true;
    }
    GradientPainter.INSTANCE.paint(paramComponent, (Graphics2D)paramGraphics, localList, paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean);
    return true;
  }
  
  static boolean isToolBarButton(JComponent paramJComponent)
  {
    return paramJComponent.getParent() instanceof JToolBar;
  }
  
  static Icon getOceanToolBarIcon(Image paramImage)
  {
    FilteredImageSource localFilteredImageSource = new FilteredImageSource(paramImage.getSource(), new OceanToolBarImageFilter());
    return new ImageIconUIResource(Toolkit.getDefaultToolkit().createImage(localFilteredImageSource));
  }
  
  static Icon getOceanDisabledButtonIcon(Image paramImage)
  {
    Object[] arrayOfObject = (Object[])UIManager.get("Button.disabledGrayRange");
    int i = 180;
    int j = 215;
    if (arrayOfObject != null)
    {
      i = ((Integer)arrayOfObject[0]).intValue();
      j = ((Integer)arrayOfObject[1]).intValue();
    }
    FilteredImageSource localFilteredImageSource = new FilteredImageSource(paramImage.getSource(), new OceanDisabledButtonImageFilter(i, j));
    return new ImageIconUIResource(Toolkit.getDefaultToolkit().createImage(localFilteredImageSource));
  }
  
  private static class GradientPainter
    extends CachedPainter
  {
    public static final GradientPainter INSTANCE = new GradientPainter(8);
    private static final int IMAGE_SIZE = 64;
    private int w;
    private int h;
    
    GradientPainter(int paramInt)
    {
      super();
    }
    
    public void paint(Component paramComponent, Graphics2D paramGraphics2D, List paramList, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      int i;
      int j;
      if (paramBoolean)
      {
        i = 64;
        j = paramInt4;
      }
      else
      {
        i = paramInt3;
        j = 64;
      }
      synchronized (paramComponent.getTreeLock())
      {
        w = paramInt3;
        h = paramInt4;
        paint(paramComponent, paramGraphics2D, paramInt1, paramInt2, i, j, new Object[] { paramList, Boolean.valueOf(paramBoolean) });
      }
    }
    
    protected void paintToImage(Component paramComponent, Image paramImage, Graphics paramGraphics, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    {
      Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
      List localList = (List)paramArrayOfObject[0];
      boolean bool = ((Boolean)paramArrayOfObject[1]).booleanValue();
      if (bool) {
        drawVerticalGradient(localGraphics2D, ((Number)localList.get(0)).floatValue(), ((Number)localList.get(1)).floatValue(), (Color)localList.get(2), (Color)localList.get(3), (Color)localList.get(4), paramInt1, paramInt2);
      } else {
        drawHorizontalGradient(localGraphics2D, ((Number)localList.get(0)).floatValue(), ((Number)localList.get(1)).floatValue(), (Color)localList.get(2), (Color)localList.get(3), (Color)localList.get(4), paramInt1, paramInt2);
      }
    }
    
    protected void paintImage(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Image paramImage, Object[] paramArrayOfObject)
    {
      boolean bool = ((Boolean)paramArrayOfObject[1]).booleanValue();
      paramGraphics.translate(paramInt1, paramInt2);
      int i;
      int j;
      if (bool) {
        for (i = 0; i < w; i += 64)
        {
          j = Math.min(64, w - i);
          paramGraphics.drawImage(paramImage, i, 0, i + j, h, 0, 0, j, h, null);
        }
      } else {
        for (i = 0; i < h; i += 64)
        {
          j = Math.min(64, h - i);
          paramGraphics.drawImage(paramImage, 0, i, w, i + j, 0, 0, w, j, null);
        }
      }
      paramGraphics.translate(-paramInt1, -paramInt2);
    }
    
    private void drawVerticalGradient(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2, Color paramColor1, Color paramColor2, Color paramColor3, int paramInt1, int paramInt2)
    {
      int i = (int)(paramFloat1 * paramInt2);
      int j = (int)(paramFloat2 * paramInt2);
      if (i > 0)
      {
        paramGraphics2D.setPaint(getGradient(0.0F, 0.0F, paramColor1, 0.0F, i, paramColor2));
        paramGraphics2D.fillRect(0, 0, paramInt1, i);
      }
      if (j > 0)
      {
        paramGraphics2D.setColor(paramColor2);
        paramGraphics2D.fillRect(0, i, paramInt1, j);
      }
      if (i > 0)
      {
        paramGraphics2D.setPaint(getGradient(0.0F, i + j, paramColor2, 0.0F, i * 2.0F + j, paramColor1));
        paramGraphics2D.fillRect(0, i + j, paramInt1, i);
      }
      if (paramInt2 - i * 2 - j > 0)
      {
        paramGraphics2D.setPaint(getGradient(0.0F, i * 2.0F + j, paramColor1, 0.0F, paramInt2, paramColor3));
        paramGraphics2D.fillRect(0, i * 2 + j, paramInt1, paramInt2 - i * 2 - j);
      }
    }
    
    private void drawHorizontalGradient(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2, Color paramColor1, Color paramColor2, Color paramColor3, int paramInt1, int paramInt2)
    {
      int i = (int)(paramFloat1 * paramInt1);
      int j = (int)(paramFloat2 * paramInt1);
      if (i > 0)
      {
        paramGraphics2D.setPaint(getGradient(0.0F, 0.0F, paramColor1, i, 0.0F, paramColor2));
        paramGraphics2D.fillRect(0, 0, i, paramInt2);
      }
      if (j > 0)
      {
        paramGraphics2D.setColor(paramColor2);
        paramGraphics2D.fillRect(i, 0, j, paramInt2);
      }
      if (i > 0)
      {
        paramGraphics2D.setPaint(getGradient(i + j, 0.0F, paramColor2, i * 2.0F + j, 0.0F, paramColor1));
        paramGraphics2D.fillRect(i + j, 0, i, paramInt2);
      }
      if (paramInt1 - i * 2 - j > 0)
      {
        paramGraphics2D.setPaint(getGradient(i * 2.0F + j, 0.0F, paramColor1, paramInt1, 0.0F, paramColor3));
        paramGraphics2D.fillRect(i * 2 + j, 0, paramInt1 - i * 2 - j, paramInt2);
      }
    }
    
    private GradientPaint getGradient(float paramFloat1, float paramFloat2, Color paramColor1, float paramFloat3, float paramFloat4, Color paramColor2)
    {
      return new GradientPaint(paramFloat1, paramFloat2, paramColor1, paramFloat3, paramFloat4, paramColor2, true);
    }
  }
  
  private static class OceanDisabledButtonImageFilter
    extends RGBImageFilter
  {
    private float min;
    private float factor;
    
    OceanDisabledButtonImageFilter(int paramInt1, int paramInt2)
    {
      canFilterIndexColorModel = true;
      min = paramInt1;
      factor = ((paramInt2 - paramInt1) / 255.0F);
    }
    
    public int filterRGB(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = Math.min(255, (int)((0.2125F * (paramInt3 >> 16 & 0xFF) + 0.7154F * (paramInt3 >> 8 & 0xFF) + 0.0721F * (paramInt3 & 0xFF) + 0.5F) * factor + min));
      return paramInt3 & 0xFF000000 | i << 16 | i << 8 | i << 0;
    }
  }
  
  private static class OceanToolBarImageFilter
    extends RGBImageFilter
  {
    OceanToolBarImageFilter()
    {
      canFilterIndexColorModel = true;
    }
    
    public int filterRGB(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = paramInt3 >> 16 & 0xFF;
      int j = paramInt3 >> 8 & 0xFF;
      int k = paramInt3 & 0xFF;
      int m = Math.max(Math.max(i, j), k);
      return paramInt3 & 0xFF000000 | m << 16 | m << 8 | m << 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
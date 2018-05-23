package sun.awt;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

public class RepaintArea
{
  private static final int MAX_BENEFIT_RATIO = 4;
  private static final int HORIZONTAL = 0;
  private static final int VERTICAL = 1;
  private static final int UPDATE = 2;
  private static final int RECT_COUNT = 3;
  private Rectangle[] paintRects = new Rectangle[3];
  
  public RepaintArea() {}
  
  private RepaintArea(RepaintArea paramRepaintArea)
  {
    for (int i = 0; i < 3; i++) {
      paintRects[i] = paintRects[i];
    }
  }
  
  public synchronized void add(Rectangle paramRectangle, int paramInt)
  {
    if (paramRectangle.isEmpty()) {
      return;
    }
    int i = 2;
    if (paramInt == 800) {
      i = width > height ? 0 : 1;
    }
    if (paintRects[i] != null) {
      paintRects[i].add(paramRectangle);
    } else {
      paintRects[i] = new Rectangle(paramRectangle);
    }
  }
  
  private synchronized RepaintArea cloneAndReset()
  {
    RepaintArea localRepaintArea = new RepaintArea(this);
    for (int i = 0; i < 3; i++) {
      paintRects[i] = null;
    }
    return localRepaintArea;
  }
  
  public boolean isEmpty()
  {
    for (int i = 0; i < 3; i++) {
      if (paintRects[i] != null) {
        return false;
      }
    }
    return true;
  }
  
  public synchronized void constrain(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    for (int i = 0; i < 3; i++)
    {
      Rectangle localRectangle = paintRects[i];
      if (localRectangle != null)
      {
        if (x < paramInt1)
        {
          width -= paramInt1 - x;
          x = paramInt1;
        }
        if (y < paramInt2)
        {
          height -= paramInt2 - y;
          y = paramInt2;
        }
        int j = x + width - paramInt1 - paramInt3;
        if (j > 0) {
          width -= j;
        }
        int k = y + height - paramInt2 - paramInt4;
        if (k > 0) {
          height -= k;
        }
        if ((width <= 0) || (height <= 0)) {
          paintRects[i] = null;
        }
      }
    }
  }
  
  public synchronized void subtract(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Rectangle localRectangle = new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4);
    for (int i = 0; i < 3; i++) {
      if ((subtract(paintRects[i], localRectangle)) && (paintRects[i] != null) && (paintRects[i].isEmpty())) {
        paintRects[i] = null;
      }
    }
  }
  
  public void paint(Object paramObject, boolean paramBoolean)
  {
    Component localComponent = (Component)paramObject;
    if (isEmpty()) {
      return;
    }
    if (!localComponent.isVisible()) {
      return;
    }
    RepaintArea localRepaintArea = cloneAndReset();
    if (!subtract(paintRects[1], paintRects[0])) {
      subtract(paintRects[0], paintRects[1]);
    }
    if ((paintRects[0] != null) && (paintRects[1] != null))
    {
      Rectangle localRectangle = paintRects[0].union(paintRects[1]);
      int j = width * height;
      int k = j - paintRects[0].width * paintRects[0].height - paintRects[1].width * paintRects[1].height;
      if (4 * k < j)
      {
        paintRects[0] = localRectangle;
        paintRects[1] = null;
      }
    }
    for (int i = 0; i < paintRects.length; i++) {
      if ((paintRects[i] != null) && (!paintRects[i].isEmpty()))
      {
        Graphics localGraphics = localComponent.getGraphics();
        if (localGraphics != null) {
          try
          {
            localGraphics.setClip(paintRects[i]);
            if (i == 2)
            {
              updateComponent(localComponent, localGraphics);
            }
            else
            {
              if (paramBoolean) {
                localGraphics.clearRect(paintRects[i].x, paintRects[i].y, paintRects[i].width, paintRects[i].height);
              }
              paintComponent(localComponent, localGraphics);
            }
          }
          finally
          {
            localGraphics.dispose();
          }
        }
      }
    }
  }
  
  protected void updateComponent(Component paramComponent, Graphics paramGraphics)
  {
    if (paramComponent != null) {
      paramComponent.update(paramGraphics);
    }
  }
  
  protected void paintComponent(Component paramComponent, Graphics paramGraphics)
  {
    if (paramComponent != null) {
      paramComponent.paint(paramGraphics);
    }
  }
  
  static boolean subtract(Rectangle paramRectangle1, Rectangle paramRectangle2)
  {
    if ((paramRectangle1 == null) || (paramRectangle2 == null)) {
      return true;
    }
    Rectangle localRectangle = paramRectangle1.intersection(paramRectangle2);
    if (localRectangle.isEmpty()) {
      return true;
    }
    if ((x == x) && (y == y))
    {
      if (width == width)
      {
        y += height;
        height -= height;
        return true;
      }
      if (height == height)
      {
        x += width;
        width -= width;
        return true;
      }
    }
    else if ((x + width == x + width) && (y + height == y + height))
    {
      if (width == width)
      {
        height -= height;
        return true;
      }
      if (height == height)
      {
        width -= width;
        return true;
      }
    }
    return false;
  }
  
  public String toString()
  {
    return super.toString() + "[ horizontal=" + paintRects[0] + " vertical=" + paintRects[1] + " update=" + paintRects[2] + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\RepaintArea.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
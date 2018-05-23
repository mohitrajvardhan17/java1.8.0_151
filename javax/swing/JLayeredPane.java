package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.SunToolkit;

public class JLayeredPane
  extends JComponent
  implements Accessible
{
  public static final Integer DEFAULT_LAYER = new Integer(0);
  public static final Integer PALETTE_LAYER = new Integer(100);
  public static final Integer MODAL_LAYER = new Integer(200);
  public static final Integer POPUP_LAYER = new Integer(300);
  public static final Integer DRAG_LAYER = new Integer(400);
  public static final Integer FRAME_CONTENT_LAYER = new Integer(35536);
  public static final String LAYER_PROPERTY = "layeredContainerLayer";
  private Hashtable<Component, Integer> componentToLayer;
  private boolean optimizedDrawingPossible = true;
  
  public JLayeredPane()
  {
    setLayout(null);
  }
  
  private void validateOptimizedDrawing()
  {
    int i = 0;
    synchronized (getTreeLock())
    {
      for (Component localComponent : getComponents())
      {
        Integer localInteger = null;
        if (((SunToolkit.isInstanceOf(localComponent, "javax.swing.JInternalFrame")) || (((localComponent instanceof JComponent)) && ((localInteger = (Integer)((JComponent)localComponent).getClientProperty("layeredContainerLayer")) != null))) && ((localInteger == null) || (!localInteger.equals(FRAME_CONTENT_LAYER))))
        {
          i = 1;
          break;
        }
      }
    }
    if (i != 0) {
      optimizedDrawingPossible = false;
    } else {
      optimizedDrawingPossible = true;
    }
  }
  
  protected void addImpl(Component paramComponent, Object paramObject, int paramInt)
  {
    int i;
    if ((paramObject instanceof Integer))
    {
      i = ((Integer)paramObject).intValue();
      setLayer(paramComponent, i);
    }
    else
    {
      i = getLayer(paramComponent);
    }
    int j = insertIndexForLayer(i, paramInt);
    super.addImpl(paramComponent, paramObject, j);
    paramComponent.validate();
    paramComponent.repaint();
    validateOptimizedDrawing();
  }
  
  public void remove(int paramInt)
  {
    Component localComponent = getComponent(paramInt);
    super.remove(paramInt);
    if ((localComponent != null) && (!(localComponent instanceof JComponent))) {
      getComponentToLayer().remove(localComponent);
    }
    validateOptimizedDrawing();
  }
  
  public void removeAll()
  {
    Component[] arrayOfComponent = getComponents();
    Hashtable localHashtable = getComponentToLayer();
    for (int i = arrayOfComponent.length - 1; i >= 0; i--)
    {
      Component localComponent = arrayOfComponent[i];
      if ((localComponent != null) && (!(localComponent instanceof JComponent))) {
        localHashtable.remove(localComponent);
      }
    }
    super.removeAll();
  }
  
  public boolean isOptimizedDrawingEnabled()
  {
    return optimizedDrawingPossible;
  }
  
  public static void putLayer(JComponent paramJComponent, int paramInt)
  {
    Integer localInteger = new Integer(paramInt);
    paramJComponent.putClientProperty("layeredContainerLayer", localInteger);
  }
  
  public static int getLayer(JComponent paramJComponent)
  {
    Integer localInteger;
    if ((localInteger = (Integer)paramJComponent.getClientProperty("layeredContainerLayer")) != null) {
      return localInteger.intValue();
    }
    return DEFAULT_LAYER.intValue();
  }
  
  public static JLayeredPane getLayeredPaneAbove(Component paramComponent)
  {
    if (paramComponent == null) {
      return null;
    }
    for (Container localContainer = paramComponent.getParent(); (localContainer != null) && (!(localContainer instanceof JLayeredPane)); localContainer = localContainer.getParent()) {}
    return (JLayeredPane)localContainer;
  }
  
  public void setLayer(Component paramComponent, int paramInt)
  {
    setLayer(paramComponent, paramInt, -1);
  }
  
  public void setLayer(Component paramComponent, int paramInt1, int paramInt2)
  {
    Integer localInteger = getObjectForLayer(paramInt1);
    if ((paramInt1 == getLayer(paramComponent)) && (paramInt2 == getPosition(paramComponent)))
    {
      repaint(paramComponent.getBounds());
      return;
    }
    if ((paramComponent instanceof JComponent)) {
      ((JComponent)paramComponent).putClientProperty("layeredContainerLayer", localInteger);
    } else {
      getComponentToLayer().put(paramComponent, localInteger);
    }
    if ((paramComponent.getParent() == null) || (paramComponent.getParent() != this))
    {
      repaint(paramComponent.getBounds());
      return;
    }
    int i = insertIndexForLayer(paramComponent, paramInt1, paramInt2);
    setComponentZOrder(paramComponent, i);
    repaint(paramComponent.getBounds());
  }
  
  public int getLayer(Component paramComponent)
  {
    Integer localInteger;
    if ((paramComponent instanceof JComponent)) {
      localInteger = (Integer)((JComponent)paramComponent).getClientProperty("layeredContainerLayer");
    } else {
      localInteger = (Integer)getComponentToLayer().get(paramComponent);
    }
    if (localInteger == null) {
      return DEFAULT_LAYER.intValue();
    }
    return localInteger.intValue();
  }
  
  public int getIndexOf(Component paramComponent)
  {
    int j = getComponentCount();
    for (int i = 0; i < j; i++) {
      if (paramComponent == getComponent(i)) {
        return i;
      }
    }
    return -1;
  }
  
  public void moveToFront(Component paramComponent)
  {
    setPosition(paramComponent, 0);
  }
  
  public void moveToBack(Component paramComponent)
  {
    setPosition(paramComponent, -1);
  }
  
  public void setPosition(Component paramComponent, int paramInt)
  {
    setLayer(paramComponent, getLayer(paramComponent), paramInt);
  }
  
  public int getPosition(Component paramComponent)
  {
    int n = 0;
    getComponentCount();
    int m = getIndexOf(paramComponent);
    if (m == -1) {
      return -1;
    }
    int j = getLayer(paramComponent);
    for (int i = m - 1; i >= 0; i--)
    {
      int k = getLayer(getComponent(i));
      if (k == j) {
        n++;
      } else {
        return n;
      }
    }
    return n;
  }
  
  public int highestLayer()
  {
    if (getComponentCount() > 0) {
      return getLayer(getComponent(0));
    }
    return 0;
  }
  
  public int lowestLayer()
  {
    int i = getComponentCount();
    if (i > 0) {
      return getLayer(getComponent(i - 1));
    }
    return 0;
  }
  
  public int getComponentCountInLayer(int paramInt)
  {
    int m = 0;
    int j = getComponentCount();
    for (int i = 0; i < j; i++)
    {
      int k = getLayer(getComponent(i));
      if (k == paramInt) {
        m++;
      } else {
        if ((m > 0) || (k < paramInt)) {
          break;
        }
      }
    }
    return m;
  }
  
  public Component[] getComponentsInLayer(int paramInt)
  {
    int m = 0;
    Component[] arrayOfComponent = new Component[getComponentCountInLayer(paramInt)];
    int j = getComponentCount();
    for (int i = 0; i < j; i++)
    {
      int k = getLayer(getComponent(i));
      if (k == paramInt) {
        arrayOfComponent[(m++)] = getComponent(i);
      } else {
        if ((m > 0) || (k < paramInt)) {
          break;
        }
      }
    }
    return arrayOfComponent;
  }
  
  public void paint(Graphics paramGraphics)
  {
    if (isOpaque())
    {
      Rectangle localRectangle = paramGraphics.getClipBounds();
      Color localColor = getBackground();
      if (localColor == null) {
        localColor = Color.lightGray;
      }
      paramGraphics.setColor(localColor);
      if (localRectangle != null) {
        paramGraphics.fillRect(x, y, width, height);
      } else {
        paramGraphics.fillRect(0, 0, getWidth(), getHeight());
      }
    }
    super.paint(paramGraphics);
  }
  
  protected Hashtable<Component, Integer> getComponentToLayer()
  {
    if (componentToLayer == null) {
      componentToLayer = new Hashtable(4);
    }
    return componentToLayer;
  }
  
  protected Integer getObjectForLayer(int paramInt)
  {
    Integer localInteger;
    switch (paramInt)
    {
    case 0: 
      localInteger = DEFAULT_LAYER;
      break;
    case 100: 
      localInteger = PALETTE_LAYER;
      break;
    case 200: 
      localInteger = MODAL_LAYER;
      break;
    case 300: 
      localInteger = POPUP_LAYER;
      break;
    case 400: 
      localInteger = DRAG_LAYER;
      break;
    default: 
      localInteger = new Integer(paramInt);
    }
    return localInteger;
  }
  
  protected int insertIndexForLayer(int paramInt1, int paramInt2)
  {
    return insertIndexForLayer(null, paramInt1, paramInt2);
  }
  
  private int insertIndexForLayer(Component paramComponent, int paramInt1, int paramInt2)
  {
    int m = -1;
    int n = -1;
    int i1 = getComponentCount();
    ArrayList localArrayList = new ArrayList(i1);
    for (int i2 = 0; i2 < i1; i2++) {
      if (getComponent(i2) != paramComponent) {
        localArrayList.add(getComponent(i2));
      }
    }
    int j = localArrayList.size();
    for (int i = 0; i < j; i++)
    {
      int k = getLayer((Component)localArrayList.get(i));
      if ((m == -1) && (k == paramInt1)) {
        m = i;
      }
      if (k < paramInt1)
      {
        if (i == 0)
        {
          m = 0;
          n = 0;
          break;
        }
        n = i;
        break;
      }
    }
    if ((m == -1) && (n == -1)) {
      return j;
    }
    if ((m != -1) && (n == -1)) {
      n = j;
    }
    if ((n != -1) && (m == -1)) {
      m = n;
    }
    if (paramInt2 == -1) {
      return n;
    }
    if ((paramInt2 > -1) && (m + paramInt2 <= n)) {
      return m + paramInt2;
    }
    return n;
  }
  
  protected String paramString()
  {
    String str = optimizedDrawingPossible ? "true" : "false";
    return super.paramString() + ",optimizedDrawingPossible=" + str;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJLayeredPane();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJLayeredPane
    extends JComponent.AccessibleJComponent
  {
    protected AccessibleJLayeredPane()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.LAYERED_PANE;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JLayeredPane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
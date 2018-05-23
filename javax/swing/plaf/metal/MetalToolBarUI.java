package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.swing.plaf.basic.BasicToolBarUI.DockingListener;
import javax.swing.plaf.basic.BasicToolBarUI.DragWindow;
import javax.swing.plaf.basic.BasicToolBarUI.PropertyListener;
import javax.swing.plaf.basic.BasicToolBarUI.ToolBarContListener;

public class MetalToolBarUI
  extends BasicToolBarUI
{
  private static List<WeakReference<JComponent>> components = new ArrayList();
  protected ContainerListener contListener;
  protected PropertyChangeListener rolloverListener;
  private static Border nonRolloverBorder;
  private JMenuBar lastMenuBar;
  
  public MetalToolBarUI() {}
  
  static synchronized void register(JComponent paramJComponent)
  {
    if (paramJComponent == null) {
      throw new NullPointerException("JComponent must be non-null");
    }
    components.add(new WeakReference(paramJComponent));
  }
  
  static synchronized void unregister(JComponent paramJComponent)
  {
    for (int i = components.size() - 1; i >= 0; i--)
    {
      JComponent localJComponent = (JComponent)((WeakReference)components.get(i)).get();
      if ((localJComponent == paramJComponent) || (localJComponent == null)) {
        components.remove(i);
      }
    }
  }
  
  static synchronized Object findRegisteredComponentOfType(JComponent paramJComponent, Class paramClass)
  {
    JRootPane localJRootPane = SwingUtilities.getRootPane(paramJComponent);
    if (localJRootPane != null) {
      for (int i = components.size() - 1; i >= 0; i--)
      {
        Object localObject = ((WeakReference)components.get(i)).get();
        if (localObject == null) {
          components.remove(i);
        } else if ((paramClass.isInstance(localObject)) && (SwingUtilities.getRootPane((Component)localObject) == localJRootPane)) {
          return localObject;
        }
      }
    }
    return null;
  }
  
  static boolean doesMenuBarBorderToolBar(JMenuBar paramJMenuBar)
  {
    JToolBar localJToolBar = (JToolBar)findRegisteredComponentOfType(paramJMenuBar, JToolBar.class);
    if ((localJToolBar != null) && (localJToolBar.getOrientation() == 0))
    {
      JRootPane localJRootPane = SwingUtilities.getRootPane(paramJMenuBar);
      Point localPoint = new Point(0, 0);
      localPoint = SwingUtilities.convertPoint(paramJMenuBar, localPoint, localJRootPane);
      int i = x;
      int j = y;
      x = (y = 0);
      localPoint = SwingUtilities.convertPoint(localJToolBar, localPoint, localJRootPane);
      return (x == i) && (j + paramJMenuBar.getHeight() == y) && (paramJMenuBar.getWidth() == localJToolBar.getWidth());
    }
    return false;
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new MetalToolBarUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    register(paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    super.uninstallUI(paramJComponent);
    nonRolloverBorder = null;
    unregister(paramJComponent);
  }
  
  protected void installListeners()
  {
    super.installListeners();
    contListener = createContainerListener();
    if (contListener != null) {
      toolBar.addContainerListener(contListener);
    }
    rolloverListener = createRolloverListener();
    if (rolloverListener != null) {
      toolBar.addPropertyChangeListener(rolloverListener);
    }
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    if (contListener != null) {
      toolBar.removeContainerListener(contListener);
    }
    rolloverListener = createRolloverListener();
    if (rolloverListener != null) {
      toolBar.removePropertyChangeListener(rolloverListener);
    }
  }
  
  protected Border createRolloverBorder()
  {
    return super.createRolloverBorder();
  }
  
  protected Border createNonRolloverBorder()
  {
    return super.createNonRolloverBorder();
  }
  
  private Border createNonRolloverToggleBorder()
  {
    return createNonRolloverBorder();
  }
  
  protected void setBorderToNonRollover(Component paramComponent)
  {
    if (((paramComponent instanceof JToggleButton)) && (!(paramComponent instanceof JCheckBox)))
    {
      JToggleButton localJToggleButton = (JToggleButton)paramComponent;
      Border localBorder = localJToggleButton.getBorder();
      super.setBorderToNonRollover(paramComponent);
      if ((localBorder instanceof UIResource))
      {
        if (nonRolloverBorder == null) {
          nonRolloverBorder = createNonRolloverToggleBorder();
        }
        localJToggleButton.setBorder(nonRolloverBorder);
      }
    }
    else
    {
      super.setBorderToNonRollover(paramComponent);
    }
  }
  
  protected ContainerListener createContainerListener()
  {
    return null;
  }
  
  protected PropertyChangeListener createRolloverListener()
  {
    return null;
  }
  
  protected MouseInputListener createDockingListener()
  {
    return new MetalDockingListener(toolBar);
  }
  
  protected void setDragOffset(Point paramPoint)
  {
    if (!GraphicsEnvironment.isHeadless())
    {
      if (dragWindow == null) {
        dragWindow = createDragWindow(toolBar);
      }
      dragWindow.setOffset(paramPoint);
    }
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    if (paramGraphics == null) {
      throw new NullPointerException("graphics must be non-null");
    }
    if ((paramJComponent.isOpaque()) && ((paramJComponent.getBackground() instanceof UIResource)) && (((JToolBar)paramJComponent).getOrientation() == 0) && (UIManager.get("MenuBar.gradient") != null))
    {
      JRootPane localJRootPane = SwingUtilities.getRootPane(paramJComponent);
      JMenuBar localJMenuBar = (JMenuBar)findRegisteredComponentOfType(paramJComponent, JMenuBar.class);
      if ((localJMenuBar != null) && (localJMenuBar.isOpaque()) && ((localJMenuBar.getBackground() instanceof UIResource)))
      {
        Point localPoint = new Point(0, 0);
        localPoint = SwingUtilities.convertPoint(paramJComponent, localPoint, localJRootPane);
        int i = x;
        int j = y;
        x = (y = 0);
        localPoint = SwingUtilities.convertPoint(localJMenuBar, localPoint, localJRootPane);
        if ((x == i) && (j == y + localJMenuBar.getHeight()) && (localJMenuBar.getWidth() == paramJComponent.getWidth()) && (MetalUtils.drawGradient(paramJComponent, paramGraphics, "MenuBar.gradient", 0, -localJMenuBar.getHeight(), paramJComponent.getWidth(), paramJComponent.getHeight() + localJMenuBar.getHeight(), true)))
        {
          setLastMenuBar(localJMenuBar);
          paint(paramGraphics, paramJComponent);
          return;
        }
      }
      if (MetalUtils.drawGradient(paramJComponent, paramGraphics, "MenuBar.gradient", 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), true))
      {
        setLastMenuBar(null);
        paint(paramGraphics, paramJComponent);
        return;
      }
    }
    setLastMenuBar(null);
    super.update(paramGraphics, paramJComponent);
  }
  
  private void setLastMenuBar(JMenuBar paramJMenuBar)
  {
    if ((MetalLookAndFeel.usingOcean()) && (lastMenuBar != paramJMenuBar))
    {
      if (lastMenuBar != null) {
        lastMenuBar.repaint();
      }
      if (paramJMenuBar != null) {
        paramJMenuBar.repaint();
      }
      lastMenuBar = paramJMenuBar;
    }
  }
  
  protected class MetalContainerListener
    extends BasicToolBarUI.ToolBarContListener
  {
    protected MetalContainerListener()
    {
      super();
    }
  }
  
  protected class MetalDockingListener
    extends BasicToolBarUI.DockingListener
  {
    private boolean pressedInBumps = false;
    
    public MetalDockingListener(JToolBar paramJToolBar)
    {
      super(paramJToolBar);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      super.mousePressed(paramMouseEvent);
      if (!toolBar.isEnabled()) {
        return;
      }
      pressedInBumps = false;
      Rectangle localRectangle = new Rectangle();
      if (toolBar.getOrientation() == 0)
      {
        int i = MetalUtils.isLeftToRight(toolBar) ? 0 : toolBar.getSize().width - 14;
        localRectangle.setBounds(i, 0, 14, toolBar.getSize().height);
      }
      else
      {
        localRectangle.setBounds(0, 0, toolBar.getSize().width, 14);
      }
      if (localRectangle.contains(paramMouseEvent.getPoint()))
      {
        pressedInBumps = true;
        Point localPoint = paramMouseEvent.getPoint();
        if (!MetalUtils.isLeftToRight(toolBar)) {
          x -= toolBar.getSize().width - toolBar.getPreferredSize().width;
        }
        setDragOffset(localPoint);
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      if (pressedInBumps) {
        super.mouseDragged(paramMouseEvent);
      }
    }
  }
  
  protected class MetalRolloverListener
    extends BasicToolBarUI.PropertyListener
  {
    protected MetalRolloverListener()
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\metal\MetalToolBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
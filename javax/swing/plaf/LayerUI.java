package javax.swing.plaf;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;

public class LayerUI<V extends Component>
  extends ComponentUI
  implements Serializable
{
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  
  public LayerUI() {}
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    paramJComponent.paint(paramGraphics);
  }
  
  public void eventDispatched(AWTEvent paramAWTEvent, JLayer<? extends V> paramJLayer)
  {
    if ((paramAWTEvent instanceof FocusEvent)) {
      processFocusEvent((FocusEvent)paramAWTEvent, paramJLayer);
    } else if ((paramAWTEvent instanceof MouseEvent)) {
      switch (paramAWTEvent.getID())
      {
      case 500: 
      case 501: 
      case 502: 
      case 504: 
      case 505: 
        processMouseEvent((MouseEvent)paramAWTEvent, paramJLayer);
        break;
      case 503: 
      case 506: 
        processMouseMotionEvent((MouseEvent)paramAWTEvent, paramJLayer);
        break;
      case 507: 
        processMouseWheelEvent((MouseWheelEvent)paramAWTEvent, paramJLayer);
      }
    } else if ((paramAWTEvent instanceof KeyEvent)) {
      processKeyEvent((KeyEvent)paramAWTEvent, paramJLayer);
    } else if ((paramAWTEvent instanceof ComponentEvent)) {
      processComponentEvent((ComponentEvent)paramAWTEvent, paramJLayer);
    } else if ((paramAWTEvent instanceof InputMethodEvent)) {
      processInputMethodEvent((InputMethodEvent)paramAWTEvent, paramJLayer);
    } else if ((paramAWTEvent instanceof HierarchyEvent)) {
      switch (paramAWTEvent.getID())
      {
      case 1400: 
        processHierarchyEvent((HierarchyEvent)paramAWTEvent, paramJLayer);
        break;
      case 1401: 
      case 1402: 
        processHierarchyBoundsEvent((HierarchyEvent)paramAWTEvent, paramJLayer);
      }
    }
  }
  
  protected void processComponentEvent(ComponentEvent paramComponentEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processFocusEvent(FocusEvent paramFocusEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processKeyEvent(KeyEvent paramKeyEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processMouseEvent(MouseEvent paramMouseEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processMouseMotionEvent(MouseEvent paramMouseEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processMouseWheelEvent(MouseWheelEvent paramMouseWheelEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processInputMethodEvent(InputMethodEvent paramInputMethodEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processHierarchyEvent(HierarchyEvent paramHierarchyEvent, JLayer<? extends V> paramJLayer) {}
  
  protected void processHierarchyBoundsEvent(HierarchyEvent paramHierarchyEvent, JLayer<? extends V> paramJLayer) {}
  
  public void updateUI(JLayer<? extends V> paramJLayer) {}
  
  public void installUI(JComponent paramJComponent)
  {
    addPropertyChangeListener((JLayer)paramJComponent);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    removePropertyChangeListener((JLayer)paramJComponent);
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    propertyChangeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    propertyChangeSupport.removePropertyChangeListener(paramPropertyChangeListener);
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners()
  {
    return propertyChangeSupport.getPropertyChangeListeners();
  }
  
  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    propertyChangeSupport.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    propertyChangeSupport.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners(String paramString)
  {
    return propertyChangeSupport.getPropertyChangeListeners(paramString);
  }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    propertyChangeSupport.firePropertyChange(paramString, paramObject1, paramObject2);
  }
  
  public void applyPropertyChange(PropertyChangeEvent paramPropertyChangeEvent, JLayer<? extends V> paramJLayer) {}
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    JLayer localJLayer = (JLayer)paramJComponent;
    if (localJLayer.getView() != null) {
      return localJLayer.getView().getBaseline(paramInt1, paramInt2);
    }
    return super.getBaseline(paramJComponent, paramInt1, paramInt2);
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    JLayer localJLayer = (JLayer)paramJComponent;
    if (localJLayer.getView() != null) {
      return localJLayer.getView().getBaselineResizeBehavior();
    }
    return super.getBaselineResizeBehavior(paramJComponent);
  }
  
  public void doLayout(JLayer<? extends V> paramJLayer)
  {
    Component localComponent = paramJLayer.getView();
    if (localComponent != null) {
      localComponent.setBounds(0, 0, paramJLayer.getWidth(), paramJLayer.getHeight());
    }
    JPanel localJPanel = paramJLayer.getGlassPane();
    if (localJPanel != null) {
      localJPanel.setBounds(0, 0, paramJLayer.getWidth(), paramJLayer.getHeight());
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    JLayer localJLayer = (JLayer)paramJComponent;
    Component localComponent = localJLayer.getView();
    if (localComponent != null) {
      return localComponent.getPreferredSize();
    }
    return super.getPreferredSize(paramJComponent);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    JLayer localJLayer = (JLayer)paramJComponent;
    Component localComponent = localJLayer.getView();
    if (localComponent != null) {
      return localComponent.getMinimumSize();
    }
    return super.getMinimumSize(paramJComponent);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    JLayer localJLayer = (JLayer)paramJComponent;
    Component localComponent = localJLayer.getView();
    if (localComponent != null) {
      return localComponent.getMaximumSize();
    }
    return super.getMaximumSize(paramJComponent);
  }
  
  public void paintImmediately(int paramInt1, int paramInt2, int paramInt3, int paramInt4, JLayer<? extends V> paramJLayer)
  {
    paramJLayer.paintImmediately(paramInt1, paramInt2, paramInt3, paramInt4);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\LayerUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
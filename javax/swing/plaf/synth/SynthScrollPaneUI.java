package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import javax.swing.text.JTextComponent;

public class SynthScrollPaneUI
  extends BasicScrollPaneUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  private boolean viewportViewHasFocus = false;
  private ViewportViewFocusHandler viewportViewFocusHandler;
  
  public SynthScrollPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthScrollPaneUI();
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintScrollPaneBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics)
  {
    Border localBorder = scrollpane.getViewportBorder();
    if (localBorder != null)
    {
      Rectangle localRectangle = scrollpane.getViewportBorderBounds();
      localBorder.paintBorder(scrollpane, paramGraphics, x, y, width, height);
    }
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintScrollPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void installDefaults(JScrollPane paramJScrollPane)
  {
    updateStyle(paramJScrollPane);
  }
  
  private void updateStyle(JScrollPane paramJScrollPane)
  {
    SynthContext localSynthContext = getContext(paramJScrollPane, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      Border localBorder = scrollpane.getViewportBorder();
      if ((localBorder == null) || ((localBorder instanceof UIResource))) {
        scrollpane.setViewportBorder(new ViewportBorder(localSynthContext));
      }
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions(paramJScrollPane);
        installKeyboardActions(paramJScrollPane);
      }
    }
    localSynthContext.dispose();
  }
  
  protected void installListeners(JScrollPane paramJScrollPane)
  {
    super.installListeners(paramJScrollPane);
    paramJScrollPane.addPropertyChangeListener(this);
    if (UIManager.getBoolean("ScrollPane.useChildTextComponentFocus"))
    {
      viewportViewFocusHandler = new ViewportViewFocusHandler(null);
      paramJScrollPane.getViewport().addContainerListener(viewportViewFocusHandler);
      Component localComponent = paramJScrollPane.getViewport().getView();
      if ((localComponent instanceof JTextComponent)) {
        localComponent.addFocusListener(viewportViewFocusHandler);
      }
    }
  }
  
  protected void uninstallDefaults(JScrollPane paramJScrollPane)
  {
    SynthContext localSynthContext = getContext(paramJScrollPane, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    if ((scrollpane.getViewportBorder() instanceof UIResource)) {
      scrollpane.setViewportBorder(null);
    }
  }
  
  protected void uninstallListeners(JComponent paramJComponent)
  {
    super.uninstallListeners(paramJComponent);
    paramJComponent.removePropertyChangeListener(this);
    if (viewportViewFocusHandler != null)
    {
      JViewport localJViewport = ((JScrollPane)paramJComponent).getViewport();
      localJViewport.removeContainerListener(viewportViewFocusHandler);
      if (localJViewport.getView() != null) {
        localJViewport.getView().removeFocusListener(viewportViewFocusHandler);
      }
      viewportViewFocusHandler = null;
    }
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent)
  {
    int i = SynthLookAndFeel.getComponentState(paramJComponent);
    if ((viewportViewFocusHandler != null) && (viewportViewHasFocus)) {
      i |= 0x100;
    }
    return i;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle(scrollpane);
    }
  }
  
  private class ViewportBorder
    extends AbstractBorder
    implements UIResource
  {
    private Insets insets;
    
    ViewportBorder(SynthContext paramSynthContext)
    {
      insets = ((Insets)paramSynthContext.getStyle().get(paramSynthContext, "ScrollPane.viewportBorderInsets"));
      if (insets == null) {
        insets = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
      }
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      JComponent localJComponent = (JComponent)paramComponent;
      SynthContext localSynthContext = getContext(localJComponent);
      SynthStyle localSynthStyle = localSynthContext.getStyle();
      if (localSynthStyle == null)
      {
        if (!$assertionsDisabled) {
          throw new AssertionError("SynthBorder is being used outside after the  UI has been uninstalled");
        }
        return;
      }
      localSynthContext.getPainter().paintViewportBorder(localSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      localSynthContext.dispose();
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      if (paramInsets == null) {
        return new Insets(insets.top, insets.left, insets.bottom, insets.right);
      }
      top = insets.top;
      bottom = insets.bottom;
      left = insets.left;
      right = insets.left;
      return paramInsets;
    }
    
    public boolean isBorderOpaque()
    {
      return false;
    }
  }
  
  private class ViewportViewFocusHandler
    implements ContainerListener, FocusListener
  {
    private ViewportViewFocusHandler() {}
    
    public void componentAdded(ContainerEvent paramContainerEvent)
    {
      if ((paramContainerEvent.getChild() instanceof JTextComponent))
      {
        paramContainerEvent.getChild().addFocusListener(this);
        viewportViewHasFocus = paramContainerEvent.getChild().isFocusOwner();
        scrollpane.repaint();
      }
    }
    
    public void componentRemoved(ContainerEvent paramContainerEvent)
    {
      if ((paramContainerEvent.getChild() instanceof JTextComponent)) {
        paramContainerEvent.getChild().removeFocusListener(this);
      }
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      viewportViewHasFocus = true;
      scrollpane.repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      viewportViewHasFocus = false;
      scrollpane.repaint();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthScrollPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
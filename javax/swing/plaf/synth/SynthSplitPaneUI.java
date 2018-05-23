package javax.swing.plaf.synth;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SynthSplitPaneUI
  extends BasicSplitPaneUI
  implements PropertyChangeListener, SynthUI
{
  private static Set<KeyStroke> managingFocusForwardTraversalKeys;
  private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
  private SynthStyle style;
  private SynthStyle dividerStyle;
  
  public SynthSplitPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthSplitPaneUI();
  }
  
  protected void installDefaults()
  {
    updateStyle(splitPane);
    setOrientation(splitPane.getOrientation());
    setContinuousLayout(splitPane.isContinuousLayout());
    resetLayoutManager();
    if (nonContinuousLayoutDivider == null) {
      setNonContinuousLayoutDivider(createDefaultNonContinuousLayoutDivider(), true);
    } else {
      setNonContinuousLayoutDivider(nonContinuousLayoutDivider, true);
    }
    if (managingFocusForwardTraversalKeys == null)
    {
      managingFocusForwardTraversalKeys = new HashSet();
      managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 0));
    }
    splitPane.setFocusTraversalKeys(0, managingFocusForwardTraversalKeys);
    if (managingFocusBackwardTraversalKeys == null)
    {
      managingFocusBackwardTraversalKeys = new HashSet();
      managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(9, 1));
    }
    splitPane.setFocusTraversalKeys(1, managingFocusBackwardTraversalKeys);
  }
  
  private void updateStyle(JSplitPane paramJSplitPane)
  {
    SynthContext localSynthContext = getContext(paramJSplitPane, Region.SPLIT_PANE_DIVIDER, 1);
    SynthStyle localSynthStyle1 = dividerStyle;
    dividerStyle = SynthLookAndFeel.updateStyle(localSynthContext, this);
    localSynthContext.dispose();
    localSynthContext = getContext(paramJSplitPane, 1);
    SynthStyle localSynthStyle2 = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle2)
    {
      Object localObject = style.get(localSynthContext, "SplitPane.size");
      if (localObject == null) {
        localObject = Integer.valueOf(6);
      }
      LookAndFeel.installProperty(paramJSplitPane, "dividerSize", localObject);
      localObject = style.get(localSynthContext, "SplitPane.oneTouchExpandable");
      if (localObject != null) {
        LookAndFeel.installProperty(paramJSplitPane, "oneTouchExpandable", localObject);
      }
      if (divider != null)
      {
        paramJSplitPane.remove(divider);
        divider.setDividerSize(paramJSplitPane.getDividerSize());
      }
      if (localSynthStyle2 != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
    if ((style != localSynthStyle2) || (dividerStyle != localSynthStyle1))
    {
      if (divider != null) {
        paramJSplitPane.remove(divider);
      }
      divider = createDefaultDivider();
      divider.setBasicSplitPaneUI(this);
      paramJSplitPane.add(divider, "divider");
    }
    localSynthContext.dispose();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    splitPane.addPropertyChangeListener(this);
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(splitPane, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
    localSynthContext = getContext(splitPane, Region.SPLIT_PANE_DIVIDER, 1);
    dividerStyle.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    dividerStyle = null;
    super.uninstallDefaults();
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    splitPane.removePropertyChangeListener(this);
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  SynthContext getContext(JComponent paramJComponent, Region paramRegion)
  {
    return getContext(paramJComponent, paramRegion, getComponentState(paramJComponent, paramRegion));
  }
  
  private SynthContext getContext(JComponent paramJComponent, Region paramRegion, int paramInt)
  {
    if (paramRegion == Region.SPLIT_PANE_DIVIDER) {
      return SynthContext.getContext(paramJComponent, paramRegion, dividerStyle, paramInt);
    }
    return SynthContext.getContext(paramJComponent, paramRegion, style, paramInt);
  }
  
  private int getComponentState(JComponent paramJComponent, Region paramRegion)
  {
    int i = SynthLookAndFeel.getComponentState(paramJComponent);
    if (divider.isMouseOver()) {
      i |= 0x2;
    }
    return i;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JSplitPane)paramPropertyChangeEvent.getSource());
    }
  }
  
  public BasicSplitPaneDivider createDefaultDivider()
  {
    SynthSplitPaneDivider localSynthSplitPaneDivider = new SynthSplitPaneDivider(this);
    localSynthSplitPaneDivider.setDividerSize(splitPane.getDividerSize());
    return localSynthSplitPaneDivider;
  }
  
  protected Component createDefaultNonContinuousLayoutDivider()
  {
    new Canvas()
    {
      public void paint(Graphics paramAnonymousGraphics)
      {
        SynthSplitPaneUI.this.paintDragDivider(paramAnonymousGraphics, 0, 0, getWidth(), getHeight());
      }
    };
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintSplitPaneBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
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
    super.paint(paramGraphics, splitPane);
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintSplitPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  private void paintDragDivider(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    SynthContext localSynthContext = getContext(splitPane, Region.SPLIT_PANE_DIVIDER);
    localSynthContext.setComponentState((localSynthContext.getComponentState() | 0x2) ^ 0x2 | 0x4);
    Shape localShape = paramGraphics.getClip();
    paramGraphics.clipRect(paramInt1, paramInt2, paramInt3, paramInt4);
    localSynthContext.getPainter().paintSplitPaneDragDivider(localSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, splitPane.getOrientation());
    paramGraphics.setClip(localShape);
    localSynthContext.dispose();
  }
  
  public void finishedPaintingChildren(JSplitPane paramJSplitPane, Graphics paramGraphics)
  {
    if ((paramJSplitPane == splitPane) && (getLastDragLocation() != -1) && (!isContinuousLayout()) && (!draggingHW)) {
      if (paramJSplitPane.getOrientation() == 1) {
        paintDragDivider(paramGraphics, getLastDragLocation(), 0, dividerSize - 1, splitPane.getHeight() - 1);
      } else {
        paintDragDivider(paramGraphics, 0, getLastDragLocation(), splitPane.getWidth() - 1, dividerSize - 1);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthSplitPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
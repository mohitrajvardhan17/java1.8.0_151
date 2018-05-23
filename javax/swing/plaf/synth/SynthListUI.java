package javax.swing.plaf.synth;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultListCellRenderer.UIResource;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicListUI;

public class SynthListUI
  extends BasicListUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  private boolean useListColors;
  private boolean useUIBorder;
  
  public SynthListUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthListUI();
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintListBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    localSynthContext.dispose();
    paint(paramGraphics, paramJComponent);
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintListBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void installListeners()
  {
    super.installListeners();
    list.addPropertyChangeListener(this);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JList)paramPropertyChangeEvent.getSource());
    }
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    list.removePropertyChangeListener(this);
  }
  
  protected void installDefaults()
  {
    if ((list.getCellRenderer() == null) || ((list.getCellRenderer() instanceof UIResource))) {
      list.setCellRenderer(new SynthListCellRenderer(null));
    }
    updateStyle(list);
  }
  
  private void updateStyle(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(list, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      localSynthContext.setComponentState(512);
      Color localColor1 = list.getSelectionBackground();
      if ((localColor1 == null) || ((localColor1 instanceof UIResource))) {
        list.setSelectionBackground(style.getColor(localSynthContext, ColorType.TEXT_BACKGROUND));
      }
      Color localColor2 = list.getSelectionForeground();
      if ((localColor2 == null) || ((localColor2 instanceof UIResource))) {
        list.setSelectionForeground(style.getColor(localSynthContext, ColorType.TEXT_FOREGROUND));
      }
      useListColors = style.getBoolean(localSynthContext, "List.rendererUseListColors", true);
      useUIBorder = style.getBoolean(localSynthContext, "List.rendererUseUIBorder", true);
      int i = style.getInt(localSynthContext, "List.cellHeight", -1);
      if (i != -1) {
        list.setFixedCellHeight(i);
      }
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
    }
    localSynthContext.dispose();
  }
  
  protected void uninstallDefaults()
  {
    super.uninstallDefaults();
    SynthContext localSynthContext = getContext(list, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
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
    return SynthLookAndFeel.getComponentState(paramJComponent);
  }
  
  private class SynthListCellRenderer
    extends DefaultListCellRenderer.UIResource
  {
    private SynthListCellRenderer() {}
    
    public String getName()
    {
      return "List.cellRenderer";
    }
    
    public void setBorder(Border paramBorder)
    {
      if ((useUIBorder) || ((paramBorder instanceof SynthBorder))) {
        super.setBorder(paramBorder);
      }
    }
    
    public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      if ((!useListColors) && ((paramBoolean1) || (paramBoolean2))) {
        SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), paramBoolean1, paramBoolean2, paramJList.isEnabled(), false);
      } else {
        SynthLookAndFeel.resetSelectedUI();
      }
      super.getListCellRendererComponent(paramJList, paramObject, paramInt, paramBoolean1, paramBoolean2);
      return this;
    }
    
    public void paint(Graphics paramGraphics)
    {
      super.paint(paramGraphics);
      SynthLookAndFeel.resetSelectedUI();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthListUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
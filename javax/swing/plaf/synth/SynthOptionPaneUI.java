package javax.swing.plaf.synth;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import sun.swing.DefaultLookup;

public class SynthOptionPaneUI
  extends BasicOptionPaneUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  
  public SynthOptionPaneUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthOptionPaneUI();
  }
  
  protected void installDefaults()
  {
    updateStyle(optionPane);
  }
  
  protected void installListeners()
  {
    super.installListeners();
    optionPane.addPropertyChangeListener(this);
  }
  
  private void updateStyle(JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      minimumSize = ((Dimension)style.get(localSynthContext, "OptionPane.minimumSize"));
      if (minimumSize == null) {
        minimumSize = new Dimension(262, 90);
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
    SynthContext localSynthContext = getContext(optionPane, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    optionPane.removePropertyChangeListener(this);
  }
  
  protected void installComponents()
  {
    optionPane.add(createMessageArea());
    Container localContainer = createSeparator();
    if (localContainer != null)
    {
      optionPane.add(localContainer);
      SynthContext localSynthContext = getContext(optionPane, 1);
      optionPane.add(Box.createVerticalStrut(localSynthContext.getStyle().getInt(localSynthContext, "OptionPane.separatorPadding", 6)));
      localSynthContext.dispose();
    }
    optionPane.add(createButtonArea());
    optionPane.applyComponentOrientation(optionPane.getComponentOrientation());
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
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintOptionPaneBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
    localSynthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintOptionPaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle((JOptionPane)paramPropertyChangeEvent.getSource());
    }
  }
  
  protected boolean getSizeButtonsToSameWidth()
  {
    return DefaultLookup.getBoolean(optionPane, this, "OptionPane.sameSizeButtons", true);
  }
  
  protected Container createMessageArea()
  {
    JPanel localJPanel1 = new JPanel();
    localJPanel1.setName("OptionPane.messageArea");
    localJPanel1.setLayout(new BorderLayout());
    JPanel localJPanel2 = new JPanel(new GridBagLayout());
    JPanel localJPanel3 = new JPanel(new BorderLayout());
    localJPanel2.setName("OptionPane.body");
    localJPanel3.setName("OptionPane.realBody");
    if (getIcon() != null)
    {
      localObject = new JPanel();
      ((JPanel)localObject).setName("OptionPane.separator");
      ((JPanel)localObject).setPreferredSize(new Dimension(15, 1));
      localJPanel3.add((Component)localObject, "Before");
    }
    localJPanel3.add(localJPanel2, "Center");
    Object localObject = new GridBagConstraints();
    gridx = (gridy = 0);
    gridwidth = 0;
    gridheight = 1;
    SynthContext localSynthContext = getContext(optionPane, 1);
    anchor = localSynthContext.getStyle().getInt(localSynthContext, "OptionPane.messageAnchor", 10);
    localSynthContext.dispose();
    insets = new Insets(0, 0, 3, 0);
    addMessageComponents(localJPanel2, (GridBagConstraints)localObject, getMessage(), getMaxCharactersPerLineCount(), false);
    localJPanel1.add(localJPanel3, "Center");
    addIcon(localJPanel1);
    return localJPanel1;
  }
  
  protected Container createSeparator()
  {
    JSeparator localJSeparator = new JSeparator(0);
    localJSeparator.setName("OptionPane.separator");
    return localJSeparator;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthOptionPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
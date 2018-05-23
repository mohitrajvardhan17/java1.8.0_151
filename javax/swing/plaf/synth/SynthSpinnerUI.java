package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SpinnerUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class SynthSpinnerUI
  extends BasicSpinnerUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  private EditorFocusHandler editorFocusHandler = new EditorFocusHandler(null);
  
  public SynthSpinnerUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthSpinnerUI();
  }
  
  protected void installListeners()
  {
    super.installListeners();
    spinner.addPropertyChangeListener(this);
    JComponent localJComponent = spinner.getEditor();
    if ((localJComponent instanceof JSpinner.DefaultEditor))
    {
      JFormattedTextField localJFormattedTextField = ((JSpinner.DefaultEditor)localJComponent).getTextField();
      if (localJFormattedTextField != null) {
        localJFormattedTextField.addFocusListener(editorFocusHandler);
      }
    }
  }
  
  protected void uninstallListeners()
  {
    super.uninstallListeners();
    spinner.removePropertyChangeListener(this);
    JComponent localJComponent = spinner.getEditor();
    if ((localJComponent instanceof JSpinner.DefaultEditor))
    {
      JFormattedTextField localJFormattedTextField = ((JSpinner.DefaultEditor)localJComponent).getTextField();
      if (localJFormattedTextField != null) {
        localJFormattedTextField.removeFocusListener(editorFocusHandler);
      }
    }
  }
  
  protected void installDefaults()
  {
    LayoutManager localLayoutManager = spinner.getLayout();
    if ((localLayoutManager == null) || ((localLayoutManager instanceof UIResource))) {
      spinner.setLayout(createLayout());
    }
    updateStyle(spinner);
  }
  
  private void updateStyle(JSpinner paramJSpinner)
  {
    SynthContext localSynthContext = getContext(paramJSpinner, 1);
    SynthStyle localSynthStyle = style;
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if ((style != localSynthStyle) && (localSynthStyle != null)) {
      installKeyboardActions();
    }
    localSynthContext.dispose();
  }
  
  protected void uninstallDefaults()
  {
    if ((spinner.getLayout() instanceof UIResource)) {
      spinner.setLayout(null);
    }
    SynthContext localSynthContext = getContext(spinner, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  protected LayoutManager createLayout()
  {
    return new SpinnerLayout(null);
  }
  
  protected Component createPreviousButton()
  {
    SynthArrowButton localSynthArrowButton = new SynthArrowButton(5);
    localSynthArrowButton.setName("Spinner.previousButton");
    installPreviousButtonListeners(localSynthArrowButton);
    return localSynthArrowButton;
  }
  
  protected Component createNextButton()
  {
    SynthArrowButton localSynthArrowButton = new SynthArrowButton(1);
    localSynthArrowButton.setName("Spinner.nextButton");
    installNextButtonListeners(localSynthArrowButton);
    return localSynthArrowButton;
  }
  
  protected JComponent createEditor()
  {
    JComponent localJComponent = spinner.getEditor();
    localJComponent.setName("Spinner.editor");
    updateEditorAlignment(localJComponent);
    return localJComponent;
  }
  
  protected void replaceEditor(JComponent paramJComponent1, JComponent paramJComponent2)
  {
    spinner.remove(paramJComponent1);
    spinner.add(paramJComponent2, "Editor");
    JFormattedTextField localJFormattedTextField;
    if ((paramJComponent1 instanceof JSpinner.DefaultEditor))
    {
      localJFormattedTextField = ((JSpinner.DefaultEditor)paramJComponent1).getTextField();
      if (localJFormattedTextField != null) {
        localJFormattedTextField.removeFocusListener(editorFocusHandler);
      }
    }
    if ((paramJComponent2 instanceof JSpinner.DefaultEditor))
    {
      localJFormattedTextField = ((JSpinner.DefaultEditor)paramJComponent2).getTextField();
      if (localJFormattedTextField != null) {
        localJFormattedTextField.addFocusListener(editorFocusHandler);
      }
    }
  }
  
  private void updateEditorAlignment(JComponent paramJComponent)
  {
    if ((paramJComponent instanceof JSpinner.DefaultEditor))
    {
      SynthContext localSynthContext = getContext(spinner);
      Integer localInteger = (Integer)localSynthContext.getStyle().get(localSynthContext, "Spinner.editorAlignment");
      JFormattedTextField localJFormattedTextField = ((JSpinner.DefaultEditor)paramJComponent).getTextField();
      if (localInteger != null) {
        localJFormattedTextField.setHorizontalAlignment(localInteger.intValue());
      }
      localJFormattedTextField.putClientProperty("JComponent.sizeVariant", spinner.getClientProperty("JComponent.sizeVariant"));
    }
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return getContext(paramJComponent, SynthLookAndFeel.getComponentState(paramJComponent));
  }
  
  private SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    return SynthContext.getContext(paramJComponent, style, paramInt);
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintSpinnerBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
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
    paramSynthContext.getPainter().paintSpinnerBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    JSpinner localJSpinner = (JSpinner)paramPropertyChangeEvent.getSource();
    SpinnerUI localSpinnerUI = localJSpinner.getUI();
    if ((localSpinnerUI instanceof SynthSpinnerUI))
    {
      SynthSpinnerUI localSynthSpinnerUI = (SynthSpinnerUI)localSpinnerUI;
      if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
        localSynthSpinnerUI.updateStyle(localJSpinner);
      }
    }
  }
  
  private class EditorFocusHandler
    implements FocusListener
  {
    private EditorFocusHandler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      spinner.repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      spinner.repaint();
    }
  }
  
  private static class SpinnerLayout
    implements LayoutManager, UIResource
  {
    private Component nextButton = null;
    private Component previousButton = null;
    private Component editor = null;
    
    private SpinnerLayout() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent)
    {
      if ("Next".equals(paramString)) {
        nextButton = paramComponent;
      } else if ("Previous".equals(paramString)) {
        previousButton = paramComponent;
      } else if ("Editor".equals(paramString)) {
        editor = paramComponent;
      }
    }
    
    public void removeLayoutComponent(Component paramComponent)
    {
      if (paramComponent == nextButton) {
        nextButton = null;
      } else if (paramComponent == previousButton) {
        previousButton = null;
      } else if (paramComponent == editor) {
        editor = null;
      }
    }
    
    private Dimension preferredSize(Component paramComponent)
    {
      return paramComponent == null ? new Dimension(0, 0) : paramComponent.getPreferredSize();
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      Dimension localDimension1 = preferredSize(nextButton);
      Dimension localDimension2 = preferredSize(previousButton);
      Dimension localDimension3 = preferredSize(editor);
      height = ((height + 1) / 2 * 2);
      Dimension localDimension4 = new Dimension(width, height);
      width += Math.max(width, width);
      Insets localInsets = paramContainer.getInsets();
      width += left + right;
      height += top + bottom;
      return localDimension4;
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return preferredLayoutSize(paramContainer);
    }
    
    private void setBounds(Component paramComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (paramComponent != null) {
        paramComponent.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public void layoutContainer(Container paramContainer)
    {
      Insets localInsets = paramContainer.getInsets();
      int i = paramContainer.getWidth() - (left + right);
      int j = paramContainer.getHeight() - (top + bottom);
      Dimension localDimension1 = preferredSize(nextButton);
      Dimension localDimension2 = preferredSize(previousButton);
      int k = j / 2;
      int m = j - k;
      int n = Math.max(width, width);
      int i1 = i - n;
      int i2;
      int i3;
      if (paramContainer.getComponentOrientation().isLeftToRight())
      {
        i2 = left;
        i3 = i2 + i1;
      }
      else
      {
        i3 = left;
        i2 = i3 + n;
      }
      int i4 = top + k;
      setBounds(editor, i2, top, i1, j);
      setBounds(nextButton, i3, top, n, k);
      setBounds(previousButton, i3, i4, n, m);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthSpinnerUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
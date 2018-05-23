package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.CellRendererPane;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxEditor.UIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;

public class SynthComboBoxUI
  extends BasicComboBoxUI
  implements PropertyChangeListener, SynthUI
{
  private SynthStyle style;
  private boolean useListColors;
  Insets popupInsets;
  private boolean buttonWhenNotEditable;
  private boolean pressedWhenPopupVisible;
  private ButtonHandler buttonHandler;
  private EditorFocusHandler editorFocusHandler;
  private boolean forceOpaque = false;
  
  public SynthComboBoxUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthComboBoxUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    buttonHandler = new ButtonHandler(null);
    super.installUI(paramJComponent);
  }
  
  protected void installDefaults()
  {
    updateStyle(comboBox);
  }
  
  private void updateStyle(JComboBox paramJComboBox)
  {
    SynthStyle localSynthStyle = style;
    SynthContext localSynthContext = getContext(paramJComboBox, 1);
    style = SynthLookAndFeel.updateStyle(localSynthContext, this);
    if (style != localSynthStyle)
    {
      padding = ((Insets)style.get(localSynthContext, "ComboBox.padding"));
      popupInsets = ((Insets)style.get(localSynthContext, "ComboBox.popupInsets"));
      useListColors = style.getBoolean(localSynthContext, "ComboBox.rendererUseListColors", true);
      buttonWhenNotEditable = style.getBoolean(localSynthContext, "ComboBox.buttonWhenNotEditable", false);
      pressedWhenPopupVisible = style.getBoolean(localSynthContext, "ComboBox.pressedWhenPopupVisible", false);
      squareButton = style.getBoolean(localSynthContext, "ComboBox.squareButton", true);
      if (localSynthStyle != null)
      {
        uninstallKeyboardActions();
        installKeyboardActions();
      }
      forceOpaque = style.getBoolean(localSynthContext, "ComboBox.forceOpaque", false);
    }
    localSynthContext.dispose();
    if (listBox != null) {
      SynthLookAndFeel.updateStyles(listBox);
    }
  }
  
  protected void installListeners()
  {
    comboBox.addPropertyChangeListener(this);
    comboBox.addMouseListener(buttonHandler);
    editorFocusHandler = new EditorFocusHandler(comboBox, null);
    super.installListeners();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    if ((popup instanceof SynthComboPopup)) {
      ((SynthComboPopup)popup).removePopupMenuListener(buttonHandler);
    }
    super.uninstallUI(paramJComponent);
    buttonHandler = null;
  }
  
  protected void uninstallDefaults()
  {
    SynthContext localSynthContext = getContext(comboBox, 1);
    style.uninstallDefaults(localSynthContext);
    localSynthContext.dispose();
    style = null;
  }
  
  protected void uninstallListeners()
  {
    editorFocusHandler.unregister();
    comboBox.removePropertyChangeListener(this);
    comboBox.removeMouseListener(buttonHandler);
    buttonHandler.pressed = false;
    buttonHandler.over = false;
    super.uninstallListeners();
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
    if (!(paramJComponent instanceof JComboBox)) {
      return SynthLookAndFeel.getComponentState(paramJComponent);
    }
    JComboBox localJComboBox = (JComboBox)paramJComponent;
    if (shouldActLikeButton())
    {
      i = 1;
      if (!paramJComponent.isEnabled()) {
        i = 8;
      }
      if (buttonHandler.isPressed()) {
        i |= 0x4;
      }
      if (buttonHandler.isRollover()) {
        i |= 0x2;
      }
      if (localJComboBox.isFocusOwner()) {
        i |= 0x100;
      }
      return i;
    }
    int i = SynthLookAndFeel.getComponentState(paramJComponent);
    if ((localJComboBox.isEditable()) && (localJComboBox.getEditor().getEditorComponent().isFocusOwner())) {
      i |= 0x100;
    }
    return i;
  }
  
  protected ComboPopup createPopup()
  {
    SynthComboPopup localSynthComboPopup = new SynthComboPopup(comboBox);
    localSynthComboPopup.addPopupMenuListener(buttonHandler);
    return localSynthComboPopup;
  }
  
  protected ListCellRenderer createRenderer()
  {
    return new SynthComboBoxRenderer();
  }
  
  protected ComboBoxEditor createEditor()
  {
    return new SynthComboBoxEditor(null);
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent)) {
      updateStyle(comboBox);
    }
  }
  
  protected JButton createArrowButton()
  {
    SynthArrowButton localSynthArrowButton = new SynthArrowButton(5);
    localSynthArrowButton.setName("ComboBox.arrowButton");
    localSynthArrowButton.setModel(buttonHandler);
    return localSynthArrowButton;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    SynthLookAndFeel.update(localSynthContext, paramGraphics);
    localSynthContext.getPainter().paintComboBoxBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
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
    hasFocus = comboBox.hasFocus();
    if (!comboBox.isEditable())
    {
      Rectangle localRectangle = rectangleForCurrentValue();
      paintCurrentValue(paramGraphics, localRectangle, hasFocus);
    }
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramSynthContext.getPainter().paintComboBoxBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
  {
    ListCellRenderer localListCellRenderer = comboBox.getRenderer();
    Component localComponent = localListCellRenderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
    boolean bool = false;
    if ((localComponent instanceof JPanel)) {
      bool = true;
    }
    if ((localComponent instanceof UIResource)) {
      localComponent.setName("ComboBox.renderer");
    }
    int i = (forceOpaque) && ((localComponent instanceof JComponent)) ? 1 : 0;
    if (i != 0) {
      ((JComponent)localComponent).setOpaque(false);
    }
    int j = x;
    int k = y;
    int m = width;
    int n = height;
    if (padding != null)
    {
      j = x + padding.left;
      k = y + padding.top;
      m = width - (padding.left + padding.right);
      n = height - (padding.top + padding.bottom);
    }
    currentValuePane.paintComponent(paramGraphics, localComponent, comboBox, j, k, m, n, bool);
    if (i != 0) {
      ((JComponent)localComponent).setOpaque(true);
    }
  }
  
  private boolean shouldActLikeButton()
  {
    return (buttonWhenNotEditable) && (!comboBox.isEditable());
  }
  
  protected Dimension getDefaultSize()
  {
    SynthComboBoxRenderer localSynthComboBoxRenderer = new SynthComboBoxRenderer();
    Dimension localDimension = getSizeForComponent(localSynthComboBoxRenderer.getListCellRendererComponent(listBox, " ", -1, false, false));
    return new Dimension(width, height);
  }
  
  private final class ButtonHandler
    extends DefaultButtonModel
    implements MouseListener, PopupMenuListener
  {
    private boolean over;
    private boolean pressed;
    
    private ButtonHandler() {}
    
    private void updatePressed(boolean paramBoolean)
    {
      pressed = ((paramBoolean) && (isEnabled()));
      if (SynthComboBoxUI.this.shouldActLikeButton()) {
        comboBox.repaint();
      }
    }
    
    private void updateOver(boolean paramBoolean)
    {
      boolean bool1 = isRollover();
      over = ((paramBoolean) && (isEnabled()));
      boolean bool2 = isRollover();
      if ((SynthComboBoxUI.this.shouldActLikeButton()) && (bool1 != bool2)) {
        comboBox.repaint();
      }
    }
    
    public boolean isPressed()
    {
      boolean bool = SynthComboBoxUI.this.shouldActLikeButton() ? pressed : super.isPressed();
      return (bool) || ((pressedWhenPopupVisible) && (comboBox.isPopupVisible()));
    }
    
    public boolean isArmed()
    {
      int i = (SynthComboBoxUI.this.shouldActLikeButton()) || ((pressedWhenPopupVisible) && (comboBox.isPopupVisible())) ? 1 : 0;
      return i != 0 ? isPressed() : super.isArmed();
    }
    
    public boolean isRollover()
    {
      return SynthComboBoxUI.this.shouldActLikeButton() ? over : super.isRollover();
    }
    
    public void setPressed(boolean paramBoolean)
    {
      super.setPressed(paramBoolean);
      updatePressed(paramBoolean);
    }
    
    public void setRollover(boolean paramBoolean)
    {
      super.setRollover(paramBoolean);
      updateOver(paramBoolean);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      updateOver(true);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      updateOver(false);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      updatePressed(true);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      updatePressed(false);
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void popupMenuCanceled(PopupMenuEvent paramPopupMenuEvent)
    {
      if ((SynthComboBoxUI.this.shouldActLikeButton()) || (pressedWhenPopupVisible)) {
        comboBox.repaint();
      }
    }
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent paramPopupMenuEvent) {}
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent paramPopupMenuEvent) {}
  }
  
  private static class EditorFocusHandler
    implements FocusListener, PropertyChangeListener
  {
    private JComboBox comboBox;
    private ComboBoxEditor editor = null;
    private Component editorComponent = null;
    
    private EditorFocusHandler(JComboBox paramJComboBox)
    {
      comboBox = paramJComboBox;
      editor = paramJComboBox.getEditor();
      if (editor != null)
      {
        editorComponent = editor.getEditorComponent();
        if (editorComponent != null) {
          editorComponent.addFocusListener(this);
        }
      }
      paramJComboBox.addPropertyChangeListener("editor", this);
    }
    
    public void unregister()
    {
      comboBox.removePropertyChangeListener(this);
      if (editorComponent != null) {
        editorComponent.removeFocusListener(this);
      }
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      comboBox.repaint();
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      comboBox.repaint();
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      ComboBoxEditor localComboBoxEditor = comboBox.getEditor();
      if (editor != localComboBoxEditor)
      {
        if (editorComponent != null) {
          editorComponent.removeFocusListener(this);
        }
        editor = localComboBoxEditor;
        if (editor != null)
        {
          editorComponent = editor.getEditorComponent();
          if (editorComponent != null) {
            editorComponent.addFocusListener(this);
          }
        }
      }
    }
  }
  
  private static class SynthComboBoxEditor
    extends BasicComboBoxEditor.UIResource
  {
    private SynthComboBoxEditor() {}
    
    public JTextField createEditorComponent()
    {
      JTextField localJTextField = new JTextField("", 9);
      localJTextField.setName("ComboBox.textField");
      return localJTextField;
    }
  }
  
  private class SynthComboBoxRenderer
    extends JLabel
    implements ListCellRenderer<Object>, UIResource
  {
    public SynthComboBoxRenderer()
    {
      setText(" ");
    }
    
    public String getName()
    {
      String str = super.getName();
      return str == null ? "ComboBox.renderer" : str;
    }
    
    public Component getListCellRendererComponent(JList<?> paramJList, Object paramObject, int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      setName("ComboBox.listRenderer");
      SynthLookAndFeel.resetSelectedUI();
      if (paramBoolean1)
      {
        setBackground(paramJList.getSelectionBackground());
        setForeground(paramJList.getSelectionForeground());
        if (!useListColors) {
          SynthLookAndFeel.setSelectedUI((SynthLabelUI)SynthLookAndFeel.getUIOfType(getUI(), SynthLabelUI.class), paramBoolean1, paramBoolean2, paramJList.isEnabled(), false);
        }
      }
      else
      {
        setBackground(paramJList.getBackground());
        setForeground(paramJList.getForeground());
      }
      setFont(paramJList.getFont());
      if ((paramObject instanceof Icon))
      {
        setIcon((Icon)paramObject);
        setText("");
      }
      else
      {
        String str = paramObject == null ? " " : paramObject.toString();
        if ("".equals(str)) {
          str = " ";
        }
        setText(str);
      }
      if (comboBox != null)
      {
        setEnabled(comboBox.isEnabled());
        setComponentOrientation(comboBox.getComponentOrientation());
      }
      return this;
    }
    
    public void paint(Graphics paramGraphics)
    {
      super.paint(paramGraphics);
      SynthLookAndFeel.resetSelectedUI();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\synth\SynthComboBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
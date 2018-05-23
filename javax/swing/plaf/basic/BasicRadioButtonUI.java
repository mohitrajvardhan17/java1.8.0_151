package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.HashSet;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class BasicRadioButtonUI
  extends BasicToggleButtonUI
{
  private static final Object BASIC_RADIO_BUTTON_UI_KEY = new Object();
  protected Icon icon;
  private boolean defaults_initialized = false;
  private static final String propertyPrefix = "RadioButton.";
  private KeyListener keyListener = null;
  private static Dimension size = new Dimension();
  private static Rectangle viewRect = new Rectangle();
  private static Rectangle iconRect = new Rectangle();
  private static Rectangle textRect = new Rectangle();
  private static Rectangle prefViewRect = new Rectangle();
  private static Rectangle prefIconRect = new Rectangle();
  private static Rectangle prefTextRect = new Rectangle();
  private static Insets prefInsets = new Insets(0, 0, 0, 0);
  
  public BasicRadioButtonUI() {}
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    AppContext localAppContext = AppContext.getAppContext();
    BasicRadioButtonUI localBasicRadioButtonUI = (BasicRadioButtonUI)localAppContext.get(BASIC_RADIO_BUTTON_UI_KEY);
    if (localBasicRadioButtonUI == null)
    {
      localBasicRadioButtonUI = new BasicRadioButtonUI();
      localAppContext.put(BASIC_RADIO_BUTTON_UI_KEY, localBasicRadioButtonUI);
    }
    return localBasicRadioButtonUI;
  }
  
  protected String getPropertyPrefix()
  {
    return "RadioButton.";
  }
  
  protected void installDefaults(AbstractButton paramAbstractButton)
  {
    super.installDefaults(paramAbstractButton);
    if (!defaults_initialized)
    {
      icon = UIManager.getIcon(getPropertyPrefix() + "icon");
      defaults_initialized = true;
    }
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton)
  {
    super.uninstallDefaults(paramAbstractButton);
    defaults_initialized = false;
  }
  
  public Icon getDefaultIcon()
  {
    return icon;
  }
  
  protected void installListeners(AbstractButton paramAbstractButton)
  {
    super.installListeners(paramAbstractButton);
    if (!(paramAbstractButton instanceof JRadioButton)) {
      return;
    }
    keyListener = createKeyListener();
    paramAbstractButton.addKeyListener(keyListener);
    paramAbstractButton.setFocusTraversalKeysEnabled(false);
    paramAbstractButton.getActionMap().put("Previous", new SelectPreviousBtn());
    paramAbstractButton.getActionMap().put("Next", new SelectNextBtn());
    paramAbstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("UP"), "Previous");
    paramAbstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("DOWN"), "Next");
    paramAbstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("LEFT"), "Previous");
    paramAbstractButton.getInputMap(1).put(KeyStroke.getKeyStroke("RIGHT"), "Next");
  }
  
  protected void uninstallListeners(AbstractButton paramAbstractButton)
  {
    super.uninstallListeners(paramAbstractButton);
    if (!(paramAbstractButton instanceof JRadioButton)) {
      return;
    }
    paramAbstractButton.getActionMap().remove("Previous");
    paramAbstractButton.getActionMap().remove("Next");
    paramAbstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("UP"));
    paramAbstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("DOWN"));
    paramAbstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("LEFT"));
    paramAbstractButton.getInputMap(1).remove(KeyStroke.getKeyStroke("RIGHT"));
    if (keyListener != null)
    {
      paramAbstractButton.removeKeyListener(keyListener);
      keyListener = null;
    }
  }
  
  public synchronized void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();
    Font localFont = paramJComponent.getFont();
    paramGraphics.setFont(localFont);
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, localFont);
    Insets localInsets = paramJComponent.getInsets();
    size = localAbstractButton.getSize(size);
    viewRectx = left;
    viewRecty = top;
    viewRectwidth = (sizewidth - (right + viewRectx));
    viewRectheight = (sizeheight - (bottom + viewRecty));
    iconRectx = (iconRecty = iconRectwidth = iconRectheight = 0);
    textRectx = (textRecty = textRectwidth = textRectheight = 0);
    Icon localIcon = localAbstractButton.getIcon();
    Object localObject1 = null;
    Object localObject2 = null;
    String str = SwingUtilities.layoutCompoundLabel(paramJComponent, localFontMetrics, localAbstractButton.getText(), localIcon != null ? localIcon : getDefaultIcon(), localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalTextPosition(), localAbstractButton.getHorizontalTextPosition(), viewRect, iconRect, textRect, localAbstractButton.getText() == null ? 0 : localAbstractButton.getIconTextGap());
    if (paramJComponent.isOpaque())
    {
      paramGraphics.setColor(localAbstractButton.getBackground());
      paramGraphics.fillRect(0, 0, sizewidth, sizeheight);
    }
    if (localIcon != null)
    {
      if (!localButtonModel.isEnabled())
      {
        if (localButtonModel.isSelected()) {
          localIcon = localAbstractButton.getDisabledSelectedIcon();
        } else {
          localIcon = localAbstractButton.getDisabledIcon();
        }
      }
      else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
      {
        localIcon = localAbstractButton.getPressedIcon();
        if (localIcon == null) {
          localIcon = localAbstractButton.getSelectedIcon();
        }
      }
      else if (localButtonModel.isSelected())
      {
        if ((localAbstractButton.isRolloverEnabled()) && (localButtonModel.isRollover()))
        {
          localIcon = localAbstractButton.getRolloverSelectedIcon();
          if (localIcon == null) {
            localIcon = localAbstractButton.getSelectedIcon();
          }
        }
        else
        {
          localIcon = localAbstractButton.getSelectedIcon();
        }
      }
      else if ((localAbstractButton.isRolloverEnabled()) && (localButtonModel.isRollover()))
      {
        localIcon = localAbstractButton.getRolloverIcon();
      }
      if (localIcon == null) {
        localIcon = localAbstractButton.getIcon();
      }
      localIcon.paintIcon(paramJComponent, paramGraphics, iconRectx, iconRecty);
    }
    else
    {
      getDefaultIcon().paintIcon(paramJComponent, paramGraphics, iconRectx, iconRecty);
    }
    if (str != null)
    {
      View localView = (View)paramJComponent.getClientProperty("html");
      if (localView != null) {
        localView.paint(paramGraphics, textRect);
      } else {
        paintText(paramGraphics, localAbstractButton, textRect, str);
      }
      if ((localAbstractButton.hasFocus()) && (localAbstractButton.isFocusPainted()) && (textRectwidth > 0) && (textRectheight > 0)) {
        paintFocus(paramGraphics, textRect, size);
      }
    }
  }
  
  protected void paintFocus(Graphics paramGraphics, Rectangle paramRectangle, Dimension paramDimension) {}
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    if (paramJComponent.getComponentCount() > 0) {
      return null;
    }
    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    String str = localAbstractButton.getText();
    Icon localIcon = localAbstractButton.getIcon();
    if (localIcon == null) {
      localIcon = getDefaultIcon();
    }
    Font localFont = localAbstractButton.getFont();
    FontMetrics localFontMetrics = localAbstractButton.getFontMetrics(localFont);
    prefViewRectx = (prefViewRecty = 0);
    prefViewRectwidth = 32767;
    prefViewRectheight = 32767;
    prefIconRectx = (prefIconRecty = prefIconRectwidth = prefIconRectheight = 0);
    prefTextRectx = (prefTextRecty = prefTextRectwidth = prefTextRectheight = 0);
    SwingUtilities.layoutCompoundLabel(paramJComponent, localFontMetrics, str, localIcon, localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalTextPosition(), localAbstractButton.getHorizontalTextPosition(), prefViewRect, prefIconRect, prefTextRect, str == null ? 0 : localAbstractButton.getIconTextGap());
    int i = Math.min(prefIconRectx, prefTextRectx);
    int j = Math.max(prefIconRectx + prefIconRectwidth, prefTextRectx + prefTextRectwidth);
    int k = Math.min(prefIconRecty, prefTextRecty);
    int m = Math.max(prefIconRecty + prefIconRectheight, prefTextRecty + prefTextRectheight);
    int n = j - i;
    int i1 = m - k;
    prefInsets = localAbstractButton.getInsets(prefInsets);
    n += prefInsetsleft + prefInsetsright;
    i1 += prefInsetstop + prefInsetsbottom;
    return new Dimension(n, i1);
  }
  
  private KeyListener createKeyListener()
  {
    if (keyListener == null) {
      keyListener = new KeyHandler(null);
    }
    return keyListener;
  }
  
  private boolean isValidRadioButtonObj(Object paramObject)
  {
    return ((paramObject instanceof JRadioButton)) && (((JRadioButton)paramObject).isVisible()) && (((JRadioButton)paramObject).isEnabled());
  }
  
  private void selectRadioButton(ActionEvent paramActionEvent, boolean paramBoolean)
  {
    Object localObject = paramActionEvent.getSource();
    if (!isValidRadioButtonObj(localObject)) {
      return;
    }
    ButtonGroupInfo localButtonGroupInfo = new ButtonGroupInfo((JRadioButton)localObject);
    localButtonGroupInfo.selectNewButton(paramBoolean);
  }
  
  private class ButtonGroupInfo
  {
    JRadioButton activeBtn = null;
    JRadioButton firstBtn = null;
    JRadioButton lastBtn = null;
    JRadioButton previousBtn = null;
    JRadioButton nextBtn = null;
    HashSet<JRadioButton> btnsInGroup = null;
    boolean srcFound = false;
    
    public ButtonGroupInfo(JRadioButton paramJRadioButton)
    {
      activeBtn = paramJRadioButton;
      btnsInGroup = new HashSet();
    }
    
    boolean containsInGroup(Object paramObject)
    {
      return btnsInGroup.contains(paramObject);
    }
    
    Component getFocusTransferBaseComponent(boolean paramBoolean)
    {
      JRadioButton localJRadioButton = activeBtn;
      Container localContainer = localJRadioButton.getFocusCycleRootAncestor();
      if (localContainer != null)
      {
        FocusTraversalPolicy localFocusTraversalPolicy = localContainer.getFocusTraversalPolicy();
        Component localComponent = paramBoolean ? localFocusTraversalPolicy.getComponentAfter(localContainer, activeBtn) : localFocusTraversalPolicy.getComponentBefore(localContainer, activeBtn);
        if (containsInGroup(localComponent)) {
          localJRadioButton = paramBoolean ? lastBtn : firstBtn;
        }
      }
      return localJRadioButton;
    }
    
    boolean getButtonGroupInfo()
    {
      if (activeBtn == null) {
        return false;
      }
      btnsInGroup.clear();
      ButtonModel localButtonModel = activeBtn.getModel();
      if (!(localButtonModel instanceof DefaultButtonModel)) {
        return false;
      }
      DefaultButtonModel localDefaultButtonModel = (DefaultButtonModel)localButtonModel;
      ButtonGroup localButtonGroup = localDefaultButtonModel.getGroup();
      if (localButtonGroup == null) {
        return false;
      }
      Enumeration localEnumeration = localButtonGroup.getElements();
      if (localEnumeration == null) {
        return false;
      }
      while (localEnumeration.hasMoreElements())
      {
        AbstractButton localAbstractButton = (AbstractButton)localEnumeration.nextElement();
        if (BasicRadioButtonUI.this.isValidRadioButtonObj(localAbstractButton))
        {
          btnsInGroup.add((JRadioButton)localAbstractButton);
          if (null == firstBtn) {
            firstBtn = ((JRadioButton)localAbstractButton);
          }
          if (activeBtn == localAbstractButton) {
            srcFound = true;
          } else if (!srcFound) {
            previousBtn = ((JRadioButton)localAbstractButton);
          } else if (nextBtn == null) {
            nextBtn = ((JRadioButton)localAbstractButton);
          }
          lastBtn = ((JRadioButton)localAbstractButton);
        }
      }
      return true;
    }
    
    void selectNewButton(boolean paramBoolean)
    {
      if (!getButtonGroupInfo()) {
        return;
      }
      if (srcFound)
      {
        JRadioButton localJRadioButton = null;
        if (paramBoolean) {
          localJRadioButton = null == nextBtn ? firstBtn : nextBtn;
        } else {
          localJRadioButton = null == previousBtn ? lastBtn : previousBtn;
        }
        if ((localJRadioButton != null) && (localJRadioButton != activeBtn))
        {
          localJRadioButton.requestFocusInWindow();
          localJRadioButton.setSelected(true);
        }
      }
    }
    
    void jumpToNextComponent(boolean paramBoolean)
    {
      if (!getButtonGroupInfo()) {
        if (activeBtn != null)
        {
          lastBtn = activeBtn;
          firstBtn = activeBtn;
        }
        else
        {
          return;
        }
      }
      JRadioButton localJRadioButton = activeBtn;
      Component localComponent = getFocusTransferBaseComponent(paramBoolean);
      if (localComponent != null) {
        if (paramBoolean) {
          KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent(localComponent);
        } else {
          KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent(localComponent);
        }
      }
    }
  }
  
  private class KeyHandler
    implements KeyListener
  {
    private KeyHandler() {}
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      if (paramKeyEvent.getKeyCode() == 9)
      {
        Object localObject = paramKeyEvent.getSource();
        if (BasicRadioButtonUI.this.isValidRadioButtonObj(localObject))
        {
          paramKeyEvent.consume();
          BasicRadioButtonUI.ButtonGroupInfo localButtonGroupInfo = new BasicRadioButtonUI.ButtonGroupInfo(BasicRadioButtonUI.this, (JRadioButton)localObject);
          localButtonGroupInfo.jumpToNextComponent(!paramKeyEvent.isShiftDown());
        }
      }
    }
    
    public void keyReleased(KeyEvent paramKeyEvent) {}
    
    public void keyTyped(KeyEvent paramKeyEvent) {}
  }
  
  private class SelectNextBtn
    extends AbstractAction
  {
    public SelectNextBtn()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      BasicRadioButtonUI.this.selectRadioButton(paramActionEvent, true);
    }
  }
  
  private class SelectPreviousBtn
    extends AbstractAction
  {
    public SelectPreviousBtn()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      BasicRadioButtonUI.this.selectRadioButton(paramActionEvent, false);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicRadioButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
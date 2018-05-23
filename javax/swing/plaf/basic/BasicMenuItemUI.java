package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuItemUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.View;
import sun.swing.MenuItemCheckIconFactory;
import sun.swing.MenuItemLayoutHelper;
import sun.swing.MenuItemLayoutHelper.LayoutResult;
import sun.swing.MenuItemLayoutHelper.RectSize;
import sun.swing.SwingUtilities2;
import sun.swing.UIAction;

public class BasicMenuItemUI
  extends MenuItemUI
{
  protected JMenuItem menuItem = null;
  protected Color selectionBackground;
  protected Color selectionForeground;
  protected Color disabledForeground;
  protected Color acceleratorForeground;
  protected Color acceleratorSelectionForeground;
  protected String acceleratorDelimiter;
  protected int defaultTextIconGap;
  protected Font acceleratorFont;
  protected MouseInputListener mouseInputListener;
  protected MenuDragMouseListener menuDragMouseListener;
  protected MenuKeyListener menuKeyListener;
  protected PropertyChangeListener propertyChangeListener;
  Handler handler;
  protected Icon arrowIcon = null;
  protected Icon checkIcon = null;
  protected boolean oldBorderPainted;
  private static final boolean TRACE = false;
  private static final boolean VERBOSE = false;
  private static final boolean DEBUG = false;
  
  public BasicMenuItemUI() {}
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("doClick"));
    BasicLookAndFeel.installAudioActionMap(paramLazyActionMap);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicMenuItemUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    menuItem = ((JMenuItem)paramJComponent);
    installDefaults();
    installComponents(menuItem);
    installListeners();
    installKeyboardActions();
  }
  
  protected void installDefaults()
  {
    String str = getPropertyPrefix();
    acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");
    if (acceleratorFont == null) {
      acceleratorFont = UIManager.getFont("MenuItem.font");
    }
    Object localObject = UIManager.get(getPropertyPrefix() + ".opaque");
    if (localObject != null) {
      LookAndFeel.installProperty(menuItem, "opaque", localObject);
    } else {
      LookAndFeel.installProperty(menuItem, "opaque", Boolean.TRUE);
    }
    if ((menuItem.getMargin() == null) || ((menuItem.getMargin() instanceof UIResource))) {
      menuItem.setMargin(UIManager.getInsets(str + ".margin"));
    }
    LookAndFeel.installProperty(menuItem, "iconTextGap", Integer.valueOf(4));
    defaultTextIconGap = menuItem.getIconTextGap();
    LookAndFeel.installBorder(menuItem, str + ".border");
    oldBorderPainted = menuItem.isBorderPainted();
    LookAndFeel.installProperty(menuItem, "borderPainted", Boolean.valueOf(UIManager.getBoolean(str + ".borderPainted")));
    LookAndFeel.installColorsAndFont(menuItem, str + ".background", str + ".foreground", str + ".font");
    if ((selectionBackground == null) || ((selectionBackground instanceof UIResource))) {
      selectionBackground = UIManager.getColor(str + ".selectionBackground");
    }
    if ((selectionForeground == null) || ((selectionForeground instanceof UIResource))) {
      selectionForeground = UIManager.getColor(str + ".selectionForeground");
    }
    if ((disabledForeground == null) || ((disabledForeground instanceof UIResource))) {
      disabledForeground = UIManager.getColor(str + ".disabledForeground");
    }
    if ((acceleratorForeground == null) || ((acceleratorForeground instanceof UIResource))) {
      acceleratorForeground = UIManager.getColor(str + ".acceleratorForeground");
    }
    if ((acceleratorSelectionForeground == null) || ((acceleratorSelectionForeground instanceof UIResource))) {
      acceleratorSelectionForeground = UIManager.getColor(str + ".acceleratorSelectionForeground");
    }
    acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
    if (acceleratorDelimiter == null) {
      acceleratorDelimiter = "+";
    }
    if ((arrowIcon == null) || ((arrowIcon instanceof UIResource))) {
      arrowIcon = UIManager.getIcon(str + ".arrowIcon");
    }
    if ((checkIcon == null) || ((checkIcon instanceof UIResource)))
    {
      checkIcon = UIManager.getIcon(str + ".checkIcon");
      boolean bool = MenuItemLayoutHelper.isColumnLayout(BasicGraphicsUtils.isLeftToRight(menuItem), menuItem);
      if (bool)
      {
        MenuItemCheckIconFactory localMenuItemCheckIconFactory = (MenuItemCheckIconFactory)UIManager.get(str + ".checkIconFactory");
        if ((localMenuItemCheckIconFactory != null) && (MenuItemLayoutHelper.useCheckAndArrow(menuItem)) && (localMenuItemCheckIconFactory.isCompatible(checkIcon, str))) {
          checkIcon = localMenuItemCheckIconFactory.getIcon(menuItem);
        }
      }
    }
  }
  
  protected void installComponents(JMenuItem paramJMenuItem)
  {
    BasicHTML.updateRenderer(paramJMenuItem, paramJMenuItem.getText());
  }
  
  protected String getPropertyPrefix()
  {
    return "MenuItem";
  }
  
  protected void installListeners()
  {
    if ((mouseInputListener = createMouseInputListener(menuItem)) != null)
    {
      menuItem.addMouseListener(mouseInputListener);
      menuItem.addMouseMotionListener(mouseInputListener);
    }
    if ((menuDragMouseListener = createMenuDragMouseListener(menuItem)) != null) {
      menuItem.addMenuDragMouseListener(menuDragMouseListener);
    }
    if ((menuKeyListener = createMenuKeyListener(menuItem)) != null) {
      menuItem.addMenuKeyListener(menuKeyListener);
    }
    if ((propertyChangeListener = createPropertyChangeListener(menuItem)) != null) {
      menuItem.addPropertyChangeListener(propertyChangeListener);
    }
  }
  
  protected void installKeyboardActions()
  {
    installLazyActionMap();
    updateAcceleratorBinding();
  }
  
  void installLazyActionMap()
  {
    LazyActionMap.installLazyActionMap(menuItem, BasicMenuItemUI.class, getPropertyPrefix() + ".actionMap");
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    menuItem = ((JMenuItem)paramJComponent);
    uninstallDefaults();
    uninstallComponents(menuItem);
    uninstallListeners();
    uninstallKeyboardActions();
    MenuItemLayoutHelper.clearUsedParentClientProperties(menuItem);
    menuItem = null;
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.uninstallBorder(menuItem);
    LookAndFeel.installProperty(menuItem, "borderPainted", Boolean.valueOf(oldBorderPainted));
    if ((menuItem.getMargin() instanceof UIResource)) {
      menuItem.setMargin(null);
    }
    if ((arrowIcon instanceof UIResource)) {
      arrowIcon = null;
    }
    if ((checkIcon instanceof UIResource)) {
      checkIcon = null;
    }
  }
  
  protected void uninstallComponents(JMenuItem paramJMenuItem)
  {
    BasicHTML.updateRenderer(paramJMenuItem, "");
  }
  
  protected void uninstallListeners()
  {
    if (mouseInputListener != null)
    {
      menuItem.removeMouseListener(mouseInputListener);
      menuItem.removeMouseMotionListener(mouseInputListener);
    }
    if (menuDragMouseListener != null) {
      menuItem.removeMenuDragMouseListener(menuDragMouseListener);
    }
    if (menuKeyListener != null) {
      menuItem.removeMenuKeyListener(menuKeyListener);
    }
    if (propertyChangeListener != null) {
      menuItem.removePropertyChangeListener(propertyChangeListener);
    }
    mouseInputListener = null;
    menuDragMouseListener = null;
    menuKeyListener = null;
    propertyChangeListener = null;
    handler = null;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIActionMap(menuItem, null);
    SwingUtilities.replaceUIInputMap(menuItem, 2, null);
  }
  
  protected MouseInputListener createMouseInputListener(JComponent paramJComponent)
  {
    return getHandler();
  }
  
  protected MenuDragMouseListener createMenuDragMouseListener(JComponent paramJComponent)
  {
    return getHandler();
  }
  
  protected MenuKeyListener createMenuKeyListener(JComponent paramJComponent)
  {
    return null;
  }
  
  protected PropertyChangeListener createPropertyChangeListener(JComponent paramJComponent)
  {
    return getHandler();
  }
  
  Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler();
    }
    return handler;
  }
  
  InputMap createInputMap(int paramInt)
  {
    if (paramInt == 2) {
      return new ComponentInputMapUIResource(menuItem);
    }
    return null;
  }
  
  void updateAcceleratorBinding()
  {
    KeyStroke localKeyStroke = menuItem.getAccelerator();
    InputMap localInputMap = SwingUtilities.getUIInputMap(menuItem, 2);
    if (localInputMap != null) {
      localInputMap.clear();
    }
    if (localKeyStroke != null)
    {
      if (localInputMap == null)
      {
        localInputMap = createInputMap(2);
        SwingUtilities.replaceUIInputMap(menuItem, 2, localInputMap);
      }
      localInputMap.put(localKeyStroke, "doClick");
    }
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    Dimension localDimension = null;
    View localView = (View)paramJComponent.getClientProperty("html");
    if (localView != null)
    {
      localDimension = getPreferredSize(paramJComponent);
      Dimension tmp23_22 = localDimension;
      2322width = ((int)(2322width - (localView.getPreferredSpan(0) - localView.getMinimumSpan(0))));
    }
    return localDimension;
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return getPreferredMenuItemSize(paramJComponent, checkIcon, arrowIcon, defaultTextIconGap);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    Dimension localDimension = null;
    View localView = (View)paramJComponent.getClientProperty("html");
    if (localView != null)
    {
      localDimension = getPreferredSize(paramJComponent);
      Dimension tmp23_22 = localDimension;
      2322width = ((int)(2322width + (localView.getMaximumSpan(0) - localView.getPreferredSpan(0))));
    }
    return localDimension;
  }
  
  protected Dimension getPreferredMenuItemSize(JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, int paramInt)
  {
    JMenuItem localJMenuItem = (JMenuItem)paramJComponent;
    MenuItemLayoutHelper localMenuItemLayoutHelper = new MenuItemLayoutHelper(localJMenuItem, paramIcon1, paramIcon2, MenuItemLayoutHelper.createMaxRect(), paramInt, acceleratorDelimiter, BasicGraphicsUtils.isLeftToRight(localJMenuItem), localJMenuItem.getFont(), acceleratorFont, MenuItemLayoutHelper.useCheckAndArrow(menuItem), getPropertyPrefix());
    Dimension localDimension = new Dimension();
    width = localMenuItemLayoutHelper.getLeadingGap();
    MenuItemLayoutHelper.addMaxWidth(localMenuItemLayoutHelper.getCheckSize(), localMenuItemLayoutHelper.getAfterCheckIconGap(), localDimension);
    if ((!localMenuItemLayoutHelper.isTopLevelMenu()) && (localMenuItemLayoutHelper.getMinTextOffset() > 0) && (width < localMenuItemLayoutHelper.getMinTextOffset())) {
      width = localMenuItemLayoutHelper.getMinTextOffset();
    }
    MenuItemLayoutHelper.addMaxWidth(localMenuItemLayoutHelper.getLabelSize(), localMenuItemLayoutHelper.getGap(), localDimension);
    MenuItemLayoutHelper.addMaxWidth(localMenuItemLayoutHelper.getAccSize(), localMenuItemLayoutHelper.getGap(), localDimension);
    MenuItemLayoutHelper.addMaxWidth(localMenuItemLayoutHelper.getArrowSize(), localMenuItemLayoutHelper.getGap(), localDimension);
    height = MenuItemLayoutHelper.max(new int[] { localMenuItemLayoutHelper.getCheckSize().getHeight(), localMenuItemLayoutHelper.getLabelSize().getHeight(), localMenuItemLayoutHelper.getAccSize().getHeight(), localMenuItemLayoutHelper.getArrowSize().getHeight() });
    Insets localInsets = localMenuItemLayoutHelper.getMenuItem().getInsets();
    if (localInsets != null)
    {
      width += left + right;
      height += top + bottom;
    }
    if (width % 2 == 0) {
      width += 1;
    }
    if ((height % 2 == 0) && (Boolean.TRUE != UIManager.get(getPropertyPrefix() + ".evenHeight"))) {
      height += 1;
    }
    return localDimension;
  }
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    paint(paramGraphics, paramJComponent);
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    paintMenuItem(paramGraphics, paramJComponent, checkIcon, arrowIcon, selectionBackground, selectionForeground, defaultTextIconGap);
  }
  
  protected void paintMenuItem(Graphics paramGraphics, JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, Color paramColor1, Color paramColor2, int paramInt)
  {
    Font localFont = paramGraphics.getFont();
    Color localColor = paramGraphics.getColor();
    JMenuItem localJMenuItem = (JMenuItem)paramJComponent;
    paramGraphics.setFont(localJMenuItem.getFont());
    Rectangle localRectangle = new Rectangle(0, 0, localJMenuItem.getWidth(), localJMenuItem.getHeight());
    applyInsets(localRectangle, localJMenuItem.getInsets());
    MenuItemLayoutHelper localMenuItemLayoutHelper = new MenuItemLayoutHelper(localJMenuItem, paramIcon1, paramIcon2, localRectangle, paramInt, acceleratorDelimiter, BasicGraphicsUtils.isLeftToRight(localJMenuItem), localJMenuItem.getFont(), acceleratorFont, MenuItemLayoutHelper.useCheckAndArrow(menuItem), getPropertyPrefix());
    MenuItemLayoutHelper.LayoutResult localLayoutResult = localMenuItemLayoutHelper.layoutMenuItem();
    paintBackground(paramGraphics, localJMenuItem, paramColor1);
    paintCheckIcon(paramGraphics, localMenuItemLayoutHelper, localLayoutResult, localColor, paramColor2);
    paintIcon(paramGraphics, localMenuItemLayoutHelper, localLayoutResult, localColor);
    paintText(paramGraphics, localMenuItemLayoutHelper, localLayoutResult);
    paintAccText(paramGraphics, localMenuItemLayoutHelper, localLayoutResult);
    paintArrowIcon(paramGraphics, localMenuItemLayoutHelper, localLayoutResult, paramColor2);
    paramGraphics.setColor(localColor);
    paramGraphics.setFont(localFont);
  }
  
  private void paintIcon(Graphics paramGraphics, MenuItemLayoutHelper paramMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult, Color paramColor)
  {
    if (paramMenuItemLayoutHelper.getIcon() != null)
    {
      ButtonModel localButtonModel = paramMenuItemLayoutHelper.getMenuItem().getModel();
      Icon localIcon;
      if (!localButtonModel.isEnabled())
      {
        localIcon = paramMenuItemLayoutHelper.getMenuItem().getDisabledIcon();
      }
      else if ((localButtonModel.isPressed()) && (localButtonModel.isArmed()))
      {
        localIcon = paramMenuItemLayoutHelper.getMenuItem().getPressedIcon();
        if (localIcon == null) {
          localIcon = paramMenuItemLayoutHelper.getMenuItem().getIcon();
        }
      }
      else
      {
        localIcon = paramMenuItemLayoutHelper.getMenuItem().getIcon();
      }
      if (localIcon != null)
      {
        localIcon.paintIcon(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, getIconRectx, getIconRecty);
        paramGraphics.setColor(paramColor);
      }
    }
  }
  
  private void paintCheckIcon(Graphics paramGraphics, MenuItemLayoutHelper paramMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult, Color paramColor1, Color paramColor2)
  {
    if (paramMenuItemLayoutHelper.getCheckIcon() != null)
    {
      ButtonModel localButtonModel = paramMenuItemLayoutHelper.getMenuItem().getModel();
      if ((localButtonModel.isArmed()) || (((paramMenuItemLayoutHelper.getMenuItem() instanceof JMenu)) && (localButtonModel.isSelected()))) {
        paramGraphics.setColor(paramColor2);
      } else {
        paramGraphics.setColor(paramColor1);
      }
      if (paramMenuItemLayoutHelper.useCheckAndArrow()) {
        paramMenuItemLayoutHelper.getCheckIcon().paintIcon(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, getCheckRectx, getCheckRecty);
      }
      paramGraphics.setColor(paramColor1);
    }
  }
  
  private void paintAccText(Graphics paramGraphics, MenuItemLayoutHelper paramMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult)
  {
    if (!paramMenuItemLayoutHelper.getAccText().equals(""))
    {
      ButtonModel localButtonModel = paramMenuItemLayoutHelper.getMenuItem().getModel();
      paramGraphics.setFont(paramMenuItemLayoutHelper.getAccFontMetrics().getFont());
      if (!localButtonModel.isEnabled())
      {
        if (disabledForeground != null)
        {
          paramGraphics.setColor(disabledForeground);
          SwingUtilities2.drawString(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, paramMenuItemLayoutHelper.getAccText(), getAccRectx, getAccRecty + paramMenuItemLayoutHelper.getAccFontMetrics().getAscent());
        }
        else
        {
          paramGraphics.setColor(paramMenuItemLayoutHelper.getMenuItem().getBackground().brighter());
          SwingUtilities2.drawString(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, paramMenuItemLayoutHelper.getAccText(), getAccRectx, getAccRecty + paramMenuItemLayoutHelper.getAccFontMetrics().getAscent());
          paramGraphics.setColor(paramMenuItemLayoutHelper.getMenuItem().getBackground().darker());
          SwingUtilities2.drawString(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, paramMenuItemLayoutHelper.getAccText(), getAccRectx - 1, getAccRecty + paramMenuItemLayoutHelper.getFontMetrics().getAscent() - 1);
        }
      }
      else
      {
        if ((localButtonModel.isArmed()) || (((paramMenuItemLayoutHelper.getMenuItem() instanceof JMenu)) && (localButtonModel.isSelected()))) {
          paramGraphics.setColor(acceleratorSelectionForeground);
        } else {
          paramGraphics.setColor(acceleratorForeground);
        }
        SwingUtilities2.drawString(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, paramMenuItemLayoutHelper.getAccText(), getAccRectx, getAccRecty + paramMenuItemLayoutHelper.getAccFontMetrics().getAscent());
      }
    }
  }
  
  private void paintText(Graphics paramGraphics, MenuItemLayoutHelper paramMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult)
  {
    if (!paramMenuItemLayoutHelper.getText().equals("")) {
      if (paramMenuItemLayoutHelper.getHtmlView() != null) {
        paramMenuItemLayoutHelper.getHtmlView().paint(paramGraphics, paramLayoutResult.getTextRect());
      } else {
        paintText(paramGraphics, paramMenuItemLayoutHelper.getMenuItem(), paramLayoutResult.getTextRect(), paramMenuItemLayoutHelper.getText());
      }
    }
  }
  
  private void paintArrowIcon(Graphics paramGraphics, MenuItemLayoutHelper paramMenuItemLayoutHelper, MenuItemLayoutHelper.LayoutResult paramLayoutResult, Color paramColor)
  {
    if (paramMenuItemLayoutHelper.getArrowIcon() != null)
    {
      ButtonModel localButtonModel = paramMenuItemLayoutHelper.getMenuItem().getModel();
      if ((localButtonModel.isArmed()) || (((paramMenuItemLayoutHelper.getMenuItem() instanceof JMenu)) && (localButtonModel.isSelected()))) {
        paramGraphics.setColor(paramColor);
      }
      if (paramMenuItemLayoutHelper.useCheckAndArrow()) {
        paramMenuItemLayoutHelper.getArrowIcon().paintIcon(paramMenuItemLayoutHelper.getMenuItem(), paramGraphics, getArrowRectx, getArrowRecty);
      }
    }
  }
  
  private void applyInsets(Rectangle paramRectangle, Insets paramInsets)
  {
    if (paramInsets != null)
    {
      x += left;
      y += top;
      width -= right + x;
      height -= bottom + y;
    }
  }
  
  protected void paintBackground(Graphics paramGraphics, JMenuItem paramJMenuItem, Color paramColor)
  {
    ButtonModel localButtonModel = paramJMenuItem.getModel();
    Color localColor = paramGraphics.getColor();
    int i = paramJMenuItem.getWidth();
    int j = paramJMenuItem.getHeight();
    if (paramJMenuItem.isOpaque())
    {
      if ((localButtonModel.isArmed()) || (((paramJMenuItem instanceof JMenu)) && (localButtonModel.isSelected())))
      {
        paramGraphics.setColor(paramColor);
        paramGraphics.fillRect(0, 0, i, j);
      }
      else
      {
        paramGraphics.setColor(paramJMenuItem.getBackground());
        paramGraphics.fillRect(0, 0, i, j);
      }
      paramGraphics.setColor(localColor);
    }
    else if ((localButtonModel.isArmed()) || (((paramJMenuItem instanceof JMenu)) && (localButtonModel.isSelected())))
    {
      paramGraphics.setColor(paramColor);
      paramGraphics.fillRect(0, 0, i, j);
      paramGraphics.setColor(localColor);
    }
  }
  
  protected void paintText(Graphics paramGraphics, JMenuItem paramJMenuItem, Rectangle paramRectangle, String paramString)
  {
    ButtonModel localButtonModel = paramJMenuItem.getModel();
    FontMetrics localFontMetrics = SwingUtilities2.getFontMetrics(paramJMenuItem, paramGraphics);
    int i = paramJMenuItem.getDisplayedMnemonicIndex();
    if (!localButtonModel.isEnabled())
    {
      if ((UIManager.get("MenuItem.disabledForeground") instanceof Color))
      {
        paramGraphics.setColor(UIManager.getColor("MenuItem.disabledForeground"));
        SwingUtilities2.drawStringUnderlineCharAt(paramJMenuItem, paramGraphics, paramString, i, x, y + localFontMetrics.getAscent());
      }
      else
      {
        paramGraphics.setColor(paramJMenuItem.getBackground().brighter());
        SwingUtilities2.drawStringUnderlineCharAt(paramJMenuItem, paramGraphics, paramString, i, x, y + localFontMetrics.getAscent());
        paramGraphics.setColor(paramJMenuItem.getBackground().darker());
        SwingUtilities2.drawStringUnderlineCharAt(paramJMenuItem, paramGraphics, paramString, i, x - 1, y + localFontMetrics.getAscent() - 1);
      }
    }
    else
    {
      if ((localButtonModel.isArmed()) || (((paramJMenuItem instanceof JMenu)) && (localButtonModel.isSelected()))) {
        paramGraphics.setColor(selectionForeground);
      }
      SwingUtilities2.drawStringUnderlineCharAt(paramJMenuItem, paramGraphics, paramString, i, x, y + localFontMetrics.getAscent());
    }
  }
  
  public MenuElement[] getPath()
  {
    MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
    MenuElement[] arrayOfMenuElement1 = localMenuSelectionManager.getSelectedPath();
    int i = arrayOfMenuElement1.length;
    if (i == 0) {
      return new MenuElement[0];
    }
    Container localContainer = menuItem.getParent();
    MenuElement[] arrayOfMenuElement2;
    if (arrayOfMenuElement1[(i - 1)].getComponent() == localContainer)
    {
      arrayOfMenuElement2 = new MenuElement[i + 1];
      System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement2, 0, i);
      arrayOfMenuElement2[i] = menuItem;
    }
    else
    {
      for (int j = arrayOfMenuElement1.length - 1; (j >= 0) && (arrayOfMenuElement1[j].getComponent() != localContainer); j--) {}
      arrayOfMenuElement2 = new MenuElement[j + 2];
      System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement2, 0, j + 1);
      arrayOfMenuElement2[(j + 1)] = menuItem;
    }
    return arrayOfMenuElement2;
  }
  
  void printMenuElementArray(MenuElement[] paramArrayOfMenuElement, boolean paramBoolean)
  {
    System.out.println("Path is(");
    int i = 0;
    int j = paramArrayOfMenuElement.length;
    while (i < j)
    {
      for (int k = 0; k <= i; k++) {
        System.out.print("  ");
      }
      MenuElement localMenuElement = paramArrayOfMenuElement[i];
      if ((localMenuElement instanceof JMenuItem)) {
        System.out.println(((JMenuItem)localMenuElement).getText() + ", ");
      } else if (localMenuElement == null) {
        System.out.println("NULL , ");
      } else {
        System.out.println("" + localMenuElement + ", ");
      }
      i++;
    }
    System.out.println(")");
    if (paramBoolean == true) {
      Thread.dumpStack();
    }
  }
  
  protected void doClick(MenuSelectionManager paramMenuSelectionManager)
  {
    if (!isInternalFrameSystemMenu()) {
      BasicLookAndFeel.playSound(menuItem, getPropertyPrefix() + ".commandSound");
    }
    if (paramMenuSelectionManager == null) {
      paramMenuSelectionManager = MenuSelectionManager.defaultManager();
    }
    paramMenuSelectionManager.clearSelectedPath();
    menuItem.doClick(0);
  }
  
  private boolean isInternalFrameSystemMenu()
  {
    String str = menuItem.getActionCommand();
    return (str == "Close") || (str == "Minimize") || (str == "Restore") || (str == "Maximize");
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String CLICK = "doClick";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JMenuItem localJMenuItem = (JMenuItem)paramActionEvent.getSource();
      MenuSelectionManager.defaultManager().clearSelectedPath();
      localJMenuItem.doClick();
    }
  }
  
  class Handler
    implements MenuDragMouseListener, MouseInputListener, PropertyChangeListener
  {
    Handler() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mousePressed(MouseEvent paramMouseEvent) {}
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      if (!menuItem.isEnabled()) {
        return;
      }
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      Point localPoint = paramMouseEvent.getPoint();
      if ((x >= 0) && (x < menuItem.getWidth()) && (y >= 0) && (y < menuItem.getHeight())) {
        doClick(localMenuSelectionManager);
      } else {
        localMenuSelectionManager.processMouseEvent(paramMouseEvent);
      }
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      int i = paramMouseEvent.getModifiers();
      if ((i & 0x1C) != 0) {
        MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
      } else {
        localMenuSelectionManager.setSelectedPath(getPath());
      }
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      MenuSelectionManager localMenuSelectionManager = MenuSelectionManager.defaultManager();
      int i = paramMouseEvent.getModifiers();
      if ((i & 0x1C) != 0)
      {
        MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
      }
      else
      {
        MenuElement[] arrayOfMenuElement1 = localMenuSelectionManager.getSelectedPath();
        if ((arrayOfMenuElement1.length > 1) && (arrayOfMenuElement1[(arrayOfMenuElement1.length - 1)] == menuItem))
        {
          MenuElement[] arrayOfMenuElement2 = new MenuElement[arrayOfMenuElement1.length - 1];
          int j = 0;
          int k = arrayOfMenuElement1.length - 1;
          while (j < k)
          {
            arrayOfMenuElement2[j] = arrayOfMenuElement1[j];
            j++;
          }
          localMenuSelectionManager.setSelectedPath(arrayOfMenuElement2);
        }
      }
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent);
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent) {}
    
    public void menuDragMouseEntered(MenuDragMouseEvent paramMenuDragMouseEvent)
    {
      MenuSelectionManager localMenuSelectionManager = paramMenuDragMouseEvent.getMenuSelectionManager();
      MenuElement[] arrayOfMenuElement = paramMenuDragMouseEvent.getPath();
      localMenuSelectionManager.setSelectedPath(arrayOfMenuElement);
    }
    
    public void menuDragMouseDragged(MenuDragMouseEvent paramMenuDragMouseEvent)
    {
      MenuSelectionManager localMenuSelectionManager = paramMenuDragMouseEvent.getMenuSelectionManager();
      MenuElement[] arrayOfMenuElement = paramMenuDragMouseEvent.getPath();
      localMenuSelectionManager.setSelectedPath(arrayOfMenuElement);
    }
    
    public void menuDragMouseExited(MenuDragMouseEvent paramMenuDragMouseEvent) {}
    
    public void menuDragMouseReleased(MenuDragMouseEvent paramMenuDragMouseEvent)
    {
      if (!menuItem.isEnabled()) {
        return;
      }
      MenuSelectionManager localMenuSelectionManager = paramMenuDragMouseEvent.getMenuSelectionManager();
      MenuElement[] arrayOfMenuElement = paramMenuDragMouseEvent.getPath();
      Point localPoint = paramMenuDragMouseEvent.getPoint();
      if ((x >= 0) && (x < menuItem.getWidth()) && (y >= 0) && (y < menuItem.getHeight())) {
        doClick(localMenuSelectionManager);
      } else {
        localMenuSelectionManager.clearSelectedPath();
      }
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str1 = paramPropertyChangeEvent.getPropertyName();
      if ((str1 == "labelFor") || (str1 == "displayedMnemonic") || (str1 == "accelerator"))
      {
        updateAcceleratorBinding();
      }
      else if ((str1 == "text") || ("font" == str1) || ("foreground" == str1))
      {
        JMenuItem localJMenuItem = (JMenuItem)paramPropertyChangeEvent.getSource();
        String str2 = localJMenuItem.getText();
        BasicHTML.updateRenderer(localJMenuItem, str2);
      }
      else if (str1 == "iconTextGap")
      {
        defaultTextIconGap = ((Number)paramPropertyChangeEvent.getNewValue()).intValue();
      }
    }
  }
  
  protected class MouseInputHandler
    implements MouseInputListener
  {
    protected MouseInputHandler() {}
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      getHandler().mouseClicked(paramMouseEvent);
    }
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      getHandler().mousePressed(paramMouseEvent);
    }
    
    public void mouseReleased(MouseEvent paramMouseEvent)
    {
      getHandler().mouseReleased(paramMouseEvent);
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent)
    {
      getHandler().mouseEntered(paramMouseEvent);
    }
    
    public void mouseExited(MouseEvent paramMouseEvent)
    {
      getHandler().mouseExited(paramMouseEvent);
    }
    
    public void mouseDragged(MouseEvent paramMouseEvent)
    {
      getHandler().mouseDragged(paramMouseEvent);
    }
    
    public void mouseMoved(MouseEvent paramMouseEvent)
    {
      getHandler().mouseMoved(paramMouseEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicMenuItemUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
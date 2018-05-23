package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.CellRendererPane;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.Position.Bias;
import sun.awt.AppContext;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicComboBoxUI
  extends ComboBoxUI
{
  protected JComboBox comboBox;
  protected boolean hasFocus = false;
  private boolean isTableCellEditor = false;
  private static final String IS_TABLE_CELL_EDITOR = "JComboBox.isTableCellEditor";
  protected JList listBox;
  protected CellRendererPane currentValuePane = new CellRendererPane();
  protected ComboPopup popup;
  protected Component editor;
  protected JButton arrowButton;
  protected KeyListener keyListener;
  protected FocusListener focusListener;
  protected PropertyChangeListener propertyChangeListener;
  protected ItemListener itemListener;
  protected MouseListener popupMouseListener;
  protected MouseMotionListener popupMouseMotionListener;
  protected KeyListener popupKeyListener;
  protected ListDataListener listDataListener;
  private Handler handler;
  private long timeFactor = 1000L;
  private long lastTime = 0L;
  private long time = 0L;
  JComboBox.KeySelectionManager keySelectionManager;
  protected boolean isMinimumSizeDirty = true;
  protected Dimension cachedMinimumSize = new Dimension(0, 0);
  private boolean isDisplaySizeDirty = true;
  private Dimension cachedDisplaySize = new Dimension(0, 0);
  private static final Object COMBO_UI_LIST_CELL_RENDERER_KEY = new StringBuffer("DefaultListCellRendererKey");
  static final StringBuffer HIDE_POPUP_KEY = new StringBuffer("HidePopupKey");
  private boolean sameBaseline;
  protected boolean squareButton = true;
  protected Insets padding;
  
  public BasicComboBoxUI() {}
  
  private static ListCellRenderer getDefaultListCellRenderer()
  {
    Object localObject = (ListCellRenderer)AppContext.getAppContext().get(COMBO_UI_LIST_CELL_RENDERER_KEY);
    if (localObject == null)
    {
      localObject = new DefaultListCellRenderer();
      AppContext.getAppContext().put(COMBO_UI_LIST_CELL_RENDERER_KEY, new DefaultListCellRenderer());
    }
    return (ListCellRenderer)localObject;
  }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("hidePopup"));
    paramLazyActionMap.put(new Actions("pageDownPassThrough"));
    paramLazyActionMap.put(new Actions("pageUpPassThrough"));
    paramLazyActionMap.put(new Actions("homePassThrough"));
    paramLazyActionMap.put(new Actions("endPassThrough"));
    paramLazyActionMap.put(new Actions("selectNext"));
    paramLazyActionMap.put(new Actions("selectNext2"));
    paramLazyActionMap.put(new Actions("togglePopup"));
    paramLazyActionMap.put(new Actions("spacePopup"));
    paramLazyActionMap.put(new Actions("selectPrevious"));
    paramLazyActionMap.put(new Actions("selectPrevious2"));
    paramLazyActionMap.put(new Actions("enterPressed"));
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicComboBoxUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    isMinimumSizeDirty = true;
    comboBox = ((JComboBox)paramJComponent);
    installDefaults();
    popup = createPopup();
    listBox = popup.getList();
    Boolean localBoolean = (Boolean)paramJComponent.getClientProperty("JComboBox.isTableCellEditor");
    if (localBoolean != null) {
      isTableCellEditor = (localBoolean.equals(Boolean.TRUE));
    }
    if ((comboBox.getRenderer() == null) || ((comboBox.getRenderer() instanceof UIResource))) {
      comboBox.setRenderer(createRenderer());
    }
    if ((comboBox.getEditor() == null) || ((comboBox.getEditor() instanceof UIResource))) {
      comboBox.setEditor(createEditor());
    }
    installListeners();
    installComponents();
    comboBox.setLayout(createLayoutManager());
    comboBox.setRequestFocusEnabled(true);
    installKeyboardActions();
    comboBox.putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
    if ((keySelectionManager == null) || ((keySelectionManager instanceof UIResource))) {
      keySelectionManager = new DefaultKeySelectionManager();
    }
    comboBox.setKeySelectionManager(keySelectionManager);
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    setPopupVisible(comboBox, false);
    popup.uninstallingUI();
    uninstallKeyboardActions();
    comboBox.setLayout(null);
    uninstallComponents();
    uninstallListeners();
    uninstallDefaults();
    if ((comboBox.getRenderer() == null) || ((comboBox.getRenderer() instanceof UIResource))) {
      comboBox.setRenderer(null);
    }
    ComboBoxEditor localComboBoxEditor = comboBox.getEditor();
    if ((localComboBoxEditor instanceof UIResource))
    {
      if (localComboBoxEditor.getEditorComponent().hasFocus()) {
        comboBox.requestFocusInWindow();
      }
      comboBox.setEditor(null);
    }
    if ((keySelectionManager instanceof UIResource)) {
      comboBox.setKeySelectionManager(null);
    }
    handler = null;
    keyListener = null;
    focusListener = null;
    listDataListener = null;
    propertyChangeListener = null;
    popup = null;
    listBox = null;
    comboBox = null;
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installColorsAndFont(comboBox, "ComboBox.background", "ComboBox.foreground", "ComboBox.font");
    LookAndFeel.installBorder(comboBox, "ComboBox.border");
    LookAndFeel.installProperty(comboBox, "opaque", Boolean.TRUE);
    Long localLong = (Long)UIManager.get("ComboBox.timeFactor");
    timeFactor = (localLong == null ? 1000L : localLong.longValue());
    Boolean localBoolean = (Boolean)UIManager.get("ComboBox.squareButton");
    squareButton = (localBoolean == null ? true : localBoolean.booleanValue());
    padding = UIManager.getInsets("ComboBox.padding");
  }
  
  protected void installListeners()
  {
    if ((itemListener = createItemListener()) != null) {
      comboBox.addItemListener(itemListener);
    }
    if ((propertyChangeListener = createPropertyChangeListener()) != null) {
      comboBox.addPropertyChangeListener(propertyChangeListener);
    }
    if ((keyListener = createKeyListener()) != null) {
      comboBox.addKeyListener(keyListener);
    }
    if ((focusListener = createFocusListener()) != null) {
      comboBox.addFocusListener(focusListener);
    }
    if ((popupMouseListener = popup.getMouseListener()) != null) {
      comboBox.addMouseListener(popupMouseListener);
    }
    if ((popupMouseMotionListener = popup.getMouseMotionListener()) != null) {
      comboBox.addMouseMotionListener(popupMouseMotionListener);
    }
    if ((popupKeyListener = popup.getKeyListener()) != null) {
      comboBox.addKeyListener(popupKeyListener);
    }
    if ((comboBox.getModel() != null) && ((listDataListener = createListDataListener()) != null)) {
      comboBox.getModel().addListDataListener(listDataListener);
    }
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.installColorsAndFont(comboBox, "ComboBox.background", "ComboBox.foreground", "ComboBox.font");
    LookAndFeel.uninstallBorder(comboBox);
  }
  
  protected void uninstallListeners()
  {
    if (keyListener != null) {
      comboBox.removeKeyListener(keyListener);
    }
    if (itemListener != null) {
      comboBox.removeItemListener(itemListener);
    }
    if (propertyChangeListener != null) {
      comboBox.removePropertyChangeListener(propertyChangeListener);
    }
    if (focusListener != null) {
      comboBox.removeFocusListener(focusListener);
    }
    if (popupMouseListener != null) {
      comboBox.removeMouseListener(popupMouseListener);
    }
    if (popupMouseMotionListener != null) {
      comboBox.removeMouseMotionListener(popupMouseMotionListener);
    }
    if (popupKeyListener != null) {
      comboBox.removeKeyListener(popupKeyListener);
    }
    if ((comboBox.getModel() != null) && (listDataListener != null)) {
      comboBox.getModel().removeListDataListener(listDataListener);
    }
  }
  
  protected ComboPopup createPopup()
  {
    return new BasicComboPopup(comboBox);
  }
  
  protected KeyListener createKeyListener()
  {
    return getHandler();
  }
  
  protected FocusListener createFocusListener()
  {
    return getHandler();
  }
  
  protected ListDataListener createListDataListener()
  {
    return getHandler();
  }
  
  protected ItemListener createItemListener()
  {
    return null;
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  protected LayoutManager createLayoutManager()
  {
    return getHandler();
  }
  
  protected ListCellRenderer createRenderer()
  {
    return new BasicComboBoxRenderer.UIResource();
  }
  
  protected ComboBoxEditor createEditor()
  {
    return new BasicComboBoxEditor.UIResource();
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  private void updateToolTipTextForChildren()
  {
    Component[] arrayOfComponent = comboBox.getComponents();
    for (int i = 0; i < arrayOfComponent.length; i++) {
      if ((arrayOfComponent[i] instanceof JComponent)) {
        ((JComponent)arrayOfComponent[i]).setToolTipText(comboBox.getToolTipText());
      }
    }
  }
  
  protected void installComponents()
  {
    arrowButton = createArrowButton();
    if (arrowButton != null)
    {
      comboBox.add(arrowButton);
      configureArrowButton();
    }
    if (comboBox.isEditable()) {
      addEditor();
    }
    comboBox.add(currentValuePane);
  }
  
  protected void uninstallComponents()
  {
    if (arrowButton != null) {
      unconfigureArrowButton();
    }
    if (editor != null) {
      unconfigureEditor();
    }
    comboBox.removeAll();
    arrowButton = null;
  }
  
  public void addEditor()
  {
    removeEditor();
    editor = comboBox.getEditor().getEditorComponent();
    if (editor != null)
    {
      configureEditor();
      comboBox.add(editor);
      if (comboBox.isFocusOwner()) {
        editor.requestFocusInWindow();
      }
    }
  }
  
  public void removeEditor()
  {
    if (editor != null)
    {
      unconfigureEditor();
      comboBox.remove(editor);
      editor = null;
    }
  }
  
  protected void configureEditor()
  {
    editor.setEnabled(comboBox.isEnabled());
    editor.setFocusable(comboBox.isFocusable());
    editor.setFont(comboBox.getFont());
    if (focusListener != null) {
      editor.addFocusListener(focusListener);
    }
    editor.addFocusListener(getHandler());
    comboBox.getEditor().addActionListener(getHandler());
    if ((editor instanceof JComponent))
    {
      ((JComponent)editor).putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
      ((JComponent)editor).setInheritsPopupMenu(true);
    }
    comboBox.configureEditor(comboBox.getEditor(), comboBox.getSelectedItem());
    editor.addPropertyChangeListener(propertyChangeListener);
  }
  
  protected void unconfigureEditor()
  {
    if (focusListener != null) {
      editor.removeFocusListener(focusListener);
    }
    editor.removePropertyChangeListener(propertyChangeListener);
    editor.removeFocusListener(getHandler());
    comboBox.getEditor().removeActionListener(getHandler());
  }
  
  public void configureArrowButton()
  {
    if (arrowButton != null)
    {
      arrowButton.setEnabled(comboBox.isEnabled());
      arrowButton.setFocusable(comboBox.isFocusable());
      arrowButton.setRequestFocusEnabled(false);
      arrowButton.addMouseListener(popup.getMouseListener());
      arrowButton.addMouseMotionListener(popup.getMouseMotionListener());
      arrowButton.resetKeyboardActions();
      arrowButton.putClientProperty("doNotCancelPopup", HIDE_POPUP_KEY);
      arrowButton.setInheritsPopupMenu(true);
    }
  }
  
  public void unconfigureArrowButton()
  {
    if (arrowButton != null)
    {
      arrowButton.removeMouseListener(popup.getMouseListener());
      arrowButton.removeMouseMotionListener(popup.getMouseMotionListener());
    }
  }
  
  protected JButton createArrowButton()
  {
    BasicArrowButton localBasicArrowButton = new BasicArrowButton(5, UIManager.getColor("ComboBox.buttonBackground"), UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"), UIManager.getColor("ComboBox.buttonHighlight"));
    localBasicArrowButton.setName("ComboBox.arrowButton");
    return localBasicArrowButton;
  }
  
  public boolean isPopupVisible(JComboBox paramJComboBox)
  {
    return popup.isVisible();
  }
  
  public void setPopupVisible(JComboBox paramJComboBox, boolean paramBoolean)
  {
    if (paramBoolean) {
      popup.show();
    } else {
      popup.hide();
    }
  }
  
  public boolean isFocusTraversable(JComboBox paramJComboBox)
  {
    return !comboBox.isEditable();
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    hasFocus = comboBox.hasFocus();
    if (!comboBox.isEditable())
    {
      Rectangle localRectangle = rectangleForCurrentValue();
      paintCurrentValueBackground(paramGraphics, localRectangle, hasFocus);
      paintCurrentValue(paramGraphics, localRectangle, hasFocus);
    }
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    return getMinimumSize(paramJComponent);
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent)
  {
    if (!isMinimumSizeDirty) {
      return new Dimension(cachedMinimumSize);
    }
    Dimension localDimension = getDisplaySize();
    Insets localInsets = getInsets();
    int i = height;
    int j = squareButton ? i : arrowButton.getPreferredSize().width;
    height += top + bottom;
    width += left + right + j;
    cachedMinimumSize.setSize(width, height);
    isMinimumSizeDirty = false;
    return new Dimension(localDimension);
  }
  
  public Dimension getMaximumSize(JComponent paramJComponent)
  {
    return new Dimension(32767, 32767);
  }
  
  public int getBaseline(JComponent paramJComponent, int paramInt1, int paramInt2)
  {
    super.getBaseline(paramJComponent, paramInt1, paramInt2);
    int i = -1;
    getDisplaySize();
    if (sameBaseline)
    {
      Insets localInsets = paramJComponent.getInsets();
      paramInt2 = paramInt2 - top - bottom;
      if (!comboBox.isEditable())
      {
        Object localObject1 = comboBox.getRenderer();
        if (localObject1 == null) {
          localObject1 = new DefaultListCellRenderer();
        }
        Object localObject2 = null;
        Object localObject3 = comboBox.getPrototypeDisplayValue();
        if (localObject3 != null) {
          localObject2 = localObject3;
        } else if (comboBox.getModel().getSize() > 0) {
          localObject2 = comboBox.getModel().getElementAt(0);
        }
        Component localComponent = ((ListCellRenderer)localObject1).getListCellRendererComponent(listBox, localObject2, -1, false, false);
        if ((localComponent instanceof JLabel))
        {
          JLabel localJLabel = (JLabel)localComponent;
          String str = localJLabel.getText();
          if ((str == null) || (str.isEmpty())) {
            localJLabel.setText(" ");
          }
        }
        if ((localComponent instanceof JComponent)) {
          localComponent.setFont(comboBox.getFont());
        }
        i = localComponent.getBaseline(paramInt1, paramInt2);
      }
      else
      {
        i = editor.getBaseline(paramInt1, paramInt2);
      }
      if (i > 0) {
        i += top;
      }
    }
    return i;
  }
  
  public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent paramJComponent)
  {
    super.getBaselineResizeBehavior(paramJComponent);
    getDisplaySize();
    if (comboBox.isEditable()) {
      return editor.getBaselineResizeBehavior();
    }
    if (sameBaseline)
    {
      Object localObject1 = comboBox.getRenderer();
      if (localObject1 == null) {
        localObject1 = new DefaultListCellRenderer();
      }
      Object localObject2 = null;
      Object localObject3 = comboBox.getPrototypeDisplayValue();
      if (localObject3 != null) {
        localObject2 = localObject3;
      } else if (comboBox.getModel().getSize() > 0) {
        localObject2 = comboBox.getModel().getElementAt(0);
      }
      if (localObject2 != null)
      {
        Component localComponent = ((ListCellRenderer)localObject1).getListCellRendererComponent(listBox, localObject2, -1, false, false);
        return localComponent.getBaselineResizeBehavior();
      }
    }
    return Component.BaselineResizeBehavior.OTHER;
  }
  
  public int getAccessibleChildrenCount(JComponent paramJComponent)
  {
    if (comboBox.isEditable()) {
      return 2;
    }
    return 1;
  }
  
  public Accessible getAccessibleChild(JComponent paramJComponent, int paramInt)
  {
    AccessibleContext localAccessibleContext;
    switch (paramInt)
    {
    case 0: 
      if ((popup instanceof Accessible))
      {
        localAccessibleContext = ((Accessible)popup).getAccessibleContext();
        localAccessibleContext.setAccessibleParent(comboBox);
        return (Accessible)popup;
      }
      break;
    case 1: 
      if ((comboBox.isEditable()) && ((editor instanceof Accessible)))
      {
        localAccessibleContext = ((Accessible)editor).getAccessibleContext();
        localAccessibleContext.setAccessibleParent(comboBox);
        return (Accessible)editor;
      }
      break;
    }
    return null;
  }
  
  protected boolean isNavigationKey(int paramInt)
  {
    return (paramInt == 38) || (paramInt == 40) || (paramInt == 224) || (paramInt == 225);
  }
  
  private boolean isNavigationKey(int paramInt1, int paramInt2)
  {
    InputMap localInputMap = comboBox.getInputMap(1);
    KeyStroke localKeyStroke = KeyStroke.getKeyStroke(paramInt1, paramInt2);
    return (localInputMap != null) && (localInputMap.get(localKeyStroke) != null);
  }
  
  protected void selectNextPossibleValue()
  {
    int i;
    if (comboBox.isPopupVisible()) {
      i = listBox.getSelectedIndex();
    } else {
      i = comboBox.getSelectedIndex();
    }
    if (i < comboBox.getModel().getSize() - 1)
    {
      listBox.setSelectedIndex(i + 1);
      listBox.ensureIndexIsVisible(i + 1);
      if ((!isTableCellEditor) && ((!UIManager.getBoolean("ComboBox.noActionOnKeyNavigation")) || (!comboBox.isPopupVisible()))) {
        comboBox.setSelectedIndex(i + 1);
      }
      comboBox.repaint();
    }
  }
  
  protected void selectPreviousPossibleValue()
  {
    int i;
    if (comboBox.isPopupVisible()) {
      i = listBox.getSelectedIndex();
    } else {
      i = comboBox.getSelectedIndex();
    }
    if (i > 0)
    {
      listBox.setSelectedIndex(i - 1);
      listBox.ensureIndexIsVisible(i - 1);
      if ((!isTableCellEditor) && ((!UIManager.getBoolean("ComboBox.noActionOnKeyNavigation")) || (!comboBox.isPopupVisible()))) {
        comboBox.setSelectedIndex(i - 1);
      }
      comboBox.repaint();
    }
  }
  
  protected void toggleOpenClose()
  {
    setPopupVisible(comboBox, !isPopupVisible(comboBox));
  }
  
  protected Rectangle rectangleForCurrentValue()
  {
    int i = comboBox.getWidth();
    int j = comboBox.getHeight();
    Insets localInsets = getInsets();
    int k = j - (top + bottom);
    if (arrowButton != null) {
      k = arrowButton.getWidth();
    }
    if (BasicGraphicsUtils.isLeftToRight(comboBox)) {
      return new Rectangle(left, top, i - (left + right + k), j - (top + bottom));
    }
    return new Rectangle(left + k, top, i - (left + right + k), j - (top + bottom));
  }
  
  protected Insets getInsets()
  {
    return comboBox.getInsets();
  }
  
  public void paintCurrentValue(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
  {
    ListCellRenderer localListCellRenderer = comboBox.getRenderer();
    Component localComponent;
    if ((paramBoolean) && (!isPopupVisible(comboBox)))
    {
      localComponent = localListCellRenderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, true, false);
    }
    else
    {
      localComponent = localListCellRenderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
      localComponent.setBackground(UIManager.getColor("ComboBox.background"));
    }
    localComponent.setFont(comboBox.getFont());
    if ((paramBoolean) && (!isPopupVisible(comboBox)))
    {
      localComponent.setForeground(listBox.getSelectionForeground());
      localComponent.setBackground(listBox.getSelectionBackground());
    }
    else if (comboBox.isEnabled())
    {
      localComponent.setForeground(comboBox.getForeground());
      localComponent.setBackground(comboBox.getBackground());
    }
    else
    {
      localComponent.setForeground(DefaultLookup.getColor(comboBox, this, "ComboBox.disabledForeground", null));
      localComponent.setBackground(DefaultLookup.getColor(comboBox, this, "ComboBox.disabledBackground", null));
    }
    boolean bool = false;
    if ((localComponent instanceof JPanel)) {
      bool = true;
    }
    int i = x;
    int j = y;
    int k = width;
    int m = height;
    if (padding != null)
    {
      i = x + padding.left;
      j = y + padding.top;
      k = width - (padding.left + padding.right);
      m = height - (padding.top + padding.bottom);
    }
    currentValuePane.paintComponent(paramGraphics, localComponent, comboBox, i, j, k, m, bool);
  }
  
  public void paintCurrentValueBackground(Graphics paramGraphics, Rectangle paramRectangle, boolean paramBoolean)
  {
    Color localColor = paramGraphics.getColor();
    if (comboBox.isEnabled()) {
      paramGraphics.setColor(DefaultLookup.getColor(comboBox, this, "ComboBox.background", null));
    } else {
      paramGraphics.setColor(DefaultLookup.getColor(comboBox, this, "ComboBox.disabledBackground", null));
    }
    paramGraphics.fillRect(x, y, width, height);
    paramGraphics.setColor(localColor);
  }
  
  void repaintCurrentValue()
  {
    Rectangle localRectangle = rectangleForCurrentValue();
    comboBox.repaint(x, y, width, height);
  }
  
  protected Dimension getDefaultSize()
  {
    Dimension localDimension = getSizeForComponent(getDefaultListCellRenderer().getListCellRendererComponent(listBox, " ", -1, false, false));
    return new Dimension(width, height);
  }
  
  protected Dimension getDisplaySize()
  {
    if (!isDisplaySizeDirty) {
      return new Dimension(cachedDisplaySize);
    }
    Dimension localDimension1 = new Dimension();
    Object localObject1 = comboBox.getRenderer();
    if (localObject1 == null) {
      localObject1 = new DefaultListCellRenderer();
    }
    sameBaseline = true;
    Object localObject2 = comboBox.getPrototypeDisplayValue();
    Object localObject3;
    if (localObject2 != null)
    {
      localDimension1 = getSizeForComponent(((ListCellRenderer)localObject1).getListCellRendererComponent(listBox, localObject2, -1, false, false));
    }
    else
    {
      localObject3 = comboBox.getModel();
      int i = ((ComboBoxModel)localObject3).getSize();
      int j = -1;
      if (i > 0)
      {
        for (int k = 0; k < i; k++)
        {
          Object localObject4 = ((ComboBoxModel)localObject3).getElementAt(k);
          Component localComponent = ((ListCellRenderer)localObject1).getListCellRendererComponent(listBox, localObject4, -1, false, false);
          Dimension localDimension2 = getSizeForComponent(localComponent);
          if ((sameBaseline) && (localObject4 != null) && ((!(localObject4 instanceof String)) || (!"".equals(localObject4))))
          {
            int m = localComponent.getBaseline(width, height);
            if (m == -1) {
              sameBaseline = false;
            } else if (j == -1) {
              j = m;
            } else if (j != m) {
              sameBaseline = false;
            }
          }
          width = Math.max(width, width);
          height = Math.max(height, height);
        }
      }
      else
      {
        localDimension1 = getDefaultSize();
        if (comboBox.isEditable()) {
          width = 100;
        }
      }
    }
    if (comboBox.isEditable())
    {
      localObject3 = editor.getPreferredSize();
      width = Math.max(width, width);
      height = Math.max(height, height);
    }
    if (padding != null)
    {
      width += padding.left + padding.right;
      height += padding.top + padding.bottom;
    }
    cachedDisplaySize.setSize(width, height);
    isDisplaySizeDirty = false;
    return localDimension1;
  }
  
  protected Dimension getSizeForComponent(Component paramComponent)
  {
    currentValuePane.add(paramComponent);
    paramComponent.setFont(comboBox.getFont());
    Dimension localDimension = paramComponent.getPreferredSize();
    currentValuePane.remove(paramComponent);
    return localDimension;
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(1);
    SwingUtilities.replaceUIInputMap(comboBox, 1, localInputMap);
    LazyActionMap.installLazyActionMap(comboBox, BasicComboBoxUI.class, "ComboBox.actionMap");
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 1) {
      return (InputMap)DefaultLookup.get(comboBox, this, "ComboBox.ancestorInputMap");
    }
    return null;
  }
  
  boolean isTableCellEditor()
  {
    return isTableCellEditor;
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIInputMap(comboBox, 1, null);
    SwingUtilities.replaceUIActionMap(comboBox, null);
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String HIDE = "hidePopup";
    private static final String DOWN = "selectNext";
    private static final String DOWN_2 = "selectNext2";
    private static final String TOGGLE = "togglePopup";
    private static final String TOGGLE_2 = "spacePopup";
    private static final String UP = "selectPrevious";
    private static final String UP_2 = "selectPrevious2";
    private static final String ENTER = "enterPressed";
    private static final String PAGE_DOWN = "pageDownPassThrough";
    private static final String PAGE_UP = "pageUpPassThrough";
    private static final String HOME = "homePassThrough";
    private static final String END = "endPassThrough";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      String str = getName();
      JComboBox localJComboBox = (JComboBox)paramActionEvent.getSource();
      BasicComboBoxUI localBasicComboBoxUI = (BasicComboBoxUI)BasicLookAndFeel.getUIOfType(localJComboBox.getUI(), BasicComboBoxUI.class);
      if (str == "hidePopup")
      {
        localJComboBox.firePopupMenuCanceled();
        localJComboBox.setPopupVisible(false);
      }
      else if ((str == "pageDownPassThrough") || (str == "pageUpPassThrough") || (str == "homePassThrough") || (str == "endPassThrough"))
      {
        int i = getNextIndex(localJComboBox, str);
        if ((i >= 0) && (i < localJComboBox.getItemCount())) {
          if ((UIManager.getBoolean("ComboBox.noActionOnKeyNavigation")) && (localJComboBox.isPopupVisible()))
          {
            listBox.setSelectedIndex(i);
            listBox.ensureIndexIsVisible(i);
            localJComboBox.repaint();
          }
          else
          {
            localJComboBox.setSelectedIndex(i);
          }
        }
      }
      else if (str == "selectNext")
      {
        if (localJComboBox.isShowing()) {
          if (localJComboBox.isPopupVisible())
          {
            if (localBasicComboBoxUI != null) {
              localBasicComboBoxUI.selectNextPossibleValue();
            }
          }
          else {
            localJComboBox.setPopupVisible(true);
          }
        }
      }
      else if (str == "selectNext2")
      {
        if (localJComboBox.isShowing()) {
          if (((localJComboBox.isEditable()) || ((localBasicComboBoxUI != null) && (localBasicComboBoxUI.isTableCellEditor()))) && (!localJComboBox.isPopupVisible())) {
            localJComboBox.setPopupVisible(true);
          } else if (localBasicComboBoxUI != null) {
            localBasicComboBoxUI.selectNextPossibleValue();
          }
        }
      }
      else if ((str == "togglePopup") || (str == "spacePopup"))
      {
        if ((localBasicComboBoxUI != null) && ((str == "togglePopup") || (!localJComboBox.isEditable()))) {
          if (localBasicComboBoxUI.isTableCellEditor()) {
            localJComboBox.setSelectedIndex(popup.getList().getSelectedIndex());
          } else {
            localJComboBox.setPopupVisible(!localJComboBox.isPopupVisible());
          }
        }
      }
      else if (str == "selectPrevious")
      {
        if (localBasicComboBoxUI != null) {
          if (localBasicComboBoxUI.isPopupVisible(localJComboBox)) {
            localBasicComboBoxUI.selectPreviousPossibleValue();
          } else if (DefaultLookup.getBoolean(localJComboBox, localBasicComboBoxUI, "ComboBox.showPopupOnNavigation", false)) {
            localBasicComboBoxUI.setPopupVisible(localJComboBox, true);
          }
        }
      }
      else if (str == "selectPrevious2")
      {
        if ((localJComboBox.isShowing()) && (localBasicComboBoxUI != null)) {
          if ((localJComboBox.isEditable()) && (!localJComboBox.isPopupVisible())) {
            localJComboBox.setPopupVisible(true);
          } else {
            localBasicComboBoxUI.selectPreviousPossibleValue();
          }
        }
      }
      else if (str == "enterPressed")
      {
        Object localObject2;
        if (localJComboBox.isPopupVisible())
        {
          if (UIManager.getBoolean("ComboBox.noActionOnKeyNavigation"))
          {
            Object localObject1 = popup.getList().getSelectedValue();
            if (localObject1 != null)
            {
              localJComboBox.getEditor().setItem(localObject1);
              localJComboBox.setSelectedItem(localObject1);
            }
            localJComboBox.setPopupVisible(false);
          }
          else
          {
            boolean bool = UIManager.getBoolean("ComboBox.isEnterSelectablePopup");
            if ((!localJComboBox.isEditable()) || (bool) || (isTableCellEditor))
            {
              localObject2 = popup.getList().getSelectedValue();
              if (localObject2 != null)
              {
                localJComboBox.getEditor().setItem(localObject2);
                localJComboBox.setSelectedItem(localObject2);
              }
            }
            localJComboBox.setPopupVisible(false);
          }
        }
        else
        {
          if ((isTableCellEditor) && (!localJComboBox.isEditable())) {
            localJComboBox.setSelectedItem(localJComboBox.getSelectedItem());
          }
          JRootPane localJRootPane = SwingUtilities.getRootPane(localJComboBox);
          if (localJRootPane != null)
          {
            localObject2 = localJRootPane.getInputMap(2);
            ActionMap localActionMap = localJRootPane.getActionMap();
            if ((localObject2 != null) && (localActionMap != null))
            {
              Object localObject3 = ((InputMap)localObject2).get(KeyStroke.getKeyStroke(10, 0));
              if (localObject3 != null)
              {
                Action localAction = localActionMap.get(localObject3);
                if (localAction != null) {
                  localAction.actionPerformed(new ActionEvent(localJRootPane, paramActionEvent.getID(), paramActionEvent.getActionCommand(), paramActionEvent.getWhen(), paramActionEvent.getModifiers()));
                }
              }
            }
          }
        }
      }
    }
    
    private int getNextIndex(JComboBox paramJComboBox, String paramString)
    {
      int i = paramJComboBox.getMaximumRowCount();
      int j = paramJComboBox.getSelectedIndex();
      if ((UIManager.getBoolean("ComboBox.noActionOnKeyNavigation")) && ((paramJComboBox.getUI() instanceof BasicComboBoxUI))) {
        j = getUIlistBox.getSelectedIndex();
      }
      int k;
      if (paramString == "pageUpPassThrough")
      {
        k = j - i;
        return k < 0 ? 0 : k;
      }
      if (paramString == "pageDownPassThrough")
      {
        k = j + i;
        int m = paramJComboBox.getItemCount();
        return k < m ? k : m - 1;
      }
      if (paramString == "homePassThrough") {
        return 0;
      }
      if (paramString == "endPassThrough") {
        return paramJComboBox.getItemCount() - 1;
      }
      return paramJComboBox.getSelectedIndex();
    }
    
    public boolean isEnabled(Object paramObject)
    {
      if (getName() == "hidePopup") {
        return (paramObject != null) && (((JComboBox)paramObject).isPopupVisible());
      }
      return true;
    }
  }
  
  public class ComboBoxLayoutManager
    implements LayoutManager
  {
    public ComboBoxLayoutManager() {}
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return BasicComboBoxUI.this.getHandler().preferredLayoutSize(paramContainer);
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return BasicComboBoxUI.this.getHandler().minimumLayoutSize(paramContainer);
    }
    
    public void layoutContainer(Container paramContainer)
    {
      BasicComboBoxUI.this.getHandler().layoutContainer(paramContainer);
    }
  }
  
  class DefaultKeySelectionManager
    implements JComboBox.KeySelectionManager, UIResource
  {
    private String prefix = "";
    private String typedString = "";
    
    DefaultKeySelectionManager() {}
    
    public int selectionForKey(char paramChar, ComboBoxModel paramComboBoxModel)
    {
      if (lastTime == 0L)
      {
        prefix = "";
        typedString = "";
      }
      int i = 1;
      int j = comboBox.getSelectedIndex();
      if (time - lastTime < timeFactor)
      {
        typedString += paramChar;
        if ((prefix.length() == 1) && (paramChar == prefix.charAt(0))) {
          j++;
        } else {
          prefix = typedString;
        }
      }
      else
      {
        j++;
        typedString = ("" + paramChar);
        prefix = typedString;
      }
      lastTime = time;
      if ((j < 0) || (j >= paramComboBoxModel.getSize()))
      {
        i = 0;
        j = 0;
      }
      int k = listBox.getNextMatch(prefix, j, Position.Bias.Forward);
      if ((k < 0) && (i != 0)) {
        k = listBox.getNextMatch(prefix, 0, Position.Bias.Forward);
      }
      return k;
    }
  }
  
  public class FocusHandler
    implements FocusListener
  {
    public FocusHandler() {}
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      BasicComboBoxUI.this.getHandler().focusGained(paramFocusEvent);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      BasicComboBoxUI.this.getHandler().focusLost(paramFocusEvent);
    }
  }
  
  private class Handler
    implements ActionListener, FocusListener, KeyListener, LayoutManager, ListDataListener, PropertyChangeListener
  {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (paramPropertyChangeEvent.getSource() == editor)
      {
        if ("border".equals(str))
        {
          isMinimumSizeDirty = true;
          isDisplaySizeDirty = true;
          comboBox.revalidate();
        }
      }
      else
      {
        JComboBox localJComboBox = (JComboBox)paramPropertyChangeEvent.getSource();
        if (str == "model")
        {
          ComboBoxModel localComboBoxModel1 = (ComboBoxModel)paramPropertyChangeEvent.getNewValue();
          ComboBoxModel localComboBoxModel2 = (ComboBoxModel)paramPropertyChangeEvent.getOldValue();
          if ((localComboBoxModel2 != null) && (listDataListener != null)) {
            localComboBoxModel2.removeListDataListener(listDataListener);
          }
          if ((localComboBoxModel1 != null) && (listDataListener != null)) {
            localComboBoxModel1.addListDataListener(listDataListener);
          }
          if (editor != null) {
            localJComboBox.configureEditor(localJComboBox.getEditor(), localJComboBox.getSelectedItem());
          }
          isMinimumSizeDirty = true;
          isDisplaySizeDirty = true;
          localJComboBox.revalidate();
          localJComboBox.repaint();
        }
        else if ((str == "editor") && (localJComboBox.isEditable()))
        {
          addEditor();
          localJComboBox.revalidate();
        }
        else if (str == "editable")
        {
          if (localJComboBox.isEditable())
          {
            localJComboBox.setRequestFocusEnabled(false);
            addEditor();
          }
          else
          {
            localJComboBox.setRequestFocusEnabled(true);
            removeEditor();
          }
          BasicComboBoxUI.this.updateToolTipTextForChildren();
          localJComboBox.revalidate();
        }
        else
        {
          boolean bool;
          if (str == "enabled")
          {
            bool = localJComboBox.isEnabled();
            if (editor != null) {
              editor.setEnabled(bool);
            }
            if (arrowButton != null) {
              arrowButton.setEnabled(bool);
            }
            localJComboBox.repaint();
          }
          else if (str == "focusable")
          {
            bool = localJComboBox.isFocusable();
            if (editor != null) {
              editor.setFocusable(bool);
            }
            if (arrowButton != null) {
              arrowButton.setFocusable(bool);
            }
            localJComboBox.repaint();
          }
          else if (str == "maximumRowCount")
          {
            if (isPopupVisible(localJComboBox))
            {
              setPopupVisible(localJComboBox, false);
              setPopupVisible(localJComboBox, true);
            }
          }
          else if (str == "font")
          {
            listBox.setFont(localJComboBox.getFont());
            if (editor != null) {
              editor.setFont(localJComboBox.getFont());
            }
            isMinimumSizeDirty = true;
            isDisplaySizeDirty = true;
            localJComboBox.validate();
          }
          else if (str == "ToolTipText")
          {
            BasicComboBoxUI.this.updateToolTipTextForChildren();
          }
          else if (str == "JComboBox.isTableCellEditor")
          {
            Boolean localBoolean = (Boolean)paramPropertyChangeEvent.getNewValue();
            isTableCellEditor = (localBoolean.equals(Boolean.TRUE));
          }
          else if (str == "prototypeDisplayValue")
          {
            isMinimumSizeDirty = true;
            isDisplaySizeDirty = true;
            localJComboBox.revalidate();
          }
          else if (str == "renderer")
          {
            isMinimumSizeDirty = true;
            isDisplaySizeDirty = true;
            localJComboBox.revalidate();
          }
        }
      }
    }
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      if (BasicComboBoxUI.this.isNavigationKey(paramKeyEvent.getKeyCode(), paramKeyEvent.getModifiers()))
      {
        lastTime = 0L;
      }
      else if ((comboBox.isEnabled()) && (comboBox.getModel().getSize() != 0) && (isTypeAheadKey(paramKeyEvent)) && (paramKeyEvent.getKeyChar() != 65535))
      {
        time = paramKeyEvent.getWhen();
        if (comboBox.selectWithKeyChar(paramKeyEvent.getKeyChar())) {
          paramKeyEvent.consume();
        }
      }
    }
    
    public void keyTyped(KeyEvent paramKeyEvent) {}
    
    public void keyReleased(KeyEvent paramKeyEvent) {}
    
    private boolean isTypeAheadKey(KeyEvent paramKeyEvent)
    {
      return (!paramKeyEvent.isAltDown()) && (!BasicGraphicsUtils.isMenuShortcutKeyDown(paramKeyEvent));
    }
    
    public void focusGained(FocusEvent paramFocusEvent)
    {
      ComboBoxEditor localComboBoxEditor = comboBox.getEditor();
      if ((localComboBoxEditor != null) && (paramFocusEvent.getSource() == localComboBoxEditor.getEditorComponent())) {
        return;
      }
      hasFocus = true;
      comboBox.repaint();
      if ((comboBox.isEditable()) && (editor != null)) {
        editor.requestFocus();
      }
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      ComboBoxEditor localComboBoxEditor = comboBox.getEditor();
      if ((localComboBoxEditor != null) && (paramFocusEvent.getSource() == localComboBoxEditor.getEditorComponent()))
      {
        Object localObject1 = localComboBoxEditor.getItem();
        Object localObject2 = comboBox.getSelectedItem();
        if ((!paramFocusEvent.isTemporary()) && (localObject1 != null)) {
          if (!localObject1.equals(localObject2 == null ? "" : localObject2)) {
            comboBox.actionPerformed(new ActionEvent(localComboBoxEditor, 0, "", EventQueue.getMostRecentEventTime(), 0));
          }
        }
      }
      hasFocus = false;
      if (!paramFocusEvent.isTemporary()) {
        setPopupVisible(comboBox, false);
      }
      comboBox.repaint();
    }
    
    public void contentsChanged(ListDataEvent paramListDataEvent)
    {
      if ((paramListDataEvent.getIndex0() != -1) || (paramListDataEvent.getIndex1() != -1))
      {
        isMinimumSizeDirty = true;
        comboBox.revalidate();
      }
      if ((comboBox.isEditable()) && (editor != null)) {
        comboBox.configureEditor(comboBox.getEditor(), comboBox.getSelectedItem());
      }
      isDisplaySizeDirty = true;
      comboBox.repaint();
    }
    
    public void intervalAdded(ListDataEvent paramListDataEvent)
    {
      contentsChanged(paramListDataEvent);
    }
    
    public void intervalRemoved(ListDataEvent paramListDataEvent)
    {
      contentsChanged(paramListDataEvent);
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void removeLayoutComponent(Component paramComponent) {}
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return paramContainer.getPreferredSize();
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      return paramContainer.getMinimumSize();
    }
    
    public void layoutContainer(Container paramContainer)
    {
      JComboBox localJComboBox = (JComboBox)paramContainer;
      int i = localJComboBox.getWidth();
      int j = localJComboBox.getHeight();
      Insets localInsets = getInsets();
      int k = j - (top + bottom);
      int m = k;
      Object localObject;
      if (arrowButton != null)
      {
        localObject = arrowButton.getInsets();
        m = squareButton ? k : arrowButton.getPreferredSize().width + left + right;
      }
      if (arrowButton != null) {
        if (BasicGraphicsUtils.isLeftToRight(localJComboBox)) {
          arrowButton.setBounds(i - (right + m), top, m, k);
        } else {
          arrowButton.setBounds(left, top, m, k);
        }
      }
      if (editor != null)
      {
        localObject = rectangleForCurrentValue();
        editor.setBounds((Rectangle)localObject);
      }
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      Object localObject = comboBox.getEditor().getItem();
      if (localObject != null)
      {
        if ((!comboBox.isPopupVisible()) && (!localObject.equals(comboBox.getSelectedItem()))) {
          comboBox.setSelectedItem(comboBox.getEditor().getItem());
        }
        ActionMap localActionMap = comboBox.getActionMap();
        if (localActionMap != null)
        {
          Action localAction = localActionMap.get("enterPressed");
          if (localAction != null) {
            localAction.actionPerformed(new ActionEvent(comboBox, paramActionEvent.getID(), paramActionEvent.getActionCommand(), paramActionEvent.getModifiers()));
          }
        }
      }
    }
  }
  
  public class ItemHandler
    implements ItemListener
  {
    public ItemHandler() {}
    
    public void itemStateChanged(ItemEvent paramItemEvent) {}
  }
  
  public class KeyHandler
    extends KeyAdapter
  {
    public KeyHandler() {}
    
    public void keyPressed(KeyEvent paramKeyEvent)
    {
      BasicComboBoxUI.this.getHandler().keyPressed(paramKeyEvent);
    }
  }
  
  public class ListDataHandler
    implements ListDataListener
  {
    public ListDataHandler() {}
    
    public void contentsChanged(ListDataEvent paramListDataEvent)
    {
      BasicComboBoxUI.this.getHandler().contentsChanged(paramListDataEvent);
    }
    
    public void intervalAdded(ListDataEvent paramListDataEvent)
    {
      BasicComboBoxUI.this.getHandler().intervalAdded(paramListDataEvent);
    }
    
    public void intervalRemoved(ListDataEvent paramListDataEvent)
    {
      BasicComboBoxUI.this.getHandler().intervalRemoved(paramListDataEvent);
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicComboBoxUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicComboBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
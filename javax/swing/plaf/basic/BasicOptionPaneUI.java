package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.security.AccessController;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.OptionPaneUI;
import sun.security.action.GetPropertyAction;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicOptionPaneUI
  extends OptionPaneUI
{
  public static final int MinimumWidth = 262;
  public static final int MinimumHeight = 90;
  private static String newline = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
  protected JOptionPane optionPane;
  protected Dimension minimumSize;
  protected JComponent inputComponent;
  protected Component initialFocusComponent;
  protected boolean hasCustomComponents;
  protected PropertyChangeListener propertyChangeListener;
  private Handler handler;
  
  public BasicOptionPaneUI() {}
  
  static void loadActionMap(LazyActionMap paramLazyActionMap)
  {
    paramLazyActionMap.put(new Actions("close"));
    BasicLookAndFeel.installAudioActionMap(paramLazyActionMap);
  }
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new BasicOptionPaneUI();
  }
  
  public void installUI(JComponent paramJComponent)
  {
    optionPane = ((JOptionPane)paramJComponent);
    installDefaults();
    optionPane.setLayout(createLayoutManager());
    installComponents();
    installListeners();
    installKeyboardActions();
  }
  
  public void uninstallUI(JComponent paramJComponent)
  {
    uninstallComponents();
    optionPane.setLayout(null);
    uninstallKeyboardActions();
    uninstallListeners();
    uninstallDefaults();
    optionPane = null;
  }
  
  protected void installDefaults()
  {
    LookAndFeel.installColorsAndFont(optionPane, "OptionPane.background", "OptionPane.foreground", "OptionPane.font");
    LookAndFeel.installBorder(optionPane, "OptionPane.border");
    minimumSize = UIManager.getDimension("OptionPane.minimumSize");
    LookAndFeel.installProperty(optionPane, "opaque", Boolean.TRUE);
  }
  
  protected void uninstallDefaults()
  {
    LookAndFeel.uninstallBorder(optionPane);
  }
  
  protected void installComponents()
  {
    optionPane.add(createMessageArea());
    Container localContainer = createSeparator();
    if (localContainer != null) {
      optionPane.add(localContainer);
    }
    optionPane.add(createButtonArea());
    optionPane.applyComponentOrientation(optionPane.getComponentOrientation());
  }
  
  protected void uninstallComponents()
  {
    hasCustomComponents = false;
    inputComponent = null;
    initialFocusComponent = null;
    optionPane.removeAll();
  }
  
  protected LayoutManager createLayoutManager()
  {
    return new BoxLayout(optionPane, 1);
  }
  
  protected void installListeners()
  {
    if ((propertyChangeListener = createPropertyChangeListener()) != null) {
      optionPane.addPropertyChangeListener(propertyChangeListener);
    }
  }
  
  protected void uninstallListeners()
  {
    if (propertyChangeListener != null)
    {
      optionPane.removePropertyChangeListener(propertyChangeListener);
      propertyChangeListener = null;
    }
    handler = null;
  }
  
  protected PropertyChangeListener createPropertyChangeListener()
  {
    return getHandler();
  }
  
  private Handler getHandler()
  {
    if (handler == null) {
      handler = new Handler(null);
    }
    return handler;
  }
  
  protected void installKeyboardActions()
  {
    InputMap localInputMap = getInputMap(2);
    SwingUtilities.replaceUIInputMap(optionPane, 2, localInputMap);
    LazyActionMap.installLazyActionMap(optionPane, BasicOptionPaneUI.class, "OptionPane.actionMap");
  }
  
  protected void uninstallKeyboardActions()
  {
    SwingUtilities.replaceUIInputMap(optionPane, 2, null);
    SwingUtilities.replaceUIActionMap(optionPane, null);
  }
  
  InputMap getInputMap(int paramInt)
  {
    if (paramInt == 2)
    {
      Object[] arrayOfObject = (Object[])DefaultLookup.get(optionPane, this, "OptionPane.windowBindings");
      if (arrayOfObject != null) {
        return LookAndFeel.makeComponentInputMap(optionPane, arrayOfObject);
      }
    }
    return null;
  }
  
  public Dimension getMinimumOptionPaneSize()
  {
    if (minimumSize == null) {
      return new Dimension(262, 90);
    }
    return new Dimension(minimumSize.width, minimumSize.height);
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    if (paramJComponent == optionPane)
    {
      Dimension localDimension1 = getMinimumOptionPaneSize();
      LayoutManager localLayoutManager = paramJComponent.getLayout();
      if (localLayoutManager != null)
      {
        Dimension localDimension2 = localLayoutManager.preferredLayoutSize(paramJComponent);
        if (localDimension1 != null) {
          return new Dimension(Math.max(width, width), Math.max(height, height));
        }
        return localDimension2;
      }
      return localDimension1;
    }
    return null;
  }
  
  protected Container createMessageArea()
  {
    JPanel localJPanel1 = new JPanel();
    Border localBorder = (Border)DefaultLookup.get(optionPane, this, "OptionPane.messageAreaBorder");
    if (localBorder != null) {
      localJPanel1.setBorder(localBorder);
    }
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
    anchor = DefaultLookup.getInt(optionPane, this, "OptionPane.messageAnchor", 10);
    insets = new Insets(0, 0, 3, 0);
    addMessageComponents(localJPanel2, (GridBagConstraints)localObject, getMessage(), getMaxCharactersPerLineCount(), false);
    localJPanel1.add(localJPanel3, "Center");
    addIcon(localJPanel1);
    return localJPanel1;
  }
  
  protected void addMessageComponents(Container paramContainer, GridBagConstraints paramGridBagConstraints, Object paramObject, int paramInt, boolean paramBoolean)
  {
    if (paramObject == null) {
      return;
    }
    if ((paramObject instanceof Component))
    {
      if (((paramObject instanceof JScrollPane)) || ((paramObject instanceof JPanel)))
      {
        fill = 1;
        weighty = 1.0D;
      }
      else
      {
        fill = 2;
      }
      weightx = 1.0D;
      paramContainer.add((Component)paramObject, paramGridBagConstraints);
      weightx = 0.0D;
      weighty = 0.0D;
      fill = 0;
      gridy += 1;
      if (!paramBoolean) {
        hasCustomComponents = true;
      }
    }
    else
    {
      Object localObject1;
      Object localObject3;
      if ((paramObject instanceof Object[]))
      {
        localObject1 = (Object[])paramObject;
        for (localObject3 : localObject1) {
          addMessageComponents(paramContainer, paramGridBagConstraints, localObject3, paramInt, false);
        }
      }
      else if ((paramObject instanceof Icon))
      {
        localObject1 = new JLabel((Icon)paramObject, 0);
        configureMessageLabel((JLabel)localObject1);
        addMessageComponents(paramContainer, paramGridBagConstraints, localObject1, paramInt, true);
      }
      else
      {
        localObject1 = paramObject.toString();
        int i = ((String)localObject1).length();
        if (i <= 0) {
          return;
        }
        ??? = 0;
        if ((??? = ((String)localObject1).indexOf(newline)) >= 0) {
          ??? = newline.length();
        } else if ((??? = ((String)localObject1).indexOf("\r\n")) >= 0) {
          ??? = 2;
        } else if ((??? = ((String)localObject1).indexOf('\n')) >= 0) {
          ??? = 1;
        }
        if (??? >= 0)
        {
          if (??? == 0)
          {
            localObject3 = new JPanel()
            {
              public Dimension getPreferredSize()
              {
                Font localFont = getFont();
                if (localFont != null) {
                  return new Dimension(1, localFont.getSize() + 2);
                }
                return new Dimension(0, 0);
              }
            };
            ((JPanel)localObject3).setName("OptionPane.break");
            addMessageComponents(paramContainer, paramGridBagConstraints, localObject3, paramInt, true);
          }
          else
          {
            addMessageComponents(paramContainer, paramGridBagConstraints, ((String)localObject1).substring(0, ???), paramInt, false);
          }
          addMessageComponents(paramContainer, paramGridBagConstraints, ((String)localObject1).substring(??? + ???), paramInt, false);
        }
        else if (i > paramInt)
        {
          localObject3 = Box.createVerticalBox();
          ((Container)localObject3).setName("OptionPane.verticalBox");
          burstStringInto((Container)localObject3, (String)localObject1, paramInt);
          addMessageComponents(paramContainer, paramGridBagConstraints, localObject3, paramInt, true);
        }
        else
        {
          localObject3 = new JLabel((String)localObject1, 10);
          ((JLabel)localObject3).setName("OptionPane.label");
          configureMessageLabel((JLabel)localObject3);
          addMessageComponents(paramContainer, paramGridBagConstraints, localObject3, paramInt, true);
        }
      }
    }
  }
  
  protected Object getMessage()
  {
    inputComponent = null;
    if (optionPane != null)
    {
      if (optionPane.getWantsInput())
      {
        Object localObject1 = optionPane.getMessage();
        Object[] arrayOfObject = optionPane.getSelectionValues();
        Object localObject2 = optionPane.getInitialSelectionValue();
        Object localObject4;
        Object localObject3;
        Object localObject5;
        if (arrayOfObject != null)
        {
          if (arrayOfObject.length < 20)
          {
            localObject4 = new JComboBox();
            ((JComboBox)localObject4).setName("OptionPane.comboBox");
            int i = 0;
            int j = arrayOfObject.length;
            while (i < j)
            {
              ((JComboBox)localObject4).addItem(arrayOfObject[i]);
              i++;
            }
            if (localObject2 != null) {
              ((JComboBox)localObject4).setSelectedItem(localObject2);
            }
            inputComponent = ((JComponent)localObject4);
            localObject3 = localObject4;
          }
          else
          {
            localObject4 = new JList(arrayOfObject);
            localObject5 = new JScrollPane((Component)localObject4);
            ((JScrollPane)localObject5).setName("OptionPane.scrollPane");
            ((JList)localObject4).setName("OptionPane.list");
            ((JList)localObject4).setVisibleRowCount(10);
            ((JList)localObject4).setSelectionMode(0);
            if (localObject2 != null) {
              ((JList)localObject4).setSelectedValue(localObject2, true);
            }
            ((JList)localObject4).addMouseListener(getHandler());
            localObject3 = localObject5;
            inputComponent = ((JComponent)localObject4);
          }
        }
        else
        {
          localObject4 = new MultiplexingTextField(20);
          ((MultiplexingTextField)localObject4).setName("OptionPane.textField");
          ((MultiplexingTextField)localObject4).setKeyStrokes(new KeyStroke[] { KeyStroke.getKeyStroke("ENTER") });
          if (localObject2 != null)
          {
            localObject5 = localObject2.toString();
            ((MultiplexingTextField)localObject4).setText((String)localObject5);
            ((MultiplexingTextField)localObject4).setSelectionStart(0);
            ((MultiplexingTextField)localObject4).setSelectionEnd(((String)localObject5).length());
          }
          ((MultiplexingTextField)localObject4).addActionListener(getHandler());
          localObject3 = inputComponent = localObject4;
        }
        if (localObject1 == null)
        {
          localObject4 = new Object[1];
          localObject4[0] = localObject3;
        }
        else
        {
          localObject4 = new Object[2];
          localObject4[0] = localObject1;
          localObject4[1] = localObject3;
        }
        return localObject4;
      }
      return optionPane.getMessage();
    }
    return null;
  }
  
  protected void addIcon(Container paramContainer)
  {
    Icon localIcon = getIcon();
    if (localIcon != null)
    {
      JLabel localJLabel = new JLabel(localIcon);
      localJLabel.setName("OptionPane.iconLabel");
      localJLabel.setVerticalAlignment(1);
      paramContainer.add(localJLabel, "Before");
    }
  }
  
  protected Icon getIcon()
  {
    Icon localIcon = optionPane == null ? null : optionPane.getIcon();
    if ((localIcon == null) && (optionPane != null)) {
      localIcon = getIconForType(optionPane.getMessageType());
    }
    return localIcon;
  }
  
  protected Icon getIconForType(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 3)) {
      return null;
    }
    String str = null;
    switch (paramInt)
    {
    case 0: 
      str = "OptionPane.errorIcon";
      break;
    case 1: 
      str = "OptionPane.informationIcon";
      break;
    case 2: 
      str = "OptionPane.warningIcon";
      break;
    case 3: 
      str = "OptionPane.questionIcon";
    }
    if (str != null) {
      return (Icon)DefaultLookup.get(optionPane, this, str);
    }
    return null;
  }
  
  protected int getMaxCharactersPerLineCount()
  {
    return optionPane.getMaxCharactersPerLineCount();
  }
  
  protected void burstStringInto(Container paramContainer, String paramString, int paramInt)
  {
    int i = paramString.length();
    if (i <= 0) {
      return;
    }
    if (i > paramInt)
    {
      int j = paramString.lastIndexOf(' ', paramInt);
      if (j <= 0) {
        j = paramString.indexOf(' ', paramInt);
      }
      if ((j > 0) && (j < i))
      {
        burstStringInto(paramContainer, paramString.substring(0, j), paramInt);
        burstStringInto(paramContainer, paramString.substring(j + 1), paramInt);
        return;
      }
    }
    JLabel localJLabel = new JLabel(paramString, 2);
    localJLabel.setName("OptionPane.label");
    configureMessageLabel(localJLabel);
    paramContainer.add(localJLabel);
  }
  
  protected Container createSeparator()
  {
    return null;
  }
  
  protected Container createButtonArea()
  {
    JPanel localJPanel = new JPanel();
    Border localBorder = (Border)DefaultLookup.get(optionPane, this, "OptionPane.buttonAreaBorder");
    localJPanel.setName("OptionPane.buttonArea");
    if (localBorder != null) {
      localJPanel.setBorder(localBorder);
    }
    localJPanel.setLayout(new ButtonAreaLayout(DefaultLookup.getBoolean(optionPane, this, "OptionPane.sameSizeButtons", true), DefaultLookup.getInt(optionPane, this, "OptionPane.buttonPadding", 6), DefaultLookup.getInt(optionPane, this, "OptionPane.buttonOrientation", 0), DefaultLookup.getBoolean(optionPane, this, "OptionPane.isYesLast", false)));
    addButtonComponents(localJPanel, getButtons(), getInitialValueIndex());
    return localJPanel;
  }
  
  protected void addButtonComponents(Container paramContainer, Object[] paramArrayOfObject, int paramInt)
  {
    if ((paramArrayOfObject != null) && (paramArrayOfObject.length > 0))
    {
      boolean bool = getSizeButtonsToSameWidth();
      int i = 1;
      int j = paramArrayOfObject.length;
      JButton[] arrayOfJButton = null;
      int k = 0;
      if (bool) {
        arrayOfJButton = new JButton[j];
      }
      for (int m = 0; m < j; m++)
      {
        Object localObject1 = paramArrayOfObject[m];
        Object localObject2;
        JButton localJButton2;
        if ((localObject1 instanceof Component))
        {
          i = 0;
          localObject2 = (Component)localObject1;
          paramContainer.add((Component)localObject2);
          hasCustomComponents = true;
        }
        else
        {
          if ((localObject1 instanceof ButtonFactory)) {
            localJButton2 = ((ButtonFactory)localObject1).createButton();
          } else if ((localObject1 instanceof Icon)) {
            localJButton2 = new JButton((Icon)localObject1);
          } else {
            localJButton2 = new JButton(localObject1.toString());
          }
          localJButton2.setName("OptionPane.button");
          localJButton2.setMultiClickThreshhold(DefaultLookup.getInt(optionPane, this, "OptionPane.buttonClickThreshhold", 0));
          configureButton(localJButton2);
          paramContainer.add(localJButton2);
          ActionListener localActionListener = createButtonActionListener(m);
          if (localActionListener != null) {
            localJButton2.addActionListener(localActionListener);
          }
          localObject2 = localJButton2;
        }
        if ((bool) && (i != 0) && ((localObject2 instanceof JButton)))
        {
          arrayOfJButton[m] = ((JButton)localObject2);
          k = Math.max(k, getMinimumSizewidth);
        }
        if (m == paramInt)
        {
          initialFocusComponent = ((Component)localObject2);
          if ((initialFocusComponent instanceof JButton))
          {
            localJButton2 = (JButton)initialFocusComponent;
            localJButton2.addHierarchyListener(new HierarchyListener()
            {
              public void hierarchyChanged(HierarchyEvent paramAnonymousHierarchyEvent)
              {
                if ((paramAnonymousHierarchyEvent.getChangeFlags() & 1L) != 0L)
                {
                  JButton localJButton = (JButton)paramAnonymousHierarchyEvent.getComponent();
                  JRootPane localJRootPane = SwingUtilities.getRootPane(localJButton);
                  if (localJRootPane != null) {
                    localJRootPane.setDefaultButton(localJButton);
                  }
                }
              }
            });
          }
        }
      }
      ((ButtonAreaLayout)paramContainer.getLayout()).setSyncAllWidths((bool) && (i != 0));
      if ((DefaultLookup.getBoolean(optionPane, this, "OptionPane.setButtonMargin", true)) && (bool) && (i != 0))
      {
        int n = j <= 2 ? 8 : 4;
        for (int i1 = 0; i1 < j; i1++)
        {
          JButton localJButton1 = arrayOfJButton[i1];
          localJButton1.setMargin(new Insets(2, n, 2, n));
        }
      }
    }
  }
  
  protected ActionListener createButtonActionListener(int paramInt)
  {
    return new ButtonActionListener(paramInt);
  }
  
  protected Object[] getButtons()
  {
    if (optionPane != null)
    {
      Object[] arrayOfObject = optionPane.getOptions();
      if (arrayOfObject == null)
      {
        int i = optionPane.getOptionType();
        Locale localLocale = optionPane.getLocale();
        int j = DefaultLookup.getInt(optionPane, this, "OptionPane.buttonMinimumWidth", -1);
        ButtonFactory[] arrayOfButtonFactory;
        if (i == 0)
        {
          arrayOfButtonFactory = new ButtonFactory[2];
          arrayOfButtonFactory[0] = new ButtonFactory(UIManager.getString("OptionPane.yesButtonText", localLocale), getMnemonic("OptionPane.yesButtonMnemonic", localLocale), (Icon)DefaultLookup.get(optionPane, this, "OptionPane.yesIcon"), j);
          arrayOfButtonFactory[1] = new ButtonFactory(UIManager.getString("OptionPane.noButtonText", localLocale), getMnemonic("OptionPane.noButtonMnemonic", localLocale), (Icon)DefaultLookup.get(optionPane, this, "OptionPane.noIcon"), j);
        }
        else if (i == 1)
        {
          arrayOfButtonFactory = new ButtonFactory[3];
          arrayOfButtonFactory[0] = new ButtonFactory(UIManager.getString("OptionPane.yesButtonText", localLocale), getMnemonic("OptionPane.yesButtonMnemonic", localLocale), (Icon)DefaultLookup.get(optionPane, this, "OptionPane.yesIcon"), j);
          arrayOfButtonFactory[1] = new ButtonFactory(UIManager.getString("OptionPane.noButtonText", localLocale), getMnemonic("OptionPane.noButtonMnemonic", localLocale), (Icon)DefaultLookup.get(optionPane, this, "OptionPane.noIcon"), j);
          arrayOfButtonFactory[2] = new ButtonFactory(UIManager.getString("OptionPane.cancelButtonText", localLocale), getMnemonic("OptionPane.cancelButtonMnemonic", localLocale), (Icon)DefaultLookup.get(optionPane, this, "OptionPane.cancelIcon"), j);
        }
        else if (i == 2)
        {
          arrayOfButtonFactory = new ButtonFactory[2];
          arrayOfButtonFactory[0] = new ButtonFactory(UIManager.getString("OptionPane.okButtonText", localLocale), getMnemonic("OptionPane.okButtonMnemonic", localLocale), (Icon)DefaultLookup.get(optionPane, this, "OptionPane.okIcon"), j);
          arrayOfButtonFactory[1] = new ButtonFactory(UIManager.getString("OptionPane.cancelButtonText", localLocale), getMnemonic("OptionPane.cancelButtonMnemonic", localLocale), (Icon)DefaultLookup.get(optionPane, this, "OptionPane.cancelIcon"), j);
        }
        else
        {
          arrayOfButtonFactory = new ButtonFactory[1];
          arrayOfButtonFactory[0] = new ButtonFactory(UIManager.getString("OptionPane.okButtonText", localLocale), getMnemonic("OptionPane.okButtonMnemonic", localLocale), (Icon)DefaultLookup.get(optionPane, this, "OptionPane.okIcon"), j);
        }
        return arrayOfButtonFactory;
      }
      return arrayOfObject;
    }
    return null;
  }
  
  private int getMnemonic(String paramString, Locale paramLocale)
  {
    String str = (String)UIManager.get(paramString, paramLocale);
    if (str == null) {
      return 0;
    }
    try
    {
      return Integer.parseInt(str);
    }
    catch (NumberFormatException localNumberFormatException) {}
    return 0;
  }
  
  protected boolean getSizeButtonsToSameWidth()
  {
    return true;
  }
  
  protected int getInitialValueIndex()
  {
    if (optionPane != null)
    {
      Object localObject = optionPane.getInitialValue();
      Object[] arrayOfObject = optionPane.getOptions();
      if (arrayOfObject == null) {
        return 0;
      }
      if (localObject != null) {
        for (int i = arrayOfObject.length - 1; i >= 0; i--) {
          if (arrayOfObject[i].equals(localObject)) {
            return i;
          }
        }
      }
    }
    return -1;
  }
  
  protected void resetInputValue()
  {
    if ((inputComponent != null) && ((inputComponent instanceof JTextField))) {
      optionPane.setInputValue(((JTextField)inputComponent).getText());
    } else if ((inputComponent != null) && ((inputComponent instanceof JComboBox))) {
      optionPane.setInputValue(((JComboBox)inputComponent).getSelectedItem());
    } else if (inputComponent != null) {
      optionPane.setInputValue(((JList)inputComponent).getSelectedValue());
    }
  }
  
  public void selectInitialValue(JOptionPane paramJOptionPane)
  {
    if (inputComponent != null)
    {
      inputComponent.requestFocus();
    }
    else
    {
      if (initialFocusComponent != null) {
        initialFocusComponent.requestFocus();
      }
      if ((initialFocusComponent instanceof JButton))
      {
        JRootPane localJRootPane = SwingUtilities.getRootPane(initialFocusComponent);
        if (localJRootPane != null) {
          localJRootPane.setDefaultButton((JButton)initialFocusComponent);
        }
      }
    }
  }
  
  public boolean containsCustomComponents(JOptionPane paramJOptionPane)
  {
    return hasCustomComponents;
  }
  
  private void configureMessageLabel(JLabel paramJLabel)
  {
    Color localColor = (Color)DefaultLookup.get(optionPane, this, "OptionPane.messageForeground");
    if (localColor != null) {
      paramJLabel.setForeground(localColor);
    }
    Font localFont = (Font)DefaultLookup.get(optionPane, this, "OptionPane.messageFont");
    if (localFont != null) {
      paramJLabel.setFont(localFont);
    }
  }
  
  private void configureButton(JButton paramJButton)
  {
    Font localFont = (Font)DefaultLookup.get(optionPane, this, "OptionPane.buttonFont");
    if (localFont != null) {
      paramJButton.setFont(localFont);
    }
  }
  
  static
  {
    if (newline == null) {
      newline = "\n";
    }
  }
  
  private static class Actions
    extends UIAction
  {
    private static final String CLOSE = "close";
    
    Actions(String paramString)
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (getName() == "close")
      {
        JOptionPane localJOptionPane = (JOptionPane)paramActionEvent.getSource();
        localJOptionPane.setValue(Integer.valueOf(-1));
      }
    }
  }
  
  public class ButtonActionListener
    implements ActionListener
  {
    protected int buttonIndex;
    
    public ButtonActionListener(int paramInt)
    {
      buttonIndex = paramInt;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (optionPane != null)
      {
        int i = optionPane.getOptionType();
        Object[] arrayOfObject = optionPane.getOptions();
        if ((inputComponent != null) && ((arrayOfObject != null) || (i == -1) || (((i == 0) || (i == 1) || (i == 2)) && (buttonIndex == 0)))) {
          resetInputValue();
        }
        if (arrayOfObject == null)
        {
          if ((i == 2) && (buttonIndex == 1)) {
            optionPane.setValue(Integer.valueOf(2));
          } else {
            optionPane.setValue(Integer.valueOf(buttonIndex));
          }
        }
        else {
          optionPane.setValue(arrayOfObject[buttonIndex]);
        }
      }
    }
  }
  
  public static class ButtonAreaLayout
    implements LayoutManager
  {
    protected boolean syncAllWidths;
    protected int padding;
    protected boolean centersChildren;
    private int orientation;
    private boolean reverseButtons;
    private boolean useOrientation;
    
    public ButtonAreaLayout(boolean paramBoolean, int paramInt)
    {
      syncAllWidths = paramBoolean;
      padding = paramInt;
      centersChildren = true;
      useOrientation = false;
    }
    
    ButtonAreaLayout(boolean paramBoolean1, int paramInt1, int paramInt2, boolean paramBoolean2)
    {
      this(paramBoolean1, paramInt1);
      useOrientation = true;
      orientation = paramInt2;
      reverseButtons = paramBoolean2;
    }
    
    public void setSyncAllWidths(boolean paramBoolean)
    {
      syncAllWidths = paramBoolean;
    }
    
    public boolean getSyncAllWidths()
    {
      return syncAllWidths;
    }
    
    public void setPadding(int paramInt)
    {
      padding = paramInt;
    }
    
    public int getPadding()
    {
      return padding;
    }
    
    public void setCentersChildren(boolean paramBoolean)
    {
      centersChildren = paramBoolean;
      useOrientation = false;
    }
    
    public boolean getCentersChildren()
    {
      return centersChildren;
    }
    
    private int getOrientation(Container paramContainer)
    {
      if (!useOrientation) {
        return 0;
      }
      if (paramContainer.getComponentOrientation().isLeftToRight()) {
        return orientation;
      }
      switch (orientation)
      {
      case 2: 
        return 4;
      case 4: 
        return 2;
      case 0: 
        return 0;
      }
      return 2;
    }
    
    public void addLayoutComponent(String paramString, Component paramComponent) {}
    
    public void layoutContainer(Container paramContainer)
    {
      Component[] arrayOfComponent = paramContainer.getComponents();
      if ((arrayOfComponent != null) && (arrayOfComponent.length > 0))
      {
        int i = arrayOfComponent.length;
        Insets localInsets = paramContainer.getInsets();
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        int i1 = 0;
        boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
        int i2 = !reverseButtons ? 1 : bool ? reverseButtons : 0;
        Dimension localDimension2;
        for (Dimension localDimension1 = 0; localDimension1 < i; localDimension1++)
        {
          localDimension2 = arrayOfComponent[localDimension1].getPreferredSize();
          j = Math.max(j, width);
          k = Math.max(k, height);
          m += width;
        }
        if (getSyncAllWidths()) {
          m = j * i;
        }
        m += (i - 1) * padding;
        switch (getOrientation(paramContainer))
        {
        case 2: 
          n = left;
          break;
        case 4: 
          n = paramContainer.getWidth() - right - m;
          break;
        case 0: 
          if ((getCentersChildren()) || (i < 2))
          {
            n = (paramContainer.getWidth() - m) / 2;
          }
          else
          {
            n = left;
            if (getSyncAllWidths()) {
              i1 = (paramContainer.getWidth() - left - right - m) / (i - 1) + j;
            } else {
              i1 = (paramContainer.getWidth() - left - right - m) / (i - 1);
            }
          }
          break;
        }
        for (localDimension1 = 0; localDimension1 < i; localDimension1++)
        {
          localDimension2 = i2 != 0 ? i - localDimension1 - 1 : localDimension1;
          Dimension localDimension3 = arrayOfComponent[localDimension2].getPreferredSize();
          if (getSyncAllWidths()) {
            arrayOfComponent[localDimension2].setBounds(n, top, j, k);
          } else {
            arrayOfComponent[localDimension2].setBounds(n, top, width, height);
          }
          if (i1 != 0) {
            n += i1;
          } else {
            n += arrayOfComponent[localDimension2].getWidth() + padding;
          }
        }
      }
    }
    
    public Dimension minimumLayoutSize(Container paramContainer)
    {
      if (paramContainer != null)
      {
        Component[] arrayOfComponent = paramContainer.getComponents();
        if ((arrayOfComponent != null) && (arrayOfComponent.length > 0))
        {
          int i = arrayOfComponent.length;
          int j = 0;
          Insets localInsets = paramContainer.getInsets();
          int k = top + bottom;
          int m = left + right;
          Dimension localDimension;
          if (syncAllWidths)
          {
            n = 0;
            for (i1 = 0; i1 < i; i1++)
            {
              localDimension = arrayOfComponent[i1].getPreferredSize();
              j = Math.max(j, height);
              n = Math.max(n, width);
            }
            return new Dimension(m + n * i + (i - 1) * padding, k + j);
          }
          int n = 0;
          for (int i1 = 0; i1 < i; i1++)
          {
            localDimension = arrayOfComponent[i1].getPreferredSize();
            j = Math.max(j, height);
            n += width;
          }
          n += (i - 1) * padding;
          return new Dimension(m + n, k + j);
        }
      }
      return new Dimension(0, 0);
    }
    
    public Dimension preferredLayoutSize(Container paramContainer)
    {
      return minimumLayoutSize(paramContainer);
    }
    
    public void removeLayoutComponent(Component paramComponent) {}
  }
  
  private static class ButtonFactory
  {
    private String text;
    private int mnemonic;
    private Icon icon;
    private int minimumWidth = -1;
    
    ButtonFactory(String paramString, int paramInt1, Icon paramIcon, int paramInt2)
    {
      text = paramString;
      mnemonic = paramInt1;
      icon = paramIcon;
      minimumWidth = paramInt2;
    }
    
    JButton createButton()
    {
      Object localObject;
      if (minimumWidth > 0) {
        localObject = new ConstrainedButton(text, minimumWidth);
      } else {
        localObject = new JButton(text);
      }
      if (icon != null) {
        ((JButton)localObject).setIcon(icon);
      }
      if (mnemonic != 0) {
        ((JButton)localObject).setMnemonic(mnemonic);
      }
      return (JButton)localObject;
    }
    
    private static class ConstrainedButton
      extends JButton
    {
      int minimumWidth;
      
      ConstrainedButton(String paramString, int paramInt)
      {
        super();
        minimumWidth = paramInt;
      }
      
      public Dimension getMinimumSize()
      {
        Dimension localDimension = super.getMinimumSize();
        width = Math.max(width, minimumWidth);
        return localDimension;
      }
      
      public Dimension getPreferredSize()
      {
        Dimension localDimension = super.getPreferredSize();
        width = Math.max(width, minimumWidth);
        return localDimension;
      }
    }
  }
  
  private class Handler
    implements ActionListener, MouseListener, PropertyChangeListener
  {
    private Handler() {}
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      optionPane.setInputValue(((JTextField)paramActionEvent.getSource()).getText());
    }
    
    public void mouseClicked(MouseEvent paramMouseEvent) {}
    
    public void mouseReleased(MouseEvent paramMouseEvent) {}
    
    public void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
    
    public void mousePressed(MouseEvent paramMouseEvent)
    {
      if (paramMouseEvent.getClickCount() == 2)
      {
        JList localJList = (JList)paramMouseEvent.getSource();
        int i = localJList.locationToIndex(paramMouseEvent.getPoint());
        optionPane.setInputValue(localJList.getModel().getElementAt(i));
        optionPane.setValue(Integer.valueOf(0));
      }
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      if (paramPropertyChangeEvent.getSource() == optionPane)
      {
        if ("ancestor" == paramPropertyChangeEvent.getPropertyName())
        {
          localObject = (JOptionPane)paramPropertyChangeEvent.getSource();
          int i;
          if (paramPropertyChangeEvent.getOldValue() == null) {
            i = 1;
          } else {
            i = 0;
          }
          switch (((JOptionPane)localObject).getMessageType())
          {
          case -1: 
            if (i != 0) {
              BasicLookAndFeel.playSound(optionPane, "OptionPane.informationSound");
            }
            break;
          case 3: 
            if (i != 0) {
              BasicLookAndFeel.playSound(optionPane, "OptionPane.questionSound");
            }
            break;
          case 1: 
            if (i != 0) {
              BasicLookAndFeel.playSound(optionPane, "OptionPane.informationSound");
            }
            break;
          case 2: 
            if (i != 0) {
              BasicLookAndFeel.playSound(optionPane, "OptionPane.warningSound");
            }
            break;
          case 0: 
            if (i != 0) {
              BasicLookAndFeel.playSound(optionPane, "OptionPane.errorSound");
            }
            break;
          default: 
            System.err.println("Undefined JOptionPane type: " + ((JOptionPane)localObject).getMessageType());
          }
        }
        Object localObject = paramPropertyChangeEvent.getPropertyName();
        if ((localObject == "options") || (localObject == "initialValue") || (localObject == "icon") || (localObject == "messageType") || (localObject == "optionType") || (localObject == "message") || (localObject == "selectionValues") || (localObject == "initialSelectionValue") || (localObject == "wantsInput"))
        {
          uninstallComponents();
          installComponents();
          optionPane.validate();
        }
        else if (localObject == "componentOrientation")
        {
          ComponentOrientation localComponentOrientation = (ComponentOrientation)paramPropertyChangeEvent.getNewValue();
          JOptionPane localJOptionPane = (JOptionPane)paramPropertyChangeEvent.getSource();
          if (localComponentOrientation != paramPropertyChangeEvent.getOldValue()) {
            localJOptionPane.applyComponentOrientation(localComponentOrientation);
          }
        }
      }
    }
  }
  
  private static class MultiplexingTextField
    extends JTextField
  {
    private KeyStroke[] strokes;
    
    MultiplexingTextField(int paramInt)
    {
      super();
    }
    
    void setKeyStrokes(KeyStroke[] paramArrayOfKeyStroke)
    {
      strokes = paramArrayOfKeyStroke;
    }
    
    protected boolean processKeyBinding(KeyStroke paramKeyStroke, KeyEvent paramKeyEvent, int paramInt, boolean paramBoolean)
    {
      boolean bool = super.processKeyBinding(paramKeyStroke, paramKeyEvent, paramInt, paramBoolean);
      if ((bool) && (paramInt != 2)) {
        for (int i = strokes.length - 1; i >= 0; i--) {
          if (strokes[i].equals(paramKeyStroke)) {
            return false;
          }
        }
      }
      return bool;
    }
  }
  
  public class PropertyChangeHandler
    implements PropertyChangeListener
  {
    public PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      BasicOptionPaneUI.this.getHandler().propertyChange(paramPropertyChangeEvent);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\BasicOptionPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
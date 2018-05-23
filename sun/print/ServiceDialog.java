package sun.print;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobPriority;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterInfo;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterMakeAndModel;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.NumberFormatter;

public class ServiceDialog
  extends JDialog
  implements ActionListener
{
  public static final int WAITING = 0;
  public static final int APPROVE = 1;
  public static final int CANCEL = 2;
  private static final String strBundle = "sun.print.resources.serviceui";
  private static final Insets panelInsets = new Insets(6, 6, 6, 6);
  private static final Insets compInsets = new Insets(3, 6, 3, 6);
  private static ResourceBundle messageRB;
  private JTabbedPane tpTabs;
  private JButton btnCancel;
  private JButton btnApprove;
  private PrintService[] services;
  private int defaultServiceIndex;
  private PrintRequestAttributeSet asOriginal;
  private HashPrintRequestAttributeSet asCurrent;
  private PrintService psCurrent;
  private DocFlavor docFlavor;
  private int status;
  private ValidatingFileChooser jfc;
  private GeneralPanel pnlGeneral;
  private PageSetupPanel pnlPageSetup;
  private AppearancePanel pnlAppearance;
  private boolean isAWT = false;
  static Class _keyEventClazz = null;
  
  public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Dialog paramDialog)
  {
    super(paramDialog, getMsg("dialog.printtitle"), true, paramGraphicsConfiguration);
    initPrintDialog(paramInt1, paramInt2, paramArrayOfPrintService, paramInt3, paramDocFlavor, paramPrintRequestAttributeSet);
  }
  
  public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Frame paramFrame)
  {
    super(paramFrame, getMsg("dialog.printtitle"), true, paramGraphicsConfiguration);
    initPrintDialog(paramInt1, paramInt2, paramArrayOfPrintService, paramInt3, paramDocFlavor, paramPrintRequestAttributeSet);
  }
  
  void initPrintDialog(int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, int paramInt3, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    services = paramArrayOfPrintService;
    defaultServiceIndex = paramInt3;
    asOriginal = paramPrintRequestAttributeSet;
    asCurrent = new HashPrintRequestAttributeSet(paramPrintRequestAttributeSet);
    psCurrent = paramArrayOfPrintService[paramInt3];
    docFlavor = paramDocFlavor;
    SunPageSelection localSunPageSelection = (SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class);
    if (localSunPageSelection != null) {
      isAWT = true;
    }
    Container localContainer = getContentPane();
    localContainer.setLayout(new BorderLayout());
    tpTabs = new JTabbedPane();
    tpTabs.setBorder(new EmptyBorder(5, 5, 5, 5));
    String str1 = getMsg("tab.general");
    int i = getVKMnemonic("tab.general");
    pnlGeneral = new GeneralPanel();
    tpTabs.add(str1, pnlGeneral);
    tpTabs.setMnemonicAt(0, i);
    String str2 = getMsg("tab.pagesetup");
    int j = getVKMnemonic("tab.pagesetup");
    pnlPageSetup = new PageSetupPanel();
    tpTabs.add(str2, pnlPageSetup);
    tpTabs.setMnemonicAt(1, j);
    String str3 = getMsg("tab.appearance");
    int k = getVKMnemonic("tab.appearance");
    pnlAppearance = new AppearancePanel();
    tpTabs.add(str3, pnlAppearance);
    tpTabs.setMnemonicAt(2, k);
    localContainer.add(tpTabs, "Center");
    updatePanels();
    JPanel localJPanel = new JPanel(new FlowLayout(4));
    btnApprove = createExitButton("button.print", this);
    localJPanel.add(btnApprove);
    getRootPane().setDefaultButton(btnApprove);
    btnCancel = createExitButton("button.cancel", this);
    handleEscKey(btnCancel);
    localJPanel.add(btnCancel);
    localContainer.add(localJPanel, "South");
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        dispose(2);
      }
    });
    getAccessibleContext().setAccessibleDescription(getMsg("dialog.printtitle"));
    setResizable(false);
    setLocation(paramInt1, paramInt2);
    pack();
  }
  
  public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Dialog paramDialog)
  {
    super(paramDialog, getMsg("dialog.pstitle"), true, paramGraphicsConfiguration);
    initPageDialog(paramInt1, paramInt2, paramPrintService, paramDocFlavor, paramPrintRequestAttributeSet);
  }
  
  public ServiceDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet, Frame paramFrame)
  {
    super(paramFrame, getMsg("dialog.pstitle"), true, paramGraphicsConfiguration);
    initPageDialog(paramInt1, paramInt2, paramPrintService, paramDocFlavor, paramPrintRequestAttributeSet);
  }
  
  void initPageDialog(int paramInt1, int paramInt2, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet)
  {
    psCurrent = paramPrintService;
    docFlavor = paramDocFlavor;
    asOriginal = paramPrintRequestAttributeSet;
    asCurrent = new HashPrintRequestAttributeSet(paramPrintRequestAttributeSet);
    Container localContainer = getContentPane();
    localContainer.setLayout(new BorderLayout());
    pnlPageSetup = new PageSetupPanel();
    localContainer.add(pnlPageSetup, "Center");
    pnlPageSetup.updateInfo();
    JPanel localJPanel = new JPanel(new FlowLayout(4));
    btnApprove = createExitButton("button.ok", this);
    localJPanel.add(btnApprove);
    getRootPane().setDefaultButton(btnApprove);
    btnCancel = createExitButton("button.cancel", this);
    handleEscKey(btnCancel);
    localJPanel.add(btnCancel);
    localContainer.add(localJPanel, "South");
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent)
      {
        dispose(2);
      }
    });
    getAccessibleContext().setAccessibleDescription(getMsg("dialog.pstitle"));
    setResizable(false);
    setLocation(paramInt1, paramInt2);
    pack();
  }
  
  private void handleEscKey(JButton paramJButton)
  {
    AbstractAction local3 = new AbstractAction()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        dispose(2);
      }
    };
    KeyStroke localKeyStroke = KeyStroke.getKeyStroke(27, 0);
    InputMap localInputMap = paramJButton.getInputMap(2);
    ActionMap localActionMap = paramJButton.getActionMap();
    if ((localInputMap != null) && (localActionMap != null))
    {
      localInputMap.put(localKeyStroke, "cancel");
      localActionMap.put("cancel", local3);
    }
  }
  
  public int getStatus()
  {
    return status;
  }
  
  public PrintRequestAttributeSet getAttributes()
  {
    if (status == 1) {
      return asCurrent;
    }
    return asOriginal;
  }
  
  public PrintService getPrintService()
  {
    if (status == 1) {
      return psCurrent;
    }
    return null;
  }
  
  public void dispose(int paramInt)
  {
    status = paramInt;
    super.dispose();
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    Object localObject = paramActionEvent.getSource();
    boolean bool = false;
    if (localObject == btnApprove)
    {
      bool = true;
      if (pnlGeneral != null) {
        if (pnlGeneral.isPrintToFileRequested()) {
          bool = showFileChooser();
        } else {
          asCurrent.remove(Destination.class);
        }
      }
    }
    dispose(bool ? 1 : 2);
  }
  
  private boolean showFileChooser()
  {
    Class localClass = Destination.class;
    Destination localDestination = (Destination)asCurrent.get(localClass);
    if (localDestination == null)
    {
      localDestination = (Destination)asOriginal.get(localClass);
      if (localDestination == null)
      {
        localDestination = (Destination)psCurrent.getDefaultAttributeValue(localClass);
        if (localDestination == null) {
          try
          {
            localDestination = new Destination(new URI("file:out.prn"));
          }
          catch (URISyntaxException localURISyntaxException) {}
        }
      }
    }
    File localFile;
    if (localDestination != null) {
      try
      {
        localFile = new File(localDestination.getURI());
      }
      catch (Exception localException1)
      {
        localFile = new File("out.prn");
      }
    } else {
      localFile = new File("out.prn");
    }
    ValidatingFileChooser localValidatingFileChooser = new ValidatingFileChooser(null);
    localValidatingFileChooser.setApproveButtonText(getMsg("button.ok"));
    localValidatingFileChooser.setDialogTitle(getMsg("dialog.printtofile"));
    localValidatingFileChooser.setDialogType(1);
    localValidatingFileChooser.setSelectedFile(localFile);
    int i = localValidatingFileChooser.showDialog(this, null);
    if (i == 0)
    {
      localFile = localValidatingFileChooser.getSelectedFile();
      try
      {
        asCurrent.add(new Destination(localFile.toURI()));
      }
      catch (Exception localException2)
      {
        asCurrent.remove(localClass);
      }
    }
    else
    {
      asCurrent.remove(localClass);
    }
    return i == 0;
  }
  
  private void updatePanels()
  {
    pnlGeneral.updateInfo();
    pnlPageSetup.updateInfo();
    pnlAppearance.updateInfo();
  }
  
  public static void initResource()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        try
        {
          ServiceDialog.access$102(ResourceBundle.getBundle("sun.print.resources.serviceui"));
          return null;
        }
        catch (MissingResourceException localMissingResourceException)
        {
          throw new Error("Fatal: Resource for ServiceUI is missing");
        }
      }
    });
  }
  
  public static String getMsg(String paramString)
  {
    try
    {
      return removeMnemonics(messageRB.getString(paramString));
    }
    catch (MissingResourceException localMissingResourceException)
    {
      throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + paramString + " key in resource");
    }
  }
  
  private static String removeMnemonics(String paramString)
  {
    int i = paramString.indexOf('&');
    int j = paramString.length();
    if ((i < 0) || (i == j - 1)) {
      return paramString;
    }
    int k = paramString.indexOf('&', i + 1);
    if (k == i + 1)
    {
      if (k + 1 == j) {
        return paramString.substring(0, i + 1);
      }
      return paramString.substring(0, i + 1) + removeMnemonics(paramString.substring(k + 1));
    }
    if (i == 0) {
      return removeMnemonics(paramString.substring(1));
    }
    return paramString.substring(0, i) + removeMnemonics(paramString.substring(i + 1));
  }
  
  private static char getMnemonic(String paramString)
  {
    String str = messageRB.getString(paramString).replace("&&", "");
    int i = str.indexOf('&');
    if ((0 <= i) && (i < str.length() - 1))
    {
      char c = str.charAt(i + 1);
      return Character.toUpperCase(c);
    }
    return '\000';
  }
  
  private static int getVKMnemonic(String paramString)
  {
    String str1 = String.valueOf(getMnemonic(paramString));
    if ((str1 == null) || (str1.length() != 1)) {
      return 0;
    }
    String str2 = "VK_" + str1.toUpperCase();
    try
    {
      if (_keyEventClazz == null) {
        _keyEventClazz = Class.forName("java.awt.event.KeyEvent", true, ServiceDialog.class.getClassLoader());
      }
      Field localField = _keyEventClazz.getDeclaredField(str2);
      int i = localField.getInt(null);
      return i;
    }
    catch (Exception localException) {}
    return 0;
  }
  
  private static URL getImageResource(String paramString)
  {
    URL localURL = (URL)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        URL localURL = ServiceDialog.class.getResource("resources/" + val$key);
        return localURL;
      }
    });
    if (localURL == null) {
      throw new Error("Fatal: Resource for ServiceUI is broken; there is no " + paramString + " key in resource");
    }
    return localURL;
  }
  
  private static JButton createButton(String paramString, ActionListener paramActionListener)
  {
    JButton localJButton = new JButton(getMsg(paramString));
    localJButton.setMnemonic(getMnemonic(paramString));
    localJButton.addActionListener(paramActionListener);
    return localJButton;
  }
  
  private static JButton createExitButton(String paramString, ActionListener paramActionListener)
  {
    String str = getMsg(paramString);
    JButton localJButton = new JButton(str);
    localJButton.addActionListener(paramActionListener);
    localJButton.getAccessibleContext().setAccessibleDescription(str);
    return localJButton;
  }
  
  private static JCheckBox createCheckBox(String paramString, ActionListener paramActionListener)
  {
    JCheckBox localJCheckBox = new JCheckBox(getMsg(paramString));
    localJCheckBox.setMnemonic(getMnemonic(paramString));
    localJCheckBox.addActionListener(paramActionListener);
    return localJCheckBox;
  }
  
  private static JRadioButton createRadioButton(String paramString, ActionListener paramActionListener)
  {
    JRadioButton localJRadioButton = new JRadioButton(getMsg(paramString));
    localJRadioButton.setMnemonic(getMnemonic(paramString));
    localJRadioButton.addActionListener(paramActionListener);
    return localJRadioButton;
  }
  
  public static void showNoPrintService(GraphicsConfiguration paramGraphicsConfiguration)
  {
    Frame localFrame = new Frame(paramGraphicsConfiguration);
    JOptionPane.showMessageDialog(localFrame, getMsg("dialog.noprintermsg"));
    localFrame.dispose();
  }
  
  private static void addToGB(Component paramComponent, Container paramContainer, GridBagLayout paramGridBagLayout, GridBagConstraints paramGridBagConstraints)
  {
    paramGridBagLayout.setConstraints(paramComponent, paramGridBagConstraints);
    paramContainer.add(paramComponent);
  }
  
  private static void addToBG(AbstractButton paramAbstractButton, Container paramContainer, ButtonGroup paramButtonGroup)
  {
    paramButtonGroup.add(paramAbstractButton);
    paramContainer.add(paramAbstractButton);
  }
  
  static
  {
    initResource();
  }
  
  private class AppearancePanel
    extends JPanel
  {
    private ServiceDialog.ChromaticityPanel pnlChromaticity;
    private ServiceDialog.QualityPanel pnlQuality;
    private ServiceDialog.JobAttributesPanel pnlJobAttributes;
    private ServiceDialog.SidesPanel pnlSides;
    
    public AppearancePanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      fill = 1;
      insets = ServiceDialog.panelInsets;
      weightx = 1.0D;
      weighty = 1.0D;
      gridwidth = -1;
      pnlChromaticity = new ServiceDialog.ChromaticityPanel(ServiceDialog.this);
      ServiceDialog.addToGB(pnlChromaticity, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 0;
      pnlQuality = new ServiceDialog.QualityPanel(ServiceDialog.this);
      ServiceDialog.addToGB(pnlQuality, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 1;
      pnlSides = new ServiceDialog.SidesPanel(ServiceDialog.this);
      ServiceDialog.addToGB(pnlSides, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 0;
      pnlJobAttributes = new ServiceDialog.JobAttributesPanel(ServiceDialog.this);
      ServiceDialog.addToGB(pnlJobAttributes, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public void updateInfo()
    {
      pnlChromaticity.updateInfo();
      pnlQuality.updateInfo();
      pnlSides.updateInfo();
      pnlJobAttributes.updateInfo();
    }
  }
  
  private class ChromaticityPanel
    extends JPanel
    implements ActionListener
  {
    private final String strTitle = ServiceDialog.getMsg("border.chromaticity");
    private JRadioButton rbMonochrome;
    private JRadioButton rbColor;
    
    public ChromaticityPanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      setBorder(BorderFactory.createTitledBorder(strTitle));
      fill = 1;
      gridwidth = 0;
      weighty = 1.0D;
      ButtonGroup localButtonGroup = new ButtonGroup();
      rbMonochrome = ServiceDialog.createRadioButton("radiobutton.monochrome", this);
      rbMonochrome.setSelected(true);
      localButtonGroup.add(rbMonochrome);
      ServiceDialog.addToGB(rbMonochrome, this, localGridBagLayout, localGridBagConstraints);
      rbColor = ServiceDialog.createRadioButton("radiobutton.color", this);
      localButtonGroup.add(rbColor);
      ServiceDialog.addToGB(rbColor, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      Object localObject = paramActionEvent.getSource();
      if (localObject == rbMonochrome) {
        asCurrent.add(Chromaticity.MONOCHROME);
      } else if (localObject == rbColor) {
        asCurrent.add(Chromaticity.COLOR);
      }
    }
    
    public void updateInfo()
    {
      Class localClass = Chromaticity.class;
      boolean bool1 = false;
      boolean bool2 = false;
      if (isAWT)
      {
        bool1 = true;
        bool2 = true;
      }
      else if (psCurrent.isAttributeCategorySupported(localClass))
      {
        localObject = psCurrent.getSupportedAttributeValues(localClass, docFlavor, asCurrent);
        if ((localObject instanceof Chromaticity[]))
        {
          Chromaticity[] arrayOfChromaticity = (Chromaticity[])localObject;
          for (int i = 0; i < arrayOfChromaticity.length; i++)
          {
            Chromaticity localChromaticity = arrayOfChromaticity[i];
            if (localChromaticity == Chromaticity.MONOCHROME) {
              bool1 = true;
            } else if (localChromaticity == Chromaticity.COLOR) {
              bool2 = true;
            }
          }
        }
      }
      rbMonochrome.setEnabled(bool1);
      rbColor.setEnabled(bool2);
      Object localObject = (Chromaticity)asCurrent.get(localClass);
      if (localObject == null)
      {
        localObject = (Chromaticity)psCurrent.getDefaultAttributeValue(localClass);
        if (localObject == null) {
          localObject = Chromaticity.MONOCHROME;
        }
      }
      if (localObject == Chromaticity.MONOCHROME) {
        rbMonochrome.setSelected(true);
      } else {
        rbColor.setSelected(true);
      }
    }
  }
  
  private class CopiesPanel
    extends JPanel
    implements ActionListener, ChangeListener
  {
    private final String strTitle = ServiceDialog.getMsg("border.copies");
    private SpinnerNumberModel snModel;
    private JSpinner spinCopies;
    private JLabel lblCopies;
    private JCheckBox cbCollate;
    private boolean scSupported;
    
    public CopiesPanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      setBorder(BorderFactory.createTitledBorder(strTitle));
      fill = 2;
      insets = ServiceDialog.compInsets;
      lblCopies = new JLabel(ServiceDialog.getMsg("label.numcopies"), 11);
      lblCopies.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.numcopies"));
      lblCopies.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.numcopies"));
      ServiceDialog.addToGB(lblCopies, this, localGridBagLayout, localGridBagConstraints);
      snModel = new SpinnerNumberModel(1, 1, 999, 1);
      spinCopies = new JSpinner(snModel);
      lblCopies.setLabelFor(spinCopies);
      ((JSpinner.NumberEditor)spinCopies.getEditor()).getTextField().setColumns(3);
      spinCopies.addChangeListener(this);
      gridwidth = 0;
      ServiceDialog.addToGB(spinCopies, this, localGridBagLayout, localGridBagConstraints);
      cbCollate = ServiceDialog.createCheckBox("checkbox.collate", this);
      cbCollate.setEnabled(false);
      ServiceDialog.addToGB(cbCollate, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (cbCollate.isSelected()) {
        asCurrent.add(SheetCollate.COLLATED);
      } else {
        asCurrent.add(SheetCollate.UNCOLLATED);
      }
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      updateCollateCB();
      asCurrent.add(new Copies(snModel.getNumber().intValue()));
    }
    
    private void updateCollateCB()
    {
      int i = snModel.getNumber().intValue();
      if (isAWT) {
        cbCollate.setEnabled(true);
      } else {
        cbCollate.setEnabled((i > 1) && (scSupported));
      }
    }
    
    public void updateInfo()
    {
      Class localClass1 = Copies.class;
      Class localClass2 = CopiesSupported.class;
      Class localClass3 = SheetCollate.class;
      boolean bool = false;
      scSupported = false;
      if (psCurrent.isAttributeCategorySupported(localClass1)) {
        bool = true;
      }
      CopiesSupported localCopiesSupported = (CopiesSupported)psCurrent.getSupportedAttributeValues(localClass1, null, null);
      if (localCopiesSupported == null) {
        localCopiesSupported = new CopiesSupported(1, 999);
      }
      Copies localCopies = (Copies)asCurrent.get(localClass1);
      if (localCopies == null)
      {
        localCopies = (Copies)psCurrent.getDefaultAttributeValue(localClass1);
        if (localCopies == null) {
          localCopies = new Copies(1);
        }
      }
      spinCopies.setEnabled(bool);
      lblCopies.setEnabled(bool);
      int[][] arrayOfInt = localCopiesSupported.getMembers();
      int i;
      int j;
      if ((arrayOfInt.length > 0) && (arrayOfInt[0].length > 0))
      {
        i = arrayOfInt[0][0];
        j = arrayOfInt[0][1];
      }
      else
      {
        i = 1;
        j = Integer.MAX_VALUE;
      }
      snModel.setMinimum(new Integer(i));
      snModel.setMaximum(new Integer(j));
      int k = localCopies.getValue();
      if ((k < i) || (k > j)) {
        k = i;
      }
      snModel.setValue(new Integer(k));
      if (psCurrent.isAttributeCategorySupported(localClass3)) {
        scSupported = true;
      }
      SheetCollate localSheetCollate = (SheetCollate)asCurrent.get(localClass3);
      if (localSheetCollate == null)
      {
        localSheetCollate = (SheetCollate)psCurrent.getDefaultAttributeValue(localClass3);
        if (localSheetCollate == null) {
          localSheetCollate = SheetCollate.UNCOLLATED;
        }
      }
      cbCollate.setSelected(localSheetCollate == SheetCollate.COLLATED);
      updateCollateCB();
    }
  }
  
  private class GeneralPanel
    extends JPanel
  {
    private ServiceDialog.PrintServicePanel pnlPrintService;
    private ServiceDialog.PrintRangePanel pnlPrintRange;
    private ServiceDialog.CopiesPanel pnlCopies;
    
    public GeneralPanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      fill = 1;
      insets = ServiceDialog.panelInsets;
      weightx = 1.0D;
      weighty = 1.0D;
      gridwidth = 0;
      pnlPrintService = new ServiceDialog.PrintServicePanel(ServiceDialog.this);
      ServiceDialog.addToGB(pnlPrintService, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = -1;
      pnlPrintRange = new ServiceDialog.PrintRangePanel(ServiceDialog.this);
      ServiceDialog.addToGB(pnlPrintRange, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 0;
      pnlCopies = new ServiceDialog.CopiesPanel(ServiceDialog.this);
      ServiceDialog.addToGB(pnlCopies, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public boolean isPrintToFileRequested()
    {
      return pnlPrintService.isPrintToFileSelected();
    }
    
    public void updateInfo()
    {
      pnlPrintService.updateInfo();
      pnlPrintRange.updateInfo();
      pnlCopies.updateInfo();
    }
  }
  
  private class IconRadioButton
    extends JPanel
  {
    private JRadioButton rb;
    private JLabel lbl;
    
    public IconRadioButton(String paramString1, String paramString2, boolean paramBoolean, ButtonGroup paramButtonGroup, ActionListener paramActionListener)
    {
      super();
      final URL localURL = ServiceDialog.getImageResource(paramString2);
      Icon localIcon = (Icon)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          ImageIcon localImageIcon = new ImageIcon(localURL);
          return localImageIcon;
        }
      });
      lbl = new JLabel(localIcon);
      add(lbl);
      rb = ServiceDialog.createRadioButton(paramString1, paramActionListener);
      rb.setSelected(paramBoolean);
      ServiceDialog.addToBG(rb, this, paramButtonGroup);
    }
    
    public void addActionListener(ActionListener paramActionListener)
    {
      rb.addActionListener(paramActionListener);
    }
    
    public boolean isSameAs(Object paramObject)
    {
      return rb == paramObject;
    }
    
    public void setEnabled(boolean paramBoolean)
    {
      rb.setEnabled(paramBoolean);
      lbl.setEnabled(paramBoolean);
    }
    
    public boolean isSelected()
    {
      return rb.isSelected();
    }
    
    public void setSelected(boolean paramBoolean)
    {
      rb.setSelected(paramBoolean);
    }
  }
  
  private class JobAttributesPanel
    extends JPanel
    implements ActionListener, ChangeListener, FocusListener
  {
    private final String strTitle = ServiceDialog.getMsg("border.jobattributes");
    private JLabel lblPriority;
    private JLabel lblJobName;
    private JLabel lblUserName;
    private JSpinner spinPriority;
    private SpinnerNumberModel snModel;
    private JCheckBox cbJobSheets;
    private JTextField tfJobName;
    private JTextField tfUserName;
    
    public JobAttributesPanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      setBorder(BorderFactory.createTitledBorder(strTitle));
      fill = 0;
      insets = ServiceDialog.compInsets;
      weighty = 1.0D;
      cbJobSheets = ServiceDialog.createCheckBox("checkbox.jobsheets", this);
      anchor = 21;
      ServiceDialog.addToGB(cbJobSheets, this, localGridBagLayout, localGridBagConstraints);
      JPanel localJPanel = new JPanel();
      lblPriority = new JLabel(ServiceDialog.getMsg("label.priority"), 11);
      lblPriority.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.priority"));
      localJPanel.add(lblPriority);
      snModel = new SpinnerNumberModel(1, 1, 100, 1);
      spinPriority = new JSpinner(snModel);
      lblPriority.setLabelFor(spinPriority);
      ((JSpinner.NumberEditor)spinPriority.getEditor()).getTextField().setColumns(3);
      spinPriority.addChangeListener(this);
      localJPanel.add(spinPriority);
      anchor = 22;
      gridwidth = 0;
      localJPanel.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.priority"));
      ServiceDialog.addToGB(localJPanel, this, localGridBagLayout, localGridBagConstraints);
      fill = 2;
      anchor = 10;
      weightx = 0.0D;
      gridwidth = 1;
      char c1 = ServiceDialog.getMnemonic("label.jobname");
      lblJobName = new JLabel(ServiceDialog.getMsg("label.jobname"), 11);
      lblJobName.setDisplayedMnemonic(c1);
      ServiceDialog.addToGB(lblJobName, this, localGridBagLayout, localGridBagConstraints);
      weightx = 1.0D;
      gridwidth = 0;
      tfJobName = new JTextField();
      lblJobName.setLabelFor(tfJobName);
      tfJobName.addFocusListener(this);
      tfJobName.setFocusAccelerator(c1);
      tfJobName.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.jobname"));
      ServiceDialog.addToGB(tfJobName, this, localGridBagLayout, localGridBagConstraints);
      weightx = 0.0D;
      gridwidth = 1;
      char c2 = ServiceDialog.getMnemonic("label.username");
      lblUserName = new JLabel(ServiceDialog.getMsg("label.username"), 11);
      lblUserName.setDisplayedMnemonic(c2);
      ServiceDialog.addToGB(lblUserName, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 0;
      tfUserName = new JTextField();
      lblUserName.setLabelFor(tfUserName);
      tfUserName.addFocusListener(this);
      tfUserName.setFocusAccelerator(c2);
      tfUserName.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.username"));
      ServiceDialog.addToGB(tfUserName, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      if (cbJobSheets.isSelected()) {
        asCurrent.add(JobSheets.STANDARD);
      } else {
        asCurrent.add(JobSheets.NONE);
      }
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      asCurrent.add(new JobPriority(snModel.getNumber().intValue()));
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      Object localObject = paramFocusEvent.getSource();
      if (localObject == tfJobName) {
        asCurrent.add(new JobName(tfJobName.getText(), Locale.getDefault()));
      } else if (localObject == tfUserName) {
        asCurrent.add(new RequestingUserName(tfUserName.getText(), Locale.getDefault()));
      }
    }
    
    public void focusGained(FocusEvent paramFocusEvent) {}
    
    public void updateInfo()
    {
      Class localClass1 = JobSheets.class;
      Class localClass2 = JobPriority.class;
      Class localClass3 = JobName.class;
      Class localClass4 = RequestingUserName.class;
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      boolean bool4 = false;
      if (psCurrent.isAttributeCategorySupported(localClass1)) {
        bool1 = true;
      }
      JobSheets localJobSheets = (JobSheets)asCurrent.get(localClass1);
      if (localJobSheets == null)
      {
        localJobSheets = (JobSheets)psCurrent.getDefaultAttributeValue(localClass1);
        if (localJobSheets == null) {
          localJobSheets = JobSheets.NONE;
        }
      }
      cbJobSheets.setSelected(localJobSheets != JobSheets.NONE);
      cbJobSheets.setEnabled(bool1);
      if ((!isAWT) && (psCurrent.isAttributeCategorySupported(localClass2))) {
        bool2 = true;
      }
      JobPriority localJobPriority = (JobPriority)asCurrent.get(localClass2);
      if (localJobPriority == null)
      {
        localJobPriority = (JobPriority)psCurrent.getDefaultAttributeValue(localClass2);
        if (localJobPriority == null) {
          localJobPriority = new JobPriority(1);
        }
      }
      int i = localJobPriority.getValue();
      if ((i < 1) || (i > 100)) {
        i = 1;
      }
      snModel.setValue(new Integer(i));
      lblPriority.setEnabled(bool2);
      spinPriority.setEnabled(bool2);
      if (psCurrent.isAttributeCategorySupported(localClass3)) {
        bool3 = true;
      }
      JobName localJobName = (JobName)asCurrent.get(localClass3);
      if (localJobName == null)
      {
        localJobName = (JobName)psCurrent.getDefaultAttributeValue(localClass3);
        if (localJobName == null) {
          localJobName = new JobName("", Locale.getDefault());
        }
      }
      tfJobName.setText(localJobName.getValue());
      tfJobName.setEnabled(bool3);
      lblJobName.setEnabled(bool3);
      if ((!isAWT) && (psCurrent.isAttributeCategorySupported(localClass4))) {
        bool4 = true;
      }
      RequestingUserName localRequestingUserName = (RequestingUserName)asCurrent.get(localClass4);
      if (localRequestingUserName == null)
      {
        localRequestingUserName = (RequestingUserName)psCurrent.getDefaultAttributeValue(localClass4);
        if (localRequestingUserName == null) {
          localRequestingUserName = new RequestingUserName("", Locale.getDefault());
        }
      }
      tfUserName.setText(localRequestingUserName.getValue());
      tfUserName.setEnabled(bool4);
      lblUserName.setEnabled(bool4);
    }
  }
  
  private class MarginsPanel
    extends JPanel
    implements ActionListener, FocusListener
  {
    private final String strTitle = ServiceDialog.getMsg("border.margins");
    private JFormattedTextField leftMargin;
    private JFormattedTextField rightMargin;
    private JFormattedTextField topMargin;
    private JFormattedTextField bottomMargin;
    private JLabel lblLeft;
    private JLabel lblRight;
    private JLabel lblTop;
    private JLabel lblBottom;
    private int units = 1000;
    private float lmVal = -1.0F;
    private float rmVal = -1.0F;
    private float tmVal = -1.0F;
    private float bmVal = -1.0F;
    private Float lmObj;
    private Float rmObj;
    private Float tmObj;
    private Float bmObj;
    
    public MarginsPanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      fill = 2;
      weightx = 1.0D;
      weighty = 0.0D;
      insets = ServiceDialog.compInsets;
      setLayout(localGridBagLayout);
      setBorder(BorderFactory.createTitledBorder(strTitle));
      String str1 = "label.millimetres";
      String str2 = Locale.getDefault().getCountry();
      if ((str2 != null) && ((str2.equals("")) || (str2.equals(Locale.US.getCountry())) || (str2.equals(Locale.CANADA.getCountry()))))
      {
        str1 = "label.inches";
        units = 25400;
      }
      String str3 = ServiceDialog.getMsg(str1);
      DecimalFormat localDecimalFormat;
      if (units == 1000)
      {
        localDecimalFormat = new DecimalFormat("###.##");
        localDecimalFormat.setMaximumIntegerDigits(3);
      }
      else
      {
        localDecimalFormat = new DecimalFormat("##.##");
        localDecimalFormat.setMaximumIntegerDigits(2);
      }
      localDecimalFormat.setMinimumFractionDigits(1);
      localDecimalFormat.setMaximumFractionDigits(2);
      localDecimalFormat.setMinimumIntegerDigits(1);
      localDecimalFormat.setParseIntegerOnly(false);
      localDecimalFormat.setDecimalSeparatorAlwaysShown(true);
      NumberFormatter localNumberFormatter = new NumberFormatter(localDecimalFormat);
      localNumberFormatter.setMinimum(new Float(0.0F));
      localNumberFormatter.setMaximum(new Float(999.0F));
      localNumberFormatter.setAllowsInvalid(true);
      localNumberFormatter.setCommitsOnValidEdit(true);
      leftMargin = new JFormattedTextField(localNumberFormatter);
      leftMargin.addFocusListener(this);
      leftMargin.addActionListener(this);
      leftMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.leftmargin"));
      rightMargin = new JFormattedTextField(localNumberFormatter);
      rightMargin.addFocusListener(this);
      rightMargin.addActionListener(this);
      rightMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.rightmargin"));
      topMargin = new JFormattedTextField(localNumberFormatter);
      topMargin.addFocusListener(this);
      topMargin.addActionListener(this);
      topMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.topmargin"));
      topMargin = new JFormattedTextField(localNumberFormatter);
      bottomMargin = new JFormattedTextField(localNumberFormatter);
      bottomMargin.addFocusListener(this);
      bottomMargin.addActionListener(this);
      bottomMargin.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.bottommargin"));
      topMargin = new JFormattedTextField(localNumberFormatter);
      gridwidth = -1;
      lblLeft = new JLabel(ServiceDialog.getMsg("label.leftmargin") + " " + str3, 10);
      lblLeft.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.leftmargin"));
      lblLeft.setLabelFor(leftMargin);
      ServiceDialog.addToGB(lblLeft, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 0;
      lblRight = new JLabel(ServiceDialog.getMsg("label.rightmargin") + " " + str3, 10);
      lblRight.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.rightmargin"));
      lblRight.setLabelFor(rightMargin);
      ServiceDialog.addToGB(lblRight, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = -1;
      ServiceDialog.addToGB(leftMargin, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 0;
      ServiceDialog.addToGB(rightMargin, this, localGridBagLayout, localGridBagConstraints);
      ServiceDialog.addToGB(new JPanel(), this, localGridBagLayout, localGridBagConstraints);
      gridwidth = -1;
      lblTop = new JLabel(ServiceDialog.getMsg("label.topmargin") + " " + str3, 10);
      lblTop.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.topmargin"));
      lblTop.setLabelFor(topMargin);
      ServiceDialog.addToGB(lblTop, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 0;
      lblBottom = new JLabel(ServiceDialog.getMsg("label.bottommargin") + " " + str3, 10);
      lblBottom.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.bottommargin"));
      lblBottom.setLabelFor(bottomMargin);
      ServiceDialog.addToGB(lblBottom, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = -1;
      ServiceDialog.addToGB(topMargin, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 0;
      ServiceDialog.addToGB(bottomMargin, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      Object localObject = paramActionEvent.getSource();
      updateMargins(localObject);
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      Object localObject = paramFocusEvent.getSource();
      updateMargins(localObject);
    }
    
    public void focusGained(FocusEvent paramFocusEvent) {}
    
    public void updateMargins(Object paramObject)
    {
      if (!(paramObject instanceof JFormattedTextField)) {
        return;
      }
      Object localObject = (JFormattedTextField)paramObject;
      Float localFloat1 = (Float)((JFormattedTextField)localObject).getValue();
      if (localFloat1 == null) {
        return;
      }
      if ((localObject == leftMargin) && (localFloat1.equals(lmObj))) {
        return;
      }
      if ((localObject == rightMargin) && (localFloat1.equals(rmObj))) {
        return;
      }
      if ((localObject == topMargin) && (localFloat1.equals(tmObj))) {
        return;
      }
      if ((localObject == bottomMargin) && (localFloat1.equals(bmObj))) {
        return;
      }
      localObject = (Float)leftMargin.getValue();
      localFloat1 = (Float)rightMargin.getValue();
      Float localFloat2 = (Float)topMargin.getValue();
      Float localFloat3 = (Float)bottomMargin.getValue();
      float f1 = ((Float)localObject).floatValue();
      float f2 = localFloat1.floatValue();
      float f3 = localFloat2.floatValue();
      float f4 = localFloat3.floatValue();
      Class localClass = OrientationRequested.class;
      OrientationRequested localOrientationRequested = (OrientationRequested)asCurrent.get(localClass);
      if (localOrientationRequested == null) {
        localOrientationRequested = (OrientationRequested)psCurrent.getDefaultAttributeValue(localClass);
      }
      float f5;
      if (localOrientationRequested == OrientationRequested.REVERSE_PORTRAIT)
      {
        f5 = f1;
        f1 = f2;
        f2 = f5;
        f5 = f3;
        f3 = f4;
        f4 = f5;
      }
      else if (localOrientationRequested == OrientationRequested.LANDSCAPE)
      {
        f5 = f1;
        f1 = f3;
        f3 = f2;
        f2 = f4;
        f4 = f5;
      }
      else if (localOrientationRequested == OrientationRequested.REVERSE_LANDSCAPE)
      {
        f5 = f1;
        f1 = f4;
        f4 = f2;
        f2 = f3;
        f3 = f5;
      }
      MediaPrintableArea localMediaPrintableArea;
      if ((localMediaPrintableArea = validateMargins(f1, f2, f3, f4)) != null)
      {
        asCurrent.add(localMediaPrintableArea);
        lmVal = f1;
        rmVal = f2;
        tmVal = f3;
        bmVal = f4;
        lmObj = ((Float)localObject);
        rmObj = localFloat1;
        tmObj = localFloat2;
        bmObj = localFloat3;
      }
      else
      {
        if ((lmObj == null) || (rmObj == null) || (tmObj == null) || (rmObj == null)) {
          return;
        }
        leftMargin.setValue(lmObj);
        rightMargin.setValue(rmObj);
        topMargin.setValue(tmObj);
        bottomMargin.setValue(bmObj);
      }
    }
    
    private MediaPrintableArea validateMargins(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      Class localClass = MediaPrintableArea.class;
      MediaPrintableArea localMediaPrintableArea = null;
      MediaSize localMediaSize = null;
      Media localMedia = (Media)asCurrent.get(Media.class);
      if ((localMedia == null) || (!(localMedia instanceof MediaSizeName))) {
        localMedia = (Media)psCurrent.getDefaultAttributeValue(Media.class);
      }
      Object localObject1;
      if ((localMedia != null) && ((localMedia instanceof MediaSizeName)))
      {
        localObject1 = (MediaSizeName)localMedia;
        localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject1);
      }
      if (localMediaSize == null) {
        localMediaSize = new MediaSize(8.5F, 11.0F, 25400);
      }
      if (localMedia != null)
      {
        localObject1 = new HashPrintRequestAttributeSet(asCurrent);
        ((PrintRequestAttributeSet)localObject1).add(localMedia);
        Object localObject2 = psCurrent.getSupportedAttributeValues(localClass, docFlavor, (AttributeSet)localObject1);
        if (((localObject2 instanceof MediaPrintableArea[])) && (((MediaPrintableArea[])localObject2).length > 0)) {
          localMediaPrintableArea = ((MediaPrintableArea[])(MediaPrintableArea[])localObject2)[0];
        }
      }
      if (localMediaPrintableArea == null) {
        localMediaPrintableArea = new MediaPrintableArea(0.0F, 0.0F, localMediaSize.getX(units), localMediaSize.getY(units), units);
      }
      float f1 = localMediaSize.getX(units);
      float f2 = localMediaSize.getY(units);
      float f3 = paramFloat1;
      float f4 = paramFloat3;
      float f5 = f1 - paramFloat1 - paramFloat2;
      float f6 = f2 - paramFloat3 - paramFloat4;
      if ((f5 <= 0.0F) || (f6 <= 0.0F) || (f3 < 0.0F) || (f4 < 0.0F) || (f3 < localMediaPrintableArea.getX(units)) || (f5 > localMediaPrintableArea.getWidth(units)) || (f4 < localMediaPrintableArea.getY(units)) || (f6 > localMediaPrintableArea.getHeight(units))) {
        return null;
      }
      return new MediaPrintableArea(paramFloat1, paramFloat3, f5, f6, units);
    }
    
    public void updateInfo()
    {
      if (isAWT)
      {
        leftMargin.setEnabled(false);
        rightMargin.setEnabled(false);
        topMargin.setEnabled(false);
        bottomMargin.setEnabled(false);
        lblLeft.setEnabled(false);
        lblRight.setEnabled(false);
        lblTop.setEnabled(false);
        lblBottom.setEnabled(false);
        return;
      }
      Class localClass1 = MediaPrintableArea.class;
      MediaPrintableArea localMediaPrintableArea1 = (MediaPrintableArea)asCurrent.get(localClass1);
      MediaPrintableArea localMediaPrintableArea2 = null;
      MediaSize localMediaSize = null;
      Media localMedia = (Media)asCurrent.get(Media.class);
      if ((localMedia == null) || (!(localMedia instanceof MediaSizeName))) {
        localMedia = (Media)psCurrent.getDefaultAttributeValue(Media.class);
      }
      Object localObject1;
      if ((localMedia != null) && ((localMedia instanceof MediaSizeName)))
      {
        localObject1 = (MediaSizeName)localMedia;
        localMediaSize = MediaSize.getMediaSizeForName((MediaSizeName)localObject1);
      }
      if (localMediaSize == null) {
        localMediaSize = new MediaSize(8.5F, 11.0F, 25400);
      }
      if (localMedia != null)
      {
        localObject1 = new HashPrintRequestAttributeSet(asCurrent);
        ((PrintRequestAttributeSet)localObject1).add(localMedia);
        Object localObject2 = psCurrent.getSupportedAttributeValues(localClass1, docFlavor, (AttributeSet)localObject1);
        if (((localObject2 instanceof MediaPrintableArea[])) && (((MediaPrintableArea[])localObject2).length > 0)) {
          localMediaPrintableArea2 = ((MediaPrintableArea[])(MediaPrintableArea[])localObject2)[0];
        } else if ((localObject2 instanceof MediaPrintableArea)) {
          localMediaPrintableArea2 = (MediaPrintableArea)localObject2;
        }
      }
      if (localMediaPrintableArea2 == null) {
        localMediaPrintableArea2 = new MediaPrintableArea(0.0F, 0.0F, localMediaSize.getX(units), localMediaSize.getY(units), units);
      }
      float f1 = localMediaSize.getX(25400);
      float f2 = localMediaSize.getY(25400);
      float f3 = 5.0F;
      float f4;
      if (f1 > f3) {
        f4 = 1.0F;
      } else {
        f4 = f1 / f3;
      }
      float f5;
      if (f2 > f3) {
        f5 = 1.0F;
      } else {
        f5 = f2 / f3;
      }
      if (localMediaPrintableArea1 == null)
      {
        localMediaPrintableArea1 = new MediaPrintableArea(f4, f5, f1 - 2.0F * f4, f2 - 2.0F * f5, 25400);
        asCurrent.add(localMediaPrintableArea1);
      }
      float f6 = localMediaPrintableArea1.getX(units);
      float f7 = localMediaPrintableArea1.getY(units);
      float f8 = localMediaPrintableArea1.getWidth(units);
      float f9 = localMediaPrintableArea1.getHeight(units);
      float f10 = localMediaPrintableArea2.getX(units);
      float f11 = localMediaPrintableArea2.getY(units);
      float f12 = localMediaPrintableArea2.getWidth(units);
      float f13 = localMediaPrintableArea2.getHeight(units);
      int i = 0;
      f1 = localMediaSize.getX(units);
      f2 = localMediaSize.getY(units);
      if (lmVal >= 0.0F)
      {
        i = 1;
        if (lmVal + rmVal > f1)
        {
          if (f8 > f12) {
            f8 = f12;
          }
          f6 = (f1 - f8) / 2.0F;
        }
        else
        {
          f6 = lmVal >= f10 ? lmVal : f10;
          f8 = f1 - f6 - rmVal;
        }
        if (tmVal + bmVal > f2)
        {
          if (f9 > f13) {
            f9 = f13;
          }
          f7 = (f2 - f9) / 2.0F;
        }
        else
        {
          f7 = tmVal >= f11 ? tmVal : f11;
          f9 = f2 - f7 - bmVal;
        }
      }
      if (f6 < f10)
      {
        i = 1;
        f6 = f10;
      }
      if (f7 < f11)
      {
        i = 1;
        f7 = f11;
      }
      if (f8 > f12)
      {
        i = 1;
        f8 = f12;
      }
      if (f9 > f13)
      {
        i = 1;
        f9 = f13;
      }
      if ((f6 + f8 > f10 + f12) || (f8 <= 0.0F))
      {
        i = 1;
        f6 = f10;
        f8 = f12;
      }
      if ((f7 + f9 > f11 + f13) || (f9 <= 0.0F))
      {
        i = 1;
        f7 = f11;
        f9 = f13;
      }
      if (i != 0)
      {
        localMediaPrintableArea1 = new MediaPrintableArea(f6, f7, f8, f9, units);
        asCurrent.add(localMediaPrintableArea1);
      }
      lmVal = f6;
      tmVal = f7;
      rmVal = (localMediaSize.getX(units) - f6 - f8);
      bmVal = (localMediaSize.getY(units) - f7 - f9);
      lmObj = new Float(lmVal);
      rmObj = new Float(rmVal);
      tmObj = new Float(tmVal);
      bmObj = new Float(bmVal);
      Class localClass2 = OrientationRequested.class;
      OrientationRequested localOrientationRequested = (OrientationRequested)asCurrent.get(localClass2);
      if (localOrientationRequested == null) {
        localOrientationRequested = (OrientationRequested)psCurrent.getDefaultAttributeValue(localClass2);
      }
      Float localFloat;
      if (localOrientationRequested == OrientationRequested.REVERSE_PORTRAIT)
      {
        localFloat = lmObj;
        lmObj = rmObj;
        rmObj = localFloat;
        localFloat = tmObj;
        tmObj = bmObj;
        bmObj = localFloat;
      }
      else if (localOrientationRequested == OrientationRequested.LANDSCAPE)
      {
        localFloat = lmObj;
        lmObj = bmObj;
        bmObj = rmObj;
        rmObj = tmObj;
        tmObj = localFloat;
      }
      else if (localOrientationRequested == OrientationRequested.REVERSE_LANDSCAPE)
      {
        localFloat = lmObj;
        lmObj = tmObj;
        tmObj = rmObj;
        rmObj = bmObj;
        bmObj = localFloat;
      }
      leftMargin.setValue(lmObj);
      rightMargin.setValue(rmObj);
      topMargin.setValue(tmObj);
      bottomMargin.setValue(bmObj);
    }
  }
  
  private class MediaPanel
    extends JPanel
    implements ItemListener
  {
    private final String strTitle = ServiceDialog.getMsg("border.media");
    private JLabel lblSize;
    private JLabel lblSource;
    private JComboBox cbSize;
    private JComboBox cbSource;
    private Vector sizes = new Vector();
    private Vector sources = new Vector();
    private ServiceDialog.MarginsPanel pnlMargins = null;
    
    public MediaPanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      setBorder(BorderFactory.createTitledBorder(strTitle));
      cbSize = new JComboBox();
      cbSource = new JComboBox();
      fill = 1;
      insets = ServiceDialog.compInsets;
      weighty = 1.0D;
      weightx = 0.0D;
      lblSize = new JLabel(ServiceDialog.getMsg("label.size"), 11);
      lblSize.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.size"));
      lblSize.setLabelFor(cbSize);
      ServiceDialog.addToGB(lblSize, this, localGridBagLayout, localGridBagConstraints);
      weightx = 1.0D;
      gridwidth = 0;
      ServiceDialog.addToGB(cbSize, this, localGridBagLayout, localGridBagConstraints);
      weightx = 0.0D;
      gridwidth = 1;
      lblSource = new JLabel(ServiceDialog.getMsg("label.source"), 11);
      lblSource.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.source"));
      lblSource.setLabelFor(cbSource);
      ServiceDialog.addToGB(lblSource, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 0;
      ServiceDialog.addToGB(cbSource, this, localGridBagLayout, localGridBagConstraints);
    }
    
    private String getMediaName(String paramString)
    {
      try
      {
        String str = paramString.replace(' ', '-');
        str = str.replace('#', 'n');
        return ServiceDialog.messageRB.getString(str);
      }
      catch (MissingResourceException localMissingResourceException) {}
      return paramString;
    }
    
    public void itemStateChanged(ItemEvent paramItemEvent)
    {
      Object localObject1 = paramItemEvent.getSource();
      if (paramItemEvent.getStateChange() == 1)
      {
        int i;
        Object localObject2;
        if (localObject1 == cbSize)
        {
          i = cbSize.getSelectedIndex();
          if ((i >= 0) && (i < sizes.size()))
          {
            if ((cbSource.getItemCount() > 1) && (cbSource.getSelectedIndex() >= 1))
            {
              int j = cbSource.getSelectedIndex() - 1;
              localObject2 = (MediaTray)sources.get(j);
              asCurrent.add(new SunAlternateMedia((Media)localObject2));
            }
            asCurrent.add((MediaSizeName)sizes.get(i));
          }
        }
        else if (localObject1 == cbSource)
        {
          i = cbSource.getSelectedIndex();
          if ((i >= 1) && (i < sources.size() + 1))
          {
            asCurrent.remove(SunAlternateMedia.class);
            MediaTray localMediaTray = (MediaTray)sources.get(i - 1);
            localObject2 = (Media)asCurrent.get(Media.class);
            if ((localObject2 == null) || ((localObject2 instanceof MediaTray)))
            {
              asCurrent.add(localMediaTray);
            }
            else if ((localObject2 instanceof MediaSizeName))
            {
              MediaSizeName localMediaSizeName = (MediaSizeName)localObject2;
              Media localMedia = (Media)psCurrent.getDefaultAttributeValue(Media.class);
              if (((localMedia instanceof MediaSizeName)) && (localMedia.equals(localMediaSizeName))) {
                asCurrent.add(localMediaTray);
              } else {
                asCurrent.add(new SunAlternateMedia(localMediaTray));
              }
            }
          }
          else if (i == 0)
          {
            asCurrent.remove(SunAlternateMedia.class);
            if (cbSize.getItemCount() > 0)
            {
              int k = cbSize.getSelectedIndex();
              asCurrent.add((MediaSizeName)sizes.get(k));
            }
          }
        }
        if (pnlMargins != null) {
          pnlMargins.updateInfo();
        }
      }
    }
    
    public void addMediaListener(ServiceDialog.MarginsPanel paramMarginsPanel)
    {
      pnlMargins = paramMarginsPanel;
    }
    
    public void updateInfo()
    {
      Class localClass1 = Media.class;
      Class localClass2 = SunAlternateMedia.class;
      boolean bool1 = false;
      cbSize.removeItemListener(this);
      cbSize.removeAllItems();
      cbSource.removeItemListener(this);
      cbSource.removeAllItems();
      cbSource.addItem(getMediaName("auto-select"));
      sizes.clear();
      sources.clear();
      Object localObject2;
      Object localObject3;
      if (psCurrent.isAttributeCategorySupported(localClass1))
      {
        bool1 = true;
        Object localObject1 = psCurrent.getSupportedAttributeValues(localClass1, docFlavor, asCurrent);
        if ((localObject1 instanceof Media[]))
        {
          localObject2 = (Media[])localObject1;
          for (int i = 0; i < localObject2.length; i++)
          {
            localObject3 = localObject2[i];
            if ((localObject3 instanceof MediaSizeName))
            {
              sizes.add(localObject3);
              cbSize.addItem(getMediaName(((Media)localObject3).toString()));
            }
            else if ((localObject3 instanceof MediaTray))
            {
              sources.add(localObject3);
              cbSource.addItem(getMediaName(((Media)localObject3).toString()));
            }
          }
        }
      }
      boolean bool2 = (bool1) && (sizes.size() > 0);
      lblSize.setEnabled(bool2);
      cbSize.setEnabled(bool2);
      if (isAWT)
      {
        cbSource.setEnabled(false);
        lblSource.setEnabled(false);
      }
      else
      {
        cbSource.setEnabled(bool1);
      }
      if (bool1)
      {
        localObject2 = (Media)asCurrent.get(localClass1);
        Media localMedia1 = (Media)psCurrent.getDefaultAttributeValue(localClass1);
        if ((localMedia1 instanceof MediaSizeName)) {
          cbSize.setSelectedIndex(sizes.size() > 0 ? sizes.indexOf(localMedia1) : -1);
        }
        if ((localObject2 == null) || (!psCurrent.isAttributeValueSupported((Attribute)localObject2, docFlavor, asCurrent)))
        {
          localObject2 = localMedia1;
          if ((localObject2 == null) && (sizes.size() > 0)) {
            localObject2 = (Media)sizes.get(0);
          }
          if (localObject2 != null) {
            asCurrent.add((Attribute)localObject2);
          }
        }
        if (localObject2 != null)
        {
          if ((localObject2 instanceof MediaSizeName))
          {
            localObject3 = (MediaSizeName)localObject2;
            cbSize.setSelectedIndex(sizes.indexOf(localObject3));
          }
          else if ((localObject2 instanceof MediaTray))
          {
            localObject3 = (MediaTray)localObject2;
            cbSource.setSelectedIndex(sources.indexOf(localObject3) + 1);
          }
        }
        else
        {
          cbSize.setSelectedIndex(sizes.size() > 0 ? 0 : -1);
          cbSource.setSelectedIndex(0);
        }
        localObject3 = (SunAlternateMedia)asCurrent.get(localClass2);
        MediaTray localMediaTray;
        if (localObject3 != null)
        {
          Media localMedia2 = ((SunAlternateMedia)localObject3).getMedia();
          if ((localMedia2 instanceof MediaTray))
          {
            localMediaTray = (MediaTray)localMedia2;
            cbSource.setSelectedIndex(sources.indexOf(localMediaTray) + 1);
          }
        }
        int j = cbSize.getSelectedIndex();
        if ((j >= 0) && (j < sizes.size())) {
          asCurrent.add((MediaSizeName)sizes.get(j));
        }
        j = cbSource.getSelectedIndex();
        if ((j >= 1) && (j < sources.size() + 1))
        {
          localMediaTray = (MediaTray)sources.get(j - 1);
          if ((localObject2 instanceof MediaTray)) {
            asCurrent.add(localMediaTray);
          } else {
            asCurrent.add(new SunAlternateMedia(localMediaTray));
          }
        }
      }
      cbSize.addItemListener(this);
      cbSource.addItemListener(this);
    }
  }
  
  private class OrientationPanel
    extends JPanel
    implements ActionListener
  {
    private final String strTitle = ServiceDialog.getMsg("border.orientation");
    private ServiceDialog.IconRadioButton rbPortrait;
    private ServiceDialog.IconRadioButton rbLandscape;
    private ServiceDialog.IconRadioButton rbRevPortrait;
    private ServiceDialog.IconRadioButton rbRevLandscape;
    private ServiceDialog.MarginsPanel pnlMargins = null;
    
    public OrientationPanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      setBorder(BorderFactory.createTitledBorder(strTitle));
      fill = 1;
      insets = ServiceDialog.compInsets;
      weighty = 1.0D;
      gridwidth = 0;
      ButtonGroup localButtonGroup = new ButtonGroup();
      rbPortrait = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.portrait", "orientPortrait.png", true, localButtonGroup, this);
      rbPortrait.addActionListener(this);
      ServiceDialog.addToGB(rbPortrait, this, localGridBagLayout, localGridBagConstraints);
      rbLandscape = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.landscape", "orientLandscape.png", false, localButtonGroup, this);
      rbLandscape.addActionListener(this);
      ServiceDialog.addToGB(rbLandscape, this, localGridBagLayout, localGridBagConstraints);
      rbRevPortrait = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.revportrait", "orientRevPortrait.png", false, localButtonGroup, this);
      rbRevPortrait.addActionListener(this);
      ServiceDialog.addToGB(rbRevPortrait, this, localGridBagLayout, localGridBagConstraints);
      rbRevLandscape = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.revlandscape", "orientRevLandscape.png", false, localButtonGroup, this);
      rbRevLandscape.addActionListener(this);
      ServiceDialog.addToGB(rbRevLandscape, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      Object localObject = paramActionEvent.getSource();
      if (rbPortrait.isSameAs(localObject)) {
        asCurrent.add(OrientationRequested.PORTRAIT);
      } else if (rbLandscape.isSameAs(localObject)) {
        asCurrent.add(OrientationRequested.LANDSCAPE);
      } else if (rbRevPortrait.isSameAs(localObject)) {
        asCurrent.add(OrientationRequested.REVERSE_PORTRAIT);
      } else if (rbRevLandscape.isSameAs(localObject)) {
        asCurrent.add(OrientationRequested.REVERSE_LANDSCAPE);
      }
      if (pnlMargins != null) {
        pnlMargins.updateInfo();
      }
    }
    
    void addOrientationListener(ServiceDialog.MarginsPanel paramMarginsPanel)
    {
      pnlMargins = paramMarginsPanel;
    }
    
    public void updateInfo()
    {
      Class localClass = OrientationRequested.class;
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      boolean bool4 = false;
      Object localObject2;
      if (isAWT)
      {
        bool1 = true;
        bool2 = true;
      }
      else if (psCurrent.isAttributeCategorySupported(localClass))
      {
        localObject1 = psCurrent.getSupportedAttributeValues(localClass, docFlavor, asCurrent);
        if ((localObject1 instanceof OrientationRequested[]))
        {
          localObject2 = (OrientationRequested[])localObject1;
          for (int i = 0; i < localObject2.length; i++)
          {
            Object localObject3 = localObject2[i];
            if (localObject3 == OrientationRequested.PORTRAIT) {
              bool1 = true;
            } else if (localObject3 == OrientationRequested.LANDSCAPE) {
              bool2 = true;
            } else if (localObject3 == OrientationRequested.REVERSE_PORTRAIT) {
              bool3 = true;
            } else if (localObject3 == OrientationRequested.REVERSE_LANDSCAPE) {
              bool4 = true;
            }
          }
        }
      }
      rbPortrait.setEnabled(bool1);
      rbLandscape.setEnabled(bool2);
      rbRevPortrait.setEnabled(bool3);
      rbRevLandscape.setEnabled(bool4);
      Object localObject1 = (OrientationRequested)asCurrent.get(localClass);
      if ((localObject1 == null) || (!psCurrent.isAttributeValueSupported((Attribute)localObject1, docFlavor, asCurrent)))
      {
        localObject1 = (OrientationRequested)psCurrent.getDefaultAttributeValue(localClass);
        if ((localObject1 != null) && (!psCurrent.isAttributeValueSupported((Attribute)localObject1, docFlavor, asCurrent)))
        {
          localObject1 = null;
          localObject2 = psCurrent.getSupportedAttributeValues(localClass, docFlavor, asCurrent);
          if ((localObject2 instanceof OrientationRequested[]))
          {
            OrientationRequested[] arrayOfOrientationRequested = (OrientationRequested[])localObject2;
            if (arrayOfOrientationRequested.length > 1) {
              localObject1 = arrayOfOrientationRequested[0];
            }
          }
        }
        if (localObject1 == null) {
          localObject1 = OrientationRequested.PORTRAIT;
        }
        asCurrent.add((Attribute)localObject1);
      }
      if (localObject1 == OrientationRequested.PORTRAIT) {
        rbPortrait.setSelected(true);
      } else if (localObject1 == OrientationRequested.LANDSCAPE) {
        rbLandscape.setSelected(true);
      } else if (localObject1 == OrientationRequested.REVERSE_PORTRAIT) {
        rbRevPortrait.setSelected(true);
      } else {
        rbRevLandscape.setSelected(true);
      }
    }
  }
  
  private class PageSetupPanel
    extends JPanel
  {
    private ServiceDialog.MediaPanel pnlMedia;
    private ServiceDialog.OrientationPanel pnlOrientation;
    private ServiceDialog.MarginsPanel pnlMargins;
    
    public PageSetupPanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      fill = 1;
      insets = ServiceDialog.panelInsets;
      weightx = 1.0D;
      weighty = 1.0D;
      gridwidth = 0;
      pnlMedia = new ServiceDialog.MediaPanel(ServiceDialog.this);
      ServiceDialog.addToGB(pnlMedia, this, localGridBagLayout, localGridBagConstraints);
      pnlOrientation = new ServiceDialog.OrientationPanel(ServiceDialog.this);
      gridwidth = -1;
      ServiceDialog.addToGB(pnlOrientation, this, localGridBagLayout, localGridBagConstraints);
      pnlMargins = new ServiceDialog.MarginsPanel(ServiceDialog.this);
      pnlOrientation.addOrientationListener(pnlMargins);
      pnlMedia.addMediaListener(pnlMargins);
      gridwidth = 0;
      ServiceDialog.addToGB(pnlMargins, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public void updateInfo()
    {
      pnlMedia.updateInfo();
      pnlOrientation.updateInfo();
      pnlMargins.updateInfo();
    }
  }
  
  private class PrintRangePanel
    extends JPanel
    implements ActionListener, FocusListener
  {
    private final String strTitle = ServiceDialog.getMsg("border.printrange");
    private final PageRanges prAll = new PageRanges(1, Integer.MAX_VALUE);
    private JRadioButton rbAll;
    private JRadioButton rbPages;
    private JRadioButton rbSelect;
    private JFormattedTextField tfRangeFrom;
    private JFormattedTextField tfRangeTo;
    private JLabel lblRangeTo;
    private boolean prSupported;
    
    public PrintRangePanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      setBorder(BorderFactory.createTitledBorder(strTitle));
      fill = 1;
      insets = ServiceDialog.compInsets;
      gridwidth = 0;
      ButtonGroup localButtonGroup = new ButtonGroup();
      JPanel localJPanel1 = new JPanel(new FlowLayout(3));
      rbAll = ServiceDialog.createRadioButton("radiobutton.rangeall", this);
      rbAll.setSelected(true);
      localButtonGroup.add(rbAll);
      localJPanel1.add(rbAll);
      ServiceDialog.addToGB(localJPanel1, this, localGridBagLayout, localGridBagConstraints);
      JPanel localJPanel2 = new JPanel(new FlowLayout(3));
      rbPages = ServiceDialog.createRadioButton("radiobutton.rangepages", this);
      localButtonGroup.add(rbPages);
      localJPanel2.add(rbPages);
      DecimalFormat localDecimalFormat = new DecimalFormat("####0");
      localDecimalFormat.setMinimumFractionDigits(0);
      localDecimalFormat.setMaximumFractionDigits(0);
      localDecimalFormat.setMinimumIntegerDigits(0);
      localDecimalFormat.setMaximumIntegerDigits(5);
      localDecimalFormat.setParseIntegerOnly(true);
      localDecimalFormat.setDecimalSeparatorAlwaysShown(false);
      NumberFormatter localNumberFormatter1 = new NumberFormatter(localDecimalFormat);
      localNumberFormatter1.setMinimum(new Integer(1));
      localNumberFormatter1.setMaximum(new Integer(Integer.MAX_VALUE));
      localNumberFormatter1.setAllowsInvalid(true);
      localNumberFormatter1.setCommitsOnValidEdit(true);
      tfRangeFrom = new JFormattedTextField(localNumberFormatter1);
      tfRangeFrom.setColumns(4);
      tfRangeFrom.setEnabled(false);
      tfRangeFrom.addActionListener(this);
      tfRangeFrom.addFocusListener(this);
      tfRangeFrom.setFocusLostBehavior(3);
      tfRangeFrom.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("radiobutton.rangepages"));
      localJPanel2.add(tfRangeFrom);
      lblRangeTo = new JLabel(ServiceDialog.getMsg("label.rangeto"));
      lblRangeTo.setEnabled(false);
      localJPanel2.add(lblRangeTo);
      NumberFormatter localNumberFormatter2;
      try
      {
        localNumberFormatter2 = (NumberFormatter)localNumberFormatter1.clone();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        localNumberFormatter2 = new NumberFormatter();
      }
      tfRangeTo = new JFormattedTextField(localNumberFormatter2);
      tfRangeTo.setColumns(4);
      tfRangeTo.setEnabled(false);
      tfRangeTo.addFocusListener(this);
      tfRangeTo.getAccessibleContext().setAccessibleName(ServiceDialog.getMsg("label.rangeto"));
      localJPanel2.add(tfRangeTo);
      ServiceDialog.addToGB(localJPanel2, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      Object localObject = paramActionEvent.getSource();
      SunPageSelection localSunPageSelection = SunPageSelection.ALL;
      setupRangeWidgets();
      if (localObject == rbAll)
      {
        asCurrent.add(prAll);
      }
      else if (localObject == rbSelect)
      {
        localSunPageSelection = SunPageSelection.SELECTION;
      }
      else if ((localObject == rbPages) || (localObject == tfRangeFrom) || (localObject == tfRangeTo))
      {
        updateRangeAttribute();
        localSunPageSelection = SunPageSelection.RANGE;
      }
      if (isAWT) {
        asCurrent.add(localSunPageSelection);
      }
    }
    
    public void focusLost(FocusEvent paramFocusEvent)
    {
      Object localObject = paramFocusEvent.getSource();
      if ((localObject == tfRangeFrom) || (localObject == tfRangeTo)) {
        updateRangeAttribute();
      }
    }
    
    public void focusGained(FocusEvent paramFocusEvent) {}
    
    private void setupRangeWidgets()
    {
      boolean bool = (rbPages.isSelected()) && (prSupported);
      tfRangeFrom.setEnabled(bool);
      tfRangeTo.setEnabled(bool);
      lblRangeTo.setEnabled(bool);
    }
    
    private void updateRangeAttribute()
    {
      String str1 = tfRangeFrom.getText();
      String str2 = tfRangeTo.getText();
      int i;
      try
      {
        i = Integer.parseInt(str1);
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        i = 1;
      }
      int j;
      try
      {
        j = Integer.parseInt(str2);
      }
      catch (NumberFormatException localNumberFormatException2)
      {
        j = i;
      }
      if (i < 1)
      {
        i = 1;
        tfRangeFrom.setValue(new Integer(1));
      }
      if (j < i)
      {
        j = i;
        tfRangeTo.setValue(new Integer(i));
      }
      PageRanges localPageRanges = new PageRanges(i, j);
      asCurrent.add(localPageRanges);
    }
    
    public void updateInfo()
    {
      Class localClass = PageRanges.class;
      prSupported = false;
      if ((psCurrent.isAttributeCategorySupported(localClass)) || (isAWT)) {
        prSupported = true;
      }
      SunPageSelection localSunPageSelection = SunPageSelection.ALL;
      int i = 1;
      int j = 1;
      PageRanges localPageRanges = (PageRanges)asCurrent.get(localClass);
      if ((localPageRanges != null) && (!localPageRanges.equals(prAll)))
      {
        localSunPageSelection = SunPageSelection.RANGE;
        int[][] arrayOfInt = localPageRanges.getMembers();
        if ((arrayOfInt.length > 0) && (arrayOfInt[0].length > 1))
        {
          i = arrayOfInt[0][0];
          j = arrayOfInt[0][1];
        }
      }
      if (isAWT) {
        localSunPageSelection = (SunPageSelection)asCurrent.get(SunPageSelection.class);
      }
      if (localSunPageSelection == SunPageSelection.ALL) {
        rbAll.setSelected(true);
      } else if (localSunPageSelection != SunPageSelection.SELECTION) {
        rbPages.setSelected(true);
      }
      tfRangeFrom.setValue(new Integer(i));
      tfRangeTo.setValue(new Integer(j));
      rbAll.setEnabled(prSupported);
      rbPages.setEnabled(prSupported);
      setupRangeWidgets();
    }
  }
  
  private class PrintServicePanel
    extends JPanel
    implements ActionListener, ItemListener, PopupMenuListener
  {
    private final String strTitle = ServiceDialog.getMsg("border.printservice");
    private FilePermission printToFilePermission;
    private JButton btnProperties;
    private JCheckBox cbPrintToFile;
    private JComboBox cbName;
    private JLabel lblType;
    private JLabel lblStatus;
    private JLabel lblInfo;
    private ServiceUIFactory uiFactory = psCurrent.getServiceUIFactory();
    private boolean changedService = false;
    private boolean filePermission;
    
    public PrintServicePanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      setBorder(BorderFactory.createTitledBorder(strTitle));
      String[] arrayOfString = new String[services.length];
      for (int i = 0; i < arrayOfString.length; i++) {
        arrayOfString[i] = services[i].getName();
      }
      cbName = new JComboBox(arrayOfString);
      cbName.setSelectedIndex(defaultServiceIndex);
      cbName.addItemListener(this);
      cbName.addPopupMenuListener(this);
      fill = 1;
      insets = ServiceDialog.compInsets;
      weightx = 0.0D;
      JLabel localJLabel = new JLabel(ServiceDialog.getMsg("label.psname"), 11);
      localJLabel.setDisplayedMnemonic(ServiceDialog.getMnemonic("label.psname"));
      localJLabel.setLabelFor(cbName);
      ServiceDialog.addToGB(localJLabel, this, localGridBagLayout, localGridBagConstraints);
      weightx = 1.0D;
      gridwidth = -1;
      ServiceDialog.addToGB(cbName, this, localGridBagLayout, localGridBagConstraints);
      weightx = 0.0D;
      gridwidth = 0;
      btnProperties = ServiceDialog.createButton("button.properties", this);
      ServiceDialog.addToGB(btnProperties, this, localGridBagLayout, localGridBagConstraints);
      weighty = 1.0D;
      lblStatus = addLabel(ServiceDialog.getMsg("label.status"), localGridBagLayout, localGridBagConstraints);
      lblStatus.setLabelFor(null);
      lblType = addLabel(ServiceDialog.getMsg("label.pstype"), localGridBagLayout, localGridBagConstraints);
      lblType.setLabelFor(null);
      gridwidth = 1;
      ServiceDialog.addToGB(new JLabel(ServiceDialog.getMsg("label.info"), 11), this, localGridBagLayout, localGridBagConstraints);
      gridwidth = -1;
      lblInfo = new JLabel();
      lblInfo.setLabelFor(null);
      ServiceDialog.addToGB(lblInfo, this, localGridBagLayout, localGridBagConstraints);
      gridwidth = 0;
      cbPrintToFile = ServiceDialog.createCheckBox("checkbox.printtofile", this);
      ServiceDialog.addToGB(cbPrintToFile, this, localGridBagLayout, localGridBagConstraints);
      filePermission = allowedToPrintToFile();
    }
    
    public boolean isPrintToFileSelected()
    {
      return cbPrintToFile.isSelected();
    }
    
    private JLabel addLabel(String paramString, GridBagLayout paramGridBagLayout, GridBagConstraints paramGridBagConstraints)
    {
      gridwidth = 1;
      ServiceDialog.addToGB(new JLabel(paramString, 11), this, paramGridBagLayout, paramGridBagConstraints);
      gridwidth = 0;
      JLabel localJLabel = new JLabel();
      ServiceDialog.addToGB(localJLabel, this, paramGridBagLayout, paramGridBagConstraints);
      return localJLabel;
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      Object localObject = paramActionEvent.getSource();
      if ((localObject == btnProperties) && (uiFactory != null))
      {
        JDialog localJDialog = (JDialog)uiFactory.getUI(3, "javax.swing.JDialog");
        if (localJDialog != null)
        {
          localJDialog.show();
        }
        else
        {
          DocumentPropertiesUI localDocumentPropertiesUI = null;
          try
          {
            localDocumentPropertiesUI = (DocumentPropertiesUI)uiFactory.getUI(199, DocumentPropertiesUI.DOCPROPERTIESCLASSNAME);
          }
          catch (Exception localException) {}
          if (localDocumentPropertiesUI != null)
          {
            PrinterJobWrapper localPrinterJobWrapper = (PrinterJobWrapper)asCurrent.get(PrinterJobWrapper.class);
            if (localPrinterJobWrapper == null) {
              return;
            }
            PrinterJob localPrinterJob = localPrinterJobWrapper.getPrinterJob();
            if (localPrinterJob == null) {
              return;
            }
            PrintRequestAttributeSet localPrintRequestAttributeSet = localDocumentPropertiesUI.showDocumentProperties(localPrinterJob, ServiceDialog.this, psCurrent, asCurrent);
            if (localPrintRequestAttributeSet != null)
            {
              asCurrent.addAll(localPrintRequestAttributeSet);
              ServiceDialog.this.updatePanels();
            }
          }
        }
      }
    }
    
    public void itemStateChanged(ItemEvent paramItemEvent)
    {
      if (paramItemEvent.getStateChange() == 1)
      {
        int i = cbName.getSelectedIndex();
        if ((i >= 0) && (i < services.length) && (!services[i].equals(psCurrent)))
        {
          psCurrent = services[i];
          uiFactory = psCurrent.getServiceUIFactory();
          changedService = true;
          Destination localDestination = (Destination)asOriginal.get(Destination.class);
          if (((localDestination != null) || (isPrintToFileSelected())) && (psCurrent.isAttributeCategorySupported(Destination.class)))
          {
            if (localDestination != null)
            {
              asCurrent.add(localDestination);
            }
            else
            {
              localDestination = (Destination)psCurrent.getDefaultAttributeValue(Destination.class);
              if (localDestination == null) {
                try
                {
                  localDestination = new Destination(new URI("file:out.prn"));
                }
                catch (URISyntaxException localURISyntaxException) {}
              }
              if (localDestination != null) {
                asCurrent.add(localDestination);
              }
            }
          }
          else {
            asCurrent.remove(Destination.class);
          }
        }
      }
    }
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent paramPopupMenuEvent)
    {
      changedService = false;
    }
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent paramPopupMenuEvent)
    {
      if (changedService)
      {
        changedService = false;
        ServiceDialog.this.updatePanels();
      }
    }
    
    public void popupMenuCanceled(PopupMenuEvent paramPopupMenuEvent) {}
    
    private boolean allowedToPrintToFile()
    {
      try
      {
        throwPrintToFile();
        return true;
      }
      catch (SecurityException localSecurityException) {}
      return false;
    }
    
    private void throwPrintToFile()
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        if (printToFilePermission == null) {
          printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write");
        }
        localSecurityManager.checkPermission(printToFilePermission);
      }
    }
    
    public void updateInfo()
    {
      Class localClass = Destination.class;
      int i = 0;
      int j = 0;
      int k = filePermission ? allowedToPrintToFile() : 0;
      if (psCurrent.isAttributeCategorySupported(localClass)) {
        i = 1;
      }
      Destination localDestination = (Destination)asCurrent.get(localClass);
      if (localDestination != null) {
        j = 1;
      }
      cbPrintToFile.setEnabled((i != 0) && (k != 0));
      cbPrintToFile.setSelected((j != 0) && (k != 0) && (i != 0));
      PrintServiceAttribute localPrintServiceAttribute1 = psCurrent.getAttribute(PrinterMakeAndModel.class);
      if (localPrintServiceAttribute1 != null) {
        lblType.setText(localPrintServiceAttribute1.toString());
      }
      PrintServiceAttribute localPrintServiceAttribute2 = psCurrent.getAttribute(PrinterIsAcceptingJobs.class);
      if (localPrintServiceAttribute2 != null) {
        lblStatus.setText(ServiceDialog.getMsg(localPrintServiceAttribute2.toString()));
      }
      PrintServiceAttribute localPrintServiceAttribute3 = psCurrent.getAttribute(PrinterInfo.class);
      if (localPrintServiceAttribute3 != null) {
        lblInfo.setText(localPrintServiceAttribute3.toString());
      }
      btnProperties.setEnabled(uiFactory != null);
    }
  }
  
  private class QualityPanel
    extends JPanel
    implements ActionListener
  {
    private final String strTitle = ServiceDialog.getMsg("border.quality");
    private JRadioButton rbDraft;
    private JRadioButton rbNormal;
    private JRadioButton rbHigh;
    
    public QualityPanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      setBorder(BorderFactory.createTitledBorder(strTitle));
      fill = 1;
      gridwidth = 0;
      weighty = 1.0D;
      ButtonGroup localButtonGroup = new ButtonGroup();
      rbDraft = ServiceDialog.createRadioButton("radiobutton.draftq", this);
      localButtonGroup.add(rbDraft);
      ServiceDialog.addToGB(rbDraft, this, localGridBagLayout, localGridBagConstraints);
      rbNormal = ServiceDialog.createRadioButton("radiobutton.normalq", this);
      rbNormal.setSelected(true);
      localButtonGroup.add(rbNormal);
      ServiceDialog.addToGB(rbNormal, this, localGridBagLayout, localGridBagConstraints);
      rbHigh = ServiceDialog.createRadioButton("radiobutton.highq", this);
      localButtonGroup.add(rbHigh);
      ServiceDialog.addToGB(rbHigh, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      Object localObject = paramActionEvent.getSource();
      if (localObject == rbDraft) {
        asCurrent.add(PrintQuality.DRAFT);
      } else if (localObject == rbNormal) {
        asCurrent.add(PrintQuality.NORMAL);
      } else if (localObject == rbHigh) {
        asCurrent.add(PrintQuality.HIGH);
      }
    }
    
    public void updateInfo()
    {
      Class localClass = PrintQuality.class;
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      if (isAWT)
      {
        bool1 = true;
        bool2 = true;
        bool3 = true;
      }
      else if (psCurrent.isAttributeCategorySupported(localClass))
      {
        localObject = psCurrent.getSupportedAttributeValues(localClass, docFlavor, asCurrent);
        if ((localObject instanceof PrintQuality[]))
        {
          PrintQuality[] arrayOfPrintQuality = (PrintQuality[])localObject;
          for (int i = 0; i < arrayOfPrintQuality.length; i++)
          {
            PrintQuality localPrintQuality = arrayOfPrintQuality[i];
            if (localPrintQuality == PrintQuality.DRAFT) {
              bool1 = true;
            } else if (localPrintQuality == PrintQuality.NORMAL) {
              bool2 = true;
            } else if (localPrintQuality == PrintQuality.HIGH) {
              bool3 = true;
            }
          }
        }
      }
      rbDraft.setEnabled(bool1);
      rbNormal.setEnabled(bool2);
      rbHigh.setEnabled(bool3);
      Object localObject = (PrintQuality)asCurrent.get(localClass);
      if (localObject == null)
      {
        localObject = (PrintQuality)psCurrent.getDefaultAttributeValue(localClass);
        if (localObject == null) {
          localObject = PrintQuality.NORMAL;
        }
      }
      if (localObject == PrintQuality.DRAFT) {
        rbDraft.setSelected(true);
      } else if (localObject == PrintQuality.NORMAL) {
        rbNormal.setSelected(true);
      } else {
        rbHigh.setSelected(true);
      }
    }
  }
  
  private class SidesPanel
    extends JPanel
    implements ActionListener
  {
    private final String strTitle = ServiceDialog.getMsg("border.sides");
    private ServiceDialog.IconRadioButton rbOneSide;
    private ServiceDialog.IconRadioButton rbTumble;
    private ServiceDialog.IconRadioButton rbDuplex;
    
    public SidesPanel()
    {
      GridBagLayout localGridBagLayout = new GridBagLayout();
      GridBagConstraints localGridBagConstraints = new GridBagConstraints();
      setLayout(localGridBagLayout);
      setBorder(BorderFactory.createTitledBorder(strTitle));
      fill = 1;
      insets = ServiceDialog.compInsets;
      weighty = 1.0D;
      gridwidth = 0;
      ButtonGroup localButtonGroup = new ButtonGroup();
      rbOneSide = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.oneside", "oneside.png", true, localButtonGroup, this);
      rbOneSide.addActionListener(this);
      ServiceDialog.addToGB(rbOneSide, this, localGridBagLayout, localGridBagConstraints);
      rbTumble = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.tumble", "tumble.png", false, localButtonGroup, this);
      rbTumble.addActionListener(this);
      ServiceDialog.addToGB(rbTumble, this, localGridBagLayout, localGridBagConstraints);
      rbDuplex = new ServiceDialog.IconRadioButton(ServiceDialog.this, "radiobutton.duplex", "duplex.png", false, localButtonGroup, this);
      rbDuplex.addActionListener(this);
      gridwidth = 0;
      ServiceDialog.addToGB(rbDuplex, this, localGridBagLayout, localGridBagConstraints);
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      Object localObject = paramActionEvent.getSource();
      if (rbOneSide.isSameAs(localObject)) {
        asCurrent.add(Sides.ONE_SIDED);
      } else if (rbTumble.isSameAs(localObject)) {
        asCurrent.add(Sides.TUMBLE);
      } else if (rbDuplex.isSameAs(localObject)) {
        asCurrent.add(Sides.DUPLEX);
      }
    }
    
    public void updateInfo()
    {
      Class localClass = Sides.class;
      boolean bool1 = false;
      boolean bool2 = false;
      boolean bool3 = false;
      if (psCurrent.isAttributeCategorySupported(localClass))
      {
        localObject = psCurrent.getSupportedAttributeValues(localClass, docFlavor, asCurrent);
        if ((localObject instanceof Sides[]))
        {
          Sides[] arrayOfSides = (Sides[])localObject;
          for (int i = 0; i < arrayOfSides.length; i++)
          {
            Sides localSides = arrayOfSides[i];
            if (localSides == Sides.ONE_SIDED) {
              bool1 = true;
            } else if (localSides == Sides.TUMBLE) {
              bool2 = true;
            } else if (localSides == Sides.DUPLEX) {
              bool3 = true;
            }
          }
        }
      }
      rbOneSide.setEnabled(bool1);
      rbTumble.setEnabled(bool2);
      rbDuplex.setEnabled(bool3);
      Object localObject = (Sides)asCurrent.get(localClass);
      if (localObject == null)
      {
        localObject = (Sides)psCurrent.getDefaultAttributeValue(localClass);
        if (localObject == null) {
          localObject = Sides.ONE_SIDED;
        }
      }
      if (localObject == Sides.ONE_SIDED) {
        rbOneSide.setSelected(true);
      } else if (localObject == Sides.TUMBLE) {
        rbTumble.setSelected(true);
      } else {
        rbDuplex.setSelected(true);
      }
    }
  }
  
  private class ValidatingFileChooser
    extends JFileChooser
  {
    private ValidatingFileChooser() {}
    
    public void approveSelection()
    {
      File localFile1 = getSelectedFile();
      boolean bool;
      try
      {
        bool = localFile1.exists();
      }
      catch (SecurityException localSecurityException1)
      {
        bool = false;
      }
      if (bool)
      {
        int i = JOptionPane.showConfirmDialog(this, ServiceDialog.getMsg("dialog.overwrite"), ServiceDialog.getMsg("dialog.owtitle"), 0);
        if (i != 0) {
          return;
        }
      }
      try
      {
        if (localFile1.createNewFile()) {
          localFile1.delete();
        }
      }
      catch (IOException localIOException)
      {
        JOptionPane.showMessageDialog(this, ServiceDialog.getMsg("dialog.writeerror") + " " + localFile1, ServiceDialog.getMsg("dialog.owtitle"), 2);
        return;
      }
      catch (SecurityException localSecurityException2) {}
      File localFile2 = localFile1.getParentFile();
      if (((localFile1.exists()) && ((!localFile1.isFile()) || (!localFile1.canWrite()))) || ((localFile2 != null) && ((!localFile2.exists()) || ((localFile2.exists()) && (!localFile2.canWrite())))))
      {
        JOptionPane.showMessageDialog(this, ServiceDialog.getMsg("dialog.writeerror") + " " + localFile1, ServiceDialog.getMsg("dialog.owtitle"), 2);
        return;
      }
      super.approveSelection();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\ServiceDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
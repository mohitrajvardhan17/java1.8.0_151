package sun.swing.plaf.synth;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;
import javax.swing.plaf.synth.SynthUI;

public abstract class SynthFileChooserUI
  extends BasicFileChooserUI
  implements SynthUI
{
  private JButton approveButton;
  private JButton cancelButton;
  private SynthStyle style;
  private Action fileNameCompletionAction = new FileNameCompletionAction();
  private FileFilter actualFileFilter = null;
  private GlobFilter globFilter = null;
  private String fileNameCompletionString;
  
  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return new SynthFileChooserUIImpl((JFileChooser)paramJComponent);
  }
  
  public SynthFileChooserUI(JFileChooser paramJFileChooser)
  {
    super(paramJFileChooser);
  }
  
  public SynthContext getContext(JComponent paramJComponent)
  {
    return new SynthContext(paramJComponent, Region.FILE_CHOOSER, style, getComponentState(paramJComponent));
  }
  
  protected SynthContext getContext(JComponent paramJComponent, int paramInt)
  {
    Region localRegion = SynthLookAndFeel.getRegion(paramJComponent);
    return new SynthContext(paramJComponent, Region.FILE_CHOOSER, style, paramInt);
  }
  
  private Region getRegion(JComponent paramJComponent)
  {
    return SynthLookAndFeel.getRegion(paramJComponent);
  }
  
  private int getComponentState(JComponent paramJComponent)
  {
    if (paramJComponent.isEnabled())
    {
      if (paramJComponent.isFocusOwner()) {
        return 257;
      }
      return 1;
    }
    return 8;
  }
  
  private void updateStyle(JComponent paramJComponent)
  {
    SynthStyle localSynthStyle = SynthLookAndFeel.getStyleFactory().getStyle(paramJComponent, Region.FILE_CHOOSER);
    if (localSynthStyle != style)
    {
      if (style != null) {
        style.uninstallDefaults(getContext(paramJComponent, 1));
      }
      style = localSynthStyle;
      SynthContext localSynthContext = getContext(paramJComponent, 1);
      style.installDefaults(localSynthContext);
      Border localBorder = paramJComponent.getBorder();
      if ((localBorder == null) || ((localBorder instanceof UIResource))) {
        paramJComponent.setBorder(new UIBorder(style.getInsets(localSynthContext, null)));
      }
      directoryIcon = style.getIcon(localSynthContext, "FileView.directoryIcon");
      fileIcon = style.getIcon(localSynthContext, "FileView.fileIcon");
      computerIcon = style.getIcon(localSynthContext, "FileView.computerIcon");
      hardDriveIcon = style.getIcon(localSynthContext, "FileView.hardDriveIcon");
      floppyDriveIcon = style.getIcon(localSynthContext, "FileView.floppyDriveIcon");
      newFolderIcon = style.getIcon(localSynthContext, "FileChooser.newFolderIcon");
      upFolderIcon = style.getIcon(localSynthContext, "FileChooser.upFolderIcon");
      homeFolderIcon = style.getIcon(localSynthContext, "FileChooser.homeFolderIcon");
      detailsViewIcon = style.getIcon(localSynthContext, "FileChooser.detailsViewIcon");
      listViewIcon = style.getIcon(localSynthContext, "FileChooser.listViewIcon");
    }
  }
  
  public void installUI(JComponent paramJComponent)
  {
    super.installUI(paramJComponent);
    SwingUtilities.replaceUIActionMap(paramJComponent, createActionMap());
  }
  
  public void installComponents(JFileChooser paramJFileChooser)
  {
    SynthContext localSynthContext = getContext(paramJFileChooser, 1);
    cancelButton = new JButton(cancelButtonText);
    cancelButton.setName("SynthFileChooser.cancelButton");
    cancelButton.setIcon(localSynthContext.getStyle().getIcon(localSynthContext, "FileChooser.cancelIcon"));
    cancelButton.setMnemonic(cancelButtonMnemonic);
    cancelButton.setToolTipText(cancelButtonToolTipText);
    cancelButton.addActionListener(getCancelSelectionAction());
    approveButton = new JButton(getApproveButtonText(paramJFileChooser));
    approveButton.setName("SynthFileChooser.approveButton");
    approveButton.setIcon(localSynthContext.getStyle().getIcon(localSynthContext, "FileChooser.okIcon"));
    approveButton.setMnemonic(getApproveButtonMnemonic(paramJFileChooser));
    approveButton.setToolTipText(getApproveButtonToolTipText(paramJFileChooser));
    approveButton.addActionListener(getApproveSelectionAction());
  }
  
  public void uninstallComponents(JFileChooser paramJFileChooser)
  {
    paramJFileChooser.removeAll();
  }
  
  protected void installListeners(JFileChooser paramJFileChooser)
  {
    super.installListeners(paramJFileChooser);
    getModel().addListDataListener(new ListDataListener()
    {
      public void contentsChanged(ListDataEvent paramAnonymousListDataEvent)
      {
        new SynthFileChooserUI.DelayedSelectionUpdater(SynthFileChooserUI.this);
      }
      
      public void intervalAdded(ListDataEvent paramAnonymousListDataEvent)
      {
        new SynthFileChooserUI.DelayedSelectionUpdater(SynthFileChooserUI.this);
      }
      
      public void intervalRemoved(ListDataEvent paramAnonymousListDataEvent) {}
    });
  }
  
  protected abstract ActionMap createActionMap();
  
  protected void installDefaults(JFileChooser paramJFileChooser)
  {
    super.installDefaults(paramJFileChooser);
    updateStyle(paramJFileChooser);
  }
  
  protected void uninstallDefaults(JFileChooser paramJFileChooser)
  {
    super.uninstallDefaults(paramJFileChooser);
    SynthContext localSynthContext = getContext(getFileChooser(), 1);
    style.uninstallDefaults(localSynthContext);
    style = null;
  }
  
  protected void installIcons(JFileChooser paramJFileChooser) {}
  
  public void update(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    if (paramJComponent.isOpaque())
    {
      paramGraphics.setColor(style.getColor(localSynthContext, ColorType.BACKGROUND));
      paramGraphics.fillRect(0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    }
    style.getPainter(localSynthContext).paintFileChooserBackground(localSynthContext, paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight());
    paint(localSynthContext, paramGraphics);
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    SynthContext localSynthContext = getContext(paramJComponent);
    paint(localSynthContext, paramGraphics);
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {}
  
  public abstract void setFileName(String paramString);
  
  public abstract String getFileName();
  
  protected void doSelectedFileChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected void doSelectedFilesChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected void doDirectoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected void doAccessoryChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected void doFileSelectionModeChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  protected void doMultiSelectionChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (!getFileChooser().isMultiSelectionEnabled()) {
      getFileChooser().setSelectedFiles(null);
    }
  }
  
  protected void doControlButtonsChanged(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (getFileChooser().getControlButtonsAreShown())
    {
      approveButton.setText(getApproveButtonText(getFileChooser()));
      approveButton.setToolTipText(getApproveButtonToolTipText(getFileChooser()));
      approveButton.setMnemonic(getApproveButtonMnemonic(getFileChooser()));
    }
  }
  
  protected void doAncestorChanged(PropertyChangeEvent paramPropertyChangeEvent) {}
  
  public PropertyChangeListener createPropertyChangeListener(JFileChooser paramJFileChooser)
  {
    return new SynthFCPropertyChangeListener(null);
  }
  
  private void updateFileNameCompletion()
  {
    if ((fileNameCompletionString != null) && (fileNameCompletionString.equals(getFileName())))
    {
      File[] arrayOfFile = (File[])getModel().getFiles().toArray(new File[0]);
      String str = getCommonStartString(arrayOfFile);
      if ((str != null) && (str.startsWith(fileNameCompletionString))) {
        setFileName(str);
      }
      fileNameCompletionString = null;
    }
  }
  
  private String getCommonStartString(File[] paramArrayOfFile)
  {
    Object localObject = null;
    String str1 = null;
    int i = 0;
    if (paramArrayOfFile.length == 0) {
      return null;
    }
    for (;;)
    {
      for (int j = 0; j < paramArrayOfFile.length; j++)
      {
        String str2 = paramArrayOfFile[j].getName();
        if (j == 0)
        {
          if (str2.length() == i) {
            return (String)localObject;
          }
          str1 = str2.substring(0, i + 1);
        }
        if (!str2.startsWith(str1)) {
          return (String)localObject;
        }
      }
      localObject = str1;
      i++;
    }
  }
  
  private void resetGlobFilter()
  {
    if (actualFileFilter != null)
    {
      JFileChooser localJFileChooser = getFileChooser();
      FileFilter localFileFilter = localJFileChooser.getFileFilter();
      if ((localFileFilter != null) && (localFileFilter.equals(globFilter)))
      {
        localJFileChooser.setFileFilter(actualFileFilter);
        localJFileChooser.removeChoosableFileFilter(globFilter);
      }
      actualFileFilter = null;
    }
  }
  
  private static boolean isGlobPattern(String paramString)
  {
    return ((File.separatorChar == '\\') && (paramString.indexOf('*') >= 0)) || ((File.separatorChar == '/') && ((paramString.indexOf('*') >= 0) || (paramString.indexOf('?') >= 0) || (paramString.indexOf('[') >= 0)));
  }
  
  public Action getFileNameCompletionAction()
  {
    return fileNameCompletionAction;
  }
  
  protected JButton getApproveButton(JFileChooser paramJFileChooser)
  {
    return approveButton;
  }
  
  protected JButton getCancelButton(JFileChooser paramJFileChooser)
  {
    return cancelButton;
  }
  
  public void clearIconCache() {}
  
  private class DelayedSelectionUpdater
    implements Runnable
  {
    DelayedSelectionUpdater()
    {
      SwingUtilities.invokeLater(this);
    }
    
    public void run()
    {
      SynthFileChooserUI.this.updateFileNameCompletion();
    }
  }
  
  private class FileNameCompletionAction
    extends AbstractAction
  {
    protected FileNameCompletionAction()
    {
      super();
    }
    
    public void actionPerformed(ActionEvent paramActionEvent)
    {
      JFileChooser localJFileChooser = getFileChooser();
      String str = getFileName();
      if (str != null) {
        str = str.trim();
      }
      SynthFileChooserUI.this.resetGlobFilter();
      if ((str == null) || (str.equals("")) || ((localJFileChooser.isMultiSelectionEnabled()) && (str.startsWith("\"")))) {
        return;
      }
      FileFilter localFileFilter = localJFileChooser.getFileFilter();
      if (globFilter == null) {
        globFilter = new SynthFileChooserUI.GlobFilter(SynthFileChooserUI.this);
      }
      try
      {
        globFilter.setPattern(!SynthFileChooserUI.isGlobPattern(str) ? str + "*" : str);
        if (!(localFileFilter instanceof SynthFileChooserUI.GlobFilter)) {
          actualFileFilter = localFileFilter;
        }
        localJFileChooser.setFileFilter(null);
        localJFileChooser.setFileFilter(globFilter);
        fileNameCompletionString = str;
      }
      catch (PatternSyntaxException localPatternSyntaxException) {}
    }
  }
  
  class GlobFilter
    extends FileFilter
  {
    Pattern pattern;
    String globPattern;
    
    GlobFilter() {}
    
    public void setPattern(String paramString)
    {
      char[] arrayOfChar1 = paramString.toCharArray();
      char[] arrayOfChar2 = new char[arrayOfChar1.length * 2];
      int i = File.separatorChar == '\\' ? 1 : 0;
      int j = 0;
      int k = 0;
      globPattern = paramString;
      int m;
      if (i != 0)
      {
        m = arrayOfChar1.length;
        if (paramString.endsWith("*.*")) {
          m -= 2;
        }
        for (int n = 0; n < m; n++)
        {
          if (arrayOfChar1[n] == '*') {
            arrayOfChar2[(k++)] = '.';
          }
          arrayOfChar2[(k++)] = arrayOfChar1[n];
        }
      }
      else
      {
        for (m = 0; m < arrayOfChar1.length; m++) {
          switch (arrayOfChar1[m])
          {
          case '*': 
            if (j == 0) {
              arrayOfChar2[(k++)] = '.';
            }
            arrayOfChar2[(k++)] = '*';
            break;
          case '?': 
            arrayOfChar2[(k++)] = (j != 0 ? 63 : '.');
            break;
          case '[': 
            j = 1;
            arrayOfChar2[(k++)] = arrayOfChar1[m];
            if (m < arrayOfChar1.length - 1) {
              switch (arrayOfChar1[(m + 1)])
              {
              case '!': 
              case '^': 
                arrayOfChar2[(k++)] = '^';
                m++;
                break;
              case ']': 
                arrayOfChar2[(k++)] = arrayOfChar1[(++m)];
              }
            }
            break;
          case ']': 
            arrayOfChar2[(k++)] = arrayOfChar1[m];
            j = 0;
            break;
          case '\\': 
            if ((m == 0) && (arrayOfChar1.length > 1) && (arrayOfChar1[1] == '~'))
            {
              arrayOfChar2[(k++)] = arrayOfChar1[(++m)];
            }
            else
            {
              arrayOfChar2[(k++)] = '\\';
              if ((m < arrayOfChar1.length - 1) && ("*?[]".indexOf(arrayOfChar1[(m + 1)]) >= 0)) {
                arrayOfChar2[(k++)] = arrayOfChar1[(++m)];
              } else {
                arrayOfChar2[(k++)] = '\\';
              }
            }
            break;
          default: 
            if (!Character.isLetterOrDigit(arrayOfChar1[m])) {
              arrayOfChar2[(k++)] = '\\';
            }
            arrayOfChar2[(k++)] = arrayOfChar1[m];
          }
        }
      }
      pattern = Pattern.compile(new String(arrayOfChar2, 0, k), 2);
    }
    
    public boolean accept(File paramFile)
    {
      if (paramFile == null) {
        return false;
      }
      if (paramFile.isDirectory()) {
        return true;
      }
      return pattern.matcher(paramFile.getName()).matches();
    }
    
    public String getDescription()
    {
      return globPattern;
    }
  }
  
  private class SynthFCPropertyChangeListener
    implements PropertyChangeListener
  {
    private SynthFCPropertyChangeListener() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if (str.equals("fileSelectionChanged"))
      {
        doFileSelectionModeChanged(paramPropertyChangeEvent);
      }
      else if (str.equals("SelectedFileChangedProperty"))
      {
        doSelectedFileChanged(paramPropertyChangeEvent);
      }
      else if (str.equals("SelectedFilesChangedProperty"))
      {
        doSelectedFilesChanged(paramPropertyChangeEvent);
      }
      else if (str.equals("directoryChanged"))
      {
        doDirectoryChanged(paramPropertyChangeEvent);
      }
      else if (str == "MultiSelectionEnabledChangedProperty")
      {
        doMultiSelectionChanged(paramPropertyChangeEvent);
      }
      else if (str == "AccessoryChangedProperty")
      {
        doAccessoryChanged(paramPropertyChangeEvent);
      }
      else if ((str == "ApproveButtonTextChangedProperty") || (str == "ApproveButtonToolTipTextChangedProperty") || (str == "DialogTypeChangedProperty") || (str == "ControlButtonsAreShownChangedProperty"))
      {
        doControlButtonsChanged(paramPropertyChangeEvent);
      }
      else if (str.equals("componentOrientation"))
      {
        ComponentOrientation localComponentOrientation = (ComponentOrientation)paramPropertyChangeEvent.getNewValue();
        JFileChooser localJFileChooser = (JFileChooser)paramPropertyChangeEvent.getSource();
        if (localComponentOrientation != (ComponentOrientation)paramPropertyChangeEvent.getOldValue()) {
          localJFileChooser.applyComponentOrientation(localComponentOrientation);
        }
      }
      else if (str.equals("ancestor"))
      {
        doAncestorChanged(paramPropertyChangeEvent);
      }
    }
  }
  
  private class UIBorder
    extends AbstractBorder
    implements UIResource
  {
    private Insets _insets;
    
    UIBorder(Insets paramInsets)
    {
      if (paramInsets != null) {
        _insets = new Insets(top, left, bottom, right);
      } else {
        _insets = null;
      }
    }
    
    public void paintBorder(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!(paramComponent instanceof JComponent)) {
        return;
      }
      JComponent localJComponent = (JComponent)paramComponent;
      SynthContext localSynthContext = getContext(localJComponent);
      SynthStyle localSynthStyle = localSynthContext.getStyle();
      if (localSynthStyle != null) {
        localSynthStyle.getPainter(localSynthContext).paintFileChooserBorder(localSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public Insets getBorderInsets(Component paramComponent, Insets paramInsets)
    {
      if (paramInsets == null) {
        paramInsets = new Insets(0, 0, 0, 0);
      }
      if (_insets != null)
      {
        top = _insets.top;
        bottom = _insets.bottom;
        left = _insets.left;
        right = _insets.right;
      }
      else
      {
        top = (bottom = right = left = 0);
      }
      return paramInsets;
    }
    
    public boolean isBorderOpaque()
    {
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\plaf\synth\SynthFileChooserUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
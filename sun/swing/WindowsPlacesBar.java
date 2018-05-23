package sun.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import sun.awt.OSInfo;
import sun.awt.OSInfo.OSType;
import sun.awt.OSInfo.WindowsVersion;
import sun.awt.shell.ShellFolder;

public class WindowsPlacesBar
  extends JToolBar
  implements ActionListener, PropertyChangeListener
{
  JFileChooser fc;
  JToggleButton[] buttons;
  ButtonGroup buttonGroup;
  File[] files;
  final Dimension buttonSize;
  
  public WindowsPlacesBar(JFileChooser paramJFileChooser, boolean paramBoolean)
  {
    super(1);
    fc = paramJFileChooser;
    setFloatable(false);
    putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    int i = (OSInfo.getOSType() == OSInfo.OSType.WINDOWS) && (OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) >= 0) ? 1 : 0;
    if (paramBoolean)
    {
      buttonSize = new Dimension(83, 69);
      putClientProperty("XPStyle.subAppName", "placesbar");
      setBorder(new EmptyBorder(1, 1, 1, 1));
    }
    else
    {
      buttonSize = new Dimension(83, i != 0 ? 65 : 54);
      setBorder(new BevelBorder(1, UIManager.getColor("ToolBar.highlight"), UIManager.getColor("ToolBar.background"), UIManager.getColor("ToolBar.darkShadow"), UIManager.getColor("ToolBar.shadow")));
    }
    Color localColor = new Color(UIManager.getColor("ToolBar.shadow").getRGB());
    setBackground(localColor);
    FileSystemView localFileSystemView = paramJFileChooser.getFileSystemView();
    files = ((File[])ShellFolder.get("fileChooserShortcutPanelFolders"));
    buttons = new JToggleButton[files.length];
    buttonGroup = new ButtonGroup();
    for (int j = 0; j < files.length; j++)
    {
      if (localFileSystemView.isFileSystemRoot(files[j])) {
        files[j] = localFileSystemView.createFileObject(files[j].getAbsolutePath());
      }
      String str = localFileSystemView.getSystemDisplayName(files[j]);
      int k = str.lastIndexOf(File.separatorChar);
      if ((k >= 0) && (k < str.length() - 1)) {
        str = str.substring(k + 1);
      }
      Object localObject2;
      Object localObject1;
      if ((files[j] instanceof ShellFolder))
      {
        localObject2 = (ShellFolder)files[j];
        Image localImage = ((ShellFolder)localObject2).getIcon(true);
        if (localImage == null) {
          localImage = (Image)ShellFolder.get("shell32LargeIcon 1");
        }
        localObject1 = localImage == null ? null : new ImageIcon(localImage, ((ShellFolder)localObject2).getFolderType());
      }
      else
      {
        localObject1 = localFileSystemView.getSystemIcon(files[j]);
      }
      buttons[j] = new JToggleButton(str, (Icon)localObject1);
      if (paramBoolean)
      {
        buttons[j].putClientProperty("XPStyle.subAppName", "placesbar");
      }
      else
      {
        localObject2 = new Color(UIManager.getColor("List.selectionForeground").getRGB());
        buttons[j].setContentAreaFilled(false);
        buttons[j].setForeground((Color)localObject2);
      }
      buttons[j].setMargin(new Insets(3, 2, 1, 2));
      buttons[j].setFocusPainted(false);
      buttons[j].setIconTextGap(0);
      buttons[j].setHorizontalTextPosition(0);
      buttons[j].setVerticalTextPosition(3);
      buttons[j].setAlignmentX(0.5F);
      buttons[j].setPreferredSize(buttonSize);
      buttons[j].setMaximumSize(buttonSize);
      buttons[j].addActionListener(this);
      add(buttons[j]);
      if ((j < files.length - 1) && (paramBoolean)) {
        add(Box.createRigidArea(new Dimension(1, 1)));
      }
      buttonGroup.add(buttons[j]);
    }
    doDirectoryChanged(paramJFileChooser.getCurrentDirectory());
  }
  
  protected void doDirectoryChanged(File paramFile)
  {
    for (int i = 0; i < buttons.length; i++)
    {
      JToggleButton localJToggleButton = buttons[i];
      if (files[i].equals(paramFile))
      {
        localJToggleButton.setSelected(true);
        break;
      }
      if (localJToggleButton.isSelected())
      {
        buttonGroup.remove(localJToggleButton);
        localJToggleButton.setSelected(false);
        buttonGroup.add(localJToggleButton);
      }
    }
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str == "directoryChanged") {
      doDirectoryChanged(fc.getCurrentDirectory());
    }
  }
  
  public void actionPerformed(ActionEvent paramActionEvent)
  {
    JToggleButton localJToggleButton = (JToggleButton)paramActionEvent.getSource();
    for (int i = 0; i < buttons.length; i++) {
      if (localJToggleButton == buttons[i])
      {
        fc.setCurrentDirectory(files[i]);
        break;
      }
    }
  }
  
  public Dimension getPreferredSize()
  {
    Dimension localDimension1 = super.getMinimumSize();
    Dimension localDimension2 = super.getPreferredSize();
    int i = height;
    if ((buttons != null) && (buttons.length > 0) && (buttons.length < 5))
    {
      JToggleButton localJToggleButton = buttons[0];
      if (localJToggleButton != null)
      {
        int j = 5 * (getPreferredSizeheight + 1);
        if (j > i) {
          i = j;
        }
      }
    }
    if (i > height) {
      localDimension2 = new Dimension(width, i);
    }
    return localDimension2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\WindowsPlacesBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
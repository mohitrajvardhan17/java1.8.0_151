package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;

public class ProgressMonitor
  implements Accessible
{
  private ProgressMonitor root;
  private JDialog dialog;
  private JOptionPane pane;
  private JProgressBar myBar;
  private JLabel noteLabel;
  private Component parentComponent;
  private String note;
  private Object[] cancelOption = null;
  private Object message;
  private long T0;
  private int millisToDecideToPopup = 500;
  private int millisToPopup = 2000;
  private int min;
  private int max;
  protected AccessibleContext accessibleContext = null;
  private AccessibleContext accessibleJOptionPane = null;
  
  public ProgressMonitor(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2)
  {
    this(paramComponent, paramObject, paramString, paramInt1, paramInt2, null);
  }
  
  private ProgressMonitor(Component paramComponent, Object paramObject, String paramString, int paramInt1, int paramInt2, ProgressMonitor paramProgressMonitor)
  {
    min = paramInt1;
    max = paramInt2;
    parentComponent = paramComponent;
    cancelOption = new Object[1];
    cancelOption[0] = UIManager.getString("OptionPane.cancelButtonText");
    message = paramObject;
    note = paramString;
    if (paramProgressMonitor != null)
    {
      root = (root != null ? root : paramProgressMonitor);
      T0 = root.T0;
      dialog = root.dialog;
    }
    else
    {
      T0 = System.currentTimeMillis();
    }
  }
  
  public void setProgress(int paramInt)
  {
    if (paramInt >= max)
    {
      close();
    }
    else if (myBar != null)
    {
      myBar.setValue(paramInt);
    }
    else
    {
      long l1 = System.currentTimeMillis();
      long l2 = (int)(l1 - T0);
      if (l2 >= millisToDecideToPopup)
      {
        int i;
        if (paramInt > min) {
          i = (int)(l2 * (max - min) / (paramInt - min));
        } else {
          i = millisToPopup;
        }
        if (i >= millisToPopup)
        {
          myBar = new JProgressBar();
          myBar.setMinimum(min);
          myBar.setMaximum(max);
          myBar.setValue(paramInt);
          if (note != null) {
            noteLabel = new JLabel(note);
          }
          pane = new ProgressOptionPane(new Object[] { message, noteLabel, myBar });
          dialog = pane.createDialog(parentComponent, UIManager.getString("ProgressMonitor.progressText"));
          dialog.show();
        }
      }
    }
  }
  
  public void close()
  {
    if (dialog != null)
    {
      dialog.setVisible(false);
      dialog.dispose();
      dialog = null;
      pane = null;
      myBar = null;
    }
  }
  
  public int getMinimum()
  {
    return min;
  }
  
  public void setMinimum(int paramInt)
  {
    if (myBar != null) {
      myBar.setMinimum(paramInt);
    }
    min = paramInt;
  }
  
  public int getMaximum()
  {
    return max;
  }
  
  public void setMaximum(int paramInt)
  {
    if (myBar != null) {
      myBar.setMaximum(paramInt);
    }
    max = paramInt;
  }
  
  public boolean isCanceled()
  {
    if (pane == null) {
      return false;
    }
    Object localObject = pane.getValue();
    return (localObject != null) && (cancelOption.length == 1) && (localObject.equals(cancelOption[0]));
  }
  
  public void setMillisToDecideToPopup(int paramInt)
  {
    millisToDecideToPopup = paramInt;
  }
  
  public int getMillisToDecideToPopup()
  {
    return millisToDecideToPopup;
  }
  
  public void setMillisToPopup(int paramInt)
  {
    millisToPopup = paramInt;
  }
  
  public int getMillisToPopup()
  {
    return millisToPopup;
  }
  
  public void setNote(String paramString)
  {
    note = paramString;
    if (noteLabel != null) {
      noteLabel.setText(paramString);
    }
  }
  
  public String getNote()
  {
    return note;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleProgressMonitor();
    }
    if ((pane != null) && (accessibleJOptionPane == null) && ((accessibleContext instanceof AccessibleProgressMonitor))) {
      ((AccessibleProgressMonitor)accessibleContext).optionPaneCreated();
    }
    return accessibleContext;
  }
  
  protected class AccessibleProgressMonitor
    extends AccessibleContext
    implements AccessibleText, ChangeListener, PropertyChangeListener
  {
    private Object oldModelValue;
    
    protected AccessibleProgressMonitor() {}
    
    private void optionPaneCreated()
    {
      accessibleJOptionPane = ProgressMonitor.ProgressOptionPane.access$400((ProgressMonitor.ProgressOptionPane)pane);
      if (myBar != null) {
        myBar.addChangeListener(this);
      }
      if (noteLabel != null) {
        noteLabel.addPropertyChangeListener(this);
      }
    }
    
    public void stateChanged(ChangeEvent paramChangeEvent)
    {
      if (paramChangeEvent == null) {
        return;
      }
      if (myBar != null)
      {
        Integer localInteger = Integer.valueOf(myBar.getValue());
        firePropertyChange("AccessibleValue", oldModelValue, localInteger);
        oldModelValue = localInteger;
      }
    }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      if ((paramPropertyChangeEvent.getSource() == noteLabel) && (paramPropertyChangeEvent.getPropertyName() == "text")) {
        firePropertyChange("AccessibleText", null, Integer.valueOf(0));
      }
    }
    
    public String getAccessibleName()
    {
      if (accessibleName != null) {
        return accessibleName;
      }
      if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getAccessibleName();
      }
      return null;
    }
    
    public String getAccessibleDescription()
    {
      if (accessibleDescription != null) {
        return accessibleDescription;
      }
      if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getAccessibleDescription();
      }
      return null;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.PROGRESS_MONITOR;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getAccessibleStateSet();
      }
      return null;
    }
    
    public Accessible getAccessibleParent()
    {
      return dialog;
    }
    
    private AccessibleContext getParentAccessibleContext()
    {
      if (dialog != null) {
        return dialog.getAccessibleContext();
      }
      return null;
    }
    
    public int getAccessibleIndexInParent()
    {
      if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getAccessibleIndexInParent();
      }
      return -1;
    }
    
    public int getAccessibleChildrenCount()
    {
      AccessibleContext localAccessibleContext = getPanelAccessibleContext();
      if (localAccessibleContext != null) {
        return localAccessibleContext.getAccessibleChildrenCount();
      }
      return 0;
    }
    
    public Accessible getAccessibleChild(int paramInt)
    {
      AccessibleContext localAccessibleContext = getPanelAccessibleContext();
      if (localAccessibleContext != null) {
        return localAccessibleContext.getAccessibleChild(paramInt);
      }
      return null;
    }
    
    private AccessibleContext getPanelAccessibleContext()
    {
      if (myBar != null)
      {
        Container localContainer = myBar.getParent();
        if ((localContainer instanceof Accessible)) {
          return localContainer.getAccessibleContext();
        }
      }
      return null;
    }
    
    public Locale getLocale()
      throws IllegalComponentStateException
    {
      if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getLocale();
      }
      return null;
    }
    
    public AccessibleComponent getAccessibleComponent()
    {
      if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getAccessibleComponent();
      }
      return null;
    }
    
    public AccessibleValue getAccessibleValue()
    {
      if (myBar != null) {
        return myBar.getAccessibleContext().getAccessibleValue();
      }
      return null;
    }
    
    public AccessibleText getAccessibleText()
    {
      if (getNoteLabelAccessibleText() != null) {
        return this;
      }
      return null;
    }
    
    private AccessibleText getNoteLabelAccessibleText()
    {
      if (noteLabel != null) {
        return noteLabel.getAccessibleContext().getAccessibleText();
      }
      return null;
    }
    
    public int getIndexAtPoint(Point paramPoint)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if ((localAccessibleText != null) && (sameWindowAncestor(pane, noteLabel)))
      {
        Point localPoint = SwingUtilities.convertPoint(pane, paramPoint, noteLabel);
        if (localPoint != null) {
          return localAccessibleText.getIndexAtPoint(localPoint);
        }
      }
      return -1;
    }
    
    public Rectangle getCharacterBounds(int paramInt)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if ((localAccessibleText != null) && (sameWindowAncestor(pane, noteLabel)))
      {
        Rectangle localRectangle = localAccessibleText.getCharacterBounds(paramInt);
        if (localRectangle != null) {
          return SwingUtilities.convertRectangle(noteLabel, localRectangle, pane);
        }
      }
      return null;
    }
    
    private boolean sameWindowAncestor(Component paramComponent1, Component paramComponent2)
    {
      if ((paramComponent1 == null) || (paramComponent2 == null)) {
        return false;
      }
      return SwingUtilities.getWindowAncestor(paramComponent1) == SwingUtilities.getWindowAncestor(paramComponent2);
    }
    
    public int getCharCount()
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getCharCount();
      }
      return -1;
    }
    
    public int getCaretPosition()
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getCaretPosition();
      }
      return -1;
    }
    
    public String getAtIndex(int paramInt1, int paramInt2)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getAtIndex(paramInt1, paramInt2);
      }
      return null;
    }
    
    public String getAfterIndex(int paramInt1, int paramInt2)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getAfterIndex(paramInt1, paramInt2);
      }
      return null;
    }
    
    public String getBeforeIndex(int paramInt1, int paramInt2)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getBeforeIndex(paramInt1, paramInt2);
      }
      return null;
    }
    
    public AttributeSet getCharacterAttribute(int paramInt)
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getCharacterAttribute(paramInt);
      }
      return null;
    }
    
    public int getSelectionStart()
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getSelectionStart();
      }
      return -1;
    }
    
    public int getSelectionEnd()
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getSelectionEnd();
      }
      return -1;
    }
    
    public String getSelectedText()
    {
      AccessibleText localAccessibleText = getNoteLabelAccessibleText();
      if (localAccessibleText != null) {
        return localAccessibleText.getSelectedText();
      }
      return null;
    }
  }
  
  private class ProgressOptionPane
    extends JOptionPane
  {
    ProgressOptionPane(Object paramObject)
    {
      super(1, -1, null, cancelOption, null);
    }
    
    public int getMaxCharactersPerLineCount()
    {
      return 60;
    }
    
    public JDialog createDialog(Component paramComponent, String paramString)
    {
      Window localWindow = JOptionPane.getWindowForComponent(paramComponent);
      final JDialog localJDialog;
      if ((localWindow instanceof Frame)) {
        localJDialog = new JDialog((Frame)localWindow, paramString, false);
      } else {
        localJDialog = new JDialog((Dialog)localWindow, paramString, false);
      }
      if ((localWindow instanceof SwingUtilities.SharedOwnerFrame))
      {
        localObject = SwingUtilities.getSharedOwnerFrameShutdownListener();
        localJDialog.addWindowListener((WindowListener)localObject);
      }
      Object localObject = localJDialog.getContentPane();
      ((Container)localObject).setLayout(new BorderLayout());
      ((Container)localObject).add(this, "Center");
      localJDialog.pack();
      localJDialog.setLocationRelativeTo(paramComponent);
      localJDialog.addWindowListener(new WindowAdapter()
      {
        boolean gotFocus = false;
        
        public void windowClosing(WindowEvent paramAnonymousWindowEvent)
        {
          setValue(cancelOption[0]);
        }
        
        public void windowActivated(WindowEvent paramAnonymousWindowEvent)
        {
          if (!gotFocus)
          {
            selectInitialValue();
            gotFocus = true;
          }
        }
      });
      addPropertyChangeListener(new PropertyChangeListener()
      {
        public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent)
        {
          if ((localJDialog.isVisible()) && (paramAnonymousPropertyChangeEvent.getSource() == ProgressMonitor.ProgressOptionPane.this) && ((paramAnonymousPropertyChangeEvent.getPropertyName().equals("value")) || (paramAnonymousPropertyChangeEvent.getPropertyName().equals("inputValue"))))
          {
            localJDialog.setVisible(false);
            localJDialog.dispose();
          }
        }
      });
      return localJDialog;
    }
    
    public AccessibleContext getAccessibleContext()
    {
      return ProgressMonitor.this.getAccessibleContext();
    }
    
    private AccessibleContext getAccessibleJOptionPane()
    {
      return super.getAccessibleContext();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\ProgressMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
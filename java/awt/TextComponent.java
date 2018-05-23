package java.awt;

import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextComponentPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.BreakIterator;
import java.util.EventListener;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleText;
import javax.swing.text.AttributeSet;
import sun.awt.InputMethodSupport;
import sun.security.util.SecurityConstants.AWT;

public class TextComponent
  extends Component
  implements Accessible
{
  String text;
  boolean editable = true;
  int selectionStart;
  int selectionEnd;
  boolean backgroundSetByClientCode = false;
  protected transient TextListener textListener;
  private static final long serialVersionUID = -2214773872412987419L;
  private int textComponentSerializedDataVersion = 1;
  private boolean checkForEnableIM = true;
  
  TextComponent(String paramString)
    throws HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    text = (paramString != null ? paramString : "");
    setCursor(Cursor.getPredefinedCursor(2));
  }
  
  private void enableInputMethodsIfNecessary()
  {
    if (checkForEnableIM)
    {
      checkForEnableIM = false;
      try
      {
        Toolkit localToolkit = Toolkit.getDefaultToolkit();
        boolean bool = false;
        if ((localToolkit instanceof InputMethodSupport)) {
          bool = ((InputMethodSupport)localToolkit).enableInputMethodsForTextComponent();
        }
        enableInputMethods(bool);
      }
      catch (Exception localException) {}
    }
  }
  
  public void enableInputMethods(boolean paramBoolean)
  {
    checkForEnableIM = false;
    super.enableInputMethods(paramBoolean);
  }
  
  boolean areInputMethodsEnabled()
  {
    if (checkForEnableIM) {
      enableInputMethodsIfNecessary();
    }
    return (eventMask & 0x1000) != 0L;
  }
  
  public InputMethodRequests getInputMethodRequests()
  {
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    if (localTextComponentPeer != null) {
      return localTextComponentPeer.getInputMethodRequests();
    }
    return null;
  }
  
  public void addNotify()
  {
    super.addNotify();
    enableInputMethodsIfNecessary();
  }
  
  public void removeNotify()
  {
    synchronized (getTreeLock())
    {
      TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
      if (localTextComponentPeer != null)
      {
        text = localTextComponentPeer.getText();
        selectionStart = localTextComponentPeer.getSelectionStart();
        selectionEnd = localTextComponentPeer.getSelectionEnd();
      }
      super.removeNotify();
    }
  }
  
  public synchronized void setText(String paramString)
  {
    int i = ((text == null) || (text.isEmpty())) && ((paramString == null) || (paramString.isEmpty())) ? 1 : 0;
    text = (paramString != null ? paramString : "");
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    if ((localTextComponentPeer != null) && (i == 0)) {
      localTextComponentPeer.setText(text);
    }
  }
  
  public synchronized String getText()
  {
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    if (localTextComponentPeer != null) {
      text = localTextComponentPeer.getText();
    }
    return text;
  }
  
  public synchronized String getSelectedText()
  {
    return getText().substring(getSelectionStart(), getSelectionEnd());
  }
  
  public boolean isEditable()
  {
    return editable;
  }
  
  public synchronized void setEditable(boolean paramBoolean)
  {
    if (editable == paramBoolean) {
      return;
    }
    editable = paramBoolean;
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    if (localTextComponentPeer != null) {
      localTextComponentPeer.setEditable(paramBoolean);
    }
  }
  
  public Color getBackground()
  {
    if ((!editable) && (!backgroundSetByClientCode)) {
      return SystemColor.control;
    }
    return super.getBackground();
  }
  
  public void setBackground(Color paramColor)
  {
    backgroundSetByClientCode = true;
    super.setBackground(paramColor);
  }
  
  public synchronized int getSelectionStart()
  {
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    if (localTextComponentPeer != null) {
      selectionStart = localTextComponentPeer.getSelectionStart();
    }
    return selectionStart;
  }
  
  public synchronized void setSelectionStart(int paramInt)
  {
    select(paramInt, getSelectionEnd());
  }
  
  public synchronized int getSelectionEnd()
  {
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    if (localTextComponentPeer != null) {
      selectionEnd = localTextComponentPeer.getSelectionEnd();
    }
    return selectionEnd;
  }
  
  public synchronized void setSelectionEnd(int paramInt)
  {
    select(getSelectionStart(), paramInt);
  }
  
  public synchronized void select(int paramInt1, int paramInt2)
  {
    String str = getText();
    if (paramInt1 < 0) {
      paramInt1 = 0;
    }
    if (paramInt1 > str.length()) {
      paramInt1 = str.length();
    }
    if (paramInt2 > str.length()) {
      paramInt2 = str.length();
    }
    if (paramInt2 < paramInt1) {
      paramInt2 = paramInt1;
    }
    selectionStart = paramInt1;
    selectionEnd = paramInt2;
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    if (localTextComponentPeer != null) {
      localTextComponentPeer.select(paramInt1, paramInt2);
    }
  }
  
  public synchronized void selectAll()
  {
    selectionStart = 0;
    selectionEnd = getText().length();
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    if (localTextComponentPeer != null) {
      localTextComponentPeer.select(selectionStart, selectionEnd);
    }
  }
  
  public synchronized void setCaretPosition(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("position less than zero.");
    }
    int i = getText().length();
    if (paramInt > i) {
      paramInt = i;
    }
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    if (localTextComponentPeer != null) {
      localTextComponentPeer.setCaretPosition(paramInt);
    } else {
      select(paramInt, paramInt);
    }
  }
  
  public synchronized int getCaretPosition()
  {
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    int i = 0;
    if (localTextComponentPeer != null) {
      i = localTextComponentPeer.getCaretPosition();
    } else {
      i = selectionStart;
    }
    int j = getText().length();
    if (i > j) {
      i = j;
    }
    return i;
  }
  
  public synchronized void addTextListener(TextListener paramTextListener)
  {
    if (paramTextListener == null) {
      return;
    }
    textListener = AWTEventMulticaster.add(textListener, paramTextListener);
    newEventsOnly = true;
  }
  
  public synchronized void removeTextListener(TextListener paramTextListener)
  {
    if (paramTextListener == null) {
      return;
    }
    textListener = AWTEventMulticaster.remove(textListener, paramTextListener);
  }
  
  public synchronized TextListener[] getTextListeners()
  {
    return (TextListener[])getListeners(TextListener.class);
  }
  
  public <T extends EventListener> T[] getListeners(Class<T> paramClass)
  {
    TextListener localTextListener = null;
    if (paramClass == TextListener.class) {
      localTextListener = textListener;
    } else {
      return super.getListeners(paramClass);
    }
    return AWTEventMulticaster.getListeners(localTextListener, paramClass);
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent)
  {
    if (id == 900) {
      return ((eventMask & 0x400) != 0L) || (textListener != null);
    }
    return super.eventEnabled(paramAWTEvent);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof TextEvent))
    {
      processTextEvent((TextEvent)paramAWTEvent);
      return;
    }
    super.processEvent(paramAWTEvent);
  }
  
  protected void processTextEvent(TextEvent paramTextEvent)
  {
    TextListener localTextListener = textListener;
    if (localTextListener != null)
    {
      int i = paramTextEvent.getID();
      switch (i)
      {
      case 900: 
        localTextListener.textValueChanged(paramTextEvent);
      }
    }
  }
  
  protected String paramString()
  {
    String str = super.paramString() + ",text=" + getText();
    if (editable) {
      str = str + ",editable";
    }
    return str + ",selection=" + getSelectionStart() + "-" + getSelectionEnd();
  }
  
  private boolean canAccessClipboard()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null) {
      return true;
    }
    try
    {
      localSecurityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
      return true;
    }
    catch (SecurityException localSecurityException) {}
    return false;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    TextComponentPeer localTextComponentPeer = (TextComponentPeer)peer;
    if (localTextComponentPeer != null)
    {
      text = localTextComponentPeer.getText();
      selectionStart = localTextComponentPeer.getSelectionStart();
      selectionEnd = localTextComponentPeer.getSelectionEnd();
    }
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "textL", textListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    GraphicsEnvironment.checkHeadless();
    paramObjectInputStream.defaultReadObject();
    text = (text != null ? text : "");
    select(selectionStart, selectionEnd);
    Object localObject;
    while (null != (localObject = paramObjectInputStream.readObject()))
    {
      String str = ((String)localObject).intern();
      if ("textL" == str) {
        addTextListener((TextListener)paramObjectInputStream.readObject());
      } else {
        paramObjectInputStream.readObject();
      }
    }
    enableInputMethodsIfNecessary();
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleAWTTextComponent();
    }
    return accessibleContext;
  }
  
  protected class AccessibleAWTTextComponent
    extends Component.AccessibleAWTComponent
    implements AccessibleText, TextListener
  {
    private static final long serialVersionUID = 3631432373506317811L;
    private static final boolean NEXT = true;
    private static final boolean PREVIOUS = false;
    
    public AccessibleAWTTextComponent()
    {
      super();
      addTextListener(this);
    }
    
    public void textValueChanged(TextEvent paramTextEvent)
    {
      Integer localInteger = Integer.valueOf(TextComponent.this.getCaretPosition());
      firePropertyChange("AccessibleText", null, localInteger);
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      if (isEditable()) {
        localAccessibleStateSet.add(AccessibleState.EDITABLE);
      }
      return localAccessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.TEXT;
    }
    
    public AccessibleText getAccessibleText()
    {
      return this;
    }
    
    public int getIndexAtPoint(Point paramPoint)
    {
      return -1;
    }
    
    public Rectangle getCharacterBounds(int paramInt)
    {
      return null;
    }
    
    public int getCharCount()
    {
      return getText().length();
    }
    
    public int getCaretPosition()
    {
      return TextComponent.this.getCaretPosition();
    }
    
    public AttributeSet getCharacterAttribute(int paramInt)
    {
      return null;
    }
    
    public int getSelectionStart()
    {
      return TextComponent.this.getSelectionStart();
    }
    
    public int getSelectionEnd()
    {
      return TextComponent.this.getSelectionEnd();
    }
    
    public String getSelectedText()
    {
      String str = TextComponent.this.getSelectedText();
      if ((str == null) || (str.equals(""))) {
        return null;
      }
      return str;
    }
    
    public String getAtIndex(int paramInt1, int paramInt2)
    {
      if ((paramInt2 < 0) || (paramInt2 >= getText().length())) {
        return null;
      }
      String str;
      BreakIterator localBreakIterator;
      int i;
      switch (paramInt1)
      {
      case 1: 
        return getText().substring(paramInt2, paramInt2 + 1);
      case 2: 
        str = getText();
        localBreakIterator = BreakIterator.getWordInstance();
        localBreakIterator.setText(str);
        i = localBreakIterator.following(paramInt2);
        return str.substring(localBreakIterator.previous(), i);
      case 3: 
        str = getText();
        localBreakIterator = BreakIterator.getSentenceInstance();
        localBreakIterator.setText(str);
        i = localBreakIterator.following(paramInt2);
        return str.substring(localBreakIterator.previous(), i);
      }
      return null;
    }
    
    private int findWordLimit(int paramInt, BreakIterator paramBreakIterator, boolean paramBoolean, String paramString)
    {
      int i = paramBoolean == true ? paramBreakIterator.following(paramInt) : paramBreakIterator.preceding(paramInt);
      for (int j = paramBoolean == true ? paramBreakIterator.next() : paramBreakIterator.previous(); j != -1; j = paramBoolean == true ? paramBreakIterator.next() : paramBreakIterator.previous())
      {
        for (int k = Math.min(i, j); k < Math.max(i, j); k++) {
          if (Character.isLetter(paramString.charAt(k))) {
            return i;
          }
        }
        i = j;
      }
      return -1;
    }
    
    public String getAfterIndex(int paramInt1, int paramInt2)
    {
      if ((paramInt2 < 0) || (paramInt2 >= getText().length())) {
        return null;
      }
      String str;
      BreakIterator localBreakIterator;
      int i;
      int j;
      switch (paramInt1)
      {
      case 1: 
        if (paramInt2 + 1 >= getText().length()) {
          return null;
        }
        return getText().substring(paramInt2 + 1, paramInt2 + 2);
      case 2: 
        str = getText();
        localBreakIterator = BreakIterator.getWordInstance();
        localBreakIterator.setText(str);
        i = findWordLimit(paramInt2, localBreakIterator, true, str);
        if ((i == -1) || (i >= str.length())) {
          return null;
        }
        j = localBreakIterator.following(i);
        if ((j == -1) || (j >= str.length())) {
          return null;
        }
        return str.substring(i, j);
      case 3: 
        str = getText();
        localBreakIterator = BreakIterator.getSentenceInstance();
        localBreakIterator.setText(str);
        i = localBreakIterator.following(paramInt2);
        if ((i == -1) || (i >= str.length())) {
          return null;
        }
        j = localBreakIterator.following(i);
        if ((j == -1) || (j >= str.length())) {
          return null;
        }
        return str.substring(i, j);
      }
      return null;
    }
    
    public String getBeforeIndex(int paramInt1, int paramInt2)
    {
      if ((paramInt2 < 0) || (paramInt2 > getText().length() - 1)) {
        return null;
      }
      String str;
      BreakIterator localBreakIterator;
      int i;
      int j;
      switch (paramInt1)
      {
      case 1: 
        if (paramInt2 == 0) {
          return null;
        }
        return getText().substring(paramInt2 - 1, paramInt2);
      case 2: 
        str = getText();
        localBreakIterator = BreakIterator.getWordInstance();
        localBreakIterator.setText(str);
        i = findWordLimit(paramInt2, localBreakIterator, false, str);
        if (i == -1) {
          return null;
        }
        j = localBreakIterator.preceding(i);
        if (j == -1) {
          return null;
        }
        return str.substring(j, i);
      case 3: 
        str = getText();
        localBreakIterator = BreakIterator.getSentenceInstance();
        localBreakIterator.setText(str);
        i = localBreakIterator.following(paramInt2);
        i = localBreakIterator.previous();
        j = localBreakIterator.previous();
        if (j == -1) {
          return null;
        }
        return str.substring(j, i);
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\TextComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
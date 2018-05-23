package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleText;
import javax.accessibility.AccessibleTextSequence;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Segment;

public class JPasswordField
  extends JTextField
{
  private static final String uiClassID = "PasswordFieldUI";
  private char echoChar;
  private boolean echoCharSet = false;
  
  public JPasswordField()
  {
    this(null, null, 0);
  }
  
  public JPasswordField(String paramString)
  {
    this(null, paramString, 0);
  }
  
  public JPasswordField(int paramInt)
  {
    this(null, null, paramInt);
  }
  
  public JPasswordField(String paramString, int paramInt)
  {
    this(null, paramString, paramInt);
  }
  
  public JPasswordField(Document paramDocument, String paramString, int paramInt)
  {
    super(paramDocument, paramString, paramInt);
    enableInputMethods(false);
  }
  
  public String getUIClassID()
  {
    return "PasswordFieldUI";
  }
  
  public void updateUI()
  {
    if (!echoCharSet) {
      echoChar = '*';
    }
    super.updateUI();
  }
  
  public char getEchoChar()
  {
    return echoChar;
  }
  
  public void setEchoChar(char paramChar)
  {
    echoChar = paramChar;
    echoCharSet = true;
    repaint();
    revalidate();
  }
  
  public boolean echoCharIsSet()
  {
    return echoChar != 0;
  }
  
  public void cut()
  {
    if (getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
    } else {
      super.cut();
    }
  }
  
  public void copy()
  {
    if (getClientProperty("JPasswordField.cutCopyAllowed") != Boolean.TRUE) {
      UIManager.getLookAndFeel().provideErrorFeedback(this);
    } else {
      super.copy();
    }
  }
  
  @Deprecated
  public String getText()
  {
    return super.getText();
  }
  
  @Deprecated
  public String getText(int paramInt1, int paramInt2)
    throws BadLocationException
  {
    return super.getText(paramInt1, paramInt2);
  }
  
  public char[] getPassword()
  {
    Document localDocument = getDocument();
    Segment localSegment = new Segment();
    try
    {
      localDocument.getText(0, localDocument.getLength(), localSegment);
    }
    catch (BadLocationException localBadLocationException)
    {
      return null;
    }
    char[] arrayOfChar = new char[count];
    System.arraycopy(array, offset, arrayOfChar, 0, count);
    return arrayOfChar;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("PasswordFieldUI"))
    {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if ((b == 0) && (ui != null)) {
        ui.installUI(this);
      }
    }
  }
  
  protected String paramString()
  {
    return super.paramString() + ",echoChar=" + echoChar;
  }
  
  boolean customSetUIProperty(String paramString, Object paramObject)
  {
    if (paramString == "echoChar")
    {
      if (!echoCharSet)
      {
        setEchoChar(((Character)paramObject).charValue());
        echoCharSet = false;
      }
      return true;
    }
    return false;
  }
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleJPasswordField();
    }
    return accessibleContext;
  }
  
  protected class AccessibleJPasswordField
    extends JTextField.AccessibleJTextField
  {
    protected AccessibleJPasswordField()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.PASSWORD_TEXT;
    }
    
    public AccessibleText getAccessibleText()
    {
      return this;
    }
    
    private String getEchoString(String paramString)
    {
      if (paramString == null) {
        return null;
      }
      char[] arrayOfChar = new char[paramString.length()];
      Arrays.fill(arrayOfChar, getEchoChar());
      return new String(arrayOfChar);
    }
    
    public String getAtIndex(int paramInt1, int paramInt2)
    {
      String str = null;
      if (paramInt1 == 1)
      {
        str = super.getAtIndex(paramInt1, paramInt2);
      }
      else
      {
        char[] arrayOfChar = getPassword();
        if ((arrayOfChar == null) || (paramInt2 < 0) || (paramInt2 >= arrayOfChar.length)) {
          return null;
        }
        str = new String(arrayOfChar);
      }
      return getEchoString(str);
    }
    
    public String getAfterIndex(int paramInt1, int paramInt2)
    {
      if (paramInt1 == 1)
      {
        String str = super.getAfterIndex(paramInt1, paramInt2);
        return getEchoString(str);
      }
      return null;
    }
    
    public String getBeforeIndex(int paramInt1, int paramInt2)
    {
      if (paramInt1 == 1)
      {
        String str = super.getBeforeIndex(paramInt1, paramInt2);
        return getEchoString(str);
      }
      return null;
    }
    
    public String getTextRange(int paramInt1, int paramInt2)
    {
      String str = super.getTextRange(paramInt1, paramInt2);
      return getEchoString(str);
    }
    
    public AccessibleTextSequence getTextSequenceAt(int paramInt1, int paramInt2)
    {
      if (paramInt1 == 1)
      {
        localObject = super.getTextSequenceAt(paramInt1, paramInt2);
        if (localObject == null) {
          return null;
        }
        return new AccessibleTextSequence(startIndex, endIndex, getEchoString(text));
      }
      Object localObject = getPassword();
      if ((localObject == null) || (paramInt2 < 0) || (paramInt2 >= localObject.length)) {
        return null;
      }
      String str = new String((char[])localObject);
      return new AccessibleTextSequence(0, localObject.length - 1, getEchoString(str));
    }
    
    public AccessibleTextSequence getTextSequenceAfter(int paramInt1, int paramInt2)
    {
      if (paramInt1 == 1)
      {
        AccessibleTextSequence localAccessibleTextSequence = super.getTextSequenceAfter(paramInt1, paramInt2);
        if (localAccessibleTextSequence == null) {
          return null;
        }
        return new AccessibleTextSequence(startIndex, endIndex, getEchoString(text));
      }
      return null;
    }
    
    public AccessibleTextSequence getTextSequenceBefore(int paramInt1, int paramInt2)
    {
      if (paramInt1 == 1)
      {
        AccessibleTextSequence localAccessibleTextSequence = super.getTextSequenceBefore(paramInt1, paramInt2);
        if (localAccessibleTextSequence == null) {
          return null;
        }
        return new AccessibleTextSequence(startIndex, endIndex, getEchoString(text));
      }
      return null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\JPasswordField.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
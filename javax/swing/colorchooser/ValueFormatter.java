package javax.swing.colorchooser;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

final class ValueFormatter
  extends JFormattedTextField.AbstractFormatter
  implements FocusListener, Runnable
{
  private final DocumentFilter filter = new DocumentFilter()
  {
    public void remove(DocumentFilter.FilterBypass paramAnonymousFilterBypass, int paramAnonymousInt1, int paramAnonymousInt2)
      throws BadLocationException
    {
      if (ValueFormatter.this.isValid(paramAnonymousFilterBypass.getDocument().getLength() - paramAnonymousInt2)) {
        paramAnonymousFilterBypass.remove(paramAnonymousInt1, paramAnonymousInt2);
      }
    }
    
    public void replace(DocumentFilter.FilterBypass paramAnonymousFilterBypass, int paramAnonymousInt1, int paramAnonymousInt2, String paramAnonymousString, AttributeSet paramAnonymousAttributeSet)
      throws BadLocationException
    {
      if ((ValueFormatter.this.isValid(paramAnonymousFilterBypass.getDocument().getLength() + paramAnonymousString.length() - paramAnonymousInt2)) && (ValueFormatter.this.isValid(paramAnonymousString))) {
        paramAnonymousFilterBypass.replace(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousString.toUpperCase(Locale.ENGLISH), paramAnonymousAttributeSet);
      }
    }
    
    public void insertString(DocumentFilter.FilterBypass paramAnonymousFilterBypass, int paramAnonymousInt, String paramAnonymousString, AttributeSet paramAnonymousAttributeSet)
      throws BadLocationException
    {
      if ((ValueFormatter.this.isValid(paramAnonymousFilterBypass.getDocument().getLength() + paramAnonymousString.length())) && (ValueFormatter.this.isValid(paramAnonymousString))) {
        paramAnonymousFilterBypass.insertString(paramAnonymousInt, paramAnonymousString.toUpperCase(Locale.ENGLISH), paramAnonymousAttributeSet);
      }
    }
  };
  private final int length;
  private final int radix;
  private JFormattedTextField text;
  
  static void init(int paramInt, boolean paramBoolean, JFormattedTextField paramJFormattedTextField)
  {
    ValueFormatter localValueFormatter = new ValueFormatter(paramInt, paramBoolean);
    paramJFormattedTextField.setColumns(paramInt);
    paramJFormattedTextField.setFormatterFactory(new DefaultFormatterFactory(localValueFormatter));
    paramJFormattedTextField.setHorizontalAlignment(4);
    paramJFormattedTextField.setMinimumSize(paramJFormattedTextField.getPreferredSize());
    paramJFormattedTextField.addFocusListener(localValueFormatter);
  }
  
  ValueFormatter(int paramInt, boolean paramBoolean)
  {
    length = paramInt;
    radix = (paramBoolean ? 16 : 10);
  }
  
  public Object stringToValue(String paramString)
    throws ParseException
  {
    try
    {
      return Integer.valueOf(paramString, radix);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      ParseException localParseException = new ParseException("illegal format", 0);
      localParseException.initCause(localNumberFormatException);
      throw localParseException;
    }
  }
  
  public String valueToString(Object paramObject)
    throws ParseException
  {
    if ((paramObject instanceof Integer))
    {
      if (radix == 10) {
        return paramObject.toString();
      }
      int i = ((Integer)paramObject).intValue();
      int j = length;
      char[] arrayOfChar = new char[j];
      while (0 < j--)
      {
        arrayOfChar[j] = Character.forDigit(i & 0xF, radix);
        i >>= 4;
      }
      return new String(arrayOfChar).toUpperCase(Locale.ENGLISH);
    }
    throw new ParseException("illegal object", 0);
  }
  
  protected DocumentFilter getDocumentFilter()
  {
    return filter;
  }
  
  public void focusGained(FocusEvent paramFocusEvent)
  {
    Object localObject = paramFocusEvent.getSource();
    if ((localObject instanceof JFormattedTextField))
    {
      text = ((JFormattedTextField)localObject);
      SwingUtilities.invokeLater(this);
    }
  }
  
  public void focusLost(FocusEvent paramFocusEvent) {}
  
  public void run()
  {
    if (text != null) {
      text.selectAll();
    }
  }
  
  private boolean isValid(int paramInt)
  {
    return (0 <= paramInt) && (paramInt <= length);
  }
  
  private boolean isValid(String paramString)
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++)
    {
      char c = paramString.charAt(j);
      if (Character.digit(c, radix) < 0) {
        return false;
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\colorchooser\ValueFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
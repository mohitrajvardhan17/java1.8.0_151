package javax.swing.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.JFormattedTextField;

public class MaskFormatter
  extends DefaultFormatter
{
  private static final char DIGIT_KEY = '#';
  private static final char LITERAL_KEY = '\'';
  private static final char UPPERCASE_KEY = 'U';
  private static final char LOWERCASE_KEY = 'L';
  private static final char ALPHA_NUMERIC_KEY = 'A';
  private static final char CHARACTER_KEY = '?';
  private static final char ANYTHING_KEY = '*';
  private static final char HEX_KEY = 'H';
  private static final MaskCharacter[] EmptyMaskChars = new MaskCharacter[0];
  private String mask;
  private transient MaskCharacter[] maskChars;
  private String validCharacters;
  private String invalidCharacters;
  private String placeholderString;
  private char placeholder;
  private boolean containsLiteralChars;
  
  public MaskFormatter()
  {
    setAllowsInvalid(false);
    containsLiteralChars = true;
    maskChars = EmptyMaskChars;
    placeholder = ' ';
  }
  
  public MaskFormatter(String paramString)
    throws ParseException
  {
    this();
    setMask(paramString);
  }
  
  public void setMask(String paramString)
    throws ParseException
  {
    mask = paramString;
    updateInternalMask();
  }
  
  public String getMask()
  {
    return mask;
  }
  
  public void setValidCharacters(String paramString)
  {
    validCharacters = paramString;
  }
  
  public String getValidCharacters()
  {
    return validCharacters;
  }
  
  public void setInvalidCharacters(String paramString)
  {
    invalidCharacters = paramString;
  }
  
  public String getInvalidCharacters()
  {
    return invalidCharacters;
  }
  
  public void setPlaceholder(String paramString)
  {
    placeholderString = paramString;
  }
  
  public String getPlaceholder()
  {
    return placeholderString;
  }
  
  public void setPlaceholderCharacter(char paramChar)
  {
    placeholder = paramChar;
  }
  
  public char getPlaceholderCharacter()
  {
    return placeholder;
  }
  
  public void setValueContainsLiteralCharacters(boolean paramBoolean)
  {
    containsLiteralChars = paramBoolean;
  }
  
  public boolean getValueContainsLiteralCharacters()
  {
    return containsLiteralChars;
  }
  
  public Object stringToValue(String paramString)
    throws ParseException
  {
    return stringToValue(paramString, true);
  }
  
  public String valueToString(Object paramObject)
    throws ParseException
  {
    String str1 = paramObject == null ? "" : paramObject.toString();
    StringBuilder localStringBuilder = new StringBuilder();
    String str2 = getPlaceholder();
    int[] arrayOfInt = { 0 };
    append(localStringBuilder, str1, arrayOfInt, str2, maskChars);
    return localStringBuilder.toString();
  }
  
  public void install(JFormattedTextField paramJFormattedTextField)
  {
    super.install(paramJFormattedTextField);
    if (paramJFormattedTextField != null)
    {
      Object localObject = paramJFormattedTextField.getValue();
      try
      {
        stringToValue(valueToString(localObject));
      }
      catch (ParseException localParseException)
      {
        setEditValid(false);
      }
    }
  }
  
  private Object stringToValue(String paramString, boolean paramBoolean)
    throws ParseException
  {
    int i;
    if ((i = getInvalidOffset(paramString, paramBoolean)) == -1)
    {
      if (!getValueContainsLiteralCharacters()) {
        paramString = stripLiteralChars(paramString);
      }
      return super.stringToValue(paramString);
    }
    throw new ParseException("stringToValue passed invalid value", i);
  }
  
  private int getInvalidOffset(String paramString, boolean paramBoolean)
  {
    int i = paramString.length();
    if (i != getMaxLength()) {
      return i;
    }
    int j = 0;
    int k = paramString.length();
    while (j < k)
    {
      char c = paramString.charAt(j);
      if ((!isValidCharacter(j, c)) && ((paramBoolean) || (!isPlaceholder(j, c)))) {
        return j;
      }
      j++;
    }
    return -1;
  }
  
  private void append(StringBuilder paramStringBuilder, String paramString1, int[] paramArrayOfInt, String paramString2, MaskCharacter[] paramArrayOfMaskCharacter)
    throws ParseException
  {
    int i = 0;
    int j = paramArrayOfMaskCharacter.length;
    while (i < j)
    {
      paramArrayOfMaskCharacter[i].append(paramStringBuilder, paramString1, paramArrayOfInt, paramString2);
      i++;
    }
  }
  
  private void updateInternalMask()
    throws ParseException
  {
    String str = getMask();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = localArrayList1;
    if (str != null)
    {
      int i = 0;
      int j = str.length();
      while (i < j)
      {
        char c = str.charAt(i);
        switch (c)
        {
        case '#': 
          localArrayList2.add(new DigitMaskCharacter(null));
          break;
        case '\'': 
          i++;
          if (i < j)
          {
            c = str.charAt(i);
            localArrayList2.add(new LiteralCharacter(c));
          }
          break;
        case 'U': 
          localArrayList2.add(new UpperCaseCharacter(null));
          break;
        case 'L': 
          localArrayList2.add(new LowerCaseCharacter(null));
          break;
        case 'A': 
          localArrayList2.add(new AlphaNumericCharacter(null));
          break;
        case '?': 
          localArrayList2.add(new CharCharacter(null));
          break;
        case '*': 
          localArrayList2.add(new MaskCharacter(null));
          break;
        case 'H': 
          localArrayList2.add(new HexCharacter(null));
          break;
        default: 
          localArrayList2.add(new LiteralCharacter(c));
        }
        i++;
      }
    }
    if (localArrayList1.size() == 0)
    {
      maskChars = EmptyMaskChars;
    }
    else
    {
      maskChars = new MaskCharacter[localArrayList1.size()];
      localArrayList1.toArray(maskChars);
    }
  }
  
  private MaskCharacter getMaskCharacter(int paramInt)
  {
    if (paramInt >= maskChars.length) {
      return null;
    }
    return maskChars[paramInt];
  }
  
  private boolean isPlaceholder(int paramInt, char paramChar)
  {
    return getPlaceholderCharacter() == paramChar;
  }
  
  private boolean isValidCharacter(int paramInt, char paramChar)
  {
    return getMaskCharacter(paramInt).isValidCharacter(paramChar);
  }
  
  private boolean isLiteral(int paramInt)
  {
    return getMaskCharacter(paramInt).isLiteral();
  }
  
  private int getMaxLength()
  {
    return maskChars.length;
  }
  
  private char getLiteral(int paramInt)
  {
    return getMaskCharacter(paramInt).getChar('\000');
  }
  
  private char getCharacter(int paramInt, char paramChar)
  {
    return getMaskCharacter(paramInt).getChar(paramChar);
  }
  
  private String stripLiteralChars(String paramString)
  {
    StringBuilder localStringBuilder = null;
    int i = 0;
    int j = 0;
    int k = paramString.length();
    while (j < k)
    {
      if (isLiteral(j))
      {
        if (localStringBuilder == null)
        {
          localStringBuilder = new StringBuilder();
          if (j > 0) {
            localStringBuilder.append(paramString.substring(0, j));
          }
          i = j + 1;
        }
        else if (i != j)
        {
          localStringBuilder.append(paramString.substring(i, j));
        }
        i = j + 1;
      }
      j++;
    }
    if (localStringBuilder == null) {
      return paramString;
    }
    if (i != paramString.length())
    {
      if (localStringBuilder == null) {
        return paramString.substring(i);
      }
      localStringBuilder.append(paramString.substring(i));
    }
    return localStringBuilder.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    try
    {
      updateInternalMask();
    }
    catch (ParseException localParseException) {}
  }
  
  boolean isNavigatable(int paramInt)
  {
    if (!getAllowsInvalid()) {
      return (paramInt < getMaxLength()) && (!isLiteral(paramInt));
    }
    return true;
  }
  
  boolean isValidEdit(DefaultFormatter.ReplaceHolder paramReplaceHolder)
  {
    if (!getAllowsInvalid())
    {
      String str = getReplaceString(offset, length, text);
      try
      {
        value = stringToValue(str, false);
        return true;
      }
      catch (ParseException localParseException)
      {
        return false;
      }
    }
    return true;
  }
  
  boolean canReplace(DefaultFormatter.ReplaceHolder paramReplaceHolder)
  {
    if (!getAllowsInvalid())
    {
      StringBuilder localStringBuilder = null;
      String str = text;
      int i = str != null ? str.length() : 0;
      if ((i == 0) && (length == 1) && (getFormattedTextField().getSelectionStart() != offset)) {
        while ((offset > 0) && (isLiteral(offset))) {
          offset -= 1;
        }
      }
      int j = Math.min(getMaxLength() - offset, Math.max(i, length));
      int k = 0;
      int m = 0;
      while (k < j)
      {
        if ((m < i) && (isValidCharacter(offset + k, str.charAt(m))))
        {
          char c = str.charAt(m);
          if ((c != getCharacter(offset + k, c)) && (localStringBuilder == null))
          {
            localStringBuilder = new StringBuilder();
            if (m > 0) {
              localStringBuilder.append(str.substring(0, m));
            }
          }
          if (localStringBuilder != null) {
            localStringBuilder.append(getCharacter(offset + k, c));
          }
          m++;
        }
        else if (isLiteral(offset + k))
        {
          if (localStringBuilder != null)
          {
            localStringBuilder.append(getLiteral(offset + k));
            if (m < i) {
              j = Math.min(j + 1, getMaxLength() - offset);
            }
          }
          else if (m > 0)
          {
            localStringBuilder = new StringBuilder(j);
            localStringBuilder.append(str.substring(0, m));
            localStringBuilder.append(getLiteral(offset + k));
            if (m < i) {
              j = Math.min(j + 1, getMaxLength() - offset);
            } else if (cursorPosition == -1) {
              cursorPosition = (offset + k);
            }
          }
          else
          {
            offset += 1;
            length -= 1;
            k--;
            j--;
          }
        }
        else if (m >= i)
        {
          if (localStringBuilder == null)
          {
            localStringBuilder = new StringBuilder();
            if (str != null) {
              localStringBuilder.append(str);
            }
          }
          localStringBuilder.append(getPlaceholderCharacter());
          if ((i > 0) && (cursorPosition == -1)) {
            cursorPosition = (offset + k);
          }
        }
        else
        {
          return false;
        }
        k++;
      }
      if (localStringBuilder != null) {
        text = localStringBuilder.toString();
      } else if ((str != null) && (offset + i > getMaxLength())) {
        text = str.substring(0, getMaxLength() - offset);
      }
      if ((getOverwriteMode()) && (text != null)) {
        length = text.length();
      }
    }
    return super.canReplace(paramReplaceHolder);
  }
  
  private class AlphaNumericCharacter
    extends MaskFormatter.MaskCharacter
  {
    private AlphaNumericCharacter()
    {
      super(null);
    }
    
    public boolean isValidCharacter(char paramChar)
    {
      return (Character.isLetterOrDigit(paramChar)) && (super.isValidCharacter(paramChar));
    }
  }
  
  private class CharCharacter
    extends MaskFormatter.MaskCharacter
  {
    private CharCharacter()
    {
      super(null);
    }
    
    public boolean isValidCharacter(char paramChar)
    {
      return (Character.isLetter(paramChar)) && (super.isValidCharacter(paramChar));
    }
  }
  
  private class DigitMaskCharacter
    extends MaskFormatter.MaskCharacter
  {
    private DigitMaskCharacter()
    {
      super(null);
    }
    
    public boolean isValidCharacter(char paramChar)
    {
      return (Character.isDigit(paramChar)) && (super.isValidCharacter(paramChar));
    }
  }
  
  private class HexCharacter
    extends MaskFormatter.MaskCharacter
  {
    private HexCharacter()
    {
      super(null);
    }
    
    public boolean isValidCharacter(char paramChar)
    {
      return ((paramChar == '0') || (paramChar == '1') || (paramChar == '2') || (paramChar == '3') || (paramChar == '4') || (paramChar == '5') || (paramChar == '6') || (paramChar == '7') || (paramChar == '8') || (paramChar == '9') || (paramChar == 'a') || (paramChar == 'A') || (paramChar == 'b') || (paramChar == 'B') || (paramChar == 'c') || (paramChar == 'C') || (paramChar == 'd') || (paramChar == 'D') || (paramChar == 'e') || (paramChar == 'E') || (paramChar == 'f') || (paramChar == 'F')) && (super.isValidCharacter(paramChar));
    }
    
    public char getChar(char paramChar)
    {
      if (Character.isDigit(paramChar)) {
        return paramChar;
      }
      return Character.toUpperCase(paramChar);
    }
  }
  
  private class LiteralCharacter
    extends MaskFormatter.MaskCharacter
  {
    private char fixedChar;
    
    public LiteralCharacter(char paramChar)
    {
      super(null);
      fixedChar = paramChar;
    }
    
    public boolean isLiteral()
    {
      return true;
    }
    
    public char getChar(char paramChar)
    {
      return fixedChar;
    }
  }
  
  private class LowerCaseCharacter
    extends MaskFormatter.MaskCharacter
  {
    private LowerCaseCharacter()
    {
      super(null);
    }
    
    public boolean isValidCharacter(char paramChar)
    {
      return (Character.isLetter(paramChar)) && (super.isValidCharacter(paramChar));
    }
    
    public char getChar(char paramChar)
    {
      return Character.toLowerCase(paramChar);
    }
  }
  
  private class MaskCharacter
  {
    private MaskCharacter() {}
    
    public boolean isLiteral()
    {
      return false;
    }
    
    public boolean isValidCharacter(char paramChar)
    {
      if (isLiteral()) {
        return getChar(paramChar) == paramChar;
      }
      paramChar = getChar(paramChar);
      String str = getValidCharacters();
      if ((str != null) && (str.indexOf(paramChar) == -1)) {
        return false;
      }
      str = getInvalidCharacters();
      return (str == null) || (str.indexOf(paramChar) == -1);
    }
    
    public char getChar(char paramChar)
    {
      return paramChar;
    }
    
    public void append(StringBuilder paramStringBuilder, String paramString1, int[] paramArrayOfInt, String paramString2)
      throws ParseException
    {
      int i = paramArrayOfInt[0] < paramString1.length() ? 1 : 0;
      char c = i != 0 ? paramString1.charAt(paramArrayOfInt[0]) : '\000';
      if (isLiteral())
      {
        paramStringBuilder.append(getChar(c));
        if (getValueContainsLiteralCharacters())
        {
          if ((i != 0) && (c != getChar(c))) {
            throw new ParseException("Invalid character: " + c, paramArrayOfInt[0]);
          }
          paramArrayOfInt[0] += 1;
        }
      }
      else if (paramArrayOfInt[0] >= paramString1.length())
      {
        if ((paramString2 != null) && (paramArrayOfInt[0] < paramString2.length())) {
          paramStringBuilder.append(paramString2.charAt(paramArrayOfInt[0]));
        } else {
          paramStringBuilder.append(getPlaceholderCharacter());
        }
        paramArrayOfInt[0] += 1;
      }
      else if (isValidCharacter(c))
      {
        paramStringBuilder.append(getChar(c));
        paramArrayOfInt[0] += 1;
      }
      else
      {
        throw new ParseException("Invalid character: " + c, paramArrayOfInt[0]);
      }
    }
  }
  
  private class UpperCaseCharacter
    extends MaskFormatter.MaskCharacter
  {
    private UpperCaseCharacter()
    {
      super(null);
    }
    
    public boolean isValidCharacter(char paramChar)
    {
      return (Character.isLetter(paramChar)) && (super.isValidCharacter(paramChar));
    }
    
    public char getChar(char paramChar)
    {
      return Character.toUpperCase(paramChar);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\MaskFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
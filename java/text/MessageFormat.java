package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Category;

public class MessageFormat
  extends Format
{
  private static final long serialVersionUID = 6479157306784022952L;
  private Locale locale;
  private String pattern = "";
  private static final int INITIAL_FORMATS = 10;
  private Format[] formats = new Format[10];
  private int[] offsets = new int[10];
  private int[] argumentNumbers = new int[10];
  private int maxOffset = -1;
  private static final int SEG_RAW = 0;
  private static final int SEG_INDEX = 1;
  private static final int SEG_TYPE = 2;
  private static final int SEG_MODIFIER = 3;
  private static final int TYPE_NULL = 0;
  private static final int TYPE_NUMBER = 1;
  private static final int TYPE_DATE = 2;
  private static final int TYPE_TIME = 3;
  private static final int TYPE_CHOICE = 4;
  private static final String[] TYPE_KEYWORDS = { "", "number", "date", "time", "choice" };
  private static final int MODIFIER_DEFAULT = 0;
  private static final int MODIFIER_CURRENCY = 1;
  private static final int MODIFIER_PERCENT = 2;
  private static final int MODIFIER_INTEGER = 3;
  private static final String[] NUMBER_MODIFIER_KEYWORDS = { "", "currency", "percent", "integer" };
  private static final int MODIFIER_SHORT = 1;
  private static final int MODIFIER_MEDIUM = 2;
  private static final int MODIFIER_LONG = 3;
  private static final int MODIFIER_FULL = 4;
  private static final String[] DATE_TIME_MODIFIER_KEYWORDS = { "", "short", "medium", "long", "full" };
  private static final int[] DATE_TIME_MODIFIERS = { 2, 3, 2, 1, 0 };
  
  public MessageFormat(String paramString)
  {
    locale = Locale.getDefault(Locale.Category.FORMAT);
    applyPattern(paramString);
  }
  
  public MessageFormat(String paramString, Locale paramLocale)
  {
    locale = paramLocale;
    applyPattern(paramString);
  }
  
  public void setLocale(Locale paramLocale)
  {
    locale = paramLocale;
  }
  
  public Locale getLocale()
  {
    return locale;
  }
  
  public void applyPattern(String paramString)
  {
    StringBuilder[] arrayOfStringBuilder = new StringBuilder[4];
    arrayOfStringBuilder[0] = new StringBuilder();
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    maxOffset = -1;
    for (int n = 0; n < paramString.length(); n++)
    {
      char c = paramString.charAt(n);
      if (i == 0)
      {
        if (c == '\'')
        {
          if ((n + 1 < paramString.length()) && (paramString.charAt(n + 1) == '\''))
          {
            arrayOfStringBuilder[i].append(c);
            n++;
          }
          else
          {
            k = k == 0 ? 1 : 0;
          }
        }
        else if ((c == '{') && (k == 0))
        {
          i = 1;
          if (arrayOfStringBuilder[1] == null) {
            arrayOfStringBuilder[1] = new StringBuilder();
          }
        }
        else
        {
          arrayOfStringBuilder[i].append(c);
        }
      }
      else if (k != 0)
      {
        arrayOfStringBuilder[i].append(c);
        if (c == '\'') {
          k = 0;
        }
      }
      else
      {
        switch (c)
        {
        case ',': 
          if (i < 3)
          {
            if (arrayOfStringBuilder[(++i)] == null) {
              arrayOfStringBuilder[i] = new StringBuilder();
            }
          }
          else {
            arrayOfStringBuilder[i].append(c);
          }
          break;
        case '{': 
          m++;
          arrayOfStringBuilder[i].append(c);
          break;
        case '}': 
          if (m == 0)
          {
            i = 0;
            makeFormat(n, j, arrayOfStringBuilder);
            j++;
            arrayOfStringBuilder[1] = null;
            arrayOfStringBuilder[2] = null;
            arrayOfStringBuilder[3] = null;
          }
          else
          {
            m--;
            arrayOfStringBuilder[i].append(c);
          }
          break;
        case ' ': 
          if ((i != 2) || (arrayOfStringBuilder[2].length() > 0)) {
            arrayOfStringBuilder[i].append(c);
          }
          break;
        case '\'': 
          k = 1;
        default: 
          arrayOfStringBuilder[i].append(c);
        }
      }
    }
    if ((m == 0) && (i != 0))
    {
      maxOffset = -1;
      throw new IllegalArgumentException("Unmatched braces in the pattern.");
    }
    pattern = arrayOfStringBuilder[0].toString();
  }
  
  public String toPattern()
  {
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    for (int j = 0; j <= maxOffset; j++)
    {
      copyAndFixQuotes(pattern, i, offsets[j], localStringBuilder);
      i = offsets[j];
      localStringBuilder.append('{').append(argumentNumbers[j]);
      Format localFormat = formats[j];
      if (localFormat != null) {
        if ((localFormat instanceof NumberFormat))
        {
          if (localFormat.equals(NumberFormat.getInstance(locale))) {
            localStringBuilder.append(",number");
          } else if (localFormat.equals(NumberFormat.getCurrencyInstance(locale))) {
            localStringBuilder.append(",number,currency");
          } else if (localFormat.equals(NumberFormat.getPercentInstance(locale))) {
            localStringBuilder.append(",number,percent");
          } else if (localFormat.equals(NumberFormat.getIntegerInstance(locale))) {
            localStringBuilder.append(",number,integer");
          } else if ((localFormat instanceof DecimalFormat)) {
            localStringBuilder.append(",number,").append(((DecimalFormat)localFormat).toPattern());
          } else if ((localFormat instanceof ChoiceFormat)) {
            localStringBuilder.append(",choice,").append(((ChoiceFormat)localFormat).toPattern());
          }
        }
        else if ((localFormat instanceof DateFormat))
        {
          for (int k = 0; k < DATE_TIME_MODIFIERS.length; k++)
          {
            DateFormat localDateFormat = DateFormat.getDateInstance(DATE_TIME_MODIFIERS[k], locale);
            if (localFormat.equals(localDateFormat))
            {
              localStringBuilder.append(",date");
              break;
            }
            localDateFormat = DateFormat.getTimeInstance(DATE_TIME_MODIFIERS[k], locale);
            if (localFormat.equals(localDateFormat))
            {
              localStringBuilder.append(",time");
              break;
            }
          }
          if (k >= DATE_TIME_MODIFIERS.length)
          {
            if ((localFormat instanceof SimpleDateFormat)) {
              localStringBuilder.append(",date,").append(((SimpleDateFormat)localFormat).toPattern());
            }
          }
          else if (k != 0) {
            localStringBuilder.append(',').append(DATE_TIME_MODIFIER_KEYWORDS[k]);
          }
        }
      }
      localStringBuilder.append('}');
    }
    copyAndFixQuotes(pattern, i, pattern.length(), localStringBuilder);
    return localStringBuilder.toString();
  }
  
  public void setFormatsByArgumentIndex(Format[] paramArrayOfFormat)
  {
    for (int i = 0; i <= maxOffset; i++)
    {
      int j = argumentNumbers[i];
      if (j < paramArrayOfFormat.length) {
        formats[i] = paramArrayOfFormat[j];
      }
    }
  }
  
  public void setFormats(Format[] paramArrayOfFormat)
  {
    int i = paramArrayOfFormat.length;
    if (i > maxOffset + 1) {
      i = maxOffset + 1;
    }
    for (int j = 0; j < i; j++) {
      formats[j] = paramArrayOfFormat[j];
    }
  }
  
  public void setFormatByArgumentIndex(int paramInt, Format paramFormat)
  {
    for (int i = 0; i <= maxOffset; i++) {
      if (argumentNumbers[i] == paramInt) {
        formats[i] = paramFormat;
      }
    }
  }
  
  public void setFormat(int paramInt, Format paramFormat)
  {
    formats[paramInt] = paramFormat;
  }
  
  public Format[] getFormatsByArgumentIndex()
  {
    int i = -1;
    for (int j = 0; j <= maxOffset; j++) {
      if (argumentNumbers[j] > i) {
        i = argumentNumbers[j];
      }
    }
    Format[] arrayOfFormat = new Format[i + 1];
    for (int k = 0; k <= maxOffset; k++) {
      arrayOfFormat[argumentNumbers[k]] = formats[k];
    }
    return arrayOfFormat;
  }
  
  public Format[] getFormats()
  {
    Format[] arrayOfFormat = new Format[maxOffset + 1];
    System.arraycopy(formats, 0, arrayOfFormat, 0, maxOffset + 1);
    return arrayOfFormat;
  }
  
  public final StringBuffer format(Object[] paramArrayOfObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    return subformat(paramArrayOfObject, paramStringBuffer, paramFieldPosition, null);
  }
  
  public static String format(String paramString, Object... paramVarArgs)
  {
    MessageFormat localMessageFormat = new MessageFormat(paramString);
    return localMessageFormat.format(paramVarArgs);
  }
  
  public final StringBuffer format(Object paramObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    return subformat((Object[])paramObject, paramStringBuffer, paramFieldPosition, null);
  }
  
  public AttributedCharacterIterator formatToCharacterIterator(Object paramObject)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    ArrayList localArrayList = new ArrayList();
    if (paramObject == null) {
      throw new NullPointerException("formatToCharacterIterator must be passed non-null object");
    }
    subformat((Object[])paramObject, localStringBuffer, null, localArrayList);
    if (localArrayList.size() == 0) {
      return createAttributedCharacterIterator("");
    }
    return createAttributedCharacterIterator((AttributedCharacterIterator[])localArrayList.toArray(new AttributedCharacterIterator[localArrayList.size()]));
  }
  
  public Object[] parse(String paramString, ParsePosition paramParsePosition)
  {
    if (paramString == null)
    {
      Object[] arrayOfObject1 = new Object[0];
      return arrayOfObject1;
    }
    int i = -1;
    for (int j = 0; j <= maxOffset; j++) {
      if (argumentNumbers[j] > i) {
        i = argumentNumbers[j];
      }
    }
    Object[] arrayOfObject2 = new Object[i + 1];
    int k = 0;
    int m = index;
    ParsePosition localParsePosition = new ParsePosition(0);
    for (int n = 0; n <= maxOffset; n++)
    {
      int i1 = offsets[n] - k;
      if ((i1 == 0) || (pattern.regionMatches(k, paramString, m, i1)))
      {
        m += i1;
        k += i1;
      }
      else
      {
        errorIndex = m;
        return null;
      }
      if (formats[n] == null)
      {
        int i2 = n != maxOffset ? offsets[(n + 1)] : pattern.length();
        int i3;
        if (k >= i2) {
          i3 = paramString.length();
        } else {
          i3 = paramString.indexOf(pattern.substring(k, i2), m);
        }
        if (i3 < 0)
        {
          errorIndex = m;
          return null;
        }
        String str = paramString.substring(m, i3);
        if (!str.equals("{" + argumentNumbers[n] + "}")) {
          arrayOfObject2[argumentNumbers[n]] = paramString.substring(m, i3);
        }
        m = i3;
      }
      else
      {
        index = m;
        arrayOfObject2[argumentNumbers[n]] = formats[n].parseObject(paramString, localParsePosition);
        if (index == m)
        {
          errorIndex = m;
          return null;
        }
        m = index;
      }
    }
    n = pattern.length() - k;
    if ((n == 0) || (pattern.regionMatches(k, paramString, m, n)))
    {
      index = (m + n);
    }
    else
    {
      errorIndex = m;
      return null;
    }
    return arrayOfObject2;
  }
  
  public Object[] parse(String paramString)
    throws ParseException
  {
    ParsePosition localParsePosition = new ParsePosition(0);
    Object[] arrayOfObject = parse(paramString, localParsePosition);
    if (index == 0) {
      throw new ParseException("MessageFormat parse error!", errorIndex);
    }
    return arrayOfObject;
  }
  
  public Object parseObject(String paramString, ParsePosition paramParsePosition)
  {
    return parse(paramString, paramParsePosition);
  }
  
  public Object clone()
  {
    MessageFormat localMessageFormat = (MessageFormat)super.clone();
    formats = ((Format[])formats.clone());
    for (int i = 0; i < formats.length; i++) {
      if (formats[i] != null) {
        formats[i] = ((Format)formats[i].clone());
      }
    }
    offsets = ((int[])offsets.clone());
    argumentNumbers = ((int[])argumentNumbers.clone());
    return localMessageFormat;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    MessageFormat localMessageFormat = (MessageFormat)paramObject;
    return (maxOffset == maxOffset) && (pattern.equals(pattern)) && (((locale != null) && (locale.equals(locale))) || ((locale == null) && (locale == null) && (Arrays.equals(offsets, offsets)) && (Arrays.equals(argumentNumbers, argumentNumbers)) && (Arrays.equals(formats, formats))));
  }
  
  public int hashCode()
  {
    return pattern.hashCode();
  }
  
  private StringBuffer subformat(Object[] paramArrayOfObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition, List<AttributedCharacterIterator> paramList)
  {
    int i = 0;
    int j = paramStringBuffer.length();
    for (int k = 0; k <= maxOffset; k++)
    {
      paramStringBuffer.append(pattern.substring(i, offsets[k]));
      i = offsets[k];
      int m = argumentNumbers[k];
      if ((paramArrayOfObject == null) || (m >= paramArrayOfObject.length))
      {
        paramStringBuffer.append('{').append(m).append('}');
      }
      else
      {
        Object localObject1 = paramArrayOfObject[m];
        String str = null;
        Object localObject2 = null;
        if (localObject1 == null)
        {
          str = "null";
        }
        else if (formats[k] != null)
        {
          localObject2 = formats[k];
          if ((localObject2 instanceof ChoiceFormat))
          {
            str = formats[k].format(localObject1);
            if (str.indexOf('{') >= 0)
            {
              localObject2 = new MessageFormat(str, locale);
              localObject1 = paramArrayOfObject;
              str = null;
            }
          }
        }
        else if ((localObject1 instanceof Number))
        {
          localObject2 = NumberFormat.getInstance(locale);
        }
        else if ((localObject1 instanceof Date))
        {
          localObject2 = DateFormat.getDateTimeInstance(3, 3, locale);
        }
        else if ((localObject1 instanceof String))
        {
          str = (String)localObject1;
        }
        else
        {
          str = localObject1.toString();
          if (str == null) {
            str = "null";
          }
        }
        if (paramList != null)
        {
          if (j != paramStringBuffer.length())
          {
            paramList.add(createAttributedCharacterIterator(paramStringBuffer.substring(j)));
            j = paramStringBuffer.length();
          }
          if (localObject2 != null)
          {
            AttributedCharacterIterator localAttributedCharacterIterator = ((Format)localObject2).formatToCharacterIterator(localObject1);
            append(paramStringBuffer, localAttributedCharacterIterator);
            if (j != paramStringBuffer.length())
            {
              paramList.add(createAttributedCharacterIterator(localAttributedCharacterIterator, Field.ARGUMENT, Integer.valueOf(m)));
              j = paramStringBuffer.length();
            }
            str = null;
          }
          if ((str != null) && (str.length() > 0))
          {
            paramStringBuffer.append(str);
            paramList.add(createAttributedCharacterIterator(str, Field.ARGUMENT, Integer.valueOf(m)));
            j = paramStringBuffer.length();
          }
        }
        else
        {
          if (localObject2 != null) {
            str = ((Format)localObject2).format(localObject1);
          }
          j = paramStringBuffer.length();
          paramStringBuffer.append(str);
          if ((k == 0) && (paramFieldPosition != null) && (Field.ARGUMENT.equals(paramFieldPosition.getFieldAttribute())))
          {
            paramFieldPosition.setBeginIndex(j);
            paramFieldPosition.setEndIndex(paramStringBuffer.length());
          }
          j = paramStringBuffer.length();
        }
      }
    }
    paramStringBuffer.append(pattern.substring(i, pattern.length()));
    if ((paramList != null) && (j != paramStringBuffer.length())) {
      paramList.add(createAttributedCharacterIterator(paramStringBuffer.substring(j)));
    }
    return paramStringBuffer;
  }
  
  private void append(StringBuffer paramStringBuffer, CharacterIterator paramCharacterIterator)
  {
    if (paramCharacterIterator.first() != 65535)
    {
      paramStringBuffer.append(paramCharacterIterator.first());
      char c;
      while ((c = paramCharacterIterator.next()) != 65535) {
        paramStringBuffer.append(c);
      }
    }
  }
  
  private void makeFormat(int paramInt1, int paramInt2, StringBuilder[] paramArrayOfStringBuilder)
  {
    String[] arrayOfString = new String[paramArrayOfStringBuilder.length];
    for (int i = 0; i < paramArrayOfStringBuilder.length; i++)
    {
      StringBuilder localStringBuilder = paramArrayOfStringBuilder[i];
      arrayOfString[i] = (localStringBuilder != null ? localStringBuilder.toString() : "");
    }
    try
    {
      i = Integer.parseInt(arrayOfString[1]);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new IllegalArgumentException("can't parse argument number: " + arrayOfString[1], localNumberFormatException);
    }
    if (i < 0) {
      throw new IllegalArgumentException("negative argument number: " + i);
    }
    if (paramInt2 >= formats.length)
    {
      j = formats.length * 2;
      localObject = new Format[j];
      int[] arrayOfInt1 = new int[j];
      int[] arrayOfInt2 = new int[j];
      System.arraycopy(formats, 0, localObject, 0, maxOffset + 1);
      System.arraycopy(offsets, 0, arrayOfInt1, 0, maxOffset + 1);
      System.arraycopy(argumentNumbers, 0, arrayOfInt2, 0, maxOffset + 1);
      formats = ((Format[])localObject);
      offsets = arrayOfInt1;
      argumentNumbers = arrayOfInt2;
    }
    int j = maxOffset;
    maxOffset = paramInt2;
    offsets[paramInt2] = arrayOfString[0].length();
    argumentNumbers[paramInt2] = i;
    Object localObject = null;
    if (arrayOfString[2].length() != 0)
    {
      int k = findKeyword(arrayOfString[2], TYPE_KEYWORDS);
      switch (k)
      {
      case 0: 
        break;
      case 1: 
        switch (findKeyword(arrayOfString[3], NUMBER_MODIFIER_KEYWORDS))
        {
        case 0: 
          localObject = NumberFormat.getInstance(locale);
          break;
        case 1: 
          localObject = NumberFormat.getCurrencyInstance(locale);
          break;
        case 2: 
          localObject = NumberFormat.getPercentInstance(locale);
          break;
        case 3: 
          localObject = NumberFormat.getIntegerInstance(locale);
          break;
        default: 
          try
          {
            localObject = new DecimalFormat(arrayOfString[3], DecimalFormatSymbols.getInstance(locale));
          }
          catch (IllegalArgumentException localIllegalArgumentException1)
          {
            maxOffset = j;
            throw localIllegalArgumentException1;
          }
        }
        break;
      case 2: 
      case 3: 
        int m = findKeyword(arrayOfString[3], DATE_TIME_MODIFIER_KEYWORDS);
        if ((m >= 0) && (m < DATE_TIME_MODIFIER_KEYWORDS.length))
        {
          if (k == 2) {
            localObject = DateFormat.getDateInstance(DATE_TIME_MODIFIERS[m], locale);
          } else {
            localObject = DateFormat.getTimeInstance(DATE_TIME_MODIFIERS[m], locale);
          }
        }
        else {
          try
          {
            localObject = new SimpleDateFormat(arrayOfString[3], locale);
          }
          catch (IllegalArgumentException localIllegalArgumentException2)
          {
            maxOffset = j;
            throw localIllegalArgumentException2;
          }
        }
        break;
      case 4: 
        try
        {
          localObject = new ChoiceFormat(arrayOfString[3]);
        }
        catch (Exception localException)
        {
          maxOffset = j;
          throw new IllegalArgumentException("Choice Pattern incorrect: " + arrayOfString[3], localException);
        }
      default: 
        maxOffset = j;
        throw new IllegalArgumentException("unknown format type: " + arrayOfString[2]);
      }
    }
    formats[paramInt2] = localObject;
  }
  
  private static final int findKeyword(String paramString, String[] paramArrayOfString)
  {
    for (int i = 0; i < paramArrayOfString.length; i++) {
      if (paramString.equals(paramArrayOfString[i])) {
        return i;
      }
    }
    String str = paramString.trim().toLowerCase(Locale.ROOT);
    if (str != paramString) {
      for (int j = 0; j < paramArrayOfString.length; j++) {
        if (str.equals(paramArrayOfString[j])) {
          return j;
        }
      }
    }
    return -1;
  }
  
  private static final void copyAndFixQuotes(String paramString, int paramInt1, int paramInt2, StringBuilder paramStringBuilder)
  {
    int i = 0;
    for (int j = paramInt1; j < paramInt2; j++)
    {
      char c = paramString.charAt(j);
      if (c == '{')
      {
        if (i == 0)
        {
          paramStringBuilder.append('\'');
          i = 1;
        }
        paramStringBuilder.append(c);
      }
      else if (c == '\'')
      {
        paramStringBuilder.append("''");
      }
      else
      {
        if (i != 0)
        {
          paramStringBuilder.append('\'');
          i = 0;
        }
        paramStringBuilder.append(c);
      }
    }
    if (i != 0) {
      paramStringBuilder.append('\'');
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = (maxOffset >= -1) && (formats.length > maxOffset) && (offsets.length > maxOffset) && (argumentNumbers.length > maxOffset) ? 1 : 0;
    if (i != 0)
    {
      int j = pattern.length() + 1;
      for (int k = maxOffset; k >= 0; k--)
      {
        if ((offsets[k] < 0) || (offsets[k] > j))
        {
          i = 0;
          break;
        }
        j = offsets[k];
      }
    }
    if (i == 0) {
      throw new InvalidObjectException("Could not reconstruct MessageFormat from corrupt stream.");
    }
  }
  
  public static class Field
    extends Format.Field
  {
    private static final long serialVersionUID = 7899943957617360810L;
    public static final Field ARGUMENT = new Field("message argument field");
    
    protected Field(String paramString)
    {
      super();
    }
    
    protected Object readResolve()
      throws InvalidObjectException
    {
      if (getClass() != Field.class) {
        throw new InvalidObjectException("subclass didn't correctly implement readResolve");
      }
      return ARGUMENT;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\MessageFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
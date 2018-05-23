package java.time.format;

import java.time.DateTimeException;

public class DateTimeParseException
  extends DateTimeException
{
  private static final long serialVersionUID = 4304633501674722597L;
  private final String parsedString;
  private final int errorIndex;
  
  public DateTimeParseException(String paramString, CharSequence paramCharSequence, int paramInt)
  {
    super(paramString);
    parsedString = paramCharSequence.toString();
    errorIndex = paramInt;
  }
  
  public DateTimeParseException(String paramString, CharSequence paramCharSequence, int paramInt, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    parsedString = paramCharSequence.toString();
    errorIndex = paramInt;
  }
  
  public String getParsedString()
  {
    return parsedString;
  }
  
  public int getErrorIndex()
  {
    return errorIndex;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\format\DateTimeParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package java.util;

public final class StringJoiner
{
  private final String prefix;
  private final String delimiter;
  private final String suffix;
  private StringBuilder value;
  private String emptyValue;
  
  public StringJoiner(CharSequence paramCharSequence)
  {
    this(paramCharSequence, "", "");
  }
  
  public StringJoiner(CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3)
  {
    Objects.requireNonNull(paramCharSequence2, "The prefix must not be null");
    Objects.requireNonNull(paramCharSequence1, "The delimiter must not be null");
    Objects.requireNonNull(paramCharSequence3, "The suffix must not be null");
    prefix = paramCharSequence2.toString();
    delimiter = paramCharSequence1.toString();
    suffix = paramCharSequence3.toString();
    emptyValue = (prefix + suffix);
  }
  
  public StringJoiner setEmptyValue(CharSequence paramCharSequence)
  {
    emptyValue = ((CharSequence)Objects.requireNonNull(paramCharSequence, "The empty value must not be null")).toString();
    return this;
  }
  
  public String toString()
  {
    if (value == null) {
      return emptyValue;
    }
    if (suffix.equals("")) {
      return value.toString();
    }
    int i = value.length();
    String str = suffix;
    value.setLength(i);
    return str;
  }
  
  public StringJoiner add(CharSequence paramCharSequence)
  {
    prepareBuilder().append(paramCharSequence);
    return this;
  }
  
  public StringJoiner merge(StringJoiner paramStringJoiner)
  {
    Objects.requireNonNull(paramStringJoiner);
    if (value != null)
    {
      int i = value.length();
      StringBuilder localStringBuilder = prepareBuilder();
      localStringBuilder.append(value, prefix.length(), i);
    }
    return this;
  }
  
  private StringBuilder prepareBuilder()
  {
    if (value != null) {
      value.append(delimiter);
    } else {
      value = new StringBuilder().append(prefix);
    }
    return value;
  }
  
  public int length()
  {
    return value != null ? value.length() + suffix.length() : emptyValue.length();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\StringJoiner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
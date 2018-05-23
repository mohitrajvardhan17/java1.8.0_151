package javax.naming;

import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Vector;

class NameImpl
{
  private static final byte LEFT_TO_RIGHT = 1;
  private static final byte RIGHT_TO_LEFT = 2;
  private static final byte FLAT = 0;
  private Vector<String> components;
  private byte syntaxDirection = 1;
  private String syntaxSeparator = "/";
  private String syntaxSeparator2 = null;
  private boolean syntaxCaseInsensitive = false;
  private boolean syntaxTrimBlanks = false;
  private String syntaxEscape = "\\";
  private String syntaxBeginQuote1 = "\"";
  private String syntaxEndQuote1 = "\"";
  private String syntaxBeginQuote2 = "'";
  private String syntaxEndQuote2 = "'";
  private String syntaxAvaSeparator = null;
  private String syntaxTypevalSeparator = null;
  private static final int STYLE_NONE = 0;
  private static final int STYLE_QUOTE1 = 1;
  private static final int STYLE_QUOTE2 = 2;
  private static final int STYLE_ESCAPE = 3;
  private int escapingStyle = 0;
  
  private final boolean isA(String paramString1, int paramInt, String paramString2)
  {
    return (paramString2 != null) && (paramString1.startsWith(paramString2, paramInt));
  }
  
  private final boolean isMeta(String paramString, int paramInt)
  {
    return (isA(paramString, paramInt, syntaxEscape)) || (isA(paramString, paramInt, syntaxBeginQuote1)) || (isA(paramString, paramInt, syntaxBeginQuote2)) || (isSeparator(paramString, paramInt));
  }
  
  private final boolean isSeparator(String paramString, int paramInt)
  {
    return (isA(paramString, paramInt, syntaxSeparator)) || (isA(paramString, paramInt, syntaxSeparator2));
  }
  
  private final int skipSeparator(String paramString, int paramInt)
  {
    if (isA(paramString, paramInt, syntaxSeparator)) {
      paramInt += syntaxSeparator.length();
    } else if (isA(paramString, paramInt, syntaxSeparator2)) {
      paramInt += syntaxSeparator2.length();
    }
    return paramInt;
  }
  
  private final int extractComp(String paramString, int paramInt1, int paramInt2, Vector<String> paramVector)
    throws InvalidNameException
  {
    int i = 1;
    boolean bool = false;
    StringBuffer localStringBuffer = new StringBuffer(paramInt2);
    while (paramInt1 < paramInt2)
    {
      String str1;
      String str2;
      if ((i != 0) && (((bool = isA(paramString, paramInt1, syntaxBeginQuote1))) || (isA(paramString, paramInt1, syntaxBeginQuote2))))
      {
        str1 = bool ? syntaxBeginQuote1 : syntaxBeginQuote2;
        str2 = bool ? syntaxEndQuote1 : syntaxEndQuote2;
        if (escapingStyle == 0) {
          escapingStyle = (bool ? 1 : 2);
        }
        paramInt1 += str1.length();
        while ((paramInt1 < paramInt2) && (!paramString.startsWith(str2, paramInt1)))
        {
          if ((isA(paramString, paramInt1, syntaxEscape)) && (isA(paramString, paramInt1 + syntaxEscape.length(), str2))) {
            paramInt1 += syntaxEscape.length();
          }
          localStringBuffer.append(paramString.charAt(paramInt1));
          paramInt1++;
        }
        if (paramInt1 >= paramInt2) {
          throw new InvalidNameException(paramString + ": no close quote");
        }
        paramInt1 += str2.length();
        if ((paramInt1 == paramInt2) || (isSeparator(paramString, paramInt1))) {
          break;
        }
        throw new InvalidNameException(paramString + ": close quote appears before end of component");
      }
      if (isSeparator(paramString, paramInt1)) {
        break;
      }
      if (isA(paramString, paramInt1, syntaxEscape))
      {
        if (isMeta(paramString, paramInt1 + syntaxEscape.length()))
        {
          paramInt1 += syntaxEscape.length();
          if (escapingStyle == 0) {
            escapingStyle = 3;
          }
        }
        else if (paramInt1 + syntaxEscape.length() >= paramInt2)
        {
          throw new InvalidNameException(paramString + ": unescaped " + syntaxEscape + " at end of component");
        }
      }
      else if ((isA(paramString, paramInt1, syntaxTypevalSeparator)) && (((bool = isA(paramString, paramInt1 + syntaxTypevalSeparator.length(), syntaxBeginQuote1))) || (isA(paramString, paramInt1 + syntaxTypevalSeparator.length(), syntaxBeginQuote2))))
      {
        str1 = bool ? syntaxBeginQuote1 : syntaxBeginQuote2;
        str2 = bool ? syntaxEndQuote1 : syntaxEndQuote2;
        paramInt1 += syntaxTypevalSeparator.length();
        localStringBuffer.append(syntaxTypevalSeparator + str1);
        paramInt1 += str1.length();
        while ((paramInt1 < paramInt2) && (!paramString.startsWith(str2, paramInt1)))
        {
          if ((isA(paramString, paramInt1, syntaxEscape)) && (isA(paramString, paramInt1 + syntaxEscape.length(), str2))) {
            paramInt1 += syntaxEscape.length();
          }
          localStringBuffer.append(paramString.charAt(paramInt1));
          paramInt1++;
        }
        if (paramInt1 >= paramInt2) {
          throw new InvalidNameException(paramString + ": typeval no close quote");
        }
        paramInt1 += str2.length();
        localStringBuffer.append(str2);
        if ((paramInt1 == paramInt2) || (isSeparator(paramString, paramInt1))) {
          break;
        }
        throw new InvalidNameException(paramString.substring(paramInt1) + ": typeval close quote appears before end of component");
      }
      localStringBuffer.append(paramString.charAt(paramInt1++));
      i = 0;
    }
    if (syntaxDirection == 2) {
      paramVector.insertElementAt(localStringBuffer.toString(), 0);
    } else {
      paramVector.addElement(localStringBuffer.toString());
    }
    return paramInt1;
  }
  
  private static boolean getBoolean(Properties paramProperties, String paramString)
  {
    return toBoolean(paramProperties.getProperty(paramString));
  }
  
  private static boolean toBoolean(String paramString)
  {
    return (paramString != null) && (paramString.toLowerCase(Locale.ENGLISH).equals("true"));
  }
  
  private final void recordNamingConvention(Properties paramProperties)
  {
    String str = paramProperties.getProperty("jndi.syntax.direction", "flat");
    if (str.equals("left_to_right")) {
      syntaxDirection = 1;
    } else if (str.equals("right_to_left")) {
      syntaxDirection = 2;
    } else if (str.equals("flat")) {
      syntaxDirection = 0;
    } else {
      throw new IllegalArgumentException(str + "is not a valid value for the jndi.syntax.direction property");
    }
    if (syntaxDirection != 0)
    {
      syntaxSeparator = paramProperties.getProperty("jndi.syntax.separator");
      syntaxSeparator2 = paramProperties.getProperty("jndi.syntax.separator2");
      if (syntaxSeparator == null) {
        throw new IllegalArgumentException("jndi.syntax.separator property required for non-flat syntax");
      }
    }
    else
    {
      syntaxSeparator = null;
    }
    syntaxEscape = paramProperties.getProperty("jndi.syntax.escape");
    syntaxCaseInsensitive = getBoolean(paramProperties, "jndi.syntax.ignorecase");
    syntaxTrimBlanks = getBoolean(paramProperties, "jndi.syntax.trimblanks");
    syntaxBeginQuote1 = paramProperties.getProperty("jndi.syntax.beginquote");
    syntaxEndQuote1 = paramProperties.getProperty("jndi.syntax.endquote");
    if ((syntaxEndQuote1 == null) && (syntaxBeginQuote1 != null)) {
      syntaxEndQuote1 = syntaxBeginQuote1;
    } else if ((syntaxBeginQuote1 == null) && (syntaxEndQuote1 != null)) {
      syntaxBeginQuote1 = syntaxEndQuote1;
    }
    syntaxBeginQuote2 = paramProperties.getProperty("jndi.syntax.beginquote2");
    syntaxEndQuote2 = paramProperties.getProperty("jndi.syntax.endquote2");
    if ((syntaxEndQuote2 == null) && (syntaxBeginQuote2 != null)) {
      syntaxEndQuote2 = syntaxBeginQuote2;
    } else if ((syntaxBeginQuote2 == null) && (syntaxEndQuote2 != null)) {
      syntaxBeginQuote2 = syntaxEndQuote2;
    }
    syntaxAvaSeparator = paramProperties.getProperty("jndi.syntax.separator.ava");
    syntaxTypevalSeparator = paramProperties.getProperty("jndi.syntax.separator.typeval");
  }
  
  NameImpl(Properties paramProperties)
  {
    if (paramProperties != null) {
      recordNamingConvention(paramProperties);
    }
    components = new Vector();
  }
  
  NameImpl(Properties paramProperties, String paramString)
    throws InvalidNameException
  {
    this(paramProperties);
    int i = syntaxDirection == 2 ? 1 : 0;
    int j = 1;
    int k = paramString.length();
    int m = 0;
    while (m < k)
    {
      m = extractComp(paramString, m, k, components);
      String str = i != 0 ? (String)components.firstElement() : (String)components.lastElement();
      if (str.length() >= 1) {
        j = 0;
      }
      if (m < k)
      {
        m = skipSeparator(paramString, m);
        if ((m == k) && (j == 0)) {
          if (i != 0) {
            components.insertElementAt("", 0);
          } else {
            components.addElement("");
          }
        }
      }
    }
  }
  
  NameImpl(Properties paramProperties, Enumeration<String> paramEnumeration)
  {
    this(paramProperties);
    while (paramEnumeration.hasMoreElements()) {
      components.addElement(paramEnumeration.nextElement());
    }
  }
  
  private final String stringifyComp(String paramString)
  {
    int i = paramString.length();
    int j = 0;
    int k = 0;
    String str1 = null;
    String str2 = null;
    StringBuffer localStringBuffer = new StringBuffer(i);
    if ((syntaxSeparator != null) && (paramString.indexOf(syntaxSeparator) >= 0)) {
      if (syntaxBeginQuote1 != null)
      {
        str1 = syntaxBeginQuote1;
        str2 = syntaxEndQuote1;
      }
      else if (syntaxBeginQuote2 != null)
      {
        str1 = syntaxBeginQuote2;
        str2 = syntaxEndQuote2;
      }
      else if (syntaxEscape != null)
      {
        j = 1;
      }
    }
    if ((syntaxSeparator2 != null) && (paramString.indexOf(syntaxSeparator2) >= 0)) {
      if (syntaxBeginQuote1 != null)
      {
        if (str1 == null)
        {
          str1 = syntaxBeginQuote1;
          str2 = syntaxEndQuote1;
        }
      }
      else if (syntaxBeginQuote2 != null)
      {
        if (str1 == null)
        {
          str1 = syntaxBeginQuote2;
          str2 = syntaxEndQuote2;
        }
      }
      else if (syntaxEscape != null) {
        k = 1;
      }
    }
    int m;
    if (str1 != null)
    {
      localStringBuffer = localStringBuffer.append(str1);
      m = 0;
      while (m < i) {
        if (paramString.startsWith(str2, m))
        {
          localStringBuffer.append(syntaxEscape).append(str2);
          m += str2.length();
        }
        else
        {
          localStringBuffer.append(paramString.charAt(m++));
        }
      }
      localStringBuffer.append(str2);
    }
    else
    {
      m = 1;
      int n = 0;
      while (n < i)
      {
        if ((m != 0) && (isA(paramString, n, syntaxBeginQuote1)))
        {
          localStringBuffer.append(syntaxEscape).append(syntaxBeginQuote1);
          n += syntaxBeginQuote1.length();
        }
        else if ((m != 0) && (isA(paramString, n, syntaxBeginQuote2)))
        {
          localStringBuffer.append(syntaxEscape).append(syntaxBeginQuote2);
          n += syntaxBeginQuote2.length();
        }
        else if (isA(paramString, n, syntaxEscape))
        {
          if (n + syntaxEscape.length() >= i) {
            localStringBuffer.append(syntaxEscape);
          } else if (isMeta(paramString, n + syntaxEscape.length())) {
            localStringBuffer.append(syntaxEscape);
          }
          localStringBuffer.append(syntaxEscape);
          n += syntaxEscape.length();
        }
        else if ((j != 0) && (paramString.startsWith(syntaxSeparator, n)))
        {
          localStringBuffer.append(syntaxEscape).append(syntaxSeparator);
          n += syntaxSeparator.length();
        }
        else if ((k != 0) && (paramString.startsWith(syntaxSeparator2, n)))
        {
          localStringBuffer.append(syntaxEscape).append(syntaxSeparator2);
          n += syntaxSeparator2.length();
        }
        else
        {
          localStringBuffer.append(paramString.charAt(n++));
        }
        m = 0;
      }
    }
    return localStringBuffer.toString();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 1;
    int j = components.size();
    for (int k = 0; k < j; k++)
    {
      String str;
      if (syntaxDirection == 2) {
        str = stringifyComp((String)components.elementAt(j - 1 - k));
      } else {
        str = stringifyComp((String)components.elementAt(k));
      }
      if ((k != 0) && (syntaxSeparator != null)) {
        localStringBuffer.append(syntaxSeparator);
      }
      if (str.length() >= 1) {
        i = 0;
      }
      localStringBuffer = localStringBuffer.append(str);
    }
    if ((i != 0) && (j >= 1) && (syntaxSeparator != null)) {
      localStringBuffer = localStringBuffer.append(syntaxSeparator);
    }
    return localStringBuffer.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof NameImpl)))
    {
      NameImpl localNameImpl = (NameImpl)paramObject;
      if (localNameImpl.size() == size())
      {
        Enumeration localEnumeration1 = getAll();
        Enumeration localEnumeration2 = localNameImpl.getAll();
        while (localEnumeration1.hasMoreElements())
        {
          String str1 = (String)localEnumeration1.nextElement();
          String str2 = (String)localEnumeration2.nextElement();
          if (syntaxTrimBlanks)
          {
            str1 = str1.trim();
            str2 = str2.trim();
          }
          if (syntaxCaseInsensitive)
          {
            if (!str1.equalsIgnoreCase(str2)) {
              return false;
            }
          }
          else if (!str1.equals(str2)) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  public int compareTo(NameImpl paramNameImpl)
  {
    if (this == paramNameImpl) {
      return 0;
    }
    int i = size();
    int j = paramNameImpl.size();
    int k = Math.min(i, j);
    int m = 0;
    int n = 0;
    while (k-- != 0)
    {
      String str1 = get(m++);
      String str2 = paramNameImpl.get(n++);
      if (syntaxTrimBlanks)
      {
        str1 = str1.trim();
        str2 = str2.trim();
      }
      int i1;
      if (syntaxCaseInsensitive) {
        i1 = str1.compareToIgnoreCase(str2);
      } else {
        i1 = str1.compareTo(str2);
      }
      if (i1 != 0) {
        return i1;
      }
    }
    return i - j;
  }
  
  public int size()
  {
    return components.size();
  }
  
  public Enumeration<String> getAll()
  {
    return components.elements();
  }
  
  public String get(int paramInt)
  {
    return (String)components.elementAt(paramInt);
  }
  
  public Enumeration<String> getPrefix(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > size())) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    return new NameImplEnumerator(components, 0, paramInt);
  }
  
  public Enumeration<String> getSuffix(int paramInt)
  {
    int i = size();
    if ((paramInt < 0) || (paramInt > i)) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    return new NameImplEnumerator(components, paramInt, i);
  }
  
  public boolean isEmpty()
  {
    return components.isEmpty();
  }
  
  public boolean startsWith(int paramInt, Enumeration<String> paramEnumeration)
  {
    if ((paramInt < 0) || (paramInt > size())) {
      return false;
    }
    try
    {
      Enumeration localEnumeration = getPrefix(paramInt);
      while (localEnumeration.hasMoreElements())
      {
        String str1 = (String)localEnumeration.nextElement();
        String str2 = (String)paramEnumeration.nextElement();
        if (syntaxTrimBlanks)
        {
          str1 = str1.trim();
          str2 = str2.trim();
        }
        if (syntaxCaseInsensitive)
        {
          if (!str1.equalsIgnoreCase(str2)) {
            return false;
          }
        }
        else if (!str1.equals(str2)) {
          return false;
        }
      }
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      return false;
    }
    return true;
  }
  
  public boolean endsWith(int paramInt, Enumeration<String> paramEnumeration)
  {
    int i = size() - paramInt;
    if ((i < 0) || (i > size())) {
      return false;
    }
    try
    {
      Enumeration localEnumeration = getSuffix(i);
      while (localEnumeration.hasMoreElements())
      {
        String str1 = (String)localEnumeration.nextElement();
        String str2 = (String)paramEnumeration.nextElement();
        if (syntaxTrimBlanks)
        {
          str1 = str1.trim();
          str2 = str2.trim();
        }
        if (syntaxCaseInsensitive)
        {
          if (!str1.equalsIgnoreCase(str2)) {
            return false;
          }
        }
        else if (!str1.equals(str2)) {
          return false;
        }
      }
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      return false;
    }
    return true;
  }
  
  public boolean addAll(Enumeration<String> paramEnumeration)
    throws InvalidNameException
  {
    boolean bool = false;
    for (;;)
    {
      if (paramEnumeration.hasMoreElements()) {
        try
        {
          String str = (String)paramEnumeration.nextElement();
          if ((size() > 0) && (syntaxDirection == 0)) {
            throw new InvalidNameException("A flat name can only have a single component");
          }
          components.addElement(str);
          bool = true;
        }
        catch (NoSuchElementException localNoSuchElementException) {}
      }
    }
    return bool;
  }
  
  public boolean addAll(int paramInt, Enumeration<String> paramEnumeration)
    throws InvalidNameException
  {
    boolean bool = false;
    for (int i = paramInt; paramEnumeration.hasMoreElements(); i++) {
      try
      {
        String str = (String)paramEnumeration.nextElement();
        if ((size() > 0) && (syntaxDirection == 0)) {
          throw new InvalidNameException("A flat name can only have a single component");
        }
        components.insertElementAt(str, i);
        bool = true;
      }
      catch (NoSuchElementException localNoSuchElementException)
      {
        break;
      }
    }
    return bool;
  }
  
  public void add(String paramString)
    throws InvalidNameException
  {
    if ((size() > 0) && (syntaxDirection == 0)) {
      throw new InvalidNameException("A flat name can only have a single component");
    }
    components.addElement(paramString);
  }
  
  public void add(int paramInt, String paramString)
    throws InvalidNameException
  {
    if ((size() > 0) && (syntaxDirection == 0)) {
      throw new InvalidNameException("A flat name can only zero or one component");
    }
    components.insertElementAt(paramString, paramInt);
  }
  
  public Object remove(int paramInt)
  {
    Object localObject = components.elementAt(paramInt);
    components.removeElementAt(paramInt);
    return localObject;
  }
  
  public int hashCode()
  {
    int i = 0;
    Enumeration localEnumeration = getAll();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      if (syntaxTrimBlanks) {
        str = str.trim();
      }
      if (syntaxCaseInsensitive) {
        str = str.toLowerCase(Locale.ENGLISH);
      }
      i += str.hashCode();
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\NameImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
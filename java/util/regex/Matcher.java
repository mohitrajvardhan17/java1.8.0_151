package java.util.regex;

import java.util.Map;
import java.util.Objects;

public final class Matcher
  implements MatchResult
{
  Pattern parentPattern;
  int[] groups;
  int from;
  int to;
  int lookbehindTo;
  CharSequence text;
  static final int ENDANCHOR = 1;
  static final int NOANCHOR = 0;
  int acceptMode = 0;
  int first = -1;
  int last = 0;
  int oldLast = -1;
  int lastAppendPosition = 0;
  int[] locals;
  boolean hitEnd;
  boolean requireEnd;
  boolean transparentBounds = false;
  boolean anchoringBounds = true;
  
  Matcher() {}
  
  Matcher(Pattern paramPattern, CharSequence paramCharSequence)
  {
    parentPattern = paramPattern;
    text = paramCharSequence;
    int i = Math.max(capturingGroupCount, 10);
    groups = new int[i * 2];
    locals = new int[localCount];
    reset();
  }
  
  public Pattern pattern()
  {
    return parentPattern;
  }
  
  public MatchResult toMatchResult()
  {
    Matcher localMatcher = new Matcher(parentPattern, text.toString());
    first = first;
    last = last;
    groups = ((int[])groups.clone());
    return localMatcher;
  }
  
  public Matcher usePattern(Pattern paramPattern)
  {
    if (paramPattern == null) {
      throw new IllegalArgumentException("Pattern cannot be null");
    }
    parentPattern = paramPattern;
    int i = Math.max(capturingGroupCount, 10);
    groups = new int[i * 2];
    locals = new int[localCount];
    for (int j = 0; j < groups.length; j++) {
      groups[j] = -1;
    }
    for (j = 0; j < locals.length; j++) {
      locals[j] = -1;
    }
    return this;
  }
  
  public Matcher reset()
  {
    first = -1;
    last = 0;
    oldLast = -1;
    for (int i = 0; i < groups.length; i++) {
      groups[i] = -1;
    }
    for (i = 0; i < locals.length; i++) {
      locals[i] = -1;
    }
    lastAppendPosition = 0;
    from = 0;
    to = getTextLength();
    return this;
  }
  
  public Matcher reset(CharSequence paramCharSequence)
  {
    text = paramCharSequence;
    return reset();
  }
  
  public int start()
  {
    if (first < 0) {
      throw new IllegalStateException("No match available");
    }
    return first;
  }
  
  public int start(int paramInt)
  {
    if (first < 0) {
      throw new IllegalStateException("No match available");
    }
    if ((paramInt < 0) || (paramInt > groupCount())) {
      throw new IndexOutOfBoundsException("No group " + paramInt);
    }
    return groups[(paramInt * 2)];
  }
  
  public int start(String paramString)
  {
    return groups[(getMatchedGroupIndex(paramString) * 2)];
  }
  
  public int end()
  {
    if (first < 0) {
      throw new IllegalStateException("No match available");
    }
    return last;
  }
  
  public int end(int paramInt)
  {
    if (first < 0) {
      throw new IllegalStateException("No match available");
    }
    if ((paramInt < 0) || (paramInt > groupCount())) {
      throw new IndexOutOfBoundsException("No group " + paramInt);
    }
    return groups[(paramInt * 2 + 1)];
  }
  
  public int end(String paramString)
  {
    return groups[(getMatchedGroupIndex(paramString) * 2 + 1)];
  }
  
  public String group()
  {
    return group(0);
  }
  
  public String group(int paramInt)
  {
    if (first < 0) {
      throw new IllegalStateException("No match found");
    }
    if ((paramInt < 0) || (paramInt > groupCount())) {
      throw new IndexOutOfBoundsException("No group " + paramInt);
    }
    if ((groups[(paramInt * 2)] == -1) || (groups[(paramInt * 2 + 1)] == -1)) {
      return null;
    }
    return getSubSequence(groups[(paramInt * 2)], groups[(paramInt * 2 + 1)]).toString();
  }
  
  public String group(String paramString)
  {
    int i = getMatchedGroupIndex(paramString);
    if ((groups[(i * 2)] == -1) || (groups[(i * 2 + 1)] == -1)) {
      return null;
    }
    return getSubSequence(groups[(i * 2)], groups[(i * 2 + 1)]).toString();
  }
  
  public int groupCount()
  {
    return parentPattern.capturingGroupCount - 1;
  }
  
  public boolean matches()
  {
    return match(from, 1);
  }
  
  public boolean find()
  {
    int i = last;
    if (i == first) {
      i++;
    }
    if (i < from) {
      i = from;
    }
    if (i > to)
    {
      for (int j = 0; j < groups.length; j++) {
        groups[j] = -1;
      }
      return false;
    }
    return search(i);
  }
  
  public boolean find(int paramInt)
  {
    int i = getTextLength();
    if ((paramInt < 0) || (paramInt > i)) {
      throw new IndexOutOfBoundsException("Illegal start index");
    }
    reset();
    return search(paramInt);
  }
  
  public boolean lookingAt()
  {
    return match(from, 0);
  }
  
  public static String quoteReplacement(String paramString)
  {
    if ((paramString.indexOf('\\') == -1) && (paramString.indexOf('$') == -1)) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c == '\\') || (c == '$')) {
        localStringBuilder.append('\\');
      }
      localStringBuilder.append(c);
    }
    return localStringBuilder.toString();
  }
  
  public Matcher appendReplacement(StringBuffer paramStringBuffer, String paramString)
  {
    if (first < 0) {
      throw new IllegalStateException("No match available");
    }
    int i = 0;
    StringBuilder localStringBuilder1 = new StringBuilder();
    while (i < paramString.length())
    {
      char c = paramString.charAt(i);
      if (c == '\\')
      {
        i++;
        if (i == paramString.length()) {
          throw new IllegalArgumentException("character to be escaped is missing");
        }
        c = paramString.charAt(i);
        localStringBuilder1.append(c);
        i++;
      }
      else if (c == '$')
      {
        i++;
        if (i == paramString.length()) {
          throw new IllegalArgumentException("Illegal group reference: group index is missing");
        }
        c = paramString.charAt(i);
        int j = -1;
        if (c == '{')
        {
          i++;
          StringBuilder localStringBuilder2 = new StringBuilder();
          while (i < paramString.length())
          {
            c = paramString.charAt(i);
            if ((!ASCII.isLower(c)) && (!ASCII.isUpper(c)) && (!ASCII.isDigit(c))) {
              break;
            }
            localStringBuilder2.append(c);
            i++;
          }
          if (localStringBuilder2.length() == 0) {
            throw new IllegalArgumentException("named capturing group has 0 length name");
          }
          if (c != '}') {
            throw new IllegalArgumentException("named capturing group is missing trailing '}'");
          }
          String str = localStringBuilder2.toString();
          if (ASCII.isDigit(str.charAt(0))) {
            throw new IllegalArgumentException("capturing group name {" + str + "} starts with digit character");
          }
          if (!parentPattern.namedGroups().containsKey(str)) {
            throw new IllegalArgumentException("No group with name {" + str + "}");
          }
          j = ((Integer)parentPattern.namedGroups().get(str)).intValue();
          i++;
        }
        else
        {
          j = c - '0';
          if ((j < 0) || (j > 9)) {
            throw new IllegalArgumentException("Illegal group reference");
          }
          i++;
          int k = 0;
          while ((k == 0) && (i < paramString.length()))
          {
            int m = paramString.charAt(i) - '0';
            if ((m < 0) || (m > 9)) {
              break;
            }
            int n = j * 10 + m;
            if (groupCount() < n)
            {
              k = 1;
            }
            else
            {
              j = n;
              i++;
            }
          }
        }
        if ((start(j) != -1) && (end(j) != -1)) {
          localStringBuilder1.append(text, start(j), end(j));
        }
      }
      else
      {
        localStringBuilder1.append(c);
        i++;
      }
    }
    paramStringBuffer.append(text, lastAppendPosition, first);
    paramStringBuffer.append(localStringBuilder1);
    lastAppendPosition = last;
    return this;
  }
  
  public StringBuffer appendTail(StringBuffer paramStringBuffer)
  {
    paramStringBuffer.append(text, lastAppendPosition, getTextLength());
    return paramStringBuffer;
  }
  
  public String replaceAll(String paramString)
  {
    reset();
    boolean bool = find();
    if (bool)
    {
      StringBuffer localStringBuffer = new StringBuffer();
      do
      {
        appendReplacement(localStringBuffer, paramString);
        bool = find();
      } while (bool);
      appendTail(localStringBuffer);
      return localStringBuffer.toString();
    }
    return text.toString();
  }
  
  public String replaceFirst(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("replacement");
    }
    reset();
    if (!find()) {
      return text.toString();
    }
    StringBuffer localStringBuffer = new StringBuffer();
    appendReplacement(localStringBuffer, paramString);
    appendTail(localStringBuffer);
    return localStringBuffer.toString();
  }
  
  public Matcher region(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > getTextLength())) {
      throw new IndexOutOfBoundsException("start");
    }
    if ((paramInt2 < 0) || (paramInt2 > getTextLength())) {
      throw new IndexOutOfBoundsException("end");
    }
    if (paramInt1 > paramInt2) {
      throw new IndexOutOfBoundsException("start > end");
    }
    reset();
    from = paramInt1;
    to = paramInt2;
    return this;
  }
  
  public int regionStart()
  {
    return from;
  }
  
  public int regionEnd()
  {
    return to;
  }
  
  public boolean hasTransparentBounds()
  {
    return transparentBounds;
  }
  
  public Matcher useTransparentBounds(boolean paramBoolean)
  {
    transparentBounds = paramBoolean;
    return this;
  }
  
  public boolean hasAnchoringBounds()
  {
    return anchoringBounds;
  }
  
  public Matcher useAnchoringBounds(boolean paramBoolean)
  {
    anchoringBounds = paramBoolean;
    return this;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("java.util.regex.Matcher");
    localStringBuilder.append("[pattern=" + pattern());
    localStringBuilder.append(" region=");
    localStringBuilder.append(regionStart() + "," + regionEnd());
    localStringBuilder.append(" lastmatch=");
    if ((first >= 0) && (group() != null)) {
      localStringBuilder.append(group());
    }
    localStringBuilder.append("]");
    return localStringBuilder.toString();
  }
  
  public boolean hitEnd()
  {
    return hitEnd;
  }
  
  public boolean requireEnd()
  {
    return requireEnd;
  }
  
  boolean search(int paramInt)
  {
    hitEnd = false;
    requireEnd = false;
    paramInt = paramInt < 0 ? 0 : paramInt;
    first = paramInt;
    oldLast = (oldLast < 0 ? paramInt : oldLast);
    for (int i = 0; i < groups.length; i++) {
      groups[i] = -1;
    }
    acceptMode = 0;
    boolean bool = parentPattern.root.match(this, paramInt, text);
    if (!bool) {
      first = -1;
    }
    oldLast = last;
    return bool;
  }
  
  boolean match(int paramInt1, int paramInt2)
  {
    hitEnd = false;
    requireEnd = false;
    paramInt1 = paramInt1 < 0 ? 0 : paramInt1;
    first = paramInt1;
    oldLast = (oldLast < 0 ? paramInt1 : oldLast);
    for (int i = 0; i < groups.length; i++) {
      groups[i] = -1;
    }
    acceptMode = paramInt2;
    boolean bool = parentPattern.matchRoot.match(this, paramInt1, text);
    if (!bool) {
      first = -1;
    }
    oldLast = last;
    return bool;
  }
  
  int getTextLength()
  {
    return text.length();
  }
  
  CharSequence getSubSequence(int paramInt1, int paramInt2)
  {
    return text.subSequence(paramInt1, paramInt2);
  }
  
  char charAt(int paramInt)
  {
    return text.charAt(paramInt);
  }
  
  int getMatchedGroupIndex(String paramString)
  {
    Objects.requireNonNull(paramString, "Group name");
    if (first < 0) {
      throw new IllegalStateException("No match found");
    }
    if (!parentPattern.namedGroups().containsKey(paramString)) {
      throw new IllegalArgumentException("No group with name <" + paramString + ">");
    }
    return ((Integer)parentPattern.namedGroups().get(paramString)).intValue();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\regex\Matcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
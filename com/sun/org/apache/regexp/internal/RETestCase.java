package com.sun.org.apache.regexp.internal;

import java.io.StringBufferInputStream;
import java.io.StringReader;

final class RETestCase
{
  private final StringBuffer log = new StringBuffer();
  private final int number;
  private final String tag;
  private final String pattern;
  private final String toMatch;
  private final boolean badPattern;
  private final boolean shouldMatch;
  private final String[] parens;
  private final RETest test;
  private RE regexp;
  
  public RETestCase(RETest paramRETest, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString)
  {
    number = (++testCount);
    test = paramRETest;
    tag = paramString1;
    pattern = paramString2;
    toMatch = paramString3;
    badPattern = paramBoolean1;
    shouldMatch = paramBoolean2;
    if (paramArrayOfString != null)
    {
      parens = new String[paramArrayOfString.length];
      for (int i = 0; i < paramArrayOfString.length; i++) {
        parens[i] = paramArrayOfString[i];
      }
    }
    else
    {
      parens = null;
    }
  }
  
  public void runTest()
  {
    test.say(tag + "(" + number + "): " + pattern);
    if (testCreation()) {
      testMatch();
    }
  }
  
  boolean testCreation()
  {
    try
    {
      regexp = new RE();
      regexp.setProgram(test.compiler.compile(pattern));
      if (badPattern)
      {
        test.fail(log, "Was expected to be an error, but wasn't.");
        return false;
      }
      return true;
    }
    catch (Exception localException)
    {
      if (badPattern)
      {
        log.append("   Match: ERR\n");
        success("Produces an error (" + localException.toString() + "), as expected.");
        return false;
      }
      String str = localException.getMessage() == null ? localException.toString() : localException.getMessage();
      test.fail(log, "Produces an unexpected exception \"" + str + "\"");
      localException.printStackTrace();
    }
    catch (Error localError)
    {
      test.fail(log, "Compiler threw fatal error \"" + localError.getMessage() + "\"");
      localError.printStackTrace();
    }
    return false;
  }
  
  private void testMatch()
  {
    log.append("   Match against: '" + toMatch + "'\n");
    try
    {
      boolean bool = regexp.match(toMatch);
      log.append("   Matched: " + (bool ? "YES" : "NO") + "\n");
      if ((checkResult(bool)) && ((!shouldMatch) || (checkParens())))
      {
        log.append("   Match using StringCharacterIterator\n");
        if (!tryMatchUsingCI(new StringCharacterIterator(toMatch))) {
          return;
        }
        log.append("   Match using CharacterArrayCharacterIterator\n");
        if (!tryMatchUsingCI(new CharacterArrayCharacterIterator(toMatch.toCharArray(), 0, toMatch.length()))) {
          return;
        }
        log.append("   Match using StreamCharacterIterator\n");
        if (!tryMatchUsingCI(new StreamCharacterIterator(new StringBufferInputStream(toMatch)))) {
          return;
        }
        log.append("   Match using ReaderCharacterIterator\n");
        if (!tryMatchUsingCI(new ReaderCharacterIterator(new StringReader(toMatch)))) {
          return;
        }
      }
    }
    catch (Exception localException)
    {
      test.fail(log, "Matcher threw exception: " + localException.toString());
      localException.printStackTrace();
    }
    catch (Error localError)
    {
      test.fail(log, "Matcher threw fatal error \"" + localError.getMessage() + "\"");
      localError.printStackTrace();
    }
  }
  
  private boolean checkResult(boolean paramBoolean)
  {
    if (paramBoolean == shouldMatch)
    {
      success((shouldMatch ? "Matched" : "Did not match") + " \"" + toMatch + "\", as expected:");
      return true;
    }
    if (shouldMatch) {
      test.fail(log, "Did not match \"" + toMatch + "\", when expected to.");
    } else {
      test.fail(log, "Matched \"" + toMatch + "\", when not expected to.");
    }
    return false;
  }
  
  private boolean checkParens()
  {
    log.append("   Paren count: " + regexp.getParenCount() + "\n");
    if (!assertEquals(log, "Wrong number of parens", parens.length, regexp.getParenCount())) {
      return false;
    }
    for (int i = 0; i < regexp.getParenCount(); i++)
    {
      log.append("   Paren " + i + ": " + regexp.getParen(i) + "\n");
      if (((!"null".equals(parens[i])) || (regexp.getParen(i) != null)) && (!assertEquals(log, "Wrong register " + i, parens[i], regexp.getParen(i)))) {
        return false;
      }
    }
    return true;
  }
  
  boolean tryMatchUsingCI(CharacterIterator paramCharacterIterator)
  {
    try
    {
      boolean bool = regexp.match(paramCharacterIterator, 0);
      log.append("   Match: " + (bool ? "YES" : "NO") + "\n");
      return (checkResult(bool)) && ((!shouldMatch) || (checkParens()));
    }
    catch (Exception localException)
    {
      test.fail(log, "Matcher threw exception: " + localException.toString());
      localException.printStackTrace();
    }
    catch (Error localError)
    {
      test.fail(log, "Matcher threw fatal error \"" + localError.getMessage() + "\"");
      localError.printStackTrace();
    }
    return false;
  }
  
  public boolean assertEquals(StringBuffer paramStringBuffer, String paramString1, String paramString2, String paramString3)
  {
    if (((paramString2 != null) && (!paramString2.equals(paramString3))) || ((paramString3 != null) && (!paramString3.equals(paramString2))))
    {
      test.fail(paramStringBuffer, paramString1 + " (expected \"" + paramString2 + "\", actual \"" + paramString3 + "\")");
      return false;
    }
    return true;
  }
  
  public boolean assertEquals(StringBuffer paramStringBuffer, String paramString, int paramInt1, int paramInt2)
  {
    if (paramInt1 != paramInt2)
    {
      test.fail(paramStringBuffer, paramString + " (expected \"" + paramInt1 + "\", actual \"" + paramInt2 + "\")");
      return false;
    }
    return true;
  }
  
  void success(String paramString) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\regexp\internal\RETestCase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
package com.sun.org.apache.regexp.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class RETest
{
  static final boolean showSuccesses = false;
  static final String NEW_LINE = System.getProperty("line.separator");
  REDebugCompiler compiler = new REDebugCompiler();
  int testCount = 0;
  int failures = 0;
  
  public static void main(String[] paramArrayOfString)
  {
    try
    {
      if (!test(paramArrayOfString)) {
        System.exit(1);
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      System.exit(1);
    }
  }
  
  public static boolean test(String[] paramArrayOfString)
    throws Exception
  {
    RETest localRETest = new RETest();
    if (paramArrayOfString.length == 2)
    {
      localRETest.runInteractiveTests(paramArrayOfString[1]);
    }
    else if (paramArrayOfString.length == 1)
    {
      localRETest.runAutomatedTests(paramArrayOfString[0]);
    }
    else
    {
      System.out.println("Usage: RETest ([-i] [regex]) ([/path/to/testfile.txt])");
      System.out.println("By Default will run automated tests from file 'docs/RETest.txt' ...");
      System.out.println();
      localRETest.runAutomatedTests("docs/RETest.txt");
    }
    return failures == 0;
  }
  
  public RETest() {}
  
  void runInteractiveTests(String paramString)
  {
    RE localRE = new RE();
    try
    {
      localRE.setProgram(compiler.compile(paramString));
      say("" + NEW_LINE + "" + paramString + "" + NEW_LINE + "");
      PrintWriter localPrintWriter = new PrintWriter(System.out);
      compiler.dumpProgram(localPrintWriter);
      localPrintWriter.flush();
      int i = 1;
      while (i != 0)
      {
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("> ");
        System.out.flush();
        String str = localBufferedReader.readLine();
        if (str != null)
        {
          if (localRE.match(str)) {
            say("Match successful.");
          } else {
            say("Match failed.");
          }
          showParens(localRE);
        }
        else
        {
          i = 0;
          System.out.println();
        }
      }
    }
    catch (Exception localException)
    {
      say("Error: " + localException.toString());
      localException.printStackTrace();
    }
  }
  
  void die(String paramString)
  {
    say("FATAL ERROR: " + paramString);
    System.exit(-1);
  }
  
  void fail(StringBuffer paramStringBuffer, String paramString)
  {
    System.out.print(paramStringBuffer.toString());
    fail(paramString);
  }
  
  void fail(String paramString)
  {
    failures += 1;
    say("" + NEW_LINE + "");
    say("*******************************************************");
    say("*********************  FAILURE!  **********************");
    say("*******************************************************");
    say("" + NEW_LINE + "");
    say(paramString);
    say("");
    if (compiler != null)
    {
      PrintWriter localPrintWriter = new PrintWriter(System.out);
      compiler.dumpProgram(localPrintWriter);
      localPrintWriter.flush();
      say("" + NEW_LINE + "");
    }
  }
  
  void say(String paramString)
  {
    System.out.println(paramString);
  }
  
  void showParens(RE paramRE)
  {
    for (int i = 0; i < paramRE.getParenCount(); i++) {
      say("$" + i + " = " + paramRE.getParen(i));
    }
  }
  
  void runAutomatedTests(String paramString)
    throws Exception
  {
    long l = System.currentTimeMillis();
    testPrecompiledRE();
    testSplitAndGrep();
    testSubst();
    testOther();
    File localFile = new File(paramString);
    if (!localFile.exists()) {
      throw new Exception("Could not find: " + paramString);
    }
    BufferedReader localBufferedReader = new BufferedReader(new FileReader(localFile));
    try
    {
      while (localBufferedReader.ready())
      {
        RETestCase localRETestCase = getNextTestCase(localBufferedReader);
        if (localRETestCase != null) {
          localRETestCase.runTest();
        }
      }
    }
    finally
    {
      localBufferedReader.close();
    }
    say(NEW_LINE + NEW_LINE + "Match time = " + (System.currentTimeMillis() - l) + " ms.");
    if (failures > 0) {
      say("*************** THERE ARE FAILURES! *******************");
    }
    say("Tests complete.  " + testCount + " tests, " + failures + " failure(s).");
  }
  
  void testOther()
    throws Exception
  {
    RE localRE = new RE("(a*)b");
    say("Serialized/deserialized (a*)b");
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(128);
    new ObjectOutputStream(localByteArrayOutputStream).writeObject(localRE);
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
    localRE = (RE)new ObjectInputStream(localByteArrayInputStream).readObject();
    if (!localRE.match("aaab"))
    {
      fail("Did not match 'aaab' with deserialized RE.");
    }
    else
    {
      say("aaaab = true");
      showParens(localRE);
    }
    localByteArrayOutputStream.reset();
    say("Deserialized (a*)b");
    new ObjectOutputStream(localByteArrayOutputStream).writeObject(localRE);
    localByteArrayInputStream = new ByteArrayInputStream(localByteArrayOutputStream.toByteArray());
    localRE = (RE)new ObjectInputStream(localByteArrayInputStream).readObject();
    if (localRE.getParenCount() != 0) {
      fail("Has parens after deserialization.");
    }
    if (!localRE.match("aaab"))
    {
      fail("Did not match 'aaab' with deserialized RE.");
    }
    else
    {
      say("aaaab = true");
      showParens(localRE);
    }
    localRE = new RE("abc(\\w*)");
    say("MATCH_CASEINDEPENDENT abc(\\w*)");
    localRE.setMatchFlags(1);
    say("abc(d*)");
    if (!localRE.match("abcddd"))
    {
      fail("Did not match 'abcddd'.");
    }
    else
    {
      say("abcddd = true");
      showParens(localRE);
    }
    if (!localRE.match("aBcDDdd"))
    {
      fail("Did not match 'aBcDDdd'.");
    }
    else
    {
      say("aBcDDdd = true");
      showParens(localRE);
    }
    if (!localRE.match("ABCDDDDD"))
    {
      fail("Did not match 'ABCDDDDD'.");
    }
    else
    {
      say("ABCDDDDD = true");
      showParens(localRE);
    }
    localRE = new RE("(A*)b\\1");
    localRE.setMatchFlags(1);
    if (!localRE.match("AaAaaaBAAAAAA"))
    {
      fail("Did not match 'AaAaaaBAAAAAA'.");
    }
    else
    {
      say("AaAaaaBAAAAAA = true");
      showParens(localRE);
    }
    localRE = new RE("[A-Z]*");
    localRE.setMatchFlags(1);
    if (!localRE.match("CaBgDe12"))
    {
      fail("Did not match 'CaBgDe12'.");
    }
    else
    {
      say("CaBgDe12 = true");
      showParens(localRE);
    }
    localRE = new RE("^abc$", 2);
    if (!localRE.match("\nabc")) {
      fail("\"\\nabc\" doesn't match \"^abc$\"");
    }
    if (!localRE.match("\rabc")) {
      fail("\"\\rabc\" doesn't match \"^abc$\"");
    }
    if (!localRE.match("\r\nabc")) {
      fail("\"\\r\\nabc\" doesn't match \"^abc$\"");
    }
    if (!localRE.match("abc")) {
      fail("\"\\u0085abc\" doesn't match \"^abc$\"");
    }
    if (!localRE.match(" abc")) {
      fail("\"\\u2028abc\" doesn't match \"^abc$\"");
    }
    if (!localRE.match(" abc")) {
      fail("\"\\u2029abc\" doesn't match \"^abc$\"");
    }
    localRE = new RE("^a.*b$", 2);
    if (localRE.match("a\nb")) {
      fail("\"a\\nb\" matches \"^a.*b$\"");
    }
    if (localRE.match("a\rb")) {
      fail("\"a\\rb\" matches \"^a.*b$\"");
    }
    if (localRE.match("a\r\nb")) {
      fail("\"a\\r\\nb\" matches \"^a.*b$\"");
    }
    if (localRE.match("ab")) {
      fail("\"a\\u0085b\" matches \"^a.*b$\"");
    }
    if (localRE.match("a b")) {
      fail("\"a\\u2028b\" matches \"^a.*b$\"");
    }
    if (localRE.match("a b")) {
      fail("\"a\\u2029b\" matches \"^a.*b$\"");
    }
  }
  
  private void testPrecompiledRE()
  {
    char[] arrayOfChar = { '|', '\000', '\032', '|', '\000', '\r', 'A', '\001', '\004', 'a', '|', '\000', '\003', 'G', '\000', 65526, '|', '\000', '\003', 'N', '\000', '\003', 'A', '\001', '\004', 'b', 'E', '\000', '\000' };
    REProgram localREProgram = new REProgram(arrayOfChar);
    RE localRE = new RE(localREProgram);
    say("a*b");
    boolean bool = localRE.match("aaab");
    say("aaab = " + bool);
    showParens(localRE);
    if (!bool) {
      fail("\"aaab\" doesn't match to precompiled \"a*b\"");
    }
    bool = localRE.match("b");
    say("b = " + bool);
    showParens(localRE);
    if (!bool) {
      fail("\"b\" doesn't match to precompiled \"a*b\"");
    }
    bool = localRE.match("c");
    say("c = " + bool);
    showParens(localRE);
    if (bool) {
      fail("\"c\" matches to precompiled \"a*b\"");
    }
    bool = localRE.match("ccccaaaaab");
    say("ccccaaaaab = " + bool);
    showParens(localRE);
    if (!bool) {
      fail("\"ccccaaaaab\" doesn't match to precompiled \"a*b\"");
    }
  }
  
  private void testSplitAndGrep()
  {
    String[] arrayOfString1 = { "xxxx", "xxxx", "yyyy", "zzz" };
    RE localRE = new RE("a*b");
    String[] arrayOfString2 = localRE.split("xxxxaabxxxxbyyyyaaabzzz");
    for (int i = 0; (i < arrayOfString1.length) && (i < arrayOfString2.length); i++) {
      assertEquals("Wrong splitted part", arrayOfString1[i], arrayOfString2[i]);
    }
    assertEquals("Wrong number of splitted parts", arrayOfString1.length, arrayOfString2.length);
    localRE = new RE("x+");
    arrayOfString1 = new String[] { "xxxx", "xxxx" };
    arrayOfString2 = localRE.grep(arrayOfString2);
    for (i = 0; i < arrayOfString2.length; i++)
    {
      say("s[" + i + "] = " + arrayOfString2[i]);
      assertEquals("Grep fails", arrayOfString1[i], arrayOfString2[i]);
    }
    assertEquals("Wrong number of string found by grep", arrayOfString1.length, arrayOfString2.length);
  }
  
  private void testSubst()
  {
    RE localRE = new RE("a*b");
    String str1 = "-foo-garply-wacky-";
    String str2 = localRE.subst("aaaabfooaaabgarplyaaabwackyb", "-");
    assertEquals("Wrong result of substitution in \"a*b\"", str1, str2);
    localRE = new RE("http://[\\.\\w\\-\\?/~_@&=%]+");
    str2 = localRE.subst("visit us: http://www.apache.org!", "1234<a href=\"$0\">$0</a>", 2);
    assertEquals("Wrong subst() result", "visit us: 1234<a href=\"http://www.apache.org\">http://www.apache.org</a>!", str2);
    localRE = new RE("(.*?)=(.*)");
    str2 = localRE.subst("variable=value", "$1_test_$212", 2);
    assertEquals("Wrong subst() result", "variable_test_value12", str2);
    localRE = new RE("^a$");
    str2 = localRE.subst("a", "b", 2);
    assertEquals("Wrong subst() result", "b", str2);
    localRE = new RE("^a$", 2);
    str2 = localRE.subst("\r\na\r\n", "b", 2);
    assertEquals("Wrong subst() result", "\r\nb\r\n", str2);
  }
  
  public void assertEquals(String paramString1, String paramString2, String paramString3)
  {
    if (((paramString2 != null) && (!paramString2.equals(paramString3))) || ((paramString3 != null) && (!paramString3.equals(paramString2)))) {
      fail(paramString1 + " (expected \"" + paramString2 + "\", actual \"" + paramString3 + "\")");
    }
  }
  
  public void assertEquals(String paramString, int paramInt1, int paramInt2)
  {
    if (paramInt1 != paramInt2) {
      fail(paramString + " (expected \"" + paramInt1 + "\", actual \"" + paramInt2 + "\")");
    }
  }
  
  private boolean getExpectedResult(String paramString)
  {
    if ("NO".equals(paramString)) {
      return false;
    }
    if ("YES".equals(paramString)) {
      return true;
    }
    die("Test script error!");
    return false;
  }
  
  private String findNextTest(BufferedReader paramBufferedReader)
    throws IOException
  {
    String str = "";
    while (paramBufferedReader.ready())
    {
      str = paramBufferedReader.readLine();
      if (str == null) {
        break;
      }
      str = str.trim();
      if (str.startsWith("#")) {
        break;
      }
      if (!str.equals(""))
      {
        say("Script error.  Line = " + str);
        System.exit(-1);
      }
    }
    return str;
  }
  
  private RETestCase getNextTestCase(BufferedReader paramBufferedReader)
    throws IOException
  {
    String str1 = findNextTest(paramBufferedReader);
    if (!paramBufferedReader.ready()) {
      return null;
    }
    String str2 = paramBufferedReader.readLine();
    String str3 = paramBufferedReader.readLine();
    boolean bool1 = "ERR".equals(str3);
    boolean bool2 = false;
    int i = 0;
    String[] arrayOfString = null;
    if (!bool1)
    {
      bool2 = getExpectedResult(paramBufferedReader.readLine().trim());
      if (bool2)
      {
        i = Integer.parseInt(paramBufferedReader.readLine().trim());
        arrayOfString = new String[i];
        for (int j = 0; j < i; j++) {
          arrayOfString[j] = paramBufferedReader.readLine();
        }
      }
    }
    return new RETestCase(this, str1, str2, str3, bool1, bool2, arrayOfString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\regexp\internal\RETest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
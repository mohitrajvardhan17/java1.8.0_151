package com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class GetOpt
{
  private Option theCurrentOption = null;
  private ListIterator theOptionsIterator;
  private List theOptions = null;
  private List theCmdArgs = null;
  private OptionMatcher theOptionMatcher = null;
  
  public GetOpt(String[] paramArrayOfString, String paramString)
  {
    int i = 0;
    theCmdArgs = new ArrayList();
    theOptionMatcher = new OptionMatcher(paramString);
    String str;
    for (int j = 0; j < paramArrayOfString.length; j++)
    {
      str = paramArrayOfString[j];
      int k = str.length();
      if (str.equals("--"))
      {
        i = j + 1;
        break;
      }
      if ((str.startsWith("-")) && (k == 2))
      {
        theOptions.add(new Option(str.charAt(1)));
      }
      else
      {
        int m;
        if ((str.startsWith("-")) && (k > 2))
        {
          for (m = 1; m < k; m++) {
            theOptions.add(new Option(str.charAt(m)));
          }
        }
        else if (!str.startsWith("-"))
        {
          if (theOptions.size() == 0)
          {
            i = j;
            break;
          }
          m = 0;
          m = theOptions.size() - 1;
          Option localOption = (Option)theOptions.get(m);
          char c = localOption.getArgLetter();
          if ((!localOption.hasArg()) && (theOptionMatcher.hasArg(c)))
          {
            localOption.setArg(str);
          }
          else
          {
            i = j;
            break;
          }
        }
      }
    }
    theOptionsIterator = theOptions.listIterator();
    for (j = i; j < paramArrayOfString.length; j++)
    {
      str = paramArrayOfString[j];
      theCmdArgs.add(str);
    }
  }
  
  public void printOptions()
  {
    ListIterator localListIterator = theOptions.listIterator();
    while (localListIterator.hasNext())
    {
      Option localOption = (Option)localListIterator.next();
      System.out.print("OPT =" + localOption.getArgLetter());
      String str = localOption.getArgument();
      if (str != null) {
        System.out.print(" " + str);
      }
      System.out.println();
    }
  }
  
  public int getNextOption()
    throws IllegalArgumentException, MissingOptArgException
  {
    int i = -1;
    if (theOptionsIterator.hasNext())
    {
      theCurrentOption = ((Option)theOptionsIterator.next());
      int j = theCurrentOption.getArgLetter();
      boolean bool = theOptionMatcher.hasArg(j);
      String str = theCurrentOption.getArgument();
      ErrorMsg localErrorMsg;
      if (!theOptionMatcher.match(j))
      {
        localErrorMsg = new ErrorMsg("ILLEGAL_CMDLINE_OPTION_ERR", new Character(j));
        throw new IllegalArgumentException(localErrorMsg.toString());
      }
      if ((bool) && (str == null))
      {
        localErrorMsg = new ErrorMsg("CMDLINE_OPT_MISSING_ARG_ERR", new Character(j));
        throw new MissingOptArgException(localErrorMsg.toString());
      }
      i = j;
    }
    return i;
  }
  
  public String getOptionArg()
  {
    Object localObject = null;
    String str = theCurrentOption.getArgument();
    char c = theCurrentOption.getArgLetter();
    if (theOptionMatcher.hasArg(c)) {
      localObject = str;
    }
    return (String)localObject;
  }
  
  public String[] getCmdArgs()
  {
    String[] arrayOfString = new String[theCmdArgs.size()];
    int i = 0;
    ListIterator localListIterator = theCmdArgs.listIterator();
    while (localListIterator.hasNext()) {
      arrayOfString[(i++)] = ((String)localListIterator.next());
    }
    return arrayOfString;
  }
  
  class Option
  {
    private char theArgLetter;
    private String theArgument = null;
    
    public Option(char paramChar)
    {
      theArgLetter = paramChar;
    }
    
    public void setArg(String paramString)
    {
      theArgument = paramString;
    }
    
    public boolean hasArg()
    {
      return theArgument != null;
    }
    
    public char getArgLetter()
    {
      return theArgLetter;
    }
    
    public String getArgument()
    {
      return theArgument;
    }
  }
  
  class OptionMatcher
  {
    private String theOptString = null;
    
    public OptionMatcher(String paramString)
    {
      theOptString = paramString;
    }
    
    public boolean match(char paramChar)
    {
      boolean bool = false;
      if (theOptString.indexOf(paramChar) != -1) {
        bool = true;
      }
      return bool;
    }
    
    public boolean hasArg(char paramChar)
    {
      boolean bool = false;
      int i = theOptString.indexOf(paramChar) + 1;
      if (i == theOptString.length()) {
        bool = false;
      } else if (theOptString.charAt(i) == ':') {
        bool = true;
      }
      return bool;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\cmdline\getopt\GetOpt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
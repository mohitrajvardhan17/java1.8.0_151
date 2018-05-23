package sun.misc;

import java.io.PrintStream;

class RegexpNode
{
  char c;
  RegexpNode firstchild;
  RegexpNode nextsibling;
  int depth;
  boolean exact;
  Object result;
  String re = null;
  
  RegexpNode()
  {
    c = '#';
    depth = 0;
  }
  
  RegexpNode(char paramChar, int paramInt)
  {
    c = paramChar;
    depth = paramInt;
  }
  
  RegexpNode add(char paramChar)
  {
    RegexpNode localRegexpNode = firstchild;
    if (localRegexpNode == null)
    {
      localRegexpNode = new RegexpNode(paramChar, depth + 1);
    }
    else
    {
      while (localRegexpNode != null)
      {
        if (c == paramChar) {
          return localRegexpNode;
        }
        localRegexpNode = nextsibling;
      }
      localRegexpNode = new RegexpNode(paramChar, depth + 1);
      nextsibling = firstchild;
    }
    firstchild = localRegexpNode;
    return localRegexpNode;
  }
  
  RegexpNode find(char paramChar)
  {
    for (RegexpNode localRegexpNode = firstchild; localRegexpNode != null; localRegexpNode = nextsibling) {
      if (c == paramChar) {
        return localRegexpNode;
      }
    }
    return null;
  }
  
  void print(PrintStream paramPrintStream)
  {
    if (nextsibling != null)
    {
      RegexpNode localRegexpNode = this;
      paramPrintStream.print("(");
      while (localRegexpNode != null)
      {
        paramPrintStream.write(c);
        if (firstchild != null) {
          firstchild.print(paramPrintStream);
        }
        localRegexpNode = nextsibling;
        paramPrintStream.write(localRegexpNode != null ? 124 : 41);
      }
    }
    else
    {
      paramPrintStream.write(c);
      if (firstchild != null) {
        firstchild.print(paramPrintStream);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\RegexpNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
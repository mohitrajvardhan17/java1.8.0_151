package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.java_cup.internal.runtime.Symbol;
import com.sun.java_cup.internal.runtime.lr_parser;
import java.util.Stack;
import java.util.Vector;

class CUP$XPathParser$actions
{
  private final XPathParser parser;
  
  CUP$XPathParser$actions(XPathParser paramXPathParser)
  {
    parser = paramXPathParser;
  }
  
  public final Symbol CUP$XPathParser$do_action(int paramInt1, lr_parser paramlr_parser, Stack paramStack, int paramInt2)
    throws Exception
  {
    Object localObject1;
    Symbol localSymbol;
    int i;
    int j;
    Object localObject2;
    Object localObject3;
    int i2;
    int n;
    int i6;
    Step localStep2;
    int i3;
    int i5;
    Object localObject4;
    int i1;
    int i7;
    switch (paramInt1)
    {
    case 140: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("id");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 139: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("self");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 138: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("preceding-sibling");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 137: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("preceding");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 136: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("parent");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 135: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("namespace");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 134: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("following-sibling");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 133: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("following");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 132: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("decendant-or-self");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 131: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("decendant");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 130: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("child");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 129: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("attribute");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 128: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("ancestor-or-self");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 127: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("child");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 126: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("key");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 125: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("mod");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 124: 
      localObject1 = null;
      localObject1 = parser.getQNameIgnoreDefaultNs("div");
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 123: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (String)elementAt0value;
      localObject1 = parser.getQNameIgnoreDefaultNs((String)localObject2);
      localSymbol = new Symbol(37, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 122: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (QName)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(26, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 121: 
      localObject1 = null;
      localObject1 = null;
      localSymbol = new Symbol(26, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 120: 
      localObject1 = null;
      localObject1 = new Integer(7);
      localSymbol = new Symbol(25, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 119: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (String)elementAt1value;
      QName localQName = parser.getQNameIgnoreDefaultNs("name");
      EqualityExpr localEqualityExpr = new EqualityExpr(0, new NameCall(localQName), new LiteralExpr((String)localObject2));
      localObject3 = new Vector();
      ((Vector)localObject3).addElement(new Predicate(localEqualityExpr));
      localObject1 = new Step(3, 7, (Vector)localObject3);
      localSymbol = new Symbol(25, elementAt3left, elementAt0right, localObject1);
      return localSymbol;
    case 118: 
      localObject1 = null;
      localObject1 = new Integer(8);
      localSymbol = new Symbol(25, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 117: 
      localObject1 = null;
      localObject1 = new Integer(3);
      localSymbol = new Symbol(25, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 116: 
      localObject1 = null;
      localObject1 = new Integer(-1);
      localSymbol = new Symbol(25, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 115: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(25, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 114: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(3, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 113: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (QName)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(39, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 112: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (QName)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(38, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 111: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      int k = elementAt0left;
      i2 = elementAt0right;
      localObject3 = (Vector)elementAt0value;
      ((Vector)localObject3).insertElementAt(localObject2, 0);
      localObject1 = localObject3;
      localSymbol = new Symbol(36, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 110: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      Vector localVector1 = new Vector();
      localVector1.addElement(localObject2);
      localObject1 = localVector1;
      localSymbol = new Symbol(36, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 109: 
      localObject1 = null;
      i = elementAt3left;
      j = elementAt3right;
      localObject2 = (QName)elementAt3value;
      int m = elementAt1left;
      i2 = elementAt1right;
      localObject3 = (Vector)elementAt1value;
      if (localObject2 == parser.getQNameIgnoreDefaultNs("concat"))
      {
        localObject1 = new ConcatCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("number"))
      {
        localObject1 = new NumberCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("document"))
      {
        parser.setMultiDocument(true);
        localObject1 = new DocumentCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("string"))
      {
        localObject1 = new StringCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("boolean"))
      {
        localObject1 = new BooleanCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("name"))
      {
        localObject1 = new NameCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("generate-id"))
      {
        localObject1 = new GenerateIdCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("not"))
      {
        localObject1 = new NotCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("format-number"))
      {
        localObject1 = new FormatNumberCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("unparsed-entity-uri"))
      {
        localObject1 = new UnparsedEntityUriCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("key"))
      {
        localObject1 = new KeyCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("id"))
      {
        localObject1 = new KeyCall((QName)localObject2, (Vector)localObject3);
        parser.setHasIdCall(true);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("ceiling"))
      {
        localObject1 = new CeilingCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("round"))
      {
        localObject1 = new RoundCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("floor"))
      {
        localObject1 = new FloorCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("contains"))
      {
        localObject1 = new ContainsCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("string-length"))
      {
        localObject1 = new StringLengthCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("starts-with"))
      {
        localObject1 = new StartsWithCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("function-available"))
      {
        localObject1 = new FunctionAvailableCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("element-available"))
      {
        localObject1 = new ElementAvailableCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("local-name"))
      {
        localObject1 = new LocalNameCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("lang"))
      {
        localObject1 = new LangCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQNameIgnoreDefaultNs("namespace-uri"))
      {
        localObject1 = new NamespaceUriCall((QName)localObject2, (Vector)localObject3);
      }
      else if (localObject2 == parser.getQName("http://xml.apache.org/xalan/xsltc", "xsltc", "cast"))
      {
        localObject1 = new CastCall((QName)localObject2, (Vector)localObject3);
      }
      else if ((((QName)localObject2).getLocalPart().equals("nodeset")) || (((QName)localObject2).getLocalPart().equals("node-set")))
      {
        parser.setCallsNodeset(true);
        localObject1 = new FunctionCall((QName)localObject2, (Vector)localObject3);
      }
      else
      {
        localObject1 = new FunctionCall((QName)localObject2, (Vector)localObject3);
      }
      localSymbol = new Symbol(16, elementAt3left, elementAt0right, localObject1);
      return localSymbol;
    case 108: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (QName)elementAt2value;
      if (localObject2 == parser.getQNameIgnoreDefaultNs("current")) {
        localObject1 = new CurrentCall((QName)localObject2);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("number")) {
        localObject1 = new NumberCall((QName)localObject2, XPathParser.EmptyArgs);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("string")) {
        localObject1 = new StringCall((QName)localObject2, XPathParser.EmptyArgs);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("concat")) {
        localObject1 = new ConcatCall((QName)localObject2, XPathParser.EmptyArgs);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("true")) {
        localObject1 = new BooleanExpr(true);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("false")) {
        localObject1 = new BooleanExpr(false);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("name")) {
        localObject1 = new NameCall((QName)localObject2);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("generate-id")) {
        localObject1 = new GenerateIdCall((QName)localObject2, XPathParser.EmptyArgs);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("string-length")) {
        localObject1 = new StringLengthCall((QName)localObject2, XPathParser.EmptyArgs);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("position")) {
        localObject1 = new PositionCall((QName)localObject2);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("last")) {
        localObject1 = new LastCall((QName)localObject2);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("local-name")) {
        localObject1 = new LocalNameCall((QName)localObject2);
      } else if (localObject2 == parser.getQNameIgnoreDefaultNs("namespace-uri")) {
        localObject1 = new NamespaceUriCall((QName)localObject2);
      } else {
        localObject1 = new FunctionCall((QName)localObject2, XPathParser.EmptyArgs);
      }
      localSymbol = new Symbol(16, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 107: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (QName)elementAt0value;
      SyntaxTreeNode localSyntaxTreeNode = parser.lookupName((QName)localObject2);
      if (localSyntaxTreeNode != null) {
        if ((localSyntaxTreeNode instanceof Variable)) {
          localObject1 = new VariableRef((Variable)localSyntaxTreeNode);
        } else if ((localSyntaxTreeNode instanceof Param)) {
          localObject1 = new ParameterRef((Param)localSyntaxTreeNode);
        } else {
          localObject1 = new UnresolvedRef((QName)localObject2);
        }
      }
      if (localSyntaxTreeNode == null) {
        localObject1 = new UnresolvedRef((QName)localObject2);
      }
      localSymbol = new Symbol(15, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 106: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(17, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 105: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Double)elementAt0value;
      localObject1 = new RealExpr(((Double)localObject2).doubleValue());
      localSymbol = new Symbol(17, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 104: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Long)elementAt0value;
      long l = ((Long)localObject2).longValue();
      if ((l < -2147483648L) || (l > 2147483647L)) {
        localObject1 = new RealExpr(l);
      } else if (((Long)localObject2).doubleValue() == 0.0D) {
        localObject1 = new RealExpr(((Long)localObject2).doubleValue());
      } else if (((Long)localObject2).intValue() == 0) {
        localObject1 = new IntExpr(((Long)localObject2).intValue());
      } else if (((Long)localObject2).doubleValue() == 0.0D) {
        localObject1 = new RealExpr(((Long)localObject2).doubleValue());
      } else {
        localObject1 = new IntExpr(((Long)localObject2).intValue());
      }
      localSymbol = new Symbol(17, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 103: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (String)elementAt0value;
      String str = null;
      i2 = ((String)localObject2).lastIndexOf(':');
      if (i2 > 0)
      {
        localObject3 = ((String)localObject2).substring(0, i2);
        str = parser._symbolTable.lookupNamespace((String)localObject3);
      }
      localObject1 = str == null ? new LiteralExpr((String)localObject2) : new LiteralExpr((String)localObject2, str);
      localSymbol = new Symbol(17, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 102: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (Expression)elementAt1value;
      localObject1 = localObject2;
      localSymbol = new Symbol(17, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 101: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(17, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 100: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (Expression)elementAt1value;
      n = elementAt0left;
      i2 = elementAt0right;
      localObject3 = (Vector)elementAt0value;
      localObject1 = new FilterExpr((Expression)localObject2, (Vector)localObject3);
      localSymbol = new Symbol(6, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 99: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(6, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 98: 
      localObject1 = null;
      localObject1 = new Step(10, -1, null);
      localSymbol = new Symbol(20, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 97: 
      localObject1 = null;
      localObject1 = new Step(13, -1, null);
      localSymbol = new Symbol(20, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 96: 
      localObject1 = null;
      localObject1 = new Integer(13);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 95: 
      localObject1 = null;
      localObject1 = new Integer(12);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 94: 
      localObject1 = null;
      localObject1 = new Integer(11);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 93: 
      localObject1 = null;
      localObject1 = new Integer(10);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 92: 
      localObject1 = null;
      localObject1 = new Integer(9);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 91: 
      localObject1 = null;
      localObject1 = new Integer(7);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 90: 
      localObject1 = null;
      localObject1 = new Integer(6);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 89: 
      localObject1 = null;
      localObject1 = new Integer(5);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 88: 
      localObject1 = null;
      localObject1 = new Integer(4);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 87: 
      localObject1 = null;
      localObject1 = new Integer(3);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 86: 
      localObject1 = null;
      localObject1 = new Integer(2);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 85: 
      localObject1 = null;
      localObject1 = new Integer(1);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 84: 
      localObject1 = null;
      localObject1 = new Integer(0);
      localSymbol = new Symbol(40, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 83: 
      localObject1 = null;
      localObject1 = new Integer(2);
      localSymbol = new Symbol(41, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 82: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (Integer)elementAt1value;
      localObject1 = localObject2;
      localSymbol = new Symbol(41, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 81: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(7, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 80: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (Integer)elementAt1value;
      n = elementAt0left;
      i2 = elementAt0right;
      localObject3 = elementAt0value;
      localObject1 = new Step(((Integer)localObject2).intValue(), parser.findNodeType(((Integer)localObject2).intValue(), localObject3), null);
      localSymbol = new Symbol(7, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 79: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Integer)elementAt2value;
      n = elementAt1left;
      i2 = elementAt1right;
      localObject3 = elementAt1value;
      int i4 = elementAt0left;
      i6 = elementAt0right;
      Vector localVector3 = (Vector)elementAt0value;
      localObject1 = new Step(((Integer)localObject2).intValue(), parser.findNodeType(((Integer)localObject2).intValue(), localObject3), localVector3);
      localSymbol = new Symbol(7, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 78: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = elementAt1value;
      n = elementAt0left;
      i2 = elementAt0right;
      localObject3 = (Vector)elementAt0value;
      if ((localObject2 instanceof Step))
      {
        localStep2 = (Step)localObject2;
        localStep2.addPredicates((Vector)localObject3);
        localObject1 = (Step)localObject2;
      }
      else
      {
        localObject1 = new Step(3, parser.findNodeType(3, localObject2), (Vector)localObject3);
      }
      localSymbol = new Symbol(7, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 77: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = elementAt0value;
      if ((localObject2 instanceof Step)) {
        localObject1 = (Step)localObject2;
      } else {
        localObject1 = new Step(3, parser.findNodeType(3, localObject2), null);
      }
      localSymbol = new Symbol(7, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 76: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      n = -1;
      if (((localObject2 instanceof Step)) && (parser.isElementAxis(((Step)localObject2).getAxis()))) {
        n = 1;
      }
      Step localStep1 = new Step(5, n, null);
      localObject1 = new AbsoluteLocationPath(parser.insertStep(localStep1, (RelativeLocationPath)localObject2));
      localSymbol = new Symbol(24, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 75: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localStep2 = (Step)localObject3;
      i6 = localStep2.getAxis();
      int i8 = localStep2.getNodeType();
      Vector localVector4 = localStep2.getPredicates();
      Object localObject5;
      Step localStep4;
      ParentLocationPath localParentLocationPath;
      if ((i6 == 3) && (i8 != 2))
      {
        if (localVector4 == null)
        {
          localStep2.setAxis(4);
          if (((localObject2 instanceof Step)) && (((Step)localObject2).isAbbreviatedDot()))
          {
            localObject1 = localStep2;
          }
          else
          {
            localObject5 = (RelativeLocationPath)localObject2;
            localObject1 = new ParentLocationPath((RelativeLocationPath)localObject5, localStep2);
          }
        }
        else if (((localObject2 instanceof Step)) && (((Step)localObject2).isAbbreviatedDot()))
        {
          localObject5 = new Step(5, 1, null);
          localObject1 = new ParentLocationPath((RelativeLocationPath)localObject5, localStep2);
        }
        else
        {
          localObject5 = (RelativeLocationPath)localObject2;
          localStep4 = new Step(5, 1, null);
          localParentLocationPath = new ParentLocationPath(localStep4, localStep2);
          localObject1 = new ParentLocationPath((RelativeLocationPath)localObject5, localParentLocationPath);
        }
      }
      else if ((i6 == 2) || (i8 == 2))
      {
        localObject5 = (RelativeLocationPath)localObject2;
        localStep4 = new Step(5, 1, null);
        localParentLocationPath = new ParentLocationPath(localStep4, localStep2);
        localObject1 = new ParentLocationPath((RelativeLocationPath)localObject5, localParentLocationPath);
      }
      else
      {
        localObject5 = (RelativeLocationPath)localObject2;
        localStep4 = new Step(5, -1, null);
        localParentLocationPath = new ParentLocationPath(localStep4, localStep2);
        localObject1 = new ParentLocationPath((RelativeLocationPath)localObject5, localParentLocationPath);
      }
      localSymbol = new Symbol(22, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 74: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(23, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 73: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = new AbsoluteLocationPath((Expression)localObject2);
      localSymbol = new Symbol(23, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 72: 
      localObject1 = null;
      localObject1 = new AbsoluteLocationPath();
      localSymbol = new Symbol(23, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 71: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(21, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 70: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      if (((localObject2 instanceof Step)) && (((Step)localObject2).isAbbreviatedDot())) {
        localObject1 = localObject3;
      } else if (((Step)localObject3).isAbbreviatedDot()) {
        localObject1 = localObject2;
      } else {
        localObject1 = new ParentLocationPath((RelativeLocationPath)localObject2, (Expression)localObject3);
      }
      localSymbol = new Symbol(21, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 69: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(21, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 68: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(4, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 67: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(4, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 66: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      i5 = -1;
      if (((localObject3 instanceof Step)) && (parser.isElementAxis(((Step)localObject3).getAxis()))) {
        i5 = 1;
      }
      Step localStep3 = new Step(5, i5, null);
      localObject4 = new FilterParentPath((Expression)localObject2, localStep3);
      localObject4 = new FilterParentPath((Expression)localObject4, (Expression)localObject3);
      if (!(localObject2 instanceof KeyCall)) {
        ((FilterParentPath)localObject4).setDescendantAxis();
      }
      localObject1 = localObject4;
      localSymbol = new Symbol(19, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 65: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new FilterParentPath((Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(19, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 64: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(19, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 63: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(19, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 62: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new UnionPathExpr((Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(18, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 61: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(18, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 60: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = new UnaryOpExpr((Expression)localObject2);
      localSymbol = new Symbol(14, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 59: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(14, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 58: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new BinOpExpr(4, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(13, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 57: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new BinOpExpr(3, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(13, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 56: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new BinOpExpr(2, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(13, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 55: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(13, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 54: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new BinOpExpr(1, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(12, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 53: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new BinOpExpr(0, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(12, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 52: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(12, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 51: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new RelationalExpr(4, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(11, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 50: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new RelationalExpr(5, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(11, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 49: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new RelationalExpr(2, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(11, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 48: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new RelationalExpr(3, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(11, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 47: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(11, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 46: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new EqualityExpr(1, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(10, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 45: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new EqualityExpr(0, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(10, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 44: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(10, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 43: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new LogicalExpr(1, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(9, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 42: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(9, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 41: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Expression)elementAt2value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Expression)elementAt0value;
      localObject1 = new LogicalExpr(0, (Expression)localObject2, (Expression)localObject3);
      localSymbol = new Symbol(8, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 40: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(8, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 39: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(2, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 38: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (Expression)elementAt1value;
      localObject1 = new Predicate((Expression)localObject2);
      localSymbol = new Symbol(5, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 37: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (Expression)elementAt1value;
      n = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Vector)elementAt0value;
      ((Vector)localObject3).insertElementAt(localObject2, 0);
      localObject1 = localObject3;
      localSymbol = new Symbol(35, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 36: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      Vector localVector2 = new Vector();
      localVector2.addElement(localObject2);
      localObject1 = localVector2;
      localSymbol = new Symbol(35, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 35: 
      localObject1 = null;
      localObject1 = new Integer(2);
      localSymbol = new Symbol(42, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 34: 
      localObject1 = null;
      localObject1 = new Integer(3);
      localSymbol = new Symbol(42, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 33: 
      localObject1 = null;
      localObject1 = new Integer(2);
      localSymbol = new Symbol(42, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 32: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (QName)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(34, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 31: 
      localObject1 = null;
      localObject1 = null;
      localSymbol = new Symbol(34, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 30: 
      localObject1 = null;
      localObject1 = new Integer(7);
      localSymbol = new Symbol(33, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 29: 
      localObject1 = null;
      localObject1 = new Integer(8);
      localSymbol = new Symbol(33, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 28: 
      localObject1 = null;
      localObject1 = new Integer(3);
      localSymbol = new Symbol(33, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 27: 
      localObject1 = null;
      localObject1 = new Integer(-1);
      localSymbol = new Symbol(33, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 26: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(33, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 25: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Integer)elementAt2value;
      i1 = elementAt1left;
      i3 = elementAt1right;
      localObject3 = (StepPattern)elementAt1value;
      i5 = elementAt0left;
      i7 = elementAt0right;
      localObject4 = (Vector)elementAt0value;
      localObject1 = (ProcessingInstructionPattern)((StepPattern)localObject3).setPredicates((Vector)localObject4);
      localSymbol = new Symbol(32, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 24: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (Integer)elementAt1value;
      i1 = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (StepPattern)elementAt0value;
      localObject1 = localObject3;
      localSymbol = new Symbol(32, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 23: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Integer)elementAt2value;
      i1 = elementAt1left;
      i3 = elementAt1right;
      localObject3 = elementAt1value;
      i5 = elementAt0left;
      i7 = elementAt0right;
      localObject4 = (Vector)elementAt0value;
      localObject1 = parser.createStepPattern(((Integer)localObject2).intValue(), localObject3, (Vector)localObject4);
      localSymbol = new Symbol(32, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 22: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (Integer)elementAt1value;
      i1 = elementAt0left;
      i3 = elementAt0right;
      localObject3 = elementAt0value;
      localObject1 = parser.createStepPattern(((Integer)localObject2).intValue(), localObject3, null);
      localSymbol = new Symbol(32, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 21: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (StepPattern)elementAt1value;
      i1 = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Vector)elementAt0value;
      localObject1 = (ProcessingInstructionPattern)((StepPattern)localObject2).setPredicates((Vector)localObject3);
      localSymbol = new Symbol(32, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 20: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (StepPattern)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(32, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 19: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = elementAt1value;
      i1 = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Vector)elementAt0value;
      localObject1 = parser.createStepPattern(3, localObject2, (Vector)localObject3);
      localSymbol = new Symbol(32, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 18: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = elementAt0value;
      localObject1 = parser.createStepPattern(3, localObject2, null);
      localSymbol = new Symbol(32, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 17: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (StepPattern)elementAt2value;
      i1 = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (RelativePathPattern)elementAt0value;
      localObject1 = new AncestorPattern((Pattern)localObject2, (RelativePathPattern)localObject3);
      localSymbol = new Symbol(31, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 16: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (StepPattern)elementAt2value;
      i1 = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (RelativePathPattern)elementAt0value;
      localObject1 = new ParentPattern((Pattern)localObject2, (RelativePathPattern)localObject3);
      localSymbol = new Symbol(31, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 15: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (StepPattern)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(31, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 14: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (String)elementAt1value;
      localObject1 = new ProcessingInstructionPattern((String)localObject2);
      localSymbol = new Symbol(30, elementAt3left, elementAt0right, localObject1);
      return localSymbol;
    case 13: 
      localObject1 = null;
      i = elementAt3left;
      j = elementAt3right;
      localObject2 = (String)elementAt3value;
      i1 = elementAt1left;
      i3 = elementAt1right;
      localObject3 = (String)elementAt1value;
      localObject1 = new KeyPattern((String)localObject2, (String)localObject3);
      localSymbol = new Symbol(27, elementAt5left, elementAt0right, localObject1);
      return localSymbol;
    case 12: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (String)elementAt1value;
      localObject1 = new IdPattern((String)localObject2);
      parser.setHasIdCall(true);
      localSymbol = new Symbol(27, elementAt3left, elementAt0right, localObject1);
      return localSymbol;
    case 11: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (RelativePathPattern)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(29, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 10: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (RelativePathPattern)elementAt0value;
      localObject1 = new AncestorPattern((RelativePathPattern)localObject2);
      localSymbol = new Symbol(29, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 9: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (IdKeyPattern)elementAt2value;
      i1 = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (RelativePathPattern)elementAt0value;
      localObject1 = new AncestorPattern((Pattern)localObject2, (RelativePathPattern)localObject3);
      localSymbol = new Symbol(29, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 8: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (IdKeyPattern)elementAt2value;
      i1 = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (RelativePathPattern)elementAt0value;
      localObject1 = new ParentPattern((Pattern)localObject2, (RelativePathPattern)localObject3);
      localSymbol = new Symbol(29, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 7: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (IdKeyPattern)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(29, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 6: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (RelativePathPattern)elementAt0value;
      localObject1 = new AbsolutePathPattern((RelativePathPattern)localObject2);
      localSymbol = new Symbol(29, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 5: 
      localObject1 = null;
      localObject1 = new AbsolutePathPattern(null);
      localSymbol = new Symbol(29, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 4: 
      localObject1 = null;
      i = elementAt2left;
      j = elementAt2right;
      localObject2 = (Pattern)elementAt2value;
      i1 = elementAt0left;
      i3 = elementAt0right;
      localObject3 = (Pattern)elementAt0value;
      localObject1 = new AlternativePattern((Pattern)localObject2, (Pattern)localObject3);
      localSymbol = new Symbol(28, elementAt2left, elementAt0right, localObject1);
      return localSymbol;
    case 3: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Pattern)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(28, elementAt0left, elementAt0right, localObject1);
      return localSymbol;
    case 2: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Expression)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(1, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 1: 
      localObject1 = null;
      i = elementAt0left;
      j = elementAt0right;
      localObject2 = (Pattern)elementAt0value;
      localObject1 = localObject2;
      localSymbol = new Symbol(1, elementAt1left, elementAt0right, localObject1);
      return localSymbol;
    case 0: 
      localObject1 = null;
      i = elementAt1left;
      j = elementAt1right;
      localObject2 = (SyntaxTreeNode)elementAt1value;
      localObject1 = localObject2;
      localSymbol = new Symbol(0, elementAt1left, elementAt0right, localObject1);
      paramlr_parser.done_parsing();
      return localSymbol;
    }
    throw new Exception("Invalid action number found in internal parse table");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CUP$XPathParser$actions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
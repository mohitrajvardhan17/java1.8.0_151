package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;

public abstract class Parser
{
  public static final String FAULT = "";
  protected static final int BUFFSIZE_READER = 512;
  protected static final int BUFFSIZE_PARSER = 128;
  public static final char EOS = '￿';
  private Pair mNoNS;
  private Pair mXml;
  private Map<String, Input> mEnt;
  private Map<String, Input> mPEnt;
  protected boolean mIsSAlone;
  protected boolean mIsSAloneSet;
  protected boolean mIsNSAware;
  protected int mPh = -1;
  protected static final int PH_BEFORE_DOC = -1;
  protected static final int PH_DOC_START = 0;
  protected static final int PH_MISC_DTD = 1;
  protected static final int PH_DTD = 2;
  protected static final int PH_DTD_MISC = 3;
  protected static final int PH_DOCELM = 4;
  protected static final int PH_DOCELM_MISC = 5;
  protected static final int PH_AFTER_DOC = 6;
  protected int mEvt;
  protected static final int EV_NULL = 0;
  protected static final int EV_ELM = 1;
  protected static final int EV_ELMS = 2;
  protected static final int EV_ELME = 3;
  protected static final int EV_TEXT = 4;
  protected static final int EV_WSPC = 5;
  protected static final int EV_PI = 6;
  protected static final int EV_CDAT = 7;
  protected static final int EV_COMM = 8;
  protected static final int EV_DTD = 9;
  protected static final int EV_ENT = 10;
  private char mESt;
  protected char[] mBuff = new char[''];
  protected int mBuffIdx;
  protected Pair mPref = pair(mPref);
  protected Pair mElm;
  protected Pair mAttL;
  protected Input mDoc;
  protected Input mInp;
  private char[] mChars;
  private int mChLen;
  private int mChIdx;
  protected Attrs mAttrs = new Attrs();
  private String[] mItems;
  private char mAttrIdx;
  private String mUnent;
  private Pair mDltd;
  private static final char[] NONS = new char[1];
  private static final char[] XML;
  private static final char[] XMLNS;
  private static final byte[] asctyp;
  private static final byte[] nmttyp;
  
  protected Parser()
  {
    mPref.name = "";
    mPref.value = "";
    mPref.chars = NONS;
    mNoNS = mPref;
    mPref = pair(mPref);
    mPref.name = "xml";
    mPref.value = "http://www.w3.org/XML/1998/namespace";
    mPref.chars = XML;
    mXml = mPref;
  }
  
  protected void init()
  {
    mUnent = null;
    mElm = null;
    mPref = mXml;
    mAttL = null;
    mPEnt = new HashMap();
    mEnt = new HashMap();
    mDoc = mInp;
    mChars = mInp.chars;
    mPh = 0;
  }
  
  protected void cleanup()
  {
    while (mAttL != null)
    {
      while (mAttL.list != null)
      {
        if (mAttL.list.list != null) {
          del(mAttL.list.list);
        }
        mAttL.list = del(mAttL.list);
      }
      mAttL = del(mAttL);
    }
    while (mElm != null) {
      mElm = del(mElm);
    }
    while (mPref != mXml) {
      mPref = del(mPref);
    }
    while (mInp != null) {
      pop();
    }
    if ((mDoc != null) && (mDoc.src != null)) {
      try
      {
        mDoc.src.close();
      }
      catch (IOException localIOException) {}
    }
    mPEnt = null;
    mEnt = null;
    mDoc = null;
    mPh = 6;
  }
  
  protected int step()
    throws Exception
  {
    mEvt = 0;
    int i = 0;
    while (mEvt == 0)
    {
      char c = mChIdx < mChLen ? mChars[(mChIdx++)] : getch();
      switch (i)
      {
      case 0: 
        if (c != '<')
        {
          bkch();
          mBuffIdx = -1;
          i = 1;
        }
        else
        {
          switch (getch())
          {
          case '/': 
            mEvt = 3;
            if (mElm == null) {
              panic("");
            }
            mBuffIdx = -1;
            bname(mIsNSAware);
            char[] arrayOfChar = mElm.chars;
            if (arrayOfChar.length == mBuffIdx + 1) {
              for (int j = 1; j <= mBuffIdx; j = (char)(j + 1)) {
                if (arrayOfChar[j] != mBuff[j]) {
                  panic("");
                }
              }
            } else {
              panic("");
            }
            if (wsskip() != '>') {
              panic("");
            }
            getch();
            break;
          case '!': 
            c = getch();
            bkch();
            switch (c)
            {
            case '-': 
              mEvt = 8;
              comm();
              break;
            case '[': 
              mEvt = 7;
              cdat();
              break;
            default: 
              mEvt = 9;
              dtd();
            }
            break;
          case '?': 
            mEvt = 6;
            pi();
            break;
          default: 
            bkch();
            mElm = pair(mElm);
            mElm.chars = qname(mIsNSAware);
            mElm.name = mElm.local();
            mElm.id = (mElm.next != null ? mElm.next.id : 0);
            mElm.num = 0;
            Pair localPair1 = find(mAttL, mElm.chars);
            mElm.list = (localPair1 != null ? list : null);
            mAttrIdx = '\000';
            Pair localPair2 = pair(null);
            num = 0;
            attr(localPair2);
            del(localPair2);
            mElm.value = (mIsNSAware ? rslv(mElm.chars) : null);
            switch (wsskip())
            {
            case '>': 
              getch();
              mEvt = 2;
              break;
            case '/': 
              getch();
              if (getch() != '>') {
                panic("");
              }
              mEvt = 1;
              break;
            default: 
              panic("");
            }
            break;
          }
        }
        break;
      case 1: 
        switch (c)
        {
        case '\t': 
        case '\n': 
        case ' ': 
          bappend(c);
          break;
        case '\r': 
          if (getch() != '\n') {
            bkch();
          }
          bappend('\n');
          break;
        case '<': 
          mEvt = 5;
          bkch();
          bflash_ws();
          break;
        default: 
          bkch();
          i = 2;
        }
        break;
      case 2: 
        switch (c)
        {
        case '&': 
          if (mUnent == null)
          {
            if ((mUnent = ent('x')) != null)
            {
              mEvt = 4;
              bkch();
              setch('&');
              bflash();
            }
          }
          else
          {
            mEvt = 10;
            skippedEnt(mUnent);
            mUnent = null;
          }
          break;
        case '<': 
          mEvt = 4;
          bkch();
          bflash();
          break;
        case '\r': 
          if (getch() != '\n') {
            bkch();
          }
          bappend('\n');
          break;
        case '￿': 
          panic("");
        default: 
          bappend(c);
        }
        break;
      default: 
        panic("");
      }
    }
    return mEvt;
  }
  
  private void dtd()
    throws Exception
  {
    Object localObject = null;
    String str = null;
    Pair localPair = null;
    if ("DOCTYPE".equals(name(false)) != true) {
      panic("");
    }
    mPh = 2;
    int i = 0;
    while (i >= 0)
    {
      char c = getch();
      switch (i)
      {
      case 0: 
        if (chtyp(c) != ' ')
        {
          bkch();
          str = name(mIsNSAware);
          wsskip();
          i = 1;
        }
        break;
      case 1: 
        switch (chtyp(c))
        {
        case 'A': 
          bkch();
          localPair = pubsys(' ');
          i = 2;
          docType(str, name, value);
          break;
        case '[': 
          bkch();
          i = 2;
          docType(str, null, null);
          break;
        case '>': 
          bkch();
          i = 3;
          docType(str, null, null);
          break;
        default: 
          panic("");
        }
        break;
      case 2: 
        switch (chtyp(c))
        {
        case '[': 
          dtdsub();
          i = 3;
          break;
        case '>': 
          bkch();
          i = 3;
          break;
        case ' ': 
          break;
        default: 
          panic("");
        }
        break;
      case 3: 
        switch (chtyp(c))
        {
        case '>': 
          if (localPair != null)
          {
            InputSource localInputSource = resolveEnt(str, name, value);
            if (localInputSource != null)
            {
              if (!mIsSAlone)
              {
                bkch();
                setch(']');
                push(new Input(512));
                setinp(localInputSource);
                mInp.pubid = name;
                mInp.sysid = value;
                dtdsub();
              }
              else
              {
                skippedEnt("[dtd]");
                if (localInputSource.getCharacterStream() != null) {
                  try
                  {
                    localInputSource.getCharacterStream().close();
                  }
                  catch (IOException localIOException1) {}
                }
                if (localInputSource.getByteStream() != null) {
                  try
                  {
                    localInputSource.getByteStream().close();
                  }
                  catch (IOException localIOException2) {}
                }
              }
            }
            else {
              skippedEnt("[dtd]");
            }
            del(localPair);
          }
          i = -1;
          break;
        case ' ': 
          break;
        default: 
          panic("");
        }
        break;
      default: 
        panic("");
      }
    }
  }
  
  private void dtdsub()
    throws Exception
  {
    int i = 0;
    while (i >= 0)
    {
      char c = getch();
      switch (i)
      {
      case 0: 
        switch (chtyp(c))
        {
        case '<': 
          c = getch();
          switch (c)
          {
          case '?': 
            pi();
            break;
          case '!': 
            c = getch();
            bkch();
            if (c == '-')
            {
              comm();
            }
            else
            {
              bntok();
              switch (bkeyword())
              {
              case 'n': 
                dtdent();
                break;
              case 'a': 
                dtdattl();
                break;
              case 'e': 
                dtdelm();
                break;
              case 'o': 
                dtdnot();
                break;
              default: 
                panic("");
              }
              i = 1;
            }
            break;
          default: 
            panic("");
          }
          break;
        case '%': 
          pent(' ');
          break;
        case ']': 
          i = -1;
          break;
        case ' ': 
          break;
        case 'Z': 
          if (getch() != ']') {
            panic("");
          }
          i = -1;
          break;
        default: 
          panic("");
        }
        break;
      case 1: 
        switch (c)
        {
        case '>': 
          i = 0;
          break;
        case '\t': 
        case '\n': 
        case '\r': 
        case ' ': 
          break;
        default: 
          panic("");
        }
        break;
      default: 
        panic("");
      }
    }
  }
  
  private void dtdent()
    throws Exception
  {
    String str = null;
    char[] arrayOfChar = null;
    Input localInput = null;
    Pair localPair = null;
    int i = 0;
    while (i >= 0)
    {
      char c = getch();
      switch (i)
      {
      case 0: 
        switch (chtyp(c))
        {
        case ' ': 
          break;
        case '%': 
          c = getch();
          bkch();
          if (chtyp(c) == ' ')
          {
            wsskip();
            str = name(false);
            switch (chtyp(wsskip()))
            {
            case 'A': 
              localPair = pubsys(' ');
              if (wsskip() == '>')
              {
                if (!mPEnt.containsKey(str))
                {
                  localInput = new Input();
                  pubid = name;
                  sysid = value;
                  mPEnt.put(str, localInput);
                }
              }
              else {
                panic("");
              }
              del(localPair);
              i = -1;
              break;
            case '"': 
            case '\'': 
              bqstr('d');
              arrayOfChar = new char[mBuffIdx + 1];
              System.arraycopy(mBuff, 1, arrayOfChar, 1, arrayOfChar.length - 1);
              arrayOfChar[0] = ' ';
              if (!mPEnt.containsKey(str))
              {
                localInput = new Input(arrayOfChar);
                pubid = mInp.pubid;
                sysid = mInp.sysid;
                xmlenc = mInp.xmlenc;
                xmlver = mInp.xmlver;
                mPEnt.put(str, localInput);
              }
              i = -1;
              break;
            default: 
              panic("");
              break;
            }
          }
          else
          {
            pent(' ');
          }
          break;
        default: 
          bkch();
          str = name(false);
          i = 1;
        }
        break;
      case 1: 
        switch (chtyp(c))
        {
        case '"': 
        case '\'': 
          bkch();
          bqstr('d');
          if (mEnt.get(str) == null)
          {
            arrayOfChar = new char[mBuffIdx];
            System.arraycopy(mBuff, 1, arrayOfChar, 0, arrayOfChar.length);
            if (!mEnt.containsKey(str))
            {
              localInput = new Input(arrayOfChar);
              pubid = mInp.pubid;
              sysid = mInp.sysid;
              xmlenc = mInp.xmlenc;
              xmlver = mInp.xmlver;
              mEnt.put(str, localInput);
            }
          }
          i = -1;
          break;
        case 'A': 
          bkch();
          localPair = pubsys(' ');
          switch (wsskip())
          {
          case '>': 
            if (mEnt.containsKey(str)) {
              break label722;
            }
            localInput = new Input();
            pubid = name;
            sysid = value;
            mEnt.put(str, localInput);
            break;
          case 'N': 
            if ("NDATA".equals(name(false)) == true)
            {
              wsskip();
              unparsedEntDecl(str, name, value, name(false));
            }
            break;
          }
          panic("");
          del(localPair);
          i = -1;
          break;
        case ' ': 
          break;
        default: 
          panic("");
        }
        break;
      default: 
        label722:
        panic("");
      }
    }
  }
  
  private void dtdelm()
    throws Exception
  {
    wsskip();
    name(mIsNSAware);
    for (;;)
    {
      int i = getch();
      switch (i)
      {
      case 62: 
        bkch();
        return;
      case 65535: 
        panic("");
      }
    }
  }
  
  private void dtdattl()
    throws Exception
  {
    char[] arrayOfChar = null;
    Pair localPair = null;
    int i = 0;
    while (i >= 0)
    {
      char c = getch();
      switch (i)
      {
      case 0: 
        switch (chtyp(c))
        {
        case ':': 
        case 'A': 
        case 'X': 
        case '_': 
        case 'a': 
          bkch();
          arrayOfChar = qname(mIsNSAware);
          localPair = find(mAttL, arrayOfChar);
          if (localPair == null)
          {
            localPair = pair(mAttL);
            chars = arrayOfChar;
            mAttL = localPair;
          }
          i = 1;
          break;
        case ' ': 
          break;
        case '%': 
          pent(' ');
          break;
        default: 
          panic("");
        }
        break;
      case 1: 
        switch (chtyp(c))
        {
        case ':': 
        case 'A': 
        case 'X': 
        case '_': 
        case 'a': 
          bkch();
          dtdatt(localPair);
          if (wsskip() == '>') {
            return;
          }
          break;
        case ' ': 
          break;
        case '%': 
          pent(' ');
          break;
        default: 
          panic("");
        }
        break;
      default: 
        panic("");
      }
    }
  }
  
  private void dtdatt(Pair paramPair)
    throws Exception
  {
    char[] arrayOfChar = null;
    Pair localPair = null;
    int i = 0;
    while (i >= 0)
    {
      char c = getch();
      switch (i)
      {
      case 0: 
        switch (chtyp(c))
        {
        case ':': 
        case 'A': 
        case 'X': 
        case '_': 
        case 'a': 
          bkch();
          arrayOfChar = qname(mIsNSAware);
          localPair = find(list, arrayOfChar);
          if (localPair == null)
          {
            localPair = pair(list);
            chars = arrayOfChar;
            list = localPair;
          }
          else
          {
            localPair = pair(null);
            chars = arrayOfChar;
            id = 99;
          }
          wsskip();
          i = 1;
          break;
        case '%': 
          pent(' ');
          break;
        case ' ': 
          break;
        default: 
          panic("");
        }
        break;
      case 1: 
        switch (chtyp(c))
        {
        case '(': 
          id = 117;
          i = 2;
          break;
        case '%': 
          pent(' ');
          break;
        case ' ': 
          break;
        default: 
          bkch();
          bntok();
          id = bkeyword();
          switch (id)
          {
          case 111: 
            if (wsskip() != '(') {
              panic("");
            }
            c = getch();
            i = 2;
            break;
          case 78: 
          case 82: 
          case 84: 
          case 99: 
          case 105: 
          case 110: 
          case 114: 
          case 116: 
            wsskip();
            i = 4;
            break;
          default: 
            panic("");
          }
          break;
        }
        break;
      case 2: 
        switch (chtyp(c))
        {
        case '-': 
        case '.': 
        case ':': 
        case 'A': 
        case 'X': 
        case '_': 
        case 'a': 
        case 'd': 
          bkch();
          switch (id)
          {
          case 117: 
            bntok();
            break;
          case 111: 
            mBuffIdx = -1;
            bname(false);
            break;
          default: 
            panic("");
          }
          wsskip();
          i = 3;
          break;
        case '%': 
          pent(' ');
          break;
        case ' ': 
          break;
        default: 
          panic("");
        }
        break;
      case 3: 
        switch (c)
        {
        case ')': 
          wsskip();
          i = 4;
          break;
        case '|': 
          wsskip();
          switch (id)
          {
          case 117: 
            bntok();
            break;
          case 111: 
            mBuffIdx = -1;
            bname(false);
            break;
          default: 
            panic("");
          }
          wsskip();
          break;
        case '%': 
          pent(' ');
          break;
        default: 
          panic("");
        }
        break;
      case 4: 
        switch (c)
        {
        case '#': 
          bntok();
          switch (bkeyword())
          {
          case 'F': 
            switch (wsskip())
            {
            case '"': 
            case '\'': 
              i = 5;
              break;
            case '￿': 
              panic("");
            default: 
              i = -1;
            }
            break;
          case 'I': 
          case 'Q': 
            i = -1;
            break;
          default: 
            panic("");
          }
          break;
        case '"': 
        case '\'': 
          bkch();
          i = 5;
          break;
        case '\t': 
        case '\n': 
        case '\r': 
        case ' ': 
          break;
        case '%': 
          pent(' ');
          break;
        default: 
          bkch();
          i = -1;
        }
        break;
      case 5: 
        switch (c)
        {
        case '"': 
        case '\'': 
          bkch();
          bqstr('d');
          list = pair(null);
          list.chars = new char[chars.length + mBuffIdx + 3];
          System.arraycopy(chars, 1, list.chars, 0, chars.length - 1);
          list.chars[(chars.length - 1)] = '=';
          list.chars[chars.length] = c;
          System.arraycopy(mBuff, 1, list.chars, chars.length + 1, mBuffIdx);
          list.chars[(chars.length + mBuffIdx + 1)] = c;
          list.chars[(chars.length + mBuffIdx + 2)] = ' ';
          i = -1;
          break;
        default: 
          panic("");
        }
        break;
      default: 
        panic("");
      }
    }
  }
  
  private void dtdnot()
    throws Exception
  {
    wsskip();
    String str = name(false);
    wsskip();
    Pair localPair = pubsys('N');
    notDecl(str, name, value);
    del(localPair);
  }
  
  private void attr(Pair paramPair)
    throws Exception
  {
    switch (wsskip())
    {
    case '/': 
    case '>': 
      if ((num & 0x2) == 0)
      {
        num |= 0x2;
        localObject1 = mInp;
        for (localObject2 = mElm.list; localObject2 != null; localObject2 = next) {
          if (list != null)
          {
            localPair = find(next, chars);
            if (localPair == null) {
              push(new Input(list.chars));
            }
          }
        }
        if (mInp != localObject1)
        {
          attr(paramPair);
          return;
        }
      }
      mAttrs.setLength(mAttrIdx);
      mItems = mAttrs.mItems;
      return;
    case '￿': 
      panic("");
    }
    chars = qname(mIsNSAware);
    name = paramPair.local();
    Object localObject1 = atype(paramPair);
    wsskip();
    if (getch() != '=') {
      panic("");
    }
    bqstr((char)id);
    Object localObject2 = new String(mBuff, 1, mBuffIdx);
    Pair localPair = pair(paramPair);
    num &= 0xFFFFFFFE;
    if ((!mIsNSAware) || (!isdecl(paramPair, (String)localObject2)))
    {
      mAttrIdx = ((char)(mAttrIdx + '\001'));
      attr(localPair);
      mAttrIdx = ((char)(mAttrIdx - '\001'));
      int i = (char)(mAttrIdx << '\003');
      mItems[(i + 1)] = paramPair.qname();
      mItems[(i + 2)] = (mIsNSAware ? name : "");
      mItems[(i + 3)] = localObject2;
      mItems[(i + 4)] = localObject1;
      switch (num & 0x3)
      {
      case 0: 
        mItems[(i + 5)] = null;
        break;
      case 1: 
        mItems[(i + 5)] = "d";
        break;
      default: 
        mItems[(i + 5)] = "D";
      }
      mItems[(i + 0)] = (chars[0] != 0 ? rslv(chars) : "");
    }
    else
    {
      newPrefix();
      attr(localPair);
    }
    del(localPair);
  }
  
  private String atype(Pair paramPair)
    throws Exception
  {
    id = 99;
    Pair localPair;
    if ((mElm.list == null) || ((localPair = find(mElm.list, chars)) == null)) {
      return "CDATA";
    }
    num |= 0x1;
    id = 105;
    switch (id)
    {
    case 105: 
      return "ID";
    case 114: 
      return "IDREF";
    case 82: 
      return "IDREFS";
    case 110: 
      return "ENTITY";
    case 78: 
      return "ENTITIES";
    case 116: 
      return "NMTOKEN";
    case 84: 
      return "NMTOKENS";
    case 117: 
      return "NMTOKEN";
    case 111: 
      return "NOTATION";
    case 99: 
      id = 99;
      return "CDATA";
    }
    panic("");
    return null;
  }
  
  private void comm()
    throws Exception
  {
    if (mPh == 0) {
      mPh = 1;
    }
    mBuffIdx = -1;
    int j = 0;
    while (j >= 0)
    {
      int i = mChIdx < mChLen ? mChars[(mChIdx++)] : getch();
      if (i == 65535) {
        panic("");
      }
      switch (j)
      {
      case 0: 
        if (i == 45) {
          j = 1;
        } else {
          panic("");
        }
        break;
      case 1: 
        if (i == 45) {
          j = 2;
        } else {
          panic("");
        }
        break;
      case 2: 
        switch (i)
        {
        case 45: 
          j = 3;
          break;
        default: 
          bappend(i);
        }
        break;
      case 3: 
        switch (i)
        {
        case 45: 
          j = 4;
          break;
        default: 
          bappend('-');
          bappend(i);
          j = 2;
        }
        break;
      case 4: 
        if (i == 62)
        {
          comm(mBuff, mBuffIdx + 1);
          j = -1;
        }
        break;
      default: 
        panic("");
      }
    }
  }
  
  private void pi()
    throws Exception
  {
    String str = null;
    mBuffIdx = -1;
    int j = 0;
    while (j >= 0)
    {
      int i = getch();
      if (i == 65535) {
        panic("");
      }
      switch (j)
      {
      case 0: 
        switch (chtyp(i))
        {
        case ':': 
        case 'A': 
        case 'X': 
        case '_': 
        case 'a': 
          bkch();
          str = name(false);
          if ((str.length() == 0) || (mXml.name.equals(str.toLowerCase()) == true)) {
            panic("");
          }
          if (mPh == 0) {
            mPh = 1;
          }
          wsskip();
          j = 1;
          mBuffIdx = -1;
          break;
        default: 
          panic("");
        }
        break;
      case 1: 
        switch (i)
        {
        case 63: 
          j = 2;
          break;
        default: 
          bappend(i);
        }
        break;
      case 2: 
        switch (i)
        {
        case 62: 
          pi(str, new String(mBuff, 0, mBuffIdx + 1));
          j = -1;
          break;
        case 63: 
          bappend('?');
          break;
        default: 
          bappend('?');
          bappend(i);
          j = 1;
        }
        break;
      default: 
        panic("");
      }
    }
  }
  
  private void cdat()
    throws Exception
  {
    mBuffIdx = -1;
    int i = 0;
    while (i >= 0)
    {
      char c = getch();
      switch (i)
      {
      case 0: 
        if (c == '[') {
          i = 1;
        } else {
          panic("");
        }
        break;
      case 1: 
        if (chtyp(c) == 'A')
        {
          bappend(c);
        }
        else
        {
          if ("CDATA".equals(new String(mBuff, 0, mBuffIdx + 1)) != true) {
            panic("");
          }
          bkch();
          i = 2;
        }
        break;
      case 2: 
        if (c != '[') {
          panic("");
        }
        mBuffIdx = -1;
        i = 3;
        break;
      case 3: 
        if (c != ']') {
          bappend(c);
        } else {
          i = 4;
        }
        break;
      case 4: 
        if (c != ']')
        {
          bappend(']');
          bappend(c);
          i = 3;
        }
        else
        {
          i = 5;
        }
        break;
      case 5: 
        switch (c)
        {
        case ']': 
          bappend(']');
          break;
        case '>': 
          bflash();
          i = -1;
          break;
        default: 
          bappend(']');
          bappend(']');
          bappend(c);
          i = 3;
        }
        break;
      default: 
        panic("");
      }
    }
  }
  
  protected String name(boolean paramBoolean)
    throws Exception
  {
    mBuffIdx = -1;
    bname(paramBoolean);
    return new String(mBuff, 1, mBuffIdx);
  }
  
  protected char[] qname(boolean paramBoolean)
    throws Exception
  {
    mBuffIdx = -1;
    bname(paramBoolean);
    char[] arrayOfChar = new char[mBuffIdx + 1];
    System.arraycopy(mBuff, 0, arrayOfChar, 0, mBuffIdx + 1);
    return arrayOfChar;
  }
  
  private void pubsys(Input paramInput)
    throws Exception
  {
    Pair localPair = pubsys(' ');
    pubid = name;
    sysid = value;
    del(localPair);
  }
  
  private Pair pubsys(char paramChar)
    throws Exception
  {
    Pair localPair = pair(null);
    String str = name(false);
    if ("PUBLIC".equals(str) == true)
    {
      bqstr('i');
      name = new String(mBuff, 1, mBuffIdx);
      switch (wsskip())
      {
      case '"': 
      case '\'': 
        bqstr(' ');
        value = new String(mBuff, 1, mBuffIdx);
        break;
      case '￿': 
        panic("");
      default: 
        if (paramChar != 'N') {
          panic("");
        }
        value = null;
      }
      return localPair;
    }
    if ("SYSTEM".equals(str) == true)
    {
      name = null;
      bqstr(' ');
      value = new String(mBuff, 1, mBuffIdx);
      return localPair;
    }
    panic("");
    return null;
  }
  
  protected String eqstr(char paramChar)
    throws Exception
  {
    if (paramChar == '=')
    {
      wsskip();
      if (getch() != '=') {
        panic("");
      }
    }
    bqstr(paramChar == '=' ? '-' : paramChar);
    return new String(mBuff, 1, mBuffIdx);
  }
  
  private String ent(char paramChar)
    throws Exception
  {
    int i = mBuffIdx + 1;
    Input localInput = null;
    String str = null;
    mESt = 'Ā';
    bappend('&');
    int j = 0;
    while (j >= 0)
    {
      char c = mChIdx < mChLen ? mChars[(mChIdx++)] : getch();
      switch (j)
      {
      case 0: 
      case 1: 
        switch (chtyp(c))
        {
        case '-': 
        case '.': 
        case 'd': 
          if (j != 1) {
            panic("");
          }
        case 'A': 
        case 'X': 
        case '_': 
        case 'a': 
          bappend(c);
          eappend(c);
          j = 1;
          break;
        case ':': 
          if (mIsNSAware) {
            panic("");
          }
          bappend(c);
          eappend(c);
          j = 1;
          break;
        case ';': 
          if (mESt < 'Ā')
          {
            mBuffIdx = (i - 1);
            bappend(mESt);
            j = -1;
          }
          else if (mPh == 2)
          {
            bappend(';');
            j = -1;
          }
          else
          {
            str = new String(mBuff, i + 1, mBuffIdx - i);
            localInput = (Input)mEnt.get(str);
            mBuffIdx = (i - 1);
            if (localInput != null)
            {
              if (chars == null)
              {
                InputSource localInputSource = resolveEnt(str, pubid, sysid);
                if (localInputSource != null)
                {
                  push(new Input(512));
                  setinp(localInputSource);
                  mInp.pubid = pubid;
                  mInp.sysid = sysid;
                  str = null;
                }
                else if (paramChar != 'x')
                {
                  panic("");
                }
              }
              else
              {
                push(localInput);
                str = null;
              }
            }
            else if (paramChar != 'x') {
              panic("");
            }
            j = -1;
          }
          break;
        case '#': 
          if (j != 0) {
            panic("");
          }
          j = 2;
          break;
        default: 
          panic("");
        }
        break;
      case 2: 
        switch (chtyp(c))
        {
        case 'd': 
          bappend(c);
          break;
        case ';': 
          try
          {
            int k = Integer.parseInt(new String(mBuff, i + 1, mBuffIdx - i), 10);
            if (k >= 65535) {
              panic("");
            }
            c = (char)k;
          }
          catch (NumberFormatException localNumberFormatException1)
          {
            panic("");
          }
          mBuffIdx = (i - 1);
          if ((c == ' ') || (mInp.next != null)) {
            bappend(c, paramChar);
          } else {
            bappend(c);
          }
          j = -1;
          break;
        case 'a': 
          if ((mBuffIdx == i) && (c == 'x')) {
            j = 3;
          }
          break;
        default: 
          panic("");
        }
        break;
      case 3: 
        switch (chtyp(c))
        {
        case 'A': 
        case 'a': 
        case 'd': 
          bappend(c);
          break;
        case ';': 
          try
          {
            int m = Integer.parseInt(new String(mBuff, i + 1, mBuffIdx - i), 16);
            if (m >= 65535) {
              panic("");
            }
            c = (char)m;
          }
          catch (NumberFormatException localNumberFormatException2)
          {
            panic("");
          }
          mBuffIdx = (i - 1);
          if ((c == ' ') || (mInp.next != null)) {
            bappend(c, paramChar);
          } else {
            bappend(c);
          }
          j = -1;
          break;
        default: 
          panic("");
        }
        break;
      default: 
        panic("");
      }
    }
    return str;
  }
  
  private void pent(char paramChar)
    throws Exception
  {
    int i = mBuffIdx + 1;
    Input localInput = null;
    String str = null;
    bappend('%');
    if (mPh != 2) {
      return;
    }
    bname(false);
    str = new String(mBuff, i + 2, mBuffIdx - i - 1);
    if (getch() != ';') {
      panic("");
    }
    localInput = (Input)mPEnt.get(str);
    mBuffIdx = (i - 1);
    if (localInput != null)
    {
      if (chars == null)
      {
        InputSource localInputSource = resolveEnt(str, pubid, sysid);
        if (localInputSource != null)
        {
          if (paramChar != '-') {
            bappend(' ');
          }
          push(new Input(512));
          setinp(localInputSource);
          mInp.pubid = pubid;
          mInp.sysid = sysid;
        }
        else
        {
          skippedEnt("%" + str);
        }
      }
      else
      {
        if (paramChar == '-')
        {
          chIdx = 1;
        }
        else
        {
          bappend(' ');
          chIdx = 0;
        }
        push(localInput);
      }
    }
    else {
      skippedEnt("%" + str);
    }
  }
  
  private boolean isdecl(Pair paramPair, String paramString)
  {
    if (chars[0] == 0)
    {
      if ("xmlns".equals(name) == true)
      {
        mPref = pair(mPref);
        mPref.list = mElm;
        mPref.value = paramString;
        mPref.name = "";
        mPref.chars = NONS;
        mElm.num += 1;
        return true;
      }
    }
    else if (paramPair.eqpref(XMLNS) == true)
    {
      int i = name.length();
      mPref = pair(mPref);
      mPref.list = mElm;
      mPref.value = paramString;
      mPref.name = name;
      mPref.chars = new char[i + 1];
      mPref.chars[0] = ((char)(i + 1));
      name.getChars(0, i, mPref.chars, 1);
      mElm.num += 1;
      return true;
    }
    return false;
  }
  
  private String rslv(char[] paramArrayOfChar)
    throws Exception
  {
    for (Pair localPair = mPref; localPair != null; localPair = next) {
      if (localPair.eqpref(paramArrayOfChar) == true) {
        return value;
      }
    }
    if (paramArrayOfChar[0] == '\001') {
      for (localPair = mPref; localPair != null; localPair = next) {
        if (chars[0] == 0) {
          return value;
        }
      }
    }
    panic("");
    return null;
  }
  
  protected char wsskip()
    throws IOException
  {
    char c;
    for (;;)
    {
      c = mChIdx < mChLen ? mChars[(mChIdx++)] : getch();
      if (c < '') {
        if (nmttyp[c] != 3) {
          break;
        }
      }
    }
    mChIdx -= 1;
    return c;
  }
  
  protected abstract void docType(String paramString1, String paramString2, String paramString3)
    throws SAXException;
  
  protected abstract void comm(char[] paramArrayOfChar, int paramInt);
  
  protected abstract void pi(String paramString1, String paramString2)
    throws Exception;
  
  protected abstract void newPrefix()
    throws Exception;
  
  protected abstract void skippedEnt(String paramString)
    throws Exception;
  
  protected abstract InputSource resolveEnt(String paramString1, String paramString2, String paramString3)
    throws Exception;
  
  protected abstract void notDecl(String paramString1, String paramString2, String paramString3)
    throws Exception;
  
  protected abstract void unparsedEntDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws Exception;
  
  protected abstract void panic(String paramString)
    throws Exception;
  
  private void bname(boolean paramBoolean)
    throws Exception
  {
    mBuffIdx += 1;
    int k = mBuffIdx;
    int m = k;
    int n = k + 1;
    int i1 = n;
    int i2 = mChIdx;
    int i3 = (short)(paramBoolean == true ? 0 : 2);
    for (;;)
    {
      if (mChIdx >= mChLen)
      {
        bcopy(i2, i1);
        getch();
        mChIdx -= 1;
        i2 = mChIdx;
        i1 = n;
      }
      int i = mChars[(mChIdx++)];
      int j = 0;
      if (i < 128) {
        j = (char)nmttyp[i];
      } else if (i == 65535) {
        panic("");
      }
      switch (i3)
      {
      case 0: 
      case 2: 
        switch (j)
        {
        case 0: 
          n++;
          i3 = (short)(i3 + 1);
          break;
        case 1: 
          mChIdx -= 1;
          i3 = (short)(i3 + 1);
          break;
        default: 
          panic("");
        }
        break;
      case 1: 
      case 3: 
        switch (j)
        {
        case 0: 
        case 2: 
          n++;
          break;
        case 1: 
          n++;
          if (paramBoolean == true)
          {
            if (m != k) {
              panic("");
            }
            m = n - 1;
            if (i3 == 1) {
              i3 = 2;
            }
          }
          break;
        default: 
          mChIdx -= 1;
          bcopy(i2, i1);
          mBuff[k] = ((char)(m - k));
          return;
        }
        break;
      default: 
        panic("");
      }
    }
  }
  
  private void bntok()
    throws Exception
  {
    mBuffIdx = -1;
    bappend('\000');
    for (;;)
    {
      char c = getch();
      switch (chtyp(c))
      {
      case '-': 
      case '.': 
      case ':': 
      case 'A': 
      case 'X': 
      case '_': 
      case 'a': 
      case 'd': 
        bappend(c);
      }
    }
    panic("");
    bkch();
  }
  
  private char bkeyword()
    throws Exception
  {
    String str = new String(mBuff, 1, mBuffIdx);
    switch (str.length())
    {
    case 2: 
      return "ID".equals(str) == true ? 'i' : '?';
    case 5: 
      switch (mBuff[1])
      {
      case 'I': 
        return "IDREF".equals(str) == true ? 'r' : '?';
      case 'C': 
        return "CDATA".equals(str) == true ? 'c' : '?';
      case 'F': 
        return "FIXED".equals(str) == true ? 'F' : '?';
      }
      break;
    case 6: 
      switch (mBuff[1])
      {
      case 'I': 
        return "IDREFS".equals(str) == true ? 'R' : '?';
      case 'E': 
        return "ENTITY".equals(str) == true ? 'n' : '?';
      }
      break;
    case 7: 
      switch (mBuff[1])
      {
      case 'I': 
        return "IMPLIED".equals(str) == true ? 'I' : '?';
      case 'N': 
        return "NMTOKEN".equals(str) == true ? 't' : '?';
      case 'A': 
        return "ATTLIST".equals(str) == true ? 'a' : '?';
      case 'E': 
        return "ELEMENT".equals(str) == true ? 'e' : '?';
      }
      break;
    case 8: 
      switch (mBuff[2])
      {
      case 'N': 
        return "ENTITIES".equals(str) == true ? 'N' : '?';
      case 'M': 
        return "NMTOKENS".equals(str) == true ? 'T' : '?';
      case 'O': 
        return "NOTATION".equals(str) == true ? 'o' : '?';
      case 'E': 
        return "REQUIRED".equals(str) == true ? 'Q' : '?';
      }
      break;
    }
    return '?';
  }
  
  private void bqstr(char paramChar)
    throws Exception
  {
    Input localInput = mInp;
    mBuffIdx = -1;
    bappend('\000');
    int i = 0;
    while (i >= 0)
    {
      char c = mChIdx < mChLen ? mChars[(mChIdx++)] : getch();
      switch (i)
      {
      case 0: 
        switch (c)
        {
        case '\t': 
        case '\n': 
        case '\r': 
        case ' ': 
          break;
        case '\'': 
          i = 2;
          break;
        case '"': 
          i = 3;
          break;
        default: 
          panic("");
        }
        break;
      case 2: 
      case 3: 
        switch (c)
        {
        case '\'': 
          if ((i == 2) && (mInp == localInput)) {
            i = -1;
          } else {
            bappend(c);
          }
          break;
        case '"': 
          if ((i == 3) && (mInp == localInput)) {
            i = -1;
          } else {
            bappend(c);
          }
          break;
        case '&': 
          if (paramChar != 'd') {
            ent(paramChar);
          } else {
            bappend(c);
          }
          break;
        case '%': 
          if (paramChar == 'd') {
            pent('-');
          } else {
            bappend(c);
          }
          break;
        case '<': 
          if ((paramChar == '-') || (paramChar == 'd')) {
            bappend(c);
          } else {
            panic("");
          }
          break;
        case '￿': 
          panic("");
        case '\r': 
          if ((paramChar != ' ') && (mInp.next == null))
          {
            if (getch() != '\n') {
              bkch();
            }
            c = '\n';
          }
        default: 
          bappend(c, paramChar);
        }
        break;
      case 1: 
      default: 
        panic("");
      }
    }
    if ((paramChar == 'i') && (mBuff[mBuffIdx] == ' ')) {
      mBuffIdx -= 1;
    }
  }
  
  protected abstract void bflash()
    throws Exception;
  
  protected abstract void bflash_ws()
    throws Exception;
  
  private void bappend(char paramChar1, char paramChar2)
  {
    switch (paramChar2)
    {
    case 'i': 
      switch (paramChar1)
      {
      case '\t': 
      case '\n': 
      case '\r': 
      case ' ': 
        if ((mBuffIdx > 0) && (mBuff[mBuffIdx] != ' ')) {
          bappend(' ');
        }
        return;
      }
      break;
    case 'c': 
      switch (paramChar1)
      {
      case '\t': 
      case '\n': 
      case '\r': 
        paramChar1 = ' ';
      }
      break;
    }
    mBuffIdx += 1;
    if (mBuffIdx < mBuff.length)
    {
      mBuff[mBuffIdx] = paramChar1;
    }
    else
    {
      mBuffIdx -= 1;
      bappend(paramChar1);
    }
  }
  
  private void bappend(char paramChar)
  {
    try
    {
      mBuff[(++mBuffIdx)] = paramChar;
    }
    catch (Exception localException)
    {
      char[] arrayOfChar = new char[mBuff.length << 1];
      System.arraycopy(mBuff, 0, arrayOfChar, 0, mBuff.length);
      mBuff = arrayOfChar;
      mBuff[mBuffIdx] = paramChar;
    }
  }
  
  private void bcopy(int paramInt1, int paramInt2)
  {
    int i = mChIdx - paramInt1;
    if (paramInt2 + i + 1 >= mBuff.length)
    {
      char[] arrayOfChar = new char[mBuff.length + i];
      System.arraycopy(mBuff, 0, arrayOfChar, 0, mBuff.length);
      mBuff = arrayOfChar;
    }
    System.arraycopy(mChars, paramInt1, mBuff, paramInt2, i);
    mBuffIdx += i;
  }
  
  private void eappend(char paramChar)
  {
    switch (mESt)
    {
    case 'Ā': 
      switch (paramChar)
      {
      case 'l': 
        mESt = 'ā';
        break;
      case 'g': 
        mESt = 'Ă';
        break;
      case 'a': 
        mESt = 'ă';
        break;
      case 'q': 
        mESt = 'ć';
        break;
      default: 
        mESt = 'Ȁ';
      }
      break;
    case 'ā': 
      mESt = (paramChar == 't' ? '<' : 'Ȁ');
      break;
    case 'Ă': 
      mESt = (paramChar == 't' ? '>' : 'Ȁ');
      break;
    case 'ă': 
      switch (paramChar)
      {
      case 'm': 
        mESt = 'Ą';
        break;
      case 'p': 
        mESt = 'ą';
        break;
      default: 
        mESt = 'Ȁ';
      }
      break;
    case 'Ą': 
      mESt = (paramChar == 'p' ? '&' : 'Ȁ');
      break;
    case 'ą': 
      mESt = (paramChar == 'o' ? 'Ć' : 'Ȁ');
      break;
    case 'Ć': 
      mESt = (paramChar == 's' ? '\'' : 'Ȁ');
      break;
    case 'ć': 
      mESt = (paramChar == 'u' ? 'Ĉ' : 'Ȁ');
      break;
    case 'Ĉ': 
      mESt = (paramChar == 'o' ? 'ĉ' : 'Ȁ');
      break;
    case 'ĉ': 
      mESt = (paramChar == 't' ? '"' : 'Ȁ');
      break;
    case '"': 
    case '&': 
    case '\'': 
    case '<': 
    case '>': 
      mESt = 'Ȁ';
    }
  }
  
  protected void setinp(InputSource paramInputSource)
    throws Exception
  {
    Reader localReader = null;
    mChIdx = 0;
    mChLen = 0;
    mChars = mInp.chars;
    mInp.src = null;
    if (mPh < 0) {
      mIsSAlone = false;
    }
    mIsSAloneSet = false;
    if (paramInputSource.getCharacterStream() != null)
    {
      localReader = paramInputSource.getCharacterStream();
      xml(localReader);
    }
    else if (paramInputSource.getByteStream() != null)
    {
      String str;
      if (paramInputSource.getEncoding() != null)
      {
        str = paramInputSource.getEncoding().toUpperCase();
        if (str.equals("UTF-16")) {
          localReader = bom(paramInputSource.getByteStream(), 'U');
        } else {
          localReader = enc(str, paramInputSource.getByteStream());
        }
        xml(localReader);
      }
      else
      {
        localReader = bom(paramInputSource.getByteStream(), ' ');
        if (localReader == null)
        {
          localReader = enc("UTF-8", paramInputSource.getByteStream());
          str = xml(localReader);
          if (str.startsWith("UTF-16")) {
            panic("");
          }
          localReader = enc(str, paramInputSource.getByteStream());
        }
        else
        {
          xml(localReader);
        }
      }
    }
    else
    {
      panic("");
    }
    mInp.src = localReader;
    mInp.pubid = paramInputSource.getPublicId();
    mInp.sysid = paramInputSource.getSystemId();
  }
  
  private Reader bom(InputStream paramInputStream, char paramChar)
    throws Exception
  {
    int i = paramInputStream.read();
    switch (i)
    {
    case 239: 
      if (paramChar == 'U') {
        panic("");
      }
      if (paramInputStream.read() != 187) {
        panic("");
      }
      if (paramInputStream.read() != 191) {
        panic("");
      }
      return new ReaderUTF8(paramInputStream);
    case 254: 
      if (paramInputStream.read() != 255) {
        panic("");
      }
      return new ReaderUTF16(paramInputStream, 'b');
    case 255: 
      if (paramInputStream.read() != 254) {
        panic("");
      }
      return new ReaderUTF16(paramInputStream, 'l');
    case -1: 
      mChars[(mChIdx++)] = 65535;
      return new ReaderUTF8(paramInputStream);
    }
    if (paramChar == 'U') {
      panic("");
    }
    switch (i & 0xF0)
    {
    case 192: 
    case 208: 
      mChars[(mChIdx++)] = ((char)((i & 0x1F) << 6 | paramInputStream.read() & 0x3F));
      break;
    case 224: 
      mChars[(mChIdx++)] = ((char)((i & 0xF) << 12 | (paramInputStream.read() & 0x3F) << 6 | paramInputStream.read() & 0x3F));
      break;
    case 240: 
      throw new UnsupportedEncodingException();
    default: 
      mChars[(mChIdx++)] = ((char)i);
    }
    return null;
  }
  
  private String xml(Reader paramReader)
    throws Exception
  {
    String str1 = null;
    String str2 = "UTF-8";
    if (mChIdx != 0) {
      k = (short)(mChars[0] == '<' ? 1 : -1);
    } else {
      k = 0;
    }
    int i;
    while ((k >= 0) && (mChIdx < mChars.length))
    {
      int j;
      i = (j = paramReader.read()) >= 0 ? (char)j : 65535;
      mChars[(mChIdx++)] = i;
      switch (k)
      {
      case 0: 
        switch (i)
        {
        case 60: 
          k = 1;
          break;
        case 65279: 
          i = (j = paramReader.read()) >= 0 ? (char)j : 65535;
          mChars[(mChIdx - 1)] = i;
          k = (short)(i == 60 ? 1 : -1);
          break;
        default: 
          k = -1;
        }
        break;
      case 1: 
        k = (short)(i == 63 ? 2 : -1);
        break;
      case 2: 
        k = (short)(i == 120 ? 3 : -1);
        break;
      case 3: 
        k = (short)(i == 109 ? 4 : -1);
        break;
      case 4: 
        k = (short)(i == 108 ? 5 : -1);
        break;
      case 5: 
        switch (i)
        {
        case 9: 
        case 10: 
        case 13: 
        case 32: 
          k = 6;
          break;
        default: 
          k = -1;
        }
        break;
      case 6: 
        switch (i)
        {
        case 63: 
          k = 7;
          break;
        case 65535: 
          k = -2;
        }
        break;
      case 7: 
        switch (i)
        {
        case 62: 
        case 65535: 
          k = -2;
          break;
        default: 
          k = 6;
        }
        break;
      default: 
        panic("");
      }
    }
    mChLen = mChIdx;
    mChIdx = 0;
    if (k == -1) {
      return str2;
    }
    mChIdx = 5;
    int k = 0;
    while (k >= 0)
    {
      i = getch();
      switch (k)
      {
      case 0: 
        if (chtyp(i) != ' ')
        {
          bkch();
          k = 1;
        }
        break;
      case 1: 
      case 2: 
      case 3: 
        switch (chtyp(i))
        {
        case 'A': 
        case '_': 
        case 'a': 
          bkch();
          str1 = name(false).toLowerCase();
          if ("version".equals(str1) == true)
          {
            if (k != 1) {
              panic("");
            }
            if ("1.0".equals(eqstr('=')) != true) {
              panic("");
            }
            mInp.xmlver = 'Ā';
            k = 2;
          }
          else if ("encoding".equals(str1) == true)
          {
            if (k != 2) {
              panic("");
            }
            mInp.xmlenc = eqstr('=').toUpperCase();
            str2 = mInp.xmlenc;
            k = 3;
          }
          else if ("standalone".equals(str1) == true)
          {
            if ((k == 1) || (mPh >= 0)) {
              panic("");
            }
            str1 = eqstr('=').toLowerCase();
            if (str1.equals("yes") == true) {
              mIsSAlone = true;
            } else if (str1.equals("no") == true) {
              mIsSAlone = false;
            } else {
              panic("");
            }
            mIsSAloneSet = true;
            k = 4;
          }
          else
          {
            panic("");
          }
          break;
        case ' ': 
          break;
        case '?': 
          if (k == 1) {
            panic("");
          }
          bkch();
          k = 4;
          break;
        default: 
          panic("");
        }
        break;
      case 4: 
        switch (chtyp(i))
        {
        case '?': 
          if (getch() != '>') {
            panic("");
          }
          if (mPh <= 0) {
            mPh = 1;
          }
          k = -1;
          break;
        case ' ': 
          break;
        default: 
          panic("");
        }
        break;
      default: 
        panic("");
      }
    }
    return str2;
  }
  
  private Reader enc(String paramString, InputStream paramInputStream)
    throws UnsupportedEncodingException
  {
    if (paramString.equals("UTF-8")) {
      return new ReaderUTF8(paramInputStream);
    }
    if (paramString.equals("UTF-16LE")) {
      return new ReaderUTF16(paramInputStream, 'l');
    }
    if (paramString.equals("UTF-16BE")) {
      return new ReaderUTF16(paramInputStream, 'b');
    }
    return new InputStreamReader(paramInputStream, paramString);
  }
  
  protected void push(Input paramInput)
  {
    mInp.chLen = mChLen;
    mInp.chIdx = mChIdx;
    next = mInp;
    mInp = paramInput;
    mChars = chars;
    mChLen = chLen;
    mChIdx = chIdx;
  }
  
  protected void pop()
  {
    if (mInp.src != null)
    {
      try
      {
        mInp.src.close();
      }
      catch (IOException localIOException) {}
      mInp.src = null;
    }
    mInp = mInp.next;
    if (mInp != null)
    {
      mChars = mInp.chars;
      mChLen = mInp.chLen;
      mChIdx = mInp.chIdx;
    }
    else
    {
      mChars = null;
      mChLen = 0;
      mChIdx = 0;
    }
  }
  
  protected char chtyp(char paramChar)
  {
    if (paramChar < '') {
      return (char)asctyp[paramChar];
    }
    return paramChar != 65535 ? 'X' : 'Z';
  }
  
  protected char getch()
    throws IOException
  {
    if (mChIdx >= mChLen)
    {
      if (mInp.src == null)
      {
        pop();
        return getch();
      }
      int i = mInp.src.read(mChars, 0, mChars.length);
      if (i < 0)
      {
        if (mInp != mDoc)
        {
          pop();
          return getch();
        }
        mChars[0] = 65535;
        mChLen = 1;
      }
      else
      {
        mChLen = i;
      }
      mChIdx = 0;
    }
    return mChars[(mChIdx++)];
  }
  
  protected void bkch()
    throws Exception
  {
    if (mChIdx <= 0) {
      panic("");
    }
    mChIdx -= 1;
  }
  
  protected void setch(char paramChar)
  {
    mChars[mChIdx] = paramChar;
  }
  
  protected Pair find(Pair paramPair, char[] paramArrayOfChar)
  {
    for (Pair localPair = paramPair; localPair != null; localPair = next) {
      if (localPair.eqname(paramArrayOfChar) == true) {
        return localPair;
      }
    }
    return null;
  }
  
  protected Pair pair(Pair paramPair)
  {
    Pair localPair;
    if (mDltd != null)
    {
      localPair = mDltd;
      mDltd = next;
    }
    else
    {
      localPair = new Pair();
    }
    next = paramPair;
    return localPair;
  }
  
  protected Pair del(Pair paramPair)
  {
    Pair localPair = next;
    name = null;
    value = null;
    chars = null;
    list = null;
    next = mDltd;
    mDltd = paramPair;
    return localPair;
  }
  
  static
  {
    NONS[0] = '\000';
    XML = new char[4];
    XML[0] = '\004';
    XML[1] = 'x';
    XML[2] = 'm';
    XML[3] = 'l';
    XMLNS = new char[6];
    XMLNS[0] = '\006';
    XMLNS[1] = 'x';
    XMLNS[2] = 'm';
    XMLNS[3] = 'l';
    XMLNS[4] = 'n';
    XMLNS[5] = 's';
    int i = 0;
    asctyp = new byte[''];
    while (i < 32)
    {
      i = (short)(i + 1);
      asctyp[i] = 122;
    }
    asctyp[9] = 32;
    asctyp[13] = 32;
    asctyp[10] = 32;
    while (i < 48)
    {
      i = (short)(i + 1);
      asctyp[i] = ((byte)i);
    }
    while (i <= 57)
    {
      i = (short)(i + 1);
      asctyp[i] = 100;
    }
    while (i < 65)
    {
      i = (short)(i + 1);
      asctyp[i] = ((byte)i);
    }
    while (i <= 90)
    {
      i = (short)(i + 1);
      asctyp[i] = 65;
    }
    while (i < 97)
    {
      i = (short)(i + 1);
      asctyp[i] = ((byte)i);
    }
    while (i <= 122)
    {
      i = (short)(i + 1);
      asctyp[i] = 97;
    }
    while (i < 128)
    {
      i = (short)(i + 1);
      asctyp[i] = ((byte)i);
    }
    nmttyp = new byte[''];
    for (i = 0; i < 48; i = (short)(i + 1)) {
      nmttyp[i] = -1;
    }
    while (i <= 57)
    {
      i = (short)(i + 1);
      nmttyp[i] = 2;
    }
    while (i < 65)
    {
      i = (short)(i + 1);
      nmttyp[i] = -1;
    }
    for (i = 91; i < 97; i = (short)(i + 1)) {
      nmttyp[i] = -1;
    }
    for (i = 123; i < 128; i = (short)(i + 1)) {
      nmttyp[i] = -1;
    }
    nmttyp[95] = 0;
    nmttyp[58] = 1;
    nmttyp[46] = 2;
    nmttyp[45] = 2;
    nmttyp[32] = 3;
    nmttyp[9] = 3;
    nmttyp[13] = 3;
    nmttyp[10] = 3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\impl\Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
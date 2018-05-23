package com.sun.jmx.snmp.IPAcl;

import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Vector;

class Parser
  implements ParserTreeConstants, ParserConstants
{
  protected JJTParserState jjtree = new JJTParserState();
  public ParserTokenManager token_source;
  ASCII_CharStream jj_input_stream;
  public Token token;
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos;
  private Token jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  private final int[] jj_la1 = new int[22];
  private final int[] jj_la1_0 = { 256, 524288, 1048576, 8192, 0, 393216, 0, Integer.MIN_VALUE, 285212672, 0, 0, 0, 0, 8192, 8192, 0, -1862270976, 0, 32768, 8192, 0, -1862270976 };
  private final int[] jj_la1_1 = { 0, 0, 0, 0, 16, 0, 16, 0, 0, 32, 32, 64, 32, 0, 0, 16, 0, 16, 0, 0, 16, 0 };
  private final JJCalls[] jj_2_rtns = new JJCalls[3];
  private boolean jj_rescan = false;
  private int jj_gc = 0;
  private Vector<int[]> jj_expentries = new Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;
  
  public final JDMSecurityDefs SecurityDefs()
    throws ParseException
  {
    JDMSecurityDefs localJDMSecurityDefs1 = new JDMSecurityDefs(0);
    int i = 1;
    jjtree.openNodeScope(localJDMSecurityDefs1);
    try
    {
      switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
      {
      case 8: 
        AclBlock();
        break;
      default: 
        jj_la1[0] = jj_gen;
      }
      switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
      {
      case 19: 
        TrapBlock();
        break;
      default: 
        jj_la1[1] = jj_gen;
      }
      switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
      {
      case 20: 
        InformBlock();
        break;
      default: 
        jj_la1[2] = jj_gen;
      }
      jj_consume_token(0);
      jjtree.closeNodeScope(localJDMSecurityDefs1, true);
      i = 0;
      JDMSecurityDefs localJDMSecurityDefs2 = localJDMSecurityDefs1;
      return localJDMSecurityDefs2;
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMSecurityDefs1);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMSecurityDefs1, true);
      }
    }
  }
  
  public final void AclBlock()
    throws ParseException
  {
    JDMAclBlock localJDMAclBlock = new JDMAclBlock(1);
    int i = 1;
    jjtree.openNodeScope(localJDMAclBlock);
    try
    {
      jj_consume_token(8);
      jj_consume_token(9);
      jj_consume_token(13);
      for (;;)
      {
        AclItem();
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        }
      }
      jj_la1[3] = jj_gen;
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMAclBlock);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMAclBlock, true);
      }
    }
  }
  
  public final void AclItem()
    throws ParseException
  {
    JDMAclItem localJDMAclItem = new JDMAclItem(2);
    int i = 1;
    jjtree.openNodeScope(localJDMAclItem);
    try
    {
      jj_consume_token(13);
      com = Communities();
      access = Access();
      Managers();
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMAclItem);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMAclItem, true);
      }
    }
  }
  
  public final JDMCommunities Communities()
    throws ParseException
  {
    JDMCommunities localJDMCommunities1 = new JDMCommunities(3);
    int i = 1;
    jjtree.openNodeScope(localJDMCommunities1);
    try
    {
      jj_consume_token(10);
      jj_consume_token(9);
      Community();
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 36: 
          break;
        default: 
          jj_la1[4] = jj_gen;
          break;
        }
        jj_consume_token(36);
        Community();
      }
      jjtree.closeNodeScope(localJDMCommunities1, true);
      i = 0;
      JDMCommunities localJDMCommunities2 = localJDMCommunities1;
      return localJDMCommunities2;
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMCommunities1);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMCommunities1, true);
      }
    }
  }
  
  public final void Community()
    throws ParseException
  {
    JDMCommunity localJDMCommunity = new JDMCommunity(4);
    int i = 1;
    jjtree.openNodeScope(localJDMCommunity);
    try
    {
      Token localToken = jj_consume_token(31);
      jjtree.closeNodeScope(localJDMCommunity, true);
      i = 0;
      communityString = image;
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMCommunity, true);
      }
    }
  }
  
  public final JDMAccess Access()
    throws ParseException
  {
    JDMAccess localJDMAccess1 = new JDMAccess(5);
    int i = 1;
    jjtree.openNodeScope(localJDMAccess1);
    try
    {
      jj_consume_token(7);
      jj_consume_token(9);
      switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
      {
      case 17: 
        jj_consume_token(17);
        access = 17;
        break;
      case 18: 
        jj_consume_token(18);
        access = 18;
        break;
      default: 
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jjtree.closeNodeScope(localJDMAccess1, true);
      i = 0;
      JDMAccess localJDMAccess2 = localJDMAccess1;
      return localJDMAccess2;
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMAccess1, true);
      }
    }
  }
  
  public final void Managers()
    throws ParseException
  {
    JDMManagers localJDMManagers = new JDMManagers(6);
    int i = 1;
    jjtree.openNodeScope(localJDMManagers);
    try
    {
      jj_consume_token(14);
      jj_consume_token(9);
      Host();
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 36: 
          break;
        default: 
          jj_la1[6] = jj_gen;
          break;
        }
        jj_consume_token(36);
        Host();
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMManagers);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMManagers, true);
      }
    }
  }
  
  public final void Host()
    throws ParseException
  {
    JDMHost localJDMHost = new JDMHost(7);
    int i = 1;
    jjtree.openNodeScope(localJDMHost);
    try
    {
      switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
      {
      case 31: 
        HostName();
        break;
      default: 
        jj_la1[7] = jj_gen;
        if (jj_2_1(Integer.MAX_VALUE)) {
          NetMask();
        } else if (jj_2_2(Integer.MAX_VALUE)) {
          NetMaskV6();
        } else if (jj_2_3(Integer.MAX_VALUE)) {
          IpAddress();
        } else {
          switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
          {
          case 28: 
            IpV6Address();
            break;
          case 24: 
            IpMask();
            break;
          default: 
            jj_la1[8] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
        break;
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMHost);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMHost, true);
      }
    }
  }
  
  public final void HostName()
    throws ParseException
  {
    JDMHostName localJDMHostName = new JDMHostName(8);
    int i = 1;
    jjtree.openNodeScope(localJDMHostName);
    try
    {
      Token localToken = jj_consume_token(31);
      name.append(image);
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 37: 
          break;
        default: 
          jj_la1[9] = jj_gen;
          break;
        }
        jj_consume_token(37);
        localToken = jj_consume_token(31);
        name.append("." + image);
      }
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMHostName, true);
      }
    }
  }
  
  public final void IpAddress()
    throws ParseException
  {
    JDMIpAddress localJDMIpAddress = new JDMIpAddress(9);
    int i = 1;
    jjtree.openNodeScope(localJDMIpAddress);
    try
    {
      Token localToken = jj_consume_token(24);
      address.append(image);
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 37: 
          break;
        default: 
          jj_la1[10] = jj_gen;
          break;
        }
        jj_consume_token(37);
        localToken = jj_consume_token(24);
        address.append("." + image);
      }
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMIpAddress, true);
      }
    }
  }
  
  public final void IpV6Address()
    throws ParseException
  {
    JDMIpV6Address localJDMIpV6Address = new JDMIpV6Address(10);
    int i = 1;
    jjtree.openNodeScope(localJDMIpV6Address);
    try
    {
      Token localToken = jj_consume_token(28);
      jjtree.closeNodeScope(localJDMIpV6Address, true);
      i = 0;
      address.append(image);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMIpV6Address, true);
      }
    }
  }
  
  public final void IpMask()
    throws ParseException
  {
    JDMIpMask localJDMIpMask = new JDMIpMask(11);
    int i = 1;
    jjtree.openNodeScope(localJDMIpMask);
    try
    {
      Token localToken = jj_consume_token(24);
      address.append(image);
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 38: 
          break;
        default: 
          jj_la1[11] = jj_gen;
          break;
        }
        jj_consume_token(38);
        localToken = jj_consume_token(24);
        address.append("." + image);
      }
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMIpMask, true);
      }
    }
  }
  
  public final void NetMask()
    throws ParseException
  {
    JDMNetMask localJDMNetMask = new JDMNetMask(12);
    int i = 1;
    jjtree.openNodeScope(localJDMNetMask);
    try
    {
      Token localToken = jj_consume_token(24);
      address.append(image);
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 37: 
          break;
        default: 
          jj_la1[12] = jj_gen;
          break;
        }
        jj_consume_token(37);
        localToken = jj_consume_token(24);
        address.append("." + image);
      }
      jj_consume_token(39);
      localToken = jj_consume_token(24);
      jjtree.closeNodeScope(localJDMNetMask, true);
      i = 0;
      mask = image;
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMNetMask, true);
      }
    }
  }
  
  public final void NetMaskV6()
    throws ParseException
  {
    JDMNetMaskV6 localJDMNetMaskV6 = new JDMNetMaskV6(13);
    int i = 1;
    jjtree.openNodeScope(localJDMNetMaskV6);
    try
    {
      Token localToken = jj_consume_token(28);
      address.append(image);
      jj_consume_token(39);
      localToken = jj_consume_token(24);
      jjtree.closeNodeScope(localJDMNetMaskV6, true);
      i = 0;
      mask = image;
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMNetMaskV6, true);
      }
    }
  }
  
  public final void TrapBlock()
    throws ParseException
  {
    JDMTrapBlock localJDMTrapBlock = new JDMTrapBlock(14);
    int i = 1;
    jjtree.openNodeScope(localJDMTrapBlock);
    try
    {
      jj_consume_token(19);
      jj_consume_token(9);
      jj_consume_token(13);
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 13: 
          break;
        default: 
          jj_la1[13] = jj_gen;
          break;
        }
        TrapItem();
      }
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMTrapBlock);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMTrapBlock, true);
      }
    }
  }
  
  public final void TrapItem()
    throws ParseException
  {
    JDMTrapItem localJDMTrapItem = new JDMTrapItem(15);
    int i = 1;
    jjtree.openNodeScope(localJDMTrapItem);
    try
    {
      jj_consume_token(13);
      comm = TrapCommunity();
      TrapInterestedHost();
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 13: 
          break;
        default: 
          jj_la1[14] = jj_gen;
          break;
        }
        Enterprise();
      }
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMTrapItem);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMTrapItem, true);
      }
    }
  }
  
  public final JDMTrapCommunity TrapCommunity()
    throws ParseException
  {
    JDMTrapCommunity localJDMTrapCommunity1 = new JDMTrapCommunity(16);
    int i = 1;
    jjtree.openNodeScope(localJDMTrapCommunity1);
    try
    {
      jj_consume_token(21);
      jj_consume_token(9);
      Token localToken = jj_consume_token(31);
      jjtree.closeNodeScope(localJDMTrapCommunity1, true);
      i = 0;
      community = image;
      JDMTrapCommunity localJDMTrapCommunity2 = localJDMTrapCommunity1;
      return localJDMTrapCommunity2;
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMTrapCommunity1, true);
      }
    }
  }
  
  public final void TrapInterestedHost()
    throws ParseException
  {
    JDMTrapInterestedHost localJDMTrapInterestedHost = new JDMTrapInterestedHost(17);
    int i = 1;
    jjtree.openNodeScope(localJDMTrapInterestedHost);
    try
    {
      jj_consume_token(12);
      jj_consume_token(9);
      HostTrap();
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 36: 
          break;
        default: 
          jj_la1[15] = jj_gen;
          break;
        }
        jj_consume_token(36);
        HostTrap();
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMTrapInterestedHost);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMTrapInterestedHost, true);
      }
    }
  }
  
  public final void HostTrap()
    throws ParseException
  {
    JDMHostTrap localJDMHostTrap = new JDMHostTrap(18);
    int i = 1;
    jjtree.openNodeScope(localJDMHostTrap);
    try
    {
      switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
      {
      case 31: 
        HostName();
        break;
      case 24: 
        IpAddress();
        break;
      case 28: 
        IpV6Address();
        break;
      default: 
        jj_la1[16] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMHostTrap);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMHostTrap, true);
      }
    }
  }
  
  public final void Enterprise()
    throws ParseException
  {
    JDMEnterprise localJDMEnterprise = new JDMEnterprise(19);
    int i = 1;
    jjtree.openNodeScope(localJDMEnterprise);
    try
    {
      jj_consume_token(13);
      jj_consume_token(11);
      jj_consume_token(9);
      Token localToken = jj_consume_token(35);
      enterprise = image;
      jj_consume_token(23);
      jj_consume_token(9);
      TrapNum();
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 36: 
          break;
        default: 
          jj_la1[17] = jj_gen;
          break;
        }
        jj_consume_token(36);
        TrapNum();
      }
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMEnterprise);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMEnterprise, true);
      }
    }
  }
  
  public final void TrapNum()
    throws ParseException
  {
    JDMTrapNum localJDMTrapNum = new JDMTrapNum(20);
    int i = 1;
    jjtree.openNodeScope(localJDMTrapNum);
    try
    {
      Token localToken = jj_consume_token(24);
      low = Integer.parseInt(image);
      switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
      {
      case 15: 
        jj_consume_token(15);
        localToken = jj_consume_token(24);
        high = Integer.parseInt(image);
        break;
      default: 
        jj_la1[18] = jj_gen;
      }
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMTrapNum, true);
      }
    }
  }
  
  public final void InformBlock()
    throws ParseException
  {
    JDMInformBlock localJDMInformBlock = new JDMInformBlock(21);
    int i = 1;
    jjtree.openNodeScope(localJDMInformBlock);
    try
    {
      jj_consume_token(20);
      jj_consume_token(9);
      jj_consume_token(13);
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 13: 
          break;
        default: 
          jj_la1[19] = jj_gen;
          break;
        }
        InformItem();
      }
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMInformBlock);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMInformBlock, true);
      }
    }
  }
  
  public final void InformItem()
    throws ParseException
  {
    JDMInformItem localJDMInformItem = new JDMInformItem(22);
    int i = 1;
    jjtree.openNodeScope(localJDMInformItem);
    try
    {
      jj_consume_token(13);
      comm = InformCommunity();
      InformInterestedHost();
      jj_consume_token(16);
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMInformItem);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMInformItem, true);
      }
    }
  }
  
  public final JDMInformCommunity InformCommunity()
    throws ParseException
  {
    JDMInformCommunity localJDMInformCommunity1 = new JDMInformCommunity(23);
    int i = 1;
    jjtree.openNodeScope(localJDMInformCommunity1);
    try
    {
      jj_consume_token(22);
      jj_consume_token(9);
      Token localToken = jj_consume_token(31);
      jjtree.closeNodeScope(localJDMInformCommunity1, true);
      i = 0;
      community = image;
      JDMInformCommunity localJDMInformCommunity2 = localJDMInformCommunity1;
      return localJDMInformCommunity2;
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMInformCommunity1, true);
      }
    }
  }
  
  public final void InformInterestedHost()
    throws ParseException
  {
    JDMInformInterestedHost localJDMInformInterestedHost = new JDMInformInterestedHost(24);
    int i = 1;
    jjtree.openNodeScope(localJDMInformInterestedHost);
    try
    {
      jj_consume_token(12);
      jj_consume_token(9);
      HostInform();
      for (;;)
      {
        switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
        {
        case 36: 
          break;
        default: 
          jj_la1[20] = jj_gen;
          break;
        }
        jj_consume_token(36);
        HostInform();
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMInformInterestedHost);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMInformInterestedHost, true);
      }
    }
  }
  
  public final void HostInform()
    throws ParseException
  {
    JDMHostInform localJDMHostInform = new JDMHostInform(25);
    int i = 1;
    jjtree.openNodeScope(localJDMHostInform);
    try
    {
      switch (jj_ntk == -1 ? jj_ntk() : jj_ntk)
      {
      case 31: 
        HostName();
        break;
      case 24: 
        IpAddress();
        break;
      case 28: 
        IpV6Address();
        break;
      default: 
        jj_la1[21] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    catch (Throwable localThrowable)
    {
      if (i != 0)
      {
        jjtree.clearNodeScope(localJDMHostInform);
        i = 0;
      }
      else
      {
        jjtree.popNode();
      }
      if ((localThrowable instanceof RuntimeException)) {
        throw ((RuntimeException)localThrowable);
      }
      if ((localThrowable instanceof ParseException)) {
        throw ((ParseException)localThrowable);
      }
      throw ((Error)localThrowable);
    }
    finally
    {
      if (i != 0) {
        jjtree.closeNodeScope(localJDMHostInform, true);
      }
    }
  }
  
  private final boolean jj_2_1(int paramInt)
  {
    jj_la = paramInt;
    jj_lastpos = (jj_scanpos = token);
    boolean bool = !jj_3_1();
    jj_save(0, paramInt);
    return bool;
  }
  
  private final boolean jj_2_2(int paramInt)
  {
    jj_la = paramInt;
    jj_lastpos = (jj_scanpos = token);
    boolean bool = !jj_3_2();
    jj_save(1, paramInt);
    return bool;
  }
  
  private final boolean jj_2_3(int paramInt)
  {
    jj_la = paramInt;
    jj_lastpos = (jj_scanpos = token);
    boolean bool = !jj_3_3();
    jj_save(2, paramInt);
    return bool;
  }
  
  private final boolean jj_3_3()
  {
    if (jj_scan_token(24)) {
      return true;
    }
    if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) {
      return false;
    }
    if (jj_scan_token(37)) {
      return true;
    }
    return (jj_la != 0) || (jj_scanpos != jj_lastpos);
  }
  
  private final boolean jj_3_2()
  {
    if (jj_scan_token(28)) {
      return true;
    }
    if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) {
      return false;
    }
    if (jj_scan_token(39)) {
      return true;
    }
    if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) {
      return false;
    }
    if (jj_scan_token(24)) {
      return true;
    }
    return (jj_la != 0) || (jj_scanpos != jj_lastpos);
  }
  
  private final boolean jj_3_1()
  {
    if (jj_scan_token(24)) {
      return true;
    }
    if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) {
      return false;
    }
    do
    {
      Token localToken = jj_scanpos;
      if (jj_3R_14())
      {
        jj_scanpos = localToken;
        break;
      }
    } while ((jj_la != 0) || (jj_scanpos != jj_lastpos));
    return false;
    if (jj_scan_token(39)) {
      return true;
    }
    if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) {
      return false;
    }
    if (jj_scan_token(24)) {
      return true;
    }
    return (jj_la != 0) || (jj_scanpos != jj_lastpos);
  }
  
  private final boolean jj_3R_14()
  {
    if (jj_scan_token(37)) {
      return true;
    }
    if ((jj_la == 0) && (jj_scanpos == jj_lastpos)) {
      return false;
    }
    if (jj_scan_token(24)) {
      return true;
    }
    return (jj_la != 0) || (jj_scanpos != jj_lastpos);
  }
  
  public Parser(InputStream paramInputStream)
  {
    jj_input_stream = new ASCII_CharStream(paramInputStream, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      jj_la1[i] = -1;
    }
    for (i = 0; i < jj_2_rtns.length; i++) {
      jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public void ReInit(InputStream paramInputStream)
  {
    jj_input_stream.ReInit(paramInputStream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      jj_la1[i] = -1;
    }
    for (i = 0; i < jj_2_rtns.length; i++) {
      jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public Parser(Reader paramReader)
  {
    jj_input_stream = new ASCII_CharStream(paramReader, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      jj_la1[i] = -1;
    }
    for (i = 0; i < jj_2_rtns.length; i++) {
      jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public void ReInit(Reader paramReader)
  {
    jj_input_stream.ReInit(paramReader, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      jj_la1[i] = -1;
    }
    for (i = 0; i < jj_2_rtns.length; i++) {
      jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public Parser(ParserTokenManager paramParserTokenManager)
  {
    token_source = paramParserTokenManager;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      jj_la1[i] = -1;
    }
    for (i = 0; i < jj_2_rtns.length; i++) {
      jj_2_rtns[i] = new JJCalls();
    }
  }
  
  public void ReInit(ParserTokenManager paramParserTokenManager)
  {
    token_source = paramParserTokenManager;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      jj_la1[i] = -1;
    }
    for (i = 0; i < jj_2_rtns.length; i++) {
      jj_2_rtns[i] = new JJCalls();
    }
  }
  
  private final Token jj_consume_token(int paramInt)
    throws ParseException
  {
    Token localToken;
    if (token).next != null) {
      token = token.next;
    } else {
      token = (token.next = token_source.getNextToken());
    }
    jj_ntk = -1;
    if (token.kind == paramInt)
    {
      jj_gen += 1;
      if (++jj_gc > 100)
      {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          for (JJCalls localJJCalls = jj_2_rtns[i]; localJJCalls != null; localJJCalls = next) {
            if (gen < jj_gen) {
              first = null;
            }
          }
        }
      }
      return token;
    }
    token = localToken;
    jj_kind = paramInt;
    throw generateParseException();
  }
  
  private final boolean jj_scan_token(int paramInt)
  {
    if (jj_scanpos == jj_lastpos)
    {
      jj_la -= 1;
      if (jj_scanpos.next == null) {
        jj_lastpos = (jj_scanpos = jj_scanpos.next = token_source.getNextToken());
      } else {
        jj_lastpos = (jj_scanpos = jj_scanpos.next);
      }
    }
    else
    {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan)
    {
      int i = 0;
      for (Token localToken = token; (localToken != null) && (localToken != jj_scanpos); localToken = next) {
        i++;
      }
      if (localToken != null) {
        jj_add_error_token(paramInt, i);
      }
    }
    return jj_scanpos.kind != paramInt;
  }
  
  public final Token getNextToken()
  {
    if (token.next != null) {
      token = token.next;
    } else {
      token = (token.next = token_source.getNextToken());
    }
    jj_ntk = -1;
    jj_gen += 1;
    return token;
  }
  
  public final Token getToken(int paramInt)
  {
    Token localToken = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < paramInt; i++) {
      if (next != null) {
        localToken = next;
      } else {
        localToken = next = token_source.getNextToken();
      }
    }
    return localToken;
  }
  
  private final int jj_ntk()
  {
    if ((jj_nt = token.next) == null) {
      return jj_ntk = token.next = token_source.getNextToken()).kind;
    }
    return jj_ntk = jj_nt.kind;
  }
  
  private void jj_add_error_token(int paramInt1, int paramInt2)
  {
    if (paramInt2 >= 100) {
      return;
    }
    if (paramInt2 == jj_endpos + 1)
    {
      jj_lasttokens[(jj_endpos++)] = paramInt1;
    }
    else if (jj_endpos != 0)
    {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      i = 0;
      Enumeration localEnumeration = jj_expentries.elements();
      while (localEnumeration.hasMoreElements())
      {
        int[] arrayOfInt = (int[])localEnumeration.nextElement();
        if (arrayOfInt.length == jj_expentry.length)
        {
          i = 1;
          for (int j = 0; j < jj_expentry.length; j++) {
            if (arrayOfInt[j] != jj_expentry[j])
            {
              i = 0;
              break;
            }
          }
          if (i != 0) {
            break;
          }
        }
      }
      if (i == 0) {
        jj_expentries.addElement(jj_expentry);
      }
      if (paramInt2 != 0) {
        jj_lasttokens[((jj_endpos = paramInt2) - 1)] = paramInt1;
      }
    }
  }
  
  public final ParseException generateParseException()
  {
    jj_expentries.removeAllElements();
    boolean[] arrayOfBoolean = new boolean[40];
    for (int i = 0; i < 40; i++) {
      arrayOfBoolean[i] = false;
    }
    if (jj_kind >= 0)
    {
      arrayOfBoolean[jj_kind] = true;
      jj_kind = -1;
    }
    for (i = 0; i < 22; i++) {
      if (jj_la1[i] == jj_gen) {
        for (j = 0; j < 32; j++)
        {
          if ((jj_la1_0[i] & 1 << j) != 0) {
            arrayOfBoolean[j] = true;
          }
          if ((jj_la1_1[i] & 1 << j) != 0) {
            arrayOfBoolean[(32 + j)] = true;
          }
        }
      }
    }
    for (i = 0; i < 40; i++) {
      if (arrayOfBoolean[i] != 0)
      {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] arrayOfInt = new int[jj_expentries.size()][];
    for (int j = 0; j < jj_expentries.size(); j++) {
      arrayOfInt[j] = ((int[])jj_expentries.elementAt(j));
    }
    return new ParseException(token, arrayOfInt, tokenImage);
  }
  
  public final void enable_tracing() {}
  
  public final void disable_tracing() {}
  
  private final void jj_rescan_token()
  {
    jj_rescan = true;
    for (int i = 0; i < 3; i++)
    {
      JJCalls localJJCalls = jj_2_rtns[i];
      do
      {
        if (gen > jj_gen)
        {
          jj_la = arg;
          jj_lastpos = (jj_scanpos = first);
          switch (i)
          {
          case 0: 
            jj_3_1();
            break;
          case 1: 
            jj_3_2();
            break;
          case 2: 
            jj_3_3();
          }
        }
        localJJCalls = next;
      } while (localJJCalls != null);
    }
    jj_rescan = false;
  }
  
  private final void jj_save(int paramInt1, int paramInt2)
  {
    for (JJCalls localJJCalls = jj_2_rtns[paramInt1]; gen > jj_gen; localJJCalls = next) {
      if (next == null)
      {
        localJJCalls = next = new JJCalls();
        break;
      }
    }
    gen = (jj_gen + paramInt2 - jj_la);
    first = token;
    arg = paramInt2;
  }
  
  static final class JJCalls
  {
    int gen;
    Token first;
    int arg;
    JJCalls next;
    
    JJCalls() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\snmp\IPAcl\Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */
public class Htdormsw {
  static String wsInStatus = "  ";
  static String wsOutStatus = "  ";
  static boolean endOfInput = false;
  static boolean inputError = false;
  static long wsRecordCount = 0;
  static long wsAcceptCount = 0;
  static long wsRejectCount = 0;
  static int wsArithError = 0;
  static String wsEdgeCode = spaces(24);
  static String wsKey = spaces(12);
  static String wsPrevKey = spaces(12);
  static String wsBranch = spaces(3);
  static String wsPrevBranch = spaces(3);
  static String wsStatus = spaces(8);
  static java.math.BigDecimal wsAmount = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsBalance = java.math.BigDecimal.ZERO;
  static long wsReadTime = 0;
  static long wsWriteTime = 0;
  static long wsAbendCode = 0;
  static String wsReturnReason = spaces(3);
  static long wsReturnCount = 0;
  static java.math.BigDecimal wsCycleTotal = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsDebits = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsCredits = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsDelta = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsReportTotal = java.math.BigDecimal.ZERO;
  static String wsAmountText = spaces(8);
  static String wsShortAmount = spaces(6);
  static String wsRawBase = spaces(120);
  static String wsFeeOverride = " ";
  static int wsItemCount = 0;
  static java.math.BigDecimal wsPacked = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsZoned = java.math.BigDecimal.ZERO;
  static char[] wsLine = spaces(200).toCharArray();
  static int wsDaysInactive = 0;
  static int wsDormantThreshold = 365;
  static java.math.BigDecimal wsSweepAmount = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal wsFee = java.math.BigDecimal.ZERO;
  static String wsFeeWaived = "N";
  static java.math.BigDecimal wsFxRate = new java.math.BigDecimal("1.0825");
  static int wsOldDate = 991231;
  static int wsCentury = 20;
  static int wsNewDate = 0;
  static int wsDayBasis = 365;
  static String wsLeapFlag = "N";
  static String wsCallKey = spaces(12);
  static java.math.BigDecimal wsCallAmt = java.math.BigDecimal.ZERO;
  static int wsCallError = 0;
  static java.math.BigDecimal wsCallValue = java.math.BigDecimal.ZERO;
  static String wsCallText = spaces(80);
  static String inRecord = spaces(160);
  static String outRecord = spaces(200);
  static int returnCode = 0;
  static java.io.BufferedReader inReader = null;
  static java.io.BufferedWriter outWriter = null;
  static boolean inOpen = false;
  static boolean outOpen = false;
  static String htdateCallName = spaces(8);
  static int htdateDateRaw = 0;
  static int htdateDateFull = 0;
  static int htdateCentury = 20;
  static java.math.BigDecimal htdateTemp = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal htdateRound = java.math.BigDecimal.ZERO;
  static long htdateCounter = 0;
  static String htdateLkKey = spaces(12);
  static java.math.BigDecimal htdateLkAmt = java.math.BigDecimal.ZERO;
  static int htdateLkError = 0;
  static java.math.BigDecimal htdateLkValue = java.math.BigDecimal.ZERO;
  static String htdateLkText = spaces(80);
  static String htpostCallName = spaces(8);
  static int htpostDateRaw = 0;
  static int htpostDateFull = 0;
  static int htpostCentury = 20;
  static java.math.BigDecimal htpostTemp = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal htpostRound = java.math.BigDecimal.ZERO;
  static long htpostCounter = 0;
  static String htpostLkKey = spaces(12);
  static java.math.BigDecimal htpostLkAmt = java.math.BigDecimal.ZERO;
  static int htpostLkError = 0;
  static java.math.BigDecimal htpostLkValue = java.math.BigDecimal.ZERO;
  static String htpostLkText = spaces(80);
  static String htaudtCallName = spaces(8);
  static int htaudtDateRaw = 0;
  static int htaudtDateFull = 0;
  static int htaudtCentury = 20;
  static java.math.BigDecimal htaudtTemp = java.math.BigDecimal.ZERO;
  static java.math.BigDecimal htaudtRound = java.math.BigDecimal.ZERO;
  static long htaudtCounter = 0;
  static String htaudtLkKey = spaces(12);
  static java.math.BigDecimal htaudtLkAmt = java.math.BigDecimal.ZERO;
  static int htaudtLkError = 0;
  static java.math.BigDecimal htaudtLkValue = java.math.BigDecimal.ZERO;
  static String htaudtLkText = spaces(80);
  public static void main(String[] args) {
    try { p0000Main(); } catch (Throwable t) {
      try { System.out.flush(); } catch (Throwable x) {}
      try { if (inReader != null && inOpen) inReader.close(); } catch (Throwable x) {}
      try { if (outWriter != null && outOpen) { outWriter.flush(); outWriter.close(); } } catch (Throwable x) {}
      if (returnCode != 0) System.exit(returnCode); else System.exit(12);
      return;
    }
    if (returnCode != 0) System.exit(returnCode);
  }
  static String spaces(int n) { StringBuilder sb=new StringBuilder(); for(int i=0;i<n;i++) sb.append(' '); return sb.toString(); }
  static String padRight(String s,int n) { if(s==null) s=""; if(s.length()>n) return s.substring(0,n); StringBuilder sb=new StringBuilder(s); while(sb.length()<n) sb.append(' '); return sb.toString(); }
  static String lowValues(int n) { StringBuilder sb=new StringBuilder(); for(int i=0;i<n;i++) sb.append('\0'); return sb.toString(); }
  static boolean isEbcdic() { String v=System.getenv("DIOCLE_COLLATION"); return "EBCDIC".equals(v); }
  static int cobolCompare(String a,String b) {
    int n=Math.max(a.length(),b.length());
    String aa=padRight(a,n); String bb=padRight(b,n);
    if(isEbcdic()) {
      try {
        byte[] ba=aa.getBytes("Cp037"); byte[] bb2=bb.getBytes("Cp037");
        for(int i=0;i<ba.length && i<bb2.length;i++){ int x=ba[i]&0xFF; int y=bb2[i]&0xFF; if(x!=y) return x-y; }
        return ba.length-bb2.length;
      } catch(Throwable t){}
    }
    return aa.compareTo(bb);
  }
  static boolean isNumericX(String s) { if(s==null||s.length()==0) return false; for(int i=0;i<s.length();i++){ char c=s.charAt(i); if(c<'0'||c>'9') return false; } return true; }
  static boolean isAlphabetic(String s) { for(int i=0;i<s.length();i++){ char c=s.charAt(i); if(c==' ') continue; if((c>='A'&&c<='Z')||(c>='a'&&c<='z')) continue; return false; } return true; }
  static String format9(long v) { long m=v%10000000000L; if(m<0) m+=10000000000L; String s=Long.toString(m); while(s.length()<10) s="0"+s; if(s.length()>10) s=s.substring(s.length()-10); return s; }
  static String formatTotal(java.math.BigDecimal v) {
    boolean neg=v.signum()<0;
    java.math.BigDecimal a=v.abs().setScale(2,java.math.RoundingMode.DOWN);
    java.math.BigInteger u=a.movePointRight(2).toBigInteger();
    String d=u.toString(); while(d.length()<13) d="0"+d;
    if(d.length()>13) d=d.substring(d.length()-13);
    String s=d.substring(0,11)+"."+d.substring(11);
    if(neg) return "-"+s; else return "+"+s;
  }
  static java.math.BigDecimal parseAmountText(String s) {
    String t=s.trim(); if(t.length()==0) return java.math.BigDecimal.ZERO;
    try {
      if(t.contains(".")) return new java.math.BigDecimal(t);
      else return new java.math.BigDecimal(t);
    } catch(Throwable e){ return java.math.BigDecimal.ZERO; }
  }
  static int parseIntSafe(String s) { String t=s.replace(" ",""); if(t.length()==0) return 0; try{ boolean neg=false; int st=0; if(t.charAt(0)=='+'){st=1;} else if(t.charAt(0)=='-'){neg=true;st=1;} String d=t.substring(st); if(d.length()==0) return 0; for(int i=0;i<d.length();i++){ if(d.charAt(i)<'0'||d.charAt(i)>'9') return 0; } long v=Long.parseLong(d); if(neg) v=-v; if(v>9999) v=v%10000; if(v<-9999) v=v%10000; return (int)v; }catch(Throwable e){return 0;} }
  static String rtrim(String s){ int i=s.length(); while(i>0&&s.charAt(i-1)==' ') i--; return s.substring(0,i); }
  static void lineMove(int start1,int len,String val){ String v=padRight(val,len); for(int i=0;i<len;i++){ wsLine[start1-1+i]=v.charAt(i); } }
  static String lineString(){ return new String(wsLine); }
  static void display(String s){ System.out.println(s); }
  private static void abend(int code){
    try{ System.out.flush(); }catch(Throwable t){}
    try{ if(inReader!=null&&inOpen) inReader.close(); }catch(Throwable t){}
    try{ if(outWriter!=null&&outOpen){ outWriter.flush(); outWriter.close(); } }catch(Throwable t){}
    inOpen=false; outOpen=false;
    System.err.println("CEE3ABD abend code "+code);
    System.exit(16);
  }
  private static void p0000Main(){ p1000Initialize(); while(!endOfInput){ p2000ReadInput(); if(!endOfInput){ p3000ValidateRecord(); if(!inputError){ p4000ProcessRecord(); p5000WriteResult(); } else { p6000RejectRecord(); } } } p7000Finalize(); }
  private static void p1000Initialize(){
    String inPath=System.getenv("DD_INPUT-FILE");
    String outPath=System.getenv("DD_OUTPUT-FILE");
    try{
      if(inPath==null) throw new java.io.IOException("missing");
      inReader=new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(inPath),java.nio.charset.StandardCharsets.ISO_8859_1));
      inOpen=true; wsInStatus="00";
    }catch(Throwable e){ wsInStatus="35"; display("OPEN INPUT FAILED "+wsInStatus); returnCode=12; endOfInput=true; }
    try{
      if(outPath==null) throw new java.io.IOException("missing");
      outWriter=new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(outPath),java.nio.charset.StandardCharsets.ISO_8859_1));
      outOpen=true; wsOutStatus="00";
    }catch(Throwable e){ wsOutStatus="35"; display("OPEN OUTPUT FAILED "+wsOutStatus); returnCode=12; endOfInput=true; }
    wsRecordCount=0; wsAcceptCount=0; wsRejectCount=0;
    wsEdgeCode=spaces(24);
    p1100CheckRunControl(); p1200InitializeTotals();
  }
  private static void p1100CheckRunControl(){
    String oldStr=String.format("%06d",wsOldDate);
    String f2=oldStr.substring(0,2);
    int n=Integer.parseInt(f2);
    if(n>80) wsCentury=19; else wsCentury=20;
    String newStr=String.format("%02d",wsCentury)+oldStr;
    wsNewDate=Integer.parseInt(newStr);
  }
  private static void p1200InitializeTotals(){ wsReportTotal=java.math.BigDecimal.ZERO; wsCycleTotal=java.math.BigDecimal.ZERO; wsDebits=java.math.BigDecimal.ZERO; wsCredits=java.math.BigDecimal.ZERO; wsDelta=java.math.BigDecimal.ZERO; wsPrevKey=spaces(12); wsPrevBranch=spaces(3); }
  private static void p2000ReadInput(){
    if(!inOpen){ wsInStatus="35"; display("READ FAILURE "+wsInStatus); inputError=true; endOfInput=true; return; }
    try{
      String line=inReader.readLine();
      if(line==null){ endOfInput=true; wsInStatus="10"; return; }
      wsInStatus="00";
      wsRecordCount++;
      if(line.length()>160) line=line.substring(0,160);
      inRecord=padRight(line,160);
      wsRawBase=inRecord.substring(0,120);
      wsFeeOverride=""+wsRawBase.charAt(119);
      wsKey=inRecord.substring(0,12);
      wsBranch=inRecord.substring(12,15);
      wsAmountText=inRecord.substring(15,23);
      wsStatus=inRecord.substring(31,39);
    }catch(Throwable e){ wsInStatus="30"; display("READ FAILURE "+wsInStatus); inputError=true; endOfInput=true; }
    if(!wsInStatus.equals("00")&&!wsInStatus.equals("10")){ display("READ FAILURE "+wsInStatus); inputError=true; endOfInput=true; }
  }
  private static void p3000ValidateRecord(){
    inputError=false;
    if(wsKey.equals(spaces(12))) p3100BadKey();
    if(!isNumericX(wsAmountText)) p3200BadAmount();
    if(wsStatus.equals(spaces(8))) p3300BadStatus();
    if(!isAlphabetic(wsBranch)) p3400BadBranch();
    if(wsRecordCount>1&&cobolCompare(wsKey,wsPrevKey)<0) wsEdgeCode=padRight("SORT-ORDER",24);
    wsPrevKey=wsKey;
    p3500CheckMultiRecordType(); p3600CheckDateBoundary();
  }
  private static void p3100BadKey(){ inputError=true; wsEdgeCode=padRight("MISSING-KEY",24); }
  private static void p3200BadAmount(){ inputError=true; wsEdgeCode=padRight("INVALID-AMOUNT",24); }
  private static void p3300BadStatus(){ inputError=true; wsEdgeCode=padRight("INVALID-STATUS",24); }
  private static void p3400BadBranch(){ inputError=true; wsEdgeCode=padRight("INVALID-BRANCH",24); }
  private static void p3500CheckMultiRecordType(){
    char c1=inRecord.charAt(0);
    if(c1!='A'&&c1!='T') wsEdgeCode=padRight("UNKNOWN-RECORD-TYPE",24);
    char c2=inRecord.charAt(1);
    if(c2=='D') wsItemCount=2; else wsItemCount=1;
  }
  private static void p3600CheckDateBoundary(){
    if(wsNewDate==20261231) wsEdgeCode=padRight("YEAR-END-ROLLOVER",24);
    if(wsNewDate==20260228) wsEdgeCode=padRight("NON-LEAP-FEB-END",24);
  }
  private static void p4000ProcessRecord(){
    wsArithError=0;
    wsAmount=parseAmountText(wsAmountText);
    wsBalance=wsAmount;
    String d4=inRecord.substring(47,51);
    wsDaysInactive=parseIntSafe(d4);
    boolean cond=wsDaysInactive>=wsDormantThreshold && wsStatus.equals(padRight("DORMANT",8));
    if(cond){
      wsSweepAmount=wsBalance;
      java.math.BigDecimal res=wsBalance.subtract(wsSweepAmount);
      java.math.BigDecimal lim=new java.math.BigDecimal("999999999.99");
      if(res.abs().compareTo(lim)>0) wsArithError=1; else wsBalance=res;
      wsEdgeCode=padRight("DORMANT-SWEEP",24);
    } else {
      wsSweepAmount=java.math.BigDecimal.ZERO;
      wsEdgeCode=padRight("BELOW-THRESHOLD",24);
    }
    wsCallKey=wsKey; wsCallAmt=wsSweepAmount;
    htdateCall();
    wsCallKey=wsKey; wsCallAmt=wsSweepAmount;
    htpostCall();
    wsCallKey=wsKey; wsCallAmt=wsSweepAmount;
    htaudtCall();
    {
      java.math.BigDecimal res=wsReportTotal.add(wsSweepAmount);
      java.math.BigDecimal lim=new java.math.BigDecimal("99999999999.99");
      if(res.abs().compareTo(lim)>0) wsArithError=1; else wsReportTotal=res;
    }
    lineMove(1,12,wsKey); lineMove(13,3,wsBranch); lineMove(16,24,wsEdgeCode); lineMove(40,12,wsAmountText); lineMove(52,8,wsStatus);
    wsAcceptCount++;
  }
  private static void p5000WriteResult(){
    outRecord=lineString();
    try{
      String t=rtrim(outRecord);
      outWriter.write(t); outWriter.newLine();
      wsOutStatus="00";
    }catch(Throwable e){ wsOutStatus="30"; }
    if(!wsOutStatus.equals("00")){ display("WRITE FAILURE "+wsOutStatus); returnCode=12; endOfInput=true; } else { wsWriteTime++; }
  }
  private static void p6000RejectRecord(){
    wsRejectCount++;
    display("REJECT "+format9(wsRecordCount)+" "+wsEdgeCode);
    java.util.Arrays.fill(wsLine,' ');
    lineMove(1,6,"REJECT"); lineMove(8,12,wsKey); lineMove(24,24,wsEdgeCode);
    outRecord=lineString();
    try{ String t=rtrim(outRecord); outWriter.write(t); outWriter.newLine(); wsOutStatus="00"; }catch(Throwable e){ wsOutStatus="30"; }
  }
  private static void p7000Finalize(){ p7100CheckControlTotals(); p7200ReportRunCounts(); try{ if(inOpen) inReader.close(); wsInStatus="00"; }catch(Throwable e){ wsInStatus="30"; } try{ if(outOpen){ outWriter.flush(); outWriter.close(); } wsOutStatus="00"; }catch(Throwable e){ wsOutStatus="30"; } inOpen=false; outOpen=false; if(!wsInStatus.equals("00")) display("CLOSE IN "+wsInStatus); if(!wsOutStatus.equals("00")) display("CLOSE OUT "+wsOutStatus); if(wsRejectCount>0) returnCode=4; }
  private static void p7100CheckControlTotals(){ if(wsAcceptCount+wsRejectCount!=wsRecordCount){ display("CONTROL COUNT MISMATCH"); returnCode=12; } if(wsArithError!=0){ display("ARITHMETIC SIZE ERROR"); returnCode=8; } }
  private static void p7200ReportRunCounts(){ display("PROGRAM HTDORMSW COMPLETE"); display("READ="+format9(wsRecordCount)); display("ACCEPT="+format9(wsAcceptCount)); display("REJECT="+format9(wsRejectCount)); display("TOTAL="+formatTotal(wsReportTotal)); display("EDGE="+wsEdgeCode); }
  private static void p8000RejectAllZeroAmounts(){ if(wsAmount.compareTo(java.math.BigDecimal.ZERO)==0&&wsStatus.equals(padRight("ACTIVE",8))) wsEdgeCode=padRight("ZERO-ACTIVE-AMOUNT",24); }
  private static void p8010CheckNegativeBalance(){ if(wsBalance.compareTo(java.math.BigDecimal.ZERO)<0) wsEdgeCode=padRight("NEGATIVE-BALANCE",24); }
  private static void p8020CheckLongKey(){ if(wsKey.charAt(11)==' '){ } else { display("FULL-WIDTH-KEY "+wsKey); } }
  private static void p8030CheckRoundedAmount(){ wsPacked=wsAmount.setScale(2,java.math.RoundingMode.HALF_UP); if(wsPacked.compareTo(java.math.BigDecimal.ZERO)==0&&wsAmount.compareTo(java.math.BigDecimal.ZERO)!=0) wsEdgeCode=padRight("TRUNCATION-RISK",24); }
  private static void p8040CheckShortMove(){ wsShortAmount=padRight(wsAmountText,6).substring(0,6); String s7=wsAmountText.length()>=8?wsAmountText.substring(6,8):"  "; if(!s7.equals("00")) wsEdgeCode=padRight("SHORT-PIC-TRUNCATION",24); }
  private static void p8050CheckFeeOverlay(){ if(wsFeeOverride.equals("Y")) wsFeeWaived="Y"; else wsFeeWaived="N"; }
  private static void p8060CheckSignedZoned(){ wsZoned=wsBalance; if(wsZoned.compareTo(java.math.BigDecimal.ZERO)<0&&wsBalance.compareTo(java.math.BigDecimal.ZERO)>=0) wsEdgeCode=padRight("SIGN-CONVERSION",24); }
  private static void p8070CheckDateWindow(){ String o=String.format("%06d",wsOldDate); if(o.substring(0,2).equals("00")) wsEdgeCode=padRight("Y2K-WINDOW-REVIEW",24); if(wsOldDate==991231) wsEdgeCode=padRight("CENTURY-BOUNDARY",24); }
  private static void p8080CheckRatePrecision(){ if(wsFxRate.compareTo(new java.math.BigDecimal("1.0825"))!=0) wsEdgeCode=padRight("RATE-IS-FOUR-DECIMAL",24); }
  private static void p8090CheckRecordType(){ char c=inRecord.charAt(0); if(c=='A'||c=='T'){} else wsEdgeCode=padRight("BAD-RECORD-TYPE",24); }
  private static void p8100CheckReturnCode(){ if(!wsReturnReason.equals(spaces(3))) wsReturnCount++; if(wsReturnCount>wsRecordCount) returnCode=12; }
  private static void p8110CheckAccumulator(){ java.math.BigDecimal lim=new java.math.BigDecimal("99999999999.99"); java.math.BigDecimal r=wsReportTotal.add(wsAmount); if(r.abs().compareTo(lim)>0) wsArithError=1; else wsReportTotal=r; if(wsReportTotal.compareTo(java.math.BigDecimal.ZERO)<0) wsEdgeCode=padRight("NEGATIVE-CONTROL-TOTAL",24); }
  private static void p8120CheckBranchBreak(){ if(!wsBranch.equals(wsPrevBranch)){ p8130WriteBranchTotal(); wsPrevBranch=wsBranch; } }
  private static void p8130WriteBranchTotal(){ if(!wsPrevBranch.equals(spaces(3))){ display("BRANCH BREAK "+wsPrevBranch); display("BRANCH TOTAL "+formatTotal(wsReportTotal)); } wsReportTotal=java.math.BigDecimal.ZERO; }
  private static void p8140CheckInputShape(){ if(inRecord.equals(spaces(160))){ inputError=true; wsEdgeCode=padRight("EMPTY-RECORD",24); } if(inRecord.charAt(159)!=' ') display("TRAILING-CONTENT PRESENT"); }
  private static void p8150CheckMonthRollover(){ if(wsNewDate==20261231) wsNewDate=20270101; if(wsNewDate==20260131) wsNewDate=20260201; }
  private static void p8160CheckLeapYear(){ String s=String.format("%08d",wsNewDate); if(s.substring(0,4).equals("2024")){ wsLeapFlag="Y"; wsDayBasis=366; } else { wsLeapFlag="N"; wsDayBasis=365; } }
  private static void p8170CheckOddCents(){ String c=wsAmountText.length()>=8?wsAmountText.substring(6,8):"  "; if(c.equals("50")) wsEdgeCode=padRight("HALF-CENT-ROUNDING-CASE",24); if(c.equals("99")) wsEdgeCode=padRight("ROUND-UP-BOUNDARY",24); }
  private static void p8180CheckStatusTransition(){ if(wsStatus.equals(padRight("ACTIVE",8))||wsStatus.equals(padRight("DORMANT",8))||wsStatus.equals(padRight("BLOCKED",8))){} else wsEdgeCode=padRight("UNKNOWN-STATE",24); }
  private static void p8190CheckBalanceLimit(){ java.math.BigDecimal lim=new java.math.BigDecimal("999999999.99"); if(wsBalance.compareTo(lim)>0) wsEdgeCode=padRight("BALANCE-OVERFLOW",24); if(wsBalance.compareTo(lim.negate())<0) wsEdgeCode=padRight("BALANCE-UNDERFLOW",24); }
  private static void p8200CheckReconciliation(){ wsDelta=wsDebits.subtract(wsCredits); if(wsDelta.compareTo(java.math.BigDecimal.ZERO)==0){} }
  private static void p8210CheckCopybookCount(){ if(wsItemCount<1||wsItemCount>12) wsEdgeCode=padRight("OCCURS-COUNT-INVALID",24); }
  private static void p8220CheckCallInterface(){ lineMove(1,12,wsCallKey); lineMove(13,24,wsEdgeCode); if(lineString().charAt(0)==' ') wsEdgeCode=padRight("CALL-KEY-MISSING",24); }
  private static void p8230CheckWriteStatus(){ if(wsOutStatus.equals("00")){} else { display("DATASET STATUS "+wsOutStatus); returnCode=12; } }
  private static void p8240CheckReadStatus(){ if(wsInStatus.equals("10")) endOfInput=true; else if(!wsInStatus.equals("00")) display("INPUT STATUS "+wsInStatus); }
  private static void p8250CheckCurrencyScale(){ wsAmount=wsAmount.multiply(new java.math.BigDecimal("1.0000")).setScale(2,java.math.RoundingMode.DOWN); if(wsFxRate.compareTo(java.math.BigDecimal.ZERO)<0) wsEdgeCode=padRight("NEGATIVE-FX-RATE",24); }
  private static void p8260CheckNestedCallResult(){ wsAbendCode=0; if(wsAbendCode!=0){ display("CALLED ROUTINE RETURNED ERROR"); returnCode=8; } }
  private static void p8270CheckAuditBalance(){ java.math.BigDecimal lim=new java.math.BigDecimal("99999999999.99"); java.math.BigDecimal r=wsCycleTotal.add(wsAmount); if(r.abs().compareTo(lim)>0) wsArithError=1; else wsCycleTotal=r; }
  private static void p8280CheckKeyUniqueness(){ if(wsKey.equals(wsPrevKey)&&!wsKey.equals(spaces(12))) wsEdgeCode=padRight("DUPLICATE-KEY",24); wsPrevKey=wsKey; }
  private static void p8290CheckRunId(){ if(wsNewDate==0){ display("RUN DATE NOT INITIALIZED"); returnCode=12; } if(wsRecordCount==0) display("EMPTY INPUT"); }
  private static void p9900OperationalChecks(){ p8000RejectAllZeroAmounts(); p8010CheckNegativeBalance(); p8020CheckLongKey(); p8030CheckRoundedAmount(); p8040CheckShortMove(); p8050CheckFeeOverlay(); p8060CheckSignedZoned(); p8070CheckDateWindow(); p8080CheckRatePrecision(); p8090CheckRecordType(); p8100CheckReturnCode(); p8110CheckAccumulator(); p8120CheckBranchBreak(); p8130WriteBranchTotal(); p8140CheckInputShape(); p8150CheckMonthRollover(); p8160CheckLeapYear(); p8170CheckOddCents(); p8180CheckStatusTransition(); p8190CheckBalanceLimit(); p8200CheckReconciliation(); p8210CheckCopybookCount(); p8220CheckCallInterface(); p8230CheckWriteStatus(); p8240CheckReadStatus(); p8250CheckCurrencyScale(); p8260CheckNestedCallResult(); p8270CheckAuditBalance(); p8280CheckKeyUniqueness(); p8290CheckRunId(); }
  static void htdateCall(){ htdateLkKey=wsCallKey; htdateLkAmt=wsCallAmt; htdateLkError=0; htdateLkValue=java.math.BigDecimal.ZERO; htdateLkText=spaces(80); htdateP1000ValidateInterface(); if(htdateLkError==0) htdateP2000ProcessRequest(); htdateP3900ServiceControls(); htdateP5000ServiceValidations(); wsCallError=htdateLkError; wsCallValue=htdateLkValue; wsCallText=htdateLkText; }
  private static void htdateP1000ValidateInterface(){ if(htdateLkKey.equals(spaces(12))) htdateLkError=4; if(htdateLkAmt.compareTo(java.math.BigDecimal.ZERO)<0) htdateLkError=8; }
  private static void htdateP2000ProcessRequest(){ htdateDateRaw=991231; htdateP3000DateWindow(); htdateLkValue=new java.math.BigDecimal(htdateDateFull); }
  private static void htdateP3000DateWindow(){ String raw=String.format("%06d",htdateDateRaw); int f=Integer.parseInt(raw.substring(0,2)); if(f>80) htdateCentury=19; else htdateCentury=20; String full=String.format("%02d",htdateCentury)+raw; htdateDateFull=Integer.parseInt(full); htdateLkText=padRight(full,80); }
  private static void htdateP3100FeeWaiver(){ if(htdateLkKey.length()>=12&&htdateLkKey.charAt(11)=='Y'){ htdateLkValue=java.math.BigDecimal.ZERO; htdateLkText=padRight("WAIVED: REDEFINES FLAG SET",80); } else { try{ java.math.BigDecimal r=htdateLkAmt.multiply(new java.math.BigDecimal("0.035")); java.math.BigDecimal lim=new java.math.BigDecimal("99999999999.99"); if(r.abs().compareTo(lim)>0) htdateLkError=8; else htdateLkValue=r; }catch(Throwable e){ htdateLkError=8; } } }
  private static void htdateP3200FxQuote(){ htdateTemp=new java.math.BigDecimal("1.0825"); try{ java.math.BigDecimal r=htdateLkAmt.multiply(htdateTemp).setScale(2,java.math.RoundingMode.HALF_UP); java.math.BigDecimal lim=new java.math.BigDecimal("99999999999.99"); if(r.abs().compareTo(lim)>0) htdateLkError=8; else htdateLkValue=r; }catch(Throwable e){ htdateLkError=8; } htdateLkText=padRight("RATE SCALE 4 DECIMAL PLACES",80); }
  private static void htdateP3300DynamicCall(){ htdateCallName=padRight("HTAUDT",8); htdateLkError=8; }
  private static void htdateP3400PackedPost(){ try{ java.math.BigDecimal r=htdateLkAmt.setScale(2,java.math.RoundingMode.HALF_UP); java.math.BigDecimal lim=new java.math.BigDecimal("99999999999.99"); if(r.abs().compareTo(lim)>0) htdateLkError=8; else htdateLkValue=r; }catch(Throwable e){ htdateLkError=8; } if(htdateCounter==999999999L) abend((int)htdateCounter); }
  private static void htdateP3500AuditFormat(){ htdateLkText=padRight(htdateLkKey,80); htdateRound=htdateLkAmt; htdateLkValue=htdateRound; }
  private static void htdateP3900ServiceControls(){ htdateP4100CheckKeyLowValues(); htdateP4110CheckAmountCap(); htdateP4120CheckAmountFloor(); htdateP4130CheckResultState(); htdateP4140CheckServiceName(); htdateP4150CheckDateCalendar(); htdateP4160CheckDateDay(); htdateP4170CheckDateRollover(); htdateP4180CheckWindowLow(); htdateP4190CheckWindowHigh(); htdateP4200CheckRoundBoundary(); htdateP4210CheckPackedDecimal(); htdateP4220CheckResultConsistency(); htdateP4230CheckDynamicName(); htdateP4240CheckKeyCharacters(); htdateP4250CheckCallCounter(); htdateP4260CheckAuditLength(); htdateP4270CheckCurrencyScale(); htdateP4280CheckWaiverState(); htdateP4290CheckRateValid(); htdateP4300CheckReturnText(); htdateP4310CheckReferenceKey(); htdateP4320CheckContentIsolation(); htdateP4330CheckResultInitialized(); htdateP4340CheckDecimalRounding(); htdateP4350CheckCenturyPolicy(); htdateP4360CheckReturnCode(); }
  private static void htdateP4100CheckKeyLowValues(){ if(htdateLkKey.equals(lowValues(12))) htdateLkError=4; }
  private static void htdateP4110CheckAmountCap(){ if(htdateLkAmt.compareTo(new java.math.BigDecimal("999999999.99"))>0) htdateLkError=8; }
  private static void htdateP4120CheckAmountFloor(){ if(htdateLkAmt.compareTo(new java.math.BigDecimal("-999999999.99"))<0) htdateLkError=8; }
  private static void htdateP4130CheckResultState(){ if(htdateLkError==0) htdateLkText="OK"+htdateLkText.substring(2); }
  private static void htdateP4140CheckServiceName(){ if(htdateCallName.equals(spaces(8))) htdateLkText=padRight("SERVICE NAME UNSET",80); }
  private static void htdateP4150CheckDateCalendar(){ String s=String.format("%08d",htdateDateFull); int m=Integer.parseInt(s.substring(4,6)); if(m>12) htdateLkError=8; }
  private static void htdateP4160CheckDateDay(){ String s=String.format("%08d",htdateDateFull); int d=Integer.parseInt(s.substring(6,8)); if(d>31) htdateLkError=8; }
  private static void htdateP4170CheckDateRollover(){ if(htdateDateRaw==123199) htdateLkText=padRight("YEAR END",80); }
  private static void htdateP4180CheckWindowLow(){ String s=String.format("%06d",htdateDateRaw); if(s.substring(0,2).equals("00")) htdateCentury=20; }
  private static void htdateP4190CheckWindowHigh(){ String s=String.format("%06d",htdateDateRaw); if(Integer.parseInt(s.substring(0,2))>80) htdateCentury=19; }
  private static void htdateP4200CheckRoundBoundary(){ try{ htdateRound=htdateLkAmt.add(new java.math.BigDecimal("0.005")).setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htdateLkError=8; } }
  private static void htdateP4210CheckPackedDecimal(){ try{ htdateTemp=htdateLkAmt.setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htdateLkError=8; } }
  private static void htdateP4220CheckResultConsistency(){ if(htdateLkError!=0) htdateLkValue=java.math.BigDecimal.ZERO; }
  private static void htdateP4230CheckDynamicName(){ if(htdateCallName.equals(padRight("HTAUDT",8))){} else htdateLkError=8; }
  private static void htdateP4240CheckKeyCharacters(){ if(htdateLkKey.length()<3||!htdateLkKey.substring(0,3).equals("SYN")) htdateLkError=4; }
  private static void htdateP4250CheckCallCounter(){ htdateCounter++; }
  private static void htdateP4260CheckAuditLength(){ if(htdateLkText.equals(spaces(80))) htdateLkText=padRight("NO AUDIT DETAIL",80); }
  private static void htdateP4270CheckCurrencyScale(){ if(htdateTemp.compareTo(java.math.BigDecimal.ZERO)<0) htdateLkError=8; }
  private static void htdateP4280CheckWaiverState(){ if(htdateLkKey.length()>=12&&htdateLkKey.charAt(11)=='Y') htdateLkValue=java.math.BigDecimal.ZERO; }
  private static void htdateP4290CheckRateValid(){ if(htdateTemp.compareTo(new java.math.BigDecimal("999.9999"))>0) htdateLkError=8; }
  private static void htdateP4300CheckReturnText(){ if(htdateLkError!=0) htdateLkText=padRight("CALL FAILED",80); }
  private static void htdateP4310CheckReferenceKey(){ }
  private static void htdateP4320CheckContentIsolation(){ htdateTemp=htdateLkAmt; }
  private static void htdateP4330CheckResultInitialized(){ }
  private static void htdateP4340CheckDecimalRounding(){ if(htdateLkAmt.compareTo(new java.math.BigDecimal("0.005"))==0) htdateLkText=padRight("HALF CENT",80); }
  private static void htdateP4350CheckCenturyPolicy(){ if(htdateCentury<19||htdateCentury>20) htdateLkError=8; }
  private static void htdateP4360CheckReturnCode(){ if(htdateLkError>8) htdateLkError=8; }
  private static void htdateP5000ServiceValidations(){ htdateP5100CheckContentKey(); htdateP5110CheckLowValues(); htdateP5120CheckDecimalUpper(); htdateP5130CheckDecimalLower(); htdateP5140CheckHalfCent(); htdateP5150CheckRounding(); htdateP5160CheckResultReset(); htdateP5170CheckResultMessage(); htdateP5180CheckWindowBegin(); htdateP5190CheckWindowEnd(); htdateP5200CheckCalendarMonth(); htdateP5210CheckCalendarDay(); htdateP5220CheckRateFourPlaces(); htdateP5230CheckRateNonzero(); htdateP5240CheckFeeWaiverState(); htdateP5250CheckAuditKey(); htdateP5260CheckCallCounter(); htdateP5270CheckValueSign(); htdateP5280CheckDateRollover(); htdateP5290CheckLeapDay(); htdateP5300CheckContentCopy(); htdateP5310CheckReferenceReturn(); htdateP5320CheckDynamicCallName(); htdateP5330CheckReturnText(); htdateP5410CheckServicePostcondition(); htdateP5420CheckServicePostcondition(); htdateP5430CheckServicePostcondition(); htdateP5440CheckServicePostcondition(); htdateP5450CheckServicePostcondition(); htdateP5460CheckServicePostcondition(); htdateP5470CheckServicePostcondition(); htdateP5480CheckServicePostcondition(); htdateP5490CheckServicePostcondition(); htdateP5500CheckServicePostcondition(); htdateP5510CheckServicePostcondition(); htdateP5520CheckServicePostcondition(); htdateP5530CheckServicePostcondition(); htdateP5540CheckServicePostcondition(); htdateP5550CheckServicePostcondition(); htdateP5560CheckServicePostcondition(); htdateP5570CheckServicePostcondition(); }
  private static void htdateP5100CheckContentKey(){ if(!htdateLkKey.substring(0,3).equals("SYN")) htdateLkError=4; }
  private static void htdateP5110CheckLowValues(){ if(htdateLkKey.equals(lowValues(12))) htdateLkError=4; }
  private static void htdateP5120CheckDecimalUpper(){ if(htdateLkAmt.compareTo(new java.math.BigDecimal("999999999.99"))>0) htdateLkError=8; }
  private static void htdateP5130CheckDecimalLower(){ if(htdateLkAmt.compareTo(new java.math.BigDecimal("-999999999.99"))<0) htdateLkError=8; }
  private static void htdateP5140CheckHalfCent(){ if(htdateLkAmt.compareTo(new java.math.BigDecimal("0.005"))==0) htdateLkText=padRight("HALF CENT ROUNDING",80); }
  private static void htdateP5150CheckRounding(){ try{ htdateRound=htdateLkAmt.setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htdateLkError=8; } }
  private static void htdateP5160CheckResultReset(){ if(htdateLkError!=0) htdateLkValue=java.math.BigDecimal.ZERO; }
  private static void htdateP5170CheckResultMessage(){ if(htdateLkError!=0) htdateLkText=padRight("SERVICE ERROR",80); }
  private static void htdateP5180CheckWindowBegin(){ String s=String.format("%06d",htdateDateRaw); if(Integer.parseInt(s.substring(0,2))<=80) htdateCentury=20; }
  private static void htdateP5190CheckWindowEnd(){ String s=String.format("%06d",htdateDateRaw); if(Integer.parseInt(s.substring(0,2))>80) htdateCentury=19; }
  private static void htdateP5200CheckCalendarMonth(){ String s=String.format("%08d",htdateDateFull); if(Integer.parseInt(s.substring(4,6))>12) htdateLkError=8; }
  private static void htdateP5210CheckCalendarDay(){ String s=String.format("%08d",htdateDateFull); if(Integer.parseInt(s.substring(6,8))>31) htdateLkError=8; }
  private static void htdateP5220CheckRateFourPlaces(){ htdateTemp=new java.math.BigDecimal("1.0825"); if(htdateTemp.compareTo(new java.math.BigDecimal("1.0825"))!=0) htdateLkError=8; }
  private static void htdateP5230CheckRateNonzero(){ if(htdateTemp.compareTo(java.math.BigDecimal.ZERO)==0) htdateLkError=8; }
  private static void htdateP5240CheckFeeWaiverState(){ if(htdateLkKey.charAt(11)=='Y') htdateLkValue=java.math.BigDecimal.ZERO; }
  private static void htdateP5250CheckAuditKey(){ if(htdateLkKey.equals(spaces(12))) htdateLkText=padRight("AUDIT KEY MISSING",80); }
  private static void htdateP5260CheckCallCounter(){ htdateCounter++; }
  private static void htdateP5270CheckValueSign(){ if(htdateLkValue.compareTo(java.math.BigDecimal.ZERO)<0) htdateLkText=padRight("NEGATIVE RESULT REVIEW",80); }
  private static void htdateP5280CheckDateRollover(){ if(htdateDateRaw==991231) htdateLkText=padRight("YEAR END ROLLOVER",80); }
  private static void htdateP5290CheckLeapDay(){ if(htdateDateFull==20240229) htdateLkText=padRight("LEAP DAY",80); }
  private static void htdateP5300CheckContentCopy(){ htdateTemp=htdateLkAmt; }
  private static void htdateP5310CheckReferenceReturn(){ if(htdateLkError>8) htdateLkError=8; }
  private static void htdateP5320CheckDynamicCallName(){ if(!htdateCallName.equals(spaces(8))&&!htdateCallName.equals(padRight("HTAUDT",8))) htdateLkError=8; }
  private static void htdateP5330CheckReturnText(){ if(htdateLkText.equals(spaces(80))&&htdateLkError==0) htdateLkText="OK"+htdateLkText.substring(2); }
  private static void htdateP5410CheckServicePostcondition(){ if(htdateLkError==0){ if(htdateLkKey.substring(0,3).equals("SYN")){} else htdateLkError=4; } }
  private static void htdateP5420CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5430CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5440CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5450CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5460CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5470CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5480CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5490CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5500CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5510CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5520CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5530CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5540CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5550CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5560CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  private static void htdateP5570CheckServicePostcondition(){ htdateP5410CheckServicePostcondition(); }
  static void htpostCall(){ htpostLkKey=wsCallKey; htpostLkAmt=wsCallAmt; htpostLkError=0; htpostLkValue=java.math.BigDecimal.ZERO; htpostLkText=spaces(80); htpostP1000ValidateInterface(); if(htpostLkError==0) htpostP2000ProcessRequest(); htpostP3900ServiceControls(); wsCallError=htpostLkError; wsCallValue=htpostLkValue; wsCallText=htpostLkText; }
  private static void htpostP1000ValidateInterface(){ if(htpostLkKey.equals(spaces(12))) htpostLkError=4; if(htpostLkAmt.compareTo(java.math.BigDecimal.ZERO)<0) htpostLkError=8; }
  private static void htpostP2000ProcessRequest(){ }
  private static void htpostP3000DateWindow(){ String raw=String.format("%06d",htpostDateRaw); int f=Integer.parseInt(raw.substring(0,2)); if(f>80) htpostCentury=19; else htpostCentury=20; String full=String.format("%02d",htpostCentury)+raw; htpostDateFull=Integer.parseInt(full); htpostLkText=padRight(full,80); }
  private static void htpostP3100FeeWaiver(){ if(htpostLkKey.charAt(11)=='Y'){ htpostLkValue=java.math.BigDecimal.ZERO; htpostLkText=padRight("WAIVED: REDEFINES FLAG SET",80); } else { try{ htpostLkValue=htpostLkAmt.multiply(new java.math.BigDecimal("0.035")); }catch(Throwable e){ htpostLkError=8; } } }
  private static void htpostP3200FxQuote(){ htpostTemp=new java.math.BigDecimal("1.0825"); try{ htpostLkValue=htpostLkAmt.multiply(htpostTemp).setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htpostLkError=8; } htpostLkText=padRight("RATE SCALE 4 DECIMAL PLACES",80); }
  private static void htpostP3300DynamicCall(){ htpostCallName=padRight("HTAUDT",8); htpostLkError=8; }
  private static void htpostP3400PackedPost(){ try{ htpostLkValue=htpostLkAmt.setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htpostLkError=8; } if(htpostCounter==999999999L) abend((int)htpostCounter); }
  private static void htpostP3500AuditFormat(){ htpostLkText=padRight(htpostLkKey,80); htpostRound=htpostLkAmt; htpostLkValue=htpostRound; }
  private static void htpostP3900ServiceControls(){ htpostP4100CheckKeyLowValues(); htpostP4110CheckAmountCap(); htpostP4120CheckAmountFloor(); htpostP4130CheckResultState(); htpostP4140CheckServiceName(); htpostP4150CheckDateCalendar(); htpostP4160CheckDateDay(); htpostP4170CheckDateRollover(); htpostP4180CheckWindowLow(); htpostP4190CheckWindowHigh(); htpostP4200CheckRoundBoundary(); htpostP4210CheckPackedDecimal(); htpostP4220CheckResultConsistency(); htpostP4230CheckDynamicName(); htpostP4240CheckKeyCharacters(); htpostP4250CheckCallCounter(); htpostP4260CheckAuditLength(); htpostP4270CheckCurrencyScale(); htpostP4280CheckWaiverState(); htpostP4290CheckRateValid(); htpostP4300CheckReturnText(); htpostP4310CheckReferenceKey(); htpostP4320CheckContentIsolation(); htpostP4330CheckResultInitialized(); htpostP4340CheckDecimalRounding(); htpostP4350CheckCenturyPolicy(); htpostP4360CheckReturnCode(); }
  private static void htpostP4100CheckKeyLowValues(){ if(htpostLkKey.equals(lowValues(12))) htpostLkError=4; }
  private static void htpostP4110CheckAmountCap(){ if(htpostLkAmt.compareTo(new java.math.BigDecimal("999999999.99"))>0) htpostLkError=8; }
  private static void htpostP4120CheckAmountFloor(){ if(htpostLkAmt.compareTo(new java.math.BigDecimal("-999999999.99"))<0) htpostLkError=8; }
  private static void htpostP4130CheckResultState(){ if(htpostLkError==0) htpostLkText="OK"+htpostLkText.substring(2); }
  private static void htpostP4140CheckServiceName(){ if(htpostCallName.equals(spaces(8))) htpostLkText=padRight("SERVICE NAME UNSET",80); }
  private static void htpostP4150CheckDateCalendar(){ String s=String.format("%08d",htpostDateFull); if(Integer.parseInt(s.substring(4,6))>12) htpostLkError=8; }
  private static void htpostP4160CheckDateDay(){ String s=String.format("%08d",htpostDateFull); if(Integer.parseInt(s.substring(6,8))>31) htpostLkError=8; }
  private static void htpostP4170CheckDateRollover(){ if(htpostDateRaw==123199) htpostLkText=padRight("YEAR END",80); }
  private static void htpostP4180CheckWindowLow(){ String s=String.format("%06d",htpostDateRaw); if(s.substring(0,2).equals("00")) htpostCentury=20; }
  private static void htpostP4190CheckWindowHigh(){ String s=String.format("%06d",htpostDateRaw); if(Integer.parseInt(s.substring(0,2))>80) htpostCentury=19; }
  private static void htpostP4200CheckRoundBoundary(){ try{ htpostRound=htpostLkAmt.add(new java.math.BigDecimal("0.005")).setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htpostLkError=8; } }
  private static void htpostP4210CheckPackedDecimal(){ try{ htpostTemp=htpostLkAmt.setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htpostLkError=8; } }
  private static void htpostP4220CheckResultConsistency(){ if(htpostLkError!=0) htpostLkValue=java.math.BigDecimal.ZERO; }
  private static void htpostP4230CheckDynamicName(){ if(htpostCallName.equals(padRight("HTAUDT",8))){} else htpostLkError=8; }
  private static void htpostP4240CheckKeyCharacters(){ if(!htpostLkKey.substring(0,3).equals("SYN")) htpostLkError=4; }
  private static void htpostP4250CheckCallCounter(){ htpostCounter++; }
  private static void htpostP4260CheckAuditLength(){ if(htpostLkText.equals(spaces(80))) htpostLkText=padRight("NO AUDIT DETAIL",80); }
  private static void htpostP4270CheckCurrencyScale(){ if(htpostTemp.compareTo(java.math.BigDecimal.ZERO)<0) htpostLkError=8; }
  private static void htpostP4280CheckWaiverState(){ if(htpostLkKey.charAt(11)=='Y') htpostLkValue=java.math.BigDecimal.ZERO; }
  private static void htpostP4290CheckRateValid(){ if(htpostTemp.compareTo(new java.math.BigDecimal("999.9999"))>0) htpostLkError=8; }
  private static void htpostP4300CheckReturnText(){ if(htpostLkError!=0) htpostLkText=padRight("CALL FAILED",80); }
  private static void htpostP4310CheckReferenceKey(){ }
  private static void htpostP4320CheckContentIsolation(){ htpostTemp=htpostLkAmt; }
  private static void htpostP4330CheckResultInitialized(){ }
  private static void htpostP4340CheckDecimalRounding(){ if(htpostLkAmt.compareTo(new java.math.BigDecimal("0.005"))==0) htpostLkText=padRight("HALF CENT",80); }
  private static void htpostP4350CheckCenturyPolicy(){ if(htpostCentury<19||htpostCentury>20) htpostLkError=8; }
  private static void htpostP4360CheckReturnCode(){ if(htpostLkError>8) htpostLkError=8; }
  private static void htpostP5410CheckServicePostcondition(){ if(htpostLkError==0){ if(htpostLkKey.substring(0,3).equals("SYN")){} else htpostLkError=4; } }
  private static void htpostP5420CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5430CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5440CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5450CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5460CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5470CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5480CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5490CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5500CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5510CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5520CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5530CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5540CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5550CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5560CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5570CheckServicePostcondition(){ htpostP5410CheckServicePostcondition(); }
  private static void htpostP5610CheckCallerBoundary(){ if(htpostLkKey.substring(0,3).equals("SYN")){} else htpostLkError=4; }
  private static void htpostP5620CheckCallerBoundary(){ htpostP5610CheckCallerBoundary(); }
  private static void htpostP5630CheckCallerBoundary(){ htpostP5610CheckCallerBoundary(); }
  private static void htpostP5640CheckCallerBoundary(){ htpostP5610CheckCallerBoundary(); }
  private static void htpostP5650CheckCallerBoundary(){ htpostP5610CheckCallerBoundary(); }
  private static void htpostP5660CheckCallerBoundary(){ htpostP5610CheckCallerBoundary(); }
  private static void htpostP5670CheckCallerBoundary(){ htpostP5610CheckCallerBoundary(); }
  static void htaudtCall(){ htaudtLkKey=wsCallKey; htaudtLkAmt=wsCallAmt; htaudtLkError=0; htaudtLkValue=java.math.BigDecimal.ZERO; htaudtLkText=spaces(80); htaudtP1000ValidateInterface(); if(htaudtLkError==0) htaudtP2000ProcessRequest(); htaudtP3900ServiceControls(); wsCallError=htaudtLkError; wsCallValue=htaudtLkValue; wsCallText=htaudtLkText; }
  private static void htaudtP1000ValidateInterface(){ if(htaudtLkKey.equals(spaces(12))) htaudtLkError=4; if(htaudtLkAmt.compareTo(java.math.BigDecimal.ZERO)<0) htaudtLkError=8; }
  private static void htaudtP2000ProcessRequest(){ }
  private static void htaudtP3000DateWindow(){ String raw=String.format("%06d",htaudtDateRaw); int f=Integer.parseInt(raw.substring(0,2)); if(f>80) htaudtCentury=19; else htaudtCentury=20; String full=String.format("%02d",htaudtCentury)+raw; htaudtDateFull=Integer.parseInt(full); htaudtLkText=padRight(full,80); }
  private static void htaudtP3100FeeWaiver(){ if(htaudtLkKey.charAt(11)=='Y'){ htaudtLkValue=java.math.BigDecimal.ZERO; htaudtLkText=padRight("WAIVED: REDEFINES FLAG SET",80); } else { try{ htaudtLkValue=htaudtLkAmt.multiply(new java.math.BigDecimal("0.035")); }catch(Throwable e){ htaudtLkError=8; } } }
  private static void htaudtP3200FxQuote(){ htaudtTemp=new java.math.BigDecimal("1.0825"); try{ htaudtLkValue=htaudtLkAmt.multiply(htaudtTemp).setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htaudtLkError=8; } htaudtLkText=padRight("RATE SCALE 4 DECIMAL PLACES",80); }
  private static void htaudtP3300DynamicCall(){ htaudtCallName=padRight("HTAUDT",8); htaudtLkError=8; }
  private static void htaudtP3400PackedPost(){ try{ htaudtLkValue=htaudtLkAmt.setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htaudtLkError=8; } abend((int)htaudtCounter); }
  private static void htaudtP3500AuditFormat(){ htaudtLkText=padRight(htaudtLkKey,80); htaudtRound=htaudtLkAmt; htaudtLkValue=htaudtRound; }
  private static void htaudtP3900ServiceControls(){ htaudtP4100CheckKeyLowValues(); htaudtP4110CheckAmountCap(); htaudtP4120CheckAmountFloor(); htaudtP4130CheckResultState(); htaudtP4140CheckServiceName(); htaudtP4150CheckDateCalendar(); htaudtP4160CheckDateDay(); htaudtP4170CheckDateRollover(); htaudtP4180CheckWindowLow(); htaudtP4190CheckWindowHigh(); htaudtP4200CheckRoundBoundary(); htaudtP4210CheckPackedDecimal(); htaudtP4220CheckResultConsistency(); htaudtP4230CheckDynamicName(); htaudtP4240CheckKeyCharacters(); htaudtP4250CheckCallCounter(); htaudtP4260CheckAuditLength(); htaudtP4270CheckCurrencyScale(); htaudtP4280CheckWaiverState(); htaudtP4290CheckRateValid(); htaudtP4300CheckReturnText(); htaudtP4310CheckReferenceKey(); htaudtP4320CheckContentIsolation(); htaudtP4330CheckResultInitialized(); htaudtP4340CheckDecimalRounding(); htaudtP4350CheckCenturyPolicy(); htaudtP4360CheckReturnCode(); }
  private static void htaudtP4100CheckKeyLowValues(){ if(htaudtLkKey.equals(lowValues(12))) htaudtLkError=4; }
  private static void htaudtP4110CheckAmountCap(){ if(htaudtLkAmt.compareTo(new java.math.BigDecimal("999999999.99"))>0) htaudtLkError=8; }
  private static void htaudtP4120CheckAmountFloor(){ if(htaudtLkAmt.compareTo(new java.math.BigDecimal("-999999999.99"))<0) htaudtLkError=8; }
  private static void htaudtP4130CheckResultState(){ if(htaudtLkError==0) htaudtLkText="OK"+htaudtLkText.substring(2); }
  private static void htaudtP4140CheckServiceName(){ if(htaudtCallName.equals(spaces(8))) htaudtLkText=padRight("SERVICE NAME UNSET",80); }
  private static void htaudtP4150CheckDateCalendar(){ String s=String.format("%08d",htaudtDateFull); if(Integer.parseInt(s.substring(4,6))>12) htaudtLkError=8; }
  private static void htaudtP4160CheckDateDay(){ String s=String.format("%08d",htaudtDateFull); if(Integer.parseInt(s.substring(6,8))>31) htaudtLkError=8; }
  private static void htaudtP4170CheckDateRollover(){ if(htaudtDateRaw==123199) htaudtLkText=padRight("YEAR END",80); }
  private static void htaudtP4180CheckWindowLow(){ String s=String.format("%06d",htaudtDateRaw); if(s.substring(0,2).equals("00")) htaudtCentury=20; }
  private static void htaudtP4190CheckWindowHigh(){ String s=String.format("%06d",htaudtDateRaw); if(Integer.parseInt(s.substring(0,2))>80) htaudtCentury=19; }
  private static void htaudtP4200CheckRoundBoundary(){ try{ htaudtRound=htaudtLkAmt.add(new java.math.BigDecimal("0.005")).setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htaudtLkError=8; } }
  private static void htaudtP4210CheckPackedDecimal(){ try{ htaudtTemp=htaudtLkAmt.setScale(2,java.math.RoundingMode.HALF_UP); }catch(Throwable e){ htaudtLkError=8; } }
  private static void htaudtP4220CheckResultConsistency(){ if(htaudtLkError!=0) htaudtLkValue=java.math.BigDecimal.ZERO; }
  private static void htaudtP4230CheckDynamicName(){ if(htaudtCallName.equals(padRight("HTAUDT",8))){} else htaudtLkError=8; }
  private static void htaudtP4240CheckKeyCharacters(){ if(!htaudtLkKey.substring(0,3).equals("SYN")) htaudtLkError=4; }
  private static void htaudtP4250CheckCallCounter(){ htaudtCounter++; }
  private static void htaudtP4260CheckAuditLength(){ if(htaudtLkText.equals(spaces(80))) htaudtLkText=padRight("NO AUDIT DETAIL",80); }
  private static void htaudtP4270CheckCurrencyScale(){ if(htaudtTemp.compareTo(java.math.BigDecimal.ZERO)<0) htaudtLkError=8; }
  private static void htaudtP4280CheckWaiverState(){ if(htaudtLkKey.charAt(11)=='Y') htaudtLkValue=java.math.BigDecimal.ZERO; }
  private static void htaudtP4290CheckRateValid(){ if(htaudtTemp.compareTo(new java.math.BigDecimal("999.9999"))>0) htaudtLkError=8; }
  private static void htaudtP4300CheckReturnText(){ if(htaudtLkError!=0) htaudtLkText=padRight("CALL FAILED",80); }
  private static void htaudtP4310CheckReferenceKey(){ }
  private static void htaudtP4320CheckContentIsolation(){ htaudtTemp=htaudtLkAmt; }
  private static void htaudtP4330CheckResultInitialized(){ }
  private static void htaudtP4340CheckDecimalRounding(){ if(htaudtLkAmt.compareTo(new java.math.BigDecimal("0.005"))==0) htaudtLkText=padRight("HALF CENT",80); }
  private static void htaudtP4350CheckCenturyPolicy(){ if(htaudtCentury<19||htaudtCentury>20) htaudtLkError=8; }
  private static void htaudtP4360CheckReturnCode(){ if(htaudtLkError>8) htaudtLkError=8; }
  private static void htaudtP5410CheckServicePostcondition(){ if(htaudtLkError==0){ if(htaudtLkKey.substring(0,3).equals("SYN")){} else htaudtLkError=4; } }
  private static void htaudtP5420CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5430CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5440CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5450CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5460CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5470CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5480CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5490CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5500CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5510CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5520CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5530CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5540CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5550CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5560CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5570CheckServicePostcondition(){ htaudtP5410CheckServicePostcondition(); }
  private static void htaudtP5610CheckCallerBoundary(){ if(htaudtLkKey.substring(0,3).equals("SYN")){} else htaudtLkError=4; }
  private static void htaudtP5620CheckCallerBoundary(){ htaudtP5610CheckCallerBoundary(); }
  private static void htaudtP5630CheckCallerBoundary(){ htaudtP5610CheckCallerBoundary(); }
  private static void htaudtP5640CheckCallerBoundary(){ htaudtP5610CheckCallerBoundary(); }
  private static void htaudtP5650CheckCallerBoundary(){ htaudtP5610CheckCallerBoundary(); }
  private static void htaudtP5660CheckCallerBoundary(){ htaudtP5610CheckCallerBoundary(); }
  private static void htaudtP5670CheckCallerBoundary(){ htaudtP5610CheckCallerBoundary(); }
}

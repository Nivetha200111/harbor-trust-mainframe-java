public class Htfxconv {
  private String wsInStatus = "  ";
  private String wsOutStatus = "  ";
  private boolean endOfInput = false;
  private boolean inputError = false;
  private long wsRecordCount = 0;
  private long wsAcceptCount = 0;
  private long wsRejectCount = 0;
  private int wsArithError = 0;
  private String wsEdgeCode = spaces(24);
  private String wsKey = spaces(12);
  private String wsPrevKey = spaces(12);
  private String wsBranch = spaces(3);
  private String wsPrevBranch = spaces(3);
  private String wsStatus = spaces(8);
  private java.math.BigDecimal wsAmount = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsBalance = java.math.BigDecimal.ZERO;
  private long wsReadTime = 0;
  private long wsWriteTime = 0;
  private int wsAbendCode = 0;
  private String wsReturnReason = spaces(3);
  private long wsReturnCount = 0;
  private java.math.BigDecimal wsCycleTotal = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsDebits = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsCredits = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsDelta = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsReportTotal = java.math.BigDecimal.ZERO;
  private String wsAmountText = spaces(8);
  private String wsShortAmount = spaces(6);
  private String wsRawBase = spaces(120);
  private char wsFeeOverride = ' ';
  private int wsItemCount = 0;
  private String[] wsItems = new String[12];
  private java.math.BigDecimal wsPacked = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsZoned = java.math.BigDecimal.ZERO;
  private String wsLine = spaces(200);
  private java.math.BigDecimal wsFxRate = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsBaseAmt = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsConvAmt = java.math.BigDecimal.ZERO;
  private String wsBaseCcy = spaces(3);
  private String wsQuoteCcy = spaces(3);
  private java.math.BigDecimal wsFee = java.math.BigDecimal.ZERO;
  private String wsFeeWaived = "N";
  private String wsOldDate = "991231";
  private String wsCentury = "20";
  private String wsNewDate = "00000000";
  private int wsDayBasis = 365;
  private String wsLeapFlag = "N";
  private String wsCallKey = spaces(12);
  private java.math.BigDecimal wsCallAmt = java.math.BigDecimal.ZERO;
  private int wsCallError = 0;
  private java.math.BigDecimal wsCallValue = java.math.BigDecimal.ZERO;
  private String wsCallText = spaces(80);
  private String inRecord = spaces(160);
  private String outRecord = spaces(200);
  private int returnCode = 0;
  private java.io.BufferedReader inReader = null;
  private java.io.BufferedWriter outWriter = null;
  private boolean inOpen = false;
  private boolean outOpen = false;
  private java.math.BigDecimal hrtTemp = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal hrtRound = java.math.BigDecimal.ZERO;
  private long hrtCounter = 0;
  private String hrtCallName = spaces(8);
  private String hrtDateRaw = "000000";
  private String hrtDateFull = "00000000";
  private String hrtCentury = "20";
  private java.math.BigDecimal hatTemp = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal hatRound = java.math.BigDecimal.ZERO;
  private long hatCounter = 0;
  private String hatCallName = spaces(8);
  private String hatDateRaw = "000000";
  private String hatDateFull = "00000000";
  private String hatCentury = "20";
  public static void main(String[] args) {
    Htfxconv p = new Htfxconv();
    p.run();
  }
  private void run() {
    String clk = System.getenv("DIOCLE_CLOCK");
    if (clk != null && clk.length() == 0) System.out.print("");
    try { p0000Main(); }
    catch (Throwable t) {
      try { if (outWriter != null && outOpen) { try { outWriter.flush(); outWriter.close(); } catch (Exception e) {} } } catch (Exception e) {}
      try { if (inReader != null && inOpen) { try { inReader.close(); } catch (Exception e) {} } } catch (Exception e) {}
      System.exit(12);
    }
    if (returnCode != 0) System.exit(returnCode);
  }
  private static String spaces(int n) { StringBuilder sb = new StringBuilder(n); for (int i=0;i<n;i++) sb.append(' '); return sb.toString(); }
  private static String padRight(String s,int n) { if (s==null) s=""; if (s.length()>n) return s.substring(0,n); if (s.length()<n) { StringBuilder sb=new StringBuilder(s); while(sb.length()<n) sb.append(' '); return sb.toString(); } return s; }
  private static String rtrim(String s) { int e=s.length(); while(e>0 && s.charAt(e-1)==' ') e--; return s.substring(0,e); }
  private static boolean isSpaces(String s) { for(int i=0;i<s.length();i++) if(s.charAt(i)!=' ') return false; return true; }
  private static boolean isNumericX(String s) { if(s==null||s.length()==0) return false; for(int i=0;i<s.length();i++){char c=s.charAt(i); if(c<'0'||c>'9') return false;} return true; }
  private static boolean isAlphabeticX(String s) { for(int i=0;i<s.length();i++){char c=s.charAt(i); if(c==' ') continue; if((c>='A'&&c<='Z')||(c>='a'&&c<='z')) continue; return false;} return true; }
  private int compareAlpha(String a,String b) {
    String col = System.getenv("DIOCLE_COLLATION");
    if ("EBCDIC".equals(col)) {
      try {
        java.nio.charset.Charset cs = java.nio.charset.Charset.forName("Cp037");
        byte[] ab = a.getBytes(cs); byte[] bb = b.getBytes(cs);
        int n = Math.min(ab.length, bb.length);
        for(int i=0;i<n;i++){int x=ab[i]&0xFF; int y=bb[i]&0xFF; if(x!=y) return x-y;}
        return ab.length-bb.length;
      } catch(Exception e){}
    }
    return a.compareTo(b);
  }
  private String formatCount(long v) { long m = v & 0xFFFFFFFFL; String s=Long.toString(m); while(s.length()<10) s="0"+s; if(s.length()>10) s=s.substring(s.length()-10); return s; }
  private String formatTotal(java.math.BigDecimal v) {
    java.math.BigDecimal sc = v.setScale(2, java.math.RoundingMode.HALF_UP);
    boolean neg = sc.signum()<0;
    String d = sc.abs().unscaledValue().toString();
    while(d.length()<13) d="0"+d;
    if(d.length()>13) d=d.substring(d.length()-13);
    String ip = d.substring(0,11); String dp = d.substring(11,13);
    return (neg?"-":"+")+ip+"."+dp;
  }
  private void setSlice(char[] buf,int start,int len,String val) { for(int i=0;i<len;i++){ char c=' '; if(val!=null && i<val.length()) c=val.charAt(i); buf[start+i]=c; } }
  private void abend(int code) {
    try { System.out.flush(); } catch(Exception e){}
    try { if(inOpen && inReader!=null){ try{inReader.close();}catch(Exception e){} inOpen=false; } } catch(Exception e){}
    try { if(outOpen && outWriter!=null){ try{outWriter.flush(); outWriter.close();}catch(Exception e){} outOpen=false; } } catch(Exception e){}
    try { System.err.println("CEE3ABD abend code "+code); } catch(Exception e){}
    System.exit(16);
  }
  private void p0000Main() {
    p1000Initialize();
    while(!endOfInput) {
      p2000ReadInput();
      if(!endOfInput) {
        p3000ValidateRecord();
        if(!inputError) { p4000ProcessRecord(); p5000WriteResult(); }
        else { p6000RejectRecord(); }
      }
    }
    p7000Finalize();
  }
  private void p1000Initialize() {
    String inPath = System.getenv("DD_INPUT-FILE");
    String outPath = System.getenv("DD_OUTPUT-FILE");
    if(inPath==null||inPath.isEmpty()){ wsInStatus="35"; System.out.println("OPEN INPUT FAILED "+wsInStatus); returnCode=12; endOfInput=true; }
    else {
      try { inReader = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get(inPath), java.nio.charset.Charset.forName("ISO-8859-1")); wsInStatus="00"; inOpen=true; }
      catch(Exception e){ wsInStatus="35"; System.out.println("OPEN INPUT FAILED "+wsInStatus); returnCode=12; endOfInput=true; }
    }
    if(outPath==null||outPath.isEmpty()){ wsOutStatus="35"; System.out.println("OPEN OUTPUT FAILED "+wsOutStatus); returnCode=12; endOfInput=true; }
    else {
      try { outWriter = java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(outPath), java.nio.charset.Charset.forName("ISO-8859-1"), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE); wsOutStatus="00"; outOpen=true; }
      catch(Exception e){ wsOutStatus="35"; System.out.println("OPEN OUTPUT FAILED "+wsOutStatus); returnCode=12; endOfInput=true; }
    }
    wsRecordCount=0; wsAcceptCount=0; wsRejectCount=0;
    wsEdgeCode=spaces(24);
    p1100CheckRunControl();
    p1200InitializeTotals();
  }
  private void p1100CheckRunControl() {
    int v=0; try{v=Integer.parseInt(wsOldDate.substring(0,2));}catch(Exception e){v=0;}
    if(v>80) wsCentury="19"; else wsCentury="20";
    char[] nd = wsNewDate.toCharArray();
    nd[0]=wsCentury.charAt(0); nd[1]=wsCentury.charAt(1);
    for(int i=0;i<6;i++) nd[2+i]=wsOldDate.charAt(i);
    wsNewDate=new String(nd);
  }
  private void p1200InitializeTotals() {
    wsReportTotal=java.math.BigDecimal.ZERO; wsCycleTotal=java.math.BigDecimal.ZERO; wsDebits=java.math.BigDecimal.ZERO; wsCredits=java.math.BigDecimal.ZERO; wsDelta=java.math.BigDecimal.ZERO;
    wsPrevKey=spaces(12); wsPrevBranch=spaces(3);
  }
  private void p2000ReadInput() {
    if(!inOpen||inReader==null){ if(!endOfInput){ wsInStatus="35"; System.out.println("READ FAILURE "+wsInStatus); inputError=true; endOfInput=true; } return; }
    try {
      String line = inReader.readLine();
      if(line==null){ endOfInput=true; wsInStatus="10"; }
      else {
        wsInStatus="00";
        wsRecordCount++;
        if(line.length()>160) inRecord=line.substring(0,160);
        else inRecord=padRight(line,160);
        wsRawBase=inRecord.substring(0,120);
        wsFeeOverride=wsRawBase.charAt(119);
        wsKey=inRecord.substring(0,12);
        wsBranch=inRecord.substring(12,15);
        wsAmountText=inRecord.substring(15,23);
        wsStatus=inRecord.substring(31,39);
      }
    } catch(Exception e){ wsInStatus="30"; System.out.println("READ FAILURE "+wsInStatus); inputError=true; endOfInput=true; return; }
    if(!wsInStatus.equals("00") && !wsInStatus.equals("10")) { System.out.println("READ FAILURE "+wsInStatus); inputError=true; endOfInput=true; }
  }
  private void p3000ValidateRecord() {
    inputError=false;
    if(isSpaces(wsKey)) p3100BadKey();
    if(!isNumericX(wsAmountText)) p3200BadAmount();
    if(isSpaces(wsStatus)) p3300BadStatus();
    if(!isAlphabeticX(wsBranch)) p3400BadBranch();
    if(wsRecordCount>1 && compareAlpha(wsKey,wsPrevKey)<0) wsEdgeCode=padRight("SORT-ORDER",24);
    wsPrevKey=wsKey;
    p3500CheckMultiRecordType();
    p3600CheckDateBoundary();
  }
  private void p3100BadKey() { inputError=true; wsEdgeCode=padRight("MISSING-KEY",24); }
  private void p3200BadAmount() { inputError=true; wsEdgeCode=padRight("INVALID-AMOUNT",24); }
  private void p3300BadStatus() { inputError=true; wsEdgeCode=padRight("INVALID-STATUS",24); }
  private void p3400BadBranch() { inputError=true; wsEdgeCode=padRight("INVALID-BRANCH",24); }
  private void p3500CheckMultiRecordType() {
    char c0=inRecord.charAt(0);
    if(c0!='A' && c0!='T') wsEdgeCode=padRight("UNKNOWN-RECORD-TYPE",24);
    char c1=inRecord.charAt(1);
    if(c1=='D') wsItemCount=2; else wsItemCount=1;
  }
  private void p3600CheckDateBoundary() {
    if(wsNewDate.equals("20261231")) wsEdgeCode=padRight("YEAR-END-ROLLOVER",24);
    if(wsNewDate.equals("20260228")) wsEdgeCode=padRight("NON-LEAP-FEB-END",24);
  }
  private void p4000ProcessRecord() {
    wsArithError=0;
    try { wsAmount=new java.math.BigDecimal(wsAmountText); } catch(Exception e){ wsAmount=java.math.BigDecimal.ZERO; }
    wsBaseAmt=wsAmount;
    wsFxRate=new java.math.BigDecimal("1.0825");
    wsBaseCcy=inRecord.substring(55,58);
    wsQuoteCcy=inRecord.substring(58,61);
    if(wsFxRate.compareTo(java.math.BigDecimal.ZERO)<=0){ wsEdgeCode=padRight("INVALID-FX-RATE",24); wsConvAmt=java.math.BigDecimal.ZERO; }
    else {
      java.math.BigDecimal raw=wsBaseAmt.multiply(wsFxRate);
      java.math.BigDecimal rounded=raw.setScale(2, java.math.RoundingMode.HALF_UP);
      if(rounded.abs().compareTo(new java.math.BigDecimal("999999999.99"))>0){ wsArithError=1; }
      else wsConvAmt=rounded;
      wsEdgeCode=padRight("RATE-SCALE-4DP",24);
    }
    wsCallKey=wsKey; wsCallAmt=wsBaseAmt; callHtfxrt();
    wsCallKey=wsKey; wsCallAmt=wsConvAmt; callHtaudt();
    java.math.BigDecimal sum=wsReportTotal.add(wsConvAmt);
    if(sum.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsArithError=1; else wsReportTotal=sum;
    char[] buf=padRight(wsLine,200).toCharArray();
    setSlice(buf,0,12,wsKey); setSlice(buf,12,3,wsBranch); setSlice(buf,15,24,wsEdgeCode);
    setSlice(buf,39,12,wsAmountText); setSlice(buf,51,8,wsStatus);
    wsLine=new String(buf);
    wsAcceptCount++;
  }
  private void p5000WriteResult() {
    outRecord=wsLine;
    try {
      String t=rtrim(outRecord);
      outWriter.write(t); outWriter.write("\n");
      wsOutStatus="00"; wsWriteTime++;
    } catch(Exception e){ wsOutStatus="30"; System.out.println("WRITE FAILURE "+wsOutStatus); returnCode=12; endOfInput=true; return; }
    if(!wsOutStatus.equals("00")){ System.out.println("WRITE FAILURE "+wsOutStatus); returnCode=12; endOfInput=true; }
  }
  private void p6000RejectRecord() {
    wsRejectCount++;
    System.out.println("REJECT "+formatCount(wsRecordCount)+" "+wsEdgeCode);
    char[] buf=spaces(200).toCharArray();
    setSlice(buf,0,6,"REJECT"); setSlice(buf,7,12,wsKey); setSlice(buf,23,24,wsEdgeCode);
    wsLine=new String(buf);
    outRecord=wsLine;
    try { String t=rtrim(outRecord); outWriter.write(t); outWriter.write("\n"); wsOutStatus="00"; } catch(Exception e){ try{wsOutStatus="30";}catch(Exception ex){} }
  }
  private void p7000Finalize() {
    p7100CheckControlTotals();
    p7200ReportRunCounts();
    try { if(inOpen&&inReader!=null){ try{inReader.close();}catch(Exception e){} inOpen=false; wsInStatus="00"; } } catch(Exception e){ wsInStatus="30"; }
    try { if(outOpen&&outWriter!=null){ try{outWriter.flush(); outWriter.close();}catch(Exception e){} outOpen=false; if(wsOutStatus.equals("00")||wsOutStatus.equals("  ")) wsOutStatus="00"; } } catch(Exception e){}
    if(!wsInStatus.equals("00")) System.out.println("CLOSE IN "+wsInStatus);
    if(!wsOutStatus.equals("00")) System.out.println("CLOSE OUT "+wsOutStatus);
    if(wsRejectCount>0) returnCode=4;
  }
  private void p7100CheckControlTotals() {
    if(wsAcceptCount+wsRejectCount!=wsRecordCount){ System.out.println("CONTROL COUNT MISMATCH"); returnCode=12; }
    if(wsArithError!=0){ System.out.println("ARITHMETIC SIZE ERROR"); returnCode=8; }
  }
  private void p7200ReportRunCounts() {
    System.out.println("PROGRAM HTFXCONV COMPLETE");
    System.out.println("READ="+formatCount(wsRecordCount));
    System.out.println("ACCEPT="+formatCount(wsAcceptCount));
    System.out.println("REJECT="+formatCount(wsRejectCount));
    System.out.println("TOTAL="+formatTotal(wsReportTotal));
    System.out.println("EDGE="+wsEdgeCode);
  }
  private void p8000RejectAllZeroAmounts() { if(wsAmount.compareTo(java.math.BigDecimal.ZERO)==0 && padRight(wsStatus,8).equals(padRight("ACTIVE",8))) wsEdgeCode=padRight("ZERO-ACTIVE-AMOUNT",24); }
  private void p8010CheckNegativeBalance() { if(wsBalance.compareTo(java.math.BigDecimal.ZERO)<0) wsEdgeCode=padRight("NEGATIVE-BALANCE",24); }
  private void p8020CheckLongKey() { if(wsKey.charAt(11)==' ') {} else System.out.println("FULL-WIDTH-KEY "+wsKey); }
  private void p8030CheckRoundedAmount() { java.math.BigDecimal r=wsAmount.setScale(2,java.math.RoundingMode.HALF_UP); if(r.abs().compareTo(new java.math.BigDecimal("999999999.99"))>0) wsArithError=1; else wsPacked=r; if(wsPacked.compareTo(java.math.BigDecimal.ZERO)==0 && wsAmount.compareTo(java.math.BigDecimal.ZERO)!=0) wsEdgeCode=padRight("TRUNCATION-RISK",24); }
  private void p8040CheckShortMove() { wsShortAmount=padRight(wsAmountText,6).substring(0,6); if(!wsAmountText.substring(6,8).equals("00")) wsEdgeCode=padRight("SHORT-PIC-TRUNCATION",24); }
  private void p8050CheckFeeOverlay() { if(wsFeeOverride=='Y') wsFeeWaived="Y"; else wsFeeWaived="N"; }
  private void p8060CheckSignedZoned() { wsZoned=wsBalance; if(wsZoned.compareTo(java.math.BigDecimal.ZERO)<0 && wsBalance.compareTo(java.math.BigDecimal.ZERO)>=0) wsEdgeCode=padRight("SIGN-CONVERSION",24); }
  private void p8070CheckDateWindow() { if(wsOldDate.substring(0,2).equals("00")) wsEdgeCode=padRight("Y2K-WINDOW-REVIEW",24); if(wsOldDate.equals("991231")) wsEdgeCode=padRight("CENTURY-BOUNDARY",24); }
  private void p8080CheckRatePrecision() { if(wsFxRate.compareTo(new java.math.BigDecimal("1.0825"))!=0) wsEdgeCode=padRight("RATE-IS-FOUR-DECIMAL",24); }
  private void p8090CheckRecordType() { char c=inRecord.charAt(0); if(c=='A'||c=='T'){} else wsEdgeCode=padRight("BAD-RECORD-TYPE",24); }
  private void p8100CheckReturnCode() { if(!wsReturnReason.equals(spaces(3))) wsReturnCount++; if(wsReturnCount>wsRecordCount) returnCode=12; }
  private void p8110CheckAccumulator() { java.math.BigDecimal s=wsReportTotal.add(wsAmount); if(s.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsArithError=1; else wsReportTotal=s; if(wsReportTotal.compareTo(java.math.BigDecimal.ZERO)<0) wsEdgeCode=padRight("NEGATIVE-CONTROL-TOTAL",24); }
  private void p8120CheckBranchBreak() { if(!wsBranch.equals(wsPrevBranch)){ p8130WriteBranchTotal(); wsPrevBranch=wsBranch; } }
  private void p8130WriteBranchTotal() { if(!wsPrevBranch.equals(spaces(3))){ System.out.println("BRANCH BREAK "+wsPrevBranch); System.out.println("BRANCH TOTAL "+formatTotal(wsReportTotal)); } wsReportTotal=java.math.BigDecimal.ZERO; }
  private void p8140CheckInputShape() { if(isSpaces(inRecord)){inputError=true; wsEdgeCode=padRight("EMPTY-RECORD",24);} if(inRecord.charAt(159)!=' ') System.out.println("TRAILING-CONTENT PRESENT"); }
  private void p8150CheckMonthRollover() { if(wsNewDate.equals("20261231")) wsNewDate="20270101"; if(wsNewDate.equals("20260131")) wsNewDate="20260201"; }
  private void p8160CheckLeapYear() { if(wsNewDate.substring(0,4).equals("2024")){wsLeapFlag="Y"; wsDayBasis=366;} else {wsLeapFlag="N"; wsDayBasis=365;} }
  private void p8170CheckOddCents() { if(wsAmountText.substring(6,8).equals("50")) wsEdgeCode=padRight("HALF-CENT-ROUNDING-CASE",24); if(wsAmountText.substring(6,8).equals("99")) wsEdgeCode=padRight("ROUND-UP-BOUNDARY",24); }
  private void p8180CheckStatusTransition() { String s=rtrim(wsStatus); if(s.equals("ACTIVE")||s.equals("DORMANT")||s.equals("BLOCKED")){} else wsEdgeCode=padRight("UNKNOWN-STATE",24); }
  private void p8190CheckBalanceLimit() { if(wsBalance.compareTo(new java.math.BigDecimal("999999999.99"))>0) wsEdgeCode=padRight("BALANCE-OVERFLOW",24); if(wsBalance.compareTo(new java.math.BigDecimal("-999999999.99"))<0) wsEdgeCode=padRight("BALANCE-UNDERFLOW",24); }
  private void p8200CheckReconciliation() { java.math.BigDecimal d=wsDebits.subtract(wsCredits); if(d.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsArithError=1; else wsDelta=d; if(wsDelta.compareTo(java.math.BigDecimal.ZERO)==0){} }
  private void p8210CheckCopybookCount() { if(wsItemCount<1||wsItemCount>12) wsEdgeCode=padRight("OCCURS-COUNT-INVALID",24); }
  private void p8220CheckCallInterface() { char[] b=spaces(200).toCharArray(); setSlice(b,0,12,wsKey); setSlice(b,12,24,wsEdgeCode); String l=new String(b); wsLine=l; if(l.charAt(0)==' ') wsEdgeCode=padRight("CALL-KEY-MISSING",24); }
  private void p8230CheckWriteStatus() { if(wsOutStatus.equals("00")){} else {System.out.println("DATASET STATUS "+wsOutStatus); returnCode=12;} }
  private void p8240CheckReadStatus() { if(wsInStatus.equals("10")) endOfInput=true; else if(!wsInStatus.equals("00")) System.out.println("INPUT STATUS "+wsInStatus); }
  private void p8250CheckCurrencyScale() { java.math.BigDecimal r=wsAmount.multiply(new java.math.BigDecimal("1.0000")).setScale(2,java.math.RoundingMode.HALF_UP); if(r.abs().compareTo(new java.math.BigDecimal("999999999.99"))>0) wsArithError=1; else wsAmount=r; if(wsFxRate.signum()<0) wsEdgeCode=padRight("NEGATIVE-FX-RATE",24); }
  private void p8260CheckNestedCallResult() { wsAbendCode=0; if(wsAbendCode!=0){System.out.println("CALLED ROUTINE RETURNED ERROR"); returnCode=8;} }
  private void p8270CheckAuditBalance() { java.math.BigDecimal s=wsCycleTotal.add(wsAmount); if(s.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsArithError=1; else wsCycleTotal=s; if(wsCycleTotal.compareTo(java.math.BigDecimal.ZERO)==0){} }
  private void p8280CheckKeyUniqueness() { if(wsKey.equals(wsPrevKey)&&!isSpaces(wsKey)) wsEdgeCode=padRight("DUPLICATE-KEY",24); wsPrevKey=wsKey; }
  private void p8290CheckRunId() { if(wsNewDate.equals("00000000")){System.out.println("RUN DATE NOT INITIALIZED"); returnCode=12;} if(wsRecordCount==0) System.out.println("EMPTY INPUT"); }
  private void p9900OperationalChecks() { p8000RejectAllZeroAmounts(); p8010CheckNegativeBalance(); p8020CheckLongKey(); p8030CheckRoundedAmount(); p8040CheckShortMove(); p8050CheckFeeOverlay(); p8060CheckSignedZoned(); p8070CheckDateWindow(); p8080CheckRatePrecision(); p8090CheckRecordType(); p8100CheckReturnCode(); p8110CheckAccumulator(); p8120CheckBranchBreak(); p8130WriteBranchTotal(); p8140CheckInputShape(); p8150CheckMonthRollover(); p8160CheckLeapYear(); p8170CheckOddCents(); p8180CheckStatusTransition(); p8190CheckBalanceLimit(); p8200CheckReconciliation(); p8210CheckCopybookCount(); p8220CheckCallInterface(); p8230CheckWriteStatus(); p8240CheckReadStatus(); p8250CheckCurrencyScale(); p8260CheckNestedCallResult(); p8270CheckAuditBalance(); p8280CheckKeyUniqueness(); p8290CheckRunId(); }
  private void callHtfxrt() {
    wsCallError=0; wsCallValue=java.math.BigDecimal.ZERO; wsCallText=spaces(80);
    hrt1000ValidateInterface();
    if(wsCallError==0) hrt2000ProcessRequest();
    hrt3900ServiceControls();
  }
  private void hrt1000ValidateInterface() { if(isSpaces(wsCallKey)) wsCallError=4; if(wsCallAmt.compareTo(java.math.BigDecimal.ZERO)<0) wsCallError=8; }
  private void hrt2000ProcessRequest() { hrt3200FxQuote(); }
  private void hrt3000DateWindow() { int v=0; try{v=Integer.parseInt(hrtDateRaw.substring(0,2));}catch(Exception e){} if(v>80) hrtCentury="19"; else hrtCentury="20"; hrtDateFull=hrtCentury+hrtDateRaw; wsCallText=padRight(hrtDateFull,80); }
  private void hrt3100FeeWaiver() { if(wsCallKey.charAt(11)=='Y'){wsCallValue=java.math.BigDecimal.ZERO; wsCallText=padRight("WAIVED: REDEFINES FLAG SET",80);} else { java.math.BigDecimal r=wsCallAmt.multiply(new java.math.BigDecimal("0.035")); if(r.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsCallError=8; else wsCallValue=r; } }
  private void hrt3200FxQuote() { hrtTemp=new java.math.BigDecimal("1.0825").setScale(2,java.math.RoundingMode.DOWN); java.math.BigDecimal raw=wsCallAmt.multiply(hrtTemp); java.math.BigDecimal rd=raw.setScale(2,java.math.RoundingMode.HALF_UP); if(rd.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsCallError=8; else wsCallValue=rd; wsCallText=padRight("RATE SCALE 4 DECIMAL PLACES",80); }
  private void hrt3300DynamicCall() { hrtCallName=padRight("HTAUDT",8); }
  private void hrt3400PackedPost() { java.math.BigDecimal rd=wsCallAmt.setScale(2,java.math.RoundingMode.HALF_UP); if(rd.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsCallError=8; else wsCallValue=rd; abend((int)hrtCounter); }
  private void hrt3500AuditFormat() { wsCallText=padRight(wsCallKey,80); hrtRound=wsCallAmt; wsCallValue=hrtRound; }
  private void hrt3900ServiceControls() { hrt4100CheckKeyLowValues(); hrt4110CheckAmountCap(); hrt4120CheckAmountFloor(); hrt4130CheckResultState(); hrt4140CheckServiceName(); hrt4150CheckDateCalendar(); hrt4160CheckDateDay(); hrt4170CheckDateRollover(); hrt4180CheckWindowLow(); hrt4190CheckWindowHigh(); hrt4200CheckRoundBoundary(); hrt4210CheckPackedDecimal(); hrt4220CheckResultConsistency(); hrt4230CheckDynamicName(); hrt4240CheckKeyCharacters(); hrt4250CheckCallCounter(); hrt4260CheckAuditLength(); hrt4270CheckCurrencyScale(); hrt4280CheckWaiverState(); hrt4290CheckRateValid(); hrt4300CheckReturnText(); hrt4310CheckReferenceKey(); hrt4320CheckContentIsolation(); hrt4330CheckResultInitialized(); hrt4340CheckDecimalRounding(); hrt4350CheckCenturyPolicy(); hrt4360CheckReturnCode(); }
  private void hrt4100CheckKeyLowValues() { if(wsCallKey.equals("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000")) wsCallError=4; }
  private void hrt4110CheckAmountCap() { if(wsCallAmt.compareTo(new java.math.BigDecimal("999999999.99"))>0) wsCallError=8; }
  private void hrt4120CheckAmountFloor() { if(wsCallAmt.compareTo(new java.math.BigDecimal("-999999999.99"))<0) wsCallError=8; }
  private void hrt4130CheckResultState() { if(wsCallError==0){ char[] b=wsCallText.toCharArray(); b[0]='O'; b[1]='K'; wsCallText=new String(b);} }
  private void hrt4140CheckServiceName() { if(isSpaces(hrtCallName)) wsCallText=padRight("SERVICE NAME UNSET",80); }
  private void hrt4150CheckDateCalendar() { int m=0; try{m=Integer.parseInt(hrtDateFull.substring(4,6));}catch(Exception e){} if(m>12) wsCallError=8; }
  private void hrt4160CheckDateDay() { int d=0; try{d=Integer.parseInt(hrtDateFull.substring(6,8));}catch(Exception e){} if(d>31) wsCallError=8; }
  private void hrt4170CheckDateRollover() { if(hrtDateRaw.equals("123199")) wsCallText=padRight("YEAR END",80); }
  private void hrt4180CheckWindowLow() { if(hrtDateRaw.substring(0,2).equals("00")) hrtCentury="20"; }
  private void hrt4190CheckWindowHigh() { int v=0; try{v=Integer.parseInt(hrtDateRaw.substring(0,2));}catch(Exception e){} if(v>80) hrtCentury="19"; }
  private void hrt4200CheckRoundBoundary() { java.math.BigDecimal r=wsCallAmt.add(new java.math.BigDecimal("0.005")).setScale(2,java.math.RoundingMode.HALF_UP); if(r.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsCallError=8; else hrtRound=r; }
  private void hrt4210CheckPackedDecimal() { java.math.BigDecimal r=wsCallAmt.setScale(2,java.math.RoundingMode.HALF_UP); if(r.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsCallError=8; else hrtTemp=r; }
  private void hrt4220CheckResultConsistency() { if(wsCallError!=0) wsCallValue=java.math.BigDecimal.ZERO; }
  private void hrt4230CheckDynamicName() { if(hrtCallName.equals("HTAUDT")){} else wsCallError=8; }
  private void hrt4240CheckKeyCharacters() { if(!wsCallKey.substring(0,3).equals("SYN")) wsCallError=4; }
  private void hrt4250CheckCallCounter() { hrtCounter++; }
  private void hrt4260CheckAuditLength() { if(isSpaces(wsCallText)) wsCallText=padRight("NO AUDIT DETAIL",80); }
  private void hrt4270CheckCurrencyScale() { if(hrtTemp.compareTo(java.math.BigDecimal.ZERO)<0) wsCallError=8; }
  private void hrt4280CheckWaiverState() { if(wsCallKey.charAt(11)=='Y') wsCallValue=java.math.BigDecimal.ZERO; }
  private void hrt4290CheckRateValid() { if(hrtTemp.compareTo(new java.math.BigDecimal("999.9999"))>0) wsCallError=8; }
  private void hrt4300CheckReturnText() { if(wsCallError!=0) wsCallText=padRight("CALL FAILED",80); }
  private void hrt4310CheckReferenceKey() { if(wsCallKey.charAt(11)==' '){} }
  private void hrt4320CheckContentIsolation() { hrtTemp=wsCallAmt; }
  private void hrt4330CheckResultInitialized() { }
  private void hrt4340CheckDecimalRounding() { if(wsCallAmt.compareTo(new java.math.BigDecimal("0.005"))==0) wsCallText=padRight("HALF CENT",80); }
  private void hrt4350CheckCenturyPolicy() { int c=0; try{c=Integer.parseInt(hrtCentury);}catch(Exception e){} if(c<19||c>20) wsCallError=8; }
  private void hrt4360CheckReturnCode() { if(wsCallError>8) wsCallError=8; }
  private void hrt5410CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5420CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5430CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5440CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5450CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5460CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5470CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5480CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5490CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5500CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5510CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5520CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5530CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5540CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5550CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5560CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5570CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hrt5610CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hrt5620CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hrt5630CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hrt5640CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hrt5650CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hrt5660CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hrt5670CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void callHtaudt() {
    wsCallError=0; wsCallValue=java.math.BigDecimal.ZERO; wsCallText=spaces(80);
    hat1000ValidateInterface();
    if(wsCallError==0) hat2000ProcessRequest();
    hat3900ServiceControls();
  }
  private void hat1000ValidateInterface() { if(isSpaces(wsCallKey)) wsCallError=4; if(wsCallAmt.compareTo(java.math.BigDecimal.ZERO)<0) wsCallError=8; }
  private void hat2000ProcessRequest() { }
  private void hat3000DateWindow() { int v=0; try{v=Integer.parseInt(hatDateRaw.substring(0,2));}catch(Exception e){} if(v>80) hatCentury="19"; else hatCentury="20"; hatDateFull=hatCentury+hatDateRaw; wsCallText=padRight(hatDateFull,80); }
  private void hat3100FeeWaiver() { if(wsCallKey.charAt(11)=='Y'){wsCallValue=java.math.BigDecimal.ZERO; wsCallText=padRight("WAIVED: REDEFINES FLAG SET",80);} else { java.math.BigDecimal r=wsCallAmt.multiply(new java.math.BigDecimal("0.035")); if(r.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsCallError=8; else wsCallValue=r; } }
  private void hat3200FxQuote() { hatTemp=new java.math.BigDecimal("1.0825").setScale(2,java.math.RoundingMode.DOWN); java.math.BigDecimal raw=wsCallAmt.multiply(hatTemp); java.math.BigDecimal rd=raw.setScale(2,java.math.RoundingMode.HALF_UP); if(rd.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsCallError=8; else wsCallValue=rd; wsCallText=padRight("RATE SCALE 4 DECIMAL PLACES",80); }
  private void hat3300DynamicCall() { hatCallName=padRight("HTAUDT",8); }
  private void hat3400PackedPost() { java.math.BigDecimal rd=wsCallAmt.setScale(2,java.math.RoundingMode.HALF_UP); if(rd.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsCallError=8; else wsCallValue=rd; abend((int)hatCounter); }
  private void hat3500AuditFormat() { wsCallText=padRight(wsCallKey,80); hatRound=wsCallAmt; wsCallValue=hatRound; }
  private void hat3900ServiceControls() { hat4100CheckKeyLowValues(); hat4110CheckAmountCap(); hat4120CheckAmountFloor(); hat4130CheckResultState(); hat4140CheckServiceName(); hat4150CheckDateCalendar(); hat4160CheckDateDay(); hat4170CheckDateRollover(); hat4180CheckWindowLow(); hat4190CheckWindowHigh(); hat4200CheckRoundBoundary(); hat4210CheckPackedDecimal(); hat4220CheckResultConsistency(); hat4230CheckDynamicName(); hat4240CheckKeyCharacters(); hat4250CheckCallCounter(); hat4260CheckAuditLength(); hat4270CheckCurrencyScale(); hat4280CheckWaiverState(); hat4290CheckRateValid(); hat4300CheckReturnText(); hat4310CheckReferenceKey(); hat4320CheckContentIsolation(); hat4330CheckResultInitialized(); hat4340CheckDecimalRounding(); hat4350CheckCenturyPolicy(); hat4360CheckReturnCode(); }
  private void hat4100CheckKeyLowValues() { if(wsCallKey.equals("\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000")) wsCallError=4; }
  private void hat4110CheckAmountCap() { if(wsCallAmt.compareTo(new java.math.BigDecimal("999999999.99"))>0) wsCallError=8; }
  private void hat4120CheckAmountFloor() { if(wsCallAmt.compareTo(new java.math.BigDecimal("-999999999.99"))<0) wsCallError=8; }
  private void hat4130CheckResultState() { if(wsCallError==0){ char[] b=wsCallText.toCharArray(); b[0]='O'; b[1]='K'; wsCallText=new String(b);} }
  private void hat4140CheckServiceName() { if(isSpaces(hatCallName)) wsCallText=padRight("SERVICE NAME UNSET",80); }
  private void hat4150CheckDateCalendar() { int m=0; try{m=Integer.parseInt(hatDateFull.substring(4,6));}catch(Exception e){} if(m>12) wsCallError=8; }
  private void hat4160CheckDateDay() { int d=0; try{d=Integer.parseInt(hatDateFull.substring(6,8));}catch(Exception e){} if(d>31) wsCallError=8; }
  private void hat4170CheckDateRollover() { if(hatDateRaw.equals("123199")) wsCallText=padRight("YEAR END",80); }
  private void hat4180CheckWindowLow() { if(hatDateRaw.substring(0,2).equals("00")) hatCentury="20"; }
  private void hat4190CheckWindowHigh() { int v=0; try{v=Integer.parseInt(hatDateRaw.substring(0,2));}catch(Exception e){} if(v>80) hatCentury="19"; }
  private void hat4200CheckRoundBoundary() { java.math.BigDecimal r=wsCallAmt.add(new java.math.BigDecimal("0.005")).setScale(2,java.math.RoundingMode.HALF_UP); if(r.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsCallError=8; else hatRound=r; }
  private void hat4210CheckPackedDecimal() { java.math.BigDecimal r=wsCallAmt.setScale(2,java.math.RoundingMode.HALF_UP); if(r.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsCallError=8; else hatTemp=r; }
  private void hat4220CheckResultConsistency() { if(wsCallError!=0) wsCallValue=java.math.BigDecimal.ZERO; }
  private void hat4230CheckDynamicName() { if(hatCallName.equals("HTAUDT")){} else wsCallError=8; }
  private void hat4240CheckKeyCharacters() { if(!wsCallKey.substring(0,3).equals("SYN")) wsCallError=4; }
  private void hat4250CheckCallCounter() { hatCounter++; }
  private void hat4260CheckAuditLength() { if(isSpaces(wsCallText)) wsCallText=padRight("NO AUDIT DETAIL",80); }
  private void hat4270CheckCurrencyScale() { if(hatTemp.compareTo(java.math.BigDecimal.ZERO)<0) wsCallError=8; }
  private void hat4280CheckWaiverState() { if(wsCallKey.charAt(11)=='Y') wsCallValue=java.math.BigDecimal.ZERO; }
  private void hat4290CheckRateValid() { if(hatTemp.compareTo(new java.math.BigDecimal("999.9999"))>0) wsCallError=8; }
  private void hat4300CheckReturnText() { if(wsCallError!=0) wsCallText=padRight("CALL FAILED",80); }
  private void hat4310CheckReferenceKey() { if(wsCallKey.charAt(11)==' '){} }
  private void hat4320CheckContentIsolation() { hatTemp=wsCallAmt; }
  private void hat4330CheckResultInitialized() { }
  private void hat4340CheckDecimalRounding() { if(wsCallAmt.compareTo(new java.math.BigDecimal("0.005"))==0) wsCallText=padRight("HALF CENT",80); }
  private void hat4350CheckCenturyPolicy() { int c=0; try{c=Integer.parseInt(hatCentury);}catch(Exception e){} if(c<19||c>20) wsCallError=8; }
  private void hat4360CheckReturnCode() { if(wsCallError>8) wsCallError=8; }
  private void hat5410CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5420CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5430CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5440CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5450CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5460CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5470CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5480CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5490CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5500CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5510CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5520CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5530CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5540CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5550CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5560CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5570CheckServicePostcondition() { if(wsCallError==0){ if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; } }
  private void hat5610CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hat5620CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hat5630CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hat5640CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hat5650CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hat5660CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
  private void hat5670CheckCallerBoundary() { if(wsCallKey.substring(0,3).equals("SYN")){} else wsCallError=4; }
}

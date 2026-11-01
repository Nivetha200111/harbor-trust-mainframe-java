public class Htglr {
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
  static java.math.BigDecimal wsAmount = zero2();
  static java.math.BigDecimal wsBalance = zero2();
  static java.math.BigDecimal wsPrincipal = zero2();
  static java.math.BigDecimal wsRate = new java.math.BigDecimal("0.0000");
  static java.math.BigDecimal wsFxRate = new java.math.BigDecimal("0.0000");
  static java.math.BigDecimal wsInterest = zero2();
  static java.math.BigDecimal wsFee = zero2();
  static String wsFeeWaived = "N";
  static java.math.BigDecimal wsCheckAmt = zero2();
  static String wsReturnReason = spaces(3);
  static long wsReturnCount = 0;
  static java.math.BigDecimal wsEntryAmt = zero2();
  static java.math.BigDecimal wsCycleTotal = zero2();
  static java.math.BigDecimal wsDebits = zero2();
  static java.math.BigDecimal wsCredits = zero2();
  static java.math.BigDecimal wsDelta = zero2();
  static long wsOutOfBalance = 0;
  static long wsDaysInactive = 0;
  static long wsDayBasis = 365;
  static String wsLeapFlag = "N";
  static String wsAmountText = spaces(8);
  static String wsShortAmount = spaces(6);
  static String wsRawBase = spaces(120);
  static int wsItemCount = 0;
  static String[] wsItems = new String[12];
  static java.math.BigDecimal wsPacked = zero2();
  static java.math.BigDecimal wsZoned = zero2();
  static String wsOldDate = "991231";
  static int wsCentury = 20;
  static String wsNewDate = "00000000";
  static String wsLine = spaces(200);
  static java.math.BigDecimal wsReportTotal = zero2();
  static long wsReadTime = 0;
  static long wsWriteTime = 0;
  static long wsAbendCode = 0;
  static String inRecord = spaces(160);
  static String outRecord = spaces(200);
  static java.util.List<String> inLines = new java.util.ArrayList<>();
  static int inIndex = 0;
  static java.util.List<String> outLines = new java.util.ArrayList<>();
  static String inPath = null;
  static String outPath = null;
  static int returnCode = 0;
  static String dioCleClockDummy = System.getenv("DIOCLE_CLOCK");
  public static void main(String[] args) throws Exception {
    p0000Main();
    if (returnCode != 0) System.exit(returnCode);
  }
  static java.math.BigDecimal zero2() { return java.math.BigDecimal.ZERO.setScale(2); }
  static String spaces(int n) { StringBuilder sb = new StringBuilder(n); for (int i=0;i<n;i++) sb.append(' '); return sb.toString(); }
  static String padRight(String s, int n) {
    if (s == null) s = "";
    if (s.length() > n) return s.substring(0, n);
    if (s.length() == n) return s;
    StringBuilder sb = new StringBuilder(n);
    sb.append(s);
    for (int i=s.length();i<n;i++) sb.append(' ');
    return sb.toString();
  }
  static String setSlice(String base, int start1, int len, String val) {
    String v = padRight(val, len);
    String b = padRight(base, Math.max(base.length(), start1-1+len));
    return b.substring(0, start1-1) + v + b.substring(start1-1+len);
  }
  static String slice(String s, int start1, int len) {
    String p = padRight(s, start1-1+len);
    return p.substring(start1-1, start1-1+len);
  }
  static boolean isEbcdic() { String c = System.getenv("DIOCLE_COLLATION"); return "EBCDIC".equals(c); }
  static int compareAlpha(String a, String b) {
    if (isEbcdic()) {
      try {
        java.nio.charset.Charset cs = java.nio.charset.Charset.forName("Cp037");
        byte[] ba = a.getBytes(cs);
        byte[] bb = b.getBytes(cs);
        int n = Math.min(ba.length, bb.length);
        for (int i=0;i<n;i++) { int x = ba[i] & 0xFF; int y = bb[i] & 0xFF; if (x!=y) return x-y; }
        return ba.length - bb.length;
      } catch (Exception e) { }
    }
    int n = Math.max(a.length(), b.length());
    for (int i=0;i<n;i++) { char ca = i<a.length()?a.charAt(i):' '; char cb = i<b.length()?b.charAt(i):' '; if (ca!=cb) return ca-cb; }
    return 0;
  }
  static boolean isNumericX(String s) {
    if (s.length()==0) return false;
    for (int i=0;i<s.length();i++) { char c=s.charAt(i); if (c<'0'||c>'9') return false; }
    return true;
  }
  static boolean isAlphabeticX(String s) {
    for (int i=0;i<s.length();i++) { char c=s.charAt(i); if (c==' ') continue; if ((c>='A'&&c<='Z')||(c>='a'&&c<='z')) continue; return false; }
    return true;
  }
  static String formatCount9(long v) {
    long u = v & 0xFFFFFFFFL;
    String s = Long.toString(u);
    while (s.length()<10) s="0"+s;
    if (s.length()>10) s=s.substring(s.length()-10);
    return s;
  }
  static String formatCount7(long v) {
    long u = v & 0xFFFFFFFFL;
    String s = Long.toString(u);
    while (s.length()<10) s="0"+s;
    if (s.length()>10) s=s.substring(s.length()-10);
    return s;
  }
  static String formatS11V99(java.math.BigDecimal v) {
    boolean neg = v.signum()<0;
    java.math.BigDecimal a = v.abs().setScale(2, java.math.RoundingMode.HALF_UP);
    String plain = a.toPlainString();
    int dot = plain.indexOf('.');
    String ip = dot<0?plain:plain.substring(0,dot);
    String fp = dot<0?"00":plain.substring(dot+1);
    while (fp.length()<2) fp=fp+"0";
    if (fp.length()>2) fp=fp.substring(0,2);
    while (ip.length()<11) ip="0"+ip;
    if (ip.length()>11) ip=ip.substring(ip.length()-11);
    return (neg?"-":"+")+ip+"."+fp;
  }
  static String rtrim(String s) {
    int n=s.length();
    while (n>0 && s.charAt(n-1)==' ') n--;
    return s.substring(0,n);
  }
  static void p0000Main() throws Exception {
    p1000Initialize();
    while (!endOfInput) {
      p2000ReadInput();
      if (!endOfInput) {
        p3000ValidateRecord();
        if (!inputError) {
          p4000ProcessRecord();
          p5000WriteResult();
        } else {
          p6000RejectRecord();
        }
      }
    }
    p7000Finalize();
  }
  static void p1000Initialize() throws Exception {
    inPath = System.getenv("DD_INPUT-FILE");
    outPath = System.getenv("DD_OUTPUT-FILE");
    if (inPath == null || inPath.isEmpty()) {
      wsInStatus = "35";
      System.out.println("OPEN INPUT FAILED "+wsInStatus);
      returnCode = 12;
      endOfInput = true;
    } else {
      try {
        byte[] all = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(inPath));
        String content = new String(all, java.nio.charset.StandardCharsets.ISO_8859_1);
        inLines.clear();
        if (!content.isEmpty()) {
          String[] parts = content.split("\\r?\\n", -1);
          boolean endsNL = content.endsWith("\n") || content.endsWith("\r");
          int lim = parts.length;
          if (endsNL && lim>0 && parts[lim-1].isEmpty()) lim--;
          for (int i=0;i<lim;i++) inLines.add(parts[i]);
        }
        inIndex = 0;
        wsInStatus = "00";
      } catch (java.nio.file.NoSuchFileException e) {
        wsInStatus = "35";
        System.out.println("OPEN INPUT FAILED "+wsInStatus);
        returnCode = 12;
        endOfInput = true;
      } catch (java.io.IOException e) {
        wsInStatus = "39";
        System.out.println("OPEN INPUT FAILED "+wsInStatus);
        returnCode = 12;
        endOfInput = true;
      }
    }
    if (outPath == null || outPath.isEmpty()) {
      wsOutStatus = "35";
      System.out.println("OPEN OUTPUT FAILED "+wsOutStatus);
      returnCode = 12;
      endOfInput = true;
    } else {
      try {
        java.nio.file.Path p = java.nio.file.Paths.get(outPath);
        java.nio.file.Path par = p.getParent();
        if (par != null) java.nio.file.Files.createDirectories(par);
        outLines.clear();
        wsOutStatus = "00";
      } catch (java.io.IOException e) {
        wsOutStatus = "39";
        System.out.println("OPEN OUTPUT FAILED "+wsOutStatus);
        returnCode = 12;
        endOfInput = true;
      }
    }
    wsRecordCount = 0; wsAcceptCount = 0; wsRejectCount = 0;
    wsEdgeCode = spaces(24);
    p1100CheckRunControl();
    p1200InitializeTotals();
  }
  static void p1100CheckRunControl() {
    String first2 = wsOldDate.substring(0,2);
    int v = Integer.parseInt(first2);
    if (v > 80) wsCentury = 19; else wsCentury = 20;
    String cs = wsCentury<10?"0"+wsCentury:Integer.toString(wsCentury);
    wsNewDate = cs + wsOldDate;
  }
  static void p1200InitializeTotals() {
    wsReportTotal = zero2(); wsCycleTotal = zero2(); wsDebits = zero2(); wsCredits = zero2(); wsDelta = zero2();
    wsPrevKey = spaces(12); wsPrevBranch = spaces(3);
  }
  static void p2000ReadInput() {
    if (inIndex >= inLines.size()) {
      wsInStatus = "10";
      endOfInput = true;
      return;
    }
    String line = inLines.get(inIndex++);
    inRecord = padRight(line, 160);
    wsInStatus = "00";
    wsRecordCount = (wsRecordCount + 1) & 0xFFFFFFFFL;
    wsRawBase = padRight(inRecord.substring(0, Math.min(120, inRecord.length())), 120);
    wsKey = slice(inRecord, 1, 12);
    wsBranch = slice(inRecord, 13, 3);
    wsAmountText = slice(inRecord, 16, 8);
    wsShortAmount = slice(inRecord, 24, 6);
    wsStatus = slice(inRecord, 24, 8);
    if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
      System.out.println("READ FAILURE "+wsInStatus);
      inputError = true;
      endOfInput = true;
    }
  }
  static void p3000ValidateRecord() {
    inputError = false;
    if (wsKey.equals(spaces(12))) p3100BadKey();
    if (!isNumericX(wsAmountText)) p3200BadAmount();
    if (wsStatus.equals(spaces(8))) p3300BadStatus();
    if (!isAlphabeticX(wsBranch)) p3400BadBranch();
    if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) wsEdgeCode = padRight("SORT-ORDER", 24);
    wsPrevKey = wsKey;
    p3500CheckMultiRecordType();
    p3600CheckDateBoundary();
  }
  static void p3100BadKey() { inputError = true; wsEdgeCode = padRight("MISSING-KEY", 24); }
  static void p3200BadAmount() { inputError = true; wsEdgeCode = padRight("INVALID-AMOUNT", 24); }
  static void p3300BadStatus() { inputError = true; wsEdgeCode = padRight("INVALID-STATUS", 24); }
  static void p3400BadBranch() { inputError = true; wsEdgeCode = padRight("INVALID-BRANCH", 24); }
  static void p3500CheckMultiRecordType() {
    String c0 = slice(inRecord, 1, 1);
    if (!c0.equals("A") && !c0.equals("T")) wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE", 24);
    String c1 = slice(inRecord, 2, 1);
    if (c1.equals("D")) wsItemCount = 2; else wsItemCount = 1;
  }
  static void p3600CheckDateBoundary() {
    String d = slice(inRecord, 40, 8);
    if (d.equals("20261231")) wsEdgeCode = padRight("YEAR-END-ROLLOVER", 24);
    if (d.equals("20260228")) wsEdgeCode = padRight("NON-LEAP-FEB-END", 24);
  }
  static void p4000ProcessRecord() {
    wsArithError = 0;
    try { wsAmount = new java.math.BigDecimal(wsAmountText).setScale(2); } catch (Exception e) { wsAmount = zero2(); }
    wsBalance = wsAmount; wsPrincipal = wsAmount;
    java.math.BigDecimal nt = wsReportTotal.add(wsAmount);
    if (nt.abs().compareTo(new java.math.BigDecimal("99999999999.99")) > 0) wsArithError = 1; else wsReportTotal = nt.setScale(2);
    wsRate = new java.math.BigDecimal("0.0525");
    wsFxRate = new java.math.BigDecimal("1.0825");
    wsCheckAmt = wsAmount; wsEntryAmt = wsAmount;
    wsFee = zero2();
    wsFeeWaived = slice(inRecord, 120, 1);
    wsInterest = zero2();
    java.math.BigDecimal d = wsDebits.subtract(wsCredits);
    d = d.setScale(2);
    if (d.abs().compareTo(new java.math.BigDecimal("99999999999.99")) > 0) wsArithError = 1; else wsDelta = d;
    if (wsDelta.compareTo(java.math.BigDecimal.ZERO) != 0) wsOutOfBalance = (wsOutOfBalance + 1) & 0xFFFFFFFFL;
    wsLine = padRight(wsLine, 200);
    wsLine = setSlice(wsLine, 1, 12, wsKey);
    wsLine = setSlice(wsLine, 13, 3, wsBranch);
    wsLine = setSlice(wsLine, 16, 24, wsEdgeCode);
    wsLine = setSlice(wsLine, 40, 12, wsAmountText);
    wsLine = setSlice(wsLine, 52, 8, wsStatus);
    wsAcceptCount = (wsAcceptCount + 1) & 0xFFFFFFFFL;
  }
  static void p5000WriteResult() {
    outRecord = padRight(wsLine, 200);
    wsOutStatus = "00";
    outLines.add(outRecord);
    if (!wsOutStatus.equals("00")) {
      System.out.println("WRITE FAILURE "+wsOutStatus);
      returnCode = 12;
      endOfInput = true;
    } else {
      wsWriteTime = (wsWriteTime + 1) & 0xFFFFFFFFL;
    }
  }
  static void p6000RejectRecord() {
    wsRejectCount = (wsRejectCount + 1) & 0xFFFFFFFFL;
    System.out.println("REJECT "+formatCount9(wsRecordCount)+" "+wsEdgeCode);
    wsLine = spaces(200);
    wsLine = setSlice(wsLine, 1, 6, "REJECT");
    wsLine = setSlice(wsLine, 8, 12, wsKey);
    wsLine = setSlice(wsLine, 24, 24, wsEdgeCode);
    outRecord = padRight(wsLine, 200);
    outLines.add(outRecord);
  }
  static void p7000Finalize() throws Exception {
    p7100CheckControlTotals();
    p7200ReportRunCounts();
    try {
      if (outPath != null && !outPath.isEmpty()) {
        StringBuilder sb = new StringBuilder();
        for (String r : outLines) { sb.append(rtrim(padRight(r,200))); sb.append('\n'); }
        byte[] bytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        java.nio.file.Files.write(java.nio.file.Paths.get(outPath), bytes);
      }
      wsInStatus = "00"; wsOutStatus = "00";
    } catch (java.io.IOException e) { wsOutStatus = "39"; }
    if (!wsInStatus.equals("00")) System.out.println("CLOSE IN "+wsInStatus);
    if (!wsOutStatus.equals("00")) System.out.println("CLOSE OUT "+wsOutStatus);
    if (wsRejectCount > 0) returnCode = 4;
  }
  static void p7100CheckControlTotals() {
    if (wsAcceptCount + wsRejectCount != wsRecordCount) { System.out.println("CONTROL COUNT MISMATCH"); returnCode = 12; }
    if (wsArithError != 0) { System.out.println("ARITHMETIC SIZE ERROR"); returnCode = 8; }
  }
  static void p7200ReportRunCounts() {
    System.out.println("PROGRAM HTGLR COMPLETE");
    System.out.println("READ="+formatCount9(wsRecordCount));
    System.out.println("ACCEPT="+formatCount9(wsAcceptCount));
    System.out.println("REJECT="+formatCount9(wsRejectCount));
    System.out.println("TOTAL="+formatS11V99(wsReportTotal));
    System.out.println("EDGE="+wsEdgeCode);
  }
  static void p8000RejectAllZeroAmounts() { if (wsAmount.compareTo(java.math.BigDecimal.ZERO)==0 && wsStatus.equals(padRight("ACTIVE",8))) wsEdgeCode = padRight("ZERO-ACTIVE-AMOUNT",24); }
  static void p8010CheckNegativeBalance() { if (wsBalance.compareTo(java.math.BigDecimal.ZERO)<0) wsEdgeCode = padRight("NEGATIVE-BALANCE",24); }
  static void p8020CheckLongKey() { if (slice(wsKey,12,1).equals(" ")) { } else { System.out.println("FULL-WIDTH-KEY "+wsKey); } }
  static void p8030CheckRoundedAmount() {
    java.math.BigDecimal r = wsAmount.setScale(2, java.math.RoundingMode.HALF_UP);
    if (r.abs().compareTo(new java.math.BigDecimal("999999999.99"))>0) wsArithError=1; else wsPacked=r;
    if (wsPacked.compareTo(java.math.BigDecimal.ZERO)==0 && wsAmount.compareTo(java.math.BigDecimal.ZERO)!=0) wsEdgeCode=padRight("TRUNCATION-RISK",24);
  }
  static void p8040CheckShortMove() {
    wsShortAmount = padRight(wsAmountText,6).substring(0,6);
    if (!slice(wsAmountText,7,2).equals("00")) wsEdgeCode=padRight("SHORT-PIC-TRUNCATION",24);
  }
  static void p8050CheckFeeOverlay() {
    String ov = wsRawBase.length()>=120?wsRawBase.substring(119,120):" ";
    if (ov.equals("Y")) wsFeeWaived="Y"; else wsFeeWaived="N";
  }
  static void p8060CheckSignedZoned() {
    wsZoned = wsBalance;
    if (wsZoned.compareTo(java.math.BigDecimal.ZERO)<0 && wsBalance.compareTo(java.math.BigDecimal.ZERO)>=0) wsEdgeCode=padRight("SIGN-CONVERSION",24);
  }
  static void p8070CheckDateWindow() {
    if (wsOldDate.substring(0,2).equals("00")) wsEdgeCode=padRight("Y2K-WINDOW-REVIEW",24);
    if (wsOldDate.equals("991231")) wsEdgeCode=padRight("CENTURY-BOUNDARY",24);
  }
  static void p8080CheckRatePrecision() { if (wsFxRate.compareTo(new java.math.BigDecimal("1.0825"))!=0) wsEdgeCode=padRight("RATE-IS-FOUR-DECIMAL",24); }
  static void p8090CheckRecordType() {
    String c = slice(inRecord,1,1);
    if (c.equals("A")) {} else if (c.equals("T")) {} else wsEdgeCode=padRight("BAD-RECORD-TYPE",24);
  }
  static void p8100CheckReturnCode() {
    if (!wsReturnReason.equals(spaces(3))) wsReturnCount=(wsReturnCount+1)& 0xFFFFFFFFL;
    if (wsReturnCount > wsRecordCount) returnCode=12;
  }
  static void p8110CheckAccumulator() {
    java.math.BigDecimal nt = wsReportTotal.add(wsAmount);
    if (nt.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsArithError=1; else wsReportTotal=nt.setScale(2);
    if (wsReportTotal.compareTo(java.math.BigDecimal.ZERO)<0) wsEdgeCode=padRight("NEGATIVE-CONTROL-TOTAL",24);
  }
  static void p8120CheckBranchBreak() {
    if (!wsBranch.equals(wsPrevBranch)) { p8130WriteBranchTotal(); wsPrevBranch=wsBranch; }
  }
  static void p8130WriteBranchTotal() {
    if (!wsPrevBranch.equals(spaces(3))) { System.out.println("BRANCH BREAK "+wsPrevBranch); System.out.println("BRANCH TOTAL "+formatS11V99(wsReportTotal)); }
    wsReportTotal=zero2();
  }
  static void p8140CheckInputShape() {
    if (inRecord.equals(spaces(160))) { inputError=true; wsEdgeCode=padRight("EMPTY-RECORD",24); }
    if (!slice(inRecord,160,1).equals(" ")) System.out.println("TRAILING-CONTENT PRESENT");
  }
  static void p8150CheckMonthRollover() {
    if (slice(inRecord,40,8).equals("20261231")) wsNewDate="20270101";
    if (slice(inRecord,40,8).equals("20260131")) wsNewDate="20260201";
  }
  static void p8160CheckLeapYear() {
    if (slice(inRecord,40,4).equals("2024")) { wsLeapFlag="Y"; wsDayBasis=366; } else { wsLeapFlag="N"; wsDayBasis=365; }
  }
  static void p8170CheckOddCents() {
    if (slice(wsAmountText,7,2).equals("50")) wsEdgeCode=padRight("HALF-CENT-ROUNDING-CASE",24);
    if (slice(wsAmountText,7,2).equals("99")) wsEdgeCode=padRight("ROUND-UP-BOUNDARY",24);
  }
  static void p8180CheckStatusTransition() {
    String s = rtrim(wsStatus);
    if (s.equals("ACTIVE")) {} else if (s.equals("DORMANT")) {} else if (s.equals("BLOCKED")) {} else {
      if (wsStatus.equals(padRight("ACTIVE",8))) {} else if (wsStatus.equals(padRight("DORMANT",8))) {} else if (wsStatus.equals(padRight("BLOCKED",8))) {} else wsEdgeCode=padRight("UNKNOWN-STATE",24);
    }
  }
  static void p8190CheckBalanceLimit() {
    if (wsBalance.compareTo(new java.math.BigDecimal("999999999.99"))>0) wsEdgeCode=padRight("BALANCE-OVERFLOW",24);
    if (wsBalance.compareTo(new java.math.BigDecimal("-999999999.99"))<0) wsEdgeCode=padRight("BALANCE-UNDERFLOW",24);
  }
  static void p8200CheckReconciliation() {
    java.math.BigDecimal d = wsDebits.subtract(wsCredits).setScale(2);
    if (d.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsArithError=1; else wsDelta=d;
    if (wsDelta.compareTo(java.math.BigDecimal.ZERO)==0) {} 
  }
  static void p8210CheckCopybookCount() {
    if (wsItemCount<1 || wsItemCount>12) wsEdgeCode=padRight("OCCURS-COUNT-INVALID",24);
    for (wsItemCount=1; wsItemCount<=1; ) { break; }
  }
  static void p8220CheckCallInterface() {
    wsLine=padRight(wsLine,200);
    wsLine=setSlice(wsLine,1,12,wsKey);
    wsLine=setSlice(wsLine,13,24,wsEdgeCode);
    if (slice(wsLine,1,1).equals(" ")) wsEdgeCode=padRight("CALL-KEY-MISSING",24);
  }
  static void p8230CheckWriteStatus() {
    if (wsOutStatus.equals("00")) {} else { System.out.println("DATASET STATUS "+wsOutStatus); returnCode=12; }
  }
  static void p8240CheckReadStatus() {
    if (wsInStatus.equals("10")) endOfInput=true;
    else { if (!wsInStatus.equals("00")) System.out.println("INPUT STATUS "+wsInStatus); }
  }
  static void p8250CheckCurrencyScale() {
    java.math.BigDecimal r = wsAmount.multiply(new java.math.BigDecimal("1.0000")).setScale(2, java.math.RoundingMode.HALF_UP);
    if (r.abs().compareTo(new java.math.BigDecimal("999999999.99"))>0) wsArithError=1; else wsAmount=r;
    if (wsFxRate.signum()<0) wsEdgeCode=padRight("NEGATIVE-FX-RATE",24);
  }
  static void p8260CheckNestedCallResult() {
    wsAbendCode=0;
    if (wsAbendCode!=0) { System.out.println("CALLED ROUTINE RETURNED ERROR"); returnCode=8; }
  }
  static void p8270CheckAuditBalance() {
    java.math.BigDecimal nt = wsCycleTotal.add(wsAmount);
    if (nt.abs().compareTo(new java.math.BigDecimal("99999999999.99"))>0) wsArithError=1; else wsCycleTotal=nt.setScale(2);
    if (wsCycleTotal.compareTo(java.math.BigDecimal.ZERO)==0) {}
  }
  static void p8280CheckKeyUniqueness() {
    if (wsKey.equals(wsPrevKey) && !wsKey.equals(spaces(12))) wsEdgeCode=padRight("DUPLICATE-KEY",24);
    wsPrevKey=wsKey;
  }
  static void p8290CheckRunId() {
    if (wsNewDate.equals("00000000")) { System.out.println("RUN DATE NOT INITIALIZED"); returnCode=12; }
    if (wsRecordCount==0) System.out.println("EMPTY INPUT");
  }
  static void p9900OperationalChecks() {
    p8000RejectAllZeroAmounts();
    p8010CheckNegativeBalance();
    p8020CheckLongKey();
    p8030CheckRoundedAmount();
    p8040CheckShortMove();
    p8050CheckFeeOverlay();
    p8060CheckSignedZoned();
    p8070CheckDateWindow();
    p8080CheckRatePrecision();
    p8090CheckRecordType();
    p8100CheckReturnCode();
    p8110CheckAccumulator();
    p8120CheckBranchBreak();
    p8130WriteBranchTotal();
    p8140CheckInputShape();
    p8150CheckMonthRollover();
    p8160CheckLeapYear();
    p8170CheckOddCents();
    p8180CheckStatusTransition();
    p8190CheckBalanceLimit();
    p8200CheckReconciliation();
    p8210CheckCopybookCount();
    p8220CheckCallInterface();
    p8230CheckWriteStatus();
    p8240CheckReadStatus();
    p8250CheckCurrencyScale();
    p8260CheckNestedCallResult();
    p8270CheckAuditBalance();
    p8280CheckKeyUniqueness();
    p8290CheckRunId();
  }
}

public class Htloanam {
  private String wsInStatus = "  ";
  private String wsOutStatus = "  ";
  private boolean endOfInput = false;
  private boolean wsErrorFlag = false;
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
  private String wsFeeOverride = " ";
  private int wsItemCount = 0;
  private java.math.BigDecimal wsPacked = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsZoned = java.math.BigDecimal.ZERO;
  private char[] wsLine = spaces(200).toCharArray();
  private java.math.BigDecimal wsLoanPrin = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsApr = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsMonthRate = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsPayment = java.math.BigDecimal.ZERO;
  private int wsTermMonths = 0;
  private int wsMonthCount = 0;
  private int wsDayBasis = 365;
  private String wsLeapFlag = "N";
  private java.math.BigDecimal wsFxRate = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal wsFee = java.math.BigDecimal.ZERO;
  private String wsFeeWaived = "N";
  private String wsOldDate = "991231";
  private String wsCentury = "20";
  private String wsNewDate = "00000000";
  private int wsYear4 = 0;
  private int wsRemainder = 0;
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
  private boolean ebcdicMode = false;
  // COPY HTLOAN
  private String loanId = spaces(12);
  private java.math.BigDecimal loanPrincipal = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal loanApr = java.math.BigDecimal.ZERO;
  private int loanTermMonths = 0;
  private String loanNextDue = spaces(8);
  // COPY HTMONEY
  private java.math.BigDecimal moneyAmount = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal moneyRate = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal moneyRounded = java.math.BigDecimal.ZERO;
  // COPY HTDATE
  private String dateShort = spaces(6);
  private String dateCentury = spaces(2);
  private String dateLong = spaces(8);
  private String dateLeapFlag = " ";
  // COPY HTERROR / HTSTATUS via HTNEST
  private String errorCode = spaces(8);
  private String errorText = spaces(80);
  private long errorCount = 0;
  private String recordStatus = " ";
  // service working-storage (HTDATE/HTPOST/HTAUDT)
  private String svcCallName = spaces(8);
  private int svcDateRaw = 0;
  private int svcDateFull = 0;
  private int svcCentury = 20;
  private java.math.BigDecimal svcTemp = java.math.BigDecimal.ZERO;
  private java.math.BigDecimal svcRound = java.math.BigDecimal.ZERO;
  private long svcCounterDate = 0;
  private long svcCounterPost = 0;
  private long svcCounterAudt = 0;
  public static void main(String[] args) {
    Htloanam p = new Htloanam();
    p.runMain();
  }
  private static String spaces(int n) {
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) sb.append(' ');
    return sb.toString();
  }
  private static String padRight(String s, int n) {
    if (s.length() > n) return s.substring(0, n);
    if (s.length() == n) return s;
    StringBuilder sb = new StringBuilder(s);
    while (sb.length() < n) sb.append(' ');
    return sb.toString();
  }
  private static String rtrim(String s) {
    int i = s.length();
    while (i > 0 && s.charAt(i - 1) == ' ') i--;
    return s.substring(0, i);
  }
  private void runMain() {
    String coll = System.getenv("DIOCLE_COLLATION");
    ebcdicMode = "EBCDIC".equals(coll);
    doMain();
    if (returnCode != 0) System.exit(returnCode);
  }
  private void doMain() {
    initialize1000();
    while (!endOfInput) {
      readInput2000();
      if (!endOfInput) {
        validateRecord3000();
        if (!wsErrorFlag) {
          processRecord4000();
          writeResult5000();
        } else {
          rejectRecord6000();
        }
      }
    }
    finalize7000();
  }
  private void initialize1000() {
    String inPath = System.getenv("DD_INPUT-FILE");
    String outPath = System.getenv("DD_OUTPUT-FILE");
    try {
      if (inPath == null) throw new java.io.IOException("no dd");
      inReader = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get(inPath), java.nio.charset.StandardCharsets.ISO_8859_1);
      inOpen = true;
      wsInStatus = "00";
    } catch (Exception e) {
      wsInStatus = "35";
      System.out.println("OPEN INPUT FAILED " + wsInStatus);
      returnCode = 12;
      endOfInput = true;
    }
    try {
      if (outPath == null) throw new java.io.IOException("no dd");
      outWriter = java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(outPath), java.nio.charset.StandardCharsets.ISO_8859_1, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING, java.nio.file.StandardOpenOption.WRITE);
      outOpen = true;
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "35";
      System.out.println("OPEN OUTPUT FAILED " + wsOutStatus);
      returnCode = 12;
      endOfInput = true;
    }
    wsRecordCount = 0;
    wsAcceptCount = 0;
    wsRejectCount = 0;
    wsEdgeCode = spaces(24);
    checkRunControl1100();
    initializeTotals1200();
  }
  private void checkRunControl1100() {
    int yy = Integer.parseInt(wsOldDate.substring(0, 2));
    if (yy > 80) wsCentury = "19";
    else wsCentury = "20";
    wsNewDate = wsCentury + wsOldDate;
  }
  private void initializeTotals1200() {
    wsReportTotal = java.math.BigDecimal.ZERO;
    wsCycleTotal = java.math.BigDecimal.ZERO;
    wsDebits = java.math.BigDecimal.ZERO;
    wsCredits = java.math.BigDecimal.ZERO;
    wsDelta = java.math.BigDecimal.ZERO;
    wsPrevKey = spaces(12);
    wsPrevBranch = spaces(3);
  }
  private void readInput2000() {
    if (endOfInput) return;
    if (!inOpen) { endOfInput = true; return; }
    try {
      String line = inReader.readLine();
      if (line == null) {
        wsInStatus = "10";
        endOfInput = true;
      } else {
        wsInStatus = "00";
        if (line.length() > 160) line = line.substring(0, 160);
        else if (line.length() < 160) line = line + spaces(160 - line.length());
        inRecord = line;
        wsRecordCount++;
        wsRawBase = inRecord.substring(0, 120);
        wsFeeOverride = wsRawBase.substring(119, 120);
        wsKey = inRecord.substring(0, 12);
        wsBranch = inRecord.substring(12, 15);
        wsAmountText = inRecord.substring(15, 23);
        wsStatus = inRecord.substring(31, 39);
      }
    } catch (Exception e) {
      wsInStatus = "30";
    }
    if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
      System.out.println("READ FAILURE " + wsInStatus);
      wsErrorFlag = true;
      endOfInput = true;
    }
  }
  private int compareKeys(String a, String b) {
    if (!ebcdicMode) return a.compareTo(b);
    try {
      java.nio.charset.Charset cs = java.nio.charset.Charset.forName("Cp037");
      byte[] ba = a.getBytes(cs);
      byte[] bb = b.getBytes(cs);
      int n = Math.min(ba.length, bb.length);
      for (int i = 0; i < n; i++) {
        int x = ba[i] & 0xFF;
        int y = bb[i] & 0xFF;
        if (x != y) return x - y;
      }
      return ba.length - bb.length;
    } catch (Exception e) { return a.compareTo(b); }
  }
  private boolean isNumericText(String s) {
    if (s.length() == 0) return false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < '0' || c > '9') return false;
    }
    return true;
  }
  private boolean isAlphabeticText(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ' ') continue;
      return false;
    }
    return true;
  }
  private void validateRecord3000() {
    wsErrorFlag = false;
    if (wsKey.equals(spaces(12))) badKey3100();
    if (!isNumericText(wsAmountText)) badAmount3200();
    if (wsStatus.equals(spaces(8))) badStatus3300();
    if (!isAlphabeticText(wsBranch)) badBranch3400();
    if (wsRecordCount > 1 && compareKeys(wsKey, wsPrevKey) < 0) {
      wsEdgeCode = padRight("SORT-ORDER", 24);
    }
    wsPrevKey = wsKey;
    checkMultiRecordType3500();
    checkDateBoundary3600();
  }
  private void badKey3100() { wsErrorFlag = true; wsEdgeCode = padRight("MISSING-KEY", 24); }
  private void badAmount3200() { wsErrorFlag = true; wsEdgeCode = padRight("INVALID-AMOUNT", 24); }
  private void badStatus3300() { wsErrorFlag = true; wsEdgeCode = padRight("INVALID-STATUS", 24); }
  private void badBranch3400() { wsErrorFlag = true; wsEdgeCode = padRight("INVALID-BRANCH", 24); }
  private void checkMultiRecordType3500() {
    String c1 = inRecord.substring(0, 1);
    if (!c1.equals("A") && !c1.equals("T")) wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE", 24);
    String c2 = inRecord.substring(1, 2);
    if (c2.equals("D")) wsItemCount = 2;
    else wsItemCount = 1;
  }
  private void checkDateBoundary3600() {
    if (wsNewDate.equals("20261231")) wsEdgeCode = padRight("YEAR-END-ROLLOVER", 24);
    if (wsNewDate.equals("20260228")) wsEdgeCode = padRight("NON-LEAP-FEB-END", 24);
  }
  private String fmtCount9(long v) { long m = v & 0xFFFFFFFFL; return String.format("%010d", m); }
  private String fmtTotal(java.math.BigDecimal v) {
    java.math.BigDecimal sc = v.setScale(2, java.math.RoundingMode.HALF_UP);
    boolean neg = sc.signum() < 0;
    java.math.BigDecimal abs = sc.abs();
    java.math.BigInteger cents = abs.movePointRight(2).toBigInteger().remainder(java.math.BigInteger.TEN.pow(13));
    String d = cents.toString();
    while (d.length() < 13) d = "0" + d;
    String ip = d.substring(0, 11);
    String fp = d.substring(11, 13);
    if (neg) return "-" + ip + "." + fp;
    return "+" + ip + "." + fp;
  }
  private void setSlice(char[] buf, int start1, int len, String val) {
    for (int i = 0; i < len; i++) {
      char c = ' ';
      if (i < val.length()) c = val.charAt(i);
      buf[start1 - 1 + i] = c;
    }
  }
  private void processRecord4000() {
    wsArithError = 0;
    java.math.BigDecimal amt;
    try { amt = new java.math.BigDecimal(wsAmountText); } catch (Exception e) { amt = java.math.BigDecimal.ZERO; }
    wsAmount = amt;
    wsLoanPrin = wsAmount;
    wsApr = new java.math.BigDecimal("0.0750");
    wsTermMonths = 360;
    int yy = Integer.parseInt(wsOldDate.substring(0, 2));
    if (yy > 80) wsCentury = "19";
    else wsCentury = "20";
    wsNewDate = wsCentury + wsOldDate;
    wsYear4 = Integer.parseInt(wsNewDate.substring(0, 4));
    int q = wsYear4 / 4;
    int r = wsYear4 % 4;
    wsYear4 = q;
    wsRemainder = r;
    if (wsRemainder == 0) { wsLeapFlag = "Y"; wsDayBasis = 366; }
    else { wsLeapFlag = "N"; wsDayBasis = 365; }
    try {
      java.math.BigDecimal exact = wsApr.divide(new java.math.BigDecimal("12"), 20, java.math.RoundingMode.HALF_UP);
      wsMonthRate = exact.setScale(4, java.math.RoundingMode.HALF_UP);
    } catch (Exception e) { wsArithError = 1; }
    wsPayment = java.math.BigDecimal.ZERO;
    wsMonthCount = 0;
    while (!(wsMonthCount >= 12)) {
      try {
        java.math.BigDecimal p = wsLoanPrin.multiply(wsMonthRate);
        java.math.BigDecimal pr = p.setScale(2, java.math.RoundingMode.HALF_UP);
        if (pr.abs().compareTo(new java.math.BigDecimal("999999999.99")) > 0) wsArithError = 1;
        else wsPayment = pr;
      } catch (Exception e) { wsArithError = 1; }
      try {
        java.math.BigDecimal nv = wsLoanPrin.add(wsPayment).subtract(wsPayment);
        java.math.BigDecimal nr = nv.setScale(2, java.math.RoundingMode.HALF_UP);
        if (nr.abs().compareTo(new java.math.BigDecimal("99999999999.99")) > 0) wsArithError = 1;
        else wsLoanPrin = nr;
      } catch (Exception e) { wsArithError = 1; }
      wsMonthCount++;
    }
    wsCallKey = wsKey;
    wsCallAmt = wsLoanPrin;
    htdateCall(wsCallKey, wsCallAmt);
    wsCallKey = wsKey;
    wsCallAmt = wsPayment;
    htpostCall(wsCallKey, wsCallAmt);
    wsCallKey = wsKey;
    wsCallAmt = wsPayment;
    htaudtCall(wsCallKey, wsCallAmt);
    try {
      java.math.BigDecimal nt = wsReportTotal.add(wsPayment);
      if (nt.abs().compareTo(new java.math.BigDecimal("99999999999.99")) > 0) wsArithError = 1;
      else wsReportTotal = nt;
    } catch (Exception e) { wsArithError = 1; }
    setSlice(wsLine, 1, 12, wsKey);
    setSlice(wsLine, 13, 3, wsBranch);
    setSlice(wsLine, 16, 24, wsEdgeCode);
    setSlice(wsLine, 40, 12, wsAmountText);
    setSlice(wsLine, 52, 8, wsStatus);
    wsAcceptCount++;
  }
  private void htdateCall(String key, java.math.BigDecimal amt) {
    int lkError = 0;
    java.math.BigDecimal lkValue = java.math.BigDecimal.ZERO;
    String lkText = spaces(80);
    if (key.equals(spaces(12))) lkError = 4;
    if (amt.signum() < 0) lkError = 8;
    if (lkError == 0) {
      String raw = "991231";
      int yy = Integer.parseInt(raw.substring(0, 2));
      String cent = yy > 80 ? "19" : "20";
      String full = cent + raw;
      try { lkValue = new java.math.BigDecimal(full); } catch (Exception e) { lkValue = java.math.BigDecimal.ZERO; }
      char[] t = lkText.toCharArray();
      for (int i = 0; i < 8 && i < full.length(); i++) t[i] = full.charAt(i);
      lkText = new String(t);
    }
    htdateServiceControls(key, amt, lkError);
    htdateServiceValidations(key, amt, lkError);
    svcCounterDate++;
  }
  private void htdateServiceControls(String key, java.math.BigDecimal amt, int err) {
    int e = err;
    java.math.BigDecimal tmp = svcTemp;
    java.math.BigDecimal rnd;
    try { rnd = amt.setScale(2, java.math.RoundingMode.HALF_UP); } catch (Exception ex) { rnd = java.math.BigDecimal.ZERO; e = 8; }
    try { tmp = amt.setScale(2, java.math.RoundingMode.HALF_UP); } catch (Exception ex) { e = 8; }
    svcTemp = tmp;
    svcRound = rnd;
    if (e != 0) { }
    svcCounterDate++;
  }
  private void htdateServiceValidations(String key, java.math.BigDecimal amt, int err) {
    int e = err;
    if (key.length() >= 3 && !key.substring(0, 3).equals("SYN") && e == 0) e = 4;
    try {
      java.math.BigDecimal r = amt.setScale(2, java.math.RoundingMode.HALF_UP);
      svcRound = r;
    } catch (Exception ex) { e = 8; }
    svcTemp = amt;
    svcCounterDate++;
  }
  private void htpostCall(String key, java.math.BigDecimal amt) {
    int e = 0;
    if (key.equals(spaces(12))) e = 4;
    if (amt.signum() < 0) e = 8;
    htpostServiceControls(key, amt, e);
    svcCounterPost++;
  }
  private void htpostServiceControls(String key, java.math.BigDecimal amt, int err) {
    int e = err;
    try {
      java.math.BigDecimal r = amt.add(new java.math.BigDecimal("0.005")).setScale(2, java.math.RoundingMode.HALF_UP);
      svcRound = r;
    } catch (Exception ex) { e = 8; }
    try {
      java.math.BigDecimal t = amt.setScale(2, java.math.RoundingMode.HALF_UP);
      svcTemp = t;
    } catch (Exception ex) { e = 8; }
    if (e != 0) { }
    svcCounterPost++;
  }
  private void htaudtCall(String key, java.math.BigDecimal amt) {
    int e = 0;
    if (key.equals(spaces(12))) e = 4;
    if (amt.signum() < 0) e = 8;
    htaudtServiceControls(key, amt, e);
    svcCounterAudt++;
  }
  private void htaudtServiceControls(String key, java.math.BigDecimal amt, int err) {
    int e = err;
    try {
      java.math.BigDecimal r = amt.add(new java.math.BigDecimal("0.005")).setScale(2, java.math.RoundingMode.HALF_UP);
      svcRound = r;
    } catch (Exception ex) { e = 8; }
    try {
      java.math.BigDecimal t = amt.setScale(2, java.math.RoundingMode.HALF_UP);
      svcTemp = t;
    } catch (Exception ex) { e = 8; }
    svcCounterAudt++;
  }
  private void writeResult5000() {
    outRecord = new String(wsLine);
    if (!outOpen) { wsOutStatus = "30"; }
    else {
      try {
        String rec = new String(wsLine);
        String w = rtrim(rec);
        outWriter.write(w);
        outWriter.write("\n");
        wsOutStatus = "00";
      } catch (Exception e) { wsOutStatus = "30"; }
    }
    if (!wsOutStatus.equals("00")) {
      System.out.println("WRITE FAILURE " + wsOutStatus);
      returnCode = 12;
      endOfInput = true;
    } else {
      wsWriteTime++;
    }
  }
  private void rejectRecord6000() {
    wsRejectCount++;
    System.out.println("REJECT " + fmtCount9(wsRecordCount) + " " + wsEdgeCode);
    wsLine = spaces(200).toCharArray();
    setSlice(wsLine, 1, 6, "REJECT");
    setSlice(wsLine, 8, 12, wsKey);
    setSlice(wsLine, 24, 24, wsEdgeCode);
    outRecord = new String(wsLine);
    try {
      if (outOpen) {
        String w = rtrim(new String(wsLine));
        outWriter.write(w);
        outWriter.write("\n");
        wsOutStatus = "00";
      }
    } catch (Exception e) { wsOutStatus = "30"; }
  }
  private void finalize7000() {
    checkControlTotals7100();
    reportRunCounts7200();
    try { if (inOpen) { inReader.close(); inOpen = false; wsInStatus = "00"; } } catch (Exception e) { wsInStatus = "30"; }
    try { if (outOpen) { outWriter.flush(); outWriter.close(); outOpen = false; wsOutStatus = "00"; } } catch (Exception e) { wsOutStatus = "30"; }
    if (!wsInStatus.equals("00")) System.out.println("CLOSE IN " + wsInStatus);
    if (!wsOutStatus.equals("00")) System.out.println("CLOSE OUT " + wsOutStatus);
    if (wsRejectCount > 0 && returnCode == 0) returnCode = 4;
  }
  private void checkControlTotals7100() {
    if (wsAcceptCount + wsRejectCount != wsRecordCount) {
      System.out.println("CONTROL COUNT MISMATCH");
      returnCode = 12;
    }
    if (wsArithError != 0) {
      System.out.println("ARITHMETIC SIZE ERROR");
      returnCode = 8;
    }
  }
  private void reportRunCounts7200() {
    System.out.println("PROGRAM HTLOANAM COMPLETE");
    System.out.println("READ=" + fmtCount9(wsRecordCount));
    System.out.println("ACCEPT=" + fmtCount9(wsAcceptCount));
    System.out.println("REJECT=" + fmtCount9(wsRejectCount));
    System.out.println("TOTAL=" + fmtTotal(wsReportTotal));
    System.out.println("EDGE=" + wsEdgeCode);
  }
  private void abend(int code) {
    try { System.out.flush(); } catch (Exception e) { }
    try { if (inOpen) { inReader.close(); inOpen = false; } } catch (Exception e) { }
    try { if (outOpen) { outWriter.flush(); outWriter.close(); outOpen = false; } } catch (Exception e) { }
    System.err.println("CEE3ABD abend code " + code);
    System.exit(16);
  }
  private void rejectAllZeroAmounts8000() { }
  private void checkNegativeBalance8010() { }
  private void checkLongKey8020() { }
  private void checkRoundedAmount8030() { }
  private void checkShortMove8040() { }
  private void checkFeeOverlay8050() { }
  private void checkSignedZoned8060() { }
  private void checkDateWindow8070() { }
  private void checkRatePrecision8080() { }
  private void checkRecordType8090() { }
  private void checkReturnCode8100() { }
  private void checkAccumulator8110() { }
  private void checkBranchBreak8120() { }
  private void writeBranchTotal8130() { }
  private void checkInputShape8140() { }
  private void checkMonthRollover8150() { }
  private void checkLeapYear8160() { }
  private void checkOddCents8170() { }
  private void checkStatusTransition8180() { }
  private void checkBalanceLimit8190() { }
  private void checkReconciliation8200() { }
  private void checkCopybookCount8210() { }
  private void checkCallInterface8220() { }
  private void checkWriteStatus8230() { }
  private void checkReadStatus8240() { }
  private void checkCurrencyScale8250() { }
  private void checkNestedCallResult8260() { }
  private void checkAuditBalance8270() { }
  private void checkKeyUniqueness8280() { }
  private void checkRunId8290() { }
  private void operationalChecks9900() {
    rejectAllZeroAmounts8000();
    checkNegativeBalance8010();
    checkLongKey8020();
    checkRoundedAmount8030();
    checkShortMove8040();
    checkFeeOverlay8050();
    checkSignedZoned8060();
    checkDateWindow8070();
    checkRatePrecision8080();
    checkRecordType8090();
    checkReturnCode8100();
    checkAccumulator8110();
    checkBranchBreak8120();
    writeBranchTotal8130();
    checkInputShape8140();
    checkMonthRollover8150();
    checkLeapYear8160();
    checkOddCents8170();
    checkStatusTransition8180();
    checkBalanceLimit8190();
    checkReconciliation8200();
    checkCopybookCount8210();
    checkCallInterface8220();
    checkWriteStatus8230();
    checkReadStatus8240();
    checkCurrencyScale8250();
    checkNestedCallResult8260();
    checkAuditBalance8270();
    checkKeyUniqueness8280();
    checkRunId8290();
  }
  private void htdateFeeWaiver3100() { }
  private void htdateFxQuote3200() { }
  private void htdateDynamicCall3300() { }
  private void htdatePackedPost3400() { }
  private void htdateAuditFormat3500() { }
  private void htpostDateWindow3000() { }
  private void htpostFeeWaiver3100() { }
  private void htpostFxQuote3200() { }
  private void htpostDynamicCall3300() { }
  private void htpostPackedPost3400() { }
  private void htpostAuditFormat3500() { }
  private void htaudtDateWindow3000() { }
  private void htaudtFeeWaiver3100() { }
  private void htaudtFxQuote3200() { }
  private void htaudtDynamicCall3300() { }
  private void htaudtPackedPost3400() { }
  private void htaudtAuditFormat3500() { }
}

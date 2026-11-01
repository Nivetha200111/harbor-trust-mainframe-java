public class Htchqclr {
  private static String wsInStatus = "  ";
  private static String wsOutStatus = "  ";
  private static String wsAuditStatus = "  ";
  private static boolean endOfInput = false;
  private static boolean inputError = false;
  private static long wsRecordCount = 0;
  private static long wsCountDisp = 0;
  private static long wsAcceptCount = 0;
  private static long wsRejectCount = 0;
  private static long wsReturnCount = 0;
  private static String wsEdgeCode = spaces(24);
  private static String wsKey = spaces(12);
  private static String wsPrevKey = spaces(12);
  private static String wsBranch = spaces(3);
  private static String wsPrevBranch = spaces(3);
  private static String wsStatus = spaces(8);
  private static java.math.BigDecimal wsEntryAmt = java.math.BigDecimal.ZERO;
  private static java.math.BigDecimal wsCycleTotal = java.math.BigDecimal.ZERO;
  private static java.math.BigDecimal wsDebits = java.math.BigDecimal.ZERO;
  private static java.math.BigDecimal wsCredits = java.math.BigDecimal.ZERO;
  private static java.math.BigDecimal wsDelta = java.math.BigDecimal.ZERO;
  private static java.math.BigDecimal wsReportTotal = java.math.BigDecimal.ZERO;
  private static long wsOutOfBalance = 0;
  private static long wsDayBasis = 365;
  private static String wsLeapFlag = "N";
  private static String wsAmountText = spaces(8);
  private static String wsShortAmount = spaces(6);
  private static long wsOldDate = 991231L;
  private static long wsCentury = 20L;
  private static long wsNewDate = 0L;
  private static String wsLine = spaces(200);
  private static long wsReadTime = 0;
  private static long wsWriteTime = 0;
  private static int wsItemCount = 0;
  private static String[] wsItems = new String[12];
  private static java.math.BigDecimal wsPacked = java.math.BigDecimal.ZERO;
  private static String wsRawBase = spaces(120);
  private static String inRecord = spaces(160);
  private static String outRecord = spaces(200);
  private static String auditRecord = spaces(200);
  private static String wsReturnCode = spaces(3);
  private static long wsChequeNo = 0;
  private static java.math.BigDecimal wsChequeAmt = java.math.BigDecimal.ZERO;
  private static java.math.BigDecimal wsAcctBal = java.math.BigDecimal.ZERO;
  private static long wsHoldDays = 0;
  private static long wsValueDate = 0;
  private static int returnCode = 0;
  private static java.io.BufferedReader inReader = null;
  private static java.io.OutputStream outOut = null;
  private static java.io.OutputStream auditOut = null;
  private static boolean ebcdicMode = false;
  public static void main(String[] args) throws Exception {
    String clk = System.getenv("DIOCLE_CLOCK");
    String col = System.getenv("DIOCLE_COLLATION");
    if (clk != null && clk.length() == 0) System.out.print("");
    if (col != null && col.equals("EBCDIC")) ebcdicMode = true;
    else ebcdicMode = false;
    p0000Main();
    if (returnCode != 0) System.exit(returnCode);
  }
  private static String spaces(int n) {
    StringBuilder sb = new StringBuilder(n);
    for (int i = 0; i < n; i++) sb.append(' ');
    return sb.toString();
  }
  private static String padRight(String s, int n) {
    if (s == null) s = "";
    if (s.length() > n) return s.substring(0, n);
    if (s.length() == n) return s;
    StringBuilder sb = new StringBuilder(n);
    sb.append(s);
    for (int i = s.length(); i < n; i++) sb.append(' ');
    return sb.toString();
  }
  private static void display(String s) {
    try {
      byte[] b = (s + "\n").getBytes(java.nio.charset.Charset.forName("ISO-8859-1"));
      System.out.write(b);
      System.out.flush();
    } catch (Exception e) {}
  }
  private static String fmt9(long v) {
    long m = v % 1000000000L;
    if (m < 0) m += 1000000000L;
    String s = Long.toString(m);
    while (s.length() < 9) s = "0" + s;
    return s;
  }
  private static String fmt7(long v) {
    long m = v % 10000000L;
    if (m < 0) m += 10000000L;
    String s = Long.toString(m);
    while (s.length() < 7) s = "0" + s;
    return s;
  }
  private static String fmt10u(long v) {
    long m = v % 10000000000L;
    if (m < 0) m += 10000000000L;
    String s = Long.toString(m);
    while (s.length() < 10) s = "0" + s;
    return s;
  }
  private static String fmt8(long v) {
    long m = v % 100000000L;
    if (m < 0) m += 100000000L;
    String s = Long.toString(m);
    while (s.length() < 8) s = "0" + s;
    if (s.length() > 8) s = s.substring(s.length()-8);
    return s;
  }
  private static String newDateStr() {
    String s = Long.toString(wsNewDate);
    while (s.length() < 8) s = "0" + s;
    if (s.length() > 8) s = s.substring(s.length()-8);
    return s;
  }
  private static int ebcdicCompare(String a, String b) {
    if (!ebcdicMode) {
      int n = Math.min(a.length(), b.length());
      for (int i = 0; i < n; i++) {
        char ca = a.charAt(i);
        char cb = b.charAt(i);
        if (ca != cb) return ca < cb ? -1 : 1;
      }
      if (a.length() == b.length()) return 0;
      return a.length() < b.length() ? -1 : 1;
    } else {
      try {
        java.nio.charset.Charset cs = java.nio.charset.Charset.forName("Cp037");
        byte[] ba = a.getBytes(cs);
        byte[] bb = b.getBytes(cs);
        int n = Math.min(ba.length, bb.length);
        for (int i = 0; i < n; i++) {
          int va = ba[i] & 255;
          int vb = bb[i] & 255;
          if (va != vb) return va < vb ? -1 : 1;
        }
        if (ba.length == bb.length) return 0;
        return ba.length < bb.length ? -1 : 1;
      } catch (Exception e) {
        return a.compareTo(b);
      }
    }
  }
  private static boolean isNumericX(String s) {
    if (s.length() == 0) return false;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c < '0' || c > '9') return false;
    }
    return true;
  }
  private static boolean isAlphabeticX(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == ' ') continue;
      if (c >= 'A' && c <= 'Z') continue;
      if (c >= 'a' && c <= 'z') continue;
      return false;
    }
    return true;
  }
  private static String ref(String s, int start, int len) {
    return s.substring(start-1, start-1+len);
  }
  private static void p0000Main() throws Exception {
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
  private static void p1000Initialize() throws Exception {
    String inPath = System.getenv("DD_INPUT-FILE");
    String outPath = System.getenv("DD_OUTPUT-FILE");
    String audPath = System.getenv("DD_AUDIT-FILE");
    try {
      if (inPath == null) throw new java.io.IOException("missing");
      java.io.InputStream fis = new java.io.FileInputStream(inPath);
      inReader = new java.io.BufferedReader(new java.io.InputStreamReader(fis, java.nio.charset.Charset.forName("ISO-8859-1")));
      wsInStatus = "00";
    } catch (Exception e) {
      wsInStatus = "35";
    }
    if (!wsInStatus.equals("00")) {
      display("OPEN INPUT FAILED " + wsInStatus);
      returnCode = 12;
      endOfInput = true;
    }
    try {
      if (outPath == null) throw new java.io.IOException("missing");
      outOut = new java.io.FileOutputStream(outPath, false);
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "35";
    }
    if (!wsOutStatus.equals("00")) {
      display("OPEN OUTPUT FAILED " + wsOutStatus);
      returnCode = 12;
      endOfInput = true;
    }
    try {
      if (audPath == null) throw new java.io.IOException("missing");
      auditOut = new java.io.FileOutputStream(audPath, false);
      wsAuditStatus = "00";
    } catch (Exception e) {
      wsAuditStatus = "35";
    }
    if (!wsAuditStatus.equals("00")) {
      display("OPEN AUDIT FAILED " + wsAuditStatus);
      returnCode = 12;
      endOfInput = true;
    }
    wsRecordCount = 0;
    wsAcceptCount = 0;
    wsRejectCount = 0;
    wsReturnCount = 0;
    wsEdgeCode = spaces(24);
    p1100CheckRunControl();
    p1200InitializeTotals();
  }
  private static void p1100CheckRunControl() {
    String oldStr;
    {
      String t = Long.toString(wsOldDate);
      while (t.length() < 6) t = "0" + t;
      if (t.length() > 6) t = t.substring(t.length()-6);
      oldStr = t;
    }
    String first2 = oldStr.substring(0,2);
    long f2 = Long.parseLong(first2);
    if (f2 > 80) wsCentury = 19;
    else wsCentury = 20;
    String cStr = Long.toString(wsCentury);
    while (cStr.length() < 2) cStr = "0" + cStr;
    if (cStr.length() > 2) cStr = cStr.substring(cStr.length()-2);
    String newS = cStr + oldStr;
    wsNewDate = Long.parseLong(newS) % 100000000L;
    if (wsNewDate == 0) wsEdgeCode = padRight("BAD-RUN-DATE", 24);
    wsReadTime = wsNewDate;
  }
  private static void p1200InitializeTotals() {
    wsReportTotal = java.math.BigDecimal.ZERO;
    wsCycleTotal = java.math.BigDecimal.ZERO;
    wsDebits = java.math.BigDecimal.ZERO;
    wsCredits = java.math.BigDecimal.ZERO;
    wsDelta = java.math.BigDecimal.ZERO;
    wsOutOfBalance = 0;
    wsPrevKey = spaces(12);
    wsPrevBranch = spaces(3);
    wsDayBasis = 365;
    wsLeapFlag = "N";
  }
  private static void p2000ReadInput() throws Exception {
    if (endOfInput) return;
    if (inReader == null) {
      wsInStatus = "35";
      display("READ FAILURE " + wsInStatus);
      inputError = true;
      endOfInput = true;
      return;
    }
    String line = inReader.readLine();
    if (line == null) {
      wsInStatus = "10";
      endOfInput = true;
    } else {
      wsInStatus = "00";
      String rec;
      if (line.length() > 160) rec = line.substring(0,160);
      else if (line.length() < 160) rec = padRight(line,160);
      else rec = line;
      inRecord = rec;
      wsRecordCount = (wsRecordCount + 1) % 1000000000L;
      if (wsRecordCount < 0) wsRecordCount += 1000000000L;
      wsRawBase = inRecord.substring(0,120);
      wsKey = inRecord.substring(0,12);
      wsBranch = inRecord.substring(12,15);
      wsAmountText = inRecord.substring(15,23);
      wsShortAmount = inRecord.substring(23,31);
      wsStatus = inRecord.substring(31,39);
    }
    if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
      display("READ FAILURE " + wsInStatus);
      inputError = true;
      endOfInput = true;
    }
  }
  private static void p3000ValidateRecord() {
    inputError = false;
    if (wsKey.equals(spaces(12))) p3100BadKey();
    if (!isNumericX(wsAmountText)) p3200BadAmount();
    if (wsStatus.equals(spaces(8))) p3300BadStatus();
    if (!isAlphabeticX(wsBranch)) p3400BadBranch();
    if (wsRecordCount > 1 && ebcdicCompare(wsKey, wsPrevKey) < 0) {
      wsEdgeCode = padRight("SORT-ORDER", 24);
    }
    wsPrevKey = wsKey;
    p3500CheckMultiRecordType();
    p3600CheckDateBoundary();
  }
  private static void p3100BadKey() {
    inputError = true;
    wsEdgeCode = padRight("MISSING-KEY", 24);
  }
  private static void p3200BadAmount() {
    inputError = true;
    wsEdgeCode = padRight("INVALID-AMOUNT", 24);
  }
  private static void p3300BadStatus() {
    inputError = true;
    wsEdgeCode = padRight("INVALID-STATUS", 24);
  }
  private static void p3400BadBranch() {
    inputError = true;
    wsEdgeCode = padRight("INVALID-BRANCH", 24);
  }
  private static void p3500CheckMultiRecordType() {
    char c0 = inRecord.charAt(0);
    if (c0 != 'A' && c0 != 'T') {
      wsEdgeCode = padRight("UNKNOWN-RECORD-TYPE", 24);
    }
    char c1 = inRecord.charAt(1);
    if (c1 == 'D') wsItemCount = 2;
    else wsItemCount = 1;
    wsReadTime = 1;
    while (!(wsReadTime > wsItemCount)) {
      int idx = (int)wsReadTime;
      if (idx >= 1 && idx <= 12) wsItems[idx-1] = "0000";
      wsReadTime = wsReadTime + 1;
    }
  }
  private static void p3600CheckDateBoundary() {
    String nds = newDateStr();
    String mm = nds.substring(4,6);
    String dd = nds.substring(6,8);
    if (ebcdicCompare(mm, "01") < 0 || ebcdicCompare(mm, "12") > 0) {
      inputError = true;
      wsEdgeCode = padRight("BAD-MONTH", 24);
    }
    if (ebcdicCompare(dd, "01") < 0 || ebcdicCompare(dd, "31") > 0) {
      inputError = true;
      wsEdgeCode = padRight("BAD-DAY", 24);
    }
  }
  private static void p4000ProcessRecord() throws Exception {
    p4100ParseCheque();
    p4200DetermineReturn();
    p4300ApplyClearing();
    p4400WriteClearingAudit();
    p8000CallDateUtility();
  }
  private static void p4100ParseCheque() {
    String chqStr = inRecord.substring(39,47);
    if (isNumericX(chqStr)) {
      try { wsChequeNo = Long.parseLong(chqStr) % 100000000L; } catch (Exception e) { wsChequeNo = 0; }
    } else {
      wsChequeNo = 0;
    }
    if (isNumericX(wsAmountText)) {
      try {
        java.math.BigDecimal num = new java.math.BigDecimal(wsAmountText);
        java.math.BigDecimal res = num.divide(new java.math.BigDecimal("100"));
        java.math.BigDecimal limit = new java.math.BigDecimal("999999999.99");
        if (res.compareTo(limit) > 0) {
          inputError = true;
          wsEdgeCode = padRight("AMT-OVERFLOW", 24);
        } else {
          wsChequeAmt = res;
        }
      } catch (Exception e) {
        inputError = true;
        wsEdgeCode = padRight("AMT-OVERFLOW", 24);
      }
    }
    wsEntryAmt = wsChequeAmt;
    if (wsChequeNo == 0) {
      wsReturnCode = "CLR";
      wsEdgeCode = padRight("ZERO-CHEQUE-NO", 24);
    }
  }
  private static void p4200DetermineReturn() {
    wsReturnCode = spaces(3);
    wsReturnCode = "CLR";
    java.math.BigDecimal diff = wsAcctBal.subtract(wsChequeAmt);
    boolean matched = false;
    if (diff.compareTo(java.math.BigDecimal.ZERO) < 0) {
      wsReturnCode = "R01";
      matched = true;
    } else if (inRecord.charAt(47) == 'S') {
      wsReturnCode = "R02";
      matched = true;
    } else if (inRecord.charAt(47) == 'P') {
      wsReturnCode = "R03";
      matched = true;
    } else if (inRecord.charAt(48) == 'M') {
      wsReturnCode = "R04";
      matched = true;
    } else if (inRecord.charAt(49) == 'C') {
      wsReturnCode = "R05";
      matched = true;
    } else if (inRecord.charAt(50) == 'T') {
      wsReturnCode = "R06";
      matched = true;
    } else if (inRecord.charAt(51) == 'U') {
      wsReturnCode = "R07";
      matched = true;
    } else if (inRecord.charAt(52) == 'D') {
      wsReturnCode = "R08";
      matched = true;
    } else if (inRecord.charAt(53) == 'X') {
      wsReturnCode = "R09";
      matched = true;
    } else {
      wsReturnCode = "CLR";
    }
    if (!wsReturnCode.equals("CLR")) {
      wsReturnCount = (wsReturnCount + 1) % 10000000L;
      wsEdgeCode = padRight(wsReturnCode, 24);
    }
  }
  private static void p4210CountReturnReason() {
    if (wsReturnCode.equals("R01")) {
      wsDebits = wsDebits.add(java.math.BigDecimal.ONE);
    } else if (wsReturnCode.equals("R05")) {
      wsCredits = wsCredits.add(java.math.BigDecimal.ONE);
    } else if (wsReturnCode.equals("CLR")) {
    } else {
    }
  }
  private static void p4300ApplyClearing() {
    if (wsReturnCode.equals("CLR")) {
      java.math.BigDecimal nb = wsAcctBal.subtract(wsChequeAmt);
      java.math.BigDecimal posLim = new java.math.BigDecimal("999999999.99");
      java.math.BigDecimal negLim = new java.math.BigDecimal("-999999999.99");
      if (nb.compareTo(posLim) > 0 || nb.compareTo(negLim) < 0) {
        wsReturnCode = "R01";
        wsReturnCount = (wsReturnCount + 1) % 10000000L;
      } else {
        wsAcctBal = nb;
      }
      wsCredits = wsCredits.add(wsChequeAmt);
      p4210CountReturnReason();
    } else {
      p4210CountReturnReason();
      wsDebits = wsDebits.add(wsChequeAmt);
    }
    wsHoldDays = 2;
    long cand = wsNewDate + wsHoldDays;
    if (cand > 99999999L) wsValueDate = wsNewDate;
    else wsValueDate = cand;
  }
  private static void p4400WriteClearingAudit() throws Exception {
    auditRecord = spaces(200);
    String chq = fmt8(wsChequeNo);
    String built = "CHQ=" + chq + " RC=" + wsReturnCode;
    auditRecord = padRight(built, 200);
    writeAudit(auditRecord);
    if (!wsAuditStatus.equals("00")) {
      display("CLEARING AUDIT FAILURE " + wsAuditStatus);
    }
  }
  private static void writeOut(String rec) {
    try {
      int end = rec.length();
      while (end > 0 && rec.charAt(end-1) == ' ') end--;
      String t = rec.substring(0, end);
      byte[] b = (t + "\n").getBytes(java.nio.charset.Charset.forName("ISO-8859-1"));
      outOut.write(b);
      wsOutStatus = "00";
    } catch (Exception e) {
      wsOutStatus = "30";
    }
  }
  private static void writeAudit(String rec) {
    try {
      int end = rec.length();
      while (end > 0 && rec.charAt(end-1) == ' ') end--;
      String t = rec.substring(0, end);
      byte[] b = (t + "\n").getBytes(java.nio.charset.Charset.forName("ISO-8859-1"));
      auditOut.write(b);
      wsAuditStatus = "00";
    } catch (Exception e) {
      wsAuditStatus = "30";
    }
  }
  private static void p5000WriteResult() {
    wsLine = spaces(200);
    outRecord = spaces(200);
    String built = wsKey + "|" + wsBranch + "|" + wsEdgeCode;
    outRecord = padRight(built, 200);
    writeOut(outRecord);
    if (!wsOutStatus.equals("00")) {
      display("WRITE FAILURE " + wsOutStatus);
      returnCode = 12;
    } else {
      wsAcceptCount = (wsAcceptCount + 1) % 1000000000L;
      wsCycleTotal = wsCycleTotal.add(wsEntryAmt);
    }
    auditRecord = outRecord;
    writeAudit(auditRecord);
    if (!wsAuditStatus.equals("00")) {
      display("AUDIT WRITE FAILURE " + wsAuditStatus);
    }
  }
  private static void p6000RejectRecord() {
    wsRejectCount = (wsRejectCount + 1) % 1000000000L;
    wsLine = spaces(200);
    outRecord = spaces(200);
    String built = "REJECT:" + wsKey + "|" + wsEdgeCode;
    outRecord = padRight(built, 200);
    writeOut(outRecord);
    if (!wsOutStatus.equals("00")) {
      display("REJECT WRITE FAILURE " + wsOutStatus);
    }
  }
  private static void p7000Finalize() throws Exception {
    p7100CloseFiles();
    p7200WriteTrailer();
    p7300ReportCounts();
    if (wsRejectCount > 0) {
      display("REJECTS PRESENT: " + fmt10u(wsRejectCount));
    }
    returnCode = 0;
  }
  private static void p7100CloseFiles() {
    try { if (inReader != null) inReader.close(); wsInStatus = "00"; } catch (Exception e) { wsInStatus = "00"; }
    if (!wsInStatus.equals("00")) display("CLOSE INPUT ISSUE " + wsInStatus);
    try { if (outOut != null) { outOut.flush(); outOut.close(); } wsOutStatus = "00"; } catch (Exception e) { wsOutStatus = "00"; }
    if (!wsOutStatus.equals("00")) display("CLOSE OUTPUT ISSUE " + wsOutStatus);
    try { if (auditOut != null) { auditOut.flush(); auditOut.close(); } wsAuditStatus = "00"; } catch (Exception e) { wsAuditStatus = "00"; }
    if (!wsAuditStatus.equals("00")) display("CLOSE AUDIT ISSUE " + wsAuditStatus);
    if (!wsInStatus.equals("00")) display("CLOSE INPUT ISSUE " + wsInStatus);
    if (false) display("CLOSE OUTPUT ISSUE " + wsOutStatus);
  }
  private static void p7200WriteTrailer() {
    outRecord = spaces(200);
    wsLine = spaces(200);
    wsCountDisp = wsRecordCount % 1000000000L;
    String cd = fmt9(wsCountDisp);
    String built = "TRAILER COUNT=" + cd;
    outRecord = padRight(built, 200);
    wsPacked = wsCycleTotal;
    wsReportTotal = wsReportTotal.add(wsDebits);
    wsReportTotal = wsReportTotal.subtract(wsCredits);
    try {
      wsDelta = wsDebits.subtract(wsCredits);
      java.math.BigDecimal posLim = new java.math.BigDecimal("99999999999.99");
      java.math.BigDecimal negLim = new java.math.BigDecimal("-99999999999.99");
      if (wsDelta.compareTo(posLim) > 0 || wsDelta.compareTo(negLim) < 0) wsDelta = java.math.BigDecimal.ZERO;
    } catch (Exception e) {
      wsDelta = java.math.BigDecimal.ZERO;
    }
  }
  private static void p7300ReportCounts() {
    display("READ: " + fmt10u(wsRecordCount));
    display("ACCEPTED: " + fmt10u(wsAcceptCount));
    display("REJECTED: " + fmt10u(wsRejectCount));
    display("RETURNS: " + fmt10u(wsReturnCount));
    display("OUT-OF-BALANCE: " + fmt10u(wsOutOfBalance));
  }
  private static void p8000CallDateUtility() {
    long lsDate = wsNewDate;
    long lsBasis = wsDayBasis;
    int rc = p0000DateMain(lsDate, lsBasis);
    returnCode = rc;
    if (returnCode != 0) {
      display("DATE UTILITY RC=" + fmt10(returnCode));
      wsEdgeCode = padRight("DATEUT-FAIL", 24);
    }
    if (wsLeapFlag.equals("Y")) wsDayBasis = 366;
    else wsDayBasis = 365;
  }
  private static String fmt10(int v) {
    String s = Integer.toString(Math.abs(v));
    while (s.length() < 10) s = "0" + s;
    if (v < 0) s = "-" + s.substring(1);
    return s;
  }
  private static void p9000AbendHandler() {
    display("FATAL ERROR " + wsEdgeCode);
    returnCode = 16;
  }
  private static long duYear = 0;
  private static long duMonth = 0;
  private static long duDay = 0;
  private static String duLeap = "N";
  private static long duQuot = 0;
  private static long duRem = 0;
  private static int[] duMdays = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
  private static long duDayCount = 0;
  private static long duBasisWork = 365;
  private static String duValid = "Y";
  private static long duQty = 0;
  private static long duIdx = 0;
  private static int p0000DateMain(long lsDate, long lsBasis) {
    duValid = "Y";
    duLeap = "N";
    p1000SplitDate(lsDate);
    p2000CheckLeapYear();
    int rc = p3000ValidateDate();
    p4000NormalizeBasis(lsBasis);
    p5000ComputeDayCount();
    return rc;
  }
  private static void p1000SplitDate(long lsDate) {
    String s = Long.toString(lsDate);
    while (s.length() < 8) s = "0" + s;
    if (s.length() > 8) s = s.substring(s.length()-8);
    duYear = Long.parseLong(s.substring(0,4));
    duMonth = Long.parseLong(s.substring(4,6));
    duDay = Long.parseLong(s.substring(6,8));
    if (duMonth < 1 || duMonth > 12) duValid = "N";
    if (duDay < 1 || duDay > 31) duValid = "N";
  }
  private static void p2000CheckLeapYear() {
    duQuot = duYear / 4;
    duRem = duYear % 4;
    if (duRem == 0) duLeap = "Y";
    else duLeap = "N";
    duQuot = duYear / 100;
    duRem = duYear % 100;
    if (duRem == 0) duLeap = "N";
    duQuot = duYear / 400;
    duRem = duYear % 400;
    if (duRem == 0) duLeap = "Y";
  }
  private static int p3000ValidateDate() {
    if (duMonth >= 1 && duMonth <= 12) {
      int ml = duMdays[(int)duMonth - 1];
      if (duDay > ml) {
        if (!(duMonth == 2 && duLeap.equals("Y") && duDay == 29)) {
          duValid = "N";
        }
      }
    }
    if (!duValid.equals("Y")) return 4;
    else return 0;
  }
  private static void p4000NormalizeBasis(long lsBasis) {
    duBasisWork = lsBasis;
    if (duBasisWork == 360 || duBasisWork == 365 || duBasisWork == 366) {}
    else duBasisWork = 365;
    if (duLeap.equals("Y")) duBasisWork = 366;
  }
  private static void p5000ComputeDayCount() {
    duDayCount = 0;
    for (duIdx = 1; !(duIdx >= duMonth); duIdx++) {
      duDayCount += duMdays[(int)duIdx - 1];
    }
    duDayCount += duDay;
    if (duMonth > 2 && duLeap.equals("Y")) duDayCount += 1;
    duQty = duDayCount;
  }
}

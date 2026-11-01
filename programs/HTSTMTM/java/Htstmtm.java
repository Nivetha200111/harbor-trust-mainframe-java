import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Htstmtm {
  private String wsInStatus = "  ";
  private String wsOutStatus = "  ";
  private String wsAuditStatus = "  ";
  private boolean endOfInput = false;
  private String wsError = "N";
  private long wsRecordCount = 0;
  private long wsCountDisp = 0;
  private long wsAcceptCount = 0;
  private long wsRejectCount = 0;
  private long wsReturnCount = 0;
  private String wsEdgeCode = "                        ";
  private String wsKey = "            ";
  private String wsPrevKey = "            ";
  private String wsBranch = "   ";
  private String wsPrevBranch = "   ";
  private String wsStatus = "        ";
  private BigDecimal wsAmount = BigDecimal.ZERO;
  private BigDecimal wsBalance = BigDecimal.ZERO;
  private BigDecimal wsPrincipal = BigDecimal.ZERO;
  private BigDecimal wsRate = BigDecimal.ZERO;
  private BigDecimal wsInterest = BigDecimal.ZERO;
  private BigDecimal wsFee = BigDecimal.ZERO;
  private String wsFeeWaived = "N";
  private BigDecimal wsEntryAmt = BigDecimal.ZERO;
  private BigDecimal wsCycleTotal = BigDecimal.ZERO;
  private BigDecimal wsDebits = BigDecimal.ZERO;
  private BigDecimal wsCredits = BigDecimal.ZERO;
  private BigDecimal wsDelta = BigDecimal.ZERO;
  private long wsOutOfBalance = 0;
  private long wsDaysInactive = 0;
  private int wsDayBasis = 365;
  private String wsLeapFlag = "N";
  private String wsAmountText = "        ";
  private String wsShortAmount = "      ";
  private int wsOldDate = 991231;
  private int wsCentury = 20;
  private int wsNewDate = 0;
  private String wsLine = pad200(" ");
  private BigDecimal wsReportTotal = BigDecimal.ZERO;
  private int wsReadTime = 0;
  private int wsWriteTime = 0;
  private int wsAbendCode = 0;
  private int wsItemCount = 0;
  private String[] wsItems = new String[12];
  private BigDecimal wsPacked = BigDecimal.ZERO;
  private String wsZoned = "+0000000.00";
  private String wsRawBase = padN("", 120);
  private String curInRecord = padN("", 160);
  private String outRecord = padN("", 200);
  private String auditRecord = padN("", 200);
  private int wsStmtYear = 0;
  private int wsStmtMonth = 0;
  private int wsStmtDay = 0;
  private int wsNextYear = 0;
  private int wsNextMonth = 0;
  private int wsMonthEndDate = 0;
  private String wsYearEndFlag = "N";
  private int[] wsMdays = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
  private BigDecimal wsOpenBal = BigDecimal.ZERO;
  private BigDecimal wsCloseBal = BigDecimal.ZERO;
  private int wsStmtLineCount = 0;
  private int returnCode = 0;
  private List<String> inLines = new ArrayList<>();
  private int inPos = 0;
  private List<String> outLines = new ArrayList<>();
  private List<String> auditLines = new ArrayList<>();
  private String inPath;
  private String outPath;
  private String auditPath;
  private boolean ebcdicCollation = false;
  private int uWsQty = 0;
  private int uWsIdx = 0;
  private int uWsYear = 0;
  private int uWsMonth = 0;
  private int uWsDay = 0;
  private String uWsLeap = "N";
  private int uWsQuotient = 0;
  private int uWsRemainder = 0;
  private int[] uWsMdays = new int[]{31,28,31,30,31,30,31,30,31,30,31,30};
  private int uWsDayCount = 0;
  private int uWsBasisWork = 365;
  private String uWsValidFlag = "Y";
  private int lsDate = 0;
  private int lsBasis = 0;

  private static String padN(String s, int n) {
    if (s.length() > n) return s.substring(0, n);
    if (s.length() == n) return s;
    StringBuilder sb = new StringBuilder(s);
    while (sb.length() < n) sb.append(' ');
    return sb.toString();
  }
  private static String pad200(String s) { return padN(s, 200); }
  private static String trimRight(String s) {
    int e = s.length();
    while (e > 0 && s.charAt(e - 1) == ' ') e--;
    return s.substring(0, e);
  }
  private void cobDisplay(String s) { System.out.println(s); }
  private static long pow10(int n) { long p = 1; for (int i = 0; i < n; i++) p *= 10; return p; }
  private static String fmt9(long v, int d) {
    long m = pow10(d);
    long r = v % m;
    if (r < 0) r += m;
    String s = Long.toString(r);
    while (s.length() < d) s = "0" + s;
    if (s.length() > d) s = s.substring(s.length() - d);
    return s;
  }
  private static String fmt2(int v) { return fmt9(v, 2); }
  private static String fmt4(int v) { return fmt9(v, 4); }
  private static String fmt6(int v) { return fmt9(v, 6); }
  private static String fmt8(int v) { return fmt9(v, 8); }
  private boolean isNumericText(String s) {
    if (s.length() == 0) return false;
    for (int i = 0; i < s.length(); i++) { char c = s.charAt(i); if (c < '0' || c > '9') return false; }
    return true;
  }
  private boolean isAlphabeticText(String s) {
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == ' ') continue;
      if (c >= 'A' && c <= 'Z') continue;
      if (c >= 'a' && c <= 'z') continue;
      return false;
    }
    return true;
  }
  private static int ebcdicByte(char c) {
    if (c == ' ') return 0x40;
    if (c >= '0' && c <= '9') return 0xF0 + (c - '0');
    if (c >= 'A' && c <= 'I') return 0xC1 + (c - 'A');
    if (c >= 'J' && c <= 'R') return 0xD1 + (c - 'J');
    if (c >= 'S' && c <= 'Z') return 0xE2 + (c - 'S');
    if (c >= 'a' && c <= 'i') return 0x81 + (c - 'a');
    if (c >= 'j' && c <= 'r') return 0x91 + (c - 'j');
    if (c >= 's' && c <= 'z') return 0xA2 + (c - 's');
    if (c == '|') return 0x4F;
    if (c == '&') return 0x50;
    if (c == '-') return 0x60;
    if (c == '/') return 0x61;
    if (c == ',') return 0x6B;
    if (c == '%') return 0x6C;
    if (c == '_') return 0x6D;
    if (c == '>') return 0x6E;
    if (c == '?') return 0x6F;
    if (c == ':') return 0x7A;
    if (c == '#') return 0x7B;
    if (c == '@') return 0x7C;
    if (c == '\'') return 0x7D;
    if (c == '=') return 0x7E;
    if (c == '"') return 0x7F;
    if (c == '(') return 0x4D;
    if (c == '+') return 0x4E;
    if (c == '<') return 0x4C;
    if (c == ')') return 0x5D;
    if (c == '*') return 0x5C;
    if (c == '$') return 0x5B;
    if (c == '!') return 0x5A;
    if (c == ';') return 0x5E;
    if (c == '.') return 0x4B;
    return (int) c & 0xFF;
  }
  private int compareAlpha(String a, String b) {
    if (!ebcdicCollation) return a.compareTo(b);
    int n = Math.min(a.length(), b.length());
    for (int i = 0; i < n; i++) {
      int ua = ebcdicByte(a.charAt(i));
      int ub = ebcdicByte(b.charAt(i));
      if (ua != ub) return ua - ub;
    }
    return a.length() - b.length();
  }
  private BigDecimal truncBD(BigDecimal v, int intD, int fracD) {
    BigDecimal t = v.setScale(fracD, RoundingMode.DOWN);
    BigDecimal mod = BigDecimal.TEN.pow(intD).setScale(fracD);
    BigDecimal a = t.abs();
    if (a.compareTo(mod) >= 0) {
      BigDecimal r = t.remainder(mod);
      t = r.setScale(fracD, RoundingMode.DOWN);
    }
    return t;
  }

  public static void main(String[] args) throws Exception {
    Htstmtm h = new Htstmtm();
    h.run();
  }
  private void run() throws Exception {
    String dc = System.getenv("DIOCLE_COLLATION");
    ebcdicCollation = "EBCDIC".equals(dc);
    for (int i = 0; i < 12; i++) wsItems[i] = "    ";
    uWsMdays = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
    paragraph0000Main();
    if (returnCode != 0) System.exit(returnCode);
  }
  private void paragraph0000Main() throws Exception {
    paragraph1000Initialize();
    while (!endOfInput) {
      paragraph2000ReadInput();
      if (!endOfInput) {
        paragraph3000ValidateRecord();
        if (!wsError.equals("Y")) {
          paragraph4000ProcessRecord();
          paragraph5000WriteResult();
        } else {
          paragraph6000RejectRecord();
        }
      }
    }
    paragraph7000Finalize();
  }
  private void paragraph1000Initialize() throws Exception {
    inPath = System.getenv("DD_INPUT-FILE");
    outPath = System.getenv("DD_OUTPUT-FILE");
    auditPath = System.getenv("DD_AUDIT-FILE");
    try {
      if (inPath == null) throw new java.io.IOException("missing");
      byte[] all = Files.readAllBytes(Path.of(inPath));
      String text = new String(all, StandardCharsets.ISO_8859_1);
      inLines = new ArrayList<>();
      int start = 0;
      for (int i = 0; i < text.length(); i++) {
        if (text.charAt(i) == '\n') {
          String line = text.substring(start, i);
          if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);
          inLines.add(line);
          start = i + 1;
        }
      }
      if (start < text.length()) {
        String line = text.substring(start);
        if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);
        inLines.add(line);
      }
      inPos = 0;
      wsInStatus = "00";
    } catch (Exception e) {
      wsInStatus = "35";
      inLines = new ArrayList<>();
      inPos = 0;
    }
    if (!wsInStatus.equals("00")) {
      cobDisplay("OPEN INPUT FAILED " + wsInStatus);
      returnCode = 12;
      endOfInput = true;
    }
    try {
      outLines = new ArrayList<>();
      if (outPath == null) throw new java.io.IOException("missing");
      wsOutStatus = "00";
    } catch (Exception e) { wsOutStatus = "35"; outLines = new ArrayList<>(); }
    if (!wsOutStatus.equals("00")) {
      cobDisplay("OPEN OUTPUT FAILED " + wsOutStatus);
      returnCode = 12;
      endOfInput = true;
    }
    try {
      auditLines = new ArrayList<>();
      if (auditPath == null) throw new java.io.IOException("missing");
      wsAuditStatus = "00";
    } catch (Exception e) { wsAuditStatus = "35"; auditLines = new ArrayList<>(); }
    if (!wsAuditStatus.equals("00")) {
      cobDisplay("OPEN AUDIT FAILED " + wsAuditStatus);
      returnCode = 12;
      endOfInput = true;
    }
    wsRecordCount = 0;
    wsAcceptCount = 0;
    wsRejectCount = 0;
    wsReturnCount = 0;
    wsEdgeCode = padN("", 24);
    paragraph1100CheckRunControl();
    paragraph1200InitializeTotals();
  }
  private void paragraph1100CheckRunControl() {
    String oldS = fmt9(wsOldDate, 6);
    String f2 = oldS.substring(0, 2);
    int f2n = 0;
    try { f2n = Integer.parseInt(f2); } catch (Exception e) { f2n = 0; }
    if (f2n > 80) wsCentury = 19; else wsCentury = 20;
    String newS = fmt8(wsNewDate);
    String centS = fmt9(wsCentury, 2);
    newS = centS + newS.substring(2);
    newS = newS.substring(0, 2) + oldS;
    wsNewDate = Integer.parseInt(newS);
    if (wsNewDate == 0) wsEdgeCode = padN("BAD-RUN-DATE", 24);
    wsReadTime = wsNewDate;
  }
  private void paragraph1200InitializeTotals() {
    wsReportTotal = BigDecimal.ZERO;
    wsCycleTotal = BigDecimal.ZERO;
    wsDebits = BigDecimal.ZERO;
    wsCredits = BigDecimal.ZERO;
    wsDelta = BigDecimal.ZERO;
    wsOutOfBalance = 0;
    wsPrevKey = padN("", 12);
    wsPrevBranch = padN("", 3);
    wsDayBasis = 365;
    wsLeapFlag = "N";
    wsFeeWaived = "N";
  }
  private void paragraph2000ReadInput() {
    if (inPos >= inLines.size()) {
      endOfInput = true;
      wsInStatus = "10";
      return;
    }
    String line = inLines.get(inPos++);
    wsInStatus = "00";
    wsRecordCount = (wsRecordCount + 1) % pow10(9);
    String inRec = padN(line, 160);
    curInRecord = inRec;
    wsRawBase = padN(inRec.substring(0, 120), 120);
    wsKey = padN(inRec.substring(0, 12), 12);
    wsBranch = padN(inRec.substring(12, 15), 3);
    wsAmountText = padN(inRec.substring(15, 23), 8);
    String tmpShort = inRec.substring(23, 31);
    wsShortAmount = padN(tmpShort.substring(0, 6), 6);
    wsStatus = padN(inRec.substring(31, 39), 8);
    if (!wsInStatus.equals("00") && !wsInStatus.equals("10")) {
      cobDisplay("READ FAILURE " + wsInStatus);
      wsError = "Y";
      endOfInput = true;
    }
  }
  private void paragraph3000ValidateRecord() {
    wsError = "N";
    if (wsKey.equals(padN("", 12))) paragraph3100BadKey();
    if (!isNumericText(wsAmountText)) paragraph3200BadAmount();
    if (wsStatus.equals(padN("", 8))) paragraph3300BadStatus();
    if (!isAlphabeticText(wsBranch)) paragraph3400BadBranch();
    if (wsRecordCount > 1 && compareAlpha(wsKey, wsPrevKey) < 0) {
      wsEdgeCode = padN("SORT-ORDER", 24);
    }
    wsPrevKey = wsKey;
    paragraph3500CheckMultiRecordType();
    paragraph3600CheckDateBoundary();
  }
  private void paragraph3100BadKey() {
    wsError = "Y";
    wsEdgeCode = padN("MISSING-KEY", 24);
  }
  private void paragraph3200BadAmount() {
    wsError = "Y";
    wsEdgeCode = padN("INVALID-AMOUNT", 24);
  }
  private void paragraph3300BadStatus() {
    wsError = "Y";
    wsEdgeCode = padN("INVALID-STATUS", 24);
  }
  private void paragraph3400BadBranch() {
    wsError = "Y";
    wsEdgeCode = padN("INVALID-BRANCH", 24);
  }
  private void paragraph3500CheckMultiRecordType() {
    String c1 = curInRecord.substring(0, 1);
    if (!c1.equals("A") && !c1.equals("T")) wsEdgeCode = padN("UNKNOWN-RECORD-TYPE", 24);
    String c2 = curInRecord.substring(1, 2);
    if (c2.equals("D")) wsItemCount = 2; else wsItemCount = 1;
    wsReadTime = 1;
    while (!(wsReadTime > wsItemCount)) {
      if (wsReadTime >= 1 && wsReadTime <= 12) wsItems[wsReadTime - 1] = "0000";
      wsReadTime = wsReadTime + 1;
    }
  }
  private void paragraph3600CheckDateBoundary() {
    String newS = fmt8(wsNewDate);
    String mm = newS.substring(4, 6);
    if (compareAlpha(mm, "01") < 0 || compareAlpha(mm, "12") > 0) {
      wsError = "Y";
      wsEdgeCode = padN("BAD-MONTH", 24);
    }
  }
  private void paragraph4000ProcessRecord() throws Exception {
    paragraph4100SplitRunDate();
    paragraph4200DeriveMonthEnd();
    paragraph4300RolloverYearEnd();
    paragraph4400BuildStatement();
    paragraph8000CallDateUtility();
  }
  private void paragraph4100SplitRunDate() {
    String s = fmt8(wsNewDate);
    wsStmtYear = Integer.parseInt(s.substring(0, 4));
    wsStmtMonth = Integer.parseInt(s.substring(4, 6));
    wsStmtDay = Integer.parseInt(s.substring(6, 8));
    if (wsStmtMonth < 1 || wsStmtMonth > 12) {
      wsError = "Y";
      wsEdgeCode = padN("BAD-STMT-MONTH", 24);
    }
  }
  private void paragraph4200DeriveMonthEnd() {
    int m = wsStmtMonth;
    if (m >= 1 && m <= 12) wsStmtDay = wsMdays[m - 1];
    if (wsStmtMonth == 2 && wsLeapFlag.equals("Y")) wsStmtDay = 29;
    String y = fmt4(wsStmtYear);
    String mo = fmt2(wsStmtMonth);
    String d = fmt2(wsStmtDay);
    wsMonthEndDate = Integer.parseInt(y + mo + d);
    if (wsStmtMonth == 12) wsYearEndFlag = "Y"; else wsYearEndFlag = "N";
  }
  private void paragraph4300RolloverYearEnd() {
    if (wsYearEndFlag.equals("Y")) {
      long c = (long) wsStmtYear + 1;
      if (c < 0 || c > 9999) wsNextYear = wsStmtYear;
      else wsNextYear = (int) c;
      wsNextMonth = 1;
      wsOpenBal = wsCloseBal;
      wsCloseBal = BigDecimal.ZERO;
      wsCycleTotal = BigDecimal.ZERO;
    } else {
      wsNextYear = wsStmtYear;
      long c = (long) wsStmtMonth + 1;
      if (c < 0 || c > 99) wsNextMonth = 1;
      else wsNextMonth = (int) c;
      wsOpenBal = wsCloseBal;
    }
  }
  private void paragraph4310FebruaryLeapAdjust() {
    if (wsNextMonth == 2) {
      if (wsLeapFlag.equals("Y")) wsStmtDay = 29;
      else wsStmtDay = 28;
    }
  }
  private void paragraph4400BuildStatement() {
    wsCloseBal = truncBD(wsCloseBal.add(wsEntryAmt), 11, 2);
    wsStmtLineCount = (int) ((wsStmtLineCount + 1) % pow10(4));
    paragraph4310FebruaryLeapAdjust();
    BigDecimal inter;
    try {
      if (wsCloseBal.compareTo(BigDecimal.ZERO) == 0 || wsRate.compareTo(BigDecimal.ZERO) == 0) {
        inter = BigDecimal.ZERO;
      } else {
        inter = wsCloseBal.multiply(wsRate).divide(new BigDecimal(12), 10, RoundingMode.DOWN);
      }
      inter = truncBD(inter, 9, 2);
      BigDecimal absLim = BigDecimal.TEN.pow(9);
      if (inter.abs().compareTo(absLim) >= 0) throw new ArithmeticException();
      wsInterest = inter;
    } catch (Exception e) { wsInterest = BigDecimal.ZERO; }
    wsCloseBal = truncBD(wsCloseBal.add(wsInterest), 11, 2);
    auditRecord = padN("", 200);
    String monthS = fmt2(wsStmtMonth);
    String built = "STMT " + wsKey + " MTH=" + monthS;
    auditRecord = padN(built, 200);
    auditLines.add(trimRight(auditRecord));
    wsAuditStatus = "00";
    if (!wsAuditStatus.equals("00")) cobDisplay("STMT AUDIT FAILURE " + wsAuditStatus);
  }
  private void paragraph5000WriteResult() {
    wsLine = padN("", 200);
    outRecord = padN("", 200);
    String built = wsKey + "|" + wsBranch + "|" + wsEdgeCode;
    outRecord = padN(built, 200);
    wsLine = outRecord;
    outLines.add(trimRight(outRecord));
    wsOutStatus = "00";
    if (!wsOutStatus.equals("00")) {
      cobDisplay("WRITE FAILURE " + wsOutStatus);
      returnCode = 12;
    } else {
      wsAcceptCount = (wsAcceptCount + 1) % pow10(9);
      wsCycleTotal = truncBD(wsCycleTotal.add(wsEntryAmt), 11, 2);
    }
    auditRecord = outRecord;
    auditLines.add(trimRight(auditRecord));
    wsAuditStatus = "00";
    if (!wsAuditStatus.equals("00")) cobDisplay("AUDIT WRITE FAILURE " + wsAuditStatus);
  }
  private void paragraph6000RejectRecord() {
    wsRejectCount = (wsRejectCount + 1) % pow10(9);
    wsLine = padN("", 200);
    outRecord = padN("", 200);
    String built = "REJECT:" + wsKey + "|" + wsEdgeCode;
    outRecord = padN(built, 200);
    wsLine = outRecord;
    outLines.add(trimRight(outRecord));
    wsOutStatus = "00";
    if (!wsOutStatus.equals("00")) cobDisplay("REJECT WRITE FAILURE " + wsOutStatus);
  }
  private void paragraph7000Finalize() throws Exception {
    paragraph7100CloseFiles();
    paragraph7200WriteTrailer();
    paragraph7300ReportCounts();
    if (wsRejectCount > 0) cobDisplay("REJECTS PRESENT: " + fmt9(wsRejectCount, 10));
    returnCode = 0;
  }
  private void paragraph7100CloseFiles() throws Exception {
    wsInStatus = "00";
    try {
      StringBuilder sb = new StringBuilder();
      for (String l : outLines) { sb.append(l).append('\n'); }
      if (outPath != null) Files.write(Path.of(outPath), sb.toString().getBytes(StandardCharsets.ISO_8859_1));
      wsOutStatus = "00";
    } catch (Exception e) { wsOutStatus = "30"; }
    if (!wsOutStatus.equals("00")) cobDisplay("CLOSE OUTPUT ISSUE " + wsOutStatus);
    try {
      StringBuilder sb2 = new StringBuilder();
      for (String l : auditLines) { sb2.append(l).append('\n'); }
      if (auditPath != null) Files.write(Path.of(auditPath), sb2.toString().getBytes(StandardCharsets.ISO_8859_1));
      wsAuditStatus = "00";
    } catch (Exception e) { wsAuditStatus = "30"; }
    if (!wsInStatus.equals("00")) cobDisplay("CLOSE INPUT ISSUE " + wsInStatus);
    if (!wsAuditStatus.equals("00")) cobDisplay("CLOSE AUDIT ISSUE " + wsAuditStatus);
    if (!wsInStatus.equals("00")) { }
  }
  private void paragraph7200WriteTrailer() {
    outRecord = padN("", 200);
    wsLine = padN("", 200);
    wsCountDisp = wsRecordCount % pow10(9);
    String cntS = fmt9(wsCountDisp, 9);
    String built = "TRAILER COUNT=" + cntS;
    outRecord = padN(built, 200);
    wsLine = outRecord;
    wsPacked = truncBD(wsCycleTotal, 9, 2);
    wsZoned = "+" + fmt9(0, 7) + ".00";
    try {
      BigDecimal p = wsPacked.setScale(2, RoundingMode.DOWN);
      boolean neg = p.signum() < 0;
      BigDecimal a = p.abs();
      long intPart = a.toBigInteger().longValue();
      int frac = a.remainder(BigDecimal.ONE).multiply(new BigDecimal(100)).intValue();
      String is = fmt9(intPart, 7);
      String fs = fmt9(frac, 2);
      wsZoned = (neg ? "-" : "+") + is + "." + fs;
    } catch (Exception e) { }
    wsReportTotal = truncBD(wsReportTotal.add(wsDebits), 11, 2);
    wsReportTotal = truncBD(wsReportTotal.subtract(wsCredits), 11, 2);
    try {
      BigDecimal d = wsDebits.subtract(wsCredits);
      d = truncBD(d, 11, 2);
      if (d.abs().compareTo(BigDecimal.TEN.pow(11)) >= 0) throw new ArithmeticException();
      wsDelta = d;
    } catch (Exception e) { wsDelta = BigDecimal.ZERO; }
  }
  private void paragraph7300ReportCounts() {
    cobDisplay("READ: " + fmt9(wsRecordCount, 10));
    cobDisplay("ACCEPTED: " + fmt9(wsAcceptCount, 10));
    cobDisplay("REJECTED: " + fmt9(wsRejectCount, 10));
    cobDisplay("RETURNS: " + fmt9(wsReturnCount, 10));
    cobDisplay("OUT-OF-BALANCE: " + fmt9(wsOutOfBalance, 10));
  }
  private void paragraph8000CallDateUtility() {
    lsDate = wsNewDate;
    int contentBasis = wsDayBasis;
    lsBasis = contentBasis;
    utility0000Main();
    if (returnCode != 0) {
      String rcS = fmt9(returnCode, 9);
      boolean neg = returnCode < 0;
      String disp;
      if (neg) disp = "-" + fmt9(Math.abs((long) returnCode), 9);
      else disp = rcS;
      cobDisplay("DATE UTILITY RC=" + disp);
      wsEdgeCode = padN("DATEUT-FAIL", 24);
    }
    if (wsLeapFlag.equals("Y")) wsDayBasis = 366;
    else wsDayBasis = 365;
  }
  private void paragraph9000AbendHandler() {
    cobDisplay("FATAL ERROR " + wsEdgeCode);
    returnCode = 16;
  }
  private void utility0000Main() {
    utility1000SplitDate();
    utility2000CheckLeapYear();
    utility3000ValidateDate();
    utility4000NormalizeBasis();
    utility5000ComputeDayCount();
  }
  private void utility1000SplitDate() {
    String s = fmt8(lsDate);
    uWsYear = Integer.parseInt(s.substring(0, 4));
    uWsMonth = Integer.parseInt(s.substring(4, 6));
    uWsDay = Integer.parseInt(s.substring(6, 8));
    if (uWsMonth < 1 || uWsMonth > 12) uWsValidFlag = "N";
    if (uWsDay < 1 || uWsDay > 31) uWsValidFlag = "N";
  }
  private void utility2000CheckLeapYear() {
    int rem = uWsYear % 4;
    if (rem == 0) uWsLeap = "Y"; else uWsLeap = "N";
    uWsQuotient = uWsYear / 100;
    uWsRemainder = uWsYear % 100;
    if (uWsRemainder == 0) uWsLeap = "N";
    uWsQuotient = uWsYear / 400;
    uWsRemainder = uWsYear % 400;
    if (uWsRemainder == 0) uWsLeap = "Y";
  }
  private void utility3000ValidateDate() {
    if (uWsMonth >= 1 && uWsMonth <= 12) {
      int ml = uWsMdays[uWsMonth - 1];
      if (uWsDay > ml) {
        if (!(uWsMonth == 2 && uWsLeap.equals("Y") && uWsDay == 29)) uWsValidFlag = "N";
      }
    }
    if (!uWsValidFlag.equals("Y")) returnCode = 4;
    else returnCode = 0;
  }
  private void utility4000NormalizeBasis() {
    uWsBasisWork = lsBasis;
    if (uWsBasisWork == 360) { }
    else if (uWsBasisWork == 365) { }
    else if (uWsBasisWork == 366) { }
    else uWsBasisWork = 365;
    if (uWsLeap.equals("Y")) uWsBasisWork = 366;
  }
  private void utility5000ComputeDayCount() {
    uWsDayCount = 0;
    uWsIdx = 1;
    while (!(uWsIdx >= uWsMonth)) {
      if (uWsIdx >= 1 && uWsIdx <= 12) uWsDayCount = uWsDayCount + uWsMdays[uWsIdx - 1];
      uWsIdx = uWsIdx + 1;
    }
    uWsDayCount = uWsDayCount + uWsDay;
    if (uWsMonth > 2 && uWsLeap.equals("Y")) uWsDayCount = uWsDayCount + 1;
    uWsQty = uWsDayCount;
  }
}

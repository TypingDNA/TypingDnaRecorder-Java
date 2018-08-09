package com.typingdna;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Once you instantiate TypingDNA class in your project, make sure you call TypingDNA.keyReleased(e); TypingDNA.keyPressed(e);
 * from keyListeners attached to the targets you want to record typing from. Please see example.java
 */

//DO NOT MODIFY
public class TypingDNARecorder {
    public static boolean mobile = false;
    public static int maxHistoryLength = 500;
    public static boolean replaceMissingKeys = true;
    public static int replaceMissingKeysPerc = 7;
    public static boolean recording = true;
    public static boolean diagramRecording = true;
    public static final double version = 2.14; // (without MOUSE tracking and without special keys)
    
    private static final int flags = 1; // JAVA version has flag=1
    private static final int maxSeekTime = 1500;
    private static final int maxPressTime = 300;
    private static final int[] keyCodes = new int[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80,
    81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 32, 222, 44, 46, 59, 61, 45, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56,
    57 };
    private static int maxKeyCode = 250;
    private static int defaultHistoryLength = 160;
    private static int[] keyCodesObj = new int[maxKeyCode];
    private static int[] wfk = new int[maxKeyCode];
    private static long[] sti = new long[maxKeyCode];
    private static int[] skt = new int[maxKeyCode];
    private static int[] dwfk = new int[maxKeyCode];
    private static long[] dsti = new long[maxKeyCode];
    private static int[] dskt = new int[maxKeyCode];
    private static int[] drkc = new int[maxKeyCode];
    private static long pt1;
    private static int prevKeyCode = 0;
    private static int lastPressedKey = 0;
    private static ArrayList<int[]> historyStack = new ArrayList<int[]>();
    private static ArrayList<int[]> stackDiagram = new ArrayList<int[]>();
    private static int savedMissingAvgValuesHistoryLength = -1;
    private static int savedMissingAvgValuesSeekTime;
    private static int savedMissingAvgValuesPressTime;
    
    
    /**
     * EXAMPLE:
     * String typingPattern = TypingDNA.getTypingPattern(type, length, text, textId, caseSensitive);
     *
     * PARAMS:
     * int type = 0; // 1,2 for diagram pattern (short identical texts - 2 for extended diagram), 0 for any-text typing pattern (random text)
     * int length = 0; // (Optional) the length of the text in the history for which you want the typing pattern, 0 = ignore
     * String text = ""; // (Only for type 1) a typed string that you want the typing pattern for
     * int textId = 0; // (Optional, only for type 1) a personalized id for the typed text, 0 = ignore
     * boolean caseSensitive = false; // (Optional, only for type 1) Used only if you pass a text for type 1
     */
    public static String getTypingPattern(int type, int length, String text, int textId, boolean caseSensitive) {
        if (type == 1) {
            return TypingDNARecorder.getDiagram(false, text, textId, length, caseSensitive);
        } else if (type == 2) {
            return TypingDNARecorder.getDiagram(true, text, textId, length, caseSensitive);
        } else {
            return TypingDNARecorder.get(length);
        }
    }
    
    public static String getTypingPattern(int type, int length, String text, int textId) {
        if (type == 1) {
            return TypingDNARecorder.getDiagram(false, text, textId, length, false);
        } else if (type == 2) {
            return TypingDNARecorder.getDiagram(true, text, textId, length, false);
        } else {
            return TypingDNARecorder.get(length);
        }
    }
    
    /**
     * Resets the history stack of recorded typing events.
     */
    public static void reset() {
        historyStack = new ArrayList<int[]>();
        stackDiagram = new ArrayList<int[]>();
    }
    
    /**
     * Automatically called at initialization. It starts the recording of typing events.
     * You only have to call .start() to resume recording after a .stop()
     */
    public static void start() {
        recording = true;
        diagramRecording = true;
    }
    
    /**
     * Ends the recording of further typing events.
     */
    public static void stop() {
        recording = false;
        diagramRecording = false;
    }
    
    public TypingDNARecorder() {
        initialize();
    }
    
    public static void initialize() {
        for (int i = 0; i < keyCodes.length; i++) {
            keyCodesObj[(int) keyCodes[i]] = 1;
        }
        pt1 = getTime();
        reset();
        start();
    }
    
    public static void keyPressed(int keyCode, char keyChar, boolean modifiers) {
        long t0 = pt1;
        pt1 = getTime();
        int seekTotal = (int) (pt1 - t0);
        long startTime = pt1;
        if(keyCode >= maxKeyCode) {
            return;
        }
        if (recording == true && !modifiers) {
            if (keyCodesObj[keyCode] == 1) {
                wfk[keyCode] = 1;
                skt[keyCode] = seekTotal;
                sti[keyCode] = startTime;
            }
        }
        if (diagramRecording == true && (Character.isDefined(keyChar))) {
            lastPressedKey = keyCode;
            dwfk[keyCode] = 1;
            dskt[keyCode] = seekTotal;
            dsti[keyCode] = startTime;
            drkc[keyCode] = keyChar;
        }
    }
    
    public static void keyTyped(char keyChar) {
        if (diagramRecording == true && (Character.isDefined(keyChar)) && lastPressedKey < maxKeyCode ) {
            drkc[lastPressedKey] = (int)keyChar;
        }
    }
    
    public static void keyReleased(int keyCode, boolean modifiers) {
        if ((!recording && !diagramRecording) ||  keyCode >= maxKeyCode) {
            return;
        }
        long ut = getTime();
        if (recording == true && !modifiers) {
            if (keyCodesObj[keyCode] == 1) {
                if (wfk[keyCode] == 1) {
                    int pressTime = (int) (ut - sti[keyCode]);
                    int seekTime = skt[keyCode];
                    int[] arr = new int[] { keyCode, seekTime, pressTime, prevKeyCode };
                    historyAdd(arr);
                    prevKeyCode = keyCode;
                    wfk[keyCode] = 0;
                }
            }
        }
        if (diagramRecording == true) {
            if (drkc[keyCode] != 0 && dwfk[keyCode] == 1) {
                int pressTime = (int) (ut - dsti[keyCode]);
                int seekTime = dskt[keyCode];
                int realKeyCode = drkc[keyCode];
                int[] arrD = new int[]{keyCode, seekTime, pressTime, realKeyCode};
                historyAddDiagram(arrD);
            }
            dwfk[keyCode] = 0;
        }
    }
    
    public static String hash32(String str) {
        str = str.toLowerCase();
        return fnv1a_32(str.getBytes()).toString();
    }
    
    // Private functions
    
    private static void historyAdd(int[] arr) {
        historyStack.add(arr);
        if (historyStack.size() > maxHistoryLength) {
            historyStack.remove(0);
        }
    }
    
    private static void historyAddDiagram(int[] arr) {
        stackDiagram.add(arr);
    }
    
    private static Integer[] getSeek(int length) {
        int historyTotalLength = historyStack.size();
        if (length > historyTotalLength) {
            length = historyTotalLength;
        }
        ArrayList<Integer> seekArr = new ArrayList<Integer>();
        for (int i = 1; i <= length; i++) {
            int seekTime = (int) historyStack.get(historyTotalLength - i)[1];
            if (seekTime < maxSeekTime && seekTime > 0) {
                seekArr.add(seekTime);
            }
        }
        Integer[] seekList = seekArr.toArray(new Integer[seekArr.size()]);
        return seekList;
    }
    
    private static Integer[] getPress(int length) {
        int historyTotalLength = historyStack.size();
        if (length > historyTotalLength) {
            length = historyTotalLength;
        }
        ArrayList<Integer> pressArr = new ArrayList<Integer>();
        for (int i = 1; i <= length; i++) {
            int pressTime = (int) historyStack.get(historyTotalLength - i)[2];
            if (pressTime < maxPressTime && pressTime > 0) {
                pressArr.add(pressTime);
            }
        }
        Integer[] pressList = pressArr.toArray(new Integer[pressArr.size()]);
        return pressList;
    }
    
    private static BigInteger fnv1a_32(byte[] data) {
        BigInteger hash = new BigInteger("721b5ad4", 16);
        ;
        for (byte b : data) {
            hash = hash.xor(BigInteger.valueOf((int) b & 0xff));
            hash = hash.multiply(new BigInteger("01000193", 16)).mod(new BigInteger("2").pow(32));
        }
        return hash;
    }
    
    private static String getDiagram(boolean extended, String str, int textId, int tpLength, boolean caseSensitive) {
        String returnArr = "";
        int diagramType = (extended == true) ? 1 : 0;
        int diagramHistoryLength = stackDiagram.size();
        int missingCount = 0;
        int strLength = tpLength;
        if (str.length() > 0) {
            strLength = str.length();
        } else if (strLength > diagramHistoryLength || strLength == 0) {
            strLength = diagramHistoryLength;
        }
        String returnTextId = "0";
        if (textId == 0 && str.length() > 0) {
            returnTextId = hash32(str);
        } else {
            returnTextId = "" + textId;
        }
        String returnArr0 = (mobile ? 1 : 0) + "," + version + "," + flags + "," + diagramType + "," + strLength + ","
        + returnTextId + ",0,-1,-1,0,-1,-1,0,-1,-1,0,-1,-1,0,-1,-1";
        returnArr += returnArr0;
        if (str.length() > 0) {
            String strLower = str.toLowerCase();
            String strUpper = str.toUpperCase();
            ArrayList<Integer> lastFoundPos = new ArrayList<Integer>();
            int lastPos = 0;
            int strUpperCharCode;
            int currentSensitiveCharCode;
            for (int i = 0; i < str.length(); i++) {
                int currentCharCode = (int) str.charAt(i);
                if (!caseSensitive) {
                    strUpperCharCode = (int) strUpper.charAt(i);
                    currentSensitiveCharCode = (strUpperCharCode != currentCharCode) ? strUpperCharCode : (int) strLower.charAt(i);
                } else {
                    currentSensitiveCharCode = currentCharCode;
                }
                int startPos = lastPos;
                int finishPos = diagramHistoryLength;
                boolean found = false;
                while (found == false) {
                    for (int j = startPos; j < finishPos; j++) {
                        int[] arr = stackDiagram.get(j);
                        int charCode = arr[3];
                        if (charCode == currentCharCode || (!caseSensitive && charCode == currentSensitiveCharCode)) {
                            found = true;
                            if (j == lastPos) {
                                lastPos++;
                                lastFoundPos.clear();
                            } else {
                                lastFoundPos.add(j);
                                int len = lastFoundPos.size();
                                if (len > 1 && lastFoundPos.get(len - 1) == lastFoundPos.get(len - 2) + 1) {
                                    lastPos = j + 1;
                                    lastFoundPos.clear();
                                }
                            }
                            int keyCode = arr[0];
                            int seekTime = arr[1];
                            int pressTime = arr[2];
                            if (extended) {
                                returnArr += "|" + charCode + "," + seekTime + "," + pressTime + "," + keyCode;
                            } else {
                                returnArr += "|" + seekTime + "," + pressTime;
                            }
                            break;
                        }
                    }
                    if (found == false) {
                        if (startPos != 0) {
                            startPos = 0;
                            finishPos = lastPos;
                        } else {
                            found = true;
                            if (replaceMissingKeys) {
                                missingCount++;
                                int seekTime, pressTime;
                                if (savedMissingAvgValuesHistoryLength == -1
                                    || savedMissingAvgValuesHistoryLength != diagramHistoryLength) {
                                    Integer[] histSktF = fo(getSeek(200));
                                    Integer[] histPrtF = fo(getPress(200));
                                    seekTime = (int) Math.round(avg(histSktF));
                                    pressTime = (int) Math.round(avg(histPrtF));
                                    savedMissingAvgValuesSeekTime = seekTime;
                                    savedMissingAvgValuesPressTime = pressTime;
                                    savedMissingAvgValuesHistoryLength = diagramHistoryLength;
                                } else {
                                    seekTime = savedMissingAvgValuesSeekTime;
                                    pressTime = savedMissingAvgValuesPressTime;
                                }
                                int missing = 1;
                                if (extended) {
                                    returnArr += "|" + currentCharCode + "," + seekTime + "," + pressTime + ","
                                    + currentCharCode + "," + missing;
                                } else {
                                    returnArr += "|" + seekTime + "," + pressTime + "," + missing;
                                }
                                break;
                            }
                        }
                    }
                }
                if (replaceMissingKeysPerc < missingCount * 100 / strLength) {
                    returnArr = returnArr0;
                    return null;
                }
            }
        } else {
            int startCount = 0;
            if (tpLength > 0) {
                startCount = diagramHistoryLength - tpLength;
            }
            if (startCount < 0) {
                startCount = 0;
            }
            for (int i = startCount; i < diagramHistoryLength; i++) {
                int[] arr = stackDiagram.get(i);
                int keyCode = arr[0];
                int seekTime = arr[1];
                int pressTime = arr[2];
                if (extended) {
                    int charCode = arr[3];
                    returnArr += "|" + charCode + "," + seekTime + "," + pressTime + "," + keyCode;
                } else {
                    returnArr += "|" + seekTime + "," + pressTime;
                }
            }
        }
        return returnArr;
    }
    
    private static String get(int length) {
        int historyTotalLength = historyStack.size();
        if (length == 0) {
            length = defaultHistoryLength;
        }
        if (length > historyTotalLength) {
            length = historyTotalLength;
        }
        Map<Integer, ArrayList<Integer>> historyStackObjSeek = new HashMap<Integer, ArrayList<Integer>>();
        Map<Integer, ArrayList<Integer>> historyStackObjPress = new HashMap<Integer, ArrayList<Integer>>();
        Map<Integer, ArrayList<Integer>> historyStackObjPrev = new HashMap<Integer, ArrayList<Integer>>();
        for (int i = 1; i <= length; i++) {
            int[] arr = historyStack.get(historyTotalLength - i);
            int keyCode = arr[0];
            int seekTime = arr[1];
            int pressTime = arr[2];
            int prevKeyCode = arr[3];
            if (keyCodesObj[keyCode] == 1) {
                if (seekTime <= maxSeekTime) {
                    ArrayList<Integer> sarr = historyStackObjSeek.get(keyCode);
                    if (sarr == null) {
                        sarr = new ArrayList<Integer>();
                    }
                    sarr.add(seekTime);
                    historyStackObjSeek.put(keyCode, sarr);
                    if (prevKeyCode != 0) {
                        if (keyCodesObj[prevKeyCode] == 1) {
                            ArrayList<Integer> poarr = historyStackObjPrev.get(prevKeyCode);
                            if (poarr == null) {
                                poarr = new ArrayList<Integer>();
                            }
                            poarr.add(seekTime);
                            historyStackObjPrev.put(prevKeyCode, poarr);
                        }
                    }
                }
                if (pressTime <= maxPressTime) {
                    ArrayList<Integer> prarr = historyStackObjPress.get(keyCode);
                    if (prarr == null) {
                        prarr = new ArrayList<Integer>();
                    }
                    prarr.add(pressTime);
                    historyStackObjPress.put(keyCode, prarr);
                }
            }
        }
        Map<Integer, ArrayList<Double>> meansArr = new HashMap<Integer, ArrayList<Double>>();
        double zl = 0.0000001;
        int histRev = length;
        Integer[] histSktF = fo(getSeek(length));
        Integer[] histPrtF = fo(getPress(length));
        Double pressHistMean = (double) Math.round(avg(histPrtF));
        if (pressHistMean.isNaN() || pressHistMean.isInfinite()) {
            pressHistMean = 0.0;
        }
        Double seekHistMean = (double) Math.round(avg(histSktF));
        if (seekHistMean.isNaN() || seekHistMean.isInfinite()) {
            seekHistMean = 0.0;
        }
        Double pressHistSD = (double) Math.round(sd(histPrtF));
        if (pressHistSD.isNaN() || pressHistSD.isInfinite()) {
            pressHistSD = 0.0;
        }
        Double seekHistSD = (double) Math.round(sd(histSktF));
        if (seekHistSD.isNaN() || seekHistSD.isInfinite()) {
            seekHistSD = 0.0;
        }
        Double charMeanTime = seekHistMean + pressHistMean;
        Double pressRatio = rd((pressHistMean + zl) / (charMeanTime + zl));
        Double seekToPressRatio = rd((1 - pressRatio) / pressRatio);
        Double pressSDToPressRatio = rd((pressHistSD + zl) / (pressHistMean + zl));
        Double seekSDToPressRatio = rd((seekHistSD + zl) / (pressHistMean + zl));
        int cpm = (int) Math.round(6E4 / (charMeanTime + zl));
        if (charMeanTime == 0) {
            cpm = 0;
        }
        for (int i = 0; i < keyCodes.length; i++) {
            int keyCode = keyCodes[i];
            ArrayList<Integer> sarr = historyStackObjSeek.get(keyCode);
            ArrayList<Integer> prarr = historyStackObjPress.get(keyCode);
            ArrayList<Integer> poarr = historyStackObjPrev.get(keyCode);
            int srev = 0;
            int prrev = 0;
            int porev = 0;
            if (sarr != null) {
                srev = sarr.size();
            }
            if (prarr != null) {
                prrev = prarr.size();
            }
            if (poarr != null) {
                porev = poarr.size();
            }
            int rev = prrev;
            double seekMean = 0.0;
            double pressMean = 0.0;
            double postMean = 0.0;
            double seekSD = 0.0;
            double pressSD = 0.0;
            double postSD = 0.0;
            switch (srev) {
                case 0:
                    break;
                case 1:
                    seekMean = rd((sarr.get(0) + zl) / (seekHistMean + zl));
                    break;
                default:
                    Integer[] newArr = sarr.toArray(new Integer[sarr.size()]);
                    Integer[] arr = fo(newArr);
                    seekMean = rd((avg(arr) + zl) / (seekHistMean + zl));
                    seekSD = rd((sd(arr) + zl) / (seekHistSD + zl));
            }
            switch (prrev) {
                case 0:
                    break;
                case 1:
                    pressMean = rd((prarr.get(0) + zl) / (pressHistMean + zl));
                    break;
                default:
                    Integer[] newArr = prarr.toArray(new Integer[prarr.size()]);
                    Integer[] arr = fo(newArr);
                    pressMean = rd((avg(arr) + zl) / (pressHistMean + zl));
                    pressSD = rd((sd(arr) + zl) / (pressHistSD + zl));
            }
            switch (porev) {
                case 0:
                    break;
                case 1:
                    postMean = rd((poarr.get(0) + zl) / (seekHistMean + zl));
                    break;
                default:
                    Integer[] newArr = poarr.toArray(new Integer[poarr.size()]);
                    Integer[] arr = fo(newArr);
                    postMean = rd((avg(arr) + zl) / (seekHistMean + zl));
                    postSD = rd((sd(arr) + zl) / (seekHistSD + zl));
            }
            ArrayList<Double> varr = new ArrayList<Double>();
            varr.add((double) rev);
            varr.add(seekMean);
            varr.add(pressMean);
            varr.add(postMean);
            varr.add(seekSD);
            varr.add(pressSD);
            varr.add(postSD);
            meansArr.put((Integer) keyCode, varr);
        }
        ArrayList<Object> arr = new ArrayList<Object>();
        arr.add(histRev);
        arr.add(cpm);
        arr.add((int) (double) charMeanTime);
        arr.add(pressRatio);
        arr.add(seekToPressRatio);
        arr.add(pressSDToPressRatio);
        arr.add(seekSDToPressRatio);
        arr.add(pressHistMean);
        arr.add(seekHistMean);
        arr.add(pressHistSD);
        arr.add(seekHistSD);
        for (int c = 0; c <= 6; c++) {
            for (int i = 0; i < keyCodes.length; i++) {
                int keyCode = keyCodes[i];
                ArrayList<Double> varr = new ArrayList<Double>();
                varr = meansArr.get(keyCode);
                double val = varr.get(c);
                if (((Double) (double) (val)).isNaN()) {
                    val = 0.0;
                }
                if (val == 0 && c > 0) {
                    val = 1;
                    arr.add((int) val);
                } else if (c == 0) {
                    arr.add((int) val);
                } else {
                    arr.add((double) val);
                }
            }
        }
        int mobile = 0;
        arr.add(mobile);
        String typingPattern = arr.toString().replaceAll("\\s", "");
        typingPattern = typingPattern.substring(1, typingPattern.length() - 1);
        return typingPattern;
    }
    
    private static long getTime() {
        return System.currentTimeMillis();
    }
    
    private static double rd(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    private static double rd(double value) {
        return rd(value, 4);
    }
    
    private static Integer[] fo(Integer[] arr) {
        int len = (int) arr.length;
        if (len > 1) {
            Arrays.sort(arr);
            double asd = sd(arr);
            double aMean = arr[(int) Math.ceil(len / 2)];
            double multiplier = 2.0;
            double maxVal = aMean + multiplier * asd;
            double minVal = aMean - multiplier * asd;
            if (len < 20) {
                minVal = 0;
            }
            ArrayList<Integer> fVal = new ArrayList<Integer>();
            for (int i = 0; i < len; i++) {
                int tempval = arr[i];
                if (tempval < maxVal && tempval > minVal) {
                    fVal.add(tempval);
                }
            }
            Integer[] newArr = fVal.toArray(new Integer[fVal.size()]);
            return newArr;
        } else {
            return arr;
        }
    }
    
    private static Double avg(Integer[] arr) {
        int len = (int) arr.length;
        if (len > 0) {
            Double sum = 0.0;
            for (int i = 0; i < len; i++) {
                sum += arr[i];
            }
            return rd(sum / ((double) len));
        } else {
            return 0.0;
        }
    }
    
    private static double sd(Integer[] arr) {
        int len = (int) arr.length;
        if (len < 2) {
            return 0.0;
        } else {
            double sumVS = 0;
            double mean = avg(arr);
            for (int i = 0; i < len; i++) {
                double numd = (double) arr[i] - mean;
                sumVS += numd * numd;
            }
            return Math.sqrt(sumVS / ((double) len));
        }
    }
}


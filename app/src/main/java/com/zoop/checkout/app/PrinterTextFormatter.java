
package com.zoop.checkout.app;
/**
 * Created by rodrigo on 7/9/15.
 */
public class PrinterTextFormatter {
    int printerColumns;
    StringBuffer sb;

    public PrinterTextFormatter(int pPrinterColumns, StringBuffer psb) {
        printerColumns = pPrinterColumns;
        sb = psb;
    }

    public void addLineLeftAligned(String text) {
        sb.append(text).append('\n');
    }

    /*public void addLineRightAligned(String text) {
        sb.append(com.zoop.commons.Extras.lPadCharToTotalLength(text, ' ', printerColumns)).append('\n');
    }*/

    public void addLineCenterAligned(String text) {
        String sTemp = "";
        int iLeftPadding;
        int iCharsToFill = printerColumns - text.length();
        if (0 == (iCharsToFill%2)) {
            iLeftPadding = iCharsToFill/2;
        }
        else {
            iLeftPadding = (iCharsToFill/2)+1;
        }

        sb.append(com.zoop.zoopandroidsdk.commons.Extras.lPadCharToTotalLength(text, ' ', iLeftPadding+text.length()));
        sb.append('\n');
    }

    public void addLineLeftAndRightText(String sLeft, String sRight) {
        int totalColumnsUsed = sLeft.length() + sRight.length();
        if (totalColumnsUsed <= printerColumns) {
            sb.append(com.zoop.zoopandroidsdk.commons.Extras.rPadCharToTotalLength(sLeft, ' ', sLeft.length()+(printerColumns-totalColumnsUsed) ));
            sb.append(sRight);
        }
        else { // WONT fit the line. sLeft + sRight > line size (columns)
            // Even.... subtract equal pieces
            if (0 == totalColumnsUsed%2) {

            }
            else { // Left has priority

            }
        }
        sb.append('\n');
    }


    public void printSeparatorLine() {
        for (Integer i = 0; i < printerColumns; i++) {
            sb.append('-');
        }
        sb.append('\n');
    }

    public void feedLine() {
        sb.append('\n');
    }

    public StringBuffer getStringBuffer() {
        return sb;
    }

}

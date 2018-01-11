/*
 * MIT License
 *
 * Copyright (c) 2018 Dimitar Zabaznoski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mk.webfactory.dz.maskededittext;

final class MaskUtil {

    /**
     * @param rawInput Input text
     * @param mask Mask to apply
     * @return Masked text
     */
    static String setMask(final String rawInput, final Mask mask) {
        StringBuilder stringBuilder = new StringBuilder();
        int inputPos;
        int maskPos;
        int lastIndexOfMaskChar;
        for (inputPos = 0, maskPos = 0;
                inputPos < rawInput.length();
                inputPos++, maskPos = lastIndexOfMaskChar + 1) {
            lastIndexOfMaskChar = mask.mask.indexOf(mask.maskCharacter, maskPos);
            if (lastIndexOfMaskChar != -1) {
                stringBuilder.append(mask.mask.substring(maskPos, lastIndexOfMaskChar));
                stringBuilder.append(rawInput.charAt(inputPos));
            } else {
                stringBuilder.append(mask.mask.substring(maskPos));
                stringBuilder.append(rawInput.substring(inputPos));
                inputPos = rawInput.length();
                maskPos = mask.mask.length();
                break;
            }
        }
        lastIndexOfMaskChar = mask.mask.indexOf(mask.maskCharacter, maskPos);
        stringBuilder.append(mask.mask.substring(maskPos,
                lastIndexOfMaskChar != -1 ? lastIndexOfMaskChar : mask.mask.length()));
        if (mask.enforceMaskLength && stringBuilder.length() > mask.mask.length()) {
            stringBuilder.delete(mask.mask.length(), stringBuilder.length());
        }
        return stringBuilder.toString();
    }

    /**
     * Removes the non masked characters from the masked input and returns raw text.
     *
     * @return Raw text not respecting initial input size
     */
    static String stripMask(final String maskedInput, final Mask mask) {
        final char maskCharacter = mask.maskCharacter;
        final String maskString = mask.mask;
        StringBuilder stringBuilder = new StringBuilder();
        int i;
        for (i = 0; i < maskedInput.length() && i < maskString.length(); i++) {
            if (maskString.charAt(i) == maskCharacter) {
                stringBuilder.append(maskedInput.charAt(i));
            }
        }
        if (i < maskedInput.length()) {
            stringBuilder.append(maskedInput.substring(i));
        }
        return stringBuilder.toString();
    }

    /**
     * Re-masks a text that has been changed but only at one place with 0 or more characters.
     * <br />Valid example: addition/removal of one or more characters at index n;
     * <br />Valid example: no change made;
     * <br />Invalid example: change at index n and another change at index m;
     *
     * <p><pre>
     *  ├──────sub1──────┼────change────┼S┼───sub2───┤
     * </pre>
     * {@code Where sub1 + sub2 = prevTextMasked; and S is selection index;}
     *
     * @param prevTextMasked Masked text before the change
     * @param nextTextWithChanges The prevTextMasked with a potential one place change
     * @return Masked text with the change and cursor index (selection)
     */
    static TextSelectionWrapper reMaskSubsequentInput(String prevTextMasked, String nextTextWithChanges, Mask mask) {
        final int lenPrev = prevTextMasked.length();
        final int lenAfter = nextTextWithChanges.length();
        String sub1, change, sub2;
        {
            int i = 0;
            while (i < lenPrev && i < lenAfter && prevTextMasked.charAt(i) == nextTextWithChanges.charAt(i)) { i++; }
            sub1 = nextTextWithChanges.substring(0, i);
        }
        {
            int kPrev = lenPrev;
            int kAfter = lenAfter;
            while (kPrev > sub1.length() && kAfter > sub1.length()
                    && prevTextMasked.charAt(kPrev - 1) == nextTextWithChanges.charAt(kAfter - 1)) {
                kPrev--;
                kAfter--;
            }
            sub2 = nextTextWithChanges.substring(kAfter);
        }
        change = nextTextWithChanges.substring(sub1.length(), lenAfter - sub2.length());

        //one non mask char deletion
        final int deletedChars = lenPrev - sub2.length() - sub1.length();
        int prevMaskCharIndex;
        if (deletedChars == 1
                && mask.mask.length() > sub1.length()
                && mask.mask.charAt(sub1.length()) != mask.maskCharacter
                && -1 != (prevMaskCharIndex = mask.mask.substring(0, sub1.length()).lastIndexOf(mask.maskCharacter))) {
            sub1 = sub1.substring(0, prevMaskCharIndex);
        }

        sub2 = MaskUtil.stripMask(
                sub2, mask.substring(Math.min(mask.mask.length(), prevTextMasked.length() - sub2.length())));

        StringBuilder remaskedTextBuilder = new StringBuilder()
                .append(sub1)
                .append(MaskUtil.setMask(change, mask.substring(Math.min(mask.mask.length(), sub1.length()))));

        final int selection = remaskedTextBuilder.length();

        remaskedTextBuilder.append(MaskUtil.setMask(sub2, mask.substring(Math.min(mask.mask.length(), selection))));
        String remaskedText = remaskedTextBuilder.toString();

        if (mask.enforceMaskLength) {
            remaskedText = remaskedText.substring(0, Math.min(remaskedText.length(), mask.mask.length()));
        }
        return new TextSelectionWrapper(remaskedText, selection);
    }

    /**
     * Checks to see if an input respects a given mask or mask fragment if input is not long enough.
     * <p>If mask length is enforced and input exceeds the mask length, false is returned.
     * <p>
     * Examples:
     * <pre>
     * Given a mask of ###--##
     * Input of "abc" returns false
     * Input of "abc-" returns false
     * Input of "abc--" returns true
     * Input of "abc--a" returns true
     * Input of "abc--ab" returns true
     *
     * Input of "abc--abEXTRA" returns true if
     * mask length is not enforced, false otherwise
     * </pre>
     *
     * @return true if input has the necessary mask characters.
     */
    static boolean isInputMasked(final CharSequence input, final Mask mask) {
        if (mask.enforceMaskLength && input.length() > mask.mask.length()) {
            return false;
        }
        char currentMaskChar;
        for (int i = 0; i < mask.mask.length(); i++) {
            currentMaskChar = mask.mask.charAt(i);
            if (currentMaskChar != mask.maskCharacter) {
                if (i >= input.length() || currentMaskChar != input.charAt(i)) {
                    return false;
                }
            } else if (i >= input.length()) {
                break;
            }
        }
        return true;
    }

    static void ensureMaskContainsMaskCharacter(final String mask, final char maskCharacter) {
        if (!mask.isEmpty() && mask.indexOf(maskCharacter) == -1) {
            throw new IllegalStateException("Mask does not contain maskCharacter: '" + maskCharacter + "'");
        }
    }

    private MaskUtil() {
        throw new AssertionError();
    }
}

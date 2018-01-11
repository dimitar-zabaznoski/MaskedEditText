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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TextOpTest {

    @Test
    public void testSubstringFromEmpty() throws Exception {
        final String empty = "";

        assertEquals("", empty.substring(0, 0));
    }

    @Test
    public void testSubstringFromNToN() throws Exception {
        final String abc = "abc";

        assertEquals("", abc.substring(0, 0));
    }

    @Test
    public void testCharAddition() throws Exception {
        char a = 'a';
        char b = (char) (a + 1);

        assertEquals('b', b);
        assertNotEquals(a, b);
    }

    @Test
    public void testCharDefaultValueInclusion() throws Exception { //Not working
        char[] charArray = new char[3];
        charArray[1] = 'A';

        final String actualString = String.valueOf(charArray);
        final String expectedString = "A";

        assertNotEquals(expectedString, actualString);
    }

    @Test
    public void testCharDefaultValueOmission() throws Exception {
        char[] charArray = new char[3];
        charArray[1] = 'A';

        StringBuilder stringBuilder = new StringBuilder();
        for (final char charValue : charArray) {
            if (charValue != Character.UNASSIGNED) {
                stringBuilder.append(charValue);
            }
        }
        final String actualString = stringBuilder.toString();
        final String expectedString = "A";

        assertEquals(expectedString, actualString);
    }

    @Test
    public void testCharSequenceDefaultValueOmission() throws Exception { //Not working
        CharSequence charSequence = new StringBuilder()
                .append(Character.UNASSIGNED)
                .append('A')
                .append(Character.UNASSIGNED)
                .toString();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < charSequence.length(); i++) {
            if (charSequence.charAt(i) != Character.UNASSIGNED) {
                stringBuilder.append(charSequence.charAt(i));
            }
        }
        final String actualString = stringBuilder.toString();
        final String expectedString = "A";

        assertNotEquals(expectedString, actualString);
    }
}

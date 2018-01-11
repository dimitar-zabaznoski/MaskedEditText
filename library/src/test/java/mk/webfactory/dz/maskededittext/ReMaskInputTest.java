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

import static mk.webfactory.dz.maskededittext.MaskUtil.isInputMasked;
import static mk.webfactory.dz.maskededittext.MaskUtil.reMaskSubsequentInput;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReMaskInputTest {

    @Test
    public void testReMaskMockUserInputMask1() throws Exception {
        final Mask mask1 = new Mask("##-#--#", '#', false);

        assertEquals("AA-B--", reMaskSubsequentInput("AA-B--", "AA-B--", mask1).text);

        TextSelectionWrapper iteration = new TextSelectionWrapper("", 0);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("a"), mask1);
        assertEquals("a", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("b"), mask1);
        assertEquals("ab-", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("c"), mask1);
        assertEquals("ab-c--", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("d"), mask1);
        assertEquals("ab-c--d", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("e"),
                new Mask.Builder(mask1).setEnforceMaskLength(true).build());
        assertEquals("ab-c--d", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("e"), mask1);
        assertEquals("ab-c--de", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("c", ""), mask1);
        assertEquals("ab-d--e", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("e", ""), mask1);
        assertEquals("ab-d--", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("d", ""), mask1);
        assertEquals("ab-", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("1"), mask1);
        assertEquals("ab-1--", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("-1", "1"), mask1);
        assertEquals("a1-", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("a", ""), mask1);
        assertEquals("1", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("4"), mask1);
        assertEquals("14-", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("4-", "234-"), mask1);
        assertEquals("12-3--4", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("1", "0"), mask1);
        assertEquals("02-3--4", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("0", ""), mask1);
        assertEquals("23-4--", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("--", ""), mask1);
        assertEquals("23-4--", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("4--", "4-"), mask1);
        assertEquals("23-", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, "", mask1);
        assertEquals("", iteration.text);

        iteration = new TextSelectionWrapper("12-3--4", 0);
        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("-3--", "XX-"), mask1);
        assertEquals("12-X--X4", iteration.text);

        iteration = new TextSelectionWrapper("12-3--456789+", 0);
        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("A"), mask1);
        assertEquals("12-3--456789+A", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("A", ""), mask1);
        assertEquals("12-3--456789+", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("89", "XXX"), mask1);
        assertEquals("12-3--4567XXX+", iteration.text);
    }

    @Test
    public void testReMaskMockUserInputMask2() throws Exception {
        final Mask mask2 = new Mask("$-##-^", '#', true);

        TextSelectionWrapper iteration = new TextSelectionWrapper("", 0);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text, mask2);
        assertEquals("$-", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("a"), mask2);
        assertEquals("$-a", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.concat("bc"), mask2);
        assertEquals("$-ab-^", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("b", ""), mask2);
        assertEquals("$-a", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("a", ""), mask2);
        assertEquals("$-", iteration.text);

        iteration = reMaskSubsequentInput(iteration.text, iteration.text.replace("-", ""), mask2);
        assertEquals("$-", iteration.text);
    }

    @Test
    public void testIsInputMasked() throws Exception {
        final Mask mask = new Mask("###--##", '#', true);

        assertFalse(isInputMasked("abc", mask));
        assertFalse(isInputMasked("abc-", mask));
        assertFalse(isInputMasked("abc--abEXTRA", mask));

        assertTrue(isInputMasked("abc--abEXTRA", new Mask("###--##", '#', false)));
        assertTrue(isInputMasked("", mask));
        assertTrue(isInputMasked("abc--", mask));
        assertTrue(isInputMasked("abc--a", mask));
        assertTrue(isInputMasked("abc--ab", mask));
    }
}

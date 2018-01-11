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

import static mk.webfactory.dz.maskededittext.MaskUtil.setMask;
import static mk.webfactory.dz.maskededittext.MaskUtil.stripMask;
import static mk.webfactory.dz.maskededittext.SetStripMaskTest.MaskInputResultWrapper.testCase;
import static org.junit.Assert.assertEquals;

public class SetStripMaskTest {

    private final MaskInputResultWrapper[] testCases = MaskInputResultWrapper.arrayOf(
            //Normal examples
            testCase(Mask.from("### ### ###"), "123123123", "123 123 123"),
            testCase(Mask.from("+(###) ## ###"), "00011222", "+(000) 11 222"),
            testCase(Mask.from("---###/-|"), "123", "---123/-|"),
            testCase(Mask.from("###$"), "100", "100$"),
            testCase(Mask.from("$###"), "100", "$100"),

            //Incomplete input
            testCase(Mask.from("###-####-##"), "123", "123-"),

            //Enforcing/not enforcing mask length
            testCase(Mask.from("### ### ###"), "123123123-XXXXX", "123 123 123", "123123123"),
            testCase(new Mask("### ### ###", '#', false), "123123123-XXXXX", "123 123 123-XXXXX"),

            //Different mask character
            testCase(new Mask("AAA AAA", 'A', true), "123123", "123 123"),

            //Use of mask characters in masked text
            testCase(Mask.from("###-###-###"), "123---123", "123-----123"),

            //Use of input characters in mask
            testCase(Mask.from("###-456-###"), "123789", "123-456-789"),

            //No mask
            testCase(new Mask("", '#', false), "123456", "123456"),
            testCase(new Mask("", '#', true), "123456", "", ""),

            //No characters
            testCase(Mask.from("### ### ###"), "", ""),

            //Mask character not in mask
            testCase(new Mask("##-##", 'Y', false), "ABC", "##-##ABC", "ABC"),
            testCase(new Mask("##-##", 'Y', true), "ABC", "##-##", "")
    );

    @Test
    public void testSetMask() throws Exception {
        for (final MaskInputResultWrapper testCase : testCases) {
            assertEquals(testCase.maskedInput, setMask(testCase.input, testCase.mask));
        }
    }

    @Test
    public void testStripMask() throws Exception {
        for (final MaskInputResultWrapper testCase : testCases) {
            assertEquals(testCase.reversedInput, stripMask(testCase.maskedInput, testCase.mask));
        }
    }

    static class MaskInputResultWrapper {

        final Mask mask;
        final String input;
        final String maskedInput;
        final String reversedInput;

        private MaskInputResultWrapper(Mask mask, String input, String maskedInput, String reversedInput) {
            this.mask = mask;
            this.input = input;
            this.maskedInput = maskedInput;
            this.reversedInput = reversedInput;
        }

        static MaskInputResultWrapper testCase(Mask mask, String input, String maskedInput) {
            return new MaskInputResultWrapper(mask, input, maskedInput, input);
        }

        static MaskInputResultWrapper testCase(Mask mask, String input, String maskedInput, String reversedInput) {
            return new MaskInputResultWrapper(mask, input, maskedInput, reversedInput);
        }

        static MaskInputResultWrapper[] arrayOf(MaskInputResultWrapper... items) {
            return items;
        }
    }
}
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

import android.app.Activity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

/**
 * Acceptance criteria:
 * - Configuration change (must persist latest Mask if changed multiple times)
 * - Change mask/maskCharacter/enforceMaskLength parameter(s) at any time
 * - Right/wrong xml attributes (see sample)
 * - Raw input always correct!
 * - Enforce input type (only for # characters)
 * - InputFilter interoperability; esp InputFilter.LengthFilter
 * - Saving/Restoring state
 * - Context only constructor (or null attribute set)
 * - Special cases mask: no mask, non standard mask, all mask, spaces in mask
 * - Special cases text: null txt, long text, pasted text
 */
@RunWith(RobolectricTestRunner.class)
public class MaskedEditTextTest {

    private static final Mask DEFAULT_MASK = new Mask("##-##", '#', true);

    private Activity activity;
    private MaskedEditText maskedEditText;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();
        maskedEditText = new MaskedEditText(activity, null);
    }

    @Test
    public void testSetDefaultMask() throws Exception {
        setMask(DEFAULT_MASK, maskedEditText);

        assertEquals(DEFAULT_MASK.mask, maskedEditText.getMask());
        assertEquals(DEFAULT_MASK.maskCharacter, maskedEditText.getMaskCharacter());
        assertEquals(DEFAULT_MASK.enforceMaskLength, maskedEditText.isMaskLengthEnforced());
    }

    @Test
    public void testMaskInput() throws Exception {
        setMask(DEFAULT_MASK, maskedEditText);
        maskedEditText.setText("12345");

        assertEquals("12-34", maskedEditText.getText().toString());
        assertEquals("1234", maskedEditText.getRawInput());
    }

    @Test
    public void testActivityRecreated() throws Exception {
        setMask(DEFAULT_MASK, maskedEditText);
        maskedEditText.setText("12345");

        activity.recreate();

        assertEquals("12-34", maskedEditText.getText().toString());
        assertEquals("1234", maskedEditText.getRawInput());
    }

    private void setMask(final Mask mask, final MaskedEditText maskedEditText) {
        maskedEditText.setMask(mask.mask, mask.maskCharacter);
        maskedEditText.setEnforceMaskLength(mask.enforceMaskLength);
    }
}
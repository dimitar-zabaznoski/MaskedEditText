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

import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.lang.ref.WeakReference;

import static mk.webfactory.dz.maskededittext.MaskUtil.reMaskSubsequentInput;

/**
 * TextWatcher that applies a mask on the user input.
 * The mask character is used to define an input char placeholder.
 * Default mask character is '#'.
 * Mind that regex is not supported.
 * <p>
 * Examples:
 * <pre>
 * Card   ####-####-####-####
 * Phone  +### (##) ###-###
 * Date   ##/##/####
 * </pre>
 */
final class MaskEnforcingTextWatcher implements TextWatcher {

    private static final String TAG = "MaskTextWatcher";

    private final Mask mask;
    private WeakReference<EditText> editTextWeakReference;
    private String currentTextMasked = "";

    /**
     * TextWatcher that applies a mask on the user input.
     *
     * @param editText The {@link EditText} this watcher is added to. Unfortunately we're not the only ones trying to
     * modify the user input and the changes overlap.
     * @param mask Mask to be applied on the input.
     * @param maskCharacter Overridden mask character.
     * @param enforceMaskLength If set to true trims excess text outside of mask.
     */
    MaskEnforcingTextWatcher(EditText editText, String mask, char maskCharacter, boolean enforceMaskLength) {
        this.mask = new Mask(mask, maskCharacter, enforceMaskLength);
        editTextWeakReference = new WeakReference<>(editText);
    }

    /**
     * For a mask of '{@code ##-##}' and input of '{@code ab-cd}' this method returns '{@code abcd}'
     *
     * @return Raw user input without mask characters
     */
    @NonNull public String getRawInput() {
        return MaskUtil.stripMask(currentTextMasked, mask);
    }

    @Override
    public void beforeTextChanged(CharSequence beforeText, int start, int deletedCount, int addedAfter) {}

    @Override
    public void onTextChanged(final CharSequence afterText, int start, int deletedBefore, int addedCount) {
        //except for afterText the other parameters are unreliable
    }

    @Override
    public void afterTextChanged(Editable editable) {
        final String afterText = editable.toString();
        if (MaskUtil.isInputMasked(afterText, mask)) {
            currentTextMasked = afterText;
            return;
        }

        TextSelectionWrapper textSelectionWrapper = reMaskSubsequentInput(currentTextMasked, afterText, mask);
        currentTextMasked = textSelectionWrapper.text;

        EditText editText = editTextWeakReference.get();
        if (editText != null) {
            editText.setText(textSelectionWrapper.text);
            editText.setSelection(textSelectionWrapper.selection);
        }
    }
}
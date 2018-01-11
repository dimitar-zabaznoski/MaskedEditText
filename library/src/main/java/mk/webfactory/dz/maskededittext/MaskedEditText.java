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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import static mk.webfactory.dz.maskededittext.Mask.DEFAULT_MASK_CHARACTER;
import static mk.webfactory.dz.maskededittext.Mask.DEFAULT_MASK_STRING;

/**
 * EditText that applies a mask on the user input.
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
public class MaskedEditText extends AppCompatEditText {

    static final String TAG = "MaskedEditText";

    private String maskString;
    private char maskCharacter;
    private boolean enforceMaskLength;
    private MaskEnforcingTextWatcher maskEnforcingTextWatcher;

    public MaskedEditText(Context context) {
        this(context, null);
    }

    public MaskedEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public MaskedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MaskedEditText, 0, 0);
        try {
            String maskCharacterString = a.getString(R.styleable.MaskedEditText_maskededittext_maskCharacter);
            if (maskCharacterString == null) {
                maskCharacter = DEFAULT_MASK_CHARACTER;
            } else if (maskCharacterString.length() == 1) {
                maskCharacter = maskCharacterString.charAt(0);
            } else {
                throw new IllegalArgumentException(TAG + " - Attribute maskCharacter has length greater than 1");
            }
            maskString = a.getString(R.styleable.MaskedEditText_maskededittext_mask);
            enforceMaskLength = a.getBoolean(R.styleable.MaskedEditText_maskededittext_enforceMaskLength,
                    maskString != null);
            if (maskString == null) {
                maskString = DEFAULT_MASK_STRING;
            }
        } finally {
            a.recycle();
        }
        MaskUtil.ensureMaskContainsMaskCharacter(maskString, maskCharacter);
        maskEnforcingTextWatcher = new MaskEnforcingTextWatcher(this, maskString, maskCharacter, enforceMaskLength);
        addTextChangedListener(maskEnforcingTextWatcher);
        setText(getText());
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.maskString = this.maskString;
        ss.maskCharacter = this.maskCharacter;
        ss.enforceMaskLength = this.enforceMaskLength;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.maskString = ss.maskString;
        this.maskCharacter = ss.maskCharacter;
        this.enforceMaskLength = ss.enforceMaskLength;
        invalidateMask();
    }

    /**
     * For a mask of '{@code ##-##}' and input of '{@code ab-cd}' this method returns '{@code abcd}'
     *
     * @return Raw user input without mask characters
     */
    @NonNull
    public String getRawInput() {
        return maskEnforcingTextWatcher.getRawInput();
    }

    public String getMask() {
        return maskString;
    }

    public char getMaskCharacter() {
        return maskCharacter;
    }

    public boolean isMaskLengthEnforced() {
        return enforceMaskLength;
    }

    /**
     * Set a mask to be applied on the user input.
     * <p>Example: {@code ####-####-####-####}
     *
     * @throws IllegalStateException If the mask does not contain a maskCharacter (default '#')
     */
    public void setMask(String maskString) {
        this.maskString = maskString != null ? maskString : "";
        MaskUtil.ensureMaskContainsMaskCharacter(maskString, maskCharacter);
        invalidateMask();
    }

    /**
     * Set a mask to be applied on the user input with custom mask character.
     * <p>Example: {@code $$$$-$$$$-$$$$-$$$$} with mask char '$'
     *
     * @throws IllegalStateException If the mask does not contain the maskCharacter
     */
    public void setMask(String maskString, char maskCharacter) throws IllegalStateException {
        this.maskString = maskString != null ? maskString : "";
        this.maskCharacter = maskCharacter;
        MaskUtil.ensureMaskContainsMaskCharacter(maskString, maskCharacter);
        invalidateMask();
    }

    /**
     * Removes mask making this behave just like regular {@link android.widget.EditText}
     */
    public void removeMask() {
        this.maskString = Mask.DEFAULT_MASK_STRING;
        this.enforceMaskLength = false;
        invalidateMask();
    }

    /**
     * If set to true trims excess text outside of mask.
     * Overrides InputFilter.LengthFilter.
     */
    public void setEnforceMaskLength(boolean enforceMaskLength) {
        this.enforceMaskLength = enforceMaskLength;
        invalidateMask();
    }

    private void invalidateMask() {
        final CharSequence rawInput = maskEnforcingTextWatcher.getRawInput();
        removeTextChangedListener(maskEnforcingTextWatcher);
        setText("");
        maskEnforcingTextWatcher = new MaskEnforcingTextWatcher(this, maskString, maskCharacter, enforceMaskLength);
        addTextChangedListener(maskEnforcingTextWatcher);
        setText(rawInput);
        setSelection(length());
    }

    private static class SavedState extends BaseSavedState {

        String maskString;
        char maskCharacter;
        boolean enforceMaskLength;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            maskString = in.readString();
            maskCharacter = (char) in.readInt();
            enforceMaskLength = in.readByte() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(maskString);
            out.writeInt(maskCharacter);
            out.writeByte((byte) (enforceMaskLength ? 1 : 0));
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

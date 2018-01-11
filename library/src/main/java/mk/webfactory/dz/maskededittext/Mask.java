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

final class Mask {

    static final String DEFAULT_MASK_STRING = "";
    static final char DEFAULT_MASK_CHARACTER = '#';
    static final boolean DEFAULT_ENFORCE_MAX_LEN = true;

    final String mask;
    final char maskCharacter;
    final boolean enforceMaskLength;

    static Mask empty() {
        return new Mask(DEFAULT_MASK_STRING, DEFAULT_MASK_CHARACTER, false);
    }

    static Mask from(String mask) {
        return new Mask(mask, DEFAULT_MASK_CHARACTER, DEFAULT_ENFORCE_MAX_LEN);
    }

    Mask(String mask, char maskCharacter, boolean enforceMaskLength) {
        if (mask == null) {
            throw new IllegalStateException("Mask not defined!");
        }
        this.mask = mask;
        this.maskCharacter = maskCharacter;
        this.enforceMaskLength = enforceMaskLength;
    }

    /** @return New {@link Mask} that's substring of this one. */
    Mask substring(int beginIndexInclusive) {
        return substring(beginIndexInclusive, mask.length());
    }

    /** @return New {@link Mask} that's substring of this one. */
    Mask substring(int beginIndexInclusive, int endIndexExclusive) {
        return new Mask.Builder(this).setMask(mask.substring(beginIndexInclusive, endIndexExclusive)).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Mask mask1 = (Mask) o;

        if (maskCharacter != mask1.maskCharacter) { return false; }
        if (enforceMaskLength != mask1.enforceMaskLength) { return false; }
        return mask.equals(mask1.mask);
    }

    @Override
    public int hashCode() {
        int result = mask.hashCode();
        result = 31 * result + (int) maskCharacter;
        result = 31 * result + (enforceMaskLength ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +
                "mask=" + mask + ", " +
                "maskCharacter=" + maskCharacter + ", " +
                "enforceMaskLength=" + enforceMaskLength + "]";
    }

    static class Builder {

        private String mask;
        private char maskCharacter = DEFAULT_MASK_CHARACTER;
        private boolean enforceMaskLength = DEFAULT_ENFORCE_MAX_LEN;

        Builder() {
        }

        Builder(Mask mask) {
            this.mask = mask.mask;
            this.maskCharacter = mask.maskCharacter;
            this.enforceMaskLength = mask.enforceMaskLength;
        }

        Builder setMask(String mask) {
            this.mask = mask;
            return this;
        }

        Builder setMaskCharacter(char maskCharacter) {
            this.maskCharacter = maskCharacter;
            return this;
        }

        Builder setEnforceMaskLength(boolean enforceMaskLength) {
            this.enforceMaskLength = enforceMaskLength;
            return this;
        }

        Mask build() {
            return new Mask(mask, maskCharacter, enforceMaskLength);
        }
    }
}

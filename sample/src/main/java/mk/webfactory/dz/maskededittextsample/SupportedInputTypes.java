package mk.webfactory.dz.maskededittextsample;

import java.util.ArrayList;
import java.util.List;

class SupportedInputTypes {

    private static List<InputTypeNameWrapper> supportedInputTypes = new ArrayList<>(12);

    static {
        supportedInputTypes.add(new InputTypeNameWrapper(0, "none"));
        supportedInputTypes.add(new InputTypeNameWrapper(14, "date"));
        supportedInputTypes.add(new InputTypeNameWrapper(4, "datetime"));
        supportedInputTypes.add(new InputTypeNameWrapper(2, "number"));
        supportedInputTypes.add(new InputTypeNameWrapper(2002, "numberDecimal"));
        supportedInputTypes.add(new InputTypeNameWrapper(3, "phone"));
        supportedInputTypes.add(new InputTypeNameWrapper(1, "text"));
        supportedInputTypes.add(new InputTypeNameWrapper(1001, "textCapCharacters"));
        supportedInputTypes.add(new InputTypeNameWrapper(4001, "textCapSentences"));
        supportedInputTypes.add(new InputTypeNameWrapper(2001, "textCapWords"));
        supportedInputTypes.add(new InputTypeNameWrapper(21, "textEmailAddress"));
        supportedInputTypes.add(new InputTypeNameWrapper(81, "textPassword"));
    }

    static List<InputTypeNameWrapper> get() {
       return supportedInputTypes;
    }

    static final class InputTypeNameWrapper {

        final int value;
        final String name;

        InputTypeNameWrapper(int value, String name) {
            this.value = value;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

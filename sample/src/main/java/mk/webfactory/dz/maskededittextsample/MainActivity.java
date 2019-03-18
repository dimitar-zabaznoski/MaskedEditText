package mk.webfactory.dz.maskededittextsample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;
import java.util.Arrays;
import mk.webfactory.dz.maskededittext.MaskedEditText;
import mk.webfactory.dz.maskededittextsample.SupportedInputTypes.InputTypeNameWrapper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edit_masked_input)
    MaskedEditText maskedEditText;
    @BindView(R.id.txt_raw_input)
    TextView rawInputTextView;
    @BindView(R.id.edit_mask)
    EditText setMaskEditText;
    @BindView(R.id.edit_mask_character)
    EditText maskCharacterEditText;
    @BindView(R.id.check_enforce_mask_length)
    Switch enforceMaskLengthSwitch;
    @BindView(R.id.ddl_input_type)
    Spinner inputTypeSpinner;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRawInputRunnable = new Runnable() {
        @Override public void run() {
            rawInputTextView.setText(maskedEditText.getRawInput());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayAdapter<InputTypeNameWrapper> adapter = new ArrayAdapter<>(this,
                R.layout.item_spinner_selected,
                SupportedInputTypes.get());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputTypeSpinner.setAdapter(adapter);
        inputTypeSpinner.setSelection(6);

        rawInputTextView.setText(maskedEditText.getRawInput());
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRawInputRunnable);
    }

    @OnTextChanged(R.id.edit_masked_input)
    void onMaskedInputTextChanged(CharSequence maskedText) {
        handler.post(updateRawInputRunnable);
    }

    @OnTextChanged(R.id.edit_mask)
    void onMaskTextChanged(CharSequence newMask) {
        try {
            maskedEditText.setMask(newMask.toString());
            maskCharacterEditText.setError(null);
        } catch (IllegalStateException exp) {
            if (!newMask.toString().isEmpty()) {
                setMaskEditText.setError("Mask must contain mask character: " + maskedEditText.getMaskCharacter());
            }
        }
        maskedEditText.setHint(newMask);
        rawInputTextView.setText(maskedEditText.getRawInput());
    }

    @OnTextChanged(R.id.edit_mask_character)
    void onMaskCharacterTextChanged(CharSequence newMaskCharacter) {
        if (newMaskCharacter.length() > 0) {
            try {
                maskedEditText.setMask(maskedEditText.getMask(), newMaskCharacter.charAt(0));
            } catch (IllegalStateException exp) {
                maskCharacterEditText.setError(getString(R.string.error_char_not_in_mask));
                return;
            }
            rawInputTextView.setText(maskedEditText.getRawInput());
        }
    }

    @OnCheckedChanged(R.id.check_enforce_mask_length)
    void enforceMaskLengthCheckChanged(boolean enforceMaskLength) {
        Log.d("MaskedEditText", "PreviousInputFilterArray: " + Arrays.toString(maskedEditText.getFilters()));
        maskedEditText.setEnforceMaskLength(enforceMaskLength);
        rawInputTextView.setText(maskedEditText.getRawInput());
        Log.d("MaskedEditText", "CurrentInputFilterArray: " + Arrays.toString(maskedEditText.getFilters()));
    }

    @OnItemSelected(R.id.ddl_input_type)
    void onInputTypeItemSelected(AdapterView<?> parent) {
        maskedEditText.setInputType(((InputTypeNameWrapper) parent.getSelectedItem()).value);
    }

    @OnClick(R.id.btn_remove_mask)
    void onRemoveMaskButtonClick() {
        maskedEditText.removeMask();
        invalidateMaskConfigControls();
    }

    @OnClick(R.id.lbl_raw_input)
    void onRawInputLabelClick() {
        rawInputTextView.setText(maskedEditText.getRawInput());
    }

    private void invalidateMaskConfigControls() {
        rawInputTextView.setText(maskedEditText.getRawInput());
        maskedEditText.setHint(maskedEditText.getMask());
        setMaskEditText.setText(maskedEditText.getMask());
        maskCharacterEditText.setText(Character.toString(maskedEditText.getMaskCharacter()));
        enforceMaskLengthSwitch.setChecked(maskedEditText.isMaskLengthEnforced());
    }
}

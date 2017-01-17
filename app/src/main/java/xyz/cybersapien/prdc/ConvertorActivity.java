package xyz.cybersapien.prdc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import xyz.cybersapien.prdc.helpers.ConvertUtils;

public class ConvertorActivity extends AppCompatActivity {

    private static final String LOG_TAG = ConvertorActivity.class.getSimpleName();

    @BindView(R.id.convertor_from_spinner) Spinner fromSpinner;
    @BindView(R.id.convertor_from_edit_text) EditText fromEditText;
    @BindView(R.id.convertor_power_spinner) Spinner powerSpinner;
    @BindViews({R.id.rad_val_display
            , R.id.gray_val_display
            , R.id.rem_val_display
            , R.id.sievert_val_display
            , R.id.roentgen_val_display
            , R.id.coulombPKilo_val_display})
            List<TextView> vals;

    private ArrayAdapter<CharSequence> fromSpinnerAdapter;
    private ArrayAdapter<CharSequence> powerSpinnerAdapter;
    private double multiplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        multiplier = 1d;

        setContentView(R.layout.activity_convertor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        fromSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.rad_units, android.R.layout.simple_spinner_item);
        fromSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromSpinnerAdapter);
        fromSpinner.setOnItemSelectedListener(spinnerListener);

        powerSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.power_ten, android.R.layout.simple_spinner_item);
        powerSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        powerSpinner.setAdapter(powerSpinnerAdapter);
        powerSpinner.setOnItemSelectedListener(powerSpinnerListener);

        fromEditText.addTextChangedListener(fromTextWatcher);
    }

    private TextWatcher fromTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() != 0){
                updateData(Double.valueOf(s.toString()) * multiplier);
            } else {
                updateData(0d);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private AdapterView.OnItemSelectedListener powerSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    multiplier = Math.pow(10, 9);
                    break;
                case 1:
                    multiplier = Math.pow(10, 6);
                    break;
                case 2:
                    multiplier = Math.pow(10, 3);
                    break;
                case 3:
                    multiplier = Math.pow(10, 0);
                    break;
                case 4:
                    multiplier = Math.pow(10, -2);
                    break;
                case 5:
                    multiplier = Math.pow(10, -3);
                    break;
                case 6:
                    multiplier = Math.pow(10, -6);
                    break;
                case 7:
                    multiplier = Math.pow(10, -9);
                    break;
            }

            String qString = fromEditText.getText().toString();
            if (!qString.isEmpty())
                updateData(Double.valueOf(fromEditText.getText().toString()) * multiplier);
            else
                updateData(0d);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    findViewById(R.id.rad_layout).setVisibility(View.GONE);
                    findViewById(R.id.gray_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.rem_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.sievert_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.roentgen_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.coulombPKilo_layout).setVisibility(View.VISIBLE);
                    break;
                case 1:
                    findViewById(R.id.rad_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.gray_layout).setVisibility(View.GONE);
                    findViewById(R.id.rem_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.sievert_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.roentgen_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.coulombPKilo_layout).setVisibility(View.VISIBLE);
                    break;
                case 2:
                    findViewById(R.id.rad_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.gray_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.rem_layout).setVisibility(View.GONE);
                    findViewById(R.id.sievert_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.roentgen_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.coulombPKilo_layout).setVisibility(View.VISIBLE);
                    break;
                case 3:
                    findViewById(R.id.rad_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.gray_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.rem_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.sievert_layout).setVisibility(View.GONE);
                    findViewById(R.id.roentgen_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.coulombPKilo_layout).setVisibility(View.VISIBLE);
                    break;
                case 4:
                    findViewById(R.id.rad_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.gray_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.rem_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.sievert_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.roentgen_layout).setVisibility(View.GONE);
                    findViewById(R.id.coulombPKilo_layout).setVisibility(View.VISIBLE);
                    break;
                case 5:
                    findViewById(R.id.rad_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.gray_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.rem_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.sievert_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.roentgen_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.coulombPKilo_layout).setVisibility(View.GONE);
                    break;
            }
            String qString = fromEditText.getText().toString();
            if (!qString.isEmpty())
                updateData(Double.valueOf(fromEditText.getText().toString()) * multiplier);
            else
                updateData(0d);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void updateData(Double fromVal){
        int selected = fromSpinner.getSelectedItemPosition();

        for (int i = 0;i < 6;i++){
            Double converted = fromVal;
            switch (selected){
                case 0:
                    converted = ConvertUtils.convertRad(fromVal, 100 + i);
                    break;
                case 1:
                    converted = ConvertUtils.convertGray(fromVal, 100 + i);
                    break;
                case 2:
                    converted = ConvertUtils.convertRem(fromVal, 100 + i);
                    break;
                case 3:
                    converted = ConvertUtils.convertSievert(fromVal, 100 + i);
                    break;
                case 4:
                    converted = ConvertUtils.convertRoentgen(fromVal, 100 + i);
                    break;
                case 5:
                    converted = ConvertUtils.convertCoulombPerKg(fromVal, 100 + i);
                    break;
            }
            vals.get(i).setText(getString(R.string.convertor_display_val, converted));
        }
    }
}

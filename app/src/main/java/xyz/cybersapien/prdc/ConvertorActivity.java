package xyz.cybersapien.prdc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import xyz.cybersapien.prdc.helpers.ConvertUtils;
import xyz.cybersapien.prdc.helpers.HelperUtils;

public class ConvertorActivity extends AppCompatActivity {

    private static final String LOG_TAG = ConvertorActivity.class.getSimpleName();

    @BindView(R.id.convertor_from_spinner) Spinner fromSpinner;
    @BindView(R.id.convertor_from_edit_text) EditText fromEditText;
    @BindViews({R.id.rad_val_display
            , R.id.gray_val_display
            , R.id.rem_val_display
            , R.id.sievert_val_display
            , R.id.roentgen_val_display
            , R.id.coulombPKilo_val_display})
            List<TextView> vals;

    ArrayAdapter<CharSequence> fromSpinnerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convertor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        fromSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.rad_units, android.R.layout.simple_spinner_item);
        fromSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromSpinnerAdapter);
        fromSpinner.setOnItemSelectedListener(spinnerListener);
        fromEditText.addTextChangedListener(fromTextWatcher);
    }

    private TextWatcher fromTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() != 0){
                updateData(Double.valueOf(s.toString()));
            } else {
                updateData(0d);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

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
                updateData(Double.valueOf(fromEditText.getText().toString()));
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
            vals.get(i).setText(getString(R.string.display_val, converted));
        }
    }
}
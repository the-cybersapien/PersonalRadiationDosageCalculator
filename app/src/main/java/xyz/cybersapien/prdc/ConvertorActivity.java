package xyz.cybersapien.prdc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ConvertorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convertor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner fromSpinner = (Spinner) findViewById(R.id.convertor_from_spinner);
        ArrayAdapter<CharSequence> fromAdapter = ArrayAdapter
                .createFromResource(this, R.array.rad_units, android.R.layout.simple_spinner_item);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromSpinner.setAdapter(fromAdapter);
    }

}

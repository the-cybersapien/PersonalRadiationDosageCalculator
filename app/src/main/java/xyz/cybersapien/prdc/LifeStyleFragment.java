package xyz.cybersapien.prdc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.concurrent.Callable;

import xyz.cybersapien.prdc.helpers.HelperUtils;

public class LifeStyleFragment extends Fragment {

    private static final String LOG_TAG = LifeStyleFragment.class.getSimpleName();

    private LayoutInflater layoutInflater;
    private MainActivity containerActivity;
    private View containerView;
    private View medicalContainerView;

    private Double totalMedical;

    // Total value and booleans for X-rays
    private double totalXRay;
    private boolean addedxRayData;
    private boolean xRaySkull;
    private boolean xRayChest;
    private boolean xRayTSpine;
    private boolean xRayLSpine;
    private boolean xRayAbdomen;
    private boolean xRayPelvis;
    private boolean xRayDental;
    private boolean xRayLimb;

    // Total value and booleans for Procedures
    private double totalProcedure;
    private boolean addedProcedureData;
    private boolean procedureIVP;
    private boolean procedureBariumSwallow;
    private boolean procedureBariumMeal;
    private boolean procedureBariumFollowUp;
    private boolean procedureBariumEnema;
    private boolean procedureCTHead;
    private boolean procedureCTChest;
    private boolean procedureCTAbdomen;
    private boolean procedureCTPelvis;
    private boolean procedurePTCA;
    private boolean procedureCoronary;
    private boolean procedureMammogram;

    public LifeStyleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        // Initialize xray data
        addedxRayData = false;
        xRaySkull = false;
        xRayChest = false;
        xRayTSpine = false;
        xRayLSpine = false;
        xRayAbdomen = false;
        xRayPelvis = false;
        xRayDental = false;
        xRayLimb = false;

        // Initialize procedure data
        addedProcedureData = false;
        procedureIVP = false;
        procedureBariumSwallow = false;
        procedureBariumMeal = false;
        procedureBariumFollowUp = false;
        procedureBariumEnema = false;
        procedureCTHead = false;
        procedureCTChest = false;
        procedureCTAbdomen = false;
        procedureCTPelvis = false;
        procedurePTCA = false;
        procedureCoronary = false;
        procedureMammogram = false;
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        containerActivity = (MainActivity) getActivity();
        // Inflate the layout for this fragment
        containerView = inflater.inflate(R.layout.fragment_lifestyle, container, false);
        layoutInflater = inflater;
        RadioGroup medicalTestsGroup = (RadioGroup) containerView.findViewById(R.id.medical_choice_radio_group);
        medicalTestsGroup.setOnCheckedChangeListener(medicalTestsChangeListener);

        return containerView;
    }

    private void initializeMedicalTests(){
        CheckBox xrayCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_check_xray);
        xrayCheckBox.setOnCheckedChangeListener(xrayCheckBoxListener);
        CheckBox procedureCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_check_procedure);
        procedureCheckBox.setOnCheckedChangeListener(procedureChangeListener);
    }

    /**
     * Listener for RadioGroup for Medical Tests
     */
    private RadioGroup.OnCheckedChangeListener medicalTestsChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            ViewGroup containmentView = (ViewGroup) containerView.findViewById(R.id.procedures_container);
            switch (checkedId){
                case R.id.medical_choice_yes_button:
                    medicalContainerView = layoutInflater.inflate(R.layout.medical_procedures_card, null);
                    containmentView.addView(medicalContainerView);
                    initializeMedicalTests();
                    break;
                case R.id.medical_choice_no_button:
                    containmentView.removeAllViews();
                    totalMedical = 0d;
                    break;
            }
        }
    };

    /**
     * Listener for X-ray CheckBox in the activity
     * Hides or displays the xray list to the user.
     *
     * Inside there is a CheckedChangeListener for Each sub-item in the Menu
     * These CheckedChangeListener sets a boolean to true or false, depending on the checked state and then Hides/Shows the amount of Radiation from the respective source
     * In the end, the Find Total button checks which all are selected and calculates the total for the x-ray component and updates the universal total
     */
    private CompoundButton.OnCheckedChangeListener xrayCheckBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                medicalContainerView.findViewById(R.id.medical_xray_container).setVisibility(View.VISIBLE);

                CheckBox xRaySkullCheckbox = (CheckBox) medicalContainerView.findViewById(R.id.medical_xray_skull);
                xRaySkullCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView xRaySkullTextView = (TextView) medicalContainerView.findViewById(R.id.medical_xray_skull_rad_count);
                        if (isChecked){
                            xRaySkull = true;
                            xRaySkullTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.XRAY_SKULL));
                            xRaySkullTextView.setVisibility(View.VISIBLE);
                        } else {
                            xRaySkull = false;
                            xRaySkullTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox xRayChestCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_xray_chest);
                xRayChestCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        TextView xRayChestTextView = (TextView)medicalContainerView.findViewById(R.id.medical_xray_chest_rad_count);
                        if (isChecked){
                            xRayChest = true;
                            xRayChestTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.XRAY_CHEST));
                            xRayChestTextView.setVisibility(View.VISIBLE);
                        } else {
                            xRayChest = false;
                            xRayChestTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox xRayThoracicSpineCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_xray_thoracic_spine);
                xRayThoracicSpineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        TextView xRayThoracicSpineView =  (TextView) medicalContainerView.findViewById(R.id.medical_xray_thoracic_rad_count);
                        if (isChecked){
                            xRayTSpine = true;
                            xRayThoracicSpineView.setText(getString(R.string.additional_radiation_display, HelperUtils.XRAY_THORACIC_SPINE));
                            xRayThoracicSpineView.setVisibility(View.VISIBLE);
                        } else {
                            xRayTSpine = false;
                            xRayThoracicSpineView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox xRayLumbarSpineCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_xray_lumbar_spine);
                xRayLumbarSpineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        TextView xRayLumbarSpineView = (TextView) medicalContainerView.findViewById(R.id.medical_xray_lumbar_rad_count);

                        if (isChecked){
                            xRayLSpine = true;
                            xRayLumbarSpineView.setText(getString(R.string.additional_radiation_display, HelperUtils.XRAY_THORACIC_SPINE));
                            xRayLumbarSpineView.setVisibility(View.VISIBLE);
                        } else {
                            xRayLSpine = false;
                            xRayLumbarSpineView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox xRayAbdomenCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_xray_abdomen);
                xRayAbdomenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView xRayAbdomenTextView = (TextView) medicalContainerView.findViewById(R.id.medical_xray_abdomen_rad_count);

                        if (isChecked){
                            xRayAbdomen = false;
                            xRayAbdomenTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.XRAY_ABDOMEN));
                            xRayAbdomenTextView.setVisibility(View.VISIBLE);
                        } else {
                            xRayAbdomen = true;
                            xRayAbdomenTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox xRayPelvisCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_xray_pelvis);
                xRayPelvisCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        TextView xRayPelvisTextView = (TextView) medicalContainerView.findViewById(R.id.medical_xray_pelvis_rad_count);

                        if (isChecked){
                            xRayPelvis = true;
                            xRayPelvisTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.XRAY_PELVIS));
                            xRayPelvisTextView.setVisibility(View.VISIBLE);
                        } else {
                            xRayPelvis = false;
                            xRayPelvisTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox xRayDentalCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_xray_dental_films);
                xRayDentalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView xRayDentalTextView = (TextView) medicalContainerView.findViewById(R.id.medical_xray_dental_rad_count);

                        if (isChecked){
                            xRayDental = true;
                            xRayDentalTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.XRAY_DENTAL));
                            xRayDentalTextView.setVisibility(View.VISIBLE);
                        } else {
                            xRayDental = false;
                            xRayDentalTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox xRayLimbsCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_xray_limbs_joints);
                xRayLimbsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView xRayLimbsTextView = (TextView) medicalContainerView.findViewById(R.id.medical_xray_limbs_rad_count);

                        if (isChecked){
                            xRayLimb = true;
                            xRayLimbsTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.XRAY_LIMBS));
                            xRayLimbsTextView.setVisibility(View.VISIBLE);
                        } else {
                            xRayLimb = false;
                            xRayLimbsTextView.setVisibility(View.GONE);
                        }
                    }
                });

                Button totalXRayButton = (Button) medicalContainerView.findViewById(R.id.medical_xray_total_button);
                totalXRayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double xRayTotal = 0d;
                        if (xRaySkull)
                            xRayTotal += HelperUtils.XRAY_SKULL;
                        if (xRayChest)
                            xRayTotal += HelperUtils.XRAY_CHEST;
                        if (xRayTSpine)
                            xRayTotal += HelperUtils.XRAY_THORACIC_SPINE;
                        if (xRayLSpine)
                            xRayTotal += HelperUtils.XRAY_LUMBAR_SPINE;
                        if (xRayAbdomen)
                            xRayTotal += HelperUtils.XRAY_ABDOMEN;
                        if (xRayPelvis)
                            xRayTotal += HelperUtils.XRAY_PELVIS;
                        if (xRayDental)
                            xRayTotal += HelperUtils.XRAY_DENTAL;
                        if (xRayLimb)
                            xRayTotal += HelperUtils.XRAY_LIMBS;

                        TextView xRayTotalTextView = (TextView) medicalContainerView.findViewById(R.id.xray_total_text_view);
                        xRayTotalTextView.setText(getString(R.string.additional_radiation_display, xRayTotal));

                        if (xRayTotal != totalXRay){
                            containerActivity.addRads(xRayTotal - totalXRay);
                            addedxRayData = true;
                            totalXRay = xRayTotal;
                        }
                    }
                });

                if (addedxRayData){
                    containerActivity.addRads(totalXRay);
                }
            } else {
                medicalContainerView.findViewById(R.id.medical_xray_container).setVisibility(View.GONE);
                if (addedxRayData){
                    containerActivity.addRads(-1 * totalXRay);
                }
            }
        }
    };

    /**
     * Listener for Procedure CheckBox in the activity
     * Hides or displays the xray list to the user.
     *
     * Inside there is a CheckedChangeListener for Each sub-item in the Menu
     * These CheckedChangeListener sets a boolean to true or false, depending on the checked state and then Hides/Shows the amount of Radiation from the respective source
     * In the end, the Find Total button checks which all are selected and calculates the total for the procedure component and updates the universal total
     */
    private CompoundButton.OnCheckedChangeListener procedureChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                medicalContainerView.findViewById(R.id.procedures_container).setVisibility(View.VISIBLE);

                CheckBox ivpCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_IVP);
                ivpCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView IVPTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_IVP_rad_count);
                        if (isChecked){
                            procedureIVP = true;
                            IVPTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_IVP));
                            IVPTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureIVP = false;
                            IVPTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox bariumSwallowCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_barium_swallow);
                bariumSwallowCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView bariumSwallowTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_barium_swallow_rad_count);

                        if (isChecked){
                            procedureBariumSwallow = true;
                            bariumSwallowTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_BARIUM_SWALLOW));
                            bariumSwallowTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureBariumSwallow = false;
                            bariumSwallowTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox bariumMealCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_barium_meal);
                bariumMealCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView bariumMealTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_barium_meal_rad_count);

                        if (isChecked){
                            procedureBariumMeal = true;
                            bariumMealTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_BARIUM_MEAL));
                            bariumMealTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureBariumMeal = false;
                            bariumMealTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox bariumFollowUpCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_barium_follow_up);
                bariumFollowUpCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView bariumFollowUpTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_barium_follow_up_rad_count);

                        if (isChecked){
                            procedureBariumFollowUp = true;
                            bariumFollowUpTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_BARIUM_FOLLOW_UP));
                            bariumFollowUpTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureBariumFollowUp = false;
                            bariumFollowUpTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox bariumEnemaCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_barium_enema);
                bariumEnemaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView bariumEnemaTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_barium_enema_rad_count);

                        if (isChecked){
                            procedureBariumEnema = true;
                            bariumEnemaTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_BARIUM_ENEMA));
                            bariumEnemaTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureBariumEnema = false;
                            bariumEnemaTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox headCTScanCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_CT_head);
                headCTScanCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView headCTScanTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_CT_head_rad_count);

                        if (isChecked){
                            procedureCTHead = true;
                            headCTScanTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_CT_HEAD));
                            headCTScanTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureCTHead = false;
                            headCTScanTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox chestCTScanCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_CT_chest);
                chestCTScanCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView chestCTScanTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_CT_chest_rad_count);

                        if (isChecked){
                            procedureCTChest = true;
                            chestCTScanTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_CT_CHEST));
                            chestCTScanTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureCTChest = false;
                            chestCTScanTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox abdomenCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_CT_abdomen);
                abdomenCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView abdomenTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_CT_abdomen_rad_count);

                        if (isChecked){
                            procedureCTAbdomen = true;
                            abdomenTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_CT_ABDOMEN));
                            abdomenTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureCTAbdomen = false;
                            abdomenTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox pelvisCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_CT_pelvis);
                pelvisCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView pelvisTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_CT_pelvis_rad_count);

                        if (isChecked){
                            procedureCTPelvis = true;
                            pelvisTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_CT_PELVIS));
                            pelvisTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureCTPelvis = false;
                            pelvisTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox PTCACheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_PTCA);
                PTCACheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView PTCATextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_PTCA_rad_count);

                        if (isChecked){
                            procedurePTCA = true;
                            PTCATextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_PTCA));
                            PTCATextView.setVisibility(View.VISIBLE);
                        } else {
                            procedurePTCA = false;
                            PTCATextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox coronaryCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_coronary);
                coronaryCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView coronaryTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_coronary_rad_count);

                        if (isChecked){
                            procedureCoronary = true;
                            coronaryTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_CORONARY));
                            coronaryTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureCoronary = false;
                            coronaryTextView.setVisibility(View.GONE);
                        }
                    }
                });

                CheckBox mammogramCheckBox = (CheckBox) medicalContainerView.findViewById(R.id.medical_procedure_mammogram);
                mammogramCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TextView mammogramTextView = (TextView) medicalContainerView.findViewById(R.id.medical_procedure_mammogram_rad_count);

                        if (isChecked){
                            procedureMammogram = true;
                            mammogramTextView.setText(getString(R.string.additional_radiation_display, HelperUtils.PROCEDURE_MAMMOGRAM));
                            mammogramTextView.setVisibility(View.VISIBLE);
                        } else {
                            procedureMammogram = false;
                            mammogramTextView.setVisibility(View.GONE);
                        }
                    }
                });

                final Button totalProcedureButton = (Button) medicalContainerView.findViewById(R.id.medical_procedure_total_button);
                totalProcedureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double procedureTotal = 0d;
                        if (procedureIVP)
                            procedureTotal += HelperUtils.PROCEDURE_IVP;
                        if (procedureBariumSwallow)
                            procedureTotal += HelperUtils.PROCEDURE_BARIUM_SWALLOW;
                        if (procedureBariumMeal)
                            procedureTotal += HelperUtils.PROCEDURE_BARIUM_MEAL;
                        if (procedureBariumFollowUp)
                            procedureTotal += HelperUtils.PROCEDURE_BARIUM_FOLLOW_UP;
                        if (procedureBariumEnema)
                            procedureTotal += HelperUtils.PROCEDURE_BARIUM_ENEMA;
                        if (procedureCTHead)
                            procedureTotal += HelperUtils.PROCEDURE_CT_HEAD;
                        if (procedureCTChest)
                            procedureTotal += HelperUtils.PROCEDURE_CT_CHEST;
                        if (procedureCTAbdomen)
                            procedureTotal += HelperUtils.PROCEDURE_CT_ABDOMEN;
                        if (procedureCTPelvis)
                            procedureTotal += HelperUtils.PROCEDURE_CT_PELVIS;
                        if (procedurePTCA)
                            procedureTotal += HelperUtils.PROCEDURE_PTCA;
                        if (procedureCoronary)
                            procedureTotal += HelperUtils.PROCEDURE_CORONARY;
                        if (procedureMammogram)
                            procedureTotal += HelperUtils.PROCEDURE_MAMMOGRAM;

                        TextView procedureTotalTextView = (TextView) medicalContainerView.findViewById(R.id.procedure_total_text_view);
                        procedureTotalTextView.setText(getString(R.string.additional_radiation_display, procedureTotal));

                        if (procedureTotal != totalProcedure){
                            containerActivity.addRads(procedureTotal - totalProcedure);
                            addedProcedureData = true;
                            totalProcedure = procedureTotal;
                        }
                    }
                });

                if (addedProcedureData){
                    containerActivity.addRads(totalProcedure);
                }
            } else {

                medicalContainerView.findViewById(R.id.medical_procedure_container).setVisibility(View.GONE);
                if (addedProcedureData){
                    containerActivity.addRads(-1 * totalProcedure);
                }
            }
        }
    };

    private void updateMainActivity(){
        containerActivity.addRads(totalMedical);
    }
}
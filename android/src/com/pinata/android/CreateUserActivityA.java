package com.pinata.android;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.Window;

import com.pinata.client.*;
import com.pinata.client.http.*;
import com.pinata.shared.*;

/**
 * CreateUserActivity UI Handling Routines.
 * @author Christian Gunderman
 */
public class CreateUserActivityA extends Activity {
    /** The gender radiobutton group. */
    private RadioGroup genderRadioGroup;
    /** The next button. */
    private Button nextButton;

    /**
     * Called by Android OS when activity is first started.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar.
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Load window XML layout.
        setContentView(R.layout.create_user_a);

        // Find view buttons and save references to convenient fields.
        this.genderRadioGroup
            = (RadioGroup)this.findViewById(R.id.create_user_gender_radiogroup);
        this.nextButton
            = (Button)this.findViewById(R.id.create_user_next_button);

        // Set radio group clicked listener for enabling button when selected.
        this.genderRadioGroup.setOnCheckedChangeListener(
            new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    CreateUserActivityA.this.nextButton.setEnabled(true);
                }
            });
    }

    /**
     * Called when the next button is clicked. Launch next activity
     * in CreateUser.
     * @param The view that was clicked, the submit button.
     */
    public void onNextButtonClicked(View view) {

        int genderButtonId = genderRadioGroup.getCheckedRadioButtonId();
        User.Gender gender;

        // Check that a gender was specified and get.
        if (genderButtonId == R.id.create_user_male_radiobutton) {
            gender = User.Gender.MALE;
        } else if (genderButtonId == R.id.create_user_female_radiobutton) {
            gender = User.Gender.FEMALE;
        } else {
            // Shouldn't be necessary because the button is disabled.
            Toast.makeText(this, ClientStatus.APP_MUST_CHOOSE_GENDER.message,
                           Toast.LENGTH_SHORT).show();
            return;
        }

        // Store gender in Intent and launch next activity.
        Intent launchActivityIntent = new Intent(this,
                                                 CreateUserActivityB.class);
        launchActivityIntent.putExtra(CreateUserActivityB.EXTRA_GENDER,
                                      gender.toString());
        startActivity(launchActivityIntent);
        finish();
    }
}

package com.pinata.android;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pinata.android.client.*;
import com.pinata.android.client.http.*;
import com.pinata.shared.*;

/**
 * CreateUserActivity UI Handling Routines.
 * @author Christian Gunderman
 */
public class CreateUserActivityB extends Activity {

    /** Specifies an expected Intent extra for passing the gender. */
    public static final String EXTRA_GENDER = "GENDER";

    /** The gender of the new user. */
    private String gender;
    /** The first name EditText. */
    private EditText firstNameEditText;
    /** The last name EditText. */
    private EditText lastNameEditText;
    /** The birthday for this user. */
    private Calendar birthday;
    /** The choose birthday button. */
    private Button birthdayButton;
    /** The next activity button. */
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
        setContentView(R.layout.create_user_b);

        // Unpack values passed from previous activity.
        this.gender = this.getIntent().getStringExtra(EXTRA_GENDER);

        // Check to make sure we have values for all extras.
        if (this.gender == null) {
            Log.e("CreateUserActivityB",
                  "Gender not passed in intent.");
            finish();
        }

        // Find view buttons and save references to convenient fields.
        this.firstNameEditText
            = (EditText)this.findViewById(R.id.create_user_firstname_edittext);
        this.lastNameEditText
            = (EditText)this.findViewById(R.id.create_user_lastname_edittext);
        this.birthdayButton
            = (Button)this.findViewById(R.id.create_user_birthday_button);
        this.nextButton
            = (Button)this.findViewById(R.id.create_user_next_button);

        // Add text watcher to each of the boxes to enable next button when
        // input is given.
        TextWatcher watcher = new TextWatcher() {
                public  void afterTextChanged(Editable s) {
                    tryEnableNext();
                }
                
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }
                
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }
            };
        this.firstNameEditText.addTextChangedListener(watcher);
        this.lastNameEditText.addTextChangedListener(watcher);
    }

    /**
     * Checks to see if the user has provided SOME input for all fields.
     * If so, enables the next button.
     */
    private void tryEnableNext() {
        if (firstNameEditText.length() > 0 &&
            lastNameEditText.length() > 0 &&
            birthday != null) {
            nextButton.setEnabled(true);
        } else {
            nextButton.setEnabled(false);
        }
    }

    /**
     * Called when the next button is clicked. Launches next Activity.
     * create a new user.
     * @param The view that was clicked, the submit button.
     */
    public void onNextButtonClicked(View view) {
        // Check that the user picked a birthday.
        if (CreateUserActivityB.this.birthday == null) {
            Toast.makeText(this, ClientStatus.APP_MUST_ENTER_BIRTHDAY.message,
                           Toast.LENGTH_SHORT).show();
            return;
        }

        // Store values in Intent and launch next activity.
        Intent launchActivityIntent = new Intent(this,
                                                 CreateUserActivityC.class);
        launchActivityIntent.putExtra(CreateUserActivityC.EXTRA_GENDER, gender);
        launchActivityIntent.putExtra(CreateUserActivityC.EXTRA_FIRST_NAME,
                        this.firstNameEditText.getText().toString());
        launchActivityIntent.putExtra(CreateUserActivityC.EXTRA_LAST_NAME,
                        this.lastNameEditText.getText().toString());
        launchActivityIntent.putExtra(CreateUserActivityC.EXTRA_BIRTHDAY,
                        this.birthday.getTimeInMillis());
        
        startActivity(launchActivityIntent);
        finish();
    }


    /**
     * Called when the birthday button is clicked. Makes async server call to
     * create a new user.
     * @param The view that was clicked, the submit button.
     */
    public void onBirthdayButtonClicked(View view) {

        // Create date picker dialog. Save birthday when set.
        final DatePickerDialog dialog = new DatePickerDialog(this,
           new DatePickerDialog.OnDateSetListener() {
               @Override
               public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                   CreateUserActivityB.this.birthday = new GregorianCalendar(year,
                                                                             monthOfYear,
                                                                             dayOfMonth);
                   // Update birthday text.
                   CreateUserActivityB.this.birthdayButton
                       .setTextColor(getResources().getColor(R.color.ui_theme_color_b));
                   CreateUserActivityB.this.birthdayButton.setText(
                       String.format("%d/%d/%s",
                       monthOfYear + 1,
                       dayOfMonth,
                       Integer.toString(year).substring(2, 4)));
               }
           }, 1993, 1, 1);

        // Create dialog close button.
        dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, 
                         getResources().getString(R.string.app_done),
                         new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int which) {
                                 dialog.dismiss();
                                 tryEnableNext();
                             }
                         });

        // Display dialog.
        dialog.show();
    }
}

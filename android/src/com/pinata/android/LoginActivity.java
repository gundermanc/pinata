package com.pinata.android;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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
 * LoginActivity UI Handling Routines.
 * @author Christian Gunderman
 */
public class LoginActivity extends Activity {

    /** The username EditText. */
    private EditText usernameEditText;
    /** The password EditText. */
    private EditText passwordEditText;
    /** Login Button. */
    private Button submitButton;

    /** Authentication session. */
    private UserSession session;

    private AsyncClientOperation backgroundOp;

    /**
     * Called by Android OS when activity is first started.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar.
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Load window XML layout.
        setContentView(R.layout.login);

        // Find view buttons and save references to convenient fields.
        this.usernameEditText
            = (EditText)this.findViewById(R.id.login_username_edittext);
        this.passwordEditText
            = (EditText)this.findViewById(R.id.login_password_edittext);
        this.submitButton
            = (Button)this.findViewById(R.id.login_submit_button);
    }

    /**
     * Called when the submit button is clicked. Makes async server call to
     * log user into server.
     * @param The view that was clicked, the submit button.
     */
    public void onSubmitButtonClicked(View view) {
        backgroundOp = new AsyncLoginRequest();
        backgroundOp.execute();
    }

    /**
     * Cancels operation if back button pressed.
     */
    public void onBackPressed() {
        if (backgroundOp != null) {
            backgroundOp.cancel(true);
        }

        finish();
    }

    /**
     * Defines an async CreateUserSession operation that grabs data from the UI
     * widgets, tells the server to make a new session, and then updates the UI.
     * @author Christian Gunderman
     */
    private class AsyncLoginRequest extends AsyncClientOperation {

        /** The username of the user to create. */
        private String username;
        /** The password for the new user. */
        private String password;

        /**
         * Async operation setup routine. This routine is run on the UI thread
         * before the async operation is run. It grabs all of the necessary info
         * the async operation needs from the UI controls and saves it in this
         * object because the async operation thread cannot access UI objects.
         */
        @Override
        protected void uiThreadBefore() {
            // TODO: start wait cursor animation.
            /* Shows "Waiting.." toast. This is awkward and should be replaced
               by wait cursor.

               Toast.makeText(LoginActivity.this,
                           ClientStatus.APP_WAITING.message,
                           Toast.LENGTH_SHORT).show();
            */

            LoginActivity.this.usernameEditText.setEnabled(false);
            LoginActivity.this.passwordEditText.setEnabled(false);
            LoginActivity.this.submitButton.setEnabled(false);

            // Cache any data from the UI that we need for this request.
            this.username = usernameEditText.getText().toString();
            this.password = passwordEditText.getText().toString();
        }

        /**
         * Async operation routine. This routine is run by a background worker
         * thread, allowing the UI thread to go undisturbed while HTTP requests
         * are being sent. This method cannot access UI elements, so that is
         * done in uiThreadBefore() and cached in this object. Any
         * ClientExceptions thrown from this method are automatically grabbed
         * and unwrapped and dispatched to the uiThreadAfterFailure() method
         * back on the UI thread.
         * This implementation performs a CreateUser request via the User
         * object.
         * @param client A pre-instantiated RestClient object.
         */
        @Override
        protected void backgroundThreadOperation(RestClient client)
            throws ClientException {
            LoginActivity.this.session = UserSession.start(client,
                                                           username,
                                                           password);
        }

        /**
         * This method is run on the UI thread if this object's cancel method
         * is called. It allows for the update of UI elements to inform the user
         * that the requested operation has ceased.
         */
        @Override
        protected void uiThreadOperationCancelled() {
            Toast.makeText(LoginActivity.this,
                           ClientStatus.APP_CANCELLED.message,
                           Toast.LENGTH_SHORT).show();
        }

        /**
         * After the backgroundThreadOperation() function finishes executing,
         * if no exceptions occur, this method is called to signal that the
         * process is assumed to be a success. To signal that the background
         * process was a failure, backgroundThreadOperation() must throw a
         * ClientException.
         */
        @Override
        protected void uiThreadAfterSuccess() {
            /* If we want UI feedback.
               Instead, though, we should probably just open the next window.

            Toast.makeText(LoginActivity.this.getContext(),
                           ClientStatus.OK.message,
                           Toast.LENGTH_SHORT).show();
            */

            // Launch profile activity.
            Intent launchActivityIntent
                = new Intent(LoginActivity.this, ProfileActivity.class);

            Util.bundleSessionWithIntent(session, launchActivityIntent);
            startActivity(launchActivityIntent);
        }

        /**
         * The background operation failure handler method. This method is
         * called automatically if a ClientException was thrown from the
         * backgroundThreadOperation() function. Message contains the
         * ClientException message, and the ApiException message too,
         * if the ClientException has an inner ApiException (exception
         * passed from the server).
         * @param message The UI friendly error message from the client
         * level AND/OR passed back from the server.
         * @param clientStatus A ClientStatus enum relating the current
         * status of the application.
         * @param apiStatus The server's status reply. This value is null
         * unless clientStatus == ClientStatus.API_ERROR.
         */
        @Override
        protected void uiThreadAfterFailure(String message,
                                            ClientStatus clientStatus,
                                            ApiStatus apiStatus) {
            LoginActivity.this.usernameEditText.setEnabled(true);
            LoginActivity.this.passwordEditText.setEnabled(true);
            LoginActivity.this.submitButton.setEnabled(true);

            // Display error message.
            Toast.makeText(LoginActivity.this,
                           message,
                           Toast.LENGTH_SHORT).show();
        }
    }
}

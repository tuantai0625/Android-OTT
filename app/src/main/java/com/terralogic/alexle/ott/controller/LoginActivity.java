package com.terralogic.alexle.ott.controller;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.terralogic.alexle.ott.R;
import com.terralogic.alexle.ott.model.User;
import com.terralogic.alexle.ott.service.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private ViewGroup rootView;
    private ViewGroup header;
    private TextView headerTitle;
    private ImageView image;
    private ImageView tempImage;
    private EditText inputEmail;
    private EditText inputPassword;
    private LinearLayout buttonLogin;
    private LinearLayout buttonRegister;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindViews();
        setupListeners();
        setupLayoutTransition();
    }

    private void bindViews() {
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        rootView = (ViewGroup) findViewById(R.id.root);
        header = (ViewGroup) findViewById(R.id.header);
        headerTitle = (TextView) findViewById(R.id.header_title);
        image = (ImageView) findViewById(R.id.header_image);
        buttonLogin = (LinearLayout) findViewById(R.id.btn_login);
        buttonRegister = (LinearLayout) findViewById(R.id.btn_register);
    }

    private void setupListeners() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidInfo()) {
                    String email = inputEmail.getText().toString();
                    String password = inputPassword.getText().toString();
                    new LoginTask().execute(email, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid info", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        InputMethodManager im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard = new SoftKeyboard(rootView, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeKeyboardHideAnimations();
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeKeyboardShowAnimations();
                    }
                });
            }
        });
    }

    private void setupLayoutTransition() {
        LayoutTransition layoutTransition = rootView.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
    }

    private void makeKeyboardShowAnimations() {
        int animatedTime = 200;
        headerTitle.setVisibility(View.INVISIBLE);
        ObjectAnimator.ofFloat(image, "scaleX", 0.25f).setDuration(animatedTime).start();
        ObjectAnimator.ofFloat(image, "scaleY", 0.25f).setDuration(animatedTime).start();
        ObjectAnimator.ofFloat(image, "translationY", -400f).setDuration(animatedTime).start();
    }

    private void makeKeyboardHideAnimations() {
        int animatedTime = 200;
        headerTitle.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(image, "scaleX", 1f).setDuration(animatedTime).start();
        ObjectAnimator.ofFloat(image, "scaleY", 1f).setDuration(animatedTime).start();
        ObjectAnimator.ofFloat(image, "translationY", 0f).setDuration(animatedTime).start();
    }

    private void addTempImage() {
        tempImage = new ImageView(this);
        tempImage.setImageResource(R.drawable.login_image);
        tempImage.setAdjustViewBounds(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 30);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        header.addView(tempImage);
    }

    private void removeTempImage() {
        header.removeView(tempImage);
    }

    private boolean isValidInfo() {
        return (isRequiredFieldFilled() && isValidEmail());
    }

    private boolean isValidEmail() {
        return Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText()).matches();
    }

    private boolean isRequiredFieldFilled() {
        return (!TextUtils.isEmpty(inputEmail.getText()) && !TextUtils.isEmpty(inputPassword.getText()));
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler("http://10.20.19.73/user");
            httpHandler.addParam("email", strings[0]);
            httpHandler.addParam("password", strings[1]);
            httpHandler.addParam("method", "login");
            return httpHandler.makeServiceCall();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (loginSuccess(s)) {
                try {
                    JSONObject json = new JSONObject(s);
                    user = new User(json.getJSONObject("data"));
                } catch (JSONException ex) {
                    Log.e(this.getClass().getSimpleName(), "JSON mapping error!");
                }
            } else {
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_LONG).show();
            }
        }

        /**
         * Check login state
         */
        private boolean loginSuccess(String message) {
            if (message.equals("Password is wrong, please try again")
                    || message.equals("Account doesn't exist")) {
                return false;
            }
            return true;
        }
    }
}
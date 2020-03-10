package es.studium.adacccornella.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import es.studium.adacccornella.MainActivity;
import es.studium.adacccornella.R;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean isLogin = sharedPref.getBoolean("isLogin", false);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        final Switch swtchRecordar = findViewById(R.id.switch1);
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);

        if (isLogin) {
            Log.println(Log.ASSERT, "Aviso", "Entra en Login con isLogin true");
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
        } else {
            loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
                @Override
                public void onChanged(@Nullable LoginFormState loginFormState) {
                    if (loginFormState == null) {
                        return;
                    }
                    loginButton.setEnabled(loginFormState.isDataValid());
                    if (loginFormState.getUsernameError() != null) {
                        usernameEditText.setError(getString(loginFormState.getUsernameError()));
                    }
                    if (loginFormState.getPasswordError() != null) {
                        passwordEditText.setError(getString(loginFormState.getPasswordError()));
                    }
                }
            });

            loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
                @Override
                public void onChanged(@Nullable LoginResult loginResult) {
                    if (loginResult == null) {
                        return;
                    }
                    if (loginResult.getError() != null) {
                        showLoginFailed(loginResult.getError());
                    }
                    if (loginResult.getSuccess() != null) {
                        updateUiWithUser(loginResult.getSuccess());
                    }
                    setResult(Activity.RESULT_OK);

                    //Complete and destroy login activity once successful
                    finish();
                }
            });

            TextWatcher afterTextChangedListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // ignore
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // ignore
                }

                @Override
                public void afterTextChanged(Editable s) {
                    loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
            };
            usernameEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        loginViewModel.login(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString());
                    }
                    return false;
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            boolean isLogin = sharedPref.getBoolean("isLogin", false);
                            String user = usernameEditText.getText().toString();
                            String pass = passwordEditText.getText().toString();
                            pass = md5(pass);
                            try {
                                // Create URL
                                URL url = new
                                        URL("http://192.168.0.14/adacc/adacc.php?user=" + user + "&pass=" + pass);
                                // Create connection
                                HttpURLConnection myConnection = (HttpURLConnection)
                                        url.openConnection();
                                // Establecer método. Por defecto GET.
                                myConnection.setRequestMethod("GET");

                                if (myConnection.getResponseCode() == 200) {
                                    // Success
                                    InputStream responseBody =
                                            myConnection.getInputStream();
                                    InputStreamReader responseBodyReader =
                                            new InputStreamReader(responseBody, "UTF-8");
                                    BufferedReader bR = new
                                            BufferedReader(responseBodyReader);
                                    String line = "";
                                    StringBuilder responseStrBuilder = new
                                            StringBuilder();
                                    while ((line = bR.readLine()) != null) {
                                        responseStrBuilder.append(line);
                                    }
                                    JSONObject result = new
                                            JSONObject(responseStrBuilder.toString());

                                    String correcto = result.getString("mensaje");

                                    if (user.equals("Laura") & pass.equals("12345678")) {
                                        if (swtchRecordar.isChecked()) {
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString("user", user);
                                            editor.putBoolean("isLogin", true);
                                            editor.commit();
                                        }
                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Inicio de sesion incorrecto", Toast.LENGTH_LONG).show();
                                    }
                                }

                                responseBody.close();
                                responseBodyReader.close();
                                myConnection.disconnect();
                            }
                                else
                            {
                                // Error handling code goes here
                                Log.println(Log.ASSERT, "Error", "Error");
                            }
                        }
                            catch(
                        Exception e)

                        {
                            Log.println(Log.ASSERT, "Excepción", e.getMessage());
                        }
                    }
                });
                    Snackbar.make(v,"Replace with your own action",
                Snackbar.LENGTH_LONG)
                        .

                setAction("Action",null).

                show();

            });
        }}
        }

        public static String md5 (String s){
            final String MD5 = "MD5";
            try {
                // Create MD5 Hash
                MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
                digest.update(s.getBytes());
                byte messageDigest[] = digest.digest();

                // Create Hex String
                StringBuilder hexString = new StringBuilder();
                for (byte aMessageDigest : messageDigest) {
                    String h = Integer.toHexString(0xFF & aMessageDigest);
                    while (h.length() < 2)
                        h = "0" + h;
                    hexString.append(h);
                }
                return hexString.toString();

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        }

        private void updateUiWithUser (LoggedInUserView model){
            String welcome = getString(R.string.bienvenida) + model.getDisplayName();
            // TODO : initiate successful logged in experience
            Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }

        private void showLoginFailed (@StringRes Integer errorString){
            Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
        }

    }

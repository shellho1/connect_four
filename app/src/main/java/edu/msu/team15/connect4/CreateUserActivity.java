package edu.msu.team15.connect4;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class CreateUserActivity extends AppCompatActivity {

    private String user = "";
    private String pass = "";
    private String pass_confirm = "";

    private String getUser() {
        EditText username  = findViewById(R.id.userEditText);
        return username.getText().toString().trim();
    }

    private String getPass() {
        EditText password = findViewById(R.id.passwordEditText);
        return password.getText().toString().trim();
    }

    private String getPass_confirm() {
        EditText password_confirm = findViewById(R.id.password2EditText);
        return password_confirm.getText().toString().trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final Button button = findViewById(R.id.buttonCreateUser);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateUser(view);
            }

        });
    }

    public void onCreateUser(View view) {
        user = getUser();
        pass = getPass();
        pass_confirm = getPass_confirm();

        final View view1 = view;

        if (!pass.equals(pass_confirm)) {
            Toast.makeText(view.getContext(), R.string.password_match_error, Toast.LENGTH_SHORT).show();
        } else if (user.equals("") || pass.equals("")){
            Toast.makeText(view.getContext(), R.string.empty_error, Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Cloud cloud = new Cloud();
                    InputStream stream = cloud.createUser(user,pass);

                    // Test for an error
                    boolean fail = stream == null;
                    if(!fail) {
                        try {

                            XmlPullParser xml = Xml.newPullParser();
                            xml.setInput(stream, "UTF-8");

                            xml.nextTag();      // Advance to first tag
                            xml.require(XmlPullParser.START_TAG, null, "connect4");
                            String status = xml.getAttributeValue(null, "status");
                            if(!status.equals("yes")) {
                                fail = true;
                            }

                        } catch(IOException ex) {
                            fail = true;
                        } catch(XmlPullParserException ex) {
                            fail = true;
                        } finally {
                            try {
                                stream.close();
                            } catch(IOException ex) {
                            }
                        }
                    }

                    final boolean fail1 = fail;
                    view1.post(new Runnable() {

                        @Override
                        public void run() {
                            if(fail1) {
                                Toast.makeText(view1.getContext(),
                                        R.string.create_user_error,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                finish();
                            }
                        }
                    });
                }
            }).start();
        }
    }
}

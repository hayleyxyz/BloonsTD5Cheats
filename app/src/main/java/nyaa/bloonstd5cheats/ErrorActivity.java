package nyaa.bloonstd5cheats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class ErrorActivity extends Activity {

    public static final String EXTRA_ERROR_RES = "error_res";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int errorRes = extras.getInt(EXTRA_ERROR_RES);

            TextView errorDetail = (TextView)findViewById(R.id.errorDetail);
            errorDetail.setText(errorRes);
        }
    }

    public void onRetry(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActity.class);
        startActivity(intent);

        finish();
    }
}

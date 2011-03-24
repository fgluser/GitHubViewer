package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.util.CommonHelper;
import net.flaxia.android.githubviewer.util.Extra;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DashboardActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);
    }

    /**
     * 検索ボタンを押したときの処理
     * 
     * @param view
     */
    public void onSearchButton(View view) {
        Context context = getApplicationContext();
        String q = ((EditText) findViewById(R.id.q)).getText().toString();
        if (CommonHelper.isEmpty(q)) {
            Toast.makeText(context, R.string.search_word_is_empty, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(Extra.Q, q);
        startActivity(intent);
    }

    public void onInformationButton(View view) {
        startActivity(new Intent(getApplicationContext(), InformationActivity.class));
    }
}

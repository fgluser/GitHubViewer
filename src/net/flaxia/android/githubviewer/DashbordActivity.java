package net.flaxia.android.githubviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DashbordActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashbord);
    }
    
    /**
     * 検索ボタンを押したときの処理
     * @param view
     */
    public void onSearchButton(View view){
        Context context = getApplicationContext();
        String q = ((EditText)findViewById(R.id.q)).getText().toString();
        //TODO:検索ワードがブランクのみだった場合も弾く
        if(CommonHelper.isEmpty(q)){
            Toast.makeText(context, R.string.search_word_is_empty, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(SearchActivity.Q, q);
        startActivity(intent);
    }
}

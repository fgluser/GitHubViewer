package net.flaxia.android.githubviewer;

import net.flaxia.android.githubviewer.model.Repositorie;
import android.os.Bundle;
import android.widget.TextView;

public class RepositorieInfoActivity extends BaseAsyncActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositorie_info);
        Repositorie repositorie = (Repositorie) getIntent().getExtras().getSerializable(
                Extra.REPOSITORIE);
        ((TextView) findViewById(R.id.repositorie_name)).setText(repositorie.get("name"));
        ((TextView) findViewById(R.id.owner)).setText(repositorie.get("owner"));
        ((TextView) findViewById(R.id.homepage)).setText(repositorie.get("homepage"));
    }

    @Override
    protected void executeAsyncTask(String... parameters) {
    }
}

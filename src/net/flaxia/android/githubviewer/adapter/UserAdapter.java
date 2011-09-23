
package net.flaxia.android.githubviewer.adapter;

import net.flaxia.android.githubviewer.model.User;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserAdapter extends BaseListAdapter<User> {
    public UserAdapter(final Context context, final int textViewResourceId, final User[] users) {
        super(context, textViewResourceId, users);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);
        ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).get(
                User.USERNAME));

        return view;
    }
}

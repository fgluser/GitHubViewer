
package net.flaxia.android.githubviewer.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

abstract class BaseListAdapter<T> extends BaseAdapter {
    protected final List<T> mObjects;
    protected final LayoutInflater mInflater;
    protected final int mResource;
    protected int mDropDownResource;

    public BaseListAdapter(final Context context, final int textViewResourceId) {
        this(context, textViewResourceId, new ArrayList<T>());
    }

    public BaseListAdapter(final Context context, final int textViewResourceId, final T[] objects) {
        this(context, textViewResourceId, Arrays.asList(objects));
    }

    public BaseListAdapter(final Context context, final int textViewResourceId,
            final List<T> objects) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = mDropDownResource = textViewResourceId;
        mObjects = objects;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public T getItem(final int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        return (null == convertView) ? mInflater.inflate(mResource, null) : convertView;
    }

    @Override
    public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
        final View view = (null == convertView) ? mInflater.inflate(mDropDownResource, parent,
                false)
                : convertView;
        final T item = getItem(position);
        if (item instanceof CharSequence) {
            ((TextView) view).setText((CharSequence) item);
        } else {
            ((TextView) view).setText(item.toString());
        }
        return view;
    }

    public void setDropDownViewResource(final int resource) {
        mDropDownResource = resource;
    }

    public void add(final T object) {
        mObjects.add(object);
        notifyDataSetChanged();
    }

    public void clear() {
        mObjects.clear();
    }
}

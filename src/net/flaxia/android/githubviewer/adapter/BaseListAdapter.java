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
    protected List<T> mObjects;
    protected LayoutInflater mInflater;
    protected int mResource;
    protected int mDropDownResource;

    public BaseListAdapter(Context context, int textViewResourceId) {
        this(context, textViewResourceId, new ArrayList<T>());
    }

    public BaseListAdapter(Context context, int textViewResourceId, T[] objects) {
        this(context, textViewResourceId, Arrays.asList(objects));
    }

    public BaseListAdapter(Context context, int textViewResourceId, List<T> objects) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = mDropDownResource = textViewResourceId;
        mObjects = objects;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public T getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return (null == convertView) ? mInflater.inflate(mResource, null) : convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = (null == convertView) ? mInflater.inflate(mDropDownResource, parent, false)
                : convertView;
        T item = getItem(position);
        if (item instanceof CharSequence) {
            ((TextView) view).setText((CharSequence) item);
        } else {
            ((TextView) view).setText(item.toString());
        }
        return view;
    }

    public void setDropDownViewResource(int resource) {
        mDropDownResource = resource;
    }

    public void add(T object) {
        mObjects.add(object);
        notifyDataSetChanged();
    }
    
    public void clear(){
        mObjects.clear();
    }
}

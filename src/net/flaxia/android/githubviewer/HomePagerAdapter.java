
package net.flaxia.android.githubviewer;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.TitleProvider;

public class HomePagerAdapter extends PagerAdapter implements TitleProvider {
    private String[] mTitles;
    private List<View> mViews;

    public HomePagerAdapter(final String[] titles, final List<View> views) {
        mTitles = titles;
        mViews = views;
    }

    // private String[] titles = new String[] {};
    // private List<View> mView;

    @Override
    public String getTitle(final int position) {
        return mTitles[position];
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public int getItemPosition(final Object object) {
        final int count = getCount();
        for (int i = 0; i < count; i++) {
            if (mViews.get(i).equals(object)) {
                return i;
            }
        }
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final View pager, final int position) {
        final ViewGroup parent = (ViewGroup) mViews.get(position).getParent();
        if (null != parent) {
            parent.removeView(mViews.get(position));
        }
        final View view = mViews.get(position);
        ((ViewPager) pager).addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(final View pager, final int position, final Object view) {
        ((ViewPager) pager).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void finishUpdate(final View view) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(final View view) {
    }

    @Override
    public void restoreState(final Parcelable arg0, final ClassLoader arg1) {
    }
}

package com.catandroid.app.common.ui.fragments.static_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.catandroid.app.R;

public class CostsReference extends Fragment {

	private static final int[] LAYOUTS = { R.layout.reference_build, R.layout.reference_buy,
			R.layout.reference_development };

	private static final int[] NAMES = { R.string.reference_tab1, R.string.reference_tab2, R.string.reference_tab3 };

	private View[] views;

	public CostsReference(){

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//super.onCreate(state);

		//getActivity().setContentView(R.layout.reference);
		getActivity().setTitle(getString(R.string.reference));

		//LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.reference, null, false);

		views = new View[LAYOUTS.length];
		
		for (int i = 0; i < LAYOUTS.length; i++)
			views[i] = inflater.inflate(LAYOUTS[i], null);
		
		ViewPager viewPager = (ViewPager) view.findViewById(R.id.reference);
		viewPager.setAdapter(new ReferenceTabAdapter());
		viewPager.setCurrentItem(1);

		Log.d("myTag", "about to return costs view");
		return view;
	}

	public class ReferenceTabAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return views.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup collection, int position) {
			collection.addView(views[position]);
			return views[position];
		}

		@Override
		public void destroyItem(ViewGroup collection, int position, Object view) {
			collection.removeView((View) view);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(NAMES[position]);
		}
	}
}

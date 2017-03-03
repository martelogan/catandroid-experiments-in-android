package com.catandroid.app.common.ui.fragments.static_fragments;

import com.catandroid.app.common.components.Board;
import com.catandroid.app.common.components.Board.Cards;
import com.catandroid.app.common.components.Resource;
import com.catandroid.app.common.ui.graphics_controllers.TextureManager;
import com.catandroid.app.R;
import com.catandroid.app.CatAndroidApp;
import com.catandroid.app.common.players.Player;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlayerStatus extends Fragment {
	
	private View[] views;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//super.onCreate(state);
		
		getActivity().setTitle(getString(R.string.status));
		
		Board board = ((CatAndroidApp) getActivity().getApplicationContext()).getBoardInstance();
		if (board == null) {
			//finish();
			return null;
		}

		int numPlayers = board.getNumPlayers();

		views = new View[numPlayers];
		
		for (int i = 0; i < numPlayers; i++) {
			views[i] = inflater.inflate(R.layout.status_player, null);
			
			Player player = board.getPlayer(i);

			boolean showAll = player == board.getCurrentPlayer()
					&& player.isHuman()
					|| board.getWinner(((CatAndroidApp) getActivity().getApplicationContext())
							.getAppSettingsInstance()) != null;

			int points;
			if (showAll)
				points = player.getVictoryPoints();
			else
				points = player.getPublicVictoryPoints();

			String message = "\n";

			message += getString(R.string.status_resources) + ": "
					+ player.getNumResources() + "\n";
			message += getString(R.string.status_dev_cards) + ": "
					+ player.getNumDevCards() + "\n";

			message += "\n";

			message += getString(R.string.status_towns) + ": "
					+ player.getNumTowns() + " / " + Player.MAX_TOWNS + "\n";
			message += getString(R.string.status_cities) + ": "
					+ player.getNumCities() + " / " + Player.MAX_CITIES + "\n";
			message += getString(R.string.status_roads) + ": "
					+ player.getNumRoads() + " / " + Player.MAX_ROADS + "\n";

			message += "\n";

			message += getString(R.string.status_road_length) + ": "
					+ player.getRoadLength() + "\n";
			if (player == board.getLongestRoadOwner())
				message += getString(R.string.status_longest_road) + ": "
						+ "2 " + getString(R.string.status_points) + "\n";

			message += getString(R.string.status_army_size) + ": "
					+ player.getArmySize() + "\n";
			if (player == board.getLargestArmyOwner())
				message += getString(R.string.status_largest_army) + ": "
						+ "2 " + getString(R.string.status_points) + "\n";

			message += "\n";

			if (showAll) {
				message += getString(R.string.status_soldier_cards) + ": "
						+ player.getNumDevCardType(Cards.SOLDIER) + "\n"
						+ getString(R.string.status_progress_cards) + ": "
						+ player.getNumDevCardType(Cards.PROGRESS) + "\n"
						+ getString(R.string.status_victory_cards) + ": "
						+ player.getVictoryCards() + "\n\n";
			}

			boolean hasHarbor = false;
			if (player.hasHarbor(null)) {
				message += "3:1 " + getString(R.string.status_trader) + "\n";
				hasHarbor = true;
			}

			for (int j = 0; j < Resource.RESOURCE_TYPES.length; j++) {
				if (player.hasHarbor(Resource.RESOURCE_TYPES[j])) {
					message += getString(Resource
							.toRString(Resource.RESOURCE_TYPES[j]))
							+ " " + getString(R.string.status_trader) + "\n";
					hasHarbor = true;
				}
			}

			if (hasHarbor)
				message += "\n";

			String turn = player.getActionLog();
			if (player == board.getCurrentPlayer() && turn != "")
				message += getString(R.string.status_this_turn) + ":\n" + turn;
			else if (turn != "")
				message += getString(R.string.status_last_turn) + ":\n" + turn;

			TextView text = (TextView) views[i].findViewById(R.id.status_text);
			text.setText(message);

			TextView point = (TextView) views[i].findViewById(R.id.status_points);
			point.setText(getString(R.string.status_victory_points) + ": "
					+ points + " / " + board.getMaxPoints());

			ProgressBar progress = (ProgressBar) views[i].findViewById(R.id.status_progress);
			progress.setMax(board.getMaxPoints());
			progress.setProgress(points);
		}

		final View view = inflater.inflate(R.layout.status, null, false);

		ViewPager viewPager = (ViewPager) view.findViewById(R.id.status);
		viewPager.setAdapter(new StatusTabAdapter());
		viewPager.setCurrentItem(board.getCurrentPlayer().getPlayerNumber());
		
		PagerTitleStrip titleStrip = (PagerTitleStrip) view.findViewById(R.id.status_title_strip);
		titleStrip.setBackgroundColor(TextureManager.darken(TextureManager.getColor(
				CatAndroidApp.getInstance().getBoardInstance().getPlayer(board.getCurrentPlayer().getPlayerNumber()).getColor()), 0.35));
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int position, float offset, int offsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				int color = TextureManager.getColor(CatAndroidApp.getInstance().getBoardInstance().getPlayer(position).getColor());
				color = TextureManager.darken(color, 0.35);
				
				PagerTitleStrip titleStrip = (PagerTitleStrip) view.findViewById(R.id.status_title_strip);
				titleStrip.setBackgroundColor(color);
			}
		});

		return view;

	}

	public class StatusTabAdapter extends PagerAdapter {

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
			return CatAndroidApp.getInstance().getBoardInstance().getPlayer(position).getName();
		}
	}
}

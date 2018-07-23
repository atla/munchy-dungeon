package com.leihwelt.munchyd.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.leihwelt.munchyd.MunchCounterMain;
import com.leihwelt.munchyd.R;
import com.leihwelt.munchyd.list.MunchkinAdapter.Theme;
import com.leihwelt.munchyd.model.Munchkin;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MunchkinAdapter extends BaseAdapter {

	public static enum Theme {
		NORMAL, PRESENTATION
	}

	private static class ViewHolder {
		public TextView nameLabel;
		public TextView level;
		private TextView gearLevel;
		private TextView overallLevel;

		public ViewHolder(View base) {
			this.nameLabel = (TextView) base.findViewById(R.id.player_name);
			this.level = (TextView) base.findViewById(R.id.player_level);
			this.gearLevel = (TextView) base.findViewById(R.id.gear_level);
			this.overallLevel = (TextView) base.findViewById(R.id.overall_level);
		}
	}

	private LayoutInflater inflater;
	private ArrayList<Munchkin> data = new ArrayList<Munchkin>();
	private Theme theme = Theme.NORMAL;

	public MunchkinAdapter(LayoutInflater inflater, Theme theme) {
		this.inflater = inflater;
		this.theme = theme;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int pos) {
		return data.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {

		ViewHolder holder = null;

		if (v == null) {

			if (theme == Theme.NORMAL) {
				v = inflater.inflate(R.layout.munchkin_list_item, null);
			} else {
				v = inflater.inflate(R.layout.munchkin_list_item_dark, null);
			}

			holder = new ViewHolder(v);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		Munchkin mk = (Munchkin) this.getItem(pos);

		holder.nameLabel.setText(mk.getPlayerName());
		holder.level.setText(Integer.toString(mk.getPlayerLevel()));
		holder.gearLevel.setText(Integer.toString(mk.getGearLevel()));
		holder.overallLevel.setText(Integer.toString(mk.getOverallLevel()));

		return v;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

		this.data = createRankedPlayerList();
	}

	private ArrayList<Munchkin> createRankedPlayerList() {

		ArrayList<Munchkin> newData = new ArrayList<Munchkin>(MunchCounterMain.CURRENT_PLAYERS);

		Collections.sort(newData, new Comparator<Munchkin>() {

			@Override
			public int compare(Munchkin lhs, Munchkin rhs) {
				if (lhs.getPlayerLevel() < rhs.getPlayerLevel())
					return 1;
				else if (lhs.getPlayerLevel() > rhs.getPlayerLevel())
					return -1;

				return 0;
			}
		});

		return newData;

	}

}

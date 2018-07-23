package com.leihwelt.munchyd.presentation;

import com.leihwelt.munchyd.R;
import com.leihwelt.munchyd.R.layout;
import com.leihwelt.munchyd.model.Munchkin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MunchkinPresentationFragment extends Fragment {

	public static final String MUNCHKIN_PLAYER = "MUNCHKIN_PLAYER";
	private Munchkin munchkin;

	private TextView nameLabel;
	private TextView playerLevel;
	private TextView gearLevel;
	private TextView modifier;
	private TextView overall;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//		int playerId = getArguments().getInt(MUNCHKIN_PLAYER);
//
//		// make sure it does not exceed the player count
//		playerId %= MunchCounterMain.CURRENT_PLAYERS.size();
//
//		munchkin = MunchCounterMain.CURRENT_PLAYERS.get(playerId);

		return inflater.inflate(R.layout.munchkin_fragment, null);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

//		this.nameLabel = (TextView) view.findViewById(R.id.player_label);
//
//		this.playerLevel = (TextView) view.findViewById(R.id.player_level_value);
//		this.gearLevel = (TextView) view.findViewById(R.id.gear_level_value);
//		this.modifier = (TextView) view.findViewById(R.id.modifier_level_value);
//		this.overall = (TextView) view.findViewById(R.id.overall_level_value);
//
//		this.increaseLevel = (ImageButton) view.findViewById(R.id.increase_level);
//		this.decreaseLevel = (ImageButton) view.findViewById(R.id.decrease_level);
//
//		this.increaseGear = (ImageButton) view.findViewById(R.id.increase_gear);
//		this.decreaseGear = (ImageButton) view.findViewById(R.id.decrease_gear);
//
//		this.increaseModifier = (ImageButton) view.findViewById(R.id.increase_modifier);
//		this.decreaseModifier = (ImageButton) view.findViewById(R.id.decrease_modifier);

	}

//	private void setToMunchkin() {
//		this.nameLabel.setText(munchkin.getPlayerName());
//		this.playerLevel.setText(Integer.toString(munchkin.getPlayerLevel()));
//		this.gearLevel.setText(Integer.toString(munchkin.getGearLevel()));
//		this.modifier.setText(Integer.toString(munchkin.getModifier()));
//		this.overall.setText(Integer.toString(munchkin.getOverallLevel()));
//	}

//	@Subscribe
//	public void onMunchkinChangedEvent(MunchkinChangedEvent event) {
//		setToMunchkin();
//	}

//	@Override
//	public void onResume() {
//		super.onResume();
//
//		BUS.INSTANCE.get().register(this);
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//
//		BUS.INSTANCE.get().unregister(this);
//	}

}

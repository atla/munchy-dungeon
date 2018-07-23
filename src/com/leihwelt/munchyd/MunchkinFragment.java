package com.leihwelt.munchyd;

import com.leihwelt.munchyd.events.MunchkinChangedEvent;
import com.leihwelt.munchyd.events.NextPlayerEvent;
import com.leihwelt.munchyd.model.Munchkin;
import com.squareup.otto.Subscribe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MunchkinFragment extends Fragment implements OnClickListener {

	public static final String MUNCHKIN_PLAYER = "MUNCHKIN_PLAYER";

	private TextView nameLabel;
	private TextView playerLevel;
	private TextView gearLevel;
	private TextView modifier;
	private TextView overall;
	private ImageButton increaseLevel;
	private ImageButton decreaseLevel;
	private ImageButton increaseGear;
	private ImageButton decreaseGear;
	private ImageButton increaseModifier;
	private ImageButton decreaseModifier;
	private Button gender;

	private int playerId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		this.playerId = getArguments().getInt(MUNCHKIN_PLAYER);
		int count = MunchCounterMain.CURRENT_PLAYERS.size();

		if (count > 0) {
			// make sure it does not exceed the player count
			playerId %= MunchCounterMain.CURRENT_PLAYERS.size();
			// munchkin = MunchCounterMain.CURRENT_PLAYERS.get(playerId);

		}

		return inflater.inflate(R.layout.munchkin_fragment, null);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.nameLabel = (TextView) view.findViewById(R.id.player_label);

		this.playerLevel = (TextView) view.findViewById(R.id.player_level_value);
		this.gearLevel = (TextView) view.findViewById(R.id.gear_level_value);
		this.modifier = (TextView) view.findViewById(R.id.modifier_level_value);
		this.overall = (TextView) view.findViewById(R.id.overall_level_value);

		this.gender = (Button) view.findViewById(R.id.gender);

		this.increaseLevel = (ImageButton) view.findViewById(R.id.increase_level);
		this.decreaseLevel = (ImageButton) view.findViewById(R.id.decrease_level);

		this.increaseGear = (ImageButton) view.findViewById(R.id.increase_gear);
		this.decreaseGear = (ImageButton) view.findViewById(R.id.decrease_gear);

		this.increaseModifier = (ImageButton) view.findViewById(R.id.increase_modifier);
		this.decreaseModifier = (ImageButton) view.findViewById(R.id.decrease_modifier);

		this.increaseLevel.setOnClickListener(this);
		this.decreaseLevel.setOnClickListener(this);
		this.increaseGear.setOnClickListener(this);
		this.decreaseGear.setOnClickListener(this);
		this.increaseModifier.setOnClickListener(this);
		this.decreaseModifier.setOnClickListener(this);
		this.gender.setOnClickListener(this);
		
		this.setToMunchkin();
	}

	private void setToMunchkin() {

		if (playerId < MunchCounterMain.CURRENT_PLAYERS.size()) {
			Munchkin munchkin = MunchCounterMain.CURRENT_PLAYERS.get(playerId);

			if (munchkin != null) {

				int genderCode = munchkin.getGender();
				String gender = this.getActivity().getString(genderCode > 0 ? R.string.gender_male : R.string.gender_female) ;
				
				this.gender.setText(gender);
				this.nameLabel.setText(munchkin.getPlayerName());
				this.playerLevel.setText(Integer.toString(munchkin.getPlayerLevel()));
				this.gearLevel.setText(Integer.toString(munchkin.getGearLevel()));
				this.modifier.setText(Integer.toString(munchkin.getModifier()));
				this.overall.setText(Integer.toString(munchkin.getOverallLevel()));
			}
		}

	}

	@Subscribe
	public void onMunchkinChangedEvent(MunchkinChangedEvent event) {
		setToMunchkin();
	}

	@Subscribe
	public void onNextPlayerEvent(NextPlayerEvent event) {

		Munchkin munchkin = MunchCounterMain.CURRENT_PLAYERS.get(playerId);

		munchkin.nextRound();
	}

	@Override
	public void onResume() {
		super.onResume();

		BUS.INSTANCE.get().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		BUS.INSTANCE.get().unregister(this);
	}

	@Override
	public void onClick(View v) {

		Munchkin munchkin = MunchCounterMain.CURRENT_PLAYERS.get(playerId);

		if (v == this.increaseLevel) {
			munchkin.increasePlayerLevel();
		} else if (v == this.decreaseLevel) {
			munchkin.decreasePlayerLevel();
		} else if (v == this.decreaseGear) {
			munchkin.decreaseGearLevel();
		} else if (v == this.increaseGear) {
			munchkin.increaseGearLevel();
		} else if (v == this.decreaseModifier) {
			munchkin.decreaseLevelModifier();
		} else if (v == this.increaseModifier) {
			munchkin.increaseLevelModifier();
		} else if (v == this.gender) {
			munchkin.switchGender();
		}

	}

}

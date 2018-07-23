package com.leihwelt.munchyd.presentation;

import com.leihwelt.munchyd.R;
import com.leihwelt.munchyd.R.id;
import com.leihwelt.munchyd.R.layout;
import com.leihwelt.munchyd.events.MunchkinChangedEvent;
import com.leihwelt.munchyd.list.MunchkinAdapter;
import com.leihwelt.munchyd.list.MunchkinAdapter.Theme;
import com.leihwelt.munchyd.model.Munchkin;
import com.squareup.otto.Subscribe;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(17)
public class MunchkinPresentation extends Presentation {

	private static final String TAG = "MunchkinPresentation";
	private ListView listView;
	private Munchkin munchkin;

	private TextView nameLabel;
	private TextView playerLevel;
	private TextView gearLevel;
	private TextView modifier;
	private TextView overall;
	private TextView gameTime;
	private TextView time;
	
	public MunchkinPresentation(Context outerContext, Display display) {
		super(outerContext, display);
	}

	public MunchkinPresentation(Context outerContext, Display display, int theme) {
		super(outerContext, display, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.presentation_layout);

		getWindow().setBackgroundDrawable(null);
		
		this.adapter = new MunchkinAdapter(this.getLayoutInflater(), Theme.PRESENTATION);
		this.listView = (ListView) this.findViewById(R.id.list);
		this.listView.setDivider(null);
		this.listView.setAdapter(adapter);

		this.adapter.notifyDataSetChanged();

		this.nameLabel = (TextView) findViewById(R.id.player_label);

		this.playerLevel = (TextView) findViewById(R.id.player_level_value);
		this.gearLevel = (TextView) findViewById(R.id.gear_level_value);
		this.modifier = (TextView) findViewById(R.id.modifier_level_value);
		this.overall = (TextView) findViewById(R.id.overall_level_value);
		this.gameTime = (TextView) findViewById(R.id.game_time);
		this.time = (TextView)findViewById(R.id.time);
	}

	private MunchkinAdapter adapter;

	@Subscribe
	public void onMunchkinChangedEvent(MunchkinChangedEvent event) {
		this.adapter.notifyDataSetChanged();

		update();
	}

	public void showMunchkin(Munchkin munchkin) {

		Log.d(TAG, "Show Munchkin: " + munchkin);

		this.munchkin = munchkin;

		update();
	}

	private void update() {

		if (munchkin != null) {
			this.nameLabel.setText(munchkin.getPlayerName());
			this.playerLevel.setText(Integer.toString(munchkin.getPlayerLevel()));
			this.gearLevel.setText(Integer.toString(munchkin.getGearLevel()));
			this.modifier.setText(Integer.toString(munchkin.getModifier()));
			this.overall.setText(Integer.toString(munchkin.getOverallLevel()));
		}
	}

	public void setGameTime(String formattedTime, String currentTime) {
		if (this.gameTime != null) {
			this.gameTime.setText(formattedTime);
		}
		
		if (this.time != null) {
			this.time.setText(currentTime);
		}

	}

}

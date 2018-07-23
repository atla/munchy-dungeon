package com.leihwelt.munchyd;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRouter;
import android.media.MediaRouter.Callback;
import android.media.MediaRouter.RouteInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leihwelt.munchyd.events.CreateGameEvent;
import com.leihwelt.munchyd.events.GameStartedEvent;
import com.leihwelt.munchyd.events.GameStoppedEvent;
import com.leihwelt.munchyd.events.MunchkinChangedEvent;
import com.leihwelt.munchyd.events.NextPlayerEvent;
import com.leihwelt.munchyd.events.SelectMunchkinByIdEvent;
import com.leihwelt.munchyd.events.UpdateGameStateEvent;
import com.leihwelt.munchyd.events.UpdateGameTimeEvent;
import com.leihwelt.munchyd.model.Munchkin;
import com.leihwelt.munchyd.presentation.MunchkinPresentation;
import com.squareup.otto.Subscribe;

@SuppressLint("NewApi")
public class MunchCounterMain extends FragmentActivity implements OnClickListener, OnPageChangeListener {

	public final static ArrayList<Munchkin> CURRENT_PLAYERS = new ArrayList<Munchkin>();
	public final static int MAX_PLAYERS = 10;
	
	protected static final String TAG = MunchCounterMain.class.getSimpleName();

	public static int getPlayerId(Munchkin current) {

		int id = 0;

		for (Munchkin m : CURRENT_PLAYERS) {
			if (m == current)
				return id;
			id++;
		}

		return -1;
	}

	public static void resetGame() {
		for (Munchkin m : CURRENT_PLAYERS) {
			m.reset();
		}

		gameStartTime = System.currentTimeMillis();

		BUS.INSTANCE.post(new MunchkinChangedEvent());
		BUS.INSTANCE.post(new UpdateGameTimeEvent());
	}

	private MunchkinPagerAdapter mSectionsPagerAdapter;
	private ViewPager pager;
	private TextView gameTime;
	private Button nextPlayer;
	private MediaRouter mediaRouter;
	private boolean pause;
	private Object presentation;

	private View line;
	private View bottomWrapper;
	private View noGameText;
	private Timer timer;

	private static long gameStartTime = -1;

	private final static DateFormat timeFormatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);

	private final static class UpdateGameTimeTask extends TimerTask {

		private Activity activity;

		private Runnable updateRunnable = new Runnable() {

			@Override
			public void run() {
				BUS.INSTANCE.post(new UpdateGameTimeEvent());
			}

		};

		public UpdateGameTimeTask(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void run() {
			activity.runOnUiThread(updateRunnable);
		}
	}

	public static class ResetGameDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Do you want to reset all current players?").setPositiveButton("Reset", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					MunchCounterMain.resetGame();
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});
			return builder.create();
		}
	}

	public static class CreateGameDialog extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Do you want to create a new game?").setPositiveButton("Create New", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					BUS.INSTANCE.post(new CreateGameEvent());

				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				}
			});
			return builder.create();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_munch_counter_main);

		// remove unnecessary GPU overdraw
		getWindow().setBackgroundDrawable(null);
		
		mSectionsPagerAdapter = new MunchkinPagerAdapter(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(mSectionsPagerAdapter);
		pager.setOnPageChangeListener(this);

		noGameText = findViewById(R.id.no_game_text);
		noGameText.setOnClickListener(this);

		nextPlayer = (Button) findViewById(R.id.next_player);
		nextPlayer.setOnClickListener(this);

		updateGameTimeVisibility();

		line = findViewById(R.id.line);
		bottomWrapper = findViewById(R.id.bottom_wrapper);

		if (isVersionReadyForSecondScreen()) {
			// Get the media router service.
			mediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
		}

		ActionBar ab = this.getActionBar();
		ab.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.actionbar));

	}

	private void updateGameTimeVisibility() {
		gameTime = (TextView) findViewById(R.id.game_time);
		boolean showPlayTime = Preferences.INSTANCE.showPlayTime(this);
		gameTime.setVisibility(showPlayTime ? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_munch_counter_main, menu);

		boolean showPlayTime = Preferences.INSTANCE.showPlayTime(this);

		MenuItem item = menu.findItem(R.id.menu_reset_game_time);
		item.setVisible(showPlayTime);

		return true;
	}

	@Subscribe
	public void onCreateGameEvent(CreateGameEvent event) {

		FragmentManager fm = getSupportFragmentManager();
		CreateNewGameDialog createGameDialog = new CreateNewGameDialog();
		createGameDialog.show(fm, "NEW_GAME_DIALOG");
	}

	@Subscribe
	public void onUpdateGameTimeEvent(UpdateGameTimeEvent event) {

		updateGameTime();

	}

	@Subscribe
	public void onNextPlayerEvent(NextPlayerEvent event) {

		int item = pager.getCurrentItem();

		item++;
		item %= CURRENT_PLAYERS.size();

		pager.setCurrentItem(item);

	}

	@Subscribe
	public void onSeletMunchkinById(SelectMunchkinByIdEvent event) {

		int pos = event.getId();

		if (pos >= 0 && pos < CURRENT_PLAYERS.size()) {
			pager.setCurrentItem(pos);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		gameStartTime = Preferences.INSTANCE.getGameStartTime(this);

		if (gameStartTime < 0) {
			gameStartTime = System.currentTimeMillis();
		}

		timer = new Timer();
		timer.scheduleAtFixedRate(new UpdateGameTimeTask(this), 1000, 30 * 1000);

		loadPlayers();

		BUS.INSTANCE.get().register(this);

		if (isVersionReadyForSecondScreen()) {

			this.presentation = null;

			mediaRouterCallback = new MediaRouter.SimpleCallback() {
				@Override
				public void onRouteSelected(MediaRouter router, int type, RouteInfo info) {
					updatePresentation();
				}

				@Override
				public void onRouteUnselected(MediaRouter router, int type, RouteInfo info) {
					updatePresentation();
				}

				@Override
				public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo info) {
					updatePresentation();
				}
			};

			mediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, (Callback) mediaRouterCallback);
			pause = false;
			updatePresentation();
		}

		updateState();

	}

	private void loadPlayers() {

		String players = Preferences.INSTANCE.loadPlayers(this);

		if (players != null) {
			TypeReference<ArrayList<Munchkin>> typeRef = new TypeReference<ArrayList<Munchkin>>() {
			};

			ObjectMapper mapper = new ObjectMapper();
			try {
				ArrayList<Munchkin> munchkins = mapper.readValue(players, typeRef);

				CURRENT_PLAYERS.clear();
				CURRENT_PLAYERS.addAll(munchkins);
				updateState();

			} catch (JsonParseException e) {
				Log.e(TAG, "Error loading players", e);
			} catch (JsonMappingException e) {
				Log.e(TAG, "Error loading players", e);
			} catch (IOException e) {
				Log.e(TAG, "Error loading players", e);
			}
		}

	}

	private void updateState() {
		int count = CURRENT_PLAYERS.size();
		int state = (count > 2 && count < MAX_PLAYERS + 1 ) ? View.VISIBLE : View.INVISIBLE;
		int invstate = (count > 2 && count < MAX_PLAYERS + 1) ? View.GONE : View.VISIBLE;

		pager.setVisibility(state);
		line.setVisibility(state);
		bottomWrapper.setVisibility(state);
		noGameText.setVisibility(invstate);

	}

	@Subscribe
	public void onUpdateGameState(UpdateGameStateEvent event) {
		updateState();
	}

	@Subscribe
	public void onGameStartedEvent(GameStartedEvent event) {

		this.gameStartTime = System.currentTimeMillis();

		updateGameTime();
	}

	private void updateGameTime() {

		String playedTime = calculateGameTime();
		String time = calculateTime();

		this.gameTime.setText(playedTime);

		if (isVersionReadyForSecondScreen()) {

			if (presentation != null && CURRENT_PLAYERS.size() > 2) {
				MunchkinPresentation p = ((MunchkinPresentation) presentation);
				p.setGameTime(playedTime, time);
			}
		}
	}

	private String calculateTime() {
		Date d = new Date();

		return timeFormatter.format(d);
	}

	private String calculateGameTime() {

		long duration = System.currentTimeMillis() - MunchCounterMain.gameStartTime;
		// switch to seconds
		duration = duration / 1000;
		// minutes
		duration = duration / 60;

		long hours = (duration / 60);
		long minutes = (duration % 60);
		// show either 2h 20m or 20m depending if we already crossed one hour
		// play time

		if (hours == 0 && minutes == 0)
			return getString(R.string.just_started);

		String playedTime = ((hours > 0) ? ("" + hours + "h ") : "") + minutes + "m " + getString(R.string.played);

		return playedTime;

	}

	@Subscribe
	public void onGameStartedEvent(GameStoppedEvent event) {

	}

	@Subscribe
	public void onMunchkinChangedEvent(MunchkinChangedEvent event) {

		if (isVersionReadyForSecondScreen()) {

			if (presentation != null && CURRENT_PLAYERS.size() > 2) {
				MunchkinPresentation p = ((MunchkinPresentation) presentation);
				p.onMunchkinChangedEvent(event);
			}
		}
	}

	private boolean isVersionReadyForSecondScreen() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.menu_about) {
			showAbout();
		} else if (item.getItemId() == R.id.menu_create_game) {
			showCreateGameDialog();
		} else if (item.getItemId() == R.id.menu_reset_game) {
			showResetGameDialog();
		} else if (item.getItemId() == R.id.menu_show_play_time) {
			togglePlayedTime();
		} else if (item.getItemId() == R.id.menu_reset_game_time) {
			resetGameTime();
		}

		return super.onOptionsItemSelected(item);
	}

	private void resetGameTime() {
		gameStartTime = System.currentTimeMillis();

		updateGameTime();
	}

	private void togglePlayedTime() {

		boolean showPlayTime = !Preferences.INSTANCE.showPlayTime(this);

		gameTime.setVisibility(showPlayTime ? View.VISIBLE : View.GONE);

		Preferences.INSTANCE.setShowPlayTime(showPlayTime, this);
		
		this.invalidateOptionsMenu();

	}

	private void showResetGameDialog() {
		(new ResetGameDialog()).show(this.getSupportFragmentManager(), "CREATE_GAME_DIALOG");
	}

	private void showCreateGameDialog() {
		(new CreateGameDialog()).show(this.getSupportFragmentManager(), "CREATE_GAME_DIALOG");
	}

	private void showAbout() {
		this.startActivity(new Intent(this, About.class));
	}

	protected void updatePresentation() {
		if (isVersionReadyForSecondScreen()) {

			// Get the current route and its presentation display.
			MediaRouter.RouteInfo route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);

			if (route != null) {

				Display presentationDisplay = route.getPresentationDisplay();
				MunchkinPresentation newPresentation = null;

				if (this.presentation != null) {
					newPresentation = (MunchkinPresentation) this.presentation;
				}

				if (newPresentation != null && (newPresentation).getDisplay() != presentationDisplay) {
					newPresentation.dismiss();
					newPresentation = null;
				}

				if (newPresentation == null && presentationDisplay != null) {
					Log.i(TAG, "Showing presentation on display " + presentationDisplay);
					newPresentation = new MunchkinPresentation(this, presentationDisplay);

					newPresentation.setOnDismissListener(onDismissListener);
					try {
						newPresentation.show();

						if (CURRENT_PLAYERS.size() > 0) {
							Munchkin current = CURRENT_PLAYERS.get(pager.getCurrentItem());
							newPresentation.showMunchkin(current);
						}

					} catch (WindowManager.InvalidDisplayException e) {
						Log.w(TAG, "Could not show the presentation, display was removed while creating.", e);
						newPresentation = null;
					}

				}

				this.presentation = newPresentation;

			} else {
				Log.d(TAG, "There is no secondary display attached");
			}

			updateContents();
		}
	}

	private void updateContents() {

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (timer != null)
			timer.cancel();

		Preferences.INSTANCE.setGameStartTime(this, gameStartTime);
		storePlayers();

		BUS.INSTANCE.get().unregister(this);

		if (isVersionReadyForSecondScreen()) {
			mediaRouter.removeCallback((Callback) mediaRouterCallback);
			pause = true;

			if (presentation != null) {
				((MunchkinPresentation) presentation).dismiss();
				presentation = null;
			}

		}

	}

	private void storePlayers() {
		ObjectMapper mapper = new ObjectMapper();
		String players;
		try {
			players = mapper.writeValueAsString(CURRENT_PLAYERS);
			Preferences.INSTANCE.storePlayers(players, this);
		} catch (JsonProcessingException e) {
			Log.d(TAG, "Could not store players", e);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();

		if (isVersionReadyForSecondScreen()) {
			if (presentation != null) {
				Log.i(TAG, "Dismissing presentation due to activity onStop call");
				((Dialog) presentation).dismiss();
				presentation = null;
			}
		}
	}

	public class MunchkinPagerAdapter extends FragmentPagerAdapter {

		public MunchkinPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			Fragment fragment = new MunchkinFragment();
			Bundle args = new Bundle();
			args.putInt(MunchkinFragment.MUNCHKIN_PLAYER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return CURRENT_PLAYERS.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "";
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.nextPlayer) {
			BUS.INSTANCE.post(new NextPlayerEvent());
		} else if (v == noGameText) {
			BUS.INSTANCE.post(new CreateGameEvent());
		}

	}

	private Object mediaRouterCallback = null;

	private final DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (dialog == presentation) {
				Log.i(TAG, "Presentation was dismissed.");
				presentation = null;
				updateContents();
			}
		}
	};

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int id) {

		if (isVersionReadyForSecondScreen()) {

			if (presentation != null) {
				Munchkin current = CURRENT_PLAYERS.get(id);
				((MunchkinPresentation) presentation).showMunchkin(current);
			}
		}
	}

	public static void removeAllPlayers() {
		CURRENT_PLAYERS.clear();
		BUS.INSTANCE.post(new UpdateGameStateEvent());
	}

	public static void addPlayer(Munchkin mk) {
		CURRENT_PLAYERS.add(mk);

		BUS.INSTANCE.post(new MunchkinChangedEvent());
		BUS.INSTANCE.post(new UpdateGameStateEvent());

	}

}

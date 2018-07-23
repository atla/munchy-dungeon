package com.leihwelt.munchyd;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public enum Preferences {
	INSTANCE;

	private static final String GAME_START_TIME = "GAME_START_TIME";
	private final static String SHOW_PLAY_TIME = "SHOW_PLAY_TIME";
	
	private static final String PLAYERS = "PLAYERS";
	private static final String PREFS = "MUNCHKIN_COUNTER_PREFS";
	
	

	private Editor edit(final Context ctx) {
		return prefs(ctx).edit();
	}

	private SharedPreferences prefs(final Context ctx) {
		return ctx.getSharedPreferences(PREFS, 0);
	}

	public void storePlayers(String players, final Context ctx) {
		edit(ctx).putString(PLAYERS, players).commit();
	}

	public String loadPlayers(final Context ctx) {
		return prefs(ctx).getString(PLAYERS, null);
	}

	public long getGameStartTime(final Context ctx) {
		return prefs(ctx).getLong(GAME_START_TIME, -1);
	}

	public void setGameStartTime(final Context ctx, long gameStartTime) {
		prefs (ctx).edit().putLong(GAME_START_TIME, gameStartTime).commit();
	}
	
	public boolean showPlayTime (final Context ctx){
		return prefs(ctx).getBoolean(SHOW_PLAY_TIME, false);
	}
	
	public void setShowPlayTime (boolean showPlayTime, final Context ctx){
		prefs (ctx).edit().putBoolean(SHOW_PLAY_TIME, showPlayTime).commit();
	}
}

package com.leihwelt.munchyd.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leihwelt.munchyd.BUS;
import com.leihwelt.munchyd.events.MunchkinChangedEvent;

public class Munchkin {

	private int playerLevel = 1;
	private int gearLevel = 0;
	private int modifier = 0;

	// 1 = male, -1 == female
	private int gender = 1;

	private String playerName = "No Name";

	public Munchkin(String name) {
		this.playerName = name;
	}

	public Munchkin() {
	}

	public int getPlayerLevel() {
		return playerLevel;
	}

	public int getGearLevel() {
		return gearLevel;
	}

	@JsonIgnore
	public int getOverallLevel() {
		return playerLevel + gearLevel + modifier;
	}

	public void increasePlayerLevel() {
		playerLevel++;

		if (playerLevel > 10)
			playerLevel = 10;

		changed();
	}

	private void changed() {
		BUS.INSTANCE.post(new MunchkinChangedEvent());
	}

	public void decreasePlayerLevel() {
		playerLevel--;

		if (playerLevel < 2) {
			playerLevel = 1;
		}
		changed();
	}

	public void increaseLevelModifier() {
		modifier++;
		changed();
	}

	public void decreaseLevelModifier() {
		modifier--;
		changed();
	}

	public void increaseGearLevel() {
		gearLevel++;
		changed();

	}

	public void decreaseGearLevel() {
		gearLevel--;

		if (gearLevel < 0)
			gearLevel = 0;

		changed();
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
		changed();
	}

	public int getModifier() {
		return modifier;
	}

	public void nextRound() {
		modifier = 0;
		changed();

	}

	public void reset() {
		this.playerLevel = 1;
		this.gearLevel = 0;
		this.modifier = 0;
	}

	public void setPlayerLevel(int playerLevel) {
		this.playerLevel = playerLevel;
	}

	public void setGearLevel(int gearLevel) {
		this.gearLevel = gearLevel;
	}

	public void setModifier(int modifier) {
		this.modifier = modifier;
	}

	public void switchGender() {

		gender *= -1;
		changed();

	}

	public int getGender() {
		return gender;
	}

}

package com.leihwelt.munchyd;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.leihwelt.munchyd.list.MunchkinAdapter;
import com.leihwelt.munchyd.list.MunchkinAdapter.Theme;
import com.leihwelt.munchyd.model.Munchkin;

public class CreateNewGameDialog extends DialogFragment implements OnClickListener, OnEditorActionListener {

	private ListView list;
	private ImageButton addButton;
	private Button createButton;
	private MunchkinAdapter adapter;
	private EditText playerNameEdit;

	public CreateNewGameDialog() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.create_game, container);

		// clear all players
		MunchCounterMain.removeAllPlayers();

		this.list = (ListView) view.findViewById(R.id.list);

		this.addButton = (ImageButton) view.findViewById(R.id.add_player);
		this.createButton = (Button) view.findViewById(R.id.create_game_button);
		this.playerNameEdit = (EditText) view.findViewById(R.id.player_name);
		this.playerNameEdit.setOnEditorActionListener(this);
		this.addButton.setOnClickListener(this);
		this.createButton.setOnClickListener(this);

		this.adapter = new MunchkinAdapter(inflater, Theme.NORMAL);
		this.list.setAdapter(adapter);

		this.getDialog().setTitle("Create New Game");

		return view;
	}

	@Override
	public void onClick(View v) {

		if (v == addButton) {

			addPlayer();

		} else if (v == createButton) {

			int count = MunchCounterMain.CURRENT_PLAYERS.size();

			if (count > 2) {
				this.dismiss();
			}
		}

	}

	private void addPlayer() {
		int count = MunchCounterMain.CURRENT_PLAYERS.size();

		if (count < MunchCounterMain.MAX_PLAYERS) {
			String name = playerNameEdit.getText().toString();

			if (!TextUtils.isEmpty(name)) {
				Munchkin mk = new Munchkin(name);
				MunchCounterMain.addPlayer(mk);

				playerNameEdit.setText("");
				adapter.notifyDataSetChanged();
				playerNameEdit.requestFocus();
			}
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		if (actionId == EditorInfo.IME_ACTION_SEND
				|| (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
			addPlayer();
			return true;
		}

		return false;
	}

}

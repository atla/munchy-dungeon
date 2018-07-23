package com.leihwelt.munchyd.list;

import com.leihwelt.munchyd.BUS;
import com.leihwelt.munchyd.MunchCounterMain;
import com.leihwelt.munchyd.events.MunchkinChangedEvent;
import com.leihwelt.munchyd.events.SelectMunchkinByIdEvent;
import com.leihwelt.munchyd.list.MunchkinAdapter.Theme;
import com.leihwelt.munchyd.model.Munchkin;
import com.squareup.otto.Subscribe;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class MunchkinListView extends ListFragment {

	private MunchkinAdapter adapter;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.adapter = new MunchkinAdapter(this.getActivity().getLayoutInflater(), Theme.NORMAL);
	}

	@Subscribe
	public void onMunchkinChangedEvent(MunchkinChangedEvent event) {
		this.adapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		BUS.INSTANCE.get().register(this);
		this.getListView().setAdapter(adapter);
		this.setListShown(true);
		this.getListView().setDivider(null);

		this.adapter.notifyDataSetChanged();

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id2) {
		super.onListItemClick(l, v, position, id2);

		Munchkin current = (Munchkin) this.adapter.getItem(position);

		int id = MunchCounterMain.getPlayerId(current);

		if (id > -1) {
			BUS.INSTANCE.post(new SelectMunchkinByIdEvent(id));
		}

	}

	@Override
	public void onPause() {
		super.onPause();

		BUS.INSTANCE.get().unregister(this);
	}

}

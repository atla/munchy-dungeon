package com.leihwelt.munchyd;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.about);

	}

	public void openWebURL(String inURL) {
		Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));

		startActivity(browse);
	}

	public void rateClicked(View v) {
		openWebURL("https://play.google.com/store/apps/details?id=com.leihwelt.munchyd");

	}

	public void googleClicked(View v) {
		openWebURL("https://plus.google.com/115465922685935745669/posts");
	}

	public void twitterClicked(View v) {
		openWebURL("http://twitter.com/atla_");
	}

}

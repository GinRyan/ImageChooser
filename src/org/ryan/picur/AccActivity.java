package org.ryan.picur;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AccActivity extends FragmentActivity {

	private static final int _FLAG_PICK = 3000;
	PlaceholderFragment placeholder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acc);
		if (savedInstanceState == null) {
			placeholder = new PlaceholderFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.container, placeholder).commit();
		}
		startActivityForResult(new Intent(this, ImageChooserActivity.class), _FLAG_PICK);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private TextView tv_access;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_acc, container, false);
			tv_access = (TextView) rootView.findViewById(R.id.tv_access);
			return rootView;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == Activity.RESULT_OK && requestCode == _FLAG_PICK) {
				StringBuffer sb = new StringBuffer();
				ArrayList<String> select = (ArrayList<String>) data.getSerializableExtra(ImageChooserAdapter.URI);
				for (String string : select) {
					sb.append(string + "\n");
				}
				tv_access.setText(sb.toString());
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		placeholder.onActivityResult(requestCode, resultCode, data);
	}
}

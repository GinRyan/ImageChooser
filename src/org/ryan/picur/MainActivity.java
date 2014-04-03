package org.ryan.picur;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	public static class PlaceholderFragment extends Fragment {
		GridView grid;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			grid = (GridView) rootView.findViewById(R.id.grid);
			grid.setAdapter(new ImageAdapter(getActivity(), grid) {

				@Override
				public void overTheMaxImagesCount(int max) {

					Toast.makeText(getActivity(), "最多选取" + max + "张图片", Toast.LENGTH_SHORT).show();
				}
			});
			return rootView;
		}
	}

}

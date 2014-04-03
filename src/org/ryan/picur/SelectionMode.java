package org.ryan.picur;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public abstract class SelectionMode implements OnClickListener {
	Activity ctx;
	private View inflate;
	TextView select_count;
	Button confirm;

	public SelectionMode(Activity ctx) {
		this.ctx = ctx;
	}

	public ViewGroup inflateBuild() {
		inflate = LayoutInflater.from(ctx).inflate(R.layout.selection_action_mode, null);
		confirm = (Button) inflate.findViewById(R.id.confirm);
		select_count = (TextView) inflate.findViewById(R.id.select_count);
		confirm.setOnClickListener(this);
		return (ViewGroup) inflate;
	}

	/**
	 * 指定显示数字
	 * 
	 * @param value
	 */
	public ViewGroup assignNum(int value) {
		select_count.setText(String.valueOf(value));
		confirm.setVisibility(value > 0 ? View.VISIBLE : View.INVISIBLE);
		return (ViewGroup) inflate;
	}

	/**
	 * 传递按钮事件
	 */
	@Override
	public void onClick(View v) {
		doDeliver(v);
	}

	/**
	 * 点击勾选按钮的事件
	 * 
	 * @param v
	 */
	public abstract void doDeliver(View v);
}

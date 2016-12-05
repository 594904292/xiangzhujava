package com.bbxiaoqu.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;

public class FragmentU4 extends Fragment  {
	private DemoApplication myapplication;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view= inflater.inflate(R.layout.fragment_u_1, null);

		return view;
	}

	
}
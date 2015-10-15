package com.clecs.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

public class RootFragment extends Fragment
	{

//		@Override
//		public boolean onBackPressed()
//			{
//				// TODO Auto-generated method stub
//				return new BackPressImp(this).onBackPressed();
//			}

		public static void replaceFragmetn(Fragment currentFragment, Fragment nextFragment, int containerId, Bundle bundle)
			{
				// CommonFragment commonFragment = new CommonFragment();
				if (bundle != null)
					nextFragment.setArguments(bundle);
				FragmentTransaction transaction = currentFragment.getChildFragmentManager().beginTransaction();
				transaction.addToBackStack(null);
				transaction.replace(containerId, nextFragment).commit();
			}

		public static void replaceFragmetn(Fragment currentFragment, Fragment nextFragment, int containerId)
			{
				replaceFragmetn(currentFragment, nextFragment, containerId, null);
			}

	}

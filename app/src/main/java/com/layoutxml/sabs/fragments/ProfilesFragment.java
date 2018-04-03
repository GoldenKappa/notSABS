package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.layoutxml.sabs.R;

public class ProfilesFragment extends LifecycleFragment {
    private FragmentManager fragmentManager;
    private AppCompatActivity parentActivity;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        parentActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (parentActivity.getSupportActionBar() != null) {
            parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            parentActivity.getSupportActionBar().setHomeButtonEnabled(false);
        }
        parentActivity.setTitle(getString(R.string.app_profiles_tab_title));
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_profiles, container, false);
        recyclerView = view.findViewById(R.id.profilesRecyclerView);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profiles_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, new BlogFragment(), BlogFragment.class.getCanonicalName())
                .addToBackStack(BlogFragment.class.getCanonicalName())
                .commit();
        return super.onOptionsItemSelected(item);
    }
}

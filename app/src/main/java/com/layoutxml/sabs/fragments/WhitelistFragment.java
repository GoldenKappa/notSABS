package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.WhiteUrl;
import com.layoutxml.sabs.utils.BlockUrlPatternsMatch;

import java.util.ArrayList;
import java.util.List;


public class WhitelistFragment extends LifecycleFragment {
    private static final String TAG = WhitelistFragment.class.getCanonicalName();
    private ListView whiteUrlListView;
    private Button addWhitelistUrl;
    private EditText whitelistUrlEditText;
    private ArrayAdapter<String> itemsAdapter;
    private List<String> whitelist;
    private AppDatabase appDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = AppDatabase.getAppDatabase(getContext());
        whitelist = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_white_list, container, false);
        getActivity().setTitle(R.string.allow_custom_urls);
        addWhitelistUrl = view.findViewById(R.id.addWhitelistUrl);
        whiteUrlListView = view.findViewById(R.id.urlList);
        whitelistUrlEditText = view.findViewById(R.id.whitelistUrlEditText);

        ((MainActivity)getActivity()).hideBottomBar();

        appDatabase.whiteUrlDao().getAll().observe(this, whitelistUrls -> {
            whitelist.clear();
            if (whitelistUrls != null) {
                for (WhiteUrl whiteUrl : whitelistUrls) {
                    whitelist.add(whiteUrl.url);
                }
            }
            itemsAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, whitelist);
            whiteUrlListView.setAdapter(itemsAdapter);
        });

        whiteUrlListView.setOnItemClickListener((parent, view1, position, id) -> {
            String item = (String) parent.getItemAtPosition(position);
            AsyncTask.execute(() -> appDatabase.whiteUrlDao().deleteByUrl(item));
            itemsAdapter.notifyDataSetChanged();
            Toast.makeText(this.getContext(), "Url removed", Toast.LENGTH_SHORT).show();
        });
        addWhitelistUrl.setOnClickListener(v -> {
            String urlToAdd = whitelistUrlEditText.getText().toString();
            if (!(BlockUrlPatternsMatch.wildcardValid(urlToAdd)) && !(BlockUrlPatternsMatch.domainValid(urlToAdd)))
            {
                Toast.makeText(this.getContext(), "Url not valid. Please check", Toast.LENGTH_SHORT).show();
                return;
            }
            AsyncTask.execute(() -> {
                WhiteUrl whiteUrl = new WhiteUrl(urlToAdd);
                appDatabase.whiteUrlDao().insert(whiteUrl);
            });
            whitelistUrlEditText.setText("");
            Toast.makeText(this.getContext(), "Url has been added", Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}

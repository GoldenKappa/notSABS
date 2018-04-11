package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.layoutxml.sabs.blocker.fwInterface;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.UserBlockUrl;
import com.layoutxml.sabs.utils.BlockUrlPatternsMatch;
import com.sec.enterprise.AppIdentity;
import com.sec.enterprise.firewall.DomainFilterRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockCustomUrlFragment extends LifecycleFragment {

    private List<String> customUrlsToBlock;
    private Context context;
    private AppDatabase appDatabase;
    private ArrayAdapter<String> itemsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = AppDatabase.getAppDatabase(getContext());
        customUrlsToBlock = new ArrayList<>();
        context = this.getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual_url_block, container, false);
        ListView listView = view.findViewById(R.id.customUrlsListView);
        getActivity().setTitle(R.string.block_custom_urls);
        ((MainActivity)getActivity()).hideBottomBar();

        appDatabase.userBlockUrlDao()
                .getAll()
                .observe(this, userBlockUrls -> {
                    customUrlsToBlock.clear();
                    if (userBlockUrls != null) {
                        for (UserBlockUrl userBlockUrl : userBlockUrls) {
                            customUrlsToBlock.add(userBlockUrl.url);
                        }
                    }
                    itemsAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, customUrlsToBlock);
                    listView.setAdapter(itemsAdapter);
                });

        listView.setOnItemClickListener((parent, view1, position, id) -> {

            // Get the selected (removed) domain
            String removedDomain = listView.getItemAtPosition(position).toString();

            AsyncTask.execute(() -> appDatabase.userBlockUrlDao().deleteByUrl(customUrlsToBlock.get(position)));
            itemsAdapter.notifyDataSetChanged();

            // Remove deny rule from the firewall
            removeDomainFilterRule(removedDomain);

            Toast.makeText(context, "URL Removed.", Toast.LENGTH_SHORT).show();

        });

        final EditText addBlockedUrlEditText = view.findViewById(R.id.addBlockedUrlEditText);
        Button addCustomBlockedUrlButton = view.findViewById(R.id.addCustomBlockedUrlButton);
        addCustomBlockedUrlButton.setOnClickListener(v -> {
            String urlToAdd = addBlockedUrlEditText.getText().toString().trim().toLowerCase();
            if (!(BlockUrlPatternsMatch.wildcardValid(urlToAdd)) && !(BlockUrlPatternsMatch.domainValid(urlToAdd)))
            {
                Toast.makeText(context, "Url not valid. Please check", Toast.LENGTH_SHORT).show();
                return;
            }

            AsyncTask.execute(() -> {
                UserBlockUrl userBlockUrl = new UserBlockUrl(urlToAdd);
                appDatabase.userBlockUrlDao().insert(userBlockUrl);
                // Add deny rule to the firewall
                addDomainFilterRule(urlToAdd);

            });
            addBlockedUrlEditText.setText("");
            Toast.makeText(context, "Url has been added", Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    private void addDomainFilterRule(String dfRule)
    {
        // Create a new instance of the firewall interface
        fwInterface FW = new fwInterface();

        // If the firewall is enabled
        if(FW.isEnabled()) {
            // Create an empty denylist
            List<String> denyList = new ArrayList<>();

            if (BlockUrlPatternsMatch.domainValid(dfRule))
            {
                // Remove www. www1. etc
                // Necessary as we do it for the denylist, whiteUrls, domain could get through the blocker if it doesn't start with www
                dfRule = dfRule.replaceAll("^(www)([0-9]{0,3})?(\\.)", "");

                //Block the same domain with www prefix
                final String urlReady = "*" + dfRule;

                // Add the blacklist URL
                denyList.add(urlReady);

            } else if (BlockUrlPatternsMatch.wildcardValid(dfRule)) {

                denyList.add(dfRule);

            }


            // Create a new 'rules' arraylist
            List<DomainFilterRule> denyrules = new ArrayList<>();

            // Add the denyList to the rules array
            denyrules.add(new DomainFilterRule(new AppIdentity("*", null), denyList, new ArrayList<>()));

            // Add the rules to the firewall
            FW.addDomainFilterRules(denyrules);
        }
    }

    private void removeDomainFilterRule(String dfRule)
    {
        // Create a new instance of the firewall interface
        fwInterface FW = new fwInterface();

        // If the firewall is enabled
        if(FW.isEnabled()) {
            // Create an empty denylist
            List<String> denyList = new ArrayList<>();

            // Add the blacklist URL
            denyList.add(dfRule);

            // Create a new 'rules' arraylist
            List<DomainFilterRule> denyrules = new ArrayList<>();

            // Add the denyList to the rules array
            denyrules.add(new DomainFilterRule(new AppIdentity("*", null), denyList, new ArrayList<>()));

            // Add the rules to the firewall
            FW.removeDomainFilterRules(denyrules);
        }
    }
}

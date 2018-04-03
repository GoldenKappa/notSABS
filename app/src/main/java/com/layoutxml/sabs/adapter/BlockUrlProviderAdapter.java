package com.layoutxml.sabs.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.BlockUrlProvider;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BlockUrlProviderAdapter extends ArrayAdapter<BlockUrlProvider> {

    private static final String TAG = BlockUrlProviderAdapter.class.getCanonicalName();

    public BlockUrlProviderAdapter(Context context, List<BlockUrlProvider> blockUrlProviders) {
        super(context, 0, blockUrlProviders);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BlockUrlProvider blockUrlProvider = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_block_url_provider, parent, false);
        }
        TextView blockUrlProviderTextView = convertView.findViewById(R.id.blockUrlProviderTextView);
        TextView blockUrlCountTextView = convertView.findViewById(R.id.blockUrlCountTextView);
        CheckBox urlProviderCheckBox = convertView.findViewById(R.id.urlProviderCheckBox);
        ImageView deleteUrlImageView = convertView.findViewById(R.id.deleteUrlProviderImageView);
        TextView lastUpdatedTextView = convertView.findViewById(R.id.lastUpdatedTextView);
        urlProviderCheckBox.setTag(position);
        deleteUrlImageView.setTag(position);
        if (blockUrlProvider != null) {
            Log.d(TAG, blockUrlProvider.url);
            switch (blockUrlProvider.url) {
                case MainActivity.ADHELL_STANDARD_PACKAGE:
                    blockUrlProviderTextView.setText(R.string.standard_package_name);
                    break;
                case MainActivity.SABS_EXPERIMENTAL_PACKAGE:
                    blockUrlProviderTextView.setText(R.string.experimental_package);
                    break;
                case MainActivity.SABS_MMOTTI_PACKAGE:
                    blockUrlProviderTextView.setText(R.string.mmottis_package);
                    break;
                default:
                    blockUrlProviderTextView.setText(blockUrlProvider.url + "");
                    break;
            }
            blockUrlCountTextView.setText(blockUrlProvider.count + "");
            urlProviderCheckBox.setChecked(blockUrlProvider.selected);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            lastUpdatedTextView.setText(dateFormat.format(blockUrlProvider.lastUpdated));
            if (!blockUrlProvider.deletable) {
                deleteUrlImageView.setVisibility(View.GONE);
                //urlProviderCheckBox.setClickable(false);
            }

        }
        urlProviderCheckBox.setOnClickListener((buttonView) -> {
            boolean isChecked = urlProviderCheckBox.isChecked();
            int position2 = (Integer) buttonView.getTag();
            BlockUrlProvider blockUrlProvider2 = getItem(position2);
            if (blockUrlProvider2 != null) {
                blockUrlProvider2.selected = isChecked;
                Maybe.fromCallable(() -> {
                    AppDatabase mDb = AppDatabase.getAppDatabase(App.get().getApplicationContext());
                    mDb.blockUrlProviderDao().updateBlockUrlProviders(blockUrlProvider2);
                    return null;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        });

        deleteUrlImageView.setOnClickListener(imageView -> {
            int position2 = (Integer) imageView.getTag();
            BlockUrlProvider blockUrlProvider2 = getItem(position2);
            if (blockUrlProvider2 != null) {
                Maybe.fromCallable(() -> {
                    AppDatabase mDb = AppDatabase.getAppDatabase(App.get().getApplicationContext());
                    mDb.blockUrlProviderDao().delete(blockUrlProvider2);
                    return null;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }

        });
        return convertView;
    }
}

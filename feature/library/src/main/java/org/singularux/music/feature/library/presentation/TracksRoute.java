package org.singularux.music.feature.library.presentation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import org.singularux.music.core.threading.ComputationExecutorService;
import org.singularux.music.feature.library.data.SearchItemData;
import org.singularux.music.feature.library.data.TrackItemData;
import org.singularux.music.feature.library.databinding.RouteTracksBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TracksRoute extends Fragment {

    public @Inject @ComputationExecutorService ExecutorService computationExecutorService;
    public @Inject Picasso picasso;

    private RouteTracksBinding binding;
    private TracksViewModel viewModel;

    private TrackItemListAdapter trackItemListAdapter;
    private SearchItemListAdapter searchItemListAdapter;

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        binding = RouteTracksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Add inset listeners
        ViewCompat.setOnApplyWindowInsetsListener(binding.searchBar, new SearchBarInsetListener());
        ViewCompat.setOnApplyWindowInsetsListener(binding.content, new ContentInsetListener());
        // Extract ViewModel
        viewModel = new ViewModelProvider(this).get(TracksViewModel.class);
        // Add static action listeners
        binding.searchView.getEditText().addTextChangedListener(new SearchViewTextWatcher());
        // Create and apply data adapters
        trackItemListAdapter = new TrackItemListAdapter(computationExecutorService, picasso,
                new TrackListItemActionListener());
        searchItemListAdapter = new SearchItemListAdapter(computationExecutorService, picasso,
                new SearchListItemActionListener());
        binding.content.setAdapter(trackItemListAdapter);
        binding.searchContent.setAdapter(searchItemListAdapter);
        // Listen data
        ActivityResultLauncher<String> readMusicPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ReadMusicPermissionLauncherResult());
        readMusicPermissionLauncher.launch(viewModel.getReadMusicPermission());
    }

    private final class TrackListItemActionListener implements TrackItemListAdapter.ActionListener {

        @Override
        public void onAction(int position,
                             @NonNull TrackItemData item,
                             @NonNull TrackItemListAdapter.Action action) {
            switch (action) {
                case PLAY:
                    viewModel.playFromTrackList(position);
                    break;
                case ADD_TO_QUEUE:
                    viewModel.addToQueueFromTrackList(position);
                    break;
            }
        }

    }

    private final class SearchListItemActionListener
            implements SearchItemListAdapter.ActionListener {

        @Override
        public void onAction(int position,
                             @NonNull SearchItemData item,
                             @NonNull SearchItemListAdapter.Action action) {
            switch (action) {
                case PLAY:
                    viewModel.playFromSearchList(position);
                    break;
                case ADD_TO_QUEUE:
                    viewModel.addToQueueFromSearchList(position);
                    break;
            }
        }

    }

    private final class SearchViewTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
            viewModel.updateSearchQuery(s.toString());
        }

    }

    private final class ReadMusicPermissionLauncherResult
            implements ActivityResultCallback<Boolean> {

        @Override
        public void onActivityResult(@NonNull Boolean result) {
            if (result) {
                viewModel.getTrackItemDataList().observe(getViewLifecycleOwner(),
                        new TrackItemDataListObserver());
                viewModel.getSearchItemDataList().observe(getViewLifecycleOwner(),
                        new SearchItemDataListObserver());
            }
            // TODO: When permission is not given, tell user what went wrong
        }

    }

    private final class TrackItemDataListObserver implements Observer<List<TrackItemData>> {

        @Override
        public void onChanged(@NonNull List<TrackItemData> trackItemDataList) {
            trackItemListAdapter.submitList(trackItemDataList);
        }

    }

    private final class SearchItemDataListObserver implements Observer<List<SearchItemData>> {

        @Override
        public void onChanged(@NonNull List<SearchItemData> searchItemDataList) {
            searchItemListAdapter.submitList(searchItemDataList);
        }

    }

    private static final class SearchBarInsetListener implements OnApplyWindowInsetsListener {

        private static final int MARGIN_HORIZONTAL_DP = 16;
        private static final int MARGIN_TOP_DP = 8;

        @Override
        public @NonNull WindowInsetsCompat onApplyWindowInsets(
                @NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            float density = view.getContext().getResources().getDisplayMetrics().density;
            ViewGroup.MarginLayoutParams marginLayoutParams =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            int leftMargin = insets.left + (int) (MARGIN_HORIZONTAL_DP * density);
            int topMargin = insets.top + (int) (MARGIN_TOP_DP * density);
            int rightMargin = insets.right + (int) (MARGIN_HORIZONTAL_DP * density);
            int bottomMargin = 0;
            marginLayoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            view.setLayoutParams(marginLayoutParams);
            return WindowInsetsCompat.CONSUMED;
        }

    }

    private static final class ContentInsetListener implements OnApplyWindowInsetsListener {

        private static final int PADDING_TOP_DP = 72;

        @Override
        public @NonNull WindowInsetsCompat onApplyWindowInsets(
                @NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            float density = view.getContext().getResources().getDisplayMetrics().density;
            int leftPadding = insets.left;
            int topPadding = insets.top + (int) (PADDING_TOP_DP * density);
            int rightPadding = insets.right;
            int bottomPadding = 0;
            view.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
            return WindowInsetsCompat.CONSUMED;
        }

    }

}

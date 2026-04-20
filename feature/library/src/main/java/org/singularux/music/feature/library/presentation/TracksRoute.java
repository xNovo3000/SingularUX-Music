package org.singularux.music.feature.library.presentation;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.singularux.music.feature.library.R;
import org.singularux.music.feature.library.databinding.RouteTracksBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TracksRoute extends Fragment {

    private RouteTracksBinding binding;
    private TracksViewModel viewModel;

    public TracksRoute() {
        super(R.layout.route_tracks);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding = RouteTracksBinding.bind(view);
        // Apply inset listeners
        ViewCompat.setOnApplyWindowInsetsListener(binding.list, new ListInsetListener());
        ViewCompat.setOnApplyWindowInsetsListener(binding.searchBar, new SearchBarInsetListener());
        ViewCompat.setOnApplyWindowInsetsListener(binding.playShuffled, new FabInsetListener());
        // Get ViewModel
        viewModel = new ViewModelProvider(this).get(TracksViewModel.class);
    }

    private static class ListInsetListener implements OnApplyWindowInsetsListener {

        private static final int MARGIN_TOP_DP = 64 + 8;

        @Override
        public @NonNull WindowInsetsCompat onApplyWindowInsets(
                @NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
            // Retrieve required insets
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            float density = view.getContext().getResources().getDisplayMetrics().density;
            // Apply margin
            ViewGroup.MarginLayoutParams marginLayoutParams =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            marginLayoutParams.topMargin = insets.top + (int) (density * MARGIN_TOP_DP);
            marginLayoutParams.leftMargin = insets.left;
            marginLayoutParams.rightMargin = insets.right;
            view.setLayoutParams(marginLayoutParams);
            // Last element of the view
            return WindowInsetsCompat.CONSUMED;
        }

    }

    private static class FabInsetListener implements OnApplyWindowInsetsListener {

        private static final int MARGIN_DP = 16;

        @Override
        public @NonNull WindowInsetsCompat onApplyWindowInsets(
                @NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
            // Retrieve required insets
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
            float density = view.getContext().getResources().getDisplayMetrics().density;
            // Apply margin
            ViewGroup.MarginLayoutParams marginLayoutParams =
                    (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            marginLayoutParams.topMargin = (int) (density * MARGIN_DP);
            marginLayoutParams.bottomMargin = (int) (density * MARGIN_DP);
            marginLayoutParams.leftMargin = (int) (density * MARGIN_DP);
            marginLayoutParams.rightMargin = insets.right + (int) (density * MARGIN_DP);
            view.setLayoutParams(marginLayoutParams);
            // Last element of the view
            return WindowInsetsCompat.CONSUMED;
        }

    }

}

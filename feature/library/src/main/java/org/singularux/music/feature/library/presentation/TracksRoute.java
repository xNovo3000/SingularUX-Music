package org.singularux.music.feature.library.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.singularux.music.feature.library.databinding.RouteTracksBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TracksRoute extends Fragment {

    private RouteTracksBinding binding;
    private TracksViewModel viewModel;

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        binding = RouteTracksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // TODO: Add inset listeners
        // Extract ViewModel
        viewModel = new ViewModelProvider(this).get(TracksViewModel.class);
        // TODO: Add static action listeners
        // TODO: Listen data
    }

}

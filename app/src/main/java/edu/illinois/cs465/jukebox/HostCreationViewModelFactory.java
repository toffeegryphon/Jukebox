package edu.illinois.cs465.jukebox;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class HostCreationViewModelFactory implements ViewModelProvider.Factory {
    private final String json;

    public HostCreationViewModelFactory(String json) {
        this.json = json;
    }
    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) HostCreationViewModel.getGson().fromJson(this.json, HostCreationViewModel.class);
    }
}

package edu.illinois.cs465.jukebox;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HostCreationViewModel extends ViewModel {
    private MutableLiveData<String> userName;
    private MutableLiveData<String> theme;

    public HostCreationViewModel() {
        userName = new MutableLiveData<>();
        theme = new MutableLiveData<>();
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.setValue(userName);
    }

    public LiveData<String> getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme.setValue(theme);
    }
}

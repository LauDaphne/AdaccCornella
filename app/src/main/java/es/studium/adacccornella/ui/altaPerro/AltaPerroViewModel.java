package es.studium.adacccornella.ui.altaPerro;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AltaPerroViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AltaPerroViewModel() {
    }

    public LiveData<String> getText() {
        return mText;
    }
}
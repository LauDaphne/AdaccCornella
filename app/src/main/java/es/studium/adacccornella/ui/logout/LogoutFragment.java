package es.studium.adacccornella.ui.logout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import es.studium.adacccornella.ui.login.LoginActivity;

public class LogoutFragment extends Fragment {

    private LogoutViewModel logoutViewModel;

    SharedPreferences sharedPref = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedPref = getContext().getSharedPreferences("ui.login.LoginActivity",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user", "");
        editor.putBoolean("isLogin", false);
        editor.commit();
        Intent i = new Intent(getContext(),LoginActivity.class);
        startActivity(i);

        return null;
    }
}
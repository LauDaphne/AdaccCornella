package es.studium.adacccornella.ui.altaPerro;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import es.studium.adacccornella.R;

public class AltaPerroFragment extends Fragment {

    private AltaPerroViewModel galleryViewModel;
    String actualdate;
    String esterilizado;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(AltaPerroViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        final EditText txtNombrePerro = root.findViewById(R.id.txtNombrePerro);
        final CalendarView calendarioPerro = root.findViewById(R.id.calendarView);
        final Switch swtcPerro = root.findViewById(R.id.switch2);
        final Button bttnAceptarPerro = root.findViewById(R.id.bttnAceptarPerro);
        final Button bttnLimpiarPerro = root.findViewById(R.id.bttnLimpiarPerro);


        calendarioPerro.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month= month+1;
                if(month<10){
                    month = Integer.parseInt("0"+month);
                }
               actualdate = year + "/"+ month + "/"+ dayOfMonth;
                Log.println(Log.ASSERT, "Aviso", "La fecha es:" + actualdate);
            }
        });

        bttnLimpiarPerro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtNombrePerro.setText("");
                swtcPerro.setChecked(false);
                calendarioPerro.clearFocus();
            }
        });

        bttnAceptarPerro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        // All your networking logic
                        // should be here
                        try {
                            if(swtcPerro.isChecked()){
                                esterilizado = "1";
                            }else{
                                esterilizado = "0";
                            }
                            String response = "";
                            HashMap<String, String> postDataParams = new
                                    HashMap<String, String>();
                            postDataParams.put("nombre", txtNombrePerro.getText().toString());
                            postDataParams.put("fecha", actualdate);
                            postDataParams.put("esterilizadoAnimal", esterilizado);
                            URL url = new
                                    URL("http://192.168.0.14/adacc/addac.php");
                            HttpURLConnection connection = (HttpURLConnection)
                                    url.openConnection();
                            connection.setReadTimeout(15000);
                            connection.setConnectTimeout(15000);
                            connection.setRequestMethod("POST");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            OutputStream os = connection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(new
                                    OutputStreamWriter(os, "UTF-8"));
                            writer.write(getPostDataString(postDataParams));
                            writer.flush();
                            writer.close();
                            os.close();
                            int responseCode=connection.getResponseCode();
                            if (responseCode == HttpsURLConnection.HTTP_OK) {
                                String line;
                                BufferedReader br=new BufferedReader(new
                                        InputStreamReader(connection.getInputStream()));
                                while ((line=br.readLine()) != null) {
                                    response+=line;
                                }
                            }
                            else {
                                response="";
                            }
                            connection.getResponseCode();
                            if (connection.getResponseCode() == 200)
                            {
                                // Success
                                Log.println(Log.ASSERT,"Resultado", "Registro insertado:"+response);
                                connection.disconnect();
                            }
                            else
                            {
                                // Error handling code goes here
                                Log.println(Log.ASSERT,"Error", "Error");
                            }
                        }
                        catch(Exception e)
                        {
                            Log.println(Log.ASSERT,"Excepción", e.getMessage());
                        }
                    }
                });
                Snackbar.make(view, "Replace with your own action",
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            private String getPostDataString(HashMap<String, String> params)
                    throws UnsupportedEncodingException {
                StringBuilder result = new StringBuilder();
                boolean first = true;
                for(Map.Entry<String, String> entry : params.entrySet()){
                    if (first)
                        first = false;
                    else
                        result.append("&");
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
                return result.toString();
            }

        });

        return root;
    }
}
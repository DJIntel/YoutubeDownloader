package pl.djintel.youtubedownloader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadActivity extends AppCompatActivity {

    ApiCommunication apiCommunication;
    WaitDialog waitDialog;
    ErrorDialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        apiCommunication = new ApiCommunication();
        waitDialog = new WaitDialog();
        errorDialog = new ErrorDialog();
        Log.i("lol", "load download");
        ApiCommunication.GetParam[] gt = new ApiCommunication.GetParam[1];
        ApiCommunication.GetParam gt1 = apiCommunication.new GetParam("ID", Vars.Url);
        gt[0] = gt1;
        ApiCommunication.ApiCommParam ACP = apiCommunication.new ApiCommParam(gt, "https://192.168.2.29/~przemek/YtToMp3/Api/Image.php", ApiCommunication.ApiCommType.IMAGE);
        waitDialog.show(getString(R.string.Loading));
        apiCommunication.new ApiComunication().execute(ACP);
    }

    public void onClickFormatButton(View v) {
        /*switch(v.getId()){
            case R.id.MP4:

                break;

            case R.id.FLV:

                break;

            case R.id.GPP:

                break;

            case R.id.WebM:

                break;

            case R.id.MP3:

                break;
        }*/

        Button btn = (Button) findViewById(v.getId());
        Button fBtn = (Button) findViewById(R.id.button2);
        fBtn.setText(btn.getText());
    }

    /*
    public void onClick_FormatButton(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.format_dialog, null);
        builder.setView(dialogView);
        builder.setTitle(R.string.SelFormat);
        builder.setPositiveButton(R.string.Login, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String login = "one są złe";
                String pass = "to też jest złe";
                if(loginInput != null) login = loginInput.getText().toString();
                if(passInput != null) pass = passInput.getText().toString();
                final boolean remember = true;
                if(rememberSwitch != null) {
                    rememberSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            boolean remember = isChecked;
                        }
                    });
                }
                LoginManager LM = new LoginManager();
                ProgressDialog progressBar = new ProgressDialog(MainActivity.this);
                VarsHolder.progressBar = progressBar;
                progressBar.setCancelable(false);
                progressBar.setMessage(getString(R.string.LggingIn));
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.show();
                VarsHolder.user = login;
                LM.Login(login, pass);
            }
        }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).setNeutralButton("Register", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent("pl.djintel.programingschool.Settings"));
            }
        });

        builder.show();
    }
    }*/

    public class WaitDialog {
        ProgressDialog progressBar;

        public void show(String msg){
            progressBar = new ProgressDialog(getApplicationContext());
            progressBar.setCancelable(false);
            progressBar.setMessage(msg);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.show();
        }

        public void dismiss(){
            progressBar.dismiss();
        }
    }

    public class ErrorDialog{

        public void show(String message){
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}

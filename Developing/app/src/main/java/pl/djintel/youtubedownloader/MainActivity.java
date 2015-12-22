package pl.djintel.youtubedownloader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    private boolean Checked;
    WaitDialog waitDialog;
    ErrorDialog errorDialog;
    ImageView img;
    Bitmap bmp;
    ApiCommunication apiCommunication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiCommunication = new ApiCommunication();
        waitDialog = new WaitDialog();
        errorDialog = new ErrorDialog();
        loadSettings();
        if(!createIfExist()) Log.w("elo", "Didn't create :'(");
    }

    public void onResume(){
        if(Vars.StartDownloadActivity){
            startActivity(new Intent(this, DownloadActivity.class));
            Vars.StartDownloadActivity = false;
        }
        super.onResume();
    }

    public void loadSettings(){
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        SharedPreferences settings = getSharedPreferences("Settings", MODE_PRIVATE);

        /* Imgs path */
        String imgPath = settings.getString("ImgPath", "");
        if(imgPath.equals("")){
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + File.separator + "YoutubeDownloader" + File.separator + "Imgs" + File.separator);
            editor.putString("ImgPath", dir.toString());
            Vars.ImgPath = dir.toString();
        } else {
            Vars.ImgPath = imgPath;
        }

        /* Video save path */
        String videoPath = settings.getString("VideoPath", "");
        if(videoPath.equals("")){
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + File.separator + "Download" + File.separator + "YoutubeDownloader" + File.separator + "Video" + File.separator);
            editor.putString("VideoPath", dir.toString());
            Vars.VideoPath = dir.toString();
        } else {
            Vars.VideoPath = videoPath;
        }

        /* Music path */
        String musicPath = settings.getString("MusicPath", "");
        if(musicPath.equals("")){
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + File.separator + "Download" + File.separator + "YoutubeDownloader" + File.separator + "Music" + File.separator);
            editor.putString("MusicPath", dir.toString());
            Vars.MusicPath = dir.toString();
        } else {
            Vars.MusicPath = musicPath;
        }

        /* Main path */
        String mainPath = settings.getString("MainPath", "");
        if(musicPath.equals("")){
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + File.separator + "Download" + File.separator + "YoutubeDownloader" + File.separator);
            editor.putString("MainPath", dir.toString());
            Vars.MainPath = dir.toString();
        } else {
            Vars.MainPath = mainPath;
        }


        Log.i("SharedPreferences: ", Vars.ImgPath + ", " + Vars.MusicPath + ", " + Vars.VideoPath);
    }

    /* Create folder is not exist */
    //TODO Set own path in settings
    //TODO Naprawić to!!!
    public boolean createIfExist(){
        File folder = new File(Vars.ImgPath);
        if(!folder.mkdirs()) return false;
        folder = new File(Vars.MusicPath);
        if(!folder.mkdirs()) return false;
        folder = new File(Vars.VideoPath);
        if(!folder.mkdirs()) return false;
        return true;
    }

    /* Menu options */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClick_button1(View v){
        ApiCommunication.GetParam[] gt = new ApiCommunication.GetParam[1];
        EditText ID = (EditText) findViewById(R.id.Yt_Url);
        String YtID = ID.getText().toString();
        String [] YtIDT = YtID.split("=");
        ApiCommunication.GetParam gt1 = apiCommunication.new GetParam("ID", YtIDT[1]);
        Vars.Url = gt1.value;
        gt[0] = gt1;
        ApiCommunication.ApiCommParam ACP = apiCommunication.new ApiCommParam(gt, "https://192.168.2.29/~przemek/YtToMp3/Api/Exist.php", ApiCommunication.ApiCommType.CHECK);
        waitDialog.show(getString(R.string.Checking));
        apiCommunication.new ApiComunication().execute(ACP);
    }

    public class WaitDialog {
        ProgressDialog progressBar;

        public void show(String msg){
            progressBar = new ProgressDialog(getBaseContext());
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

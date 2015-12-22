package pl.djintel.youtubedownloader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class ApiCommunication extends Activity {

    WaitDialog waitDialog;
    Bitmap bmp;
    ImageView img;
    ErrorDialog errorDialog;

    public ApiCommunication(){
        waitDialog = new WaitDialog();
        //img = (ImageView) findViewById(R.id.imageView);
        errorDialog = new ErrorDialog();
    }

    public class ApiComunication extends AsyncTask<ApiCommParam, Void, ApiCommResult> {

        @Override
        protected ApiCommResult doInBackground(ApiCommParam... params) {
            try {
                if(params[0].function == 0){
                    //Image
                    return getBitmapFromURL(params[0].page + "?" + params[0].getParams[0].name + "=" + params[0].getParams[0].value);
                }else if(params[0].function == 1){
                    return null;
                }else if(params[0].function == 2){
                    return null;
                }else if(params[0].function == 3){
                    return null;
                }else if(params[0].function == 4){
                    //Check
                    return Check(params[0]);
                }else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(ApiCommResult result) {
            waitDialog.dismiss();
            switch(result.type) {
                case 0:
                    if(result != null) {
                        Log.i("lol", result.result);
                        bmp = BitmapFactory.decodeFile(result.result);
                        if(bmp != null & img != null) {
                            img.setImageBitmap(bmp);
                        } else if(img == null) Log.i("lol", "img = null");
                        else {
                            Log.i("elo", result.result);
                        }
                        //saveImageToSD(result.bmp);
                    }
                    break;

                case 1:

                    break;

                case 2:

                    break;

                case 3:

                    break;

                case 4:
                    if (result.result.equals("0")) Vars.StartDownloadActivity = true;
                    else if (result.result.equals("1")) errorDialog.show(getString(R.string.NoVideo), getApplicationContext());
                    else errorDialog.show(getString(R.string.ErrorNetwork), getApplicationContext());
                    break;
            }
        }
    }

    public ApiCommResult Check(ApiCommParam params){

        /* SSL problems */
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);


        /* Download content */
        String urlString = params.page + "?";
        try {
            if (params != null) {
                for (int i = 0; i < params.getParams.length; i++) {
                    if (params.getParams[i].name != null & params.getParams[i].value != null) {
                        urlString += params.getParams[i].name + "=" + params.getParams[i].value + "&";
                    }
                }
            }
        }catch(Exception e){
            throw e;
        }

        Log.w("HttpMgr", urlString);

        URLConnection con = null;
        String inputLine = null;

        try{
            URL url = new URL(urlString);
            con = (URLConnection) url.openConnection();
            con.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            ApiCommResult Result = new ApiCommResult(ApiCommResult.CHECK, response.toString());
            Log.i("onClick", "Return result");
            return Result;
        }catch(Exception e){
            Log.e("Exception", e.toString());
            return null;
        }
    }

    public class ApiCommResult{
        public static final int IMAGE = 0;
        public static final int TITLE = 1;
        public static final int FORMATS = 2;
        public static final int VIDEO = 3;
        public static final int CHECK = 4;

        public int type;
        public String result;
        public String path;
        public String name;
        public int format;
        public Bitmap bmp;

        ApiCommResult(int type, String result){
            this.type = type;
            this.result = result;
        }

        ApiCommResult(int type, String path, String name){
            this.type = type;
            this.path = path;
            this.name = name;

        }

        ApiCommResult(int type, String path, String name, int format){
            this.type = type;
            this.path = path;
            this.name = name;
            this.format = format;
        }

        ApiCommResult(int type, Bitmap bmp){
            this.bmp = bmp;
        }
    }

    public static class ApiCommType{
        public static final int IMAGE = 0;
        public static final int TITLE = 1;
        public static final int FORMATS = 2;
        public static final int VIDEO = 3;
        public static final int CHECK = 4;
    }

    public class ApiCommParam{
        GetParam getParams[];
        String page;
        int function;
        ApiCommParam(GetParam[] getParams, String page, int function){
            this.getParams = getParams;
            this.page = page;
            this.function = function;
        }
    }

    public class GetParam{

        public String name;
        public String value;

        public GetParam(String name, String value){
            this.name = name;
            this.value = value;
        }
    }

    public class WaitDialog {
        ProgressDialog progressBar;

        public void show(String msg, Context c){
            progressBar = new ProgressDialog(c);
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

        public void show(String message, Context c){
            Toast.makeText(c, message, Toast.LENGTH_LONG).show();
        }
    }

    public ApiCommResult getBitmapFromURL(String urls){
        Log.i("lol", urls);

        try {
            String outputName = Vars.ImgPath + File.separator + Vars.Title  + ".jpg";
            URL url = new URL(urls);
            InputStream input = url.openConnection().getInputStream();
            FileOutputStream output = new FileOutputStream(new File(outputName));
            int read;
            byte[] data = new byte[32000];
            while ((read = input.read(data)) != -1) output.write(data, 0, read);
            output.close();
            input.close();

            return new ApiCommResult(ApiCommResult.IMAGE, outputName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

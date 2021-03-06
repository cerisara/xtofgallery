package fr.xtof.xtofgallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.provider.MediaStore;
import android.database.Cursor;

import java.net.Socket;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import android.net.Uri;

public class XtofGallery extends FragmentActivity {
    public static Context ctxt;
    public static XtofGallery main;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ctxt=this;
        main=this;

        Intent in = this.getIntent();
        System.out.println("ONCREATE INTENT "+in.toString());
        if (in.getAction().equals(Intent.ACTION_SEND)) sendPicture(in);
    }

    private void msg(final String s) {
        main.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(main, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendPicture(Intent in) {
        /*
        Uri uri = (Uri)in.getParcelableExtra(Intent.EXTRA_STREAM);
        System.out.println("DETSONFILE "+uri.toString());
        if (uri != null && uri.toString().startsWith("file://")) {
          String s = uri.getPath();
          main.msg("Sending"+s.length());
          putfile(s);
          */
        Uri uri = in.getData();
        if (uri==null) {
          Bundle bundle = in.getExtras();
          uri = (Uri)bundle.get(Intent.EXTRA_STREAM);
          System.out.println("DETSONURINULL "+uri);
        }
        {
          /*
          String s = uri.getPath();
          if (s!=null) {
            main.msg("Sending "+s);
            putfile(s);
          } else {
              main.msg("WARNING: nothing to share");
          }
        } else {
          */
          System.out.println("DETSONURIFIN "+uri);
          String[] filePathColumn = { MediaStore.Images.Media.DATA };
          // Get the cursor
          Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
          // Move to first row
          cursor.moveToFirst();
          int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
          String s = cursor.getString(columnIndex);
          cursor.close();
          if (s!=null) {
            main.msg("Sending Photo "+s);
            putfile(s);
          } else {
              main.msg("WARNING: cant get photo");
          }
        }
    }

    @Override
    public void onNewIntent(Intent in) {
        System.out.println("ONNEWINTENT "+in.toString());
        if (in.getAction().equals(Intent.ACTION_SEND)) sendPicture(in);
    }

    public void putfile(String s) {
        try {
            // s=URLEncoder.encode(s,"UTF-8");
            DetProgressTask dett = new DetProgressTask(s);
            dett.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DetProgressTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog = new ProgressDialog(ctxt);
        final String tgt="192.168.1.95";
        final int port=9999;
        String imgfich = null;

        public DetProgressTask(String f) {
          imgfich = f;
          // TODO: check it's an image + check its file size
        }

        private void connect() {
          try {
            Socket socket = new Socket(tgt, port);
            File file = new File(imgfich);
            // Get the size of the file
            long length = file.length();
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new FileInputStream(file);
            OutputStream out = socket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            out.close();
            in.close();
            socket.close();
          } catch (Exception e) {
            main.msg("error "+e.toString());
          }
        }

        /** progress dialog to show user that the backup is processing. */
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected Boolean doInBackground(final String... args) {
            try {
                connect();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (success) {
                main.msg("Photo transferred !");
            } else {
                main.msg("Error postexec");
            }
        }
    }

}

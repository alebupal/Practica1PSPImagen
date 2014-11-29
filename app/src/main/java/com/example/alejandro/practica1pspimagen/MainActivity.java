package com.example.alejandro.practica1pspimagen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends Activity {

    private EditText etWeb, etNombre;
    private RadioButton rbPublica, rbPrivada;
    private String rutaMovil;
    private ImageView iv;
    private ProgressDialog dialogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etWeb = (EditText) findViewById(R.id.etWeb);
        etNombre = (EditText) findViewById(R.id.etNombre);
        rbPublica = (RadioButton) findViewById(R.id.rbPublica);
        rbPrivada = (RadioButton) findViewById(R.id.rbPrivada);
        iv = (ImageView)findViewById(R.id.ivImagen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void guardar(View v) {
        if(etWeb.getText().toString().compareToIgnoreCase("")!=0){
            HiloFacil hf = new HiloFacil();
            hf.execute();
        }else{
            Toast.makeText(this, "Introduzca una URL", Toast.LENGTH_SHORT).show();
        }
    }


    class HiloFacil extends AsyncTask<Object, Integer, String> {

        HiloFacil(String... p) {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialogo = new ProgressDialog(MainActivity.this);
            dialogo.setMessage("Bajando imagen");
            dialogo.show();
        }

        @Override
        protected String doInBackground(Object[] params) {
            try {
                String web = etWeb.getText().toString();
                URL url = new URL(web);

                String nombreImg = comprobarNombre(web);
                Log.v("nombre",nombreImg);

                String extension = obtenerExtension(nombreImg);
                Log.v("extension",extension);

                URLConnection urlCon = url.openConnection();


                if(extension.equals("jpg")||extension.equals("png")||extension.equals("gif")){
                    InputStream is = urlCon.getInputStream();

                    if (rbPrivada.isChecked()) {
                        rutaMovil = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() +"/"+nombreImg;
                        Log.v("rutaPrivada: ", rutaMovil);
                    } else if(rbPublica.isChecked()){
                        rutaMovil = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()+"/"+ nombreImg;
                        Log.v("RutaPublica: ",rutaMovil);
                    }
                    FileOutputStream fos = new FileOutputStream(rutaMovil);

                    byte[] array = new byte[1000];
                    int leido = is.read(array);
                    while (leido > 0) {
                        fos.write(array, 0, leido);
                        leido = is.read(array);
                    }
                    is.close();
                    fos.close();

                }else{
                    Log.v("Extension: ", "Error en el tipo de extensión");
                }

            } catch (Exception e) {
                Log.v("LOG: ",e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {

            File imgFile = new  File(rutaMovil);

            iv.setImageURI(Uri.fromFile(imgFile));


           // iv.setImageURI(Uri.fromFile(new File(rutaMovil)));
            //o también
            //iv.setImageURI(Uri.parse(rutaMovil));

            super.onPostExecute(s);
            dialogo.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }
    }

    public String comprobarNombre(String web){
        String nombreImg;
        String[] elementosWeb = web.split("/");

        if(etNombre.getText().toString().isEmpty() || etNombre.getText().toString()==null  ||  etNombre.getText().toString().trim().length()==0){
             //se guarda un array los elementos separados por /
            nombreImg = elementosWeb[elementosWeb.length-1]; //obtengo el ultimo elemento del array que contiene el nombre de la imagen en Internet
        }else{
            String extension=obtenerExtension(elementosWeb[elementosWeb.length-1]);
            nombreImg = etNombre.getText().toString()+"."+extension;
        }

        return nombreImg;


    }

    public String obtenerExtension(String nombreImg){

        String extension = nombreImg.substring(nombreImg.length()-3);
        return extension;
    }



}

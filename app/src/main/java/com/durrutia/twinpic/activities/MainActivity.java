package com.durrutia.twinpic.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.durrutia.twinpic.Database;
import com.durrutia.twinpic.R;
import com.durrutia.twinpic.domain.Pic;
import com.durrutia.twinpic.domain.Pic_Table;
import com.durrutia.twinpic.domain.Twin;
import com.durrutia.twinpic.domain.Twin_Table;
import com.durrutia.twinpic.util.CustomAdapter;
import com.durrutia.twinpic.util.DeviceUtils;
import com.google.android.gms.location.LocationListener;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainActivity extends AppCompatActivity implements LocationListener {

    @BindView(R.id.listView)
    ListView lv;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private final String ruta_fotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/misfotos/";
    private LocationManager locationManager;
    private String provider;
    private Double longitude; //Longitud del planeta.
    private Double latitude; //Latitud del planeta.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        //getBaseContext().deleteDatabase(Database.NAME + ".db");
        FlowManager.init(new FlowConfig.Builder(this.getBaseContext()).openDatabasesOnInit(true).build());

        if(SQLite.select().from(Pic.class).queryList().size() == 0){

            Log.d("Mensaje","Poblando base de datos");
            poblarBD(this.getBaseContext());

        }

        activateGPS();
        loadImages();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Mensaje","Tomando foto");
                Snackbar.make(view, "Ejecutando cámara de fotos", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                takePhoto();

            }
        });

    }

    private void activateGPS(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria,false);

        try{

            Location location = locationManager.getLastKnownLocation(provider);

            if(location != null){

                Log.d("GPS","Localización obtenida");
                onLocationChanged(location);

            }else{

                Log.d("GPS","No se pudo obtener localización");
                longitude = 0.0;
                latitude = 0.0;

            }

        }catch(SecurityException e){

            Log.d("Error","Security Exception");

        }

    }

    public static void poblarBD(final Context context){

        Log.d("Aviso","Poblando BD");
        String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/Camera";

        File path = new File(ruta);
        String[] fileNames = path.list();

        Log.d("Ruta",path.toString());

        for(int i=0; i<50; i++){

            Log.d("Dato",ruta + "/" + fileNames[i]);
            final Pic pic = Pic.builder()
                    .deviceId(DeviceUtils.getDeviceId(context)+ RandomStringUtils.randomAlphabetic(20))
                    .latitude(RandomUtils.nextDouble())
                    .longitude(RandomUtils.nextDouble())
                    .date(new Date().getTime())
                    .url(ruta+"/"+fileNames[i])
                    .positive(RandomUtils.nextInt(0, 100))
                    .negative(RandomUtils.nextInt(0, 100))
                    .warning(RandomUtils.nextInt(0, 2))
                    .build();
            pic.save();

        }

    }

    public void onLocationChanged(Location l){

        longitude =  l.getLongitude();
        latitude = l.getLatitude();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    private void takePhoto(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            cameraLatestVersion();

        }else{

            cameraOldVersion();

        }

    }

    private void cameraOldVersion(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            storePhoto(data);

        }

    }

    private void storePhoto(Intent data){

        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        File myDir = new File(ruta_fotos);
        myDir.mkdirs();
        String fname = getCode() + ".bmp";
        File file = new File (myDir, fname);

        if (file.exists ()) file.delete ();

        try {

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        final Pic newPic = Pic.builder()
                    .deviceId(DeviceUtils.getDeviceId(this.getBaseContext()))
                    .latitude(latitude)
                    .longitude(longitude)
                    .date(new Date().getTime())
                    .url(myDir + "/" +fname)
                    .positive(0)
                    .negative(0)
                    .warning(0).build();

        log.debug("{}",newPic);
        newPic.save();
        //d
        List<Pic> remotePics = SQLite.select().from(Pic.class).where(Pic_Table.deviceId.notLike(DeviceUtils.getDeviceId(this.getBaseContext()))).queryList();
        List<Twin> twins = SQLite.select().from(Twin.class).queryList();

        if(twins.isEmpty()){

            int randomIndex = RandomUtils.nextInt(0,remotePics.size()-1);
            Pic remotePic = remotePics.get(randomIndex);
            Twin newTwin = Twin.builder().local(newPic).remote(remotePic).build();
            newTwin.save();
            loadImages();
            return;

        }else{

            List<Pic> myRemotePics = new ArrayList<Pic>();

            for(int i=0; i<twins.size(); i++){

                if(twins.get(i).getLocal().getDeviceId().equals(DeviceUtils.getDeviceId(this.getBaseContext()))){

                    myRemotePics.add(twins.get(i).getRemote());

                }
            }

            for(int i=0; i<remotePics.size(); i++){

                boolean b = true;
                int j = 0;

                while((j < myRemotePics.size()) && b ){

                    if(remotePics.get(i).getId() == myRemotePics.get(j).getId()){

                       b = false;

                    }

                    j++;

                }

                if(b){

                    Pic remotePic = remotePics.get(i);
                    Twin newTwin = Twin.builder().local(newPic).remote(remotePic).build();
                    newTwin.save();
                    loadImages();
                    return;

                }

            }

        }

        Toast.makeText(MainActivity.this,"No se pudo subir la imagen, es probable que no existan imagenes remotas " +
                "o ya se han asignado todas a su cuenta", Toast.LENGTH_LONG).show();

    }

    private void cameraLatestVersion(){

        Log.d("Mensaje","API mayor o igual a 23");
        Log.d("Versión",Integer.toString(Build.VERSION.SDK_INT));

    }

    public void loadImages(){

        List<Twin> twins = SQLite.select().from(Twin.class).queryList();
        Collections.reverse(twins);

        if(lv.getAdapter() == null){

            lv.setAdapter(new CustomAdapter(this,twins.toArray(new Twin[0])));

        }else{

            CustomAdapter a = (CustomAdapter)lv.getAdapter();
            a.update(twins.toArray(new Twin[0]));

        }

    }

    private String getCode(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date() );
        String photoCode = "pic_" + date;
        return photoCode;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.refresh:

                Log.d("Mensaje","Actualizando");
                loadImages();
                return true;

            case R.id.camera:

                Log.d("Mensaje","Tomando foto");
                takePhoto();
                return true;

            default:

                Log.d("Mensaje","Click en ningún botón");
                return super.onOptionsItemSelected(item);

        }
    }

}

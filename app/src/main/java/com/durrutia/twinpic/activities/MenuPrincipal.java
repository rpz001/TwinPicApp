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

import com.durrutia.twinpic.R;
import com.durrutia.twinpic.domain.Pic;
import com.durrutia.twinpic.domain.Pic_Table;
import com.durrutia.twinpic.domain.Twin;
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

/**
 * MenuPrincipal: Representa a la primera activity que ve el usuario. Se muestra la lista de Twins
 * que esta asociada al dispositivo del usuario y los botones para tomar fotos (y refrescar).
 */
@Slf4j
public class MenuPrincipal extends AppCompatActivity implements LocationListener {

    /**
     * Listview donde se guardan los layouts que representa la fila de una Twin.
     */
    @BindView(R.id.listView)
    ListView lv;

    /**
     * Barra de herramientas personalizada.
     */
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * Constante que representa la captura o no captura de la foto.
     */
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * Ruta donde se almacenan las fotos tomadas. (Tarjeta SD).
     */
    private final String ruta_fotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/misfotos/";

    /**
     * Administrador de localizacion. Permite obtener las coordenadas en que se encuentra el celular.
     */
    private LocationManager locationManager;

    /**
     *
     */
    private String provider;

    /**
     * Localización en meridiano en el planeta.
     */
    private Double longitude;

    /**
     * Localización en paralelo en el planeta.
     */
    private Double latitude; //Latitud del planeta.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Cargando la interfaz grafica de la aplicación.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //getBaseContext().deleteDatabase(Database.NAME + ".db"); Se borra la base de datos.

        //Inicializando la base de datos.
        FlowManager.init(new FlowConfig.Builder(this.getBaseContext()).openDatabasesOnInit(true).build());

        //Si es la primera vez que se usa la aplicación, no tiene fotos.
        if (SQLite.select().from(Pic.class).queryList().size() == 0) {

            Log.d("Mensaje", "Poblando base de datos");
            poblarBD(this.getBaseContext()); //Se llena con Pics de prueba.

        }

        //Se activa el GPS y se obtienen las coordenadas.
        activateGPS();

        //Se carga el listview "lv" las Twins que encuentre en la base de datos.
        loadImages();

        //El boton flotante se configura para que pueda tomar una foto.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Mensaje", "Tomando foto");
                Snackbar.make(view, "Ejecutando cámara de fotos", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                takePhoto();

            }
        });

    }

    /**
     * Método que se encarga de capturar las coordenadas terrestres en que se encuentra el celular.
     */
    private void activateGPS() {

        //Se configura el administrador de localizacion.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        try {

            Location location = locationManager.getLastKnownLocation(provider);

            //Si se pudo obtener la localizacion, se actualiza  el anterior.
            if (location != null) {

                Log.d("GPS", "Localización obtenida");
                onLocationChanged(location);

            } else { //De lo contrario, se lo deja como indeterminado.

                Log.d("GPS", "No se pudo obtener localización");
                longitude = 0.0;
                latitude = 0.0;

            }

        } catch (SecurityException e) {

            Log.d("Error", "Security Exception");

        }

    }

    /**
     * Método que se encarga de poblar la tabla Pic con fotos de prueba, obtenidas del disco duro del
     * celular. Ideal para la primera vez que se use la aplicación.
     *
     * @param context
     */
    public static void poblarBD(final Context context) {

        Log.d("Aviso", "Poblando BD");

        //Se obtiene las rutas de todas las fotos que encuentre en el disco duro.
        String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera";
        File path = new File(ruta);
        String[] fileNames = path.list();

        Log.d("Ruta", path.toString());

        //Se eligen las fotos encontradas en el disco duro, se crea la Pic y se agrega a la BD.
        for (int i = 0; i < fileNames.length; i++) {

            Log.d("Dato", ruta + "/" + fileNames[i]);
            final Pic pic = Pic.builder()
                    .deviceId(DeviceUtils.getDeviceId(context) + RandomStringUtils.randomAlphabetic(20))
                    .latitude(RandomUtils.nextDouble(0, 90))
                    .longitude(RandomUtils.nextDouble(0, 90))
                    .date(new Date())
                    .url(ruta + "/" + fileNames[i])
                    .positive(RandomUtils.nextInt(0, 100))
                    .negative(RandomUtils.nextInt(0, 100))
                    .warning(RandomUtils.nextInt(0, 2))
                    .build();
            pic.save(); //Se agrega la Pic a la base de datos.

        }

    }

    /**
     * Metodo que permite actualizar la nueva localizacion del celular.
     *
     * @param l
     */
    public void onLocationChanged(Location l) {

        longitude = l.getLongitude();
        latitude = l.getLatitude();

    }

    /**
     * Metodo que permite agregar el layout personalizado al menu.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    /**
     * Metodo que permite tomar una foto.
     */
    private void takePhoto() {

        //Si la version de la API de Android es a partir de MarshMallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            cameraLatestVersion();

        } else { //De lo contrario, la API de Android es a partir de Lollipop hacia abajo.

            cameraOldVersion();

        }

    }

    /**
     * Método que toma la foto para versiones antiguas (API inferior a Lollipop). Debido a permisos
     * diferentes.
     */
    private void cameraOldVersion() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Se llama a la cámara.

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }

    }

    /**
     * Método que analiza el tipo de accion que se hizo con la camara de fotos.
     *
     * @param requestCode //El tipo de servicio usado en la camara.
     * @param resultCode  //El resultado del servicio de la camara.
     * @param data        //La foto obtenida.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Si la camara tomo una foto y el usuario acepto guardarla.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            storePhoto(data);

        }

    }

    /**
     * Metodo que permite almacenar la foto en la BD y en la carpeta temporal de la tarjeta SD.
     *
     * @param data
     */
    private void storePhoto(Intent data) {

        /**
         * Obteniendo la foto que se tomo de la camara y se convierte a un archivo en que se guarda en
         * la tarjeta SD.
         */
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        File myDir = new File(ruta_fotos);
        myDir.mkdirs();
        String fname = getCode() + ".bmp";
        File file = new File(myDir, fname);

        if (file.exists()) file.delete(); //Si la foto ya existe, se borra para remplazarla.

        try {

            //Se convierte la foto (la definitiva) en formato JPG y se guarda en la tarjeta SD.
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

        //Se crea una nueva Pic.
        final Pic newPic = Pic.builder()
                .deviceId(DeviceUtils.getDeviceId(this.getBaseContext()))
                .latitude(latitude)
                .longitude(longitude)
                .date(new Date())
                .url(myDir + "/" + fname)
                .positive(0)
                .negative(0)
                .warning(0).build();

        log.debug("{}", newPic);
        newPic.save(); //Se guarda en al base de datos.

        //Se obtienen todas las fotos remotas (las que no son mias).
        List<Pic> remotePics = SQLite.select().from(Pic.class).where(Pic_Table.deviceId.notLike(DeviceUtils.getDeviceId(this.getBaseContext()))).queryList();

        //Se obtienen todas las Twins.
        List<Twin> twins = SQLite.select().from(Twin.class).queryList();

        //Si no hay Twins, significa que soy el primer usuario en usar la aplicacion.
        if (twins.isEmpty()) {

            //Se selecciona cualquier foto de las Pics de prueba de forma aleatoria.
            int randomIndex = RandomUtils.nextInt(0, remotePics.size() - 1);
            Pic remotePic = remotePics.get(randomIndex);

            //Se crea la Twin, asociando mi foto con la foto remota, se guarda y se refresca el listview.
            Twin newTwin = Twin.builder().local(newPic).remote(remotePic).build();
            newTwin.save();
            loadImages();
            return;

        } else { //De lo contrario, se elige una Pic que no sea la que yo tomé ni tampoco la que me dieron para mi Twin.

            List<Pic> myRemotePics = new ArrayList<Pic>(); //Lista que almacena todas las Pics que he tomado.

            for (int i = 0; i < twins.size(); i++) {

                /**
                 * De la lista de todas las Twins del sistema, selecciono las que son mias y de ellas, guardo las Pics remotas
                 * en la lista de mis pics que he tomado.
                 */
                if (twins.get(i).getLocal().getDeviceId().equals(DeviceUtils.getDeviceId(this.getBaseContext()))) {

                    myRemotePics.add(twins.get(i).getRemote());

                }
            }

            //Lista que guarda las Pics no repetidas (las que no he tomado).
            List<Pic> picsNoRepetidas = new ArrayList<Pic>();

            //De todas las pics del sistema, elijo las que no sean las que yo tome.

            //Recorriendo la lista de todas las fotos del sistema.
            for (int i = 0; i < remotePics.size(); i++) {

                boolean b = true;
                int j = 0;

                //Recorriendo la lista de mis fotos.
                while ((j < myRemotePics.size()) && b) {

                    //Si la foto esta, entonces la foto i es una que yo tome.
                    if (remotePics.get(i).getId() == myRemotePics.get(j).getId()) {

                        b = false;

                    }

                    j++;

                }

                //Si la foto no esta repetida (no es mia), la selecciono como candidata para la Twin.
                if (b) {

                    picsNoRepetidas.add(remotePics.get(i));

                }

            }

            //Si no existen fotos candidatas.
            if (picsNoRepetidas.isEmpty()) {

                Toast.makeText(MenuPrincipal.this, "No se pudo subir la imagen, es probable que no existan imagenes remotas " +
                        "o ya se han asignado todas a su cuenta", Toast.LENGTH_LONG).show();

            } else { //De lo contrario, se selecciona de manera aleatoria.

                Pic remotePic = picsNoRepetidas.get(RandomUtils.nextInt(0, picsNoRepetidas.size() - 1));
                Twin newTwin = Twin.builder().local(newPic).remote(remotePic).build();
                newTwin.save();
                loadImages();
                return;

            }

        }

    }

    /**
     * Método que permite obtener fotos para las API superiores o iguales a Marshmallow.
     */
    private void cameraLatestVersion() {

        Log.d("Mensaje", "API mayor o igual a 23");
        Log.d("Versión", Integer.toString(Build.VERSION.SDK_INT));

    }

    /**
     * Método que se encarga de llenar la listview con las Twins (representacion grafica)
     */
    public void loadImages() {

        List<Twin> twinsAux = SQLite.select().from(Twin.class).queryList();
        List<Twin> twins = new ArrayList<Twin>();

        for (int i = 0; i < twinsAux.size(); i++) {

            if (twinsAux.get(i).getLocal().getDeviceId().equals(DeviceUtils.getDeviceId(this.getBaseContext()))) {

                twins.add(twinsAux.get(i));

            }

        }

        Collections.reverse(twins); //La lista de twins se muestra desde la mas nueva hasta la mas vieja.

        if (lv.getAdapter() == null) { //Si no se ha llenado, se agrega el adaptador.

            lv.setAdapter(new CustomAdapter(this, twins.toArray(new Twin[0])));

        } else { //Si ya se habia llenado, solo falta actualizar con la nueva Twin.

            CustomAdapter a = (CustomAdapter) lv.getAdapter();
            a.update(twins.toArray(new Twin[0]));

        }

    }

    /**
     * Método que permite darle un nombre (formato fecha) a la foto que se guarda.
     *
     * @return
     */
    private String getCode() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());
        String photoCode = "pic_" + date;
        return photoCode;

    }

    /**
     * Método que implementa los eventos de los botones del menu superior.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            //Actualizacion.
            case R.id.refresh:

                Log.d("Mensaje", "Actualizando");
                loadImages();
                return true;

            //Toma de fotos.
            case R.id.camera:

                Log.d("Mensaje", "Tomando foto");
                takePhoto();
                return true;

            default:

                Log.d("Mensaje", "Click en ningún botón");
                return super.onOptionsItemSelected(item);

        }
    }

}

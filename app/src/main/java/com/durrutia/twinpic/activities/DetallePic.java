package com.durrutia.twinpic.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.durrutia.twinpic.R;
import com.durrutia.twinpic.domain.Pic;
import com.durrutia.twinpic.domain.PicReportada;
import com.durrutia.twinpic.domain.Pic_Table;
import com.durrutia.twinpic.domain.Twin;
import com.durrutia.twinpic.domain.Twin_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.io.File;
import java.util.List;

/**
 * DetallePic: Representa a la activity (ventana) que se obtiene al hacer click en algún Pic. Muestra
 * todos los detalles de la foto.
 * @author Rodrigo Alejandro Pizarro Zapata.
 */
public class DetallePic extends AppCompatActivity {

    /**
     * Twin de donde se saco la Pic.
     */
    Twin twin;

    /**
     * Barra de herrmientas superior.
     */
    @BindView(R.id.toolbar2)
    Toolbar toolBar;

    /**
     * ImageView donde se muestra la foto de la Pic.
     */
    @BindView(R.id.imageView2)
    ImageView imageView;

    /**
     * Textview donde se muestra el ID de la Pic.
     */
    @BindView(R.id.textView3)
    TextView textViewID;

    /**
     * Textview donde se muestra la fecha que se tomo la Pic.
     */
    @BindView(R.id.textView4)
    TextView textViewDate;

    /**
     * Textview donde se muestra la posicion en meridiano del celular al momento de tomar la foto.
     */
    @BindView(R.id.textView5)
    TextView textViewLongitude;

    /**
     * Textview donde se muestra la posicion en paralelo del celular al momento de tomar la foto.
     */
    @BindView(R.id.textView6)
    TextView textViewLatitude;

    /**
     * Textview donde se muestra la cantidad de likes.
     */
    @BindView(R.id.textView7)
    TextView textViewLikes;

    /**
     * Textview donde se muestra la cantidad de dislikes.
     */
    @BindView(R.id.textView8)
    TextView textViewDislikes;

    /**
     * Textview donde se muestra la cantidad de warnings.
     */
    @BindView(R.id.textView9)
    TextView textViewWarnings;

    /**
     * Boton que sirve para dar likes.
     */
    @BindView(R.id.buttonLike)
    ImageButton buttonLikes;

    /**
     * Boton que sirve para dar dislikes.
     */
    @BindView(R.id.buttonDislike)
    ImageButton buttonDislikes;

    /**
     * Boton que sirve para dar warnings.
     */
    @BindView(R.id.buttonWarning)
    ImageButton buttonWarnings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Se carga la interfaz grafica.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);
        toolBar.setTitle(getIntent().getExtras().getString("titleBar"));

        //Se obtiene la imagen y se ajusta en la activity.
        String urlImage = getIntent().getExtras().getString("url");
        Uri uri = Uri.fromFile(new File(urlImage));
        Picasso.with(getBaseContext()).load(uri).resize(600,600).centerCrop().into(imageView);

        //Se cargan los detalles de las pics para desplegarlas.
        picDescription();

        //Si la pic es remota, se habilita los botones para calificarla.
        if(getIntent().getExtras().getString("type").equals("remote")){

            setButtons();

        }

    }

    /**
     *  Metodo que permite crear los botones calificadores (Like, Dislike, Warning).
     */
    private void setButtons(){

        List<Twin> lt = SQLite.select().from(Twin.class).where(Twin_Table.remote_id.eq(getIntent().getExtras().getLong("id"))).queryList();

        for(int i=0; i<lt.size(); i++){

            if(lt.get(i).getLocal().getDeviceId().equals(getIntent().getExtras().getString("deviceId"))){

                twin = lt.get(i);

            }
        }

        Log.d("Twin",twin.toString());

        //Evento cuando se da like.
        buttonLikes.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                //Si no se ha dado likes o dislikes, es la primera calificacion.
                if(!twin.isDioLike() && !twin.isDioDislike()){

                    Toast.makeText(DetallePic.this,"+1 Like", Toast.LENGTH_LONG).show();
                    Pic p = SQLite.select().from(Pic.class).where(Pic_Table.id.eq(twin.getRemote().getId())).queryList().get(0);
                    Integer cantLikes = p.getPositive() + 1;
                    p.setPositive(cantLikes);
                    twin.setDioLike(true);
                    twin.setDioDislike(false);
                    p.update();
                    twin.update();
                    textViewLikes.setText("Me gusta: " +cantLikes.toString());

                }else{ //De lo contrario, se analiza el caso.

                    //Si antes dio dislike, se puede dar like y borrar el dislike.
                    if(!twin.isDioLike() && twin.isDioDislike()){

                        Toast.makeText(DetallePic.this,"+1 Like", Toast.LENGTH_LONG).show();
                        Pic p = SQLite.select().from(Pic.class).where(Pic_Table.id.eq(twin.getRemote().getId())).queryList().get(0);
                        Integer cantLikes = p.getPositive() + 1;
                        Integer cantDislikes = p.getNegative() -1;
                        p.setPositive(cantLikes);
                        p.setNegative(cantDislikes);
                        p.update();
                        twin.setDioLike(true);
                        twin.setDioDislike(false);
                        twin.update();
                        textViewLikes.setText("Me gusta: " +cantLikes.toString());
                        textViewDislikes.setText("No me gusta: " +cantDislikes.toString());

                    }else{ //De lo contrario, no se permite dar mas de un like.

                        Toast.makeText(DetallePic.this,"No se puede volver a dar +1 like.", Toast.LENGTH_LONG).show();

                    }
                }

            }

        });

        //Evento cuando se da dislike.
        buttonDislikes.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                //Si no se ha dado likes o dislikes, es la primera calificacion.
                if(!twin.isDioLike() && !twin.isDioDislike()){

                    Toast.makeText(DetallePic.this,"+1 dislike", Toast.LENGTH_LONG).show();
                    Pic p = SQLite.select().from(Pic.class).where(Pic_Table.id.eq(twin.getRemote().getId())).queryList().get(0);
                    Integer cantDislikes = p.getNegative() + 1;
                    p.setNegative(cantDislikes);
                    p.update();
                    twin.setDioDislike(true);
                    twin.setDioLike(false);
                    twin.update();
                    textViewDislikes.setText("No me gusta: " +cantDislikes.toString());

                }else{ //De lo contrario, se analiza el caso.

                    //Si antes dio like, se puede dar dislike y borrar el like.

                    if(twin.isDioLike() && !twin.isDioDislike()){

                        Toast.makeText(DetallePic.this,"+1 dislike", Toast.LENGTH_LONG).show();
                        Pic p = SQLite.select().from(Pic.class).where(Pic_Table.id.eq(twin.getRemote().getId())).queryList().get(0);
                        Integer cantDislikes = p.getNegative() + 1;
                        Integer cantLikes = p.getPositive() - 1;
                        p.setPositive(cantLikes);
                        p.setNegative(cantDislikes);
                        p.update();
                        twin.setDioDislike(true);
                        twin.setDioLike(false);
                        twin.update();
                        textViewLikes.setText("Me gusta: " +cantLikes.toString());
                        textViewDislikes.setText("No me gusta: " +cantDislikes.toString());

                    }else{ //De lo contrario, no se permite dar mas de un dislike.

                        Toast.makeText(DetallePic.this,"No se puede volver a dar +1 dislike.", Toast.LENGTH_LONG).show();

                    }
                }

            }

        });

        //Evento cuando se da Warning.
        buttonWarnings.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                //No se permite dar mas de un warning.
                if(twin.isDioWarning()){

                    Toast.makeText(DetallePic.this,"Ya dió +1 warning",Toast.LENGTH_LONG).show();

                }else{ //Si no ha dado antes warning, se puede dar ahora.

                    Pic p = SQLite.select().from(Pic.class).where(Pic_Table.id.eq(twin.getRemote().getId())).queryList().get(0);
                    Integer cantWarnings = p.getWarning() + 1;
                    p.setWarning(cantWarnings);
                    p.update();
                    twin.setDioWarning(true);
                    textViewWarnings.setText("Advertencias: " +cantWarnings.toString());

                    //Si esta Pic tiene mas de 2 advertencias, se reporta como inapropiada.
                    if(p.getWarning() > 2){

                        PicReportada pr = PicReportada.builder().picReportada(p).build();
                        pr.save();
                        Toast.makeText(DetallePic.this,"+1 Warning y enviada a la lista negra", Toast.LENGTH_LONG).show();

                    }else{

                        Toast.makeText(DetallePic.this,"+1 Warning", Toast.LENGTH_LONG).show();

                    }

                }



            }

        });

    }

    /**
     * Método que desactiva los botones en caso de que la Pic sea local.
     */
    private void setLocal(){

        buttonLikes.setVisibility(View.GONE);
        buttonDislikes.setVisibility(View.GONE);
        buttonWarnings.setVisibility(View.GONE);

    }

    /**
     * Método que despliega información de la Pic en la activity.
     */
    private void picDescription(){

        Long picID = getIntent().getExtras().getLong("id");
        String picDate = getIntent().getExtras().getString("date");
        Double picLongitude = getIntent().getExtras().getDouble("longitude");
        Double picLatitude = getIntent().getExtras().getDouble("latitude");
        Integer picLikes = getIntent().getExtras().getInt("likes");
        Integer picDislikes = getIntent().getExtras().getInt("dislikes");
        Integer picWarnings = getIntent().getExtras().getInt("warnings");

        textViewID.setText("ID: " +picID);
        textViewDate.setText("Fecha tomada: " +picDate);
        textViewLongitude.setText("Longitud: " +picLongitude.toString().substring(0,6));
        textViewLatitude.setText("Latitud: " +picLatitude.toString().substring(0,6));
        textViewLikes.setText("Me gusta: " +picLikes);
        textViewDislikes.setText("No me gusta: " +picDislikes);
        textViewWarnings.setText("Advertencias: " +picWarnings);

        if(getIntent().getExtras().getString("type").equals("local")) setLocal();

    }

}

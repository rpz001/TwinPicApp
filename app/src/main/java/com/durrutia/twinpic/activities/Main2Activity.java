package com.durrutia.twinpic.activities;

import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.durrutia.twinpic.util.DeviceUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Main2Activity: Representa a la activity (ventana) que se obtiene al hacer click en algún Pic.
 * @author Rodrigo Alejandro Pizarro Zapata.
 */
public class Main2Activity extends AppCompatActivity {

    Twin twin;

    @BindView(R.id.toolbar2)
    Toolbar toolBar;

    @BindView(R.id.imageView2)
    ImageView imageView;

    @BindView(R.id.textView3)
    TextView textViewID;

    @BindView(R.id.textView4)
    TextView textViewDate;

    @BindView(R.id.textView5)
    TextView textViewLongitude;

    @BindView(R.id.textView6)
    TextView textViewLatitude;

    @BindView(R.id.textView7)
    TextView textViewLikes;

    @BindView(R.id.textView8)
    TextView textViewDislikes;

    @BindView(R.id.textView9)
    TextView textViewWarnings;

    @BindView(R.id.buttonLike)
    ImageButton buttonLikes;

    @BindView(R.id.buttonDislike)
    ImageButton buttonDislikes;

    @BindView(R.id.buttonWarning)
    ImageButton buttonWarnings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ButterKnife.bind(this);

        toolBar.setTitle(getIntent().getExtras().getString("titleBar"));

        String urlImage = getIntent().getExtras().getString("url");
        Uri uri = Uri.fromFile(new File(urlImage));
        Picasso.with(getBaseContext()).load(uri).resize(600,600).centerCrop().into(imageView);
        picDescription();

        if(getIntent().getExtras().getString("type").equals("remote")){

            setButtons();

        }

    }

    /**
     *  Método que permite crear los botones calificadores (Like, Dislike, Warning).
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

                if(!twin.isDioLike() && !twin.isDioDislike()){

                    Toast.makeText(Main2Activity.this,"+1 Like", Toast.LENGTH_LONG).show();
                    Pic p = SQLite.select().from(Pic.class).where(Pic_Table.id.eq(twin.getRemote().getId())).queryList().get(0);
                    Integer cantLikes = p.getPositive() + 1;
                    p.setPositive(cantLikes);
                    twin.setDioLike(true);
                    twin.setDioDislike(false);
                    p.update();
                    twin.update();
                    textViewLikes.setText("Me gusta: " +cantLikes.toString());

                }else{

                    if(!twin.isDioLike() && twin.isDioDislike()){

                        Toast.makeText(Main2Activity.this,"+1 Like", Toast.LENGTH_LONG).show();
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

                    }else{

                        Toast.makeText(Main2Activity.this,"No se puede volver a dar +1 like.", Toast.LENGTH_LONG).show();

                    }
                }

            }

        });

        //Evento cuando se da dislike.
        buttonDislikes.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                if(!twin.isDioLike() && !twin.isDioDislike()){

                    Toast.makeText(Main2Activity.this,"+1 dislike", Toast.LENGTH_LONG).show();
                    Pic p = SQLite.select().from(Pic.class).where(Pic_Table.id.eq(twin.getRemote().getId())).queryList().get(0);
                    Integer cantDislikes = p.getNegative() + 1;
                    p.setNegative(cantDislikes);
                    p.update();
                    twin.setDioDislike(true);
                    twin.setDioLike(false);
                    twin.update();
                    textViewDislikes.setText("No me gusta: " +cantDislikes.toString());

                }else{

                    if(twin.isDioLike() && !twin.isDioDislike()){

                        Toast.makeText(Main2Activity.this,"+1 dislike", Toast.LENGTH_LONG).show();
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

                    }else{

                        Toast.makeText(Main2Activity.this,"No se puede volver a dar +1 dislike.", Toast.LENGTH_LONG).show();

                    }
                }

            }

        });

        //Evento cuando se da Warning.
        buttonWarnings.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                if(twin.isDioWarning()){

                    Toast.makeText(Main2Activity.this,"Ya dió +1 warning",Toast.LENGTH_LONG).show();

                }else{

                    Pic p = SQLite.select().from(Pic.class).where(Pic_Table.id.eq(twin.getRemote().getId())).queryList().get(0);
                    Integer cantWarnings = p.getWarning() + 1;
                    p.setWarning(cantWarnings);
                    p.update();
                    twin.setDioWarning(true);
                    textViewWarnings.setText("Advertencias: " +cantWarnings.toString());

                    if(p.getWarning() > 2){

                        PicReportada pr = PicReportada.builder().picReportada(p).build();
                        pr.save();
                        Toast.makeText(Main2Activity.this,"+1 Warning y enviada a la lista negra", Toast.LENGTH_LONG).show();

                    }else{

                        Toast.makeText(Main2Activity.this,"+1 Warning", Toast.LENGTH_LONG).show();

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

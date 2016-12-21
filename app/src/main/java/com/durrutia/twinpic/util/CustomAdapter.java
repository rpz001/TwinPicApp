package com.durrutia.twinpic.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.durrutia.twinpic.R;
import com.durrutia.twinpic.activities.DetallePic;
import com.durrutia.twinpic.activities.MenuPrincipal;
import com.durrutia.twinpic.domain.Twin;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * CustomAdapter: Adaptador que permite ingresar filas a la listview.
 *
 * @author Rodrigo Alejandro Pizarro Zapata.
 */
public class CustomAdapter extends BaseAdapter {

    /**
     * Contexto de la aplicacion.
     */
    private Context context;

    /**
     * Mecanismo de inflado de XML. (Como se va llenando los layouts).
     */
    private static LayoutInflater inflater = null;

    /**
     * Lista de las Twins asociadas al dispositivo.
     */
    private Twin[] twins;

    /**
     * Activity de donde se incrusta el adaptador.
     */
    private MenuPrincipal _mainActivity;

    /**
     * Constructor de la clase.
     *
     * @param mainActivity La activity en donde se encuentra el adaptador.
     * @param t            Lista de twins que deben llenarse en el adaptador.
     */
    public CustomAdapter(MenuPrincipal mainActivity, Twin[] t) {

        // TODO Auto-generated constructor stub

        context = mainActivity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.twins = t;
        this._mainActivity = mainActivity;

    }

    /**
     * Metodo que obtiene la cantidad de elementos que guarda en adaptador.
     *
     * @return Cantidad de elementos.
     */
    @Override
    public int getCount() {

        // TODO Auto-generated method stub
        return twins.length;

    }

    /**
     * Método que obtiene un item segun la posicion.
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {

        // TODO Auto-generated method stub
        return position;

    }

    /**
     * Metodo que obtiene un item segun su id.
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {

        // TODO Auto-generated method stub
        return position;

    }

    /**
     * Metodo que permite actualizar el adaptador si se detecta una nueva Twin.
     *
     * @param t
     */
    public void update(Twin[] t) {

        twins = t;
        notifyDataSetChanged();

    }

    /**
     * Clase auxiliar o de apoyo.
     */
    public class Holder {

        TextView tv1;
        ImageView img1;
        TextView tv2;
        ImageView img2;

    }

    /**
     * Método que representa al click sobre una Pic local.
     *
     * @param position Posición de la lista.
     * @return
     */
    public OnClickListener getEvent1(final int position) {

        OnClickListener evento = new OnClickListener() {

            public void onClick(View v) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                Intent intent = new Intent(context.getApplicationContext(), DetallePic.class);
                intent.putExtra("titleBar", "Local");
                intent.putExtra("url", twins[position].getLocal().getUrl());
                intent.putExtra("id", twins[position].getLocal().getId());
                intent.putExtra("date", dateFormat.format(twins[position].getLocal().getDate()));
                intent.putExtra("longitude", twins[position].getLocal().getLongitude());
                intent.putExtra("latitude", twins[position].getLocal().getLatitude());
                intent.putExtra("likes", twins[position].getLocal().getPositive());
                intent.putExtra("dislikes", twins[position].getLocal().getNegative());
                intent.putExtra("warnings", twins[position].getLocal().getWarning());
                intent.putExtra("type", "local");
                context.startActivity(intent);

            }
        };

        return evento;

    }

    /**
     * Método que representa al click sobre una Pic remota.
     *
     * @param position Posición de la lista.
     * @return
     */
    public OnClickListener getEvent2(final int position) {

        OnClickListener evento = new OnClickListener() {

            public void onClick(View v) {

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                Intent intent = new Intent(context.getApplicationContext(), DetallePic.class);
                intent.putExtra("titleBar", "Remoto");
                intent.putExtra("url", twins[position].getRemote().getUrl());
                intent.putExtra("id", twins[position].getRemote().getId());
                intent.putExtra("date", dateFormat.format(twins[position].getRemote().getDate()));
                intent.putExtra("longitude", twins[position].getRemote().getLongitude());
                intent.putExtra("latitude", twins[position].getRemote().getLatitude());
                intent.putExtra("likes", twins[position].getRemote().getPositive());
                intent.putExtra("dislikes", twins[position].getRemote().getNegative());
                intent.putExtra("warnings", twins[position].getRemote().getWarning());
                intent.putExtra("type", "remote");
                intent.putExtra("deviceId", DeviceUtils.getDeviceId(_mainActivity.getBaseContext()));
                context.startActivity(intent);

            }
        };

        return evento;

    }

    @Override
    /**
     * Método que permite crear una nueva fila en un ListView.
     */
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.program_list, null);
        holder.tv1 = (TextView) rowView.findViewById(R.id.textView1);
        holder.img1 = (ImageView) rowView.findViewById(R.id.imageView1);
        holder.tv2 = (TextView) rowView.findViewById(R.id.textView2);
        holder.img2 = (ImageView) rowView.findViewById(R.id.imageView2);

        holder.tv1.setText("Id: " + Long.toString(twins[position].getLocal().getId()));
        holder.tv2.setText("Id: " + Long.toString(twins[position].getRemote().getId()));

        Uri uri = Uri.fromFile(new File(twins[position].getLocal().getUrl()));
        Uri uri2 = Uri.fromFile(new File(twins[position].getRemote().getUrl()));
        Picasso.with(context).load(uri).resize(75, 75).centerCrop().into(holder.img1);
        Picasso.with(context).load(uri2).resize(75, 75).centerCrop().into(holder.img2);

        OnClickListener event1 = getEvent1(position);
        OnClickListener event2 = getEvent2(position);

        holder.tv1.setOnClickListener(event1);
        holder.img1.setOnClickListener(event1);
        holder.tv2.setOnClickListener(event2);
        holder.img2.setOnClickListener(event2);

        return rowView;

    }

}
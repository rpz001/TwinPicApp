package com.durrutia.twinpic;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.durrutia.twinpic.domain.Pic;
import com.durrutia.twinpic.domain.Pic_Table;
import com.durrutia.twinpic.domain.Twin;
import com.durrutia.twinpic.util.DeviceUtils;
import com.durrutia.twinpic.util.RetrofitArrayAPI;
import com.google.common.base.Stopwatch;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Test principal del backend.
 *
 * @author Diego P. Urrutia Astorga
 * @version 20161102
 */

@Slf4j
public final class Test {

    /**
     * Testing the bd.
     *
     * @param context
     */
    public static void testDatabase(final Context context) {

        Log.d("Aviso","Esto es una prueba");
        test4(context);

    }

    public static void poblarBD(final Context context){

        log.debug("Aviso: Se resetea la base de datos.");
        context.deleteDatabase(Database.NAME + ".db");
        FlowManager.init(new FlowConfig.Builder(context).openDatabasesOnInit(true).build());
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        String[] fileNames = path.list();

        log.debug(path.toString());

        for(int i=0; i<fileNames.length; i++){

            log.debug("Dato",fileNames[i]);

        }

        final Pic pic = Pic.builder()
                .deviceId(DeviceUtils.getDeviceId(context)+RandomStringUtils.randomAlphabetic(20))
                .latitude(RandomUtils.nextDouble())
                .longitude(RandomUtils.nextDouble())
                .date(new Date().getTime())
                .url("http://" + RandomStringUtils.randomAlphabetic(20))
                .positive(RandomUtils.nextInt(0, 100))
                .negative(RandomUtils.nextInt(0, 100))
                .warning(RandomUtils.nextInt(0, 2))
                .build();

        pic.save();

    }

    /**
     * Este test prueba que solo se haga una valoración (like, dislike, waning);
     * @param context
     */
    public static void test4(final Context context){

        log.debug("Aviso: Se resetea la base de datos.");
        context.deleteDatabase(Database.NAME + ".db");
        FlowManager.init(new FlowConfig.Builder(context).openDatabasesOnInit(true).build());

        for(int i=0; i<4; i++) {

            final Pic pic = Pic.builder()
                    .deviceId(DeviceUtils.getDeviceId(context))
                    .latitude(RandomUtils.nextDouble())
                    .longitude(RandomUtils.nextDouble())
                    .date(new Date().getTime())
                    .url("http://" + RandomStringUtils.randomAlphabetic(20))
                    .positive(RandomUtils.nextInt(0, 100))
                    .negative(RandomUtils.nextInt(0, 100))
                    .warning(RandomUtils.nextInt(0, 2))
                    .build();
            pic.save();

        }

        for(int i=0; i<2; i++) {

            final Pic p1 = SQLite.select().from(Pic.class).queryList().get(2*i); //Pic local.
            final Pic p2 = SQLite.select().from(Pic.class).queryList().get((2*i)+1); //Pic remota.
            final Twin twin = Twin.builder().local(p1).remote(p2).build();
            twin.save();

        }

        List<Twin> twins = SQLite.select().from(Twin.class).queryList();

        for(int i=0; i<2; i++){

            Twin t = twins.get(i);
            log.debug("Twin {} antes de calificar {}",i,t);

            //Inicialmente, la Twin no tiene ninguna calificación, se la da un like.
            if(!t.isDioLike() && !t.isDioDislike()) {

                Pic remote = t.getRemote();
                remote.setPositive(remote.getPositive() + 1);
                remote.save();
                t.setDioLike(true);
                t.save();
                log.debug("Twin {} después de darle like {}",i,t);

            }

            log.debug("Dandole otro like a la Twin {}",i);

            //Se le da otro like, no debería concretarse.
            if(t.isDioLike() && !t.isDioDislike()){

                log.debug("No se le puede dar mas likes");

            }

            //Se le da un dislike, debería disminuir en -1 la cantidad de likes.
            if(t.isDioLike() && !t.isDioDislike()){

                Pic remote = t.getRemote();
                remote.setPositive(remote.getPositive() - 1);
                remote.setNegative(remote.getNegative() + 1);
                remote.save();
                t.setDioLike(false);
                t.setDioDislike(true);
                t.save();
                log.debug("Twin {} después de darle dislke {}",i,t);

            }

            //Se le da otro dislike, no debería concretarse.
            if(!t.isDioLike() && t.isDioDislike()){

                log.debug("No se le puede dar mas dislikes");

            }

            //Se vuelve a dar un like
            if(!t.isDioLike() && t.isDioDislike()) {

                Pic remote = t.getRemote();
                remote.setPositive(remote.getPositive() + 1);
                remote.setNegative(remote.getNegative() - 1);
                remote.save();
                t.setDioLike(true);
                t.setDioDislike(false);
                t.save();
                log.debug("Twin {} después de darle like {}",i,t);

            }

        }

    }

    public static void test3(final Context context){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://190.161.123.37:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitArrayAPI service = retrofit.create(RetrofitArrayAPI.class);

    }

    public static void test2(final Context context){

        FlowManager.init(new FlowConfig.Builder(context).openDatabasesOnInit(true).build());

        List<Pic> pics = SQLite.select().from(Pic.class).queryList();

        for (final Pic p : pics) {
            log.debug("{}", p);
        }

    }

    public static void test1(final Context context){

        // Remove database
        context.deleteDatabase(Database.NAME + ".db");

        // Cronometro
        final Stopwatch stopwatch = Stopwatch.createStarted();

        log.debug("Testing database ..");

        // Inicializacion
        {
            FlowManager.init(new FlowConfig.Builder(context)
                    .openDatabasesOnInit(true)
                    .build());

            log.debug("DB initialized in: {}.", stopwatch);
        }
        stopwatch.reset().start();

        // Insert into db
        {
            // Ciclo para insertar 100 objetos en la bd
            for (int i = 1; i <= 100; i++) {

                stopwatch.reset().start();

                final Pic pic = Pic.builder()
                        .deviceId(DeviceUtils.getDeviceId(context))
                        .latitude(RandomUtils.nextDouble())
                        .longitude(RandomUtils.nextDouble())
                        .date(new Date().getTime())
                        .url("http://" + RandomStringUtils.randomAlphabetic(20))
                        .positive(RandomUtils.nextInt(0, 100))
                        .negative(RandomUtils.nextInt(0, 100))
                        .warning(RandomUtils.nextInt(0, 2))
                        .build();

                // Commit
                pic.save();
                log.debug("Saved {} in {}.", i, stopwatch);

            }

        }
        stopwatch.reset().start();

        // Select from database
        {
            List<Pic> pics = SQLite.select().from(Pic.class).queryList();
            log.debug("Result: {} in {}.", pics.size(), stopwatch);

            stopwatch.reset().start();

            for (final Pic p : pics) {
                log.debug("{}", p);
            }
        }
        stopwatch.reset().start();

        // Relations
        {
            for (long i = 1; i <= 6; i = i + 2) {
                final Pic local = SQLite.select().from(Pic.class).where(Pic_Table.id.is(i)).querySingle();
                final Pic remote = SQLite.select().from(Pic.class).where(Pic_Table.id.is(i + 1)).querySingle();

                final Twin twin = Twin.builder().local(local).remote(remote).build();
                log.debug("Twin: {}", twin);

                twin.save();
            }

            log.debug("Relation in {}.", stopwatch);
        }
        stopwatch.reset().start();

        // Get from relation
        {
            final List<Twin> twins = SQLite.select().from(Twin.class).queryList();
            log.debug("Twin size: {} in {}.", twins.size(), stopwatch);

            for (final Twin t : twins) {
                log.debug("Twin: {}.", t);
            }
        }
        stopwatch.reset().start();

        // Destroy the world.
        {
            FlowManager.destroy();

            log.debug("Finished in {}.", stopwatch);
        }


    }

}

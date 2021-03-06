package com.simarro.joshu.clientsqr.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simarro.joshu.clientsqr.BBDD.BD;
import com.simarro.joshu.clientsqr.Pojo.Client;
import com.simarro.joshu.clientsqr.Pojo.LlistaClients;
import com.simarro.joshu.clientsqr.Pojo.Tenda;
import com.simarro.joshu.clientsqr.R;
import com.simarro.joshu.clientsqr.Resources.Dialogs.DialogoInfo;

public class ModificarPuntos extends AppCompatActivity implements View.OnClickListener {

    private LlistaClients clients = new LlistaClients();
    private String qr;
    private TextView numPunts;
    private EditText anyPunts;
    private int mostrarpunts, addPunts;
    private LocationManager locationManager;
    private static Location location;
    private LocationListener locationListener;
    private double longitud, latitud;
    private int mostrarMsg = 0, canjeo;
    private ImageView gps_ok;
    private ProgressBar buscando;
    private SharedPreferences preferencies;
    private Tenda tenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_puntos);
        qr = getIntent().getExtras().getString("qr");
        tenda = (Tenda) getIntent().getExtras().getSerializable("tenda");
        numPunts = findViewById(R.id.txt_num_punts);
        anyPunts = findViewById(R.id.ed_punts);
        buscando = findViewById(R.id.progress_buscando);
        gps_ok = findViewById(R.id.img_gps_ok);
        preferencies = PreferenceManager.getDefaultSharedPreferences(this);
        canjeo = Integer.parseInt(preferencies.getString("canjeo", "100"));
        numPunts.setText(qr);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        123);
            }
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        longitud = location.getLongitude();
                        latitud = location.getLatitude();
                        if(mostrarMsg == 0){
                            Toast.makeText(getApplicationContext(), "Geolocalización Obtenida", Toast.LENGTH_SHORT).show();
                            buscando.setVisibility(View.INVISIBLE);
                            gps_ok.setVisibility(View.VISIBLE);
                            mostrarMsg = 1;
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        longitud = location.getLongitude();
                        latitud = location.getLatitude();
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        longitud = location.getLongitude();
                        latitud = location.getLatitude();
                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListener);

    }

    @Override
    public void onClick(View v) {
        if (longitud == 0 && latitud == 0) {
            Toast.makeText(this, "Obteniendo geolocalización", Toast.LENGTH_SHORT).show();
        } else {
            BD bd = new BD(getApplicationContext()) {
                public void run() {
                    try {
                        synchronized (this) {
                            wait(500);
                            conectarBDMySQL();
                            getClientes(0);
                            cerrarConexion();
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        Log.e("Error", "Waiting didnt work!!");
                        e.printStackTrace();
                    }
                }
            };
            Thread th = new Thread(bd);
            th.start();
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //Obtenemos la lista de los clientes en la BBDD
            this.clients.addLlista(bd.obClients());
            mostrarpunts = this.clients.getClientById(Integer.parseInt(qr)).getPunts();
            switch (v.getId()) {
                case R.id.btn_sumar_punto:
                    bd = new BD(getApplicationContext()) {
                        public void run() {
                            try {
                                synchronized (this) {
                                    wait(500);
                                    conectarBDMySQL();
                                    addPunts = mostrarpunts + Integer.parseInt(anyPunts.getText().toString());
                                    modPuntos(addPunts, Integer.parseInt(qr));
                                    cerrarConexion();
                                }
                            } catch (InterruptedException e) {
                                Log.e("Error", "Waiting didnt work!!");
                                e.printStackTrace();
                            }
                        }
                    };
                    th = new Thread(bd);
                    th.start();
                    try {
                        th.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bd = new BD(getApplicationContext()) {
                        public void run() {
                            try {
                                synchronized (this) {
                                    wait(500);
                                    conectarBDMySQL();
                                    addRegistrePunts(Integer.parseInt(qr), true, Integer.parseInt(anyPunts.getText().toString()), longitud, latitud);
                                    cerrarConexion();
                                }
                            } catch (InterruptedException e) {
                                Log.e("Error", "Waiting didnt work!!");
                                e.printStackTrace();
                            }
                        }
                    };
                    th = new Thread(bd);
                    th.start();
                    try {
                        th.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DialogoInfo dialeg = DialogoInfo.newInstance("El cliente Nº" + qr + " tiene " + addPunts + " puntos!");
                    dialeg.show(getSupportFragmentManager(),"dialegModPunts");
                    break;
                case R.id.btn_canjear_por_cupon:
                    bd = new BD(getApplicationContext()) {
                        public void run() {
                            try {
                                synchronized (this) {
                                    wait(500);
                                    conectarBDMySQL();
                                    getClient(Integer.parseInt(qr));
                                    cerrarConexion();
                                }
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                Log.e("Error", "Waiting didnt work!!");
                                e.printStackTrace();
                            }
                        }
                    };
                    th = new Thread(bd);
                    th.start();
                    try {
                        th.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Client client = bd.getClientOb();
                    Intent intent = new Intent(this, Premios.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("tipo",false);
                    intent.putExtra("client",client);
                    startActivity(intent);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() { //Utilizado para que al destruirse vaya al dashboard directamente
        super.onDestroy();
        Intent intent = new Intent(this, DashBoard.class);
        intent.putExtra("tenda",this.tenda);
        startActivity(intent);
    }
}

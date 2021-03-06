package com.simarro.joshu.clientsqr.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.simarro.joshu.clientsqr.BBDD.BD;
import com.simarro.joshu.clientsqr.Pojo.Client;
import com.simarro.joshu.clientsqr.Pojo.Punts;
import com.simarro.joshu.clientsqr.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Mapa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Mapa extends Fragment implements OnMapReadyCallback {

    private Client client;
    private ArrayList<Punts> punts = new ArrayList<>();
    private MapView mapa;
    private EventBus bus = EventBus.getDefault();

    public Mapa() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Mapa newInstance(Client c) {
        Mapa fragment = new Mapa();
        Bundle args = new Bundle();
        args.putSerializable("client", c);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = (Client) getArguments().getSerializable("client");
        BD bd = new BD(getContext()) {
            public void run() {
                try {
                    synchronized (this) {
                        wait(500);
                        conectarBDMySQL();
                        getRegistrePuntsByIdClient(client.getId());
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
        this.punts = bd.obPunts();

    }

    @Subscribe
    public void setPunts(ArrayList<Punts> editado){
        this.punts = editado;
        this.cargarMapa();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mapa, container, false);
        rootView.setBackgroundColor(getResources().getColor(R.color.bar));

        mapa = (MapView) rootView.findViewById(R.id.mapa);
        this.cargarMapa();

        /*SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapa);
        if (mapFragment == null) {
            Toast.makeText(getContext(), "No se ha podido cargar el mapa", Toast.LENGTH_SHORT).show();
        } else {
            mapFragment.getMapAsync(this);
        }*/

        return rootView;
    }

    public void cargarMapa(){
        mapa.onCreate(getArguments());
        mapa.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        mapa.getMapAsync(this);
    }

    private double[] calcMediaLatLong(ArrayList<Punts> puntos) {
        double[] res = new double[2];
        double medLats = 38.9674887, medLongs = -0.5862496;
        if (puntos.size() > 0) {
            medLats = 0;
            medLongs = 0;
            for (Punts p : puntos) {
                medLats += p.getLatitud();
                medLongs += p.getLongitud();
            }
            medLats = medLats / puntos.size();
            medLongs = medLongs / puntos.size();
        }
        res[0] = medLats;
        res[1] = medLongs;
        return res;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng aux;
        String asPunts;
        ArrayList<Punts> puntos = new ArrayList<>();
        double[] media;
        for (Punts p : this.punts) {
            if(p.getLatitud() != 0 && p.getLongitud() != 0) {
                aux = new LatLng(p.getLatitud(), p.getLongitud());
                puntos.add(p);
                if (p.isOperacio()) {
                    asPunts = "+" + p.getPunts() + " " + p.getRegistro();
                } else {
                    asPunts = "-" + p.getPunts() + " " + p.getRegistro();
                }
                googleMap.addMarker(new MarkerOptions().position(aux).title(asPunts));
            }
        }
        media = calcMediaLatLong(puntos);
        aux = new LatLng(media[0], media[1]);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(aux));
        if (this.punts.size() > 0) {
            googleMap.setMinZoomPreference(15);
        } else {
            googleMap.setMinZoomPreference(14);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.bus.unregister(this);
    }


}

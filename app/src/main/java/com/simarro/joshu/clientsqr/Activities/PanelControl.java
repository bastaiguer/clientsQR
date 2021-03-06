package com.simarro.joshu.clientsqr.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.simarro.joshu.clientsqr.Fragments.Mapa;
import com.simarro.joshu.clientsqr.Fragments.Tabla_Fragment;
import com.simarro.joshu.clientsqr.Fragments.gestionarPreferencias;
import com.simarro.joshu.clientsqr.Pojo.Client;
import com.simarro.joshu.clientsqr.R;
import com.simarro.joshu.clientsqr.Resources.MyFragmentPagerAdapter;

public class PanelControl extends FragmentActivity{

    private ViewPager pager = null;
    private TabLayout tabsTitulos;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_control);
        this.pager = this.findViewById(R.id.pager);
        client = (Client) getIntent().getExtras().getSerializable("client");
        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(Tabla_Fragment.newInstance(client));
        pagerAdapter.addFragment(Mapa.newInstance(client));

        this.pager.setAdapter(pagerAdapter);
        tabsTitulos = findViewById(R.id.tabLayout_titulos);
        tabsTitulos.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabsTitulos.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabsTitulos.setupWithViewPager(pager);

    }




    @Override
    public void onBackPressed() {
        if(this.pager.getCurrentItem() == 0){
            super.onBackPressed();
        }else{
            this.pager.setCurrentItem(this.pager.getCurrentItem()-1);
        }
    }
}

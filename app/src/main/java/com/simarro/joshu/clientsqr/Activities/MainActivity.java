package com.simarro.joshu.clientsqr.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.simarro.joshu.clientsqr.BBDD.BD;
import com.simarro.joshu.clientsqr.Pojo.LlistaClients;
import com.simarro.joshu.clientsqr.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private LlistaClients clients = new LlistaClients();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BD bd = new BD("picanya","picanya","jdbc:mysql://localhost:3306/clientsqr");
        bd.conectarBDMySQL(this.getApplicationContext());
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,DashBoard.class);
        intent.putExtra("clients",clients);
        startActivity(intent);
    }
}

package com.simarro.joshu.clientsqr.Pojo;

import java.util.ArrayList;

public class LlistaClients extends ArrayList<Client> {

    public void addClient(Client cl){
        cl.setId(this.size()+1);
        this.add(cl);
    }

    public Client getClientById(int id) {
        Client c = null;
        for (Client cl : this) {
            if (cl.getId() == id) {
                c = cl;
            }
        }
        return c;
    }

    public ArrayList<Client> getClientsByName(String name){
        ArrayList<Client> c = new ArrayList<>();
        for (Client cl : this) {
            if (cl.getNombre().equals(name)) {
                c.add(cl);
            }
        }
        return c;
    }

    public Client getClientByTlf(String tlf){
        Client c = null;
        for (Client cl : this) {
            if (cl.getTelefono().equals(tlf)) {
                c = cl;
            }
        }
        return c;
    }

    public void orderByName() {
        char first;
        Client cl;
        for (int j = 0; j < this.size(); j++) {
            for (int i = 1; i < this.size(); i++) {
                first = this.get(i - 1).getNombre().charAt(0);
                if (first > this.get(i).getNombre().charAt(0)) {
                    cl = this.get(i - 1);
                    this.set(i - 1, this.get(i));
                    this.set(i, cl);
                }
            }
        }
    }

}

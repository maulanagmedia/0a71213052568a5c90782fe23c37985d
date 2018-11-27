package com.leonardus.irfan.bluetoothprinter.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Transaksi {
    private String outlet;
    private String sales;
    private String no_nota;
    private Date tgl_transaksi;
    private double tunai, jml, admin, denda;
    private List<Item> listItem;
    private String biayaAdmin, dpp, ppn, nonPPN, tglNota, periode, msisdn, standMeter;

    public Transaksi(String outlet, String sales, String no_nota, Date tgl_transaksi, List<Item> listItem, String tglNota){
        this.outlet = outlet;
        this.sales = sales;
        this.no_nota = no_nota;
        this.tgl_transaksi = tgl_transaksi;
        this.listItem = listItem;
        this.tglNota = tglNota;
    }

    public void setTunai(double tunai){
        this.tunai = tunai;
    }

    public double getTunai() {
        return tunai;
    }

    public String getNo_nota() {
        return no_nota;
    }

    public Date getTgl_transaksi() {
        return tgl_transaksi;
    }

    public List<Item> getListItem() {
        return listItem;
    }

    public String getOutlet() {
        return outlet;
    }

    public String getSales() {
        return sales;
    }

    public String getBiayaAdmin() {
        return biayaAdmin;
    }

    public void setBiayaAdmin(String biayaAdmin) {
        this.biayaAdmin = biayaAdmin;
    }

    public String getDpp() {
        return dpp;
    }

    public void setDpp(String dpp) {
        this.dpp = dpp;
    }

    public String getPpn() {
        return ppn;
    }

    public void setPpn(String ppn) {
        this.ppn = ppn;
    }

    public String getNonPPN() {
        return nonPPN;
    }

    public void setNonPPN(String nonPPN) {
        this.nonPPN = nonPPN;
    }

    public double getJml() {
        return jml;
    }

    public void setJml(double jml) {
        this.jml = jml;
    }

    public double getAdmin() {
        return admin;
    }

    public void setAdmin(double admin) {
        this.admin = admin;
    }

    public double getDenda() {
        return denda;
    }

    public void setDenda(double denda) {
        this.denda = denda;
    }

    public String getTglNota() {
        return tglNota;
    }

    public void setTglNota(String tglNota) {
        this.tglNota = tglNota;
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getStandMeter() {
        return standMeter;
    }

    public void setStandMeter(String standMeter) {
        this.standMeter = standMeter;
    }
}

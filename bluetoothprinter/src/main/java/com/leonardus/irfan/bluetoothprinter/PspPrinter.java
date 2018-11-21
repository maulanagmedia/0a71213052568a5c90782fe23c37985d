package com.leonardus.irfan.bluetoothprinter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.leonardus.irfan.bluetoothprinter.Model.Item;
import com.leonardus.irfan.bluetoothprinter.Model.Transaksi;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PspPrinter extends BluetoothPrinter {
    /*
        BLUETOOTH PRINTER PSP
        Library untuk menggunakan bluetooth printer. Langkah menggunakan :
        1. Buat objek BluetoothPrinter dengan menggunakan keyword new dengan parameter input context
            (ex : btPrint = new BluetoothPrinter(this))
        2. panggil method startService untuk menginisialisasi object bluetooth printer
            (ex : btnPrint.startService())
        3. panggil method showDevices untuk melakukan koneksi dengan device bluetooth printer
            (ex : btPrint.showDevices())
        4. panggil method print dengan parameter transaksi untuk mencetak nota di device
            (ex :
            Calendar date = Calendar.getInstance();
            List<Item> listTransaksi = new ArrayList<>();
            listTransaksi.add(new Item("Denom 5k", 20, 5500));
            listTransaksi.add(new Item("Denom 25k", 10, 20500));
            listTransaksi.add(new Item("Denom 100k", 5, 97000));
            Transaksi t = new Transaksi("Yunma Jaya Cell", "Andi Kusworo", "PD001", date.getTime(), listTransaksi)
            btPrint.print(transaksi))
        5. panggil method stopService untuk mengakhiri koneksi, saran : gunakan di method onDestroy Activity
            (ex : btPrint.stopService())
    */

    public PspPrinter(Context context){
        super(context);
    }

    public boolean isPrinterReady(){

        return bluetoothDevice != null;
    }

    // this will send text data to be printed by the bluetooth printer
    public void print(Transaksi transaksi){
        if(bluetoothDevice == null){
            Toast.makeText(context, "Sambungkan ke device printer terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double jum = 0;
            outputStream.write(PrintFormatter.ALIGN_CENTER);
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.psp_header);
            byte[] bmp_byte = PrintFormatter.decodeBitmap(bmp);
            if(bmp_byte != null){
                outputStream.write(bmp_byte);
            }
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(PrintFormatter.NEW_LINE);

            outputStream.write(PrintFormatter.ALIGN_LEFT);
            outputStream.write(String.format("Nama     :  %s\n", transaksi.getOutlet()).getBytes());
            outputStream.write(String.format("Reseller :  %s\n", transaksi.getSales()).getBytes());
            outputStream.write(String.format("No. SN   :  %s\n", transaksi.getNo_nota()).getBytes());

            outputStream.write(PrintFormatter.NEW_LINE);

            outputStream.write(PrintFormatter.ALIGN_CENTER);
            outputStream.write("-------------------------\n".getBytes());
            outputStream.write(PrintFormatter.ALIGN_LEFT);
            outputStream.write("Nama Barang\tJumlah\tTotal\n".getBytes());
            outputStream.write(PrintFormatter.ALIGN_CENTER);
            outputStream.write("-------------------------\n".getBytes());
            outputStream.write(PrintFormatter.ALIGN_LEFT);
            List<Item> listItem = transaksi.getListItem();
            for(int i = 0; i < listItem.size(); i++){
                //transaksi
                Item t =  listItem.get(i);
                outputStream.write(String.format(Locale.getDefault(), "%s\t %d\t%s\n",t.getNama(), t.getJumlah(), RupiahFormatter.getRupiah(t.getHarga()*t.getJumlah())).getBytes());
                jum += t.getHarga()*t.getJumlah();
            }
            final int character_size = 12;

            //tunai selalu sama dengan jumlah
            transaksi.setTunai(jum);

            String jum_string, tunai_string, kembali_string;
            jum_string = RupiahFormatter.getRupiah(jum);
            tunai_string = RupiahFormatter.getRupiah(transaksi.getTunai());
            kembali_string = RupiahFormatter.getRupiah(transaksi.getTunai() - jum);
            outputStream.write(PrintFormatter.ALIGN_RIGHT);
            outputStream.write("----------".getBytes());
            outputStream.write("\nTOTAL : ".getBytes());
            for(int i = 0; i < character_size - jum_string.length(); i++){
                outputStream.write(" ".getBytes());
            }
            outputStream.write(jum_string.getBytes());
            outputStream.write("\nTUNAI : ".getBytes());
            for(int i = 0; i < character_size - tunai_string.length(); i++){
                outputStream.write(" ".getBytes());
            }
            outputStream.write(tunai_string.getBytes());
            outputStream.write("\nKEMBALI : ".getBytes());
            for(int i = 0; i < character_size - kembali_string.length(); i++){
                outputStream.write(" ".getBytes());
            }
            outputStream.write(kembali_string.getBytes());

            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(PrintFormatter.ALIGN_CENTER);
            outputStream.write("Terima Kasih\n".getBytes());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(transaksi.getTgl_transaksi());

            outputStream.write(String.format("%s\n", currentDateandTime).getBytes());
            outputStream.write("==============================\n".getBytes());
            outputStream.write(PrintFormatter.NEW_LINE);
            outputStream.write(PrintFormatter.NEW_LINE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

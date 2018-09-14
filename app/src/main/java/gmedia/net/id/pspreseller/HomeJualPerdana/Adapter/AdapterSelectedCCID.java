package gmedia.net.id.pspreseller.HomeJualPerdana.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.pspreseller.HomeJualPerdana.DetailJualPerdana;
import gmedia.net.id.pspreseller.R;

public class AdapterSelectedCCID extends RecyclerView.Adapter<AdapterSelectedCCID.MyViewHolder> {

    private Context context;
    private List<CustomItem> listCcid;
    private ItemValidation iv = new ItemValidation();

    public AdapterSelectedCCID(Context context, List<CustomItem> listCcid){
        this.listCcid = listCcid;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_selected_ccid, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final int id = position;
        final CustomItem ccid = listCcid.get(id);

        holder.tvItem1.setText(String.valueOf(position + 1));
        holder.tvItem2.setText(ccid.getItem2());
        holder.tvItem3.setText(ccid.getItem1());

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Konfirmasi")
                        .setMessage("APakah anda yakin ingin menghapus ccid "+ccid.getItem2()+" ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listCcid.remove(id);
                                ((DetailJualPerdana)context).updateCcid();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return listCcid.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tvItem1, tvItem2, tvItem3;
        private ImageView ivDelete;
        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem1 = itemView.findViewById(R.id.tv_item1);
            tvItem2 = itemView.findViewById(R.id.tv_item2);
            tvItem3 = itemView.findViewById(R.id.tv_item3);

            ivDelete = itemView.findViewById(R.id.iv_delete);
        }
    }
}

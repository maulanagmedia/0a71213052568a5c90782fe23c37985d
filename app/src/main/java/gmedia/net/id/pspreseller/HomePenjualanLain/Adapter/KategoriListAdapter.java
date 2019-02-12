package gmedia.net.id.pspreseller.HomePenjualanLain.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;

import java.util.HashMap;
import java.util.List;

import gmedia.net.id.pspreseller.HomeJualPerdana.DetailJualPerdana;
import gmedia.net.id.pspreseller.HomePenjualanLain.DetailOrderLain;
import gmedia.net.id.pspreseller.HomePulsa.OrderPulsa;
import gmedia.net.id.pspreseller.R;

/**
 * Created by Shin on 3/1/2017.
 */

public class KategoriListAdapter extends RecyclerView.Adapter<KategoriListAdapter.MyViewHolder> {

    private Context context;
    private List<CustomItem> masterList;
    private ItemValidation iv = new ItemValidation();
    private int menuWidth;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llContainer;
        public ImageView ivIcon;
        public TextView tvTitle;

        public MyViewHolder(View view) {
            super(view);
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            llContainer = (LinearLayout) view.findViewById(R.id.ll_container);
        }
    }

    public KategoriListAdapter(Context context, List<CustomItem> masterList, int menuWidth){
        this.context = context;
        this.masterList = masterList;
        this.menuWidth = menuWidth;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_katergori, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CustomItem kategori = masterList.get(position);

        /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(menuWidth , menuWidth);
        holder.llContainer.setLayoutParams(lp);*/
        holder.tvTitle.setText(kategori.getItem2());
        // loading image using Picasso library
        ImageUtils iu = new ImageUtils();

        if(kategori.getItem5().equals("0")){
            iu.LoadCategoryImage(context, kategori.getItemInt1(), holder.ivIcon);
        }else{
            iu.LoadCategoryImage(context, kategori.getItem3(), holder.ivIcon);
        }


        holder.llContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(kategori.getItem5().equals("0")){
                    if(kategori.getItem1().equals("pulsa")){

                        Intent intent = new Intent(context, OrderPulsa.class);
                        ((Activity) context).startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }else{

                        Intent intent = new Intent(context, DetailJualPerdana.class);
                        ((Activity) context).startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }
                }else{
                    Intent intent = new Intent(context, DetailOrderLain.class);
                    intent.putExtra("kategori", kategori.getItem1());
                    intent.putExtra("nama", kategori.getItem2());
                    intent.putExtra("flag", kategori.getItem4());
                    ((Activity)context).startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return masterList.size();
    }

}

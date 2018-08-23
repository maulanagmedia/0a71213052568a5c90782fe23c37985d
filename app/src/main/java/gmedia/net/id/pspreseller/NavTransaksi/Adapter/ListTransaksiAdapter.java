package gmedia.net.id.pspreseller.NavTransaksi.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.pspreseller.R;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListTransaksiAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListTransaksiAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_list_transaksi, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvStatus;
        private LinearLayout llDetail;
        private ImageView ivCollapse;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.adapter_list_transaksi, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);

            holder.ivCollapse = (ImageView) convertView.findViewById(R.id.iv_collapse);
            holder.llDetail = (LinearLayout) convertView.findViewById(R.id.ll_detail);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem4());
        holder.tvItem2.setText(itemSelected.getItem3());
        holder.tvItem3.setText(iv.ChangeToRupiahFormat(itemSelected.getItem5()));

        if(itemSelected.getItem6().toUpperCase().trim().equals("BARU")){

            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status));
        }else if(itemSelected.getItem6().toUpperCase().trim().equals("DISETUJUI")){

            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status_yellow));
        }else{
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_status_red));
        }
        holder.tvStatus.setText(itemSelected.getItem6());

        final ViewHolder finalHolder = holder;
        /*holder.ivCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(finalHolder.llDetail.getVisibility() == View.VISIBLE){

                    finalHolder.ivCollapse.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_collapse));
                    finalHolder.llDetail.setVisibility(View.GONE);
                }else{

                    finalHolder.ivCollapse.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_expand));
                    finalHolder.llDetail.setVisibility(View.VISIBLE);
                }
            }
        });*/
        return convertView;

    }
}

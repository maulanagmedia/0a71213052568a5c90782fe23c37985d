package gmedia.net.id.pspreseller.NavHistory.Adapter;

import android.app.Activity;
import android.text.Html;
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

public class ListHistoryAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListHistoryAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_list_history, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3, tvItem4, tvItem5, tvItem6, tvItem7, tvItem8, tvStatus;
        private LinearLayout llCashback, llSaldoAkhir;
        private RelativeLayout rlToken, rlDaya;
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
            convertView = inflater.inflate(R.layout.adapter_list_history, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_item5);
            holder.tvItem6 = (TextView) convertView.findViewById(R.id.tv_item6);
            holder.tvItem7 = (TextView) convertView.findViewById(R.id.tv_item7);
            holder.tvItem8 = (TextView) convertView.findViewById(R.id.tv_item8);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            holder.llCashback = (LinearLayout) convertView.findViewById(R.id.ll_cashback);
            holder.llSaldoAkhir = (LinearLayout) convertView.findViewById(R.id.ll_saldo_akhir);
            holder.rlToken = (RelativeLayout) convertView.findViewById(R.id.rl_token);
            holder.rlDaya = (RelativeLayout) convertView.findViewById(R.id.rl_daya);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem8());
        holder.tvItem2.setText(itemSelected.getItem3() + " "+ itemSelected.getItem9());
        holder.tvItem3.setText(itemSelected.getItem7() + " (" +itemSelected.getItem5() + ")");
        holder.tvItem4.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem6()));
        holder.tvStatus.setText(Html.fromHtml(itemSelected.getItem4()));
        if(!itemSelected.getItem12().isEmpty() && !itemSelected.getItem12().equals("0")){
            holder.rlToken.setVisibility(View.VISIBLE);
            holder.tvItem7.setText(itemSelected.getItem12());
        }else{
            holder.rlToken.setVisibility(View.GONE);
            holder.tvItem7.setText(itemSelected.getItem12());
        }

        if(!itemSelected.getItem10().equals("null") && !itemSelected.getItem10().isEmpty() && !itemSelected.getItem10().equals("0")){
            holder.llCashback.setVisibility(View.VISIBLE);
            holder.tvItem5.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem10()));
        }else{

            holder.llCashback.setVisibility(View.GONE);
            holder.tvItem5.setText("");
        }

        if(!itemSelected.getItem11().equals("null") && !itemSelected.getItem11().isEmpty() && !itemSelected.getItem11().equals("0")){
            holder.llSaldoAkhir.setVisibility(View.VISIBLE);
            holder.tvItem6.setText(iv.ChangeToCurrencyFormat(itemSelected.getItem11()));
        }else{

            holder.llSaldoAkhir.setVisibility(View.GONE);
            holder.tvItem6.setText("");
        }

        if(!itemSelected.getItem22().trim().equals("/0")){
            holder.rlDaya.setVisibility(View.VISIBLE);
            holder.tvItem8.setText(itemSelected.getItem22());
        }else{
            holder.rlDaya.setVisibility(View.GONE);
        }
        return convertView;

    }
}

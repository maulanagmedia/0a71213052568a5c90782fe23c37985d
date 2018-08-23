package gmedia.net.id.pspreseller.HomeInfoStok.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.FormatItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.pspreseller.R;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListBalasanMkiosAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListBalasanMkiosAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.cv_list_balasan_mkios, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2, tvItem3,
                tvItem4, tvItem5, tvItem6, tvItem7,
                tvItem8, tvItem9, tvItem10, tvItem11,
                tvItem12, tvItem13, tvItem14;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    public void addData(CustomItem moreData){

        items.add(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.cv_list_balasan_mkios, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.tvItem3 = (TextView) convertView.findViewById(R.id.tv_item3);
            holder.tvItem4 = (TextView) convertView.findViewById(R.id.tv_item4);
            holder.tvItem5 = (TextView) convertView.findViewById(R.id.tv_item5);
            holder.tvItem6 = (TextView) convertView.findViewById(R.id.tv_item6);
            holder.tvItem7 = (TextView) convertView.findViewById(R.id.tv_item7);
            holder.tvItem8 = (TextView) convertView.findViewById(R.id.tv_item8);
            holder.tvItem9 = (TextView) convertView.findViewById(R.id.tv_item9);
            holder.tvItem10 = (TextView) convertView.findViewById(R.id.tv_item10);
            holder.tvItem11 = (TextView) convertView.findViewById(R.id.tv_item11);
            holder.tvItem12 = (TextView) convertView.findViewById(R.id.tv_item12);
            holder.tvItem13 = (TextView) convertView.findViewById(R.id.tv_item13);
            holder.tvItem14 = (TextView) convertView.findViewById(R.id.tv_item14);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem1());
        holder.tvItem2.setText(itemSelected.getItem2());
        holder.tvItem3.setText(itemSelected.getItem3());
        holder.tvItem4.setText(itemSelected.getItem4());
        holder.tvItem5.setText(itemSelected.getItem5());
        holder.tvItem6.setText(itemSelected.getItem6());
        holder.tvItem7.setText(itemSelected.getItem7());
        holder.tvItem8.setText(itemSelected.getItem8());
        holder.tvItem9.setText(itemSelected.getItem9());
        holder.tvItem10.setText(itemSelected.getItem10());
        holder.tvItem11.setText(itemSelected.getItem11());
        holder.tvItem12.setText(itemSelected.getItem12());
        holder.tvItem13.setText(itemSelected.getItem13());
        holder.tvItem14.setText(iv.ChangeFormatDateString(itemSelected.getItem14(), FormatItem.formatTimestamp, FormatItem.formatDateTimeDisplay));
        return convertView;

    }
}

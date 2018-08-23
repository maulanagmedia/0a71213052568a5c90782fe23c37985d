package gmedia.net.id.pspreseller.HomePulsa.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.pspreseller.HomePulsa.OrderPulsa;
import gmedia.net.id.pspreseller.R;

/**
 * Created by Shin on 1/8/2017.
 */

public class BarangPulsaAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();
    public static int selectedItem = 0;

    public BarangPulsaAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.cv_list_barang_pulsa, items);
        this.context = context;
        this.items = items;
        this.selectedItem = 0;
    }

    private static class ViewHolder {
        private TextView tvItem;
        private RadioButton rbItem;
    }

    public List<CustomItem> getData(){

        return items;
    }

    public void setSelected(int position){

        selectedItem = position;
        notifyDataSetChanged();
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
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.cv_list_barang_pulsa, null);
            holder.tvItem = (TextView) convertView.findViewById(R.id.tv_item);
            holder.rbItem = (RadioButton) convertView.findViewById(R.id.rb_item);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem.setText(itemSelected.getItem2());
        if(itemSelected.getItem3().isEmpty() || itemSelected.getItem3().equals("0")){
            holder.rbItem.setText("");
        }else{
            holder.rbItem.setText(iv.ChangeToRupiahFormat(itemSelected.getItem3()));
        }

        if(selectedItem == position){

            holder.rbItem.setChecked(true);
            //DetailInjectPulsa.setSelectedItem(itemSelected);
        }else{
            holder.rbItem.setChecked(false);
        }

        holder.rbItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedItem = position;
                OrderPulsa.setSelectedItem(itemSelected);
                notifyDataSetChanged();
            }
        });

        return convertView;

    }
}

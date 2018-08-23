package gmedia.net.id.pspreseller.SideInfoPSP.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.pspreseller.R;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListSPVAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListSPVAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_list_spv, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvSPV, tvNoSPV;
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
            convertView = inflater.inflate(R.layout.adapter_list_spv, null);
            holder.tvSPV = (TextView) convertView.findViewById(R.id.tv_spv);
            holder.tvNoSPV = (TextView) convertView.findViewById(R.id.tv_nomor_spv);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvSPV.setText(itemSelected.getItem1());
        holder.tvNoSPV.setText(itemSelected.getItem2());

        return convertView;

    }
}

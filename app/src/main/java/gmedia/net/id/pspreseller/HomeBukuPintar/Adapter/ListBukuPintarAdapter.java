package gmedia.net.id.pspreseller.HomeBukuPintar.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.pspreseller.R;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListBukuPintarAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListBukuPintarAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_list_bukupintar, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2;
        private ImageView ivIcon;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
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
            convertView = inflater.inflate(R.layout.adapter_list_bukupintar, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem2());
        holder.tvItem2.setText(itemSelected.getItem3());

        final ViewHolder finalHolder1 = holder;
        final ImageUtils iu = new ImageUtils();
        finalHolder1.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(finalHolder1.tvItem2.getVisibility() == View.GONE){

                    finalHolder1.tvItem2.setVisibility(View.VISIBLE);
                    iu.LoadRealImage(context, R.mipmap.ic_up, finalHolder1.ivIcon);
                }else{

                    finalHolder1.tvItem2.setVisibility(View.GONE);
                    iu.LoadRealImage(context, R.mipmap.ic_down1, finalHolder1.ivIcon);
                }
            }
        });

        final ViewHolder finalHolder = holder;
        return convertView;

    }
}

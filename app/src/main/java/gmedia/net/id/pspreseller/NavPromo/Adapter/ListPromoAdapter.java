package gmedia.net.id.pspreseller.NavPromo.Adapter;

import android.app.Activity;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.pspreseller.R;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListPromoAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListPromoAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_list_promo, items);
        this.context = context;
        this.items = items;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(moreData);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        private ImageView ivPromo;
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
            convertView = inflater.inflate(R.layout.adapter_list_promo, null);

            holder.ivPromo= (ImageView) convertView.findViewById(R.id.iv_promo);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final CustomItem itemSelected = items.get(position);
        ImageUtils iu = new ImageUtils();

        int[] display = iv.getScreenResolution(context);
        int size = display[0] - iv.dpToPx(context,32);
        CardView.LayoutParams lp = new CardView.LayoutParams(size, size);
        holder.ivPromo.setLayoutParams(lp);
        iu.LoadCustomSizedImage(context ,itemSelected.getItem4(), holder.ivPromo, size, size);
        return convertView;

    }
}

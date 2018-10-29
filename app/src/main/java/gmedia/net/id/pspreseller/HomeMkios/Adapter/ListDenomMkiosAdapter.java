package gmedia.net.id.pspreseller.HomeMkios.Adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.pspreseller.HomeMkios.OrderMKIOS;
import gmedia.net.id.pspreseller.R;
import gmedia.net.id.pspreseller.TopUP.IsiSaldo;


/**
 * Created by Shin on 1/8/2017.
 */

public class ListDenomMkiosAdapter extends ArrayAdapter{

    private Activity context;
    private List<CustomItem> items;
    private ItemValidation iv = new ItemValidation();

    public ListDenomMkiosAdapter(Activity context, List<CustomItem> items) {
        super(context, R.layout.adapter_list_denom, items);
        this.context = context;
        this.items = items;
    }

    private static class ViewHolder {
        private TextView tvItem1, tvItem2;
        private EditText edtItem1;
    }

    public List<CustomItem> getItems(){

        return items;
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
            convertView = inflater.inflate(R.layout.adapter_list_denom, null);
            holder.tvItem1 = (TextView) convertView.findViewById(R.id.tv_item1);
            holder.tvItem2 = (TextView) convertView.findViewById(R.id.tv_item2);
            holder.edtItem1 = (EditText) convertView.findViewById(R.id.edt_item1);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final ViewHolder finalHolder = holder;

        final CustomItem itemSelected = items.get(position);
        holder.tvItem1.setText(itemSelected.getItem2());
        holder.tvItem2.setText("Harga " + iv.ChangeToRupiahFormat(itemSelected.getItem3()));

        if(itemSelected.getItem5().equals("1")){
            holder.edtItem1.setEnabled(true);
        }else{
            holder.edtItem1.setEnabled(false);
        }

        finalHolder.edtItem1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                items.get(position).setItem4(editable.toString());
                //OrderMKIOS.updateHarga();
            }
        });

        return convertView;

    }
}

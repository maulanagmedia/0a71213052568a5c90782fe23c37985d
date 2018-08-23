package gmedia.net.id.pspreseller.CSChat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maulana.custommodul.CustomItem;
import com.maulana.custommodul.ImageUtils;
import com.maulana.custommodul.ItemValidation;

import java.util.List;

import gmedia.net.id.pspreseller.R;

/**
 * Created by indra on 29/12/2016.
 */

public class ChatAdapter extends ArrayAdapter {

    private Context context;
    private List<CustomItem> items;
    private View viewInflater;
    private ItemValidation iv = new ItemValidation();

    public ChatAdapter(Context context, List<CustomItem> items) {
        super(context, R.layout.adapter_chat_send, items);
        this.context = context;
        this.items = items;
    }

    public void addMoreData(List<CustomItem> moreData){

        items.addAll(0,moreData);
        notifyDataSetChanged();
    }

    public void addMoreChat(CustomItem moreData){

        items.add(moreData);
        notifyDataSetChanged();
    }

    public void removeChat(String id){

        int x = 0;
        boolean isFind = false;
        for(CustomItem item: items){

            if(item.getItem2().equals(id)){
                isFind = true;
                break;
            }
            x++;
        }

        if(isFind){

            items.remove(x);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private static class ViewHolder {
        private TextView tvPesan ;
        private LinearLayout llAttach;
        private ImageView ivImage;
        private TextView tvFileName, tvFileSize;
    }

    @Override
    public int getItemViewType(int position) {

        int hasil = 0;
        final CustomItem item = items.get(position);
        String title = item.getItem1();
        if(title.equals("0")){
            hasil = 0;
        }else if(title.equals("1")){
            hasil = 1;
        }

        return hasil;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        final CustomItem item = items.get(position);
        int tipeViewList = getItemViewType(position);

        if(convertView == null){

            LayoutInflater inflater = ((Activity)context).getLayoutInflater();

            if(tipeViewList == 0){
                convertView = inflater.inflate(R.layout.adapter_chat_send, null);
            }else{
                convertView = inflater.inflate(R.layout.adapter_chat_receive, null);
            }

            holder.tvPesan = (TextView) convertView.findViewById(R.id.tv_pesan);
            holder.llAttach = (LinearLayout) convertView.findViewById(R.id.ll_attach);
            holder.ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.tvFileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            holder.tvFileSize = (TextView) convertView.findViewById(R.id.tv_file_size);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(item.getItem7().equals("1")){ // image
            holder.llAttach.setVisibility(View.VISIBLE);
            holder.tvPesan.setVisibility(View.GONE);

            ImageUtils iu = new ImageUtils();
            iu.LoadRealImage(context, item.getItem6(), holder.ivImage);
            holder.tvFileName.setText(item.getItem4());
            holder.tvFileSize.setText(item.getItem5());
        }else if(item.getItem8().equals("1")){ //document
            holder.llAttach.setVisibility(View.VISIBLE);
            holder.tvPesan.setVisibility(View.GONE);
            holder.tvFileName.setText(item.getItem4());
            holder.tvFileSize.setText(item.getItem5());
        }else{

            holder.ivImage.setImageResource(R.mipmap.ic_attach);
            holder.llAttach.setVisibility(View.GONE);
            holder.tvPesan.setVisibility(View.VISIBLE);
            holder.tvPesan.setText(item.getItem3());
        }

        return convertView;
    }
}

package nwc.hardware.carlift.datas;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nwc.hardware.carlift.R;

public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.MyViewHolder> {
    private List<Data> items;
    private Context mContext;

    private Drawable downBTN_Enable;
    private Drawable downBTN_Disable;
    private Drawable upBTN_Enable;
    private Drawable upBTN_Disable;

    public DataItemAdapter(List<Data> items, Context mContext){
        this.items = items;
        this.mContext = mContext;

        upBTN_Enable = mContext.getDrawable(R.drawable.value_upbutton_2);
        upBTN_Disable = mContext.getDrawable(R.drawable.value_upbutton_1);
        downBTN_Enable = mContext.getDrawable(R.drawable.value_downbutton_2);
        downBTN_Disable = mContext.getDrawable(R.drawable.value_downbutton_1);

    }

   @NonNull
   @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.dataitem, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Data data = items.get(position);

        holder.upBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    holder.upBTN.setImageDrawable(upBTN_Enable);
                    if(data.getValue() != data.getMaxValue()){
                        data.setValue(data.getValue() + data.getStepValue());
                        SetValueText(data.getType(), holder.values, data.getValue());
                    }
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    holder.upBTN.setImageDrawable(upBTN_Disable);
                }else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    holder.upBTN.setImageDrawable(upBTN_Disable);
                }
                return false;
            }
        });
        holder.title.setText(data.getTypetoString(data.getType()));
        SetValueText(data.getType(), holder.values, data.getValue());
        holder.downBTN.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    holder.downBTN.setImageDrawable(downBTN_Enable);
                    if(data.getValue() != data.getMinValue()){
                        data.setValue(data.getValue() - data.getStepValue());
                        SetValueText(data.getType(), holder.values, data.getValue());
                    }

                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    holder.downBTN.setImageDrawable(downBTN_Disable);
                }else if(event.getAction() == MotionEvent.ACTION_CANCEL){
                    holder.downBTN.setImageDrawable(downBTN_Disable);
                }
                return false;
            }
        });
    }

    public void SetValueText(int type, TextView valueText, float value){
        switch (type){
            case Data.TYPE_SENSOR_DISPLAY:
                switch ((int)value){
                    case 0:
                        valueText.setText("Default View");
                        break;
                    case 1:
                        valueText.setText("Set Value View");
                        break;
                    case 2:
                        valueText.setText("Real Value View");
                        break;
                    default:
                        break;
                }
                break;
            case Data.TYPE_SERVICE_INTERVAL:
                switch ((int)value){
                    case 0:
                        valueText.setText("6 Month");
                        break;
                    case 1:
                        valueText.setText("12 Month");
                        break;
                    default:
                        break;
                }
                break;
            case Data.TYPE_2ND_STAGE:
                switch ((int)value){
                    case 3:
                        valueText.setText("Disable");
                        break;
                    default:
                        valueText.setText("" + (int)value);
                        break;
                }
                break;
            case Data.TYPE_REMOTE_CONTROL:
                switch ((int)value){
                    case 0:
                        valueText.setText("Single");
                        break;
                    case 1:
                        valueText.setText("Dual");
                        break;
                    case 2:
                        valueText.setText("Dual(RSG Off)");
                        break;
                    default:
                        break;
                }
                break;
            case Data.TYPE_ANGLE_SENSOR:
                switch ((int) value){
                    case 0:
                        valueText.setText("OFF");
                        break;
                    case 1:
                        valueText.setText("ON");
                        break;
                    default:
                        break;
                }
                break;
            case Data.TYPE_PHOTO_SENSOR:
                switch ((int)value){
                    case 0:
                        valueText.setText("OFF");
                        break;
                    case 1:
                        valueText.setText("ON");
                        break;
                    default:
                        break;
                }
                break;
            case Data.TYPE_LOCK_SENSOR:
                switch ((int)value){
                    case 0:
                        valueText.setText("OFF");
                        break;
                    case 1:
                        valueText.setText("ON");
                        break;
                    default:
                        break;
                }
                break;
            case Data.TYPE_SENSOR_LIMIT:
                valueText.setText(String.format("%.1f", value));
                break;
            default:
                valueText.setText("" + (int)value);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItem(List<Data> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public List<Data> getItem(){
        return items;
    }

    public void updateItem(){
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView values;
        public ImageButton upBTN;
        public ImageButton downBTN;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            upBTN = itemView.findViewById(R.id.Data_upBTN);
            title = itemView.findViewById(R.id.Data_TypeTXT);
            values = itemView.findViewById(R.id.Data_valuesTXT);
            downBTN = itemView.findViewById(R.id.Data_downBTN);

        }
    }
}

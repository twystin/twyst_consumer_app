package in.twyst.adapters;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.twyst.R;

/**
 * Created by rahuls on 23/7/15.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_card, parent, false);

        NotificationViewHolder viewHolder = new NotificationViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        View view = holder.itemView;
        final Resources resources = view.getContext().getResources();

            GradientDrawable background = (GradientDrawable) holder.notifyCircle.getBackground();
            if (position == 0) {

                background.setColor(resources.getColor(R.color.notification_red));
                holder.notifyCircle.setBackground(background);
                holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_wallet_icon));

            }else if(position==1){
                background.setColor(resources.getColor(R.color.notification_light_gray));
                holder.notifyCircle.setBackground(background);
                holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_wallet_icon));
            }
            else if(position==2){
                background.setColor(resources.getColor(R.color.notification_green));
                holder.notifyCircle.setBackground(background);
                holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_wallet_icon));
            }
            else if(position==3){
                background.setColor(resources.getColor(R.color.notification_dark_gray));
                holder.notifyCircle.setBackground(background);
                holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_reminder_icon));
            }
            else if(position==4){
                background.setColor(resources.getColor(R.color.notification_blue));
                holder.notifyCircle.setBackground(background);
                holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_wallet_icon));
            }else{
                background.setColor(resources.getColor(R.color.notification_purple));
                holder.notifyCircle.setBackground(background);
                holder.notificationBtn.setImageDrawable(resources.getDrawable(R.drawable.notification_offer_icon));
            }



    }


    @Override
    public int getItemCount() {
        return 6;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder{

        ImageView notificationBtn;
        RelativeLayout notifyCircle;
        ImageView notifyClick;
        TextView notifyHeader;
        TextView notifyDetail;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            notifyCircle =(RelativeLayout)itemView.findViewById(R.id.notifyCircle);
            notificationBtn = (ImageView) itemView.findViewById(R.id.notificationBtn);

        }
    }
}

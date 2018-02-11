package level42.phrasepad;

import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    private ArrayList<Folder> folders;


    public RvAdapter(ArrayList<Folder> folders){
        this.folders = folders;
    }

    @Override
    public RvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folders, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RvAdapter.ViewHolder holder, int position) {
        switch (folders.get(position).color){
            case R.color.colorDef:
                holder.image.setImageResource(R.drawable.ic_folder);
                break;
            case R.color.colorRed:
                holder.image.setImageResource(R.drawable.ic_folder_red);
                break;
            case R.color.colorBlue:
                holder.image.setImageResource(R.drawable.ic_folder_blue);
                break;
            case R.color.colorGreen:
                holder.image.setImageResource(R.drawable.ic_folder_green);
                break;
        }
        holder.title.setText(folders.get(position).title);
        if (folders.get(position).isSelected)
        holder.underline.setBackgroundResource(R.color.colorAccent);
        else holder.underline.setBackgroundResource(R.color.colorDef);
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView image;
        public LinearLayout underline;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.textView7);
            image = (ImageView) itemView.findViewById(R.id.imageView);
            underline = (LinearLayout)itemView.findViewById(R.id.underline);
        }
    }
}

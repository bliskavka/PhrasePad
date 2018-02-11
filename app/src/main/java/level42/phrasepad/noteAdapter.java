package level42.phrasepad;

        import java.util.ArrayList;

        import android.content.Context;
        import android.graphics.Color;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.LinearLayout;
        import android.widget.TextView;

public class noteAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Note> objects;

    noteAdapter(Context context, ArrayList<Note> notes) {
        ctx = context;
        objects = notes;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.adapter, parent, false);
        }
        Note p = (Note)getItem(position);

        ((TextView) view.findViewById(R.id.textView)).setText(p.date);
        ((TextView) view.findViewById(R.id.textView2)).setText(p.phrase);
        ((TextView) view.findViewById(R.id.textView3)).setText(p.author);
         view.findViewById(R.id.box).setBackgroundResource(p.color);

        return view;
    }



}
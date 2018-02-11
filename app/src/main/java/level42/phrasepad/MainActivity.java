package level42.phrasepad;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static level42.phrasepad.R.layout.activity_main;

public class MainActivity extends AppCompatActivity{

    SwipeMenuListView lvMain;
    TextView emptyList;

    RecyclerView rvMain;
    RecyclerView.LayoutManager rvManager;
    RecyclerView.Adapter rvAdapter;

    ArrayList<String> authors = new ArrayList<>();
    ArrayList<Folder> folders = new ArrayList<>();

    noteAdapter noteAdapter;
    ArrayAdapter<String> autoCompleteAdapter;
    String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    EditText edPh, edTitle;
    AutoCompleteTextView edAu;
    Dialog addDlg, editDlg, addFolderDlg;
    DialogConfirm dialogConfirm = new DialogConfirm();
    Button doneBtn;
    InputMethodManager imm;
    Button colorDef, colorRed, colorBlue, colorGreen;
    int colorPicked = R.color.colorDef;
    LinearLayout box;
    final int DIALOG_ADD = 1, DIALOG_EDIT = 2, DIALOG_ADD_FOLDER = 3;

    int IdSelected, FolderSelected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        setTitle("Stuff Pad");
        folders.add(new Folder("Main", R.color.colorDef));

        addDlg = new Dialog(MainActivity.this);
        addDlg.setContentView(R.layout.dialog);
        addDlg.setTitle(R.string.new_note);
        addDlg.setCanceledOnTouchOutside(false);

        editDlg = new Dialog(MainActivity.this);
        editDlg.setContentView(R.layout.dialog2);
        editDlg.setTitle(R.string.edit_note);
        editDlg.setCanceledOnTouchOutside(false);

        addFolderDlg = new Dialog(MainActivity.this);
        addFolderDlg.setContentView(R.layout.add_folder_dialog);
        addFolderDlg.setTitle(R.string.new_folder);
        addFolderDlg.setCanceledOnTouchOutside(false);

        colorDef = (Button) addFolderDlg.findViewById(R.id.colorDef);
        colorRed = (Button) addFolderDlg.findViewById(R.id.colorRed);
        colorBlue = (Button) addFolderDlg.findViewById(R.id.colorBlue);
        colorGreen = (Button) addFolderDlg.findViewById(R.id.colorGreen);
        box = (LinearLayout)findViewById(R.id.box);

        doneBtn = (Button)addDlg.findViewById(R.id.button);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //stuff for popping up keyboard

        //*************************Floating Hint*************
        TextInputLayout phtil = (TextInputLayout) addDlg.findViewById(R.id.ph_til);
        edPh = (EditText) phtil.findViewById(R.id.editText);
        TextInputLayout autil = (TextInputLayout) addDlg.findViewById(R.id.au_til);
        edAu = (AutoCompleteTextView) autil.findViewById(R.id.editText2);

        edTitle = (EditText)addFolderDlg.findViewById(R.id.edit_title);
        //****************************************************


        if (Data.importFromJSON(this) != null) //check if storage contains data
        folders = Data.importFromJSON(this);   //Recieving data
        emptyList = (TextView) findViewById(R.id.textView4); //Empty list textView

        noteAdapter = new noteAdapter(this, folders.get(FolderSelected).notes);
        lvMain = (SwipeMenuListView) findViewById(R.id.lv);
        lvMain.setAdapter(noteAdapter);

        //Horizontal Recycler View
        rvMain = (RecyclerView)findViewById(R.id.rv);
        //rvMain.setHasFixedSize(true);
        rvManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvMain.setLayoutManager(rvManager);
        rvAdapter = new RvAdapter(folders);
        rvMain.setAdapter(rvAdapter);

        rvMain.addOnItemTouchListener(new RecyclerItemClickListener(rvMain ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        //Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
                        selectFolder(position);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        if(position != 0) {
                            selectFolder(position);
                            FolderRemoveDialog folderRemoveDialog = new FolderRemoveDialog();
                            folderRemoveDialog.show(getSupportFragmentManager(), "od");
                        }
                        else Toast.makeText(getApplicationContext(), R.string.mf_click_warning, Toast.LENGTH_SHORT).show();
                    }
                })
                //https://stackoverflow.com/questions/24471109/recyclerview-onclick
        );

        //****************************Swiping Menu List View**************************

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(com.baoyz.swipemenulistview.SwipeMenu menu) {
                SwipeMenuItem edit = new SwipeMenuItem(getApplicationContext());
                edit.setBackground(R.color.colorEdit);
                edit.setWidth(200);
                edit.setIcon(R.drawable.ic_edit);
                menu.addMenuItem(edit);

                SwipeMenuItem delete = new SwipeMenuItem(getApplicationContext());
                delete.setBackground(R.color.colorDelete);
                delete.setWidth(200);
                delete.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(delete);
            }
        };

        lvMain.setMenuCreator(creator);
        lvMain.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        IdSelected = position;
                        showDialog(DIALOG_EDIT);
                        break;
                    case 1:
                        IdSelected = position;
                        dialogConfirm.show(getSupportFragmentManager(), "dc");
                    break;
                }
                return false;
            }
        });
        //*************************/Swiping Menu****************************

        selectFolder(0);
    }

    protected Dialog onCreateDialog(int id) {
        switch (id){
            case DIALOG_ADD:

                autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, authors);
                for(int i = 0; i < folders.get(FolderSelected).notes.size(); i++) {
                    if (!authors.contains(folders.get(FolderSelected).notes.get(i).author))
                    authors.add(folders.get(FolderSelected).notes.get(i).author);
                }
                edAu.setThreshold(1);
                edAu.setAdapter(autoCompleteAdapter);
                return addDlg;

            case  DIALOG_EDIT:
                return editDlg;

            case DIALOG_ADD_FOLDER:
                return addFolderDlg;
        }
        return super.onCreateDialog(id);
    }
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id){
            case DIALOG_ADD:
                edPh.requestFocus();
                autoCompleteAdapter.notifyDataSetChanged();
                edPh.setText(null);
                edAu.setText(null);
                break;
            case DIALOG_EDIT:
                EditText phrase = (EditText) editDlg.findViewById(R.id.textView6);
                EditText author = (EditText) editDlg.findViewById(R.id.textView5);
                phrase.setText(folders.get(FolderSelected).notes.get(IdSelected).phrase);
                author.setText(folders.get(FolderSelected).notes.get(IdSelected).author);
                break;
            case DIALOG_ADD_FOLDER:
                edTitle.requestFocus();
                edTitle.setText(null);
                break;
        }
    }

    //*****************Toolbar*************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.note:
                showDialog(DIALOG_ADD);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); //popping up keyboard
                break;

            case R.id.folder:
                showDialog(DIALOG_ADD_FOLDER);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0); //popping up keyboard
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //*************************************************

    public void addFolder(View v){
        if (!edTitle.getText().toString().isEmpty()) {
            folders.add(new Folder(edTitle.getText().toString(), colorPicked));
            rvAdapter.notifyDataSetChanged();
            Data.exportToJSON(this, folders); //Saving data
            selectFolder(folders.size() - 1);
        }
        else Toast.makeText(getApplicationContext(), R.string.empty_title, Toast.LENGTH_SHORT).show();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//hides keyboard
        removeDialog(DIALOG_ADD_FOLDER);

        colorRed.setText(""); // reset color picker
        colorBlue.setText("");
        colorGreen.setText("");
        colorDef.setText("✓");
        colorPicked = R.color.colorDef;// reset color picker

    }
    public void removeFolder(int position){

        if (position == FolderSelected)
        selectFolder(position-1);

        folders.remove(position);
        Data.exportToJSON(MainActivity.this, folders); //Saving data
        rvAdapter.notifyDataSetChanged();
    }
    public void selectFolder(int id){
        for (int i = 0; i < folders.size(); i++) {
            folders.get(i).isSelected = false;
        }
        folders.get(id).isSelected = true;
        rvAdapter.notifyDataSetChanged();

        noteAdapter = new noteAdapter(MainActivity.this, folders.get(id).notes);
        lvMain.setAdapter(noteAdapter);

        if (folders.get(id).notes.size() == 0) emptyList.setAlpha(1);
        else emptyList.setAlpha(0);
        FolderSelected = id;
    }

    public void addItem(View v) {

        if (!edPh.getText().toString().isEmpty()) {
            if ((!edAu.getText().toString().isEmpty())) {
                folders.get(FolderSelected).notes.add(0, new Note(date, edPh.getText().toString(), edAu.getText().toString(), colorPicked));
            }
            else if (edPh.getText().toString().charAt(0) == '/') openConsole(edPh.getText().toString());
                    else folders.get(FolderSelected).notes.add(0, new Note(date, edPh.getText().toString(), getString(R.string.no_name), colorPicked));

            Data.exportToJSON(this, folders);  //Saving Data
            noteAdapter.notifyDataSetChanged();

            if (folders.get(FolderSelected).notes.size() != 0)emptyList.setAlpha(0);
        } else
            Toast.makeText(getApplicationContext(), R.string.empty_string, Toast.LENGTH_LONG).show();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//hides keyboard
        removeDialog(DIALOG_ADD);

        colorRed.setText(""); // reset color picker
        colorBlue.setText("");
        colorGreen.setText("");
        colorDef.setText("✓");
        colorPicked = R.color.colorDef;// reset color picker
    }

    public void openConsole(String cmd){
        String[] test =  cmd.trim().split(" ");
        switch (test[0]){
            case "/clearlist":
                folders.get(FolderSelected).notes.clear();
                Data.exportToJSON(this, folders);
                noteAdapter.notifyDataSetChanged();
                emptyList.setAlpha(1);
                break;
            case "/kosmos":
                Toast.makeText(getApplicationContext(), "Kosmos Pisya", Toast.LENGTH_LONG).show();
                break;
            case "/fill_list":
                Fill();
                break;
            case "/add":
                folders.get(FolderSelected).notes.add(0, new Note(test[1], test[2], test[3], colorPicked));
                break;
            case "/itemcolor":
                try {
                    if (test[2].equals("red"))
                        folders.get(FolderSelected).notes.get(Integer.parseInt(test[1]) - 1).color = R.color.colorRed;
                    if (test[2].equals("blue"))
                        folders.get(FolderSelected).notes.get(Integer.parseInt(test[1]) - 1).color = R.color.colorBlue;
                    if (test[2].equals("green"))
                        folders.get(FolderSelected).notes.get(Integer.parseInt(test[1]) - 1).color = R.color.colorGreen;
                    Data.exportToJSON(this, folders); //Saving data
                    noteAdapter.notifyDataSetChanged();
                }
                catch (Throwable t){}
                break;
            case "/mfcolor":
                try {
                    if (test[1].equals("red")) folders.get(0).color = R.color.colorRed;
                    if (test[1].equals("blue")) folders.get(0).color = R.color.colorBlue;
                    if (test[1].equals("green")) folders.get(0).color = R.color.colorGreen;
                    Data.exportToJSON(this, folders); //Saving data
                    rvAdapter.notifyDataSetChanged();
                }
                catch (Throwable t){}
                break;
            case "/mftitle":
                folders.get(0).title = test[1];
                Data.exportToJSON(this, folders); //Saving data
                rvAdapter.notifyDataSetChanged();
                break;
            case "/design":
                Toast.makeText(getApplicationContext(), "Толік підар", Toast.LENGTH_LONG).show();
                folders.add(1, new Folder("????", R.color.colorDef));
                folders.get(1).notes.add(new Note("13.66.1488", "Цю надпісь бачать тільки топові дізайнери", "і кузьміч", R.color.colorDef));
                rvAdapter.notifyDataSetChanged();
                break;
            case "/help":
                folders.add(1, new Folder("/help", R.color.colorDef));
                folders.get(1).notes.add(new Note("none", "Remove all items in a current folder", "/clearlist", R.color.colorDef));
                folders.get(1).notes.add(new Note("none", "Add item with a custom date", "/add <date> <context> <author>", R.color.colorDef));
                folders.get(1).notes.add(new Note("none", "Set color for item (red, green, blue)", "/itemcolor <item id> <color>", R.color.colorDef));
                folders.get(1).notes.add(new Note("none", "Set color for Main folder (red, green, blue)", "/mfcolor <color>", R.color.colorDef));
                folders.get(1).notes.add(new Note("none", "Set title for Main folder", "/mftitle <title>", R.color.colorDef));
                folders.get(1).notes.add(new Note("none", ":}", "/design", R.color.colorDef));
                rvAdapter.notifyDataSetChanged();
                noteAdapter.notifyDataSetChanged();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Unknown command", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void editWindowClose(View v){
        editDlg.cancel();
    }
    public void editWindowSave(View v)
    {
        EditText phrase = (EditText) editDlg.findViewById(R.id.textView6);
        EditText author = (EditText) editDlg.findViewById(R.id.textView5);
        folders.get(FolderSelected).notes.get(IdSelected).phrase = phrase.getText().toString();
        folders.get(FolderSelected).notes.get(IdSelected).author = author.getText().toString();
        Data.exportToJSON(this, folders); //Saving data
        noteAdapter.notifyDataSetChanged();
        editDlg.cancel();
    }

    public void removeItem() {
        folders.get(FolderSelected).notes.remove(IdSelected);
        if (authors.size() != 0) {
            authors.remove(folders.get(FolderSelected).notes.get(IdSelected).author);
            autoCompleteAdapter.notifyDataSetChanged();
        }
        Data.exportToJSON(this, folders); //Saving data
        noteAdapter.notifyDataSetChanged();


        if (folders.get(FolderSelected).notes.size() == 0) emptyList.setAlpha(1);
    }

    public void setColorDef(View v) {
        colorRed.setText("");
        colorBlue.setText("");
        colorGreen.setText("");
        colorDef.setText("✓");
        colorPicked = R.color.colorDef;
    }

    public void setColorRed(View v){
        colorRed.setText("✓");
        colorBlue.setText("");
        colorGreen.setText("");
        colorDef.setText("");
        colorPicked = R.color.colorRed;
    }
    public void setColorBlue(View v){
        colorRed.setText("");
        colorBlue.setText("✓");
        colorGreen.setText("");
        colorDef.setText("");
        colorPicked = R.color.colorBlue;
    }
    public void setColorGreen(View v){
        colorRed.setText("");
        colorBlue.setText("");
        colorGreen.setText("✓");
        colorDef.setText("");
        colorPicked = R.color.colorGreen;
    }
     public void Fill(){
         folders.get(FolderSelected).notes.add(new Note("05.09.2017", "Боєц KFC", "Я", colorPicked));
         folders.get(FolderSelected).notes.add(new Note("04.09.2017", "Воздопомогательна програма", "Космос", colorPicked));
         folders.get(FolderSelected).notes.add(new Note("09.08.2017", "В цьому готелі є гостініца", "Космос", colorPicked));
         folders.get(FolderSelected).notes.add(new Note("03.07.2017", "В мене озьдо дом під хатою", "Дімон Кузьміч", colorPicked));
         folders.get(FolderSelected).notes.add(new Note("22.05.2017", "Дирявку продирявив", "Ваня Клішин", colorPicked));
         folders.get(FolderSelected).notes.add(new Note("07.12.2016", "Я пердо", "Ваня Клішин", colorPicked));
         folders.get(FolderSelected).notes.add(new Note("07.12.2016", "Я не хочу шоб нас розділяла промежность", "Я", colorPicked));
         folders.get(FolderSelected).notes.add(new Note("03.12.2016", "Маленькая электрогитарка", "Саня Косс", colorPicked));
         folders.get(FolderSelected).notes.add(new Note("06.10.2016", "Вотер-ботер-фліп челендж", "Я", colorPicked));
         folders.get(FolderSelected).notes.add(new Note("05.10.2016", "Зеквікасинка", "Учілка Анг", colorPicked));
         folders.get(FolderSelected).notes.add(new Note("29.09.2016", "дєпутат собачий", getString(R.string.no_name), colorPicked));

         Data.exportToJSON(this, folders);
     }

}

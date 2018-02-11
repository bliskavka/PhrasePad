package level42.phrasepad;

import java.util.ArrayList;

/**
 * Created by Odmin on 03.01.2018.
 */

public class Folder {
    String title;
    int color;
    public Boolean isSelected = false;

    public ArrayList<Note> notes = new ArrayList<>();

    public Folder(String title, int color){
        this.title = title;
        this.color = color;
    }
}

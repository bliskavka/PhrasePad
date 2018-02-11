package level42.phrasepad;

public class Note {

    String date,
           phrase,
           author;
    public int color;
    Note (String date, String quote, String author, int color)
    {
        this.date = date;
        this.phrase = quote;
        this.author = author;
        this.color = color;

    }
}

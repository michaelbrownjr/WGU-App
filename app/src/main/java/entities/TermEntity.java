package entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import generics.GenericEntity;

@Entity(tableName = "terms")
public class TermEntity extends GenericEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String start;
    private String end;

    public TermEntity(String title, String start, String end) {
        this.title = title;
        this.start = start;
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}

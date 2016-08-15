package com.vtayur.madhvanama.data.model;

import org.simpleframework.xml.ElementList;

import java.io.Serializable;
import java.util.List;

/**
 * Created by varuntayur on 7/7/2014.
 */
public class PhalaShruthi implements Serializable {

    @ElementList(inline = true, name = "note",required = false)
    private List<Note> notesList;

    public List<Note> getNotesList() {
        return notesList;
    }

    public void setNotesList(List<Note> notesList) {
        this.notesList = notesList;
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        for (Note note: notesList) {
            sBuilder.append(note.getFormattedNote());
            sBuilder.append("\n");
        }
        return sBuilder.toString();
    }
}

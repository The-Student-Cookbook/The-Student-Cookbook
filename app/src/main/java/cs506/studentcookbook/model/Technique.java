package cs506.studentcookbook.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Technique implements Parcelable{

    private String name;
    private String description;
    private String imageURL;
    private List<Tool> tools;
    private List<String> externalURLs;

    private static final String TAG = "ToolObject";

    public Technique() {
        createLists();
    }

    public Technique(String name) {
        if(name != null) {
            this.name = name.toLowerCase().trim();
        }

        createLists();
        populateFromDatabase();
    }

    /*
    * Reconstruct from the Parcel
    */
    public Technique(Parcel parcel) {
        Log.v(TAG, "Technique(Parcel source): Put the parcel back together");
        createLists();
        name = parcel.readString();
        description = parcel.readString();
        tools = parcel.readArrayList(tools.getClass().getClassLoader());
        externalURLs = parcel.readArrayList(externalURLs.getClass().getClassLoader());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Technique createFromParcel(Parcel source) {
            return new Technique(source);
        }

        public Technique[] newArray(int size) {
            return new Technique[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        Log.v(TAG, "writeToParcel..." + flags);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeList(tools);
        dest.writeList(externalURLs);
    }

    public int describeContents() {
        return this.hashCode();
    }

    private void populateFromDatabase() {
        // TODO make this grab the description, tools, and externalURLs from the database
    }

    private void createLists() {
        tools = new ArrayList<Tool>();
        externalURLs = new ArrayList<String>();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public List<Tool> getTools() {
        return tools;
    }

    public List<String> getExternalURLs() {
        return externalURLs;
    }

    public void setName(String name) {
        if(name == null)
            return;

        name = name.toLowerCase().trim();
        this.name = name;
    }

    public void setDescription(String description) {
        if(description == null)
            return;

        description = description.trim();
        this.description = description;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }

    public void setExternalURLs(List<String> urls) {
        this.externalURLs = urls;
    }

    public void addTool(Tool tool) {
        this.tools.add(tool);
    }

    public void addExternalURL(String url) {
        if(url == null)
            return;

        url = url.trim();
        this.externalURLs.add(url);
    }

    public void setImageURL(String url) {
        if(url == null)
            return;

        url = url.trim();
        this.imageURL = url;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Technique technique) {
        if (!name.equals(technique.getName()))
            return false;
        else if (!description.equals(technique.getDescription()))
            return false;
        else if (!imageURL.equals(technique.getImageURL()))
            return false;
            //Excluded because this column doesn't exist in Technique table
            //else if (!tools.equals(technique.getTools()))
            //   return false;
            //else if (!externalURLs.equals(technique.getExternalURLs()))
            //    return false;
        else
            return true;
    }
}
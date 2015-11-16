package cs506.studentcookbook.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Tool implements Parcelable {

    private String name;
    private String description;
    private String imageURL;

    private static final String TAG = "ToolObject";

    public Tool(String name) {
        this.name = name;
    }

    public Tool() {
        this.name = "";
    }

    /*
     * Reconstruct from the Parcel
     */
    public Tool(Parcel parcel) {

        Log.v(TAG, "Ingredient(Parcel source): Put the parcel back together");
        name = parcel.readString();
        description = parcel.readString();
        imageURL = parcel.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Tool createFromParcel(Parcel source) {
            return new Tool(source);
        }

        public Tool[] newArray(int size) {
            return new Tool[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        Log.v(TAG, "writeToParcel..." + flags);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageURL);
    }

    public int describeContents() {
        return this.hashCode();
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

    public void setName(String name) {
        if (name == null)
            return;

        name = name.toLowerCase().trim();
        this.name = name;
    }

    public void setDescription(String description) {
        if (name == null)
            return;

        description = description.trim();
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        if (imageURL == null)
            return;

        imageURL = imageURL.trim();
        this.imageURL = imageURL;
    }

    public String toString() {
        return this.name + ": " + this.description;
    }

    public boolean equals(Tool tool) {
        if (!name.equals(tool.getName()))
            return false;
        else if (!description.equals(tool.getDescription()))
            return false;
        else if (!imageURL.equals(tool.getImageURL()))
            return false;
        else
            return true;
    }
}

package cs506.studentcookbook.Model;


public class Tool {

    private String name;
    private String description;
    private String imageURL;

    public Tool(String name) {
        this.name = name;
        populateFromDatabase();
    }

    public Tool() {
        this.name = "";
    }

    private void populateFromDatabase() {
        // TODO make this grab the description and imageURL from the database
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
        if(!this.name.equals(name)) {
            this.name = name;
            populateFromDatabase();
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String toString() {
        return this.name;
    }
}
